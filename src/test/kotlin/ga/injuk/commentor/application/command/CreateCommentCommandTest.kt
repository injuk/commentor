package ga.injuk.commentor.application.command

import ga.injuk.commentor.adapter.core.exception.BadRequestException
import ga.injuk.commentor.application.port.dto.Resource
import ga.injuk.commentor.application.port.dto.request.CreateCommentRequest
import ga.injuk.commentor.application.port.`in`.CreateCommentUseCase
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
class CreateCommentCommandTest : BehaviorSpec() {
    override fun extensions(): List<Extension> = listOf(SpringExtension)

    @Autowired
    private lateinit var createComment: CreateCommentUseCase

    @Autowired
    private lateinit var idConverter: IdConverter

    init {
        Given("사용자와 댓글이 준비되었을 때") {
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

            When("댓글을 생성할 경우, 응답을 디코딩하는 것으로") {
                val request = CreateCommentRequest(
                    domain = "ARTICLE",
                    resource = Resource("my-test-resource-id"),
                    parts = commentParts,
                )
                val response = createComment.execute(user, request)
                val result = idConverter.decode(response.id)

                Then("Long 형태의 id를 반환받을 수 있다.") {
                    result shouldNotBe null
                    result!! shouldBeGreaterThanOrEqual 1L
                }
            }

            When("그러나 빈 댓글을 생성 시도할 경우") {
                val emptyCommentParts = emptyList<CommentPart>()
                val request = CreateCommentRequest(
                    domain = "ARTICLE",
                    resource = Resource("my-test-resource-id"),
                    parts = emptyCommentParts,
                )
                val exception = shouldThrow<BadRequestException> {
                    createComment.execute(user, request)
                }

                Then("BadRequestException이 발생한다.") {
                    val expectedErrorDetail = ErrorDetail(
                        code = "COMMENTOR_BAD_REQUEST_EXCEPTION",
                        messages = listOf("Comment is required")
                    )

                    exception.errorDetails shouldBe expectedErrorDetail
                }
            }
        }
    }
}
