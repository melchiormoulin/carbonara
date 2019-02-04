FROM hseeberger/scala-sbt:latest as builder
# Cache dependencies
COPY build.sbt /carbonara/build.sbt
COPY project /carbonara/project
WORKDIR /carbonara
RUN sbt relay/update
# Build project
COPY relay /carbonara/relay
RUN sbt relay/assembly

FROM openjdk:8u181
WORKDIR /carbonara
EXPOSE 7080
COPY --from=builder /carbonara/relay/target/scala-2.12/relay-assembly-*.jar /carbonara/carbonara-relay.jar
ENTRYPOINT ["java", "-jar", "/carbonara/carbonara-relay.jar", "--metrics-port",  "7080"]
