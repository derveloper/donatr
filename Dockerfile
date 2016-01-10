from base/archlinux

run pacman-key --populate archlinux
run pacman-key --refresh-keys

#install base system!
run pacman -Syyu --noconfirm
run pacman-db-upgrade
run pacman -Sq --noconfirm --noprogressbar jdk8-openjdk java-runtime-common java-environment-common maven git nodejs npm base-devel phantomjs
run chmod +x /etc/profile.d/jre.sh && /etc/profile.d/jre.sh

#bust cache
ADD http://www.random.org/strings/?num=10&len=8&digits=on&upperalpha=on&loweralpha=on&unique=on&format=plain&rnd=new uuid

#install required java package
run git clone https://github.com/res-x/resx.git
run cd resx/resx-parent && mvn install && cd ../..

#install donatr
run git clone https://github.com/vileda/donatr.git && cd donatr && mvn package

expose 8080

#add database
volume /data
run ln -s /data/donatr.db /donatr/dist/donatr.db

#run donatr
cmd cd donatr/dist && java -jar donatr-1.0-SNAPSHOT-fat.jar
