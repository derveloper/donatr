# donatr
track and collect donations

## tl;dr run donatr
```
git clone https://github.com/vileda/donatr.git
cd donatr/dist
java -jar donatr-1.0-SNAPSHOT-fat.jar
```

then point your browser to `http://localhost:8080`

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

## technlogies uses
the backend is implemented in java and vert.x using a CQRS/ES pattern.
the frontend is implemented using reactjs+redux+material-ui.
database is sqlite or mongodb

## license
```
Copyright [2015] [vileda]

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