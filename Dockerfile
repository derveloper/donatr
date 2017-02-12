FROM vileda/alpine-sbt-yarn

WORKDIR /code

EXPOSE 8080

RUN /bin/bash && rm -rf donatr \
    && git clone --depth 1 https://github.com/derveloper/donatr.git -b rewrite \
    && cd /code/donatr && sbt donatrUi/fullOptJS assembly \
    && rm -rf /root/.m2 \
    ; rm -rf /root/.npm \
    ; rm -rf /root/.ivy2 \
    ; rm -rf /root/.node-gyp \
    ; rm -rf /root/.sbt \
    && cp /code/donatr/target/scala-2.12/donatr-assembly-0.1.0-SNAPSHOT.jar /code \
    && rm -rf /code/donatr

VOLUME /code/db

CMD ["java","-jar","/code/donatr-assembly-0.1.0-SNAPSHOT.jar"]
