#Dockerfile
FROM  maven:3.6-jdk-11

#RUN echo "deb http://archive.ubuntu.com/ubuntu bionic main universe" > /etc/apt/sources.list
RUN apt-get update && \
    apt-get install -y  --no-install-recommends apt-utils  && \
    apt-get install -yq  --no-install-recommends zip wget pwgen ca-certificates lsb-release && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*
ENV TOMCAT_MAJOR_VERSION 9
ENV TOMCAT_MINOR_VERSION 9.0.13
ENV MYSQL_REPO_VERSION 0.8.12-1_all
ENV CATALINA_HOME /tomcat

#Install Tomcat
RUN wget -q https://archive.apache.org/dist/tomcat/tomcat-${TOMCAT_MAJOR_VERSION}/v${TOMCAT_MINOR_VERSION}/bin/apache-tomcat-${TOMCAT_MINOR_VERSION}.tar.gz && \
	tar zxf apache-tomcat-*.tar.gz && \
 	rm apache-tomcat-*.tar.gz && \
 	mv apache-tomcat* tomcat

COPY src/main/resources/docker/create_tomcat_admin_user.sh /create_tomcat_admin_user.sh
RUN mkdir -p /etc/service/tomcat
COPY src/main/resources/docker/run.sh /etc/service/tomcat/run
RUN chmod +x /*.sh
RUN chmod +x /etc/service/tomcat/run

EXPOSE 8080

ARG WAR_FILE
COPY target/${WAR_FILE} /tomcat/webapps/lh-tool.war

# use credentials for docker
RUN mkdir -p /WEB-INF/classes
COPY src/main/resources/docker/credentials.properties /WEB-INF/classes/credentials.properties
RUN zip -r /tomcat/webapps/lh-tool.war /WEB-INF/classes/credentials.properties

# configure jacoco agent for coverage 
RUN unzip -j /tomcat/webapps/lh-tool.war "WEB-INF/lib/org.jacoco.agent*.jar" -d /tmp
RUN mv /tmp/org.jacoco.agent*.jar /tmp/jacoco-agent.jar
RUN mkdir /target
ENV JAVA_OPTS="-javaagent:/tmp/jacoco-agent.jar=destfile=/target/jacoco-it.exec,append=true -Duser.timezone=UTC"

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.2.1/wait /wait
RUN chmod +x /wait

## Launch the wait tool and then your application
CMD /wait && touch /target/jacoco-it.exec && chmod 666 /target/jacoco-it.exec && /tomcat/bin/catalina.sh run
