package com.github.rkbalgi.apps.vertxapp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  private HttpClient httpClient;

  @Override
  public void start(Future<Void> future) throws Exception {

    httpClient = vertx.createHttpClient();

    MessageConsumer<Object> consumer = vertx.eventBus()
        .consumer("eb-vertx://wf").handler(req -> {
          LOG.debug("Received - {}", req.body());

          executeHttpReq(() -> req.reply("OK"));


        });
    while (!consumer.isRegistered()) {
    }
    future.complete();


  }

  private void executeHttpReq(Runnable afterCompletion) {

    httpClient.getAbs("https://httpbin.org/get?a=10&b=20").handler(resp -> {
      resp.
          bodyHandler(
              buf -> {
                LOG.debug("Received from http://httpbin.org - {}", buf.toJsonObject());
                afterCompletion.run();
              });
    }).end();


  }
}
