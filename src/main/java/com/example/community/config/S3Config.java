package com.example.community.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 *  S3와 통신하기 위한 S3 Client Bean을 생성하고
 *  스프링 컨텍스트에 주입하기 위한 Configuration 파일을 구성
 */
@Configuration
public class S3Config {

    @Value("{spring.cloud.aws.region.static}")
    private String region;

}
