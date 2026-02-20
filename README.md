# 🌼 Flowerable Backend

#### 꽃 주문 플랫폼 **Flowerable**의 서버 애플리케이션입니다.  
#### 사용자/꽃집 계정 관리, 주문, 결제, 채팅, 알림, 이미지 업로드 등 서비스 전반의 비즈니스 로직을 담당합니다.

개발 형태 : 개인 프로젝트 (기획/설계/개발 전담)

---

## 📌 Tech Stack

### Backend
- Java 17
- Spring Boot 3.x
- Spring Security + JWT
- OAuth2 (Google / Kakao / Naver)
- Spring Data JPA (Hibernate)

### Database & Infra
- PostgreSQL
- Redis (Refresh Token / Notification / SSE)
- AWS S3 (이미지 업로드)
- Docker Compose
- AWS EC2 (배포)

### Communication
- REST API
- WebSocket (STOMP 기반 채팅)
- Server-Sent Events (알림)

---

## 📌 주요 기능

### 👤 Account
- JWT 기반 인증 (Access / Refresh)
- OAuth2 소셜 로그인
- Role 기반 권한 관리
    - ROLE_USER
    - ROLE_SHOP
    - ROLE_ADMIN

### 🌷 Flower / Shop
- 꽃 카탈로그 관리
- 꽃집 등록 및 상태 관리
- ShopFlower (가격, 색상, 판매 여부)

### 🛒 Order
- 주문 생성 / 상태 변경
- 주문 취소 및 환불
- Toss 결제 연동

### 💬 Chat
- WebSocket 기반 실시간 채팅
- ChatRoom / ChatMessage 관리
- unread count 처리

### 🔔 Notification
- SSE 기반 실시간 알림
- DB 저장 알림 로그

### 🖼 Image
- AWS S3 Presigned URL 업로드
- Gemini 이미지 생성 결과 저장

---

## 📌 ERD 핵심 엔티티

- Account
- User
- Shop
- Flower
- ShopFlower
- OrderRequest
- OrderItem
- ChatRoom
- ChatMessage
- Notification

---
## 📌 환경 변수 설정

### application.yml

```yaml
  jpa:
    hibernate:
      ddl-auto: update
      // 추후 validate로 변경 예정
    properties:
      hibernate:
        format_sql: true
        show_sql: true
jwt:
  secret: ${JWT_SECRET}
  access-expiration: 900000
  refresh-expiration: 1209600000

redis:
  host: ${REDIS_HOST}
  port: ${REDIS_PORT}

gemini:
  api:
    key: ${GEMINI_API_KEY}
  models:
    text: gemini-2.5-flash
    image: gemini-2.5-flash-image
  config:
    temperature: 0.4
    max-output-tokens: 512
    
toss:
  secret-key: ${TOSS_SECRET_KEY}

cloud:
  aws:
    credentials:
      access-key: ${AWS_ACCESS_KEY_ID}
      secret-key: ${AWS_SECRET_ACCESS_KEY}
    s3:
      bucket: ${AWS_S3_BUCKET}
    region:
      static: ap-northeast-2
```
---
## 📌 API 문서
| Domain       | Endpoint           |
| ------------ | ------------------ |
| Auth         | /api/auth          |
| User         | /api/users         |
| Shop         | /api/shops         |
| Flower       | /api/flowers       |
| Order        | /api/orders        |
| Chat         | /api/chat          |
| Notification | /api/notifications |

---
##📌 인증 구조
```declarative
Client
  ↓
JWT Access Token
  ↓
Spring Security Filter
  ↓
Controller
```
- AccessToken : 짧은 수명
- RefreshToken : Redis 저장 / 블랙리스트 관리

---

## 📌 결제 Flow
```
Order 생성
   ↓
Toss 결제 요청
   ↓
결제 성공
   ↓
Order 상태 변경
```
### 취소 시
```declarative
Order Cancel
   ↓
Toss Cancel API 호출
   ↓
환불 처리
```
---
## 📌 알림 구조

- 주문 생성
- 채팅 수신
- 상태 변경
```declarative
Event 발생
   ↓
Notification 저장
   ↓
SSE Push
```
---
## 📌 배포

- AWS EC2
- Docker Compose (예상)
- PostgreSQL (RDS 또는 Local)
---
## 📌 성능/설계 고려사항

- Fetch Join 기반 N+1 방지
- Soft Delete 적용
- 상태 enum 기반 도메인 설계
- DDD 스타일 패키징
- Redis 캐싱 및 토큰 관리
- Presigned URL 업로드로 서버 부하 감소