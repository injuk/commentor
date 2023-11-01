package ga.injuk.commentor.application.command

import ga.injuk.commentor.application.core.exception.BadRequestException
import ga.injuk.commentor.application.core.exception.ResourceNotFoundException
import ga.injuk.commentor.application.port.dto.Resource
import ga.injuk.commentor.application.port.dto.request.CreateCommentRequest
import ga.injuk.commentor.application.port.`in`.CreateSubCommentUseCase
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
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class CreateSubCommentCommandTest : BehaviorSpec() {
    override fun extensions(): List<Extension> = listOf(SpringExtension)

    @Autowired
    private lateinit var createSubComment: CreateSubCommentUseCase

    init {
        Given("사용자와 부모 댓글, 그리고 대댓글이 준비되었을 때") {
            val user = User.builder()
                .setAuthorization("my-authorization")
                .setProject("my-project-id")
                .setOrganization("my-organization-id")
                .build()
            val parentCommentId = 3L
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

            When("대댓글을 생성할 경우, 응답을 디코딩하는 것으로") {
                val request = CreateCommentRequest(
                    domain = "ARTICLE",
                    resource = Resource("my-test-resource-id"),
                    parts = commentParts,
                    parentId = parentCommentId,
                )

                val response = createSubComment.execute(user, request)
                val result = IdConverter.convert(response.id)

                Then("Long 형태의 id를 반환받을 수 있다.") {
                    result shouldNotBe null
                    result!! shouldBeGreaterThanOrEqual 1L
                }
            }

            When("부모 댓글 정보를 전달하지 않고 대댓글을 생성 시도할 경우") {
                val request = CreateCommentRequest(
                    domain = "ARTICLE",
                    resource = Resource("my-test-resource-id"),
                    parts = commentParts,
                    parentId = null,
                )
                val exception = shouldThrow<BadRequestException> {
                    createSubComment.execute(user, request)
                }

                Then("BadRequestException이 발생한다.") {
                    val expectedErrorDetail = ErrorDetail(
                        code = "COMMENTOR_BAD_REQUEST_EXCEPTION",
                        messages = listOf("ParentId is required")
                    )

                    exception.errorDetails shouldBe expectedErrorDetail
                }
            }

            When("빈 대댓글을 생성 시도할 경우") {
                val emptyCommentParts = emptyList<CommentPart>()
                val request = CreateCommentRequest(
                    domain = "ARTICLE",
                    resource = Resource("my-test-resource-id"),
                    parts = emptyCommentParts,
                    parentId = parentCommentId,
                )
                val exception = shouldThrow<BadRequestException> {
                    createSubComment.execute(user, request)
                }

                Then("BadRequestException이 발생한다.") {
                    val expectedErrorDetail = ErrorDetail(
                        code = "COMMENTOR_BAD_REQUEST_EXCEPTION",
                        messages = listOf("Comment is required")
                    )

                    exception.errorDetails shouldBe expectedErrorDetail
                }
            }

            When("존재하지 않는 부모 댓글에 대해 대댓글을 생성 시도할 경우") {
                val request = CreateCommentRequest(
                    domain = "ARTICLE",
                    resource = Resource("my-test-resource-id"),
                    parts = commentParts,
                    parentId = -1L,
                )
                val exception = shouldThrow<ResourceNotFoundException> {
                    createSubComment.execute(user, request)
                }

                Then("ResourceNotFoundException이 발생한다.") {
                    val expectedErrorDetail = ErrorDetail(
                        code = "COMMENTOR_RESOURCE_NOT_FOUND_EXCEPTION",
                        messages = listOf("There is no parent comment")
                    )

                    exception.errorDetails shouldBe expectedErrorDetail
                }
            }

            When("삭제된 부모 댓글에 대해 대댓글을 생성 시도할 경우") {
                val deletedCommentId = 4L
                val request = CreateCommentRequest(
                    domain = "ARTICLE",
                    resource = Resource("my-test-resource-id"),
                    parts = commentParts,
                    parentId = deletedCommentId,
                )
                val exception = shouldThrow<BadRequestException> {
                    createSubComment.execute(user, request)
                }

                Then("BadRequestException이 발생한다.") {
                    val expectedErrorDetail = ErrorDetail(
                        code = "COMMENTOR_BAD_REQUEST_EXCEPTION",
                        messages = listOf("Cannot create sub-comment for deleted comment")
                    )

                    exception.errorDetails shouldBe expectedErrorDetail
                }
            }
        }
    }
}
