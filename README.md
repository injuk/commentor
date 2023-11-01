# Commentor
> 꼼멘또르 - 여기 저기에 붙일 수 있는 댓글 서비스를 만들어보자
- 사실 댓글 기능을 만들어보고 싶은건 아니고, 코프링 써보고 싶어서 적당한 주제를 선정하였음
    - 때문에 인증은 안붙일 예정
### 회고
- 작성 예정

### 목표
- [x] 헥사고널 아키텍쳐 적용해보기
- [x] 커서 기반 페이지네이션 구현해보기
- [x] Kotlin으로 완성된 프로젝트 만들어보기

### 기간
넉넉히 `2023-08-21 ~ 2023-10-31`으로 잡음
- `2023-10-14` 로직 작성 완료

### 기술 스택
- Kotlin + Spring Boot + Jooq
- PostgreSQL(Docker)
- opeAPIGenerator(open api spec 3.0)

---
### 정책
#### 공통
- 어떤 org / project / domain / resource에 대해 댓글을 생성할 수 있음
- 생성된 댓글을 조회하거나 수정, 삭제할 수 있음
- 댓글은 자식 댓글(서브코멘트)를 가질 수 있음
- 댓글 형태는 Atlassian 모델을 벤치마킹하였음
#### 삭제
- 삭제하더라도 실제로 삭제되진 않고, `is_deleted` 마킹만 함
    - 유튜브를 벤치마킹했음
    - 반면, 댓글이 연결된 리소스(`resource_id`)가 제거된 경우에는 진짜로 제거함
#### LIKE / DISLIKE
- 어떤 코멘트에 대해서는 사용자 당 최대 한 번씩만 좋아요 / 싫어요할 수 있음
- 이미 좋아요한 댓글을 다시 좋아요하거나, 싫어요한 댓글을 다시 싫어요하면 취소 동작으로 취급됨
- 기존 액션과 반대되는 액션을 취하는 경우 기존 액션을 취소하고 반대 액션을 적용함
    - 예를 들어, 좋아요한 댓글에 대해 싫어요할 경우 좋아요를 취소하고 싫어요만을 적용함
---
### API
#### CRUD
댓글 하나의 정보만을 볼 일이 없을 것 같아서 `[GET] /comments/{id}`는 제공하지 않음
- `[GET] /comments`
- `[POST] /comments`
- `[PATCH] /comments/{id}`
- `[DELETE] /comments/{id}`
#### 서브코멘트 생성 / 목록조회
수정과 삭제는 상술한 API를 사용하면 됨
- `[GET] /comments/{id}/sub-comments`
- `[POST] /comments/{id}/sub-comments`
#### action API
- `[POST] /comments/actions`: 리소스가 제거된 경우, 연결된 모든 댓글을 삭제하기 위해 사용
- `[POST] /comments/{id}/actions`: 임의의 댓글에 대한 LIKE / DISLIKE

---
