package ga.injuk.commentor.application.port.`in`

import ga.injuk.commentor.application.port.dto.request.BulkDeleteCommentRequest

interface BulkDeleteCommentUseCase: UseCase<BulkDeleteCommentRequest, Int>