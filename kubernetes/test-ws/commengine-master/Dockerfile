#FROM openjdk:8-jdk-alpine3.7
#FROM ibmjava:jre
FROM ibmjava:sfj
#FROM us.icr.io/cdt-common-rns/base-images/ubuntu-ibmjre:latest
LABEL maintainer="Mahesh Gedepudi"
LABEL description="Communication engine spring java application"

RUN apt-get update -y
RUN apt-get dist-upgrade -y

#RUN echo pwd
#RUN apk --no-cache add maven && mvn --version
#CMD [ "sh", "-c", "pwd" ]
#COPY . /usr/src/comm-engine/
#WORKDIR /usr/src/comm-engine/
#RUN echo pwd
#RUN mvn clean package

#WORKDIR root
#COPY /usr/src/comm-engine/target/comm-engine-0.0.1-SNAPSHOT.jar comm-engine.jar

COPY target/comm-engine-0.0.1-SNAPSHOT.jar comm-engine.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/comm-engine.jar"]

