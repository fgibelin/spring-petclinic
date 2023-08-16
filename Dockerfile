FROM openjdk:17-slim
WORKDIR /app
ARG petclinicArtifact
COPY $petclinicArtifact spring-petclinic.jar

ENTRYPOINT ["java", "-jar", "spring-petclinic.jar"]
