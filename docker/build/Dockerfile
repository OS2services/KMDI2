# compile source
FROM maven:3-jdk-11 as build

WORKDIR /workspace/app

# copy in order least likely to be modified to optimize build speed
COPY build/pom.xml pom.xml

# build dependencies in separate layer (for caching purposes)
RUN mvn dependency:go-offline -B

# copy source (this layer is rebuild everytime there are changes to code)
COPY build/src src

# actually compile
RUN mvn package -DskipTests

# now build deployment image
FROM openjdk:11

ARG TARGET=/workspace/app/target
COPY --from=build ${TARGET}/kmd-i2-integration.jar .

EXPOSE 8997

ENTRYPOINT ["java", "-Dloader.path=config/*", "-Xmx256m", "-jar", "kmd-i2-integration.jar"]
