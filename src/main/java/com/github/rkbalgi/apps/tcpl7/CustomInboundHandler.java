package com.github.rkbalgi.apps.tcpl7;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Sharable
public class CustomInboundHandler extends ChannelInboundHandlerAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(CustomInboundHandler.class);

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    //super.channelRead(ctx, msg);
    ByteBuf buf = (ByteBuf) msg;
    LOG.debug("Received from {} - Data = {}", ctx.channel().remoteAddress(),
        buf.toString(Charset.forName("US-ASCII")));

    //do something with this ..

  }
}
