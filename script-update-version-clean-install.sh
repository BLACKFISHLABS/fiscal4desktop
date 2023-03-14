#!/usr/bin/env bash
clear
mvn versions:set -DnewVersion=1.0.0-RELEASE
mvn versions:commit
mvn clean install -U