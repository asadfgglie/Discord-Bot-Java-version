#!/usr/bin/env sh

sudo docker stop discordbot

sudo docker container rm discordbot

sudo docker image rm discordbot:java11

sudo docker build -t discordbot:java11 ./ --no-cache

sudo docker run -d -v /home/username/App/config:/bot/config -v /home/username/App/script:/bot/script -v /home/username/App/data:/bot/data --name discordbot discordbot:java11