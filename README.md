# springboot-kafka-with-docker


Your docker-compose file should look like this:

```yaml
version: '2'
services:
  zookeeper:
    image: wurstmeister/zookeeper
    ports:
    - "2181:2181"
  kafka:
    image: wurstmeister/kafka
    ports:
    - "9092:9092"
    environment:
      KAFKA_ADVERTISED_HOST_NAME: 127.0.0.1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'true'
      KAFKA_CREATE_TOPICS: "ITEM.TOPIC:1:1" # one partition one replica
```

Publish/producer a message every 2000ms 

```java
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
```

If you want to use application.yml properties file

```yaml
spring:
  kafka:
    consumer:
      bootstrap-servers: localhost:9092
      group-id: group-id
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      bootstrap-servers: localhost:9092
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
```

Otherwise you could use Configurable beans

**Consumer Config**
```java

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    private static final String BOOTSTRAP_ADDR = "localhost:9092";

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_ADDR);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group-id");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }
 
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
      kafkaListenerContainerFactory() {
    
        ConcurrentKafkaListenerContainerFactory<String, String> factory
          = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
```
**Producer Config**

```java

@Configuration
public class KafkaProducerConfig {

    private static final String BOOTSTRAP_ADDR = "localhost:9092";
 
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_ADDR);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
 
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
```
