package com.mastercard.mpqr.config;

import com.mastercard.api.core.model.Environment;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * Configuration class getting proeprties from /resources/application*.yml
 */
@Configuration
@ConfigurationProperties("com.mastercard.api-config")
@Validated
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MasterCardConfiguration {
    boolean debug;
    @NotNull
    Environment environment = Environment.PRODUCTION;
    @NotEmpty
    String consumerKey;
    @NotEmpty
    String keyAlias;
    @NotEmpty
    String keyPassword;
    @NotEmpty
    String privateKey;
}
