package com.medibook.notification.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import io.swagger.v3.oas.models.OpenAPI;

class NotificationConfigTest {

    private final RabbitMQConfig rabbitMQConfig = new RabbitMQConfig();

    @Test
    void swaggerConfigBuildsOpenApiMetadata() {
        OpenAPI openAPI = new SwaggerConfig().notificationServiceOpenAPI();

        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Notification Service API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0");
    }

    @Test
    void rabbitMqBeansUseExpectedNames() {
        Queue queue = rabbitMQConfig.notificationQueue();
        TopicExchange exchange = rabbitMQConfig.notificationExchange();
        Binding binding = rabbitMQConfig.notificationBinding();

        assertThat(queue.getName()).isEqualTo(RabbitMQConfig.NOTIFICATION_QUEUE);
        assertThat(exchange.getName()).isEqualTo(RabbitMQConfig.NOTIFICATION_EXCHANGE);
        assertThat(binding.getRoutingKey()).isEqualTo(RabbitMQConfig.NOTIFICATION_ROUTING_KEY);
        assertThat(rabbitMQConfig.jsonMessageConverter()).isInstanceOf(Jackson2JsonMessageConverter.class);
    }
}
