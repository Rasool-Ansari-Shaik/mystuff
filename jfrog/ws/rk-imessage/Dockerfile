FROM maven:3.6-ibmjava-8-alpine

# Install OS dependencies
RUN apk add curl
RUN apk add bash

# Upgrade vulnerabilities
RUN echo "http://dl-cdn.alpinelinux.org/alpine/v3.3/main" >> /etc/apk/repositories
RUN echo "http://dl-cdn.alpinelinux.org/alpine/v3.3/community" >> /etc/apk/repositories
RUN apk update
RUN apk upgrade libcrypto1.0 libssl1.0

# Run the build & publish commands with a higher previledge user, otehrwise mvn package will fail.
# Copy source code into docker image
COPY . /build

# Artifactory username & password
ARG ARTIFACTORY_USER_NAME=0
ARG ARTIFACTORY_API_KEY=0

# Build number of the artifact
ARG RELEASE_VERSION=0

# Build and publish to artifactory
WORKDIR /build
RUN mvn deploy --settings settings.xml -Dusername=${ARTIFACTORY_USER_NAME} -Dpassword=${ARTIFACTORY_API_KEY} -Dbuildnumber=${RELEASE_VERSION}

# Not Running the workload as root
# This is good security and also fixes this error:
#  Error: container has runAsNonRoot and image will run as root
# Create a group and user
RUN addgroup -S workloadgroup && adduser -u 1042 -S workloaduser -G workloadgroup -h /home/workloaduser

# Tell docker that all future commands should run as the appuser user
USER 1042

#adding a command so the image doesn't exit
CMD ["/bin/bash", "-c", "tail -f /dev/null"]