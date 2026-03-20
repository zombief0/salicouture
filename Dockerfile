FROM eclipse-temurin:25-alpine
RUN mkdir /springconfig
COPY ./target/salicouture.jar /usr/src/
WORKDIR /usr/src/
EXPOSE 8080
CMD ["java","-jar", "couture.jar", "--spring.config.import=/springconfig/"]
