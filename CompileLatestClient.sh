#!/bin/bash

chmod +x ./gradlew

./gradlew -stop

./gradlew clean

./gradlew --no-daemon 


cd target_teavm_javascript

chmod +x CompileJS.sh
./CompileJS.sh

cd ..
