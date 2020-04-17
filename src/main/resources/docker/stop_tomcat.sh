#export COMPOSE_INTERACTIVE_NO_CLI=1
#docker exec -it tomcat /bin/bash -c "/tomcat/bin/catalina.sh stop"
wget -t 1 http://localhost:8080/lh-tool/rest/testonly/integration/shutdown
exit 0
