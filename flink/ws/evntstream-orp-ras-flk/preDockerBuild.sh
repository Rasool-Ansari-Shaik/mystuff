#!/bin/bash
echo "pre Docker build run code goes here"
sed "s/{DB2_PASSWORD}/${DB2_PASSWORD}/" src/main/resources/config.properties > config-properties.tmp
cp config-properties.tmp src/main/resources/config.properties
sed "s/{FHIR_PASSWORD}/${FHIR_PASSWORD}/" src/main/resources/config.properties > config-propertiestwo.tmp
cp config-propertiestwo.tmp src/main/resources/config.properties
echo $PWD

echo "***List of files available at location***"
ls
#export MAVEN_OPTS=-Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2
java -version
#mvn -s settings.xml clean install -Dmaven.test.skip=true
#mvn clean install -Dmaven.test.skip=true
mvn clean install
