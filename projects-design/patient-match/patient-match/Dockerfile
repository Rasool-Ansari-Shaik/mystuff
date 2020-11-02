FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/patient-service-0.0.1-SNAPSHOT.jar patient-match.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/patient-match.jar"]