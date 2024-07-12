# Keep Alive Timeout Reproducer MQTT Client

This hivemq-mqtt-client will prevent inself from sending PINGREQ from the client to the broker.

The broker must disconnect the client after 1.5*keepalive (because of the Keep Alive Timeout) and the broker must publish the LWT.

The client is using MQTT v.3.1.1


## Build from code

### Use JDK 11
```shell
java -version
```
```
openjdk version "11.0.16" 2022-07-19
OpenJDK Runtime Environment Temurin-11.0.16+8 (build 11.0.16+8)
OpenJDK 64-Bit Server VM Temurin-11.0.16+8 (build 11.0.16+8, mixed mode)
```

### Build from code
```
cd prevent-ping
```

```shell
./gradlew clean shadowJar
```

## Run
The following environment variables can be set to configure the MQTT client:

- `MQTT_HOST`: The MQTT broker host (default: "localhost")
- `MQTT_PORT`: The MQTT broker port (default: 1883)
- `MQTT_TOPIC`: The topic to **subscribe** to (default: "TEST_TOPIC")
- `MQTT_CLIENT_ID`: The client identifier (default: "KeepAliveTimeoutMaker")
- `MQTT_LWT_TOPIC`: The Last Will and Testament topic (default: "LWT_TOPIC")
- `MQTT_LWT_MESSAGE`: The Last Will and Testament message (default: "LWT_MESSAGE")
- `MQTT_KEEP_ALIVE`: The keep-alive interval in seconds (default: 10)

If any of these environment variables are not set or are empty, the application will use the default values specified in parentheses.

Example usage:

```
export MQTT_HOST="localhost"
export MQTT_PORT=1883
export MQTT_TOPIC="TEST_TOPIC"
export MQTT_CLIENT_ID="MY_KeepAliveTimeoutMaker"
export MQTT_LWT_TOPIC="LWT_TOPIC"
export MQTT_LWT_MESSAGE="LWT_MESSAGE"
export MQTT_KEEP_ALIVE=10

java -jar build/libs/prevent-ping-1.0-SNAPSHOT-all.jar
```



