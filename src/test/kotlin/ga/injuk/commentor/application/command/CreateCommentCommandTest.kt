package ga.injuk.commentor.application.command

import ga.injuk.commentor.application.port.dto.Resource
import ga.injuk.commentor.application.port.dto.request.CreateCommentRequest
import ga.injuk.commentor.application.port.`in`.CreateCommentUseCase
import ga.injuk.commentor.application.port.out.persistence.CreateCommentPort
import ga.injuk.commentor.common.IdConverter
import ga.injuk.commentor.domain.User
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
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
    private lateinit var createCommentPort: CreateCommentPort

    @Autowired
    private lateinit var idConverter: IdConverter

    init {
        Given("사용자와 댓글 생성 요청이 준비되었을 때") {
            val user = User.builder()
                .setAuthorization("my-authorization")
                .setProject("my-project-id")
                .setOrganization("my-organization-id")
                .build()
            val request = CreateCommentRequest(
                domain = "ARTICLE",
                resource = Resource("my-test-resource-id"),
                parts = emptyList(),
            )

            When("댓글을 생성할 경우 응답을 디코딩하는 것으로") {
                val response = createComment.execute(user, request)
                val result = idConverter.decode(response.id)

                Then("Long 형태의 id를 반환받을 수 있다.") {
                    result shouldNotBe null
                    result!! shouldBeGreaterThanOrEqual 1L
                }
            }
        }
    }
}
