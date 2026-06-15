#!/bin/sh

export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

COMMIT_ID=$(git rev-parse --short HEAD)
COMMIT_DATE=$(git log -1 --format=%cd --date=short)

VERSION_FILE="../desktopRuntime/resources/assets/minecraft/monsoon/version/version.txt"

mkdir -p "$(dirname "$VERSION_FILE")"

echo "Monsoon Client v1.0 Patch 0 ($COMMIT_ID / $COMMIT_DATE)" > "$VERSION_FILE"

echo "Updated version.txt:"
cat "$VERSION_FILE"

cd ../ || exit 1

chmod +x gradlew
./gradlew --no-daemon target_teavm_javascript:assembleMainComponents