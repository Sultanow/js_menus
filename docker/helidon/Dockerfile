# Needs to be build from root in order to copy the parent pom.xml
# (1) docker build -f docker/helidon/Dockerfile -t hackathon-helidon-mp:v1 .
# (2) docker run --rm --name hackathon-helidon-mp -p 8080:8080 hackathon-helidon-mp:v1

FROM maven:3.6.1-jdk-12 as build
WORKDIR /
# Create a first layer to cache the "Maven World" in the local repository.
# Incremental docker builds will always resume after that, unless you update
# the pom
ADD pom.xml .
ADD /helidon/pom.xml /helidon/pom.xml
RUN mvn package -DskipTests

# Do the Maven build!
# Incremental docker builds will resume here when you change sources
ADD /helidon/src /helidon/src
RUN mvn package -DskipTests
RUN echo "done!"

# 2nd stage, build the runtime image
FROM openjdk:12
WORKDIR /opt/helidon

# Copy the binary built in the 1st stage
COPY --from=build /helidon/target/helidon-1.1.1.jar ./
COPY --from=build /helidon/target/libs ./libs

CMD ["java", "-jar", "helidon-1.1.1.jar"]

EXPOSE 8080