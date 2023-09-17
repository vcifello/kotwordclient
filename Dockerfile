ARG VERSION=17

#FROM amazonlinux:2 as BASE
#
##RUN sed -i'.bak' 's/$/ contrib/' /etc/apt/sources.list
#RUN apt-get update#; apt-get install -y ttf-mscorefonts-installer fontconfig

FROM amazoncorretto:${VERSION}

COPY build/libs/kotwordclient-1.0-SNAPSHOT-all.jar /bin/runner/run.jar
COPY DockerFonts/Fonts /usr/share/fonts/myfonts
COPY DockerFonts/.pdfbox.cache /root/.pdfbox.cache
WORKDIR /bin/runner

CMD ["java","-jar","run.jar"]