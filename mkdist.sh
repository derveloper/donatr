#!/usr/bin/env bash

cp target/keystore.jceks dist
cp target/*fat.jar dist
cp -R webroot dist/webroot
