#Dockerfile
FROM  maven:3.6-jdk-11
MAINTAINER  Jannes Kretschmer jannes.kretschmer@gmx.de

RUN echo "deb http://archive.ubuntu.com/ubuntu bionic main universe" > /etc/apt/sources.list
RUN apt-get update && \
    apt-get install -y  --allow-unauthenticated --no-install-recommends apt-utils  && \
    apt-get install -yq  --allow-unauthenticated --no-install-recommends wget pwgen ca-certificates lsb-release && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*
ENV TOMCAT_MAJOR_VERSION 9
ENV TOMCAT_MINOR_VERSION 9.0.13
ENV MYSQL_REPO_VERSION 0.8.12-1_all
ENV CATALINA_HOME /tomcat

#Install Mysql#
RUN wget https://dev.mysql.com/get/mysql-apt-config_${MYSQL_REPO_VERSION}.deb
RUN dpkg -i mysql-apt-config_${MYSQL_REPO_VERSION}.deb
RUN apt-get update
#RUN echo 'mysql-server mysql-server/root_password password root' | debconf-set-selections
#RUN echo 'mysql-server mysql-server/root_password_again password root' | debconf-set-selections
RUN apt-get install -y --allow-unauthenticated mysql-client

#RUN echo "[root]\nuser = root\npassword = root" > ~/.my.cnf
#RUN service mysql restart
#RUN service mysql status
#RUN find / -type s
#RUN echo "SELECT VERSION()" | mysql -u root
#sudo docker run -it -p 8080:8080 -v "$PWD":/app demo/spring:maven-3.6-jdk-11 bash -c "cp /app/lh-tool-0.0.1-SNAPSHOT.war /tomcat/webapps/ & /tomcat/bin/catalina.sh run"
#sudo docker run -it -p 8080:8080 -v "$PWD":/app demo/spring:maven-3.6-jdk-11 bash -c "sudo find / -type s"

#Install Tomcat
RUN wget -q https://archive.apache.org/dist/tomcat/tomcat-${TOMCAT_MAJOR_VERSION}/v${TOMCAT_MINOR_VERSION}/bin/apache-tomcat-${TOMCAT_MINOR_VERSION}.tar.gz && \
	tar zxf apache-tomcat-*.tar.gz && \
 	rm apache-tomcat-*.tar.gz && \
 	mv apache-tomcat* tomcat

ADD create_tomcat_admin_user.sh /create_tomcat_admin_user.sh
RUN mkdir -p /etc/service/tomcat
ADD run.sh /etc/service/tomcat/run
RUN chmod +x /*.sh
RUN chmod +x /etc/service/tomcat/run

EXPOSE 8080

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.2.1/wait /wait
RUN chmod +x /wait

## Launch the wait tool and then your application
CMD /wait
