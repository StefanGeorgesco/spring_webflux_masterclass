FROM bellsoft/liberica-openjdk-alpine:21
WORKDIR app
ADD https://github.com/vinsguru/spring-webflux-course/raw/master/01-external-services/external-services.jar external-services.jar
CMD java -jar external-services.jar