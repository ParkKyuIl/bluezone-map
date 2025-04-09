import requests
import json
import random
import os
from datetime import datetime
from kafka import KafkaProducer


from kafka_producer import send_to_kafka
from userScrapper import crawl_pubg_leaderboard_usernames

API_KEY = os.environ['PUBG_API_KEY']
HEADERS = {
    'Authorization': f'Bearer {API_KEY}',
    'Accept': 'application/vnd.api+json'
}


def get_one_random_match_per_player(player_list):
    selected_matches = []
    for player_name in player_list:
        try:
            url = f"https://api.pubg.com/shards/steam/players?filter[playerNames]={player_name}"
            res = requests.get(url, headers=HEADERS)
            data = res.json()

            if 'data' in data and data['data']:
                matches = data['data'][0]['relationships']['matches']['data']
                if matches:
                    match_id = random.choice(matches)['id']
                    selected_matches.append((match_id, player_name))
        except Exception as e:
            print(f"{player_name} 매치 가져오는 중 에러: {e}")
    return selected_matches


def get_telemetry_url_and_map_name(match_id):
    url = f"https://api.pubg.com/shards/steam/matches/{match_id}"
    res = requests.get(url, headers=HEADERS)
    data = res.json()

    telemetry_url = next(asset['attributes']['URL'] for asset in data['included'] if asset['type'] == 'asset')
    map_name = data['data']['attributes']['mapName']
    return telemetry_url, map_name

def save_last_gamestate_log(telemetry_url, map_name):
    res = requests.get(telemetry_url)
    logs = res.json()

    gamestates = [log for log in logs if log['_T'] == 'LogGameStatePeriodic']
    if not gamestates:
        print("No game state logs found.")
        return
    last_log = gamestates[-1]
    

    zone_data = {
        "map": map_name,
        "poisonZone": last_log['gameState']['poisonGasWarningPosition'],
        "poisonRadius": last_log['gameState']['poisonGasWarningRadius'],
        "timestamp": last_log['_D']
    }

    send_to_kafka("poison-zone", zone_data)
    print(f"Kafka 이벤트 전송 완료: {zone_data}")

def connectKafka():
    KAFKA_BROKER = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "kafka-service:9092")

    # 최대 10초 동안 재시도
    for i in range(10):
        try:
            producer = KafkaProducer(
                bootstrap_servers=KAFKA_BROKER,
                value_serializer=lambda v: json.dumps(v).encode("utf-8")
            )
            print("✅ Kafka 연결 성공")
            break
        except errors.NoBrokersAvailable as e:
            print(f"Kafka 브로커 연결 실패... 재시도 {i+1}/10")
            time.sleep(3)
    else:
        raise Exception("Kafka 연결 실패. 최대 재시도 초과.")

# 실행
if __name__ == "__main__":

    matches = get_one_random_match_per_player(crawl_pubg_leaderboard_usernames())
    for match_id, player in matches:
        try:
            telemetry_url, map_name = get_telemetry_url_and_map_name(match_id)
            print(f"{player}의 매치 {match_id} (맵: {map_name}) 저장 중...")
            save_last_gamestate_log(telemetry_url, map_name)
        except Exception as e:
            print(f"[{match_id}] 에러 발생: {e}")