#FROM openjdk:8-jdk-alpine3.7
#FROM ibmjava:jre

FROM ibmjava:sfj

#FROM us.icr.io/cdt-common-rns/base-images/ubuntu-ibmjre:latest
LABEL maintainer="Rasool Ansari Shaik"
LABEL description="Hello World spring boot application"

RUN apt-get update -y
RUN apt-get dist-upgrade -y

#RUN echo pwd
#RUN apk --no-cache add maven && mvn --version
#CMD [ "sh", "-c", "pwd" ]
#COPY . /usr/src/hello-world/
#WORKDIR /usr/src/hello-world/
#RUN echo pwd
#RUN mvn clean package

#WORKDIR root
#COPY /usr/src/hello-world/target/helloworld-1.0.0-SNAPSHOT.jar hello-world.jar

COPY target/hello-world-0.0.1-SNAPSHOT.jar hello-world.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/hello-world.jar"]

