FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
EXPOSE 8005
ADD ./target/msvc-items-0.0.1-SNAPSHOT.jar msvc-items.jar

ENTRYPOINT [ "java", "-jar", "msvc-items.jar" ]

