# Commentor TODO
## Common
* outgoing adapter에 runCatching 적용하기

## ListComments
* 삭제된 Comment의 parts를 포함하지 않도록 수정할 것

## CommentorConfig
* `@Value` 말고 `@ConfigurationProperties` 쓰게 바꿔보자

## PostgreSqlRepository
* trx를 전부 다 제거할 것! 간단히 사용할 수 있는 `isExist` Outgoing Port를 만들어두면 좋을 것 같다.