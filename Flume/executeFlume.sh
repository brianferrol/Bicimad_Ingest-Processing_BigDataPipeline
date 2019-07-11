#!/usr/bin/env bash

../flume/apache-flume-1.9.0-bin/bin/flume-ng agent \
   -f /home/bferrol/raw/conf/flume.conf \
   --name Agent1 \
   -Dflume.root.logger=INFO,console \
   --classpath "./lib/json-cleaner_2.11-1.0.0.jar"
#####################################################
## Para enviar datos:
## 
#####################################################
