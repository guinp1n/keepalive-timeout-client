package com.hivemq.example.keep_alive_timeout;

import com.hivemq.client.internal.mqtt.MqttClientConnectionConfig;
import com.hivemq.client.internal.mqtt.message.ping.MqttPingReq;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class KeepAliveTimeoutMaker {

    private static final String HOST = getEnvOrDefault("MQTT_HOST", "localhost");
    private static final int PORT = Integer.parseInt(getEnvOrDefault("MQTT_PORT", "1883"));
    private static final String TOPIC = getEnvOrDefault("MQTT_TOPIC", "TEST_TOPIC");
    private static final String CLIENT_ID = getEnvOrDefault("MQTT_CLIENT_ID", "KeepAliveTimeoutMaker");
    private static final String LWT_TOPIC = getEnvOrDefault("MQTT_LWT_TOPIC", "LWT_TOPIC");
    private static final String LWT_MESSAGE = getEnvOrDefault("MQTT_LWT_MESSAGE", "LWT_MESSAGE");
    private static final int KEEP_ALIVE = Integer.parseInt(getEnvOrDefault("MQTT_KEEP_ALIVE", "10"));

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    public static void main(String[] args) throws InterruptedException {
        Mqtt3BlockingClient subscriber = connectFaultySubscriber();
        subscriber.subscribeWith().topicFilter(TOPIC).send();
        Thread.sleep(2000);
    }

    private static Mqtt3BlockingClient connectFaultySubscriber() {
        Mqtt3BlockingClient client = Mqtt3Client.builder()
                .identifier(CLIENT_ID)
                .serverHost(HOST)
                .serverPort(PORT)
                .willPublish()
                .topic(LWT_TOPIC)
                .qos(MqttQos.AT_LEAST_ONCE)
                .payload(LWT_MESSAGE.getBytes())
                .retain(true)
                .applyWillPublish()
                .addConnectedListener(context -> {
                    Channel channel = ((MqttClientConnectionConfig) context.getClientConfig()
                            .getConnectionConfig()
                            .get()).getChannel();

                    channel.pipeline().addBefore("ping", "ping-discarder", new ChannelOutboundHandlerAdapter() {
                        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
                            if (!(msg instanceof MqttPingReq)) {
                                ctx.write(msg, promise);
                            } else {
                                System.out.println("Dropping PINGREQ");
                            }
                        }
                    });
                })
                .buildBlocking();

        client.toAsync().publishes(MqttGlobalPublishFilter.ALL, KeepAliveTimeoutMaker::prettyPrint);
        client.connectWith().cleanSession(false).keepAlive(10).send();
        return client;
    }

    private static void prettyPrint(Mqtt3Publish mqtt3Publish) {
        System.out.printf("Received PUBLISH: {topic: %s, payload: %s, qos: %s}%n",
                mqtt3Publish.getTopic(),
                new String(mqtt3Publish.getPayloadAsBytes()),
                mqtt3Publish.getQos().getCode());
    }
}
