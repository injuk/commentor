package ga.injuk.commentor.application.command

import com.ninjasquad.springmockk.SpykBean
import ga.injuk.commentor.adapter.out.dto.AffectedRows
import ga.injuk.commentor.adapter.out.persistence.dataAccess.CommentsDataAccess
import ga.injuk.commentor.application.core.exception.BadRequestException
import ga.injuk.commentor.application.core.exception.ResourceNotFoundException
import ga.injuk.commentor.application.port.dto.request.UpdateCommentRequest
import ga.injuk.commentor.application.port.`in`.UpdateCommentUseCase
import ga.injuk.commentor.common.ErrorDetail
import ga.injuk.commentor.common.IdConverter
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.CommentPart
import ga.injuk.commentor.domain.model.CommentPartType
import ga.injuk.commentor.domain.model.Content
import ga.injuk.commentor.domain.model.ContentType
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.unmockkObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class UpdateCommentCommandTest : BehaviorSpec() {
    override fun extensions(): List<Extension> = listOf(SpringExtension)

    @Autowired
    private lateinit var updateComment: UpdateCommentUseCase

    @SpykBean
    private lateinit var commentsDataAccess: CommentsDataAccess

    init {
        afterEach { unmockkObject(commentsDataAccess) }

        Given("임의의 사용자와 수정하고자 하는 댓글 내용이 존재할 때") {
            val user = User.builder()
                .setAuthorization("my-authorization")
                .setProject("my-project-id")
                .setOrganization("my-organization-id")
                .build()
            val commentParts = listOf(CommentPart(
                type = CommentPartType.PARAGRAPH,
                attrs = null,
                content = listOf(
                    Content(
                        type = ContentType.TEXT,
                        text = "my test comment",
                        marks = null,
                        attrs = null,
                    )
                )
            ))

            When("자신의 댓글 중 하나에 대해 수정을 시도할 경우, 응답을 디코딩하는 것으로") {
                val myCommentId = 2L
                val request = UpdateCommentRequest(
                    id = myCommentId,
                    parts = commentParts,
                )
                val response = updateComment.execute(user, request)
                val result = IdConverter.convert(response.id)

                Then("Long 형태의 id를 반환받을 수 있다.") {

                    result shouldNotBe null
                    result!! shouldBe myCommentId
                }
            }

            When("그러나 빈 댓글로 수정을 시도할 경우") {
                val myCommentId = 2L
                val exceptionWhenEmptyParts = shouldThrow<BadRequestException> {
                    updateComment.execute(user, UpdateCommentRequest(
                        id = myCommentId,
                        parts = emptyList()
                    ))
                }
                val exceptionWhenNullParts = shouldThrow<BadRequestException> {
                    updateComment.execute(user, UpdateCommentRequest(
                        id = myCommentId,
                    ))
                }

                Then("BadRequestException이 발생한다.") {
                    val expectedErrorDetail = ErrorDetail(
                        code = "COMMENTOR_BAD_REQUEST_EXCEPTION",
                        messages = listOf("Comment is required")
                    )

                    exceptionWhenEmptyParts.errorDetails shouldBe expectedErrorDetail
                    exceptionWhenNullParts.errorDetails shouldBe expectedErrorDetail
                }
            }

            When("존재하지 않는 댓글에 대해 삭제를 시도할 경우") {
                val invalidCommentId = -1L
                val request = UpdateCommentRequest(
                    id = invalidCommentId, parts = commentParts
                )
                val exception = shouldThrow<ResourceNotFoundException> {
                    updateComment.execute(user, request)
                }

                Then("ResourceNotFoundException이 발생한다.") {
                    val expectedErrorDetail = ErrorDetail(
                        code = "COMMENTOR_RESOURCE_NOT_FOUND_EXCEPTION",
                        messages = listOf("There is no comment")
                    )

                    exception.errorDetails shouldBe expectedErrorDetail
                }
            }

            When("자신의 댓글이 아닌 댓글에 대해 수정을 시도할 경우") {
                val notMyComment = 3L
                val request = UpdateCommentRequest(id = notMyComment, parts = commentParts)

                val exception = shouldThrow<BadRequestException> {
                    updateComment.execute(user, request)
                }

                Then("BadRequestException이 발생한다.") {
                    val expectedErrorDetail = ErrorDetail(
                        code = "COMMENTOR_BAD_REQUEST_EXCEPTION",
                        messages = listOf("Cannot update other's comment")
                    )

                    exception.errorDetails shouldBe expectedErrorDetail
                }
            }

            When("이미 제거된 댓글에 대해 수정 시도할 경우") {
                val alreadyDeletedComment = 4L
                val request = UpdateCommentRequest(id = alreadyDeletedComment, parts = commentParts)

                val exception = shouldThrow<BadRequestException> {
                    updateComment.execute(user, request)
                }

                Then("BadRequestException이 발생한다.") {
                    val expectedErrorDetail = ErrorDetail(
                        code = "COMMENTOR_BAD_REQUEST_EXCEPTION",
                        messages = listOf("Comment has already been deleted")
                    )

                    exception.errorDetails shouldBe expectedErrorDetail
                }
            }

            When("어떤 이유에 의해 아무 댓글도 수정되지 않은 경우") {
                val myCommentId = 2L
                val request = UpdateCommentRequest(id = myCommentId, parts = commentParts)
                every {
                    commentsDataAccess.update(eq(user), eq(request))
                } returns AffectedRows(count = 0)

                val exception = shouldThrow<BadRequestException> {
                    updateComment.execute(user, request)
                }

                Then("BadRequestException이 발생한다.") {
                    val expectedErrorDetail = ErrorDetail(
                        code = "COMMENTOR_BAD_REQUEST_EXCEPTION",
                        messages = listOf("There is no comment updated")
                    )

                    exception.errorDetails shouldBe expectedErrorDetail
                }
            }
        }
    }
}
