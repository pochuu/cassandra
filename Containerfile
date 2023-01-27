FROM maven:3.6.0-jdk-11-slim
COPY src /home/app/src
COPY pom.xml /home/app
ENV user_id x
WORKDIR /home/app
RUN mvn clean install package
CMD ["sh","-c","mvn exec:java -Dexec.mainClass=org.example.Main -Duser_id=$user_id"]
