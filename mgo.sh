#!/bin/bash

# define the variables
readonly oss="oss://ic-saic"
readonly sourceDir="/saic/data"
readonly outDir="/saic/output"
readonly db="newrvm"
readonly prefixC="newrvmSignalData"

##############################

process(){
    tmYMD=$(date -d @$1 +%Y%m%d)
    tmYMD2=$(date -d @$1 +%F)

    fileTGZ=$sourceDir/${tmYMD}.tgz
    fileBSON=app/dump/$tmYMD/$db/${prefixC}${tmYMD}.bson

    echo $(date +"%F %T") - processing $tmYMD2 ...

    # step 1 oss copy
    ossutil64 -f cp $oss/saic2017mgo/${tmYMD}.tgz $fileTGZ

    # step 2 pigz -> tar -> mongorestore
    pigz -p 2 -dc $fileTGZ | tar -xOf - $fileBSON | mongorestore -d $db -c ${prefixC}${tmYMD} -j 16 --noIndexRestore --drop -vv -

    # step 3 mongodb create index
    echo $(date +"%F %T") - create index ...
    mongo $db --eval "db.${prefixC}${tmYMD}.createIndex({vin:\"hashed\"}, {background:false})"

    # step 4 java
    echo $(date +"%F %T") - transforming ...
    java -jar saic-2017-1.0.0.jar --spring.profiles.active=product \
                                  --saic2017.beginDate="$tmYMD2" --saic2017.endDate="$tmYMD2" \
                                  --saic2017.mongo.database="$db" --saic2017.mongo.collection="${prefixC}${tmYMD}" \
                                  --saic2017.dataSources[0]="mongo" --saic2017.out="$outDir" \
         > /dev/null 2>&1

    # step 5 make tar.gz file then oss copy back
    pathYM=$(date -d @$1 +%Y)/$(date -d @$1 +%m)
    pathDD=$(date -d @$1 +%d)
    fileGZ=${pathDD}.tar.gz
    tar -I pigz -C $outDir/$pathYM -cvf $outDir/$pathYM/$fileGZ $pathDD && rm -rf $outDir/$pathYM/$pathDD

    ossutil64 -f cp $outDir/$pathYM/$fileGZ ${oss}-output/mgo/$pathYM/$fileGZ

    # step 6 clear
    echo $(date +"%F %T") - clear ...
    mongo $db --eval "db.${prefixC}${tmYMD}.drop()"
    rm -f $fileTGZ
    # rm -f $outDir/$pathYM/$fileGZ
}

##############################
# reset mongodb

resetMgo(){
    # stop mongod
    ps -ef | awk '/mongod/ {if($8=="mongod"){system("mongo --eval \"db.adminCommand({shutdown:1})\" ")}}'
    # force stop mongod
    ps -ef | awk '/mongod/ {if($8=="mongod"){system("kill -9 " $2)}}'

    # clear all mongodb running files
    rm -rf /saic/mongodb/var/run/* /saic/mongodb/var/log/* /saic/mongodb/var/log/*

    # restart mongodb
    mongod --config /saic/mongodb/mongod.conf
}

##############################

readonly task_file="task"
readonly xend="END"


echo "$(date +"%F %T") - SAIC to GB32960 : Start."

echo "reset mongodb"
resetMgo
# clear *.tgz *.tgz.temp
rm -rf $sourceDir/*.tgz $sourceDir/*.tgz.temp


i=1
while :
do
    tm=$(head -$i $task_file | tail -1)
    if [ "$tm" == "$xend" ]; then
        break
    fi

    if [[ ${tm} != *"done"* ]]; then

        tmK=$(date -d "$tm" +%s)

        sed -i "${i}s/$/ ... /" $task_file
        process $tmK
        sed -i "${i}s/$/done./" $task_file
    fi

    i=$((i+1))
done
echo "$(date +"%F %T") - SAIC to GB32960 : Finished."