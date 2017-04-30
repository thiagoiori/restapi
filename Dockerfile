FROM tomcat:latest
MAINTAINER Thiago Herrera

ENV MYSQL_URL="127.0.0.1"
ENV MYSQL_PORT="3306"
ENV MYSQL_USER="restapiuser"
ENV MYSQL_PASSWORD="DedamRestAPIDB"

COPY ./target/rest.war /usr/local/tomcat/webapps/

EXPOSE 8080
CMD ["catalina.sh", "run"]