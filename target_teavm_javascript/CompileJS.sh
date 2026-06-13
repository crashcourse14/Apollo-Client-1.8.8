#!/bin/sh
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

cd ../
chmod +x gradlew
./gradlew --no-daemon target_teavm_javascript:assembleMainComponents
