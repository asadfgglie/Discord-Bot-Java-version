FROM openjdk:11

MAINTAINER asadfgglie

RUN mkdir /bot

ADD ./*.jar /bot/

ADD ./config /bot/config/

ADD ./script /bot/script/

ADD ./data /bot/data/

CMD java -Dfile.encoding=UTF-8 -jar /bot/DiscordBot-all.jar --configpath=./config --scriptpath=./script