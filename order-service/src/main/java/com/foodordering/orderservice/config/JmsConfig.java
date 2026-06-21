package com.foodordering.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class JmsConfig {

    public static final String ORDER_CREATED_QUEUE = "order.created";

    @Bean
    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        
        java.util.HashMap<String, Class<?>> typeIdMappings = new java.util.HashMap<>();
        typeIdMappings.put("OrderEvent", com.foodordering.orderservice.dto.OrderEvent.class);
        converter.setTypeIdMappings(typeIdMappings);
        
        return converter;
    }
}
