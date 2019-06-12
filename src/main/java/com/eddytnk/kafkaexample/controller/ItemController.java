package com.eddytnk.kafkaexample.controller;

import com.eddytnk.kafkaexample.model.Item;
import com.eddytnk.kafkaexample.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: Edward Tanko <br/>
 * Date: 6/10/19 6:55 PM <br/>
 */
@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping("/items")
    public ResponseEntity<?> publishItem(@RequestBody Item item){
        itemService.producer(item);
        return ResponseEntity.noContent().build();
    }
}
