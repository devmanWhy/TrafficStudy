# docker exec {컨테이너명} sh -c 'exec mysqldump -u root -p{패스워드} --single-transaction --routines --triggers --events --set-gtid-purged=ON --databases traffic_study' > traffic_study_dump.sql
docker exec b8361232cd8d sh -c 'exec mysqldump -u root -ptraffic --single-transaction --routines --triggers --events --set-gtid-purged=ON --databases traffic_study' > traffic_study_dump.sql
