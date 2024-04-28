# 프로젝트 방장

- `방을 거래하는 장터`라는 의미의 이름입니다 😀
- 회원이라면 누구나 `집주인`으로 글을 작성할 수 있고 `새입자`로써 방을 확일 할 수 있습니다. 

<br>

---

## 개발 기간

- 2024-03-08 ~ 진행중
- 주기적으로 업데이트 하며 느리더라도 꾸준히 작업을 이어갈 예정입니다.

<br>

---

<br>

### 서비스 확인하기

- <a href="https://api.yongjun.store/health" target="_blank">AIP 도메인(healthCheck) 확인해 보기</a>
- <a href="https://api.yongjun.store/swagger-ui/index.html" target="_blank">Swagger 확인해 보기</a>
- <a href="https://yongjun.store/" target="_blank">방장 바로가기</a>

<br>

### 그 외

- <a href="https://yongjun96.github.io" target="_blank">yongjun-Blog 바로가기</a>
- <a href="https://github.com/yongjun96/yogjun-store-vue" target="_blank">yongjun-store-vue 바로가기</a>

<br>

--- 

<br>

### 해당 프로젝트에 사용된 기술 목록 😉

<br>

#### 프로젝트 구성

- 프레임워크 : `Spring Boot 3.2.3`, `Spring Framework`
- API 아키텍처 : `REST API`
- 언어 : `Java 17`
- 빌드 : `Gradle 8.5`
- 웹 서버 : `Apache`
- 문서 `Swagger`
- CI/CD : `Github Actions`
- 보안 : `Spring Security 6.2.2`, `JWT`, `oauth2`
- ORM 및 쿼리 라이브러리 : `Jpa`, `QureyDsl`
- 테스트 : `Junit5`, `Assertj`, `Mockito`
- RDBMS : `MySql`
- InMemory : `Redis`, `H2`

<br>

#### AWS

- 인스턴스 : `EC2(Centos)`
- DNS : `Route53`
- SSL/TLS : `ELB(Elastic Load Balancer)`
- RDS : `MySql 8.0`
- 정적 저장소 : `S3`
- Repository : `ECR`

<br>

#### 컨테이너 관리 도구

- `Docker`
- `Docker Compose`

<br>

---

<br>

### ERD 구성 👀

<br>


![ERD](src/main/resources/templates/yongjun-store-erd.PNG)

<br>

※ `BaseTimeEntity`를 상속받아 `생성 시간`과 `업데이트 시간`을 관리 합니다.  
※ `RefreshToken`을 사용해 사용자의 `Token`을 갱신시키도록 합니다.

<br>

---

### 아키텍처

<br>

![아키텍처](src/main/resources/templates/전체적인%20아키텍처.jpg)

<br>

- `yongjun-store-vue`와 `yongjun-store`를 포함한 전체적인 방장 아키텍처입니다.

<br>

![아키텍처](src/main/resources/templates/백엔드%20아키텍처.png)

- `yongjun-store`의 `백엔드 API` 아키텍처입니다.







