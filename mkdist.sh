#!/usr/bin/env bash

cp keystore.jceks dist
cp target/*fat.jar dist
cp -R webroot dist/webroot
