FROM gcc:10.2

RUN apt-get update -y

COPY ./dockerSys /usr/dockerSys

WORKDIR /usr/dockerSys

RUN chmod 777 run.sh

ENTRYPOINT "./run.sh"
