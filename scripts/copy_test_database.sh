#!/bin/bash
# prints sql statements of current data in the database that can be pasted into the ManualTestInitializer
# usage: check $DB and execute in project folder
PROPERTY_FILE=src/main/resources/credentials.properties

function getProperty {
    PROPERTY_KEY=$1
    PROPERTY_VALUE=`cat $PROPERTY_FILE | grep "$PROPERTY_KEY" | cut -d'=' -f2`
    echo $PROPERTY_VALUE
}

DB_URL=$(getProperty "jdbc.url")
#TODO: get database from url
DB="lhtool"
DB_USER=$(getProperty "jdbc.username")
export MYSQL_PWD=$(getProperty "jdbc.password")
DUMP=$(mysqldump -u $DB_USER --no-create-info --no-create-db --no-set-names --no-tablespaces --skip-add-locks --skip-comments --skip-disable-keys --ignore-table "$DB.item_image" --ignore-table "$DB.schema_version" --ignore-table "$DB.user" --ignore-table "$DB.user_role" --complete-insert $DB | grep "INSERT INTO")
readarray -t INSERTS <<< "$DUMP"
TABLES=" user user_role  "
RESULT=( )
UNSATISFIED_INSERTS=( )
while (( ${#INSERTS[@]} )); do
    for INSERT in "${INSERTS[@]}"; do
        if [[ $INSERT =~ (INSERT INTO )\`([a-zA-Z0-9_]+)\` ]]; then 
            TABLE=${BASH_REMATCH[2]};
            DEPENDENCIES=($(echo $INSERT | grep -oP '\K[a-z_]+(?=\d*_id`)'))
            SATISFIED=true
            for DEPENDENCY in "${DEPENDENCIES[@]}"; do
                if [[ $DEPENDENCY ]]; then
                    if [[ ! $TABLES =~ " $DEPENDENCY " ]]; then
                        SATISFIED=false
                        break
                    fi
                fi
            done
            
            if [[ "$SATISFIED" = true ]]; then
                TABLES="$TABLES$TABLE "
                RESULT+=( "\"$(echo $INSERT | sed 's/\\"/\\\\\\"/g')\"" )
            else
                UNSATISFIED_INSERTS+=( "$INSERT" )
            fi
        fi
    done
    
    INSERTS=( "${UNSATISFIED_INSERTS[@]}" )
    UNSATISFIED_INSERTS=( )
    #echo $INSERTS
done
echo $(IFS=, ; echo "${RESULT[*]}")
