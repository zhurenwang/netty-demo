@echo off
reg add "HKCU\Software\Microsoft\Windows\CurrentVersion\Internet Settings" /v AutoConfigURL /t REG_SZ /d "http://127.0.0.1:1080/proxy.pac" /f
java -jar netty-client-1.0-SNAPSHOT-jar-with-dependencies.jar