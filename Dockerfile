FROM tomcat:latest
MAINTAINER Thiago Herrera

COPY ./target/rest.war /usr/local/tomcat/webapps/

EXPOSE 8080
CMD ["catalina.sh", "run"]