package ga.injuk.commentor.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class UserTest : BehaviorSpec() {
    init {
        Given("User 인스턴스를 생성하기 위해 적절한 정보가 준비된 경우") {
            val authorization = "my-authorization-token"
            val projectId = "my-project-id"
            val orgId = "my-organization-id"

            When("User 클래스가 제공하는 빌더를 활용하여") {
                val result = User.builder()
                    .setAuthorization(authorization)
                    .setProject(projectId)
                    .setOrganization(orgId)
                    .build()

                Then("필요한 정보를 가진 User 인스턴스를 생성할 수 있다.") {
                    result.id shouldBe "SYSTEM"
                    result.authorization shouldBe authorization
                    result.district.project shouldBe Project(id = projectId)
                    result.district.organization shouldBe Organization(id = orgId)
                }
            }

            And("User 빌더에 orgId가 null로 전달되더라도") {
                val result = User.builder()
                    .setAuthorization(authorization)
                    .setProject(projectId)
                    .setOrganization(null)
                    .build()

                Then("User 인스턴스는 정상적으로 생성된다.") {
                    result.id shouldBe "SYSTEM"
                    result.authorization shouldBe authorization
                    result.district.project shouldBe Project(id = projectId)
                    result.district.organization shouldBe null
                }
            }
        }
    }
}
