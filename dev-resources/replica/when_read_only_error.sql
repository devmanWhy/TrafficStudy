-- root 로 replica 접속
SET GLOBAL super_read_only = OFF; -- 덤프 실행 후 ON
SET GLOBAL read_only = OFF; -- 덤프 실행 후 ON
