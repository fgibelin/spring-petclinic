FROM openjdk:17-jdk-alpine3.13

ARG ARTIFACTNAME
RUN test -n "${ARTIFACTNAME}" || ( echo "Missing argument: --build-arg ARTIFACTNAME=<artifact>" && false )

COPY ${ARTIFACTNAME} /spring-petclinic.jar

CMD ["java", "-jar", "/spring-petclinic.jar"]
