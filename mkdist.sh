#!/usr/bin/env bash

rm -Rf webroot
rm -Rf dist && mkdir dist
cd frontend && NODE_ENV='production' npm run compile && cd ..

mvn clean package
cp keystore.jceks dist
cp target/*fat.jar dist
cp -R webroot dist/webroot