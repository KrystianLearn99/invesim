package org.kris.invesim.notificationms;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.kris.invesim.SimulationResultDto;
import org.kris.invesim.SimulationStartedDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    private static Map<String, Object> baseConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        props.put(JsonDeserializer.TRUSTED_PACKAGES, "org.kris.invesim");
        return props;
    }

    @Bean
    public ConsumerFactory<String, SimulationStartedDto> simulationStartedConsumerFactory() {
        Map<String, Object> props = new HashMap<>(baseConsumerProps());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, SimulationStartedDto.class.getName());
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SimulationStartedDto>
    simulationStartedKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, SimulationStartedDto>();
        factory.setConsumerFactory(simulationStartedConsumerFactory());
        return factory;
    }


    @Bean
    public ConsumerFactory<String, SimulationResultDto> SimulationCompletedConsumerFactory() {
        Map<String, Object> props = new HashMap<>(baseConsumerProps());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, SimulationResultDto.class.getName());
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SimulationResultDto>
    simulationCompletedKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, SimulationResultDto>();
        factory.setConsumerFactory(SimulationCompletedConsumerFactory());
        return factory;
    }


}
