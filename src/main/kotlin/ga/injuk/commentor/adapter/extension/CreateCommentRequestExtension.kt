package ga.injuk.commentor.adapter.extension

import ga.injuk.commentor.models.*

internal fun CreateCommentRequest?.convert()
    = this?.run {
        ga.injuk.commentor.application.port.dto.request.CreateCommentRequest(
            domain = this.domain,
            resource = ga.injuk.commentor.application.port.dto.Resource(
                id = this.resource.id,
            ),
            parts = this.parts.map { it.convertToDomainCommentPart() }
        )
    }


private fun CommentPart.convertToDomainCommentPart()
    = ga.injuk.commentor.domain.model.CommentPart(
        type = this.type.convertToDomainCommentPartType(),
        attrs = ga.injuk.commentor.domain.model.CommentPart.Attrs(
            level = this.attrs?.level
        ),
        content = this.content.map { it.convertToDomainContent() }
    )

private fun CommentPartType.convertToDomainCommentPartType()
    = when(this) {
        CommentPartType.PARAGRAPH -> ga.injuk.commentor.domain.model.CommentPartType.PARAGRAPH
        CommentPartType.HEADING -> ga.injuk.commentor.domain.model.CommentPartType.HEADING
    }

private fun Content.convertToDomainContent()
    = ga.injuk.commentor.domain.model.Content(
        type = this.type.convertToDomainContentType(),
        text = this.text ?: "",
        marks = this.marks?.map {
            ga.injuk.commentor.domain.model.Mark(
                type = it.type.convertToDomainMarkType(),
                attrs = ga.injuk.commentor.domain.model.Mark.Attrs(
                    type = it.attrs?.type.convertToDomainMarkAttrsType(),
                    color = it.attrs?.color,
                )
            )
        } ?: emptyList(),
        attrs = ga.injuk.commentor.domain.model.Content.Attrs(
            id = this.attrs?.id,
            text = this.attrs?.text,
        ),
    )

private fun ContentType.convertToDomainContentType()
    = when (this) {
        ContentType.TEXT -> ga.injuk.commentor.domain.model.ContentType.TEXT
        ContentType.HARD_BREAK -> ga.injuk.commentor.domain.model.ContentType.HARD_BREAK
        ContentType.MENTION -> ga.injuk.commentor.domain.model.ContentType.MENTION
    }

private fun MarkType.convertToDomainMarkType()
    = when (this) {
        MarkType.STRONG -> ga.injuk.commentor.domain.model.MarkType.STRONG
        MarkType.EM -> ga.injuk.commentor.domain.model.MarkType.EM
        MarkType.STRIKE -> ga.injuk.commentor.domain.model.MarkType.STRIKE
        MarkType.CODE -> ga.injuk.commentor.domain.model.MarkType.CODE
        MarkType.UNDERLINE -> ga.injuk.commentor.domain.model.MarkType.UNDERLINE
        else -> null
    }

private fun MarkAttrsType?.convertToDomainMarkAttrsType()
    = when (this) {
        MarkAttrsType.SUB -> ga.injuk.commentor.domain.model.MarkAttrsType.SUB
        MarkAttrsType.SUP -> ga.injuk.commentor.domain.model.MarkAttrsType.SUP
        else -> null
    }