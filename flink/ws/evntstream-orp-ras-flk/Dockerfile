#FROM maven:3.6-ibmjava-8-alpine
FROM us.icr.io/cdt-common-rns/base-images/ubi8-ibmjre

LABEL maintainer="KiranKumar Boinapally"
LABEL description="OutReach rule Processor"

# Install OS dependencies
#RUN apk add curl
#RUN apk add bash
#RUN apk add jq

# Upgrade vulnerabilities
#RUN echo "http://dl-cdn.alpinelinux.org/alpine/v3.12/main" >> /etc/apk/repositories
#RUN echo "http://dl-cdn.alpinelinux.org/alpine/v3.12/community" >> /etc/apk/repositories
#RUN apk update
#RUN apk upgrade libcrypto1.0 libssl1.0

# Not Running the workload as root
# This is good security and also fixes this error:
#  Error: container has runAsNonRoot and image will run as root
# Create a group and user
#RUN addgroup -S workloadgroup && adduser -u 1042 -S workloaduser -G workloadgroup -h /home/workloaduser

# Tell docker that all future commands should run as the appuser user
#USER 1042

# Copy Workload Scripts to Image
WORKDIR /home/workloaduser
ADD ./workload-scripts ./

COPY target/outreachruleprocessor-1.0-SNAPSHOT.jar outreachruleprocessor-1.0-SNAPSHOT.jar
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Ddb2.password=${DB2_PWD_ENV_VAR}","-Dchange.password=${FHIR_SERVER_PWD_ENV_VAR}","-jar","/outreachruleprocessor-1.0-SNAPSHOT.jar"]
#adding a command so the image doesn't exit
CMD ["/bin/bash", "-c", "tail -f /dev/null"]
