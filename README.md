# **🟦 BrandPilot - AI 브랜드 컨설팅 서비스**
BrandPilot은 AI를 활용해 네이밍, 컨셉, 스토리, 로고를 단계적으로 생성하는 브랜드 컨설팅 플랫폼입니다.
스타트업 및 창업자를 대상으로 **브랜드 설계와 투자 게시판 기능**을 함께 제공하여, 브랜드 기획부터 투자 연결까지 지원하는 서비스를 구현했습니다. 

--- 

## **🛠️ Tech Stack**
| **Category** | **Technology** |
| --- | --- |
| Language | Java 21, Python |
| Framework | Spring Boot 3.5.9 |
| Token | Spring Security (JWT) |
| Database | MySQL (AWS RDS) |
| Infra | AWS EC2, Docker, Docker Compose, Nginx |
| Communication | REST API |
| AI Server | FastAPI, LangGraph, OpenAI API |
| CI/CD | GitHub Actions |

--- 

## **📖 주요 기능**
### **1️⃣ 회원 관리**

- JWT 기반 인증/인가 처리
- 사용자별 브랜드 생성 및 관리

### **2️⃣ 인터뷰 기반 브랜드 진단**

- 사용자 입력을 기반으로 브랜드 방향성 분석
- 핵심 키워드 및 타겟 페르소나 도출

### **3️⃣ AI 브랜드 컨설팅**

- **네이밍 추천**: 사용자 니즈와 브랜드 방향성에 맞는 이름 후보 생성
- **컨셉 설계**: 선택된 네이밍과 인터뷰 내용을 반영해 브랜드 컨셉 도출
- **스토리 구성**: 누적된 Context를 기반으로 브랜드 서사와 메시지 구체화
- **로고 생성**: 앞선 단계에서 정리된 브랜드 정체성을 시각화한 로고 후보 생성

### **4️⃣  투자 게시판**

- 생성된 브랜드를 기반으로 투자/홍보 게시글 작성
- 사용자 간 브랜드 공유 및 투자 연결 지원

--- 

## **🗂️ ERD 설계**
<img width="878" height="502" alt="스크린샷 2026-03-27 오전 11 26 01" src="https://github.com/user-attachments/assets/1f3e47d2-4c8f-4c4a-ad9f-30a08278ac13" />

- 회원: 사용자 정보
- 브랜드: 사용자가 컨설팅받아 만들어낸 브랜드
- 인터뷰 리포트: 사용자가 최초 브랜드 컨설팅을 시작하고 만든 브랜드 방향성
- 브랜딩 결과물: 사용자가 컨설팅 단계에서 선택한 결과물을 저장
- State 저장소: 브랜드 컨설팅 일관성을 유지하기 위한 AI가 추출한 Context를 저장

--- 

## **🧠 기술 블로그 기반 주요 고민 & 해결 전략**
### **Stateless AI 아키텍처의 한계 극복을 위한 'Context Versioning' 설계**

- **Problem**
    - LLM 기반 AI 서버의 Stateless 특성으로 인해 단계별(인터뷰~로고) 진행 시 이전 맥락(Context)이 유실되어 결과물의 일관성이 파괴되는 문제 발생.
- **Attempt**
    - 상태 관리 주체에 따른 3가지 아키텍처(Client-side, AI-side, BE-centralized)의 트레이드오프 분석.
    - 네트워크 오버헤드 최소화 및 데이터 정합성 보장을 위해 **백엔드 중심의 상태 관리 구조** 선택.
- **Action**
    - **Versioning 기반 State Context 저장 시스템** 구축: AI 응답에서 필수 문맥만 추출하여 RDB에 저장하고, 차기 단계 호출 시 이를 주입하는 브릿지 로직 구현.
    - 최신 문맥 조회를 최적화하기 위해 **(brand_id, step, is_active) 복합 인덱스** 설계 및 조회 성능 확보.
    - @Modifying 쿼리를 통한 상태 일괄 업데이트로 상태 전이의 **원자성(Atomicity)** 보장.
- **Result**
    - 서버 장애나 재시작 상황에서도 **100% 서비스 복구 능력** 확보 및 AI 서버와 백엔드 간 명확한 관심사 분리(SoC) 달성.

**[[기술블로그 - Stateless 서버 일관성 해결](https://velog.io/@jhj9903/Stateless-AI-%EC%84%9C%EB%B2%84%EC%97%90%EC%84%9C-%EC%9D%BC%EA%B4%80%EC%84%B1%EC%9D%84-%EC%9C%A0%EC%A7%80%ED%95%9C-%EB%B0%A9%EB%B2%95)]**

### **API 구간별 프로파일링을 통한 성능 병목 지점 특정 및 최적화 실험**

- **Problem**
    - 단계 진행에 따른 응답 시간의 선형적 증가로 UX 저하 및 사용자 이탈 위기 발생.
- **Attempt**
    - `System.currentTimeMillis()`를 활용한 **구간별 커스텀 프로파일링 로깅** 적용 및 분석 결과 지연의 99%가 AI 추론 구간임을 확인.
    - "Payload 크기가 추론 속도의 핵심 병목"이라는 가설 하에 데이터 46.3% 축소 실험 수행.
- **Action**
    - 실험 결과 응답 시간이 오히려 증가함을 확인하며, 병목의 본질이 전송량이 아닌 'AI 모델의 토큰 생성 비용 및 추론 복잡도'에 있음을 데이터로 증명.
- **Result**
    - 무분별한 데이터 축소 대신, **WebClient의 비동기/논블로킹 특성**을 활용한 스레드 효율화 및 사용자 대기 UX 개선으로 전략 수정하여 시스템 안정성 제고.

[[기술블로그 - AI API 병목 분석 및 payload 축소 실험](https://velog.io/@jhj9903/%EB%8B%A8%EA%B3%84%ED%98%95-AI-%EC%84%9C%EB%B9%84%EC%8A%A4%EC%97%90%EC%84%9C-%EC%9D%91%EB%8B%B5-%EC%86%8D%EB%8F%84%EA%B0%80-%EB%8A%90%EB%A6%B0-%EC%9D%B4%EC%9C%A0%EA%B0%80-%EB%AD%98%EA%B9%8C)]

### **FSM 도입을 통한 서비스 무결성 및 리소스 관리 최적화**

- **Problem**
    - 비정상적인 URL 접근이나 단계 건너뛰기 요청 시 데이터 정합성이 파손되는 보안 취약점 및 S3 고아 객체(Orphan Object) 발생.
- **Action**
    - **FSM(유한 상태 머신) 설계**: Brand 엔티티 내에 명시적인 상태 전이 메서드를 구현하여 비즈니스 로직 캡슐화 및 서비스 레이어 방어 로직 구축.
    - **조회 최적화**: 마이페이지 조회 시 발생하는 **N+1 문제를 방지**하기 위해 findByBrandIn (In-query) 기반의 Memory Aggregation 전략 적용.
    - **리소스 정리**: 브랜드 삭제 시 S3 스토리지 내 이미지까지 연쇄 삭제하는 **Cleanup 파이프라인**을 구축하여 데이터 정합성 유지 및 클라우드 비용 절감.
- **Result**
    - 비정상 접근 시도를 애플리케이션 레이어에서 **100% 차단**하고, 대량 데이터 조회 성능 및 인프라 비용 효율성을 동시에 확보.

[[기술블로그 - 상태 관리 & 무결성 설계](https://velog.io/@jhj9903/%EB%8B%A8%EA%B3%84%ED%98%95-%EC%84%9C%EB%B9%84%EC%8A%A4%EC%97%90%EC%84%9C-%EC%83%81%ED%83%9C-%EB%AC%B4%EA%B2%B0%EC%84%B1%EC%9D%84-%EB%B3%B4%EC%9E%A5%ED%95%98%EB%8A%94-%EB%B0%A9%EB%B2%95currentstep-%EC%84%A4%EA%B3%84)]

### **JWT 기반의 Stateless 인증 및 보안 강화**

- **Problem**
    - 서비스 확장성을 위해 세션 대신 토큰 기반 인증이 필요했으며, 사용자 민감 정보(비밀번호)의 안전한 보호가 요구됨.
- **Action**
    - **JWT(JSON Web Token) 아키텍처** 도입: JwtProvider를 통해 토큰 발급 및 검증 로직을 구현하고, OncePerRequestFilter를 상속받은 JwtAuthenticationFilter로 모든 요청에 대한 인증을 전역적으로 제어.
    - **BCrypt 암호화**: BCryptPasswordEncoder를 적용하여 데이터베이스 내 비밀번호를 안전하게 해싱 처리.
- **Result**
    - 서버 부하를 최소화하는 **Stateless 인증 환경** 구축 및 보안 가이드라인 준수.
    - `@AuthenticationPrincipal`을 활용해 서비스 레이어와 보안 레이어를 깔끔하게 분리하여 코드 가독성 증대.

### **외부 API 연동의 유연성 확보를 위한 클라이언트 추상화**

- **Problem**
    - AI 서버(FastAPI) 개발 속도와 백엔드 개발 속도의 차이로 인해, 외부 API 연동 없이도 독립적인 로직 테스트가 가능한 환경이 필요함.
- **Action**
    - **Interface 기반 추상화**: AiClient 인터페이스를 정의하고, 실제 연동용 AiClientImpl과 테스트용 AiClientMock으로 구현을 분리하여 환경에 따른 유연한 교체 가능 구조 설계.
    - **WebClient 활용**: RestTemplate 대비 현대적이고 비동기 확장이 용이한 WebClient를 선택하여 외부 통신 모듈 구축.
- **Result**
    - AI 서버 장애나 미완성 상태에서도 백엔드 비즈니스 로직을 완벽히 테스트할 수 있는 **테스트 친화적(Testable) 환경** 구축.
    - 향후 비동기(Async) 처리로의 전환이 용이한 기술적 부채 최소화.

### **Docker Orchestration 및 이미지 기반 CI/CD 자동화 구축**

- **Problem**
    - Spring Boot, FastAPI, React 등 상이한 실행 환경의 의존성 관리 복잡도 및 수동 배포로 인한 서버 오염 가능성 존재.
- **Attempt**
    - 소스 코드 직접 배포 방식 대비 실행 환경 고정과 롤백이 용이한 **이미지 기반 배포(Docker)** 전략 선택.
- **Action**
    - **Docker Compose 멀티 컨테이너 통합**: 3개 서비스를 하나의 가상 네트워크로 묶고, 컨테이너 이름 기반의 **내부 DNS 통신** 구조를 설계하여 서비스 간 연결 복잡도 해소.
    - **GitHub Actions CI/CD 구축**: 코드 Push 시 Docker 이미지를 자동 빌드/푸시하고, EC2에서 최신 이미지를 Pull하여 재기동하는 배포 파이프라인 자동화.
- **Result**
    - 서버 환경 의존성을 완벽히 제거하고, 배포 실수 가능성을 차단하여 운영 서버를 '실행 전용 환경'으로 고정함으로써 안정성 확보.

### **계층별 보안(Layered Security) 아키텍처 및 인프라 설계**

- **Problem**
    - 각 서비스 포트 노출에 따른 보안 취약점과 전송 데이터 평문 전송으로 인한 가로채기 위험 존재.
- **Action**
    - **단일 진입점(Nginx Gateway) 설계**: 외부 포트는 오직 80/443만 개방하고, 내부 서비스(Spring, FastAPI)는 Docker 내부 네트워크에서만 통신하도록 접근 제어 강화.
    - **전송 구간 암호화(HTTPS)**: Let's Encrypt 및 Certbot을 통해 SSL 인증서를 적용하고, 80 포트 접속 시 HTTPS로 강제 리다이렉트 처리.
    - **인프라 격리**: RDS의 Public Access를 차단하고, **AWS 보안 그룹(Security Group)** 설정을 통해 오직 백엔드 EC2 인스턴스의 3306 포트 접근만 허용하도록 네트워크 레벨에서 DB 격리.
- **Result**
    - 외부 위협으로부터 핵심 리소스(DB, API)를 격리하고, 실무 수준의 전송 보안과 네트워크 보안을 동시에 충족하는 인프라 구조 완성.

--- 
