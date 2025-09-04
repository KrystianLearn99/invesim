package org.kris.invesim.simulationms.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.kris.invesim.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

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
    public ConsumerFactory<String, MarketDataReadyEvent> marketDataReadyConsumerFactory() {
        Map<String, Object> props = new HashMap<>(baseConsumerProps());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, MarketDataReadyEvent.class.getName());
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MarketDataReadyEvent>
    marketDataReadyKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, MarketDataReadyEvent>();
        factory.setConsumerFactory(marketDataReadyConsumerFactory());
        return factory;
    }


    @Bean
    public ConsumerFactory<String, PortfolioDataReadyEvent> portfolioReadyConsumerFactory() {
        Map<String, Object> props = new HashMap<>(baseConsumerProps());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, PortfolioDataReadyEvent.class.getName());
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PortfolioDataReadyEvent>
    portfolioReadyKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, PortfolioDataReadyEvent>();
        factory.setConsumerFactory(portfolioReadyConsumerFactory());
        return factory;
    }

    private Map<String, Object> baseProducerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, SimulationPortfolioRequestedDto> simulationRequestedProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseProducerProps());
    }
    @Bean
    public KafkaTemplate<String, SimulationPortfolioRequestedDto> simulationRequestedKafkaTemplate() {
        return new KafkaTemplate<>(simulationRequestedProducerFactory());
    }

    @Bean
    public ProducerFactory<String, MarketDataRequestedEvent> marketDataRequestedProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseProducerProps());
    }
    @Bean
    public KafkaTemplate<String, MarketDataRequestedEvent> marketDataRequestedKafkaTemplate() {
        return new KafkaTemplate<>(marketDataRequestedProducerFactory());
    }

    @Bean
    public ProducerFactory<String, SimulationResultDto> simulationResultDtoProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseProducerProps());
    }
    @Bean
    public KafkaTemplate<String, SimulationResultDto> simulationResultDtoKafkaTemplate() {
        return new KafkaTemplate<>(simulationResultDtoProducerFactory());
    }

    @Bean
    public ProducerFactory<String, SimulationStartedDto> simulationStartedProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseProducerProps());
    }
    @Bean
    public KafkaTemplate<String, SimulationStartedDto> simulationStartedKafkaTemplate() {
        return new KafkaTemplate<>(simulationStartedProducerFactory());
    }
}
