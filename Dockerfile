FROM openjdk:11
COPY ./target/salicouture.jar /usr/src/
WORKDIR /usr/src/
EXPOSE 8080
CMD ["java","-jar", "salicouture.jar"]
