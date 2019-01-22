package com.github.rkbalgi.apps;

import com.google.common.primitives.Shorts;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class TcpServer {

  private static final Logger LOG = LoggerFactory.getLogger(TcpServer.class);
  private static final Random random = new Random();

  public static void main(String[] args) throws InterruptedException {

    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(new NioEventLoopGroup());
    bootstrap.channel(NioServerSocketChannel.class);
    bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
    bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(SocketChannel ch) throws Exception {

        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {

          @Override
          public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            LOG.error("Error on channel", cause);
          }

          @Override
          public void channelActive(ChannelHandlerContext ctx) throws Exception {
            LOG.debug("Channel {} is now active - remote is {} ", ctx.channel().id(),
                ctx.channel().remoteAddress());
          }

          @Override
          public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            super.channelRegistered(ctx);
            LOG.debug("Channel {} is now registered - remote is {} ", ctx.channel().id(),
                ctx.channel().remoteAddress());
          }

          @Override
          public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            //super.channelRead(ctx, msg);
            ByteBuf buf = (ByteBuf) msg;
            LOG.debug("Received ... {}", buf.toString(io.netty.util.CharsetUtil.US_ASCII));

            ReferenceCountUtil.release(msg);

            String randomText = "098H76a**&&$$#2hd76#@&usdutt65451776sgsbvxc9876affae4%%^544g432788982726544231166177716161515443eadaww";
            String data = randomText.substring(0, random.nextInt(randomText.length()));

            ctx.channel().writeAndFlush(
                Unpooled.wrappedBuffer(Shorts.toByteArray((short) data.length()), data.getBytes()));

          }
        });

      }
    });

    bootstrap.bind(9887).sync().channel().closeFuture().sync();


  }

}
