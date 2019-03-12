package com.github.rkbalgi.apps.vertxapp;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class Main {

  private static final Logger LOG = LoggerFactory.getLogger(Main.class);


  public static void main(String[] args) {

    //System.setProperty("javax.net.debug", "all");

    Vertx vertx = Vertx.vertx();

    vertx.deployVerticle(new MainVerticle(), res -> {
      if (res.succeeded()) {

        LOG.info("MainVerticle deployed...");

        new HttpServerVerticle();

        vertx.deployVerticle(HttpServerVerticle::new, new DeploymentOptions(), httpResult -> {
          if (httpResult.succeeded()) {
            LOG.debug("HTTP verticle deployed..");
          } else {
            LOG.error("Failed to deploy HTTP verticle", res.cause());
          }
        });

      }
    });

  }
}
