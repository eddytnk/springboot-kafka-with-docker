package com.eddytnk.kafkaexample.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Author: Edward Tanko <br/>
 * Date: 6/10/19 6:51 PM <br/>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Item  implements Serializable {
    private String name;
    private Integer price;
    private Integer quantity;
}
