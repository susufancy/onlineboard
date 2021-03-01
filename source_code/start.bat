@echo off
set cmd1=php Server/server.php start
set cmd2=java -jar app.jar
start %cmd1%
start %cmd2%

