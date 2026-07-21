#!/bin/sh
#!/bin/sh

export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

COMMIT_ID=$(git rev-parse --short HEAD)

VERSION_FILE="../desktopRuntime/resources/assets/minecraft/monsoon/version/version.txt"

mkdir -p "$(dirname "$VERSION_FILE")"

echo "Apollo Client 1.8.8 ($COMMIT_ID)" > "$VERSION_FILE"

echo "Updated version.txt:"
cat "$VERSION_FILE"

cd ../ || exit 1

chmod +x gradlew
java -cp "buildtools/MakeOfflineDownload.jar:buildtools/CompileEPK.jar" net.lax1dude.eaglercraft.v1_8.buildtools.workspace.MakeSignedClient "javascript/SignedBundleTemplate.txt" "javascript/classes.js" "javascript/assets.epk" "javascript/lang" "javascript/SignedClientTemplate.txt" "javascript/UpdateDownloadSources.txt" "javascript/EaglercraftX_1.8_Offline_Signed_Client.html"