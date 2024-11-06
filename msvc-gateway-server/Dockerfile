FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
EXPOSE 8090
ADD ./target/msvc-gateway-server-0.0.1-SNAPSHOT.jar msvc-gateway-server.jar

ENTRYPOINT [ "java", "-jar", "msvc-gateway-server.jar" ]
