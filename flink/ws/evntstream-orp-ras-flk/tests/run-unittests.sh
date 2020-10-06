#!/bin/bash

echo -e '<?xml version="1.0" encoding="UTF-8" ?>\n<testsuites>' > unittest.xml; 
for i in `find ../ -name surefire-reports`; do 
  for j in `ls $i/*xml`; do 
    cat $j | grep -v '<?xml version' >> unittest.xml; 
    echo >> unittest.xml; 
  done; 
done; 
echo '</testsuites>' >> unittest.xml; 
cat unittest.xml

python3 prepUtils.py --publishdeployrecord True --env ${LOGICAL_ENV_NAME} --status 'pass'
python3 prepUtils.py --publishbuildrecord True --branch ${GIT_BRANCH} --repositoryurl ${GIT_URL} --commitid ${GIT_COMMIT} --status 'pass'
