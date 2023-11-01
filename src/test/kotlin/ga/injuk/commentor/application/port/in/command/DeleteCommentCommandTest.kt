package ga.injuk.commentor.application.command

import com.ninjasquad.springmockk.SpykBean
import ga.injuk.commentor.adapter.out.dto.AffectedRows
import ga.injuk.commentor.adapter.out.persistence.dataAccess.CommentsDataAccess
import ga.injuk.commentor.application.core.exception.BadRequestException
import ga.injuk.commentor.application.core.exception.ResourceNotFoundException
import ga.injuk.commentor.application.port.dto.request.DeleteCommentRequest
import ga.injuk.commentor.application.port.`in`.DeleteCommentUseCase
import ga.injuk.commentor.common.ErrorDetail
import ga.injuk.commentor.domain.User
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.unmockkObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class DeleteCommentCommandTest : BehaviorSpec() {
    override fun extensions(): List<Extension> = listOf(SpringExtension)

    @Autowired
    private lateinit var deleteComment: DeleteCommentUseCase

    @SpykBean
    private lateinit var commentsDataAccess: CommentsDataAccess

    init {
        afterEach { unmockkObject(commentsDataAccess) }

        Given("임의의 사용자가 존재할 때") {
            val user = User.builder()
                .setAuthorization("my-authorization")
                .setProject("my-project-id")
                .setOrganization("my-organization-id")
                .build()

            When("자신의 댓글 중 하나에 대해 삭제를 시도할 경우") {
                val myCommentId = 1L
                val request = DeleteCommentRequest(id = myCommentId)
                val result = deleteComment.execute(user, request)

                Then("affectedRows를 의미하는 정수 1이 반환된다.") {

                    result shouldBe 1
                }
            }

            When("존재하지 않는 댓글에 대해 삭제를 시도할 경우") {
                val invalidCommentId = -1L
                val request = DeleteCommentRequest(id = invalidCommentId)
                val exception = shouldThrow<ResourceNotFoundException> {
                    deleteComment.execute(user, request)
                }

                Then("ResourceNotFoundException이 발생한다.") {
                    val expectedErrorDetail = ErrorDetail(
                        code = "COMMENTOR_RESOURCE_NOT_FOUND_EXCEPTION",
                        messages = listOf("There is no comment")
                    )

                    exception.errorDetails shouldBe expectedErrorDetail
                }
            }

            When("자신의 댓글이 아닌 댓글에 대해 삭제를 시도할 경우") {
                val notMyComment = 3L
                val request = DeleteCommentRequest(id = notMyComment)

                val exception = shouldThrow<BadRequestException> {
                    deleteComment.execute(user, request)
                }

                Then("BadRequestException이 발생한다.") {
                    val expectedErrorDetail = ErrorDetail(
                        code = "COMMENTOR_BAD_REQUEST_EXCEPTION",
                        messages = listOf("Cannot delete other's comment")
                    )

                    exception.errorDetails shouldBe expectedErrorDetail
                }
            }

            When("이미 제거된 댓글에 대해 다시 제거를 시도할 경우") {
                val alreadyDeletedComment = 4L
                val request = DeleteCommentRequest(id = alreadyDeletedComment)

                val exception = shouldThrow<BadRequestException> {
                    deleteComment.execute(user, request)
                }

                Then("BadRequestException이 발생한다.") {
                    val expectedErrorDetail = ErrorDetail(
                        code = "COMMENTOR_BAD_REQUEST_EXCEPTION",
                        messages = listOf("Comment has already been deleted")
                    )

                    exception.errorDetails shouldBe expectedErrorDetail
                }
            }

            When("어떤 이유에 의해 아무 댓글도 제거되지 않은 경우") {
                val myCommentId = 2L
                val request = DeleteCommentRequest(id = myCommentId)
                every {
                    commentsDataAccess.delete(eq(user), eq(request))
                } returns AffectedRows(count = 0)

                val exception = shouldThrow<BadRequestException> {
                    deleteComment.execute(user, request)
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
