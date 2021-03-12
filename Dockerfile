FROM openjdk:11-jre-alpine

ADD target/scala-2.13/users-assembly-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","/app.jar"]
