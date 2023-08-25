package ga.injuk.commentor.adapter.out.persistence

import ga.injuk.commentor.application.port.out.persistence.CreateCommentPort
import ga.injuk.commentor.common.annotation.Adapter
import org.slf4j.LoggerFactory

@Adapter
class CommentorPersistenceAdapter(
    private val commentorRepository: CommentorRepository,
): CreateCommentPort {
    init {
        LoggerFactory
            .getLogger(this.javaClass)
            .debug("commentorRepository={}", commentorRepository.javaClass);
    }
}