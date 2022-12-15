@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  app startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and APP_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\app-1.0-SNAPSHOT.jar;%APP_HOME%\lib\javalin-4.0.1.jar;%APP_HOME%\lib\slf4j-simple-2.0.5.jar;%APP_HOME%\lib\ebean-13.10.1.jar;%APP_HOME%\lib\ebean-core-13.10.1.jar;%APP_HOME%\lib\ebean-core-type-13.10.1.jar;%APP_HOME%\lib\ebean-platform-all-13.10.1.jar;%APP_HOME%\lib\ebean-platform-hsqldb-13.10.1.jar;%APP_HOME%\lib\ebean-platform-h2-13.10.1.jar;%APP_HOME%\lib\ebean-platform-clickhouse-13.10.1.jar;%APP_HOME%\lib\ebean-platform-db2-13.10.1.jar;%APP_HOME%\lib\ebean-platform-hana-13.10.1.jar;%APP_HOME%\lib\ebean-platform-mariadb-13.10.1.jar;%APP_HOME%\lib\ebean-platform-mysql-13.10.1.jar;%APP_HOME%\lib\ebean-platform-nuodb-13.10.1.jar;%APP_HOME%\lib\ebean-platform-oracle-13.10.1.jar;%APP_HOME%\lib\ebean-platform-postgres-13.10.1.jar;%APP_HOME%\lib\ebean-platform-sqlanywhere-13.10.1.jar;%APP_HOME%\lib\ebean-platform-sqlite-13.10.1.jar;%APP_HOME%\lib\ebean-platform-sqlserver-13.10.1.jar;%APP_HOME%\lib\ebean-api-13.10.1.jar;%APP_HOME%\lib\ebean-annotation-8.3.jar;%APP_HOME%\lib\ebean-ddl-generator-13.10.1.jar;%APP_HOME%\lib\ebean-migration-13.6.2.jar;%APP_HOME%\lib\h2-2.1.214.jar;%APP_HOME%\lib\postgresql-42.5.1.jar;%APP_HOME%\lib\jaxb-runtime-4.0.0.jar;%APP_HOME%\lib\activation-1.1.1.jar;%APP_HOME%\lib\thymeleaf-layout-dialect-3.1.0.jar;%APP_HOME%\lib\thymeleaf-extras-java8time-3.0.4.RELEASE.jar;%APP_HOME%\lib\thymeleaf-expression-processor-3.1.0.jar;%APP_HOME%\lib\thymeleaf-3.1.1.RELEASE.jar;%APP_HOME%\lib\bootstrap-5.2.3.jar;%APP_HOME%\lib\avaje-applog-slf4j-1.0.jar;%APP_HOME%\lib\slf4j-api-2.0.5.jar;%APP_HOME%\lib\jetty-webapp-9.4.43.v20210629.jar;%APP_HOME%\lib\websocket-server-9.4.43.v20210629.jar;%APP_HOME%\lib\jetty-servlet-9.4.43.v20210629.jar;%APP_HOME%\lib\jetty-security-9.4.43.v20210629.jar;%APP_HOME%\lib\jetty-server-9.4.43.v20210629.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.5.31.jar;%APP_HOME%\lib\ebean-joda-time-13.10.1.jar;%APP_HOME%\lib\ebean-jackson-jsonnode-13.10.1.jar;%APP_HOME%\lib\ebean-jackson-mapper-13.10.1.jar;%APP_HOME%\lib\ebean-datasource-8.2.jar;%APP_HOME%\lib\ebean-querybean-13.10.1.jar;%APP_HOME%\lib\ebean-ddl-runner-2.2.jar;%APP_HOME%\lib\avaje-config-2.2.jar;%APP_HOME%\lib\ebean-datasource-api-8.2.jar;%APP_HOME%\lib\avaje-applog-1.0.jar;%APP_HOME%\lib\classpath-scanner-7.1.jar;%APP_HOME%\lib\checker-qual-3.5.0.jar;%APP_HOME%\lib\jaxb-core-4.0.0.jar;%APP_HOME%\lib\ognl-3.3.4.jar;%APP_HOME%\lib\attoparser-2.0.6.RELEASE.jar;%APP_HOME%\lib\unbescape-1.1.6.RELEASE.jar;%APP_HOME%\lib\groovy-extensions-2.0.0.jar;%APP_HOME%\lib\groovy-4.0.0.jar;%APP_HOME%\lib\websocket-servlet-9.4.43.v20210629.jar;%APP_HOME%\lib\javax.servlet-api-3.1.0.jar;%APP_HOME%\lib\websocket-client-9.4.43.v20210629.jar;%APP_HOME%\lib\jetty-client-9.4.43.v20210629.jar;%APP_HOME%\lib\jetty-http-9.4.43.v20210629.jar;%APP_HOME%\lib\websocket-common-9.4.43.v20210629.jar;%APP_HOME%\lib\jetty-io-9.4.43.v20210629.jar;%APP_HOME%\lib\jetty-xml-9.4.43.v20210629.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.5.31.jar;%APP_HOME%\lib\kotlin-stdlib-1.5.31.jar;%APP_HOME%\lib\avaje-lang-1.1.jar;%APP_HOME%\lib\persistence-api-3.0.jar;%APP_HOME%\lib\ebean-types-3.0.jar;%APP_HOME%\lib\ebean-migration-auto-1.2.jar;%APP_HOME%\lib\ebean-externalmapping-api-13.10.1.jar;%APP_HOME%\lib\antlr4-runtime-4.8-1.jar;%APP_HOME%\lib\joda-time-2.11.1.jar;%APP_HOME%\lib\classpath-scanner-api-7.1.jar;%APP_HOME%\lib\jakarta.xml.bind-api-4.0.0.jar;%APP_HOME%\lib\angus-activation-1.0.0.jar;%APP_HOME%\lib\jakarta.activation-api-2.1.0.jar;%APP_HOME%\lib\txw2-4.0.0.jar;%APP_HOME%\lib\istack-commons-runtime-4.1.1.jar;%APP_HOME%\lib\javassist-3.29.0-GA.jar;%APP_HOME%\lib\jetty-util-ajax-9.4.43.v20210629.jar;%APP_HOME%\lib\jetty-util-9.4.43.v20210629.jar;%APP_HOME%\lib\websocket-api-9.4.43.v20210629.jar;%APP_HOME%\lib\annotations-13.0.jar;%APP_HOME%\lib\kotlin-stdlib-common-1.5.31.jar


@rem Execute app
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %APP_OPTS%  -classpath "%CLASSPATH%" hexlet.code.App %*

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable APP_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%APP_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
