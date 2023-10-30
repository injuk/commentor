package ga.injuk.commentor.application.command

import com.ninjasquad.springmockk.SpykBean
import ga.injuk.commentor.adapter.core.exception.BadRequestException
import ga.injuk.commentor.adapter.core.exception.ResourceNotFoundException
import ga.injuk.commentor.application.port.dto.request.ActionCommentRequest
import ga.injuk.commentor.application.port.dto.request.GetCommentInteractionRequest
import ga.injuk.commentor.application.port.dto.request.GetCommentRequest
import ga.injuk.commentor.application.port.`in`.ActionCommentUseCase
import ga.injuk.commentor.application.port.out.persistence.GetCommentInteractionPort
import ga.injuk.commentor.application.port.out.persistence.GetCommentPort
import ga.injuk.commentor.common.ErrorDetail
import ga.injuk.commentor.domain.User
import ga.injuk.commentor.domain.model.By
import ga.injuk.commentor.domain.model.Comment
import ga.injuk.commentor.domain.model.CommentInteractionType
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.spyk
import io.mockk.unmockkObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
class ActionCommentCommandTest : BehaviorSpec() {
    override fun extensions(): List<Extension> = listOf(SpringExtension)

    @Autowired
    private lateinit var actionComment: ActionCommentUseCase

    @Autowired
    private lateinit var getInteractionPort: GetCommentInteractionPort

    @SpykBean
    private lateinit var getCommentPort: GetCommentPort

    private val authorization = "my-authorization"

    init {
        afterEach { unmockkObject(getCommentPort) }

        Given("임의의 사용자와 댓글이 준비되었을 때") {
            val targetCommentId = 115L

            When("임의의 사용자가 댓글에 대해 'LIKE' 액션을 시도한 경우") {
                val user = User.builder()
                    .setAuthorization(authorization)
                    .setProject("action_test_proj")
                    .setOrganization("action_test_org")
                    .build()

                actionComment.execute(user, ActionCommentRequest(
                    id = targetCommentId,
                    action = CommentInteractionType.LIKE,
                ))

                Then("최초의 상호 작용으로 취급하여 새로운 interaction을 생성한다.") {
                    val likeInteraction = getInteractionPort.get(user, GetCommentInteractionRequest(
                        commentId = targetCommentId,
                    ))

                    likeInteraction shouldNotBe null
                    likeInteraction!!.commentId shouldBe targetCommentId
                    likeInteraction.type shouldBe CommentInteractionType.LIKE
                    likeInteraction.userId shouldBe user.id
                }

                And("새로운 액션은 댓글의 likeCount에도 영향을 준다.") {
                    val targetComment = getCommentPort.get(user, GetCommentRequest(
                        commentId = targetCommentId,
                    ))

                    targetComment shouldNotBe null
                    targetComment!!.likeCount shouldBe 1
                }
            }

            When("또 다른 사용자가 댓글에 대해 'DISLIKE' 액션을 시도한 경우") {
                val spyBuilder = spyk<User.UserBuilder>(recordPrivateCalls = true)
                every {
                    spyBuilder["parseAuthorization"](eq(authorization))
                } returns "USER"

                val otherUser = spyBuilder
                    .setAuthorization(authorization)
                    .setProject("action_test_proj")
                    .setOrganization("action_test_org")
                    .build()

                actionComment.execute(otherUser, ActionCommentRequest(
                    id = targetCommentId,
                    action = CommentInteractionType.DISLIKE,
                ))

                Then("최초의 상호 작용으로 취급하여 새로운 interaction을 생성한다.") {
                    val dislikeInteraction = getInteractionPort.get(otherUser, GetCommentInteractionRequest(
                        commentId = targetCommentId,
                    ))

                    dislikeInteraction shouldNotBe null
                    dislikeInteraction!!.commentId shouldBe targetCommentId
                    dislikeInteraction.type shouldBe CommentInteractionType.DISLIKE
                    dislikeInteraction.userId shouldBe otherUser.id
                }

                And("새로운 액션은 댓글의 dislikeCount에도 영향을 준다.") {
                    val targetComment = getCommentPort.get(otherUser, GetCommentRequest(
                        commentId = targetCommentId,
                    ))

                    targetComment shouldNotBe null
                    targetComment!!.dislikeCount shouldBe 1
                }
            }

            When("그러나 존재하지 않는 댓글에 대해 액션을 시도한 경우") {
                val user = User.builder()
                    .setAuthorization(authorization)
                    .setProject("action_test_proj")
                    .setOrganization("action_test_org")
                    .build()
                val invalidCommentId = -1L

                val exception = shouldThrow<ResourceNotFoundException> {
                    actionComment.execute(user, ActionCommentRequest(
                        id = invalidCommentId,
                        action = CommentInteractionType.LIKE,
                    ))
                }

                Then("ResourceNotFoundException이 발생한다.") {
                    val expectedErrorDetail = ErrorDetail(
                        code = "COMMENTOR_RESOURCE_NOT_FOUND_EXCEPTION",
                        messages = listOf("There is no comment")
                    )

                    exception.errorDetails shouldBe expectedErrorDetail
                }
            }

            When("이미 삭제된 댓글에 대해 액션을 시도한 경우") {
                val user = User.builder()
                    .setAuthorization(authorization)
                    .setProject("action_test_proj")
                    .setOrganization("action_test_org")
                    .build()
                every {
                    getCommentPort.get(any(), any())
                } returns Comment(
                    id = targetCommentId,
                    parts = emptyList(),
                    isDeleted = true,
                    hasSubComments = false,
                    likeCount = 0L,
                    dislikeCount = 0L,
                    created = Comment.Context(
                        at = LocalDateTime.now(),
                        by = By(user.id),
                    ),
                    updated = Comment.Context(
                        at = LocalDateTime.now(),
                        by = By(user.id),
                    ),
                )

                val exception = shouldThrow<BadRequestException> {
                    actionComment.execute(user, ActionCommentRequest(
                        id = targetCommentId,
                        action = CommentInteractionType.LIKE,
                    ))
                }

                Then("BadRequestException이 발생한다.") {
                    val expectedErrorDetail = ErrorDetail(
                        code = "COMMENTOR_BAD_REQUEST_EXCEPTION",
                        messages = listOf("Cannot action for deleted comment")
                    )

                    exception.errorDetails shouldBe expectedErrorDetail
                }
            }
        }

        Given("임의의 사용자와, 해당 사용자가 이미 상호 작용한 댓글이 준비되었을 때") {
            val user = User.builder()
                .setAuthorization(authorization)
                .setProject("action_test_proj")
                .setOrganization("action_test_org")
                .build()
            val targetCommentId = 116L

            When("사용자가 댓글에 대해 동일한 액션을 시도한 경우") {
                actionComment.execute(user, ActionCommentRequest(
                    id = targetCommentId,
                    action = CommentInteractionType.LIKE,
                ))
                actionComment.execute(user, ActionCommentRequest(
                    id = targetCommentId,
                    action = CommentInteractionType.LIKE,
                ))

                Then("액션에 대한 취소로 취급하여 interaction을 제거한다.") {
                    val interaction = getInteractionPort.get(user, GetCommentInteractionRequest(
                        commentId = targetCommentId,
                    ))

                    interaction shouldBe null
                }

                And("액션에 대한 취소는 해당 액션에 대응되는 댓글의 count에도 영향을 준다.") {
                    val targetComment = getCommentPort.get(user, GetCommentRequest(
                        commentId = targetCommentId,
                    ))

                    targetComment shouldNotBe null
                    targetComment!!.likeCount shouldBe 0
                    targetComment.dislikeCount shouldBe 0
                }
            }

            When("사용자가 댓글에 대해 반대의 액션을 시도한 경우") {
                actionComment.execute(user, ActionCommentRequest(
                    id = targetCommentId,
                    action = CommentInteractionType.LIKE,
                ))
                actionComment.execute(user, ActionCommentRequest(
                    id = targetCommentId,
                    action = CommentInteractionType.DISLIKE,
                ))

                Then("액션에 대한 변경으로 취급하여 interaction을 수정한다.") {
                    val interaction = getInteractionPort.get(user, GetCommentInteractionRequest(
                        commentId = targetCommentId,
                    ))

                    interaction shouldNotBe null
                    interaction!!.type shouldBe CommentInteractionType.DISLIKE
                }

                And("액션에 대한 수정은 해당 액션에 대응되는 댓글의 count에도 영향을 준다.") {
                    val targetComment = getCommentPort.get(user, GetCommentRequest(
                        commentId = targetCommentId,
                    ))

                    targetComment shouldNotBe null
                    targetComment!!.likeCount shouldBe 0
                    targetComment.dislikeCount shouldBe 1
                }
            }
        }
    }
}
