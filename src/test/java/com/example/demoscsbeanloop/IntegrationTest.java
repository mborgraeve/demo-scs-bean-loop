package com.example.demoscsbeanloop;


import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.Map;


@Tag("integration")
@SpringBootTest
class IntegrationTest {

    public static final String SOME_VALUE = "someValue";
    public static final String KEY = "key";
    private static final String KAFKA_BINDER = "kafka-local";
    private static final String INPUT_TOPIC_NAME = "IN ";
    private static final String OUTPUT_TOPIC_NAME = "OUT";
    private static KafkaContainer container;
    private static Producer<String, String> producer;
    private static Consumer<String, String> consumer;

    @DynamicPropertySource
    public static void appProperties(DynamicPropertyRegistry registry) {
        registry.add(
            "spring.cloud.stream.binders." + KAFKA_BINDER + ".environment.spring.cloud.stream.kafka.binder.brokers",
            () -> container.getBootstrapServers());
    }

    @BeforeAll
    static void setup() {
        //start a kafka container
        container = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.2"));
        container.start();
        String bootstrapServers = container.getBootstrapServers();

        //create a kafka producer
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(bootstrapServers);
        DefaultKafkaProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(producerProps,
            new StringSerializer(), new StringSerializer());
        producer = producerFactory.createProducer();

        //create a kafka consumer
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(bootstrapServers, "consumerGroup", "true");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumer = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(),
            new StringDeserializer()).createConsumer();
        consumer.subscribe(Collections.singleton(OUTPUT_TOPIC_NAME));
    }

    @AfterAll
    static void teardown() {
        consumer.close();
        producer.close();
        container.stop();
    }

    @Test
    void dummyPassThrough() {

        // a simple record
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(INPUT_TOPIC_NAME, KEY, SOME_VALUE);

        // send it
        producer.send(producerRecord);
        producer.flush();

        //wait a bit
//        await().atLeast(Duration.of(1, SECONDS));

        // get the response back fi we wanted to
        // But for demo of the loop issue we don't need to read
        // var result = consumer.poll(Duration.of(1, SECONDS));

        //assertions
//        assertThat(result.count()).isEqualTo(1);
//        assertThat(result.iterator().next().value()).isEqualTo(SOME_VALUE);
    }

}
