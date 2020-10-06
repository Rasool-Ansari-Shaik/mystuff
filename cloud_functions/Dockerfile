FROM openwhisk/java8action

LABEL maintainer="Rasool Ansari Shaik"
LABEL description="Hello World spring cloud function"

COPY target/hello-0.0.1-SNAPSHOT.jar hello.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/hello.jar"]
