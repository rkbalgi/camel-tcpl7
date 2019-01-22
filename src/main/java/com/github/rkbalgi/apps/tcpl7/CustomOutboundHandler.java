package com.github.rkbalgi.apps.tcpl7;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Sharable
public class CustomOutboundHandler extends ChannelOutboundHandlerAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(CustomOutboundHandler.class);

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    LOG.error("Error on channel", cause);
  }

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
      throws Exception {
    super.write(ctx, msg, promise);
    LOG.debug("Writing to {} - {}", ctx.channel().remoteAddress(), msg);

    //do something with this .. 

  }
}
