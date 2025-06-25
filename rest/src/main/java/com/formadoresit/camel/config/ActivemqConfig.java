package com.formadoresit.camel.config;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActivemqConfig {

    @Bean
    public ConnectionFactory connectionFactory(
            @Value("${spring.activemq.broker-url}") String brokerUrl,
            @Value("${spring.activemq.user}") String user,
            @Value("${spring.activemq.password}") String password,
            @Value("${spring.activemq.pool.max-connections}") int maxPoolSize
    ) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connectionFactory.setUserName(user);
        connectionFactory.setPassword(password);
        connectionFactory.setMaxThreadPoolSize(maxPoolSize);
        connectionFactory.setTrustAllPackages(true);
        return connectionFactory;
    }

    @Bean
    public JmsComponent activemq(ConnectionFactory connectionFactory) {
        return JmsComponent.jmsComponentAutoAcknowledge(connectionFactory);
    }


}
