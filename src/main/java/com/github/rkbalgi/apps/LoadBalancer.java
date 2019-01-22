package com.github.rkbalgi.apps;

import com.github.rkbalgi.apps.tcpl7.CustomInboundHandler;
import com.github.rkbalgi.apps.tcpl7.CustomOutboundHandler;
import com.google.common.base.Charsets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import java.nio.ByteOrder;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.netty4.ClientInitializerFactory;
import org.apache.camel.component.netty4.NettyProducer;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

/**
 * Hello world!
 */
public class LoadBalancer {

  private static final Logger LOG = LoggerFactory.getLogger(LoadBalancer.class);

  public static void main(String[] args) throws Exception {

    String nettyOptions = "?sync=false&allowDefaultCodec=false&reuseChannel=false&clientMode=true&workerGroup=#sharedPool&clientInitializerFactory=#initFactory";

    SimpleRegistry registry = new SimpleRegistry();
    registry.put("encoder", new CustomOutboundHandler());
    registry.put("decoder", new CustomOutboundHandler());
    registry.put("sharedPool", new NioEventLoopGroup());
    registry.put("initFactory", new ClientInitializerFactory() {
      @Override
      protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast("encoder", new CustomOutboundHandler());
        ch.pipeline().addLast("decoder-1", new LengthFieldBasedFrameDecoder(
            ByteOrder.BIG_ENDIAN, Short.MAX_VALUE, 0, 2, 0, 2, true));
        ch.pipeline().addLast("decoder-2", new CustomInboundHandler());
      }


      @Override
      public ClientInitializerFactory createPipelineFactory(NettyProducer producer) {
        producer.getConfiguration().setProducerPoolEnabled(true);
        producer.getConfiguration().setProducerPoolMaxActive(1);
        producer.getConfiguration().setProducerPoolMinIdle(1);

        return this;

        /*return new DefaultClientInitializerFactory(producer) {
          @Override
          protected void initChannel(Channel ch) throws Exception {
            ch.pipeline().addLast("encoder", new CustomOutboundHandler());
            ch.pipeline().addLast("decoder-1", new LengthFieldBasedFrameDecoder(
                ByteOrder.BIG_ENDIAN, Short.MAX_VALUE, 0, 2, 0, 2, true));
            ch.pipeline().addLast("decoder-2", new CustomInboundHandler());
          }
        };*/
      }

    });

    EventBus eventBus = new EventBus("bus://google-guava/app2hsm");
    EventBus eventBus2 = new EventBus("bus://google-guava/hsm2app");
    eventBus.register(new AppMsgListener());

    registry.put("app2hsm_bus", eventBus);
    registry.put("hsm2app_bus", eventBus2);

    DefaultCamelContext camelContext = new DefaultCamelContext(registry);
    camelContext.addRoutes(new RouteBuilder() {
      @Override
      public void configure() {

        from("guava-eventbus:app2hsm_bus").loadBalance().roundRobin()
            //failover(3, false, true, Exception.class)
            .to("netty4:tcp://localhost:9887" + nettyOptions,
                "netty4:tcp://localhost:9887" + nettyOptions);

      }
    });

    //posting something to the eventbus should deliver the message to our netty endpoints
    //in a round-robin fashion

    camelContext.start();

    Flux.interval(Duration.ofSeconds(10)).subscribe(l -> {
      ByteBuf buf = Unpooled
          .copiedBuffer("Hello World".getBytes(Charsets.US_ASCII));
      LOG.debug("posting ... " + buf);
      eventBus.post(buf);
    });

    System.out.println("Waiting ....");
    new CountDownLatch(1).await();

  }

  public static class AppMsgListener {

    private static final Logger LOG = LoggerFactory.getLogger(AppMsgListener.class);

    @Subscribe
    public void receiveMsg(Object ebMsg) {
      LOG.debug("Received msg on event-bus - {}", ebMsg);

    }
  }
}