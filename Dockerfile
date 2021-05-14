FROM openjdk:8-jdk
EXPOSE 8080:8080
VOLUME "/etc/woolly/"
RUN mkdir /app
COPY ./build/install/woolly-server/ /app/
WORKDIR /app/bin
CMD ["./woolly-server"]
