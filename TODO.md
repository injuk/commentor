# Commentor TODO
## Common
* ~~outgoing adapter에 runCatching 적용하기~~
  * 할 필요 없을 것 같아서 안했음

## ListComments
* ~~삭제된 Comment의 parts를 포함하지 않도록 수정할 것~~

## CommentorConfig
* ~~`@Value` 말고 `@ConfigurationProperties` 쓰게 바꿔보자~~
  * 할 필요 없을 것 같아서 안했음

## PostgreSqlRepository
* ~~trx를 전부 다 제거할 것! 간단히 사용할 수 있는 `isExist` Outgoing Port를 만들어두면 좋을 것 같다~~.
  * ~~update: commentId 기반 조회 후, 없는 경우에만 404 / 있으면 업데이트~~
  * ~~delete: 역시 commentId 기반 조회 후, 없는 경우에 404 / 다른 사람의 댓글인 경우 / 또는 이미 제거된 경우(is_deleted) BRE~~
  * ~~deleteBy: resourceId 및 domain 기반 조회 후, 아무것도 없으면 BRE / 있으면 진짜로 제거~~ 