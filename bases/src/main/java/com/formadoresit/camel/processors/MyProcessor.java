package com.formadoresit.camel.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MyProcessor implements Processor {
    private static final Logger log = LoggerFactory.getLogger(MyProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        var message = exchange.getMessage();
        var headers = message.getHeaders();
        var body = message.getBody();
        log.info("Headers: {}", headers);
        log.info("Body: {}", body);
        message.setBody("Cambiando desde Processor");
        message.setHeader("HeaderProcessor", "ValorProcessor");
    }
}
