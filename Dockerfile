FROM openjdk:11
EXPOSE 8080
MAINTAINER joshua christian
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]