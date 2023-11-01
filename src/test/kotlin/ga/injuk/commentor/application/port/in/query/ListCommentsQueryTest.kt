package ga.injuk.commentor.application.port.`in`.query

import ga.injuk.commentor.application.port.dto.Resource
import ga.injuk.commentor.application.port.dto.request.ListCommentsRequest
import ga.injuk.commentor.application.port.`in`.ListCommentsUseCase
import ga.injuk.commentor.application.port.`in`.ListSubCommentsUseCase
import ga.injuk.commentor.common.IdConverter
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.Comment
import ga.injuk.commentor.domain.model.CommentDomain
import ga.injuk.commentor.domain.model.SortCondition
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class ListCommentsQueryTest : BehaviorSpec() {
    override fun extensions(): List<Extension> = listOf(SpringExtension)

    @Autowired
    private lateinit var listComments: ListCommentsUseCase

    @Autowired
    private lateinit var listSubComments: ListSubCommentsUseCase

    init {
        Given("임의의 사용자가 준비되었을 때") {
            val user = User.builder()
                .setAuthorization("my-authorization")
                .setProject("list_test_proj")
                .setOrganization("list_test_org")
                .build()

            When("아무런 검색 조건 없이 검색을 시도할 경우") {
                val (data) = listComments.execute(user, ListCommentsRequest())

                Then("빈 목록과 null 커서가 반환된다.") {

                    data.results shouldBe emptyList()
                    data.nextCursor shouldBe null
                }
            }

            When("유효한 검색 조건과 함께 검색을 시도할 경우") {
                val limit = 11L
                val (data) = listComments.execute(user, ListCommentsRequest(
                    limit = limit,
                    domain = CommentDomain.VIDEO,
                    resource = Resource(id = "list_test_res"),
                ))
                val (results, nextCursor) = data

                Then("유효한 검색 결과가 반환된다.") {

                    results.size shouldBeLessThanOrEqual limit.toInt()
                    nextCursor shouldBe null
                }

                And("조회 결과 중 이미 삭제된 댓글의 경우") {
                    val deletedComments = results.filter { it.isDeleted }
                    val notDeletedComments = results.filter { !it.isDeleted }

                    Then("목록에는 표시되지만 댓글 내용은 보이지 않아야 한다.") {

                        deletedComments.size shouldBeGreaterThan 0
                        deletedComments.all { it.parts.isEmpty() } shouldBe true
                        notDeletedComments.all { it.parts.isNotEmpty() } shouldBe true
                    }
                }

                And("조회 결과 중 내가 좋아요 한 댓글의 경우") {
                    val likedComment = results.filter { it.myInteraction != null }

                    Then("세 건이 존재한다.") {

                        likedComment.size shouldBe 3
                    }
                }

                And("조회 결과 중 자식이 있는 댓글의 경우") {
                    val parentComments = results.filter { it.hasSubComments }

                    Then("각 댓글의 자식 댓글 수는 0이 아니어야 한다") {

                        parentComments.all {
                            val subCommentsPagination = listSubComments.execute(user, ListCommentsRequest(
                                parentId = IdConverter.convert(it.encodedId)!!
                            ))
                            subCommentsPagination.data.results.isNotEmpty()
                        } shouldBe true
                    }
                }
            }
        }

        Given("임의의 사용자가 존재할 때") {
            val user = User.builder()
                .setAuthorization("my-authorization")
                .setProject("list_test_proj")
                .setOrganization("list_test_org")
                .build()

            When("생성일 내림차순 기준으로 모든 목록을 조회한 후") {
                val (expected) = listComments.execute(user, ListCommentsRequest(
                    limit = 1000L,
                    domain = CommentDomain.VIDEO,
                    resource = Resource(id = "list_test_res"),
                    sortCondition = SortCondition(
                        criteria = SortCondition.Criteria.CREATED_AT,
                        order = SortCondition.Order.DESC,
                    )
                ))

                And("다시 첫 목록부터 커서 기반 페이지네이션을 통해 모든 목록을 생성일 기준 내림차순으로 다시 순회할 경우") {
                    var cursor: String? = null
                    val comments = mutableListOf<Comment>()

                    val limit = 4L
                    do {
                        val (response) = listComments.execute(user, ListCommentsRequest(
                            limit = limit,
                            domain = CommentDomain.VIDEO,
                            resource = Resource(id = "list_test_res"),
                            nextCursor = cursor,
                            sortCondition = SortCondition(
                                criteria = SortCondition.Criteria.CREATED_AT,
                                order = SortCondition.Order.DESC,
                            )
                        ))
                        comments.addAll(response.results)
                        cursor = response.nextCursor
                    } while (cursor != null)

                    Then("두 결과는 동등해야 한다.") {

                        expected.results shouldBe comments
                        expected.results.size shouldBe comments.size
                    }
                }
            }

            When("생성일 오름차순 기준으로 모든 목록을 조회한 후") {
                val (expected) = listComments.execute(user, ListCommentsRequest(
                    limit = 1000L,
                    domain = CommentDomain.VIDEO,
                    resource = Resource(id = "list_test_res"),
                    sortCondition = SortCondition(
                        criteria = SortCondition.Criteria.CREATED_AT,
                        order = SortCondition.Order.ASC,
                    )
                ))

                And("다시 첫 목록부터 커서 기반 페이지네이션을 통해 모든 목록을 생성일 기준 오름차순으로 다시 순회할 경우") {
                    var cursor: String? = null
                    val comments = mutableListOf<Comment>()

                    val limit = 4L
                    do {
                        val (response) = listComments.execute(user, ListCommentsRequest(
                            limit = limit,
                            domain = CommentDomain.VIDEO,
                            resource = Resource(id = "list_test_res"),
                            nextCursor = cursor,
                            sortCondition = SortCondition(
                                criteria = SortCondition.Criteria.CREATED_AT,
                                order = SortCondition.Order.ASC,
                            )
                        ))
                        comments.addAll(response.results)
                        cursor = response.nextCursor
                    } while (cursor != null)

                    Then("두 결과는 동등해야 한다.") {

                        expected.results shouldBe comments
                        expected.results.size shouldBe comments.size
                    }
                }
            }

            When("수정일 내림차순 기준으로 모든 목록을 조회한 후") {
                val (expected) = listComments.execute(user, ListCommentsRequest(
                    limit = 1000L,
                    domain = CommentDomain.VIDEO,
                    resource = Resource(id = "list_test_res"),
                    sortCondition = SortCondition(
                        criteria = SortCondition.Criteria.UPDATED_AT,
                        order = SortCondition.Order.DESC,
                    )
                ))

                And("다시 첫 목록부터 커서 기반 페이지네이션을 통해 모든 목록을 수정일 기준 내림차순으로 다시 순회할 경우") {
                    var cursor: String? = null
                    val comments = mutableListOf<Comment>()

                    val limit = 4L
                    do {
                        val (response) = listComments.execute(user, ListCommentsRequest(
                            limit = limit,
                            domain = CommentDomain.VIDEO,
                            resource = Resource(id = "list_test_res"),
                            nextCursor = cursor,
                            sortCondition = SortCondition(
                                criteria = SortCondition.Criteria.UPDATED_AT,
                                order = SortCondition.Order.DESC,
                            )
                        ))
                        comments.addAll(response.results)
                        cursor = response.nextCursor
                    } while (cursor != null)

                    Then("두 결과는 동등해야 한다.") {

                        expected.results shouldBe comments
                        expected.results.size shouldBe comments.size
                    }
                }
            }

            When("수정일 오름차순 기준으로 모든 목록을 조회한 후") {
                val (expected) = listComments.execute(user, ListCommentsRequest(
                    limit = 1000L,
                    domain = CommentDomain.VIDEO,
                    resource = Resource(id = "list_test_res"),
                    sortCondition = SortCondition(
                        criteria = SortCondition.Criteria.UPDATED_AT,
                        order = SortCondition.Order.ASC,
                    )
                ))

                And("다시 첫 목록부터 커서 기반 페이지네이션을 통해 모든 목록을 수정일 기준 오름차순으로 다시 순회할 경우") {
                    var cursor: String? = null
                    val comments = mutableListOf<Comment>()

                    val limit = 4L
                    do {
                        val (response) = listComments.execute(user, ListCommentsRequest(
                            limit = limit,
                            domain = CommentDomain.VIDEO,
                            resource = Resource(id = "list_test_res"),
                            nextCursor = cursor,
                            sortCondition = SortCondition(
                                criteria = SortCondition.Criteria.UPDATED_AT,
                                order = SortCondition.Order.ASC,
                            )
                        ))
                        comments.addAll(response.results)
                        cursor = response.nextCursor
                    } while (cursor != null)

                    Then("두 결과는 동등해야 한다.") {

                        expected.results shouldBe comments
                        expected.results.size shouldBe comments.size
                    }
                }
            }
        }
    }
}
