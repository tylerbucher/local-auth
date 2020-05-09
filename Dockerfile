FROM openjdk:8u171-jdk-alpine AS build

WORKDIR /tmp

ARG SSH_FILE

ARG HOST

RUN apk update && \
    apk upgrade && \
    apk add --no-cache \
    openssh-client \
    git \
    ca-certificates \
    && mkdir lib

COPY ${SSH_FILE} /

RUN mkdir /root/.ssh/
RUN cp /ssh.txt /root/.ssh/id_rsa \
    && chmod 0600 /root/.ssh/id_rsa

RUN touch /root/.ssh/known_hosts \
    && ssh-keyscan github.com >> /root/.ssh/known_hosts

RUN git clone git@github.com:agent6262/LocalAuth.git \
    && cd LocalAuth \
    && sed -i ${HOST} src/main/resources/public/401.html \
    && chmod +x ./gradlew \
    && ./gradlew -q build \
    && cd build/libs \
    && cp LocalAuth.jar /tmp/lib \
    && cd /tmp

FROM openjdk:8u171-jre-alpine

COPY --from=build /tmp/lib /opt/localauth

WORKDIR /opt/localauth

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/opt/localauth/LocalAuth.jar", "8080"]