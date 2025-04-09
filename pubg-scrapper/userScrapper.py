import random
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
import time

def crawl_pubg_leaderboard_usernames():
    options = Options()
    options.add_argument("--headless")
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-gpu")  # 💡 중요!
    options.add_argument("--disable-dev-shm-usage")
    driver = webdriver.Chrome(service=Service("/usr/bin/chromedriver"), options=options)
    driver.get("https://pubg.op.gg/leaderboard")

    time.sleep(3)  # 페이지 로딩 대기

    # 닉네임 요소 찾기
    nickname_elements = driver.find_elements(By.CSS_SELECTOR, "a.leader-board__nickname")

    nicknames = []
    for el in nickname_elements:
        name = el.text.strip()
        if name:
            nicknames.append(name)
    
    return random.choices(nicknames,k=5)



