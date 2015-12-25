# donatr
track and collect donations

## tl;dr run donatr
```
cd dist
java -jar donatr-1.0-SNAPSHOT-fat.jar
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

## technlogies uses
the backend is implemented in java and vert.x using a CQRS/ES pattern.
the frontend is implemented using reactjs+redux+material-ui.
database is sqlite or mongodb
