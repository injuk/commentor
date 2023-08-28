package ga.injuk.commentor.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldHave
import java.util.UUID

class RequestContextTest : BehaviorSpec() {
    init {
        Given("RequestContext 인스턴스를 create 메소드로 생성할 경우") {
            val requestContext = RequestContext.create()

            When("인스턴스에 포함된 Trace는") {
                val result = requestContext.trace

                Then("자동 생성된 UUID 형태의 traceId를 갖는다.") {
                    val uuid = UUID.fromString(result.id)

                    (uuid is UUID) shouldBe true
                }
            }
        }
    }
}
