#!/usr/bin/env bash

SCENARIO=$1
DIRECTORY="./docker_results"
COMMAND="commands_${SCENARIO}.txt"
RESULTS="drone_delivery_${SCENARIO}_results.txt"
INITIAL="drone_delivery_initial_${SCENARIO}_results.txt"
DIFFERENCE="diff_results_${SCENARIO}.txt"
TIMEFORMAT="%R"

mkdir -p ${DIRECTORY}

function dockerstuff(){
  docker exec grocery-express-app-1 sh -c "\
    java -jar grocery-express-project-0.0.1-SNAPSHOT.jar < ${COMMAND} > ${RESULTS} && \
    diff -s ${RESULTS} ${INITIAL} > ${DIFFERENCE}"
  docker cp grocery-express-app-1:/cs6310/${RESULTS} ${DIRECTORY}
  docker cp grocery-express-app-1:/cs6310/${DIFFERENCE} ${DIRECTORY}
}

if [[ -f "./test_scenarios/${COMMAND}" ]]; then
  docker exec -d grocery-express-app-1 sh -c "mkdir docker_results && sleep 100"  > /dev/null
  RUNTIME=`time (dockerstuff) 2>&1`
  FILE_CONTENTS="${DIRECTORY}/${DIFFERENCE}"
  echo "Elapsed time: ${RUNTIME}s" >> ${FILE_CONTENTS}
  echo "$(cat ${FILE_CONTENTS})"
else
    echo "File ${COMMAND} does not exist."
fi