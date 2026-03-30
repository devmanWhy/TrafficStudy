-- 1. 사용자 생성 (비밀번호는 'traffic'으로 설정, 필요시 변경 가능)
CREATE USER 'study_cdc'@'%' IDENTIFIED BY 'traffic';

-- 2. 필수 권한 부여
-- SELECT: 테이블 데이터를 읽기 위해 필요
-- RELOAD: FLUSH TABLES 구문을 실행하기 위해 필요 (스냅샷 시점 고정)
-- SHOW DATABASES: 데이터베이스 목록을 조회하기 위해 필요
-- REPLICATION SLAVE: Binlog 이벤트를 읽기 위한 핵심 권한
-- REPLICATION CLIENT: 복제 상태를 모니터링하기 위해 필요
GRANT SELECT, RELOAD, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'study_cdc'@'%';

-- 3. 권한 적용
FLUSH PRIVILEGES;