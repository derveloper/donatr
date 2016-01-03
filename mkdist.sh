#!/usr/bin/env bash

./mkkeystore.sh
cp keystore.jceks dist
cp target/*fat.jar dist
cp -R webroot dist/webroot