[![Build Status](https://travis-ci.org/vileda/donatr.svg?branch=master)](https://travis-ci.org/vileda/donatr)

# donatr
track and collect donations

## tl;dr run donatr
```
git clone https://github.com/vileda/donatr.git
cd donatr/dist
java -jar donatr-1.0-SNAPSHOT-fat.jar
```

then point your browser to `http://localhost:8080`.
the default user is `test` and the password is also `test`.
if you want to change it, you need to compile it by your self.

### docker
Run the docker image from docker hub
```
docker run -i -d -p 8080:8080 -v /path/to/db/folder:/data --name donatr vileda/donatr
```

You can also use docker to build an image, it will install all requirements
```
docker build -t donatr .
docker run -i -d -p 8080:8080 -v /path/to/db/folder:/data --name donatr donatr
```

## who should use it?
the intended purpose of this is to run at hackerspaces that want to collect donations for the space infrastructure.
but it is not tied to any hackerspace. anyone who wants to collect donations could use it.

## collect donations
create accounts and donatables. accounts then tranfere money to donatables.
accounts have a balance where the money is withdrawn from.
deposits are made of negative amount donatables.
one could use it like a prepaid system.

## track donations
donatr is written using a eventsourced pattern. every action in the system is tracked by events.

## technologies uses
the backend is implemented in java and vert.x using a CQRS/ES pattern.
the frontend is implemented using reactjs+redux+material-ui.
database is sqlite or mongodb (or whatever resx supports)

## development
run the following:
```
git clone https://github.com/res-x/resx.git
cd resx/resx-parent && mvn install && cd ../..
git clone https://github.com/vileda/donatr.git
cd donatr && ./mkkeystore.sh && mvn install
```

now start hacking with your favorite editor or IDE!
the main method is in the DonatrMain class.

### backend
the backend is a java maven project located in `src/`

### frontend
the frontend client is located in `frontend`.
you'll need nodejs >= 4.0.0 and npm.
there is also a README.md in `frontend`.

## license
```
Copyright 2015 vileda

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
