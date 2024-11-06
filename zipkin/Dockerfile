FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
EXPOSE 9411
ADD ./zipkin-server-3.4.2-exec.jar zipkin-server.jar

ENTRYPOINT [ "java", "-jar", "zipkin-server.jar" ]

