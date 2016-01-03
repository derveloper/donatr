#!/usr/bin/env bash

rm -Rf webroot
rm -Rf dist && mkdir dist
cd frontend && NODE_ENV='production' npm run compile
cd ..