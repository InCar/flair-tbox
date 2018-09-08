#!/bin/bash

# define the begin and end date
readonly strBegin="2017-09-01"
readonly strEnd="2017-09-03"

readonly sourceDir="/saic/data"
readonly db="newrvm"
readonly prefixC="newrvmSignalData"

##############################
echo "$(date +"%F %T") - SAIC to GB32960 : $strBegin -> $strEnd ..."

readonly beginTM=$(date -d "$strBegin" +%s)
readonly endTM=$(date -d "$strEnd" +%s)
readonly totalDays=$(((endTM-beginTM)/86400))

for((i=0; i<=totalDays; i++))
do
    tmMark=$((beginTM+86400*i))
    tmYMD=$(date -d @$tmMark +%Y%m%d)
    tmYMD2=$(date -d @$tmMark +%F)

    # step 1 pigz -> tar -> mongorestore
    echo $(date +"%F %T") - processing $tmYMD2 ...
    fileTGZ=$sourceDir/${tmYMD}.tgz
    fileBSON=app/dump/$tmYMD/$db/${prefixC}${tmYMD}.bson

    pigz -p 2 -dc $fileTGZ | tar -xOf - $fileBSON | mongorestore -d $db -c ${prefixC}${tmYMD} -j 16 --noIndexRestore -vv -

    # step 2 mongodb create index
    echo $(date +"%F %T") - create index ...
    mongo $db --eval "db.${prefixC}${tmYMD}.createIndex({vin:\"hashed\"}, {background:false})"

    # step 3 java
    echo $(date +"%F %T") - transforming ...
    java -jar saic-2017-1.0.0.jar --spring.profiles.active=product \
                                  --saic2017.beginDate="$tmYMD2" --saic2017.endDate="$tmYMD2" \
                                  --saic2017.mongo.database="$db" --saic2017.mongo.collection="${prefixC}${tmYMD}" \
         > /dev/null 2>&1

    # step 4 mongodb drop
    echo $(date +"%F %T") - clear ...
    mongo $db --eval "db.${prefixC}${tmYMD}.drop()"

done

echo "$(date +"%F %T") - SAIC to GB32960 : Finished."