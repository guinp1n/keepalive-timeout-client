#!/usr/bin/env bash

# Export environment variables
export MQTT_HOST="localhost"
export MQTT_PORT="1883"
export MQTT_TOPIC="TEST_TOPIC"
export MQTT_CLIENT_ID="KeepAliveTimeoutMaker"
export MQTT_LWT_TOPIC="LWT_TOPIC"
export MQTT_LWT_MESSAGE="LWT_MESSAGE"
export MQTT_KEEP_ALIVE="10"

# Run the Java application
jarfile="./build/libs/keepalive-timeout-client-1.0-SNAPSHOT-all.jar"
if [ -f "$jarfile" ]; then
  java -jar "$jarfile"
else
  echo "JAR file not found: $jarfile"
fi