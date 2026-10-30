
package com.hotel.eventprocessor;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttConfig {

    private final SecurityEventRepository repository;
    private final SecurityAlertRepository alertRepository;
    private final SecurityAlertDetector alertDetector;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MqttConfig(
            SecurityEventRepository repository,
            SecurityAlertRepository alertRepository,
            SecurityAlertDetector alertDetector) {

        this.repository = repository;
        this.alertRepository = alertRepository;
        this.alertDetector = alertDetector;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer mqttInbound() {

        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        "tcp://localhost:1883",
                        "hotel-event-processor",
                        "hotel/security/events"
                );

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());

        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler mqttMessageHandler() {

        return message -> {

            try {

                String payload;

                if (message.getPayload() instanceof byte[] bytes) {
                    payload = new String(bytes, StandardCharsets.UTF_8);
                } else {
                    payload = message.getPayload().toString();
                }

                JsonNode json = objectMapper.readTree(payload);

                SecurityEvent event = new SecurityEvent();

                event.setDeviceId(json.get("device_id").asText());
                event.setEventType(json.get("event_type").asText());

                event.setTimestamp(
                        OffsetDateTime.parse(json.get("timestamp").asText())
                );

                event.setSource(json.get("source").asText());

                // Save every incoming security event.
                repository.save(event);

                System.out.println(
                        "Saved security event: " + event.getEventType()
                );

                String alertType = null;
                String severity = null;

                // Rule 1: Detect repeated failed logins.
                if ("LOGIN_FAILED".equals(event.getEventType())) {

                    if (alertDetector.hasRepeatedFailedLogins(
                            event.getDeviceId())) {

                        alertType = "REPEATED_FAILED_LOGINS";
                        severity = "HIGH";
                    }
                }

                // Rule 2: Detect repeated device disconnections.
                if ("DEVICE_DISCONNECTED".equals(event.getEventType())) {

                    if (alertDetector.hasRepeatedDisconnections(
                            event.getDeviceId())) {

                        alertType = "REPEATED_DISCONNECTIONS";
                        severity = "MEDIUM";
                    }
                }

                // Create an alert only when a detection rule is triggered.
                if (alertType != null) {

                    OffsetDateTime oneMinuteAgo =
                            OffsetDateTime.now().minusMinutes(1);

                    // Check for an existing alert during the cooldown.
                    boolean alertAlreadyExists =
                            alertRepository
                                    .existsByDeviceIdAndAlertTypeAndDetectedAtAfter(
                                            event.getDeviceId(),
                                            alertType,
                                            oneMinuteAgo
                                    );

                    if (!alertAlreadyExists) {

                        SecurityAlert alert = new SecurityAlert();

                        alert.setDeviceId(event.getDeviceId());
                        alert.setAlertType(alertType);
                        alert.setSeverity(severity);
                        alert.setDetectedAt(OffsetDateTime.now());

                        // Save the alert to PostgreSQL.
                        alertRepository.save(alert);

                        System.out.println(
                                "SECURITY ALERT SAVED: "
                                        + alertType
                                        + " for device "
                                        + event.getDeviceId()
                        );

                    } else {

                        System.out.println(
                                "Duplicate alert skipped: "
                                        + alertType
                                        + " for device "
                                        + event.getDeviceId()
                        );
                    }
                }

            } catch (Exception e) {

                System.err.println(
                        "Failed to process MQTT event: " + e.getMessage()
                );
            }
        };
    }
}
