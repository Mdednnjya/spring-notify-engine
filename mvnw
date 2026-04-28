#!/bin/sh
# Apache Maven Wrapper startup script, version 3.3.2

set -e

# resolve project base directory
APP_HOME="$(cd "$(dirname "$0")" && pwd)"

_search="$APP_HOME"
MAVEN_PROJECTBASEDIR="$APP_HOME"
while true; do
    if [ -f "$_search/.mvn/wrapper/maven-wrapper.properties" ]; then
        MAVEN_PROJECTBASEDIR="$_search"
        break
    fi
    _parent="$(dirname "$_search")"
    if [ "$_parent" = "$_search" ]; then
        break
    fi
    _search="$_parent"
done

WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"
WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"

# parse properties
distributionUrl=""
wrapperUrl=""
if [ -r "$WRAPPER_PROPERTIES" ]; then
    while IFS= read -r _line || [ -n "$_line" ]; do
        case "$_line" in
            \#* | "") continue ;;
        esac
        _key="${_line%%=*}"
        _val="${_line#*=}"
        case "$_key" in
            distributionUrl) distributionUrl="$_val" ;;
            wrapperUrl)      wrapperUrl="$_val"      ;;
        esac
    done < "$WRAPPER_PROPERTIES"
fi

# download wrapper jar if missing
if [ ! -f "$WRAPPER_JAR" ] && [ -n "$wrapperUrl" ]; then
    if command -v curl > /dev/null 2>&1; then
        curl -fsSL -o "$WRAPPER_JAR" "$wrapperUrl"
    elif command -v wget > /dev/null 2>&1; then
        wget -q -O "$WRAPPER_JAR" "$wrapperUrl"
    else
        echo "ERROR: curl or wget required to download Maven wrapper" >&2
        exit 1
    fi
fi

# locate java
if [ -n "$JAVA_HOME" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
elif command -v java > /dev/null 2>&1; then
    JAVACMD="java"
else
    echo "ERROR: JAVA_HOME not set and java not found in PATH" >&2
    exit 1
fi

exec "$JAVACMD" \
    ${MAVEN_OPTS:-} \
    -classpath "$WRAPPER_JAR" \
    "-Dmaven.multiModuleProjectDirectory=$MAVEN_PROJECTBASEDIR" \
    org.apache.maven.wrapper.MavenWrapperMain \
    "$@"
