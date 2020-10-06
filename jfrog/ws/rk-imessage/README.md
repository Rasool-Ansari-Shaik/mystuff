# rk-imessage

Description - This is Java interface, which provides method signature for implementing Communication vendor business logic.

Input/Output - Json Object

POM Dependency - Gson.jar 

Endpoint - CommEngine/Imessage.

Exception Handling - No excpetion handling required for this Java interface. Exceptions hanlding is handled at implementaion level.

CI/CD - CI is configured for this IMessage 



# CI Setup for IMessage
Following are important to leverage whc-toolchain for CI.

## pom.xml
Following section must be available in pom.xml

```xml
<distributionManagement>
		<repository>
			<id>central</id>
			<name>artifactory-dal10-01-node-10.swg-devops.com-releases</name>
			<url>https://na.artifactory.swg-devops.com:443/artifactory/wh-phytel-unicorn-team-maven-local</url>
		</repository>
		<snapshotRepository>
			<id>snapshots</id>
			<name>artifactory-dal10-01-node-10.swg-devops.com-snapshots</name>
			<url>https://na.artifactory.swg-devops.com:443/artifactory/wh-phytel-unicorn-team-maven-local</url>
		</snapshotRepository>
</distributionManagement>
```

## settings.xml
Add Maven repository to settings.xml. If the below section is missing ```mvn publish``` may fail during <i>Build Docker image</i> stage.

```xml
<repository>
	<snapshots>
		<enabled>false</enabled>
	</snapshots>
		<id>maven</id>
		<name>maven-apache-org</name>
		<url>https://repo.maven.apache.org/maven2</url>
</repository>
```
## pipeline.config
Add the following parameters to pipeline.config. Since the purpose of the CI is to build and publish jar to artifactory, there is no need for a helm chart or close the artifacts to umbrella repository.

```
CI:
  DOCKER_IMAGE_NAME: "commengine-<library name>"
  UMBRELLA_REPO_PATH: "<any git url>"
  REGISTRY_NAMESPACE: "cdt-commengine-builds"
  NOHELMCHART: "true"
  NOUMBRELLAPUBLISH: "true"
  REPLACEARGS: "true"
```

## Create CI pipeline using whc-toolchain
Click on the link below to launch the whc-tool

https://github.ibm.com/whc-toolchain/whc-pipeline-links/blob/master/README.md