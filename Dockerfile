# Dockerfile

FROM openjdk:latest
MAINTAINER  HSL
VOLUME ["/csv"]
COPY target/batch-processing-0.0.1-SNAPSHOT.jar batch-processor.jar
CMD ["java", "-jar", "batch-processor.jar"]

