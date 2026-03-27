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
### 1. 서비스의 일관성 유지를 위한 구조 개선

- 문제: AI 서버가 사용자의 이전 결과와 상태를 기억하지 못하는 상태
- 시도: 클라이언트가 전체 데이터 전달, AI 서버 상태 저장, 백엔드 상태 관리 방안
- 해결: current_step 기반 상태 전이 + state_context 최소 문맥 저장 구조
- 결과: 서버 재시작 상황에서도 최소한의 필수 정보로 서비스의 일관성을 유지

📎 [관련 블로그](https://velog.io/@jhj9903/Stateless-AI-%EC%84%9C%EB%B2%84%EC%97%90%EC%84%9C-%EC%9D%BC%EA%B4%80%EC%84%B1%EC%9D%84-%EC%9C%A0%EC%A7%80%ED%95%9C-%EB%B0%A9%EB%B2%95)

### 2. AI 서비스의 응답 속도 측정 / 개선 실패

- 문제: 서버가 결과물을 생성해서 사용자에게까지 보내지는 응답 시간이 지연되어 UX 저하 발생
- 측정: 각 단계의 API를 구간별 로깅한 결과, 전체 응답의 99%가 AI 호출 구간에서 발생
- 추론: 각구간에 state_context가 누적되면서 AI의 응답속도가 늦어짐으로 판단
- 시도: Story단계의 state_context를 줄여 payload 감소
- 결과: payload는 약 46.3% 감소했지만 응답속도는 오히려 증가 병목이 payload가 아닌 AI 생성 비용임을 확인

📎 [관련 블로그](https://velog.io/@jhj9903/%EB%8B%A8%EA%B3%84%ED%98%95-AI-%EC%84%9C%EB%B9%84%EC%8A%A4%EC%97%90%EC%84%9C-%EC%9D%91%EB%8B%B5-%EC%86%8D%EB%8F%84%EA%B0%80-%EB%8A%90%EB%A6%B0-%EC%9D%B4%EC%9C%A0%EA%B0%80-%EB%AD%98%EA%B9%8C)

--- 
