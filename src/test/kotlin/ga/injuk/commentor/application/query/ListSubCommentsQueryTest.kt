package ga.injuk.commentor.application.query

import ga.injuk.commentor.adapter.core.exception.ResourceNotFoundException
import ga.injuk.commentor.application.port.dto.IdEncodedComment
import ga.injuk.commentor.application.port.dto.request.ListSubCommentsRequest
import ga.injuk.commentor.application.port.`in`.ListSubCommentsUseCase
import ga.injuk.commentor.common.ErrorDetail
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.SortCondition
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class ListSubCommentsQueryTest : BehaviorSpec() {

    override fun extensions(): List<Extension> = listOf(SpringExtension)

    @Autowired
    private lateinit var listSubComments: ListSubCommentsUseCase

    init {
        Given("임의의 사용자와 부모 댓글이 준비되었을 때") {
            val user = User.builder()
                .setAuthorization("my-authorization")
                .setProject("list_sub_test_proj")
                .setOrganization("list_sub_test_org")
                .build()
            val parentId = 50L

            When("유효한 검색 조건과 함께 검색을 시도할 경우") {
                val limit = 11L
                val (data) = listSubComments.execute(user, ListSubCommentsRequest(
                    limit = limit,
                    parentId = parentId,
                ))
                val (results, nextCursor) = data

                Then("유효한 검색 결과가 반환된다.") {

                    results.size shouldBe limit
                    nextCursor shouldNotBe null
                }

                And("그러나 조회 결과 중 이미 삭제된 댓글의 경우") {
                    val deletedComments = results.filter { it.isDeleted }
                    val notDeletedComments = results.filter { !it.isDeleted }

                    Then("목록에는 표시되지만 댓글 내용은 보이지 않아야 한다.") {

                        deletedComments.size shouldBeGreaterThan 0
                        deletedComments.all { it.parts.isEmpty() } shouldBe true
                        notDeletedComments.all { it.parts.isNotEmpty() } shouldBe true
                    }
                }
            }

            When("존재하지 않는 부모 댓글에 대해 검색을 시도할 경우") {
                val limit = 11L
                val invalidParentId = -1L
                val exception = shouldThrow<ResourceNotFoundException> {
                    listSubComments.execute(user, ListSubCommentsRequest(
                        limit = limit,
                        parentId = invalidParentId,
                    ))
                }

                Then("ResourceNotFoundException이 발생한다.") {
                    val expectedErrorDetail = ErrorDetail(
                        code = "COMMENTOR_RESOURCE_NOT_FOUND_EXCEPTION",
                        messages = listOf("There is no comment")
                    )

                    exception.errorDetails shouldBe expectedErrorDetail
                }
            }
        }

        Given("임의의 사용자와 부모 댓글이 존재할 때") {
            val user = User.builder()
                .setAuthorization("my-authorization")
                .setProject("list_test_proj")
                .setOrganization("list_test_org")
                .build()
            val parentId = 50L

            When("생성일 내림차순 기준으로 모든 목록을 조회한 후") {
                val (expected) = listSubComments.execute(user, ListSubCommentsRequest(
                    limit = 1000L,
                    parentId = parentId,
                    sortCondition = SortCondition(
                        criteria = SortCondition.Criteria.CREATED_AT,
                        order = SortCondition.Order.DESC,
                    )
                ))

                And("다시 첫 목록부터 커서 기반 페이지네이션을 통해 모든 목록을 생성일 기준 내림차순으로 다시 순회할 경우") {
                    var cursor: String? = null
                    val comments = mutableListOf<IdEncodedComment>()

                    val limit = 4L
                    do {
                        val (response) = listSubComments.execute(user, ListSubCommentsRequest(
                            limit = limit,
                            nextCursor = cursor,
                            parentId = parentId,
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
                val (expected) = listSubComments.execute(user, ListSubCommentsRequest(
                    limit = 1000L,
                    parentId = parentId,
                    sortCondition = SortCondition(
                        criteria = SortCondition.Criteria.CREATED_AT,
                        order = SortCondition.Order.ASC,
                    )
                ))

                And("다시 첫 목록부터 커서 기반 페이지네이션을 통해 모든 목록을 생성일 기준 오름차순으로 다시 순회할 경우") {
                    var cursor: String? = null
                    val comments = mutableListOf<IdEncodedComment>()

                    val limit = 4L
                    do {
                        val (response) = listSubComments.execute(user, ListSubCommentsRequest(
                            limit = limit,
                            parentId = parentId,
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
                val (expected) = listSubComments.execute(user, ListSubCommentsRequest(
                    limit = 1000L,
                    parentId = parentId,
                    sortCondition = SortCondition(
                        criteria = SortCondition.Criteria.UPDATED_AT,
                        order = SortCondition.Order.DESC,
                    )
                ))

                And("다시 첫 목록부터 커서 기반 페이지네이션을 통해 모든 목록을 수정일 기준 내림차순으로 다시 순회할 경우") {
                    var cursor: String? = null
                    val comments = mutableListOf<IdEncodedComment>()

                    val limit = 4L
                    do {
                        val (response) = listSubComments.execute(user, ListSubCommentsRequest(
                            limit = limit,
                            parentId = parentId,
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
                val (expected) = listSubComments.execute(user, ListSubCommentsRequest(
                    limit = 1000L,
                    parentId = parentId,
                    sortCondition = SortCondition(
                        criteria = SortCondition.Criteria.UPDATED_AT,
                        order = SortCondition.Order.ASC,
                    )
                ))

                And("다시 첫 목록부터 커서 기반 페이지네이션을 통해 모든 목록을 수정일 기준 오름차순으로 다시 순회할 경우") {
                    var cursor: String? = null
                    val comments = mutableListOf<IdEncodedComment>()

                    val limit = 4L
                    do {
                        val (response) = listSubComments.execute(user, ListSubCommentsRequest(
                            limit = limit,
                            nextCursor = cursor,
                            parentId = parentId,
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
