set SERVICE_NAME=Scheduler
set BASE_DIR=C:\software\service-setup\
set PR_INSTALL=%BASE_DIR%prunsrv.exe

REM Service log configuration
set PR_LOGPREFIX=%SERVICE_NAME%
set PR_LOGPATH=%BASE_DIR%
set PR_STDOUTPUT=%BASE_DIR%stdout.txt
set PR_STDERROR=%BASE_DIR%stderr.txt
set PR_LOGLEVEL=Error

REM Path to java installation
REM set PR_JVM=auto
set PR_JVM=C:\software\jdk1.8.0_71\jre\bin\server\jvm.dll
set PR_CLASSPATH=%BASE_DIR%%SERVICE_NAME%.jar

REM Startup configuration
set PR_STARTUP=auto
set PR_STARTIMAGE=C:\software\jdk1.8.0_71\bin\java.exe 

set PR_STARTMODE=jvm
set PR_STARTCLASS=com.intelligrated.scheduler.Bootstrap
set PR_STARTMETHOD=start
REM set PR_STARTPARAMS=-jar#%PR_CLASSPATH%#start#--server.port=8090
set PR_STARTPARAMS=start#--server.port=8095

REM Shutdown configuration
set PR_STOPMODE=jvm
set PR_STOPIMAGE=C:\software\jdk1.8.0_71\bin\java.exe 
set PR_STOPCLASS=com.intelligrated.scheduler.Bootstrap
set PR_STOPMETHOD=stop
REM set PR_STOPPARAMS=-jar#%PR_CLASSPATH%#stop
set PR_STOPPARAMS=stop

REM JVM configuration
set PR_JVMMS=1024
set PR_JVMMX=2048

REM Install service
%PR_INSTALL% //IS//%SERVICE_NAME%