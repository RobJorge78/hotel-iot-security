
import json
import random
import time
from datetime import datetime, timezone

import paho.mqtt.client as mqtt

# Fake hotel devices
DEVICES = [
    "room-101-tv",
    "room-102-tablet",
    "room-103-tv",
    "lobby-wifi",
    "front-desk-terminal"
]

EVENT_TYPES = [
    "LOGIN_SUCCESS",
    "LOGIN_FAILED",
    "DEVICE_CONNECTED",
    "DEVICE_DISCONNECTED"
]

MQTT_BROKER = "localhost"
MQTT_PORT = 1883
MQTT_TOPIC = "hotel/security/events"


def generate_event():
    return {
        "device_id": random.choice(DEVICES),
        "event_type": random.choice(EVENT_TYPES),
        "timestamp": datetime.now(timezone.utc).isoformat(),
        "source": "hotel-simulator"
    }


def main():
    client = mqtt.Client(
        mqtt.CallbackAPIVersion.VERSION2
    )

    client.connect(MQTT_BROKER, MQTT_PORT, 60)
    client.loop_start()

    print("Hotel IoT Security Simulator Started")
    print("Connected to MQTT broker")

    try:
        while True:
            event = generate_event()
            message = json.dumps(event)

            result = client.publish(
                MQTT_TOPIC,
                message,
                qos=1
            )
            result.wait_for_publish()

            if result.rc != mqtt.MQTT_ERR_SUCCESS:
                raise RuntimeError("MQTT publish failed")

            print("Published:", message)
            time.sleep(3)

    except KeyboardInterrupt:
        print("\nSimulator stopped.")

    finally:
        client.disconnect()
        client.loop_stop()


if __name__ == "__main__":
    main()
