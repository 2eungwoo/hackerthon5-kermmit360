# Contents

✔️ 주제 및 배경

✔️ 주요 기능

✔️ 기술 스택 및 시스템 아키텍처

✔️ 배포 환경

✔️ 시연
---
# Overviews

# 🌀 Kermmit360

> 🐸 *“Track every streak of your code.”*      - *Kermmit360*
> 

![image](https://github.com/user-attachments/assets/5c6cc357-afe1-40ce-8ca1-35c13b0ff174)


### 🌐 Background

### 기록 하는 행위에 대한 즐거움

- 매일 진행한 학습 내용들을 다양한 플랫폼을 통해 기록하는 사람이 많다.
- 자신이 노력했던 흔적을 남기고 눈으로 볼 수 있는 자료를 제공

### 학습 동기부여 및 성취감

- 성장하는 본인의 모습을 보며 느끼는 성취감 및 열정 고취
- 경쟁을 통한 심리적 자극으로부터 오는 학습 동기부여

### 💡 Reference

### *solved.ac*

![image](https://github.com/user-attachments/assets/e254dfc5-bc59-4e83-a8a1-66f3f7803916)

### *codeforce*

![image](https://github.com/user-attachments/assets/1458047c-feef-45fd-a385-ca34f3a7241b)

### *github-snake*

![image](https://github.com/user-attachments/assets/888b1209-1e98-40d3-b9e6-71a7638b9750)

> https://github.com/Platane/snk
> 
---
# Main Features

### ✅ Spring Security, Github OAuth2

- 회원가입, 로그인, 로그아웃

### ✅ Github 푸쉬 이벤트 로그 추적

- Github Api → pushEvent 추적
- 푸쉬 이벤트 감지를 이용한 시각 자료 제공

### ✅ 통합 랭킹 시스템

- 커밋량에 따른 rank point, tier 상승
- 자신의 자세한 깃허브 활동 정보 열람
---
# Tech Stack / System Architecture

### ✔️  Tech Stack

### Back-end

- Spring Boot v3
- MySQL v8
- Spring Security
- Spring JPA

### Front-end

- Thymeleaf, CSS

### 3rd-Party

- Naver Cloud Platform
- Github OAuth2
- Github API

### ✔️ System Architecture

***Arcitechture***

![image](https://github.com/user-attachments/assets/674cdea7-c7eb-47cd-998a-286df07dc792)

> sever-side render
> 

- *OAuth2 Flow*
    
    ![image](https://github.com/user-attachments/assets/6877c25b-ce4d-4d13-8bd8-311f77bd6378)
    
    ![image](https://github.com/user-attachments/assets/9fe4cf36-063f-456b-aa34-bb59fd161c0c)
    
---
# Deployment

### ☁️ Cloud

![image](https://github.com/user-attachments/assets/81e936ad-aba7-4cf2-a32e-2fc93e0a00e1)

### 📠 VM Spec

- `mi1-g3(vCPU 1EA, Memory 1GB)`
- `ubuntu-24.04`

### 💡Process

> remote repo → pull → builid → run .jar
> 

*run-prod.sh*

![image](https://github.com/user-attachments/assets/1a109aa5-4adf-425e-beca-afc49370dbf8)

---
# Demonstrate



### ✅ Scenario

- 사용자는 Github 계정을 통해 로그인하고 자신의 기록을 열람한다.
- 사용자는 서비스에 가입 후 열심히 커밋하고 상승하는 자신의 랭킹을 보며 흡족해한다.

### 😮 Current Limitation & Bug

- access token의 필요로 인해 자신의 정보만 열람 가능
- (시큐리티 가입 회원의 경우) 깃허브 연동 기능이 없어 대다수의 더미데이터
- github 내에서 계정명을 개인적으로 변경한 경우 data fetch가 안됨
- github 내에서 profile → email 설정 안한 경우도 data fetch가 안됨

Link : [Kermmit360](http://175.45.204.118:8081/auth/signin) 
