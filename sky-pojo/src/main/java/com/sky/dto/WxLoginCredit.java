package com.sky.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("wx")
@Data
public class WxLoginCredit {
    private String appid;
    private String secret;
}
