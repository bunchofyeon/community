# 🌲 Piney Community
Piney Community는 Spring Boot 기반으로 개발된 커뮤니티 웹 서비스 백엔드로<br>
스프링 시큐리티 기반 JWT 인증, 게시글·댓글 CRUD, S3/Lambda 파일 업로드, Soft Delete, 조회수/좋아요 기능 등 실제 서비스에 필요한 핵심 기능을 갖춘 프로젝트입니다.<br>
또한 Docker, Nginx Reverse Proxy, AWS 인프라 구성과 GitHub Actions를 통한 CI/CD를 적용해 실전 운영 환경을 목표로 제작되었습니다.<br>

---

# 👩🏻‍💻 개발 기간 및 역할
- 2025.10 ~ 2025.12
- 전체 백엔드 + 인프라 단독 구현
- 프론트엔드 SPA 구성도 직접 구현
  
# 📘 프로젝트 개요

**Piney Community**는 개인적인 고민 공유 + 개발 소통 기능을 제공하는 커뮤니티 서비스입니다.  
아키텍처는 다음과 같이 구성되어 있습니다.

- **Frontend**: Express.js 기반 SPA  
- **Backend**: Spring Boot REST API  
- **Infrastructure**: AWS EC2, S3, API Gateway, Lambda, Route53  
- **Deployment**: Docker, Docker Compose, Private Registry, GitHub Actions CI/CD  
- **Networking**: Nginx Reverse Proxy + HTTPS 

---

# 🖼 전체 아키텍처 이미지

<img width="2093" height="1525" alt="piney_community" src="https://github.com/user-attachments/assets/b6905518-3950-4d5b-9bab-69bc54f20913" />

---

# 🛠 기술 스택

### Frontend
- Express.js  
- HTML5 / CSS3 / JS  
- Docker / Docker Compose  

### Backend
- Spring Boot 3.5 / Java 21  
- Spring Security + JWT  
- Spring Data JPA
- MySQL 8.0  
  
### DevOps / Infra
- AWS EC2 / Route53
- AWS S3 / Lambda / API Gateway  
- Docker / Docker Compose 
- Portainer / Private Docker Registry
- Nginx Reverse Proxy  
- Certbot(HTTPS 자동 인증)  
- GitHub Actions (CI/CD)  

---


## 📁 파일 업로드 / 다운로드 구조

### 1️⃣ 파일 업로드 흐름
> 브라우저 → Spring Boot → (API Gateway → Lambda) → Spring Boot → S3 순서로 흐르고,  
> 실제 S3에 HTTP PUT을 날리는 주체는 **Spring Boot 서버**입니다.

### 2️⃣ 파일 다운로드 흐름
> 브라우저 → Spring Boot → (API Gateway → Lambda) → Spring Boot → 브라우저 → S3 순서이며,  
> 최종적으로 S3에서 파일을 받는 주체는 **브라우저**입니다.

---

# 🌐 도메인 / HTTPS

- 도메인: piney.cloud 
- DNS: Route53
- HTTPS: Nginx + Certbot 자동 인증  

---


# 📦 Docker / 배포 구조

### EC2 #1 – Reverse Proxy
- Nginx + Certbot (HTTPS)
- / → FE  
- /api → BE  

### EC2 #2 – Frontend
- Express.js SPA  
- Docker Compose  
- GitHub Actions로 자동 배포  

### EC2 #3 – Backend
- Spring Boot API  
- MySQL  
- Private Docker Registry  
- Portainer  
- Docker Compose 기반 배포 자동화  

---

# 🚚 CI/CD 구조

### Frontend / Backend 공통
1. main 브랜치 push  
2. GitHub Actions에서 Docker 이미지 빌드  
3. Private Docker Registry에 push  
4. SSH로 EC2 연결  
5. docker compose pull → up -d 자동 반영  

---

# 🐞 트러블슈팅

- Nginx Reverse Proxy 헤더 누락으로 SPA 라우팅 오류 -> 해결
- multipart 용량 제한 -> Nginx/Spring 충돌 해결

---

# 👩🏻‍💻 프로젝트 후기

Express 기반 단일 서버 구조에서 Spring Boot 기반 레이어드 아키텍처로 확장하며  
**백엔드 개발 역량뿐 아니라 Docker·AWS·CI/CD·네트워크 이해까지 크게 성장한 프로젝트**였습니다.

직접 운영 환경을 구축해보면서  
서비스 아키텍처 설계, 배포 자동화, 보안 구성 등  
실제 기업 개발 환경을 경험할 수 있었습니다.


