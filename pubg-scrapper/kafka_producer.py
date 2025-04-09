from kafka import KafkaProducer
import json
import os
import time
from kafka.errors import NoBrokersAvailable

producer = None

def get_producer():
    global producer
    if producer is None:
        for i in range(10):
            try:
                producer = KafkaProducer(
                    bootstrap_servers=os.getenv("KAFKA_BOOTSTRAP_SERVERS", os.environ['KAFKA_PRODUCER']).split(","),
                    value_serializer=lambda v: json.dumps(v).encode("utf-8"),
                    retries=5,
                    acks="all"
                )
                print("✅ Kafka 연결 성공")
                break
            except NoBrokersAvailable:
                print(f"❌ Kafka 연결 실패... {i+1}/10 재시도 중")
                time.sleep(3)
        else:
            raise Exception("💥 Kafka 브로커 연결 실패. 최대 재시도 초과.")
    return producer

def send_to_kafka(topic, data):
    p = get_producer()
    p.send(topic, data)
    p.flush()
