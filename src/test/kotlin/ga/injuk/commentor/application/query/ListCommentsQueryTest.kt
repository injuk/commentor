package ga.injuk.commentor.application.query

import ga.injuk.commentor.application.port.dto.IdEncodedComment
import ga.injuk.commentor.application.port.dto.Resource
import ga.injuk.commentor.application.port.dto.request.ListCommentsRequest
import ga.injuk.commentor.application.port.`in`.ListCommentsUseCase
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.CommentDomain
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class ListCommentsQueryTest : BehaviorSpec() {
    override fun extensions(): List<Extension> = listOf(SpringExtension)

    @Autowired
    private lateinit var listComments: ListCommentsUseCase

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

                Then("유효한 검색 결과가 반환된다.") {
                    val (results, nextCursor) = data

                    results.size shouldBe limit
                    nextCursor shouldNotBe null
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
                    sortConditions = ListCommentsRequest.SortConditions(
                        criteria = ListCommentsRequest.Criteria.CREATED_AT,
                        order = ListCommentsRequest.Order.DESC,
                    )
                ))

                And("다시 첫 목록부터 커서 기반 페이지네이션을 통해 모든 목록을 생성일 기준 내림차순으로 다시 순회할 경우") {
                    var cursor: String? = null
                    val comments = mutableListOf<IdEncodedComment>()

                    val limit = 4L
                    do {
                        val (response) = listComments.execute(user, ListCommentsRequest(
                            limit = limit,
                            domain = CommentDomain.VIDEO,
                            resource = Resource(id = "list_test_res"),
                            nextCursor = cursor,
                            sortConditions = ListCommentsRequest.SortConditions(
                                criteria = ListCommentsRequest.Criteria.CREATED_AT,
                                order = ListCommentsRequest.Order.DESC,
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
                    sortConditions = ListCommentsRequest.SortConditions(
                        criteria = ListCommentsRequest.Criteria.CREATED_AT,
                        order = ListCommentsRequest.Order.ASC,
                    )
                ))

                And("다시 첫 목록부터 커서 기반 페이지네이션을 통해 모든 목록을 생성일 기준 오름차순으로 다시 순회할 경우") {
                    var cursor: String? = null
                    val comments = mutableListOf<IdEncodedComment>()

                    val limit = 4L
                    do {
                        val (response) = listComments.execute(user, ListCommentsRequest(
                            limit = limit,
                            domain = CommentDomain.VIDEO,
                            resource = Resource(id = "list_test_res"),
                            nextCursor = cursor,
                            sortConditions = ListCommentsRequest.SortConditions(
                                order = ListCommentsRequest.Order.ASC,
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
                    sortConditions = ListCommentsRequest.SortConditions(
                        criteria = ListCommentsRequest.Criteria.UPDATED_AT,
                        order = ListCommentsRequest.Order.DESC,
                    )
                ))

                And("다시 첫 목록부터 커서 기반 페이지네이션을 통해 모든 목록을 수정일 기준 내림차순으로 다시 순회할 경우") {
                    var cursor: String? = null
                    val comments = mutableListOf<IdEncodedComment>()

                    val limit = 4L
                    do {
                        val (response) = listComments.execute(user, ListCommentsRequest(
                            limit = limit,
                            domain = CommentDomain.VIDEO,
                            resource = Resource(id = "list_test_res"),
                            nextCursor = cursor,
                            sortConditions = ListCommentsRequest.SortConditions(
                                criteria = ListCommentsRequest.Criteria.UPDATED_AT,
                                order = ListCommentsRequest.Order.DESC,
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
                    sortConditions = ListCommentsRequest.SortConditions(
                        criteria = ListCommentsRequest.Criteria.UPDATED_AT,
                        order = ListCommentsRequest.Order.ASC,
                    )
                ))

                And("다시 첫 목록부터 커서 기반 페이지네이션을 통해 모든 목록을 수정일 기준 오름차순으로 다시 순회할 경우") {
                    var cursor: String? = null
                    val comments = mutableListOf<IdEncodedComment>()

                    val limit = 4L
                    do {
                        val (response) = listComments.execute(user, ListCommentsRequest(
                            limit = limit,
                            domain = CommentDomain.VIDEO,
                            resource = Resource(id = "list_test_res"),
                            nextCursor = cursor,
                            sortConditions = ListCommentsRequest.SortConditions(
                                criteria = ListCommentsRequest.Criteria.UPDATED_AT,
                                order = ListCommentsRequest.Order.ASC,
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
