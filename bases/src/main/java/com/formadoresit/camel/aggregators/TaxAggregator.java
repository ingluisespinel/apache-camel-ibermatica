package com.formadoresit.camel.aggregators;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

import java.util.Objects;

public class TaxAggregator implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        // oldExchange contiene dato procesadoss previamente
        // newExchange contiene el nuevo mensaje
        Double newBody = newExchange.getMessage().getBody(Double.class);
        if (Objects.nonNull(oldExchange)) {
            Double oldBody = oldExchange.getMessage().getBody(Double.class);
            Double newValue = newBody + oldBody;
            newExchange.getMessage().setBody(newValue);
        }
        return newExchange;
    }
}
