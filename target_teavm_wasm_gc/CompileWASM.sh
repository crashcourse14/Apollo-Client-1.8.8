#!/bin/sh
cd ../
chmod +x gradlew
./gradlew --no-daemon target_teavm_wasm_gc:assembleMainComponents
