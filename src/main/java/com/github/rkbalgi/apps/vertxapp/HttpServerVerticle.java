package com.github.rkbalgi.apps.vertxapp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpServerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class HttpServerVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(HttpServerVerticle.class);

  @Override
  public void start(Future<Void> future) throws Exception {

    vertx.createHttpServer(new HttpServerOptions().setPort(7651)).requestHandler((req) -> {

      LOG.debug("Processing req - {}", req.uri());
      if (req.uri().equals("/req1")) {

        vertx.eventBus().send("eb-vertx://wf", req.uri(), new DeliveryOptions(), reply -> {
          if (reply.succeeded()) {
            LOG.debug("Received reply - {}", reply.result().body());
          } else {
            LOG.error("Failed to process reply or request execution failed", reply.cause());
          }
        });

        req.response().end("hello");
      } else if (req.uri().equals("/req2")) {
        req.response().end("world");
      }

    }).listen(res -> {
      if (res.succeeded()) {
        future.complete();
      } else {
        future.fail(res.cause());
      }
    });

  }

  @Override
  public void stop(Future<Void> future) throws Exception {

  }
}
