package com.eddytnk.kafkaexample.service;

import com.eddytnk.kafkaexample.model.Item;

/**
 * Author: Edward Tanko <br/>
 * Date: 6/10/19 7:01 PM <br/>
 */
public interface ItemService {

    void producer(Item item);
    void consumer(String itemStr);
}
