#store event stream credentials from env
credentials=$EVENT_STREAMS_CRED_ENV_VAR 

echo "credentials: $credentials"
#printenv

#create json file for flink job prog args
echo '{"programArgsList" : ["'$credentials'"]}' > programArgsList.json 
echo "Program Args List File"
cat programArgsList.json

#Call flink upload jar api
echo "uploading flink jar"

uploadResult_JarId=$(curl -v -X POST -H "Accept: application/x-java-archive" -F "jarfile=@outreachruleprocessor-1.0-SNAPSHOT.jar" http://flink-flink-jobmanager-api:8081/v1/jars/upload | jq -r '.filename') 

#output FQN of uploaded jar
echo $uploadResult_JarId
 
#split FQN to get jarId
jarId=$(echo "$uploadResult_JarId" | cut -d "/" -f 5)
echo "JarId: $jarId"

#Call flink run jar api
echo "Run flink jar"

curl -v -X POST -H "Content-Type: application/json" -d @programArgsList.json http://flink-flink-jobmanager-api:8081/v1/jars/$jarId/run | jq -r '.jobid'

#output jobId of running job
echo $runResult_JobId

