package ga.injuk.commentor.application.command

import ga.injuk.commentor.application.port.`in`.ActionCommentUseCase
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class ActionCommentCommandTest : BehaviorSpec() {
    override fun extensions(): List<Extension> = listOf(SpringExtension)

    @Autowired
    private lateinit var actionComment: ActionCommentUseCase

    init {
        Given("임의의 사용자와 댓글이 준비되었을 때") {

            When("사용자가 댓글에 대해 'LIKE' 액션을 시도한 경우") {

                Then("최초의 상호 작용으로 취급하여 새로운 interaction을 생성한다.") {

                }

                And("새로운 액션은 댓글의 likeCount에도 영향을 준다.") {

                }
            }

            When("사용자가 댓글에 대해 'DISLIKE' 액션을 시도한 경우") {

                Then("최초의 상호 작용으로 취급하여 새로운 interaction을 생성한다.") {

                }

                And("새로운 액션은 댓글의 dislikeCount에도 영향을 준다.") {

                }
            }

            When("그러나 존재하지 않는 댓글에 대해 액션을 시도한 경우") {

                Then("ResourceNotFoundException이 발생한다.") {

                }
            }

            When("이미 삭제된 댓글에 대해 액션을 시도한 경우") {

                Then("BadRequestException이 발생한다.") {

                }
            }
        }

        Given("임의의 사용자와, 해당 사용자가 이미 상호 작용한 댓글이 준비되었을 때") {

            When("사용자가 댓글에 대해 동일한 액션을 시도한 경우") {

                Then("액션에 대한 취소로 취급하여 interaction을 제거한다.") {

                }

                And("액션에 대한 취소는 해당 액션에 대응되는 댓글의 count에도 영향을 준다.") {

                }
            }

            When("사용자가 댓글에 대해 반대의 액션을 시도한 경우") {

                Then("액션에 대한 변경으로 취급하여 interaction을 수정한다.") {

                }

                And("액션에 대한 수정은 해당 액션에 대응되는 댓글의 count에도 영향을 준다.") {

                }
            }
        }
    }
}
