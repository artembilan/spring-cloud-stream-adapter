package com.vine.commons.cloudstream.source;

import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by vrustia on 3/28/18.
 */
@SpringBootApplication
@EnableBinding
@EnableIntegration
@IntegrationComponentScan
public class SourceApplication {

    private final static Logger LOG = getLogger(SourceApplication.class);

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(SourceApplication.class, args);

        getException(applicationContext);
    }

    private static void getException(ApplicationContext applicationContext) {
        TopicMessageGateway topicMessageGateway = applicationContext.getBean(TopicMessageGateway.class);
        try {
            while (true) {
                String message = "Source1:" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
                try {
                    topicMessageGateway.exception(LocalDateTime.now().getLong(ChronoField.MILLI_OF_SECOND));
                } catch (Exception e) {
                    LOG.debug("{}", e.getClass());
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
