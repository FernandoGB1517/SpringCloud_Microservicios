FROM amazoncorretto:17-alpine-jdk
WORKDIR /app

ADD ./target/msvc-users-0.0.1-SNAPSHOT.jar msvc-users.jar

ENTRYPOINT [ "java", "-jar", "msvc-users.jar" ]
