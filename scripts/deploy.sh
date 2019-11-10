#!/bin/bash
ls -l
pwd
cat src/main/resources/docker/run.sh
if [ "${1,,}" = "stage" ]; then
    echo "Executing stage deployment..."
    env="stage"
    path="stage"
elif [ "${1,,}" = "prod" ]; then
    echo "Executing prod deployment..."
    env="prod"
    path=""
else
    echo "Fail! Please pass stage or prod as argument"
fi

war_file="$(ls -t target/*.war | head -1)"

ssh_key="$(mktemp)"
chmod 0600 $ssh_key
echo "-----BEGIN RSA PRIVATE KEY-----" > $ssh_key
echo $DEPLOY_KEY >> $ssh_key
echo "-----END RSA PRIVATE KEY-----" >> $ssh_key

scp -P $DEPLOY_PORT -i $ssh_key $war_file $DEPLOY_USER@$DEPLOY_HOST:$DEPLOY_WAR_PATH/tmp.war

wget -qO- "https://travis:$DEPLOY_PW@$DEPLOY_TARGET/manager/text/undeploy?path=/$path"

wget -qO- "https://travis:$DEPLOY_PW@$DEPLOY_TARGET/manager/text/deploy?path=/$path&war=file:$DEPLOY_WAR_PATH/tmp.war"

echo "" > $ssh_key
rm $ssh_key
