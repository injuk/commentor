package ga.injuk.commentor.application.service

import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
class CommentorServiceTest : BehaviorSpec() {

    override fun extensions(): List<Extension> = listOf(SpringExtension)
}
