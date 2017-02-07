#!/bin/sh

docker build . -t vileda/donatr2 --build-arg CACHEBUST=$(date +%s)