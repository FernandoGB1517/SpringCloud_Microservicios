FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
EXPOSE 9100
ADD ./target/msvc-oauth-0.0.1-SNAPSHOT.jar msvc-oauth.jar

ENTRYPOINT [ "java", "-jar", "msvc-oauth.jar" ]
