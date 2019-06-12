package com.eddytnk.kafkaexample.service;

import com.eddytnk.kafkaexample.model.Item;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

/**
 * Author: Edward Tanko <br/>
 * Date: 6/10/19 7:03 PM <br/>
 */
@Service
public class ItemServiceImpl implements ItemService{

    private Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);
    public static final String TOPIC_NAME = "ITEM.TOPIC";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void producer(Item item) {
        logger.info("Sending message to kafka: {}", item);
        try {
            kafkaTemplate.send(TOPIC_NAME, new ObjectMapper().writeValueAsString(item));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    @KafkaListener(id = "group-id", topics = TOPIC_NAME)
    public void consumer(String itemStr) {
        try {
            logger.info("Received message from kafka: {}", new ObjectMapper().readValue(itemStr, Item.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 2000)
    public void published(){
        logger.info("publishing ...");
        Item item = new Item();
        item.setName(UUID.randomUUID().toString());
        item.setPrice(1000);
        item.setQuantity(1);
        producer(item);
    }

}
