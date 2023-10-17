package ga.injuk.commentor.application.query

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
                .setProject("migrated_project_id")
                .setOrganization("migrated_org_id")
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
                    resource = Resource(id = "migrated_resource_id"),
                ))

                Then("유효한 검색 결과가 반환된다.") {
                    val (results, nextCursor) = data

                    results.size shouldBe limit
                    nextCursor shouldNotBe null
                }
            }


        }
    }
}
