# 실습 프로젝트
## 기존 프로젝트를 개선해서 외부 상황에 영향을 받지 않는 굳건한 서비스로 만들어보자 
- Tomcat Config => 스레드 제한을 통해 시뮬레이션 상태 구축
- 나에게로 들어오는 트래픽 => RateLimiter
- 내가 의존하는 서비스 => Timeouts, CircuitBreaker
- 비동기메시징 => Command 성 로직을 나에게서 분리