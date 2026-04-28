@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF)
@REM Apache Maven Wrapper startup batch script, version 3.3.2
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET "BASE_DIR=%~dp0") ELSE (SET "BASE_DIR=%__MVNW_ARG0_NAME__%")

@SET MAVEN_PROJECTBASEDIR=%BASE_DIR%
@SET WRAPPER_PROPERTIES=%BASE_DIR%.mvn\wrapper\maven-wrapper.properties

@FOR /F "usebackq tokens=1,2 delims==" %%a IN ("%WRAPPER_PROPERTIES%") DO (
  @IF "%%a"=="distributionUrl" SET distributionUrl=%%b
  @IF "%%a"=="wrapperUrl" SET wrapperUrl=%%b
)

@SET WRAPPER_JAR=%BASE_DIR%.mvn\wrapper\maven-wrapper.jar
@IF NOT EXIST "%WRAPPER_JAR%" (
  @IF NOT "%wrapperUrl%"=="" (
    @ECHO Downloading Maven Wrapper jar from %wrapperUrl%
    @curl -fsSL -o "%WRAPPER_JAR%" "%wrapperUrl%"
    @IF ERRORLEVEL 1 (
      @ECHO Could not download %wrapperUrl%
      @EXIT /B 1
    )
  )
)

@SET JAVA_EXE=java.exe
@IF NOT "%JAVA_HOME%"=="" SET JAVA_EXE=%JAVA_HOME%\bin\java.exe

%JAVA_EXE% -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" org.apache.maven.wrapper.MavenWrapperMain %*
@IF ERRORLEVEL 1 EXIT /B 1
