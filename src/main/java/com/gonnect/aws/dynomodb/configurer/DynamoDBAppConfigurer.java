package com.gonnect.aws.dynomodb.configurer;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.gonnect.aws.dynomodb.dq.apis.DqRegistrationApis;
import com.google.common.base.Predicates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

import static springfox.documentation.builders.PathSelectors.regex;

/**
 * This class performs the bean configuration
 */
@Configuration
@EnableWebSecurity
@EnableSwagger2
@Slf4j
public class DynamoDBAppConfigurer extends WebSecurityConfigurerAdapter {


    private final static String DYNAMODB_ENDPOINT_DEFAULT_VALUE = "http://localhost:8000";

    @Value("${dynamoDbEndpoint:" + DYNAMODB_ENDPOINT_DEFAULT_VALUE + "}")
    private String dynamoDbEndpoint;

    @Bean
    public AmazonDynamoDB amazonDynamoDb() {

        log.trace("Entering amazonDynamoDb()");
        AmazonDynamoDB client = new AmazonDynamoDBClient();
        log.info("Using DynamoDb endpoint {}", dynamoDbEndpoint);
        client.setEndpoint(dynamoDbEndpoint);
        return client;
    }

    @Bean
    public DynamoDBMapper dynamoDbMapper(AmazonDynamoDB amazonDynamoDB) {

        log.trace("Entering dynamoDbMapper()");

        return new DynamoDBMapper(amazonDynamoDB);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/").permitAll();
        http.cors().and().csrf().disable();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(DqRegistrationApis.class.getPackage().getName()))
                .paths(Predicates.not(regex("/error")))
                .build()
                .pathMapping("/")
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        String description = "Data Quality As Service Registration";
        return new ApiInfoBuilder()
                .title(description)
                .description(description)
                .license("Nike")
                .licenseUrl("")
                .version("1.0")
                .build();
    }
}
