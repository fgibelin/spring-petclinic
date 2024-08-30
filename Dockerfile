#FROM soleng.jfrog.io/fg-docker/openjdk:17-slim
FROM openjdk:17-slim
WORKDIR /app
ARG petclinicArtifact
COPY $petclinicArtifact spring-petclinic.jar

ENTRYPOINT ["java", "-jar", "spring-petclinic.jar"]
