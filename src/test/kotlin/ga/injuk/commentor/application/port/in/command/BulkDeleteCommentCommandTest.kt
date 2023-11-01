package ga.injuk.commentor.application.command

import com.ninjasquad.springmockk.SpykBean
import ga.injuk.commentor.adapter.out.dto.AffectedRows
import ga.injuk.commentor.adapter.out.persistence.dataAccess.CommentsDataAccess
import ga.injuk.commentor.application.core.exception.BadRequestException
import ga.injuk.commentor.application.port.dto.request.BulkDeleteCommentRequest
import ga.injuk.commentor.application.port.`in`.BulkDeleteCommentUseCase
import ga.injuk.commentor.common.ErrorDetail
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.CommentDomain
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.spyk
import io.mockk.unmockkObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class BulkDeleteCommentCommandTest : BehaviorSpec() {
    override fun extensions(): List<Extension> = listOf(SpringExtension)

    @Autowired
    private lateinit var bulkDeleteComment: BulkDeleteCommentUseCase

    @SpykBean
    private lateinit var commentsDataAccess: CommentsDataAccess

    private val authorization = "my-authorization"

    init {
        afterEach { unmockkObject(commentsDataAccess) }

        Given("삭제 대상 도메인 및 리소스 목록이 존재할 때") {
            val domain = CommentDomain.ARTICLE
            val resourceIds = listOf("bulk_delete_target_1", "bulk_delete_target_2", "bulk_delete_target_3")

            When("SYSTEM 사용자가 리소스 별 댓글 삭제를 시도할 경우") {
                val user = User.builder()
                    .setAuthorization("my-authorization")
                    .setProject("my-project-id")
                    .setOrganization("my-organization-id")
                    .build()
                val result = bulkDeleteComment.execute(user, BulkDeleteCommentRequest(
                    domain = domain,
                    resourceIds = resourceIds,
                ))

                Then("1 이상의 정수가 반환된다.") {

                    result shouldBeGreaterThanOrEqual 1
                }
            }

            When("그러나 SYSTEM이 아닌 사용자가 리소스 별 댓글 삭제를 시도할 경우") {
                val spyBuilder = spyk<User.UserBuilder>(recordPrivateCalls = true)
                every {
                    spyBuilder["parseAuthorization"](eq(authorization))
                } returns "NO_SYSTEM_USER"

                val noSystemUser = spyBuilder
                    .setAuthorization(authorization)
                    .setProject("my-project-id")
                    .setOrganization("my-organization-id")
                    .build()

                val exception = shouldThrow<BadRequestException> {
                    bulkDeleteComment.execute(noSystemUser, BulkDeleteCommentRequest(
                        domain = domain,
                        resourceIds = resourceIds,
                    ))
                }

                Then("BadRequestException이 발생한다.") {
                    val expectedErrorDetail = ErrorDetail(
                        code = "COMMENTOR_BAD_REQUEST_EXCEPTION",
                        messages = listOf("Only SYSTEM users can invoke this api")
                    )

                    exception.errorDetails shouldBe expectedErrorDetail
                }
            }

            When("빈 리소스 목록으로 삭제를 시도할 경우") {
                val user = User.builder()
                    .setAuthorization("my-authorization")
                    .setProject("my-project-id")
                    .setOrganization("my-organization-id")
                    .build()
                val emptyResourceIds = emptyList<String>()

                val exception = shouldThrow<BadRequestException> {
                    bulkDeleteComment.execute(user, BulkDeleteCommentRequest(
                        domain = domain,
                        resourceIds = emptyResourceIds,
                    ))
                }

                Then("BadRequestException이 발생한다.") {
                    val expectedErrorDetail = ErrorDetail(
                        code = "COMMENTOR_BAD_REQUEST_EXCEPTION",
                        messages = listOf("Resource ids cannot be empty")
                    )

                    exception.errorDetails shouldBe expectedErrorDetail
                }
            }

            When("어떤 이유에 의해 아무 댓글도 수정되지 않은 경우") {
                val user = User.builder()
                    .setAuthorization("my-authorization")
                    .setProject("my-project-id")
                    .setOrganization("my-organization-id")
                    .build()
                val request = BulkDeleteCommentRequest(
                    domain = domain,
                    resourceIds = resourceIds,
                )
                every {
                    commentsDataAccess.deleteBy(eq(request))
                } returns AffectedRows(count = 0)

                val exception = shouldThrow<BadRequestException> {
                    bulkDeleteComment.execute(user, request)
                }

                Then("BadRequestException이 발생한다.") {
                    val expectedErrorDetail = ErrorDetail(
                        code = "COMMENTOR_BAD_REQUEST_EXCEPTION",
                        messages = listOf("There is no comment deleted")
                    )

                    exception.errorDetails shouldBe expectedErrorDetail
                }
            }
        }
    }
}
