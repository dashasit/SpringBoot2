package com.intelligrated.generic;

import com.fasterxml.classmate.TypeResolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.management.ManagementFactory;
import java.time.LocalDate;
import java.util.ArrayList;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

/**
 * Created by sachin.subhedar on 01/19/2017.
 * <p>
 * Copyright (c) 2001-2016 Intelligrated [https://www.intelligrated.com/]
 * <p>
 * The  information  contained  herein  is  the  confidential  and  proprietary
 * information of Intelligrated.  This information is protected,  among others,
 * by the patent,  copyright,  trademark,  and trade secret laws of  the United
 * States and its several states.  Any use,  copying, or reverse engineering is
 * strictly prohibited. This software has been developed at private expense and
 * accordingly,  if used under Government  contract,  the use,  reproduction or
 * disclosure  of  this  information  is subject to  the restrictions set forth
 * under the  contract between  Intelligrated  and its customer.  By viewing or
 * receiving this information, you consent to the foregoing.
 */

@SpringBootApplication
@EnableSwagger2
@EnableEurekaClient
@ComponentScan(basePackages = {"com.intelligrated"})
public class Application {

    private static ApplicationContext applicationContext = null;
    private final static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        String modeString = ((args != null) && (args.length > 0)) ? args[0] : null;
        logger.info("Starting Generic service");
        logger.info("PID:" + ManagementFactory.getRuntimeMXBean().getName() +
                " Application modeString:" + modeString + " context:" + applicationContext);
        if (applicationContext != null && modeString != null && "stop".equals(modeString)) {
            System.exit(SpringApplication.exit(applicationContext, (ExitCodeGenerator) () -> 0));
        } else {
            SpringApplication app = new SpringApplication(Application.class);
            //noinspection ConstantConditions
            applicationContext = app.run(args);
            logger.info("PID:" + ManagementFactory.getRuntimeMXBean().getName() +
                    " Application modeString:" + modeString + " context:" + applicationContext);
        }
    }

    @Bean
    public Docket newsApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any()).build().pathMapping("/")
                .directModelSubstitute(LocalDate.class, String.class)
                .genericModelSubstitutes(ResponseEntity.class)
                .alternateTypeRules(newRule(typeResolver.resolve(DeferredResult.class,
                        typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                        typeResolver.resolve(WildcardType.class)))
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET,
                        new ArrayList<ResponseMessage>() {
                            private static final long serialVersionUID = 1L;

                            {
                                add(new ResponseMessageBuilder()
                                        .code(500)
                                        .message("500 message")
                                        .responseModel(new ModelRef("Error"))
                                        .build());
                            }
                        });
    }

    @Autowired
    private TypeResolver typeResolver;

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("WES generic service")
                .description("WES generic service")
                .termsOfServiceUrl("https://www.intelligrated.com/software-solutions")
                .contact("Intelligrated Software")
                .license("Commercial Licensed ")
                .licenseUrl("https://www.intelligrated.com/software-solutions")
                .version("1.0.0")
                .build();
    }

}
