package com.github.rkbalgi.apps;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 *
 */
public class S3Test {


  public static void main(String[] args) {

    AmazonS3 client = AmazonS3ClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(
            new BasicAWSCredentials("AKIAJX2RRTSASWBJLYPQ",
                "BAH2FxlTrw1hVWyB8FwXE62Dqeu63b11WowFTtKD"))).build();

    client.listBuckets().forEach(System.out::println);

    try {
      Thread.sleep(Long.MAX_VALUE);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }


  }
}
