FROM openjdk:13-jdk-alpine
COPY target/imgshare-*.jar /app.jar
VOLUME /data
WORKDIR /data
ENTRYPOINT ["java","-jar","/app.jar"]
