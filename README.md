## TIKKLEMOA
캘린더 가계부 관리와 절약형 커뮤니티 👉 http://100.26.204.51/

![image](https://github.com/hjh3933/tikklemoa_back/assets/107241014/216183ad-38a8-4409-824d-1c80519aa6c3)

작업 기간: 2024.05.31 ~ 2024.06.21

인원: 1명(개인프로젝트)

test계정: 로그인 박스 상단 test계정으로 로그인 클릭

## project 관리

Front: https://github.com/hjh3933/tikklemoa_front

Back: https://github.com/hjh3933/tikklemoa_back

ERD: https://www.erdcloud.com/d/axGdZkRKyGcyymmim

발표자료 → https://www.canva.com/design/DAGIX1uemcY/EXG-QFzJ-V_T0BWG-OFvwA/view?utm_content=DAGIX1uemcY&utm_campaign=designshare&utm_medium=link&utm_source=editor

## 📖 Description

캘린더를 통한 일별, 월별 가계부 관리 기능을 제공합니다

커뮤니티를 통한 게시글 작성, 댓글 기능을 제공하여 회원들과 소통할 수 있습니다

쪽지 기능을 통해 회원 간 1대1 소통이 가능합니다
 
## ⭐ Main Feature

회원기능 -  spring security를 통한 pw 암호화, jwt token을 통한 로그인 관리, s3를 활용하여 프로필 이미지 관리

게시글 기능 - s3를 통한 게시글 이미지 관리, 댓글 기능 제공, 로그인 user검증을 통한 게시글 삭제 수정 구현, 좋아요 클릭 및 좋아요 한 게시글 마이페이지에서 조회

캘린더 기능 - react-calendar, nivo차트 라이브러리 사용, 캘린더의 컬러 테마와 일별 총계 표시여부에 대한 설정 기능 제공, 스프링 스케줄러를 사용한 회원별 뱃지 부여기능 제공(월별 소비 기준)

쪽지 기능 - 수신인 검증, 쪽지 전송 후 수신인이 읽기 전 전송취소 가능, 수신인과 발신인 각각 논리삭제 구현

## 💻 Getting Started

### Installation
```
npm install
```
### Develop Mode
```
npm run dev
```
### Production
```
npm start
```

## 🔧 Stack

Language: JavaScript, java

Library & Framework : Node.js, springboot

Database : AWS RDS (Mysql)

ORM : jpa

Deploy: AWS EC2

## 🐤 Demo

* 회원 서비스

https://github.com/hjh3933/tikklemoa_back/assets/107241014/6e292240-925b-4f66-bbc8-f01b1c34fdd5

* 게시글 서비스

https://github.com/hjh3933/tikklemoa_back/assets/107241014/6fa026c9-2fb6-49e5-9b2b-5d543f79f87b
  
* 캘린더 서비스

https://github.com/hjh3933/tikklemoa_back/assets/107241014/c49720fd-8ef6-4a52-aad3-9dbe4cf8e922

* 쪽지 서비스

https://github.com/hjh3933/tikklemoa_back/assets/107241014/7de51cc8-fb75-4aab-b14d-aae854d485da

## 👨‍👩‍👧‍👦 Developer

**홍주희** ([hjh3933](https://github.com/hjh3933))
