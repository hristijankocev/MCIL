FROM openjdk:11
RUN apt-get update -y
RUN apt-get install -y libglib2.0-0
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} mcil-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar", "/mcil-0.0.1-SNAPSHOT.jar"]