FROM openjdk:14.0.1-jdk AS build

COPY . /

RUN set -ex; \
    chmod +x ./gradlew ; \
    ./gradlew -q build ; \
    mv build/libs/LocalAuth.jar / ; \

FROM openjdk:14-alpine

COPY --from=build LocalAuth.jar /opt/localauth/

VOLUME /opt/localauth

WORKDIR /opt/localauth

ENTRYPOINT ["java", "-jar", "/opt/localauth/LocalAuth.jar"]
