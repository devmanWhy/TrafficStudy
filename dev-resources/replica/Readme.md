# 진행 순서

## Replica DB Container 생성하기

compose 파일에 설정되어있습니다.

## Primary 에 replication 계정 생성

```sql
SET SQL_LOG_BIN = 0;

DROP USER IF EXISTS 'study_replica'@'%';
CREATE USER 'study_replica'@'%' IDENTIFIED BY '<REPL_PASSWORD>';
GRANT REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'study_replica'@'%';

SET SQL_LOG_BIN = 1;
FLUSH PRIVILEGES;
```

만든 계정 혹인
```sql
SELECT user, host FROM mysql.user;
SHOW GRANTS FOR '<REPL_USER>'@'%';
```

```
GRANT REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO '<REPL_USER>'@'%';
```

## DUMP 만들기

`dump.sh` 참고해서 dump 파일 가져오기
만들어진 sql 파일 `root` 계정으로 replica DB 에서 실행하기

## Primary/Secondary GTID 상태 확인

```sql
SHOW
VARIABLES LIKE 'gtid_mode';
SHOW
VARIABLES LIKE 'enforce_gtid_consistency';
SHOW
VARIABLES LIKE 'log_bin';
SHOW
VARIABLES LIKE 'server_id';
SHOW
VARIABLES LIKE 'binlog_format';
```

Primary

```
gtid_mode=ON
enforce_gtid_consistency=ON
log_bin=ON
server_id=1
binlog_format=ROW
```

Secondary

```sql
SHOW
VARIABLES LIKE 'gtid_mode';
SHOW
VARIABLES LIKE 'enforce_gtid_consistency';
SHOW
VARIABLES LIKE 'log_bin';
SHOW
VARIABLES LIKE 'server_id';
SHOW
VARIABLES LIKE 'read_only';
SHOW
VARIABLES LIKE 'super_read_only';
```

```
gtid_mode=ON
enforce_gtid_consistency=ON
log_bin=ON
server_id=2
read_only=ON
super_read_only=ON
```

## Secondary에서 CHANGE REPLICATION SOURCE TO 실행
```sql
STOP REPLICA;
RESET REPLICA ALL;

CHANGE REPLICATION SOURCE TO
  SOURCE_HOST = 'mysql',
  SOURCE_PORT = 3306,
  SOURCE_USER = 'repl',
  SOURCE_PASSWORD = '<REPL_PASSWORD>',
  SOURCE_AUTO_POSITION = 1;
```
CHANGE REPLICATION SOURCE TO
SOURCE_HOST = 'mysql',
SOURCE_PORT = 3306,
SOURCE_USER = 'study_replica',
SOURCE_PASSWORD = 'traffic',
SOURCE_AUTO_POSITION = 1;
## Replica 시작
```sql
START REPLICA;
```

## REPLICA STATUS 확인
```sql
SHOW REPLICA STATUS\G
```

아래 값이 둘다 Yes 임을 확인
```
Replica_IO_Running: Yes
Replica_SQL_Running: Yes
```


## 빠른 진행 (세팅 시 실행한 SQL 정리)
```sql

# Primary 에서 ROOT 권한으로 실행
SET SQL_LOG_BIN = 0;

DROP USER IF EXISTS 'study_replica'@'%';
CREATE USER 'study_replica'@'%' IDENTIFIED BY 'traffic';
GRANT REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'study_replica'@'%';

SET SQL_LOG_BIN = 1;
FLUSH PRIVILEGES;

# DUMP 파일 Secondary 에 실행

# Secondary 에서 ROOT 권한으로 실행
SET GLOBAL read_only = OFF;
SET GLOBAL super_read_only = OFF;
STOP REPLICA ;
RESET REPLICA ALL;
CHANGE REPLICATION SOURCE TO
    SOURCE_HOST = 'mysql',
    SOURCE_PORT = 3306,
    SOURCE_USER = 'study_replica',
    SOURCE_PASSWORD = 'traffic',
    GET_SOURCE_PUBLIC_KEY = 1,
    SOURCE_AUTO_POSITION = 1;

START REPLICA;
SET GLOBAL read_only = ON;
SET GLOBAL super_read_only = ON;

SHOW REPLICA STATUS\G;
SELECT
    worker_id,
    last_error_number,
    last_error_message,
    last_applied_transaction
FROM performance_schema.replication_applier_status_by_worker;


```
