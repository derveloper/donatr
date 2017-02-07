FROM vileda/alpine-sbt-yarn

WORKDIR /code

EXPOSE 8080

RUN /bin/bash && rm -rf donatr
RUN /bin/bash && git clone --depth 1 https://github.com/derveloper/donatr.git -b rewrite
RUN /bin/bash && cd donatr/frontend && yarn && yarn run build
RUN /bin/bash && cd donatr && sbt assembly
RUN /bin/bash && rm -rf /root/.m2
RUN /bin/bash && rm -rf /root/.sbt
RUN /bin/bash && cp donatr/target/scala-2.12/donatr-assembly-0.1.0-SNAPSHOT.jar /code
RUN /bin/bash && rm -rf donatr

CMD ["java","-jar","/code/donatr-assembly-0.1.0-SNAPSHOT.jar"]
