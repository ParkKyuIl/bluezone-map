FROM python:3.11-slim

# 필요한 시스템 패키지 설치
RUN apt-get update && apt-get install -y \
    curl unzip gnupg wget \
    fonts-liberation libappindicator3-1 libasound2 libatk-bridge2.0-0 \
    libnspr4 libnss3 libxss1 xdg-utils libu2f-udev libvulkan1 \
    chromium chromium-driver

# 작업 디렉토리
WORKDIR /app
COPY . /app

# 파이썬 패키지 설치
RUN pip install --upgrade pip
RUN pip install -r requirements.txt


# 4. 코드 복사
COPY . .

# 5. 실행 커맨드
CMD ["python", "match_crawler.py"]

