package com.foodordering.orderservice.config;

import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActiveMqBrokerConfig {

    private static final Logger log = LoggerFactory.getLogger(ActiveMqBrokerConfig.class);

    @Bean
    public BrokerService broker() throws Exception {
        log.info("Starting Embedded ActiveMQ Broker on tcp://localhost:61616...");
        BrokerService broker = new BrokerService();
        broker.addConnector("tcp://localhost:61616");
        broker.setPersistent(false);
        broker.setUseJmx(true);
        broker.start();
        log.info("Embedded ActiveMQ Broker started successfully.");
        return broker;
    }
}
