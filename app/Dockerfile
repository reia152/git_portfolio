FROM gradle:8-jdk21

WORKDIR /app

# 依存関係が変わらない限りこの層のキャッシュが効くよう、ソース全体より先に
# build.gradle / settings.gradle だけをCOPYして依存解決する
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon

COPY . .

EXPOSE 8080

CMD ["gradle", "bootRun", "--no-daemon"]
