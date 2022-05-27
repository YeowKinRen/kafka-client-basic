package org.example.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallback {

    private static final Logger log = LoggerFactory.getLogger(ProducerDemoWithCallback.class.getSimpleName());

    public static void main(String[] args) {
        log.info("Kafka Producer");

        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "[::1]:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create Producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);


        for (int i = 0; i<100; i++) {
            // create a producer record
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>("demo_java", "hello_world " + i);

            // send data - asynchronous operation
//            producer.send(producerRecord);
            producer.send(producerRecord, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    // executes every time a record is successfully sent or an exception is thrown
                    if (exception == null) {
                        log.info("Received new metadata/ \n" +
                                "Topic: " + metadata.topic() + " Timestamp: " + metadata.timestamp() +
                                "\nPartition: " + metadata.partition() + " Offset: " + metadata.offset() + "\n");
                    } else {
                        log.error("error while producing", exception);
                    }
                }
            }); // notice all message will enter the same partition if send fast enough to improve performance (stickyPartitioner)
            // if time is delayed, then every message will enter different partition
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }

        // flush data - synchronous
        producer.flush();

        // flush and close the Producer
        producer.close();
    }
}
