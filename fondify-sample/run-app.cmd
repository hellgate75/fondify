@echo off
java -jar target\fondify-sample-0.0.1-SNAPSHOT.jar -debug=true -use.logger=true wrongArgument -enable.autorun=true -unlimited.autorun.threads=true -enable.console=true -run.services=printargumentsperlineinjectable,myunsingnedinjectableelement,myInjectableElement 
