#!/bin/bash
if [ "${1,,}" = "stage" ]; then
    echo "Executing stage deployment..."
   #env must never be "test" bc it gets checked in testonly controllers
    env="stage"
    path="stage"
    db_url=$CREDENTIALS_STAGE_DB_URL
    jwt_secret=$CREDENTIALS_STAGE_JWT_SECRET
    base=$CREDENTIALS_STAGE_BASE
    
    mv src/main/js/settings.stage.js src/main/js/settings.js

elif [ "${1,,}" = "prod" ]; then
    echo "Executing prod deployment..."
   #env must never be "test" bc it gets checked in testonly controllers
    env="prod"
    path=""
    db_url=$CREDENTIALS_PROD_DB_URL
    jwt_secret=$CREDENTIALS_PROD_JWT_SECRET
    base=$CREDENTIALS_PROD_BASE
    
    mv src/main/js/settings.prod.js src/main/js/settings.js
    
else
    echo "Fail! Please pass stage or prod as argument"
    exit 1
fi

echo "jdbc.url=$db_url" > src/main/resources/credentials.properties
echo "jdbc.username=$CREDENTIALS_DB_USERNAME" >> src/main/resources/credentials.properties
echo "jdbc.password=$CREDENTIALS_DB_PW" >> src/main/resources/credentials.properties

echo "app.jwtSecret=$jwt_secret" >> src/main/resources/credentials.properties
echo "app.jwtExpirationInMs=$CREDENTIALS_JWT_EXPIRATION" >> src/main/resources/credentials.properties
echo "app.base=$base" >> src/main/resources/credentials.properties
echo "app.environment=$env" >> src/main/resources/credentials.properties

echo "mail.smtp.host=$CREDENTIALS_SMTP_HOST" >> src/main/resources/credentials.properties
echo "mail.smtp.password=$CREDENTIALS_SMTP_PW" >> src/main/resources/credentials.properties
echo "mail.smtp.username=$CREDENTIALS_SMTP_USER" >> src/main/resources/credentials.properties
echo "mail.smtp.tlsEnabled=true" >> src/main/resources/credentials.properties

#  ._.                                   .__  __                                  .__  __  .__              .__    ._.
#  | |   ______ ____   ____  __ _________|__|/  |_ ___.__.             ___________|__|/  |_|__| ____ _____  |  |   | |
#  | |  /  ___// __ \_/ ___\|  |  \_  __ \  \   __<   |  |   ______  _/ ___\_  __ \  \   __\  |/ ___\\__  \ |  |   | |
#   \|  \___ \\  ___/\  \___|  |  /|  | \/  ||  |  \___  |  /_____/  \  \___|  | \/  ||  | |  \  \___ / __ \|  |__  \|
#   __ /____  >\___  >\___  >____/ |__|  |__||__|  / ____|            \___  >__|  |__||__| |__|\___  >____  /____/  __
#   \/      \/     \/     \/                       \/                     \/                       \/     \/        \/
# NEVER DEPLOY REST-CONTROLLERS IN THE TESTONLY-PACKAGE!
mvn clean compile war:war "-Dwar-exclude=**/testonly/**"
war_file="$(ls -t target/*.war | head -1)"

ssh_key="$(mktemp)"
chmod 0600 $ssh_key
echo "-----BEGIN RSA PRIVATE KEY-----" > $ssh_key
echo $DEPLOY_KEY >> $ssh_key
echo "-----END RSA PRIVATE KEY-----" >> $ssh_key

scp -q -P $DEPLOY_PORT -i $ssh_key -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null $war_file $DEPLOY_USER@$DEPLOY_HOST:$DEPLOY_WAR_PATH/tmp.war

wget -qO- "https://travis:$DEPLOY_PW@$DEPLOY_TARGET/manager/text/undeploy?path=/$path"

wget -qO- "https://travis:$DEPLOY_PW@$DEPLOY_TARGET/manager/text/deploy?path=/$path&war=file:$DEPLOY_WAR_PATH/tmp.war"

echo "" > $ssh_key
rm $ssh_key