FROM java:openjdk-8-jdk-alpine
MAINTAINER IFlavours's Docker Team <docker@iflavours.nl>

ENV SBT_VERSION 0.13.13
ENV SBT_HOME /usr/local/sbt
ENV PATH ${PATH}:${SBT_HOME}/bin

# Install sbt
RUN apk add --no-cache --update bash && \
    wget -q -O - "http://dl.bintray.com/sbt/native-packages/sbt/$SBT_VERSION/sbt-$SBT_VERSION.tgz" | gunzip | tar -x && \
    cp -a sbt-launcher-packaging-$SBT_VERSION/* /usr/local && rm -rf sbt-launcher-packaging-$SBT_VERSION && \
    echo -ne "- with sbt $SBT_VERSION\n" >> /root/.built

ENV VERSION=v7.5.0 NPM_VERSION=4

# For base builds
ENV CONFIG_FLAGS="--fully-static --without-npm" DEL_PKGS="libstdc++" RM_DIRS=/usr/include

RUN apk add --no-cache curl make gcc g++ python linux-headers binutils-gold gnupg libstdc++ && \
  gpg --keyserver ha.pool.sks-keyservers.net --recv-keys \
    9554F04D7259F04124DE6B476D5A82AC7E37093B \
    94AE36675C464D64BAFA68DD7434390BDBE9B9C5 \
    0034A06D9D9B0064CE8ADF6BF1747F4AD2306D93 \
    FD3A5288F042B6850C66B31F09FE44734EB7990E \
    71DCFD284A79C3B38668286BC97EC7A07EDE3FC1 \
    DD8F2338BAE7501E3DD5AC78C273792F7D83545D \
    C4F0DFFF4E8C1A8236409D08E73BC641CC11F4C8 \
    B9AE9905FFD7803F25714661B63B535A4C206CA9 && \
  curl -sSLO https://nodejs.org/dist/${VERSION}/node-${VERSION}.tar.xz && \
  curl -sSL https://nodejs.org/dist/${VERSION}/SHASUMS256.txt.asc | gpg --batch --decrypt | \
    grep " node-${VERSION}.tar.xz\$" | sha256sum -c | grep . && \
  tar -xf node-${VERSION}.tar.xz && \
  cd node-${VERSION} && \
  ./configure --prefix=/usr ${CONFIG_FLAGS} && \
  make -j$(getconf _NPROCESSORS_ONLN) && \
  make install && \
  cd / && \
  if [ -x /usr/bin/npm ]; then \
    npm install -g npm@${NPM_VERSION} && \
    find /usr/lib/node_modules/npm -name test -o -name .bin -type d | xargs rm -rf; \
  fi && \
  apk del gcc g++ linux-headers binutils-gold ${DEL_PKGS} && \
  rm -rf ${RM_DIRS} /node-${VERSION}* /usr/share/man /tmp/* /var/cache/apk/* \
    /root/.npm /root/.node-gyp /root/.gnupg /usr/lib/node_modules/npm/man \
    /usr/lib/node_modules/npm/doc /usr/lib/node_modules/npm/html /usr/lib/node_modules/npm/scripts


ENV PATH /root/.yarn/bin:$PATH

RUN apk update \
  && apk add binutils tar git \
  && rm -rf /var/cache/apk/* \
  && /bin/bash \
  && touch ~/.bashrc \
  && curl -o- -L https://yarnpkg.com/install.sh | bash

WORKDIR /code

RUN /bin/bash && git clone https://github.com/derveloper/donatr.git && cd donatr && git checkout rewrite
RUN /bin/bash && cd donatr/frontend && yarn && yarn run build
RUN /bin/bash && cd donatr && sbt assembly
EXPOSE 8080

ARG CACHEBUST=1

RUN /bin/bash && cd donatr && git pull
RUN /bin/bash && cd donatr/frontend && yarn && yarn run build
RUN /bin/bash && cd donatr && sbt assembly

CMD ["java","-jar","/code/donatr/target/scala-2.12/donatr-assembly-0.1.0-SNAPSHOT.jar"]
