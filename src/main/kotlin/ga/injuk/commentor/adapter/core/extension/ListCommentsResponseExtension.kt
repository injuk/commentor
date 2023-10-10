package ga.injuk.commentor.adapter.core.extension

import ga.injuk.commentor.application.port.dto.IdEncodedComment
import ga.injuk.commentor.application.port.dto.response.ListCommentsResponse
import ga.injuk.commentor.domain.model.*
import ga.injuk.commentor.models.MyInteraction

internal fun ListCommentsResponse.convert()
    = this.run {
        val (results, nextCursor) = `data`
        ga.injuk.commentor.models.ListCommentsResponse(
            results = results.convertToListOfComments(),
            nextCursor = nextCursor,
        )
    }

private fun List<IdEncodedComment>.convertToListOfComments()
    = this.map {
        ga.injuk.commentor.models.Comment(
            id = it.id,
            parts = it.parts.map { part -> part.convertToGeneratedCommentPart() },
            isDeleted = it.isDeleted,
            hasSubComments = it.hasSubComments,
            myInteraction = it.myInteraction?.let { interaction ->
                MyInteraction(
                    type = when(interaction) {
                        CommentInteractionType.LIKE -> MyInteraction.Type.LIKE
                        CommentInteractionType.DISLIKE -> MyInteraction.Type.DISLIKE
                    }
                )
            },
            likeCount = it.likeCount,
            dislikeCount = it.dislikeCount,
            created = ga.injuk.commentor.models.CommentCreated(
                at = it.created.at.toOffsetDateTime(),
                by = ga.injuk.commentor.models.By(it.created.by.id),
            ),
            updated = ga.injuk.commentor.models.CommentUpdated(
                at = it.updated.at.toOffsetDateTime(),
                by = ga.injuk.commentor.models.By(it.updated.by.id),
            )
        )
    }

private fun CommentPart.convertToGeneratedCommentPart()
    = ga.injuk.commentor.models.CommentPart(
        type = this.type.convertToGeneratedCommentPartType(),
        attrs = ga.injuk.commentor.models.CommentPartAttrs(
            level = this.attrs?.level,
        ),
        content = this.content.map { it.convertToGeneratedContent() }
    )

private fun CommentPartType.convertToGeneratedCommentPartType()
    = when(this) {
        CommentPartType.PARAGRAPH -> ga.injuk.commentor.models.CommentPartType.PARAGRAPH
        CommentPartType.HEADING -> ga.injuk.commentor.models.CommentPartType.HEADING
    }

private fun Content.convertToGeneratedContent()
    = ga.injuk.commentor.models.Content(
        type = this.type.convertToGeneratedContentType(),
        text = this.text,
        marks = this.marks?.map {
            ga.injuk.commentor.models.Mark(
                type = it.type?.convertToGeneratedMarkType(),
                attrs = ga.injuk.commentor.models.MarkAttrs(
                    type = it.attrs?.type.convertToGeneratedMarkAttrsType(),
                    color = it.attrs?.color,
                )
            )
        } ?: emptyList(),
        attrs = ga.injuk.commentor.models.ContentAttrs(
            id = this.attrs?.id,
            text = this.attrs?.text,
        ),
    )

private fun ContentType.convertToGeneratedContentType()
    = when(this) {
        ContentType.TEXT -> ga.injuk.commentor.models.ContentType.TEXT
        ContentType.HARD_BREAK -> ga.injuk.commentor.models.ContentType.HARD_BREAK
        ContentType.MENTION -> ga.injuk.commentor.models.ContentType.MENTION
    }

private fun MarkType?.convertToGeneratedMarkType()
    = when(this) {
        MarkType.STRONG -> ga.injuk.commentor.models.MarkType.STRONG
        MarkType.EM -> ga.injuk.commentor.models.MarkType.EM
        MarkType.STRIKE -> ga.injuk.commentor.models.MarkType.STRIKE
        MarkType.CODE -> ga.injuk.commentor.models.MarkType.CODE
        MarkType.UNDERLINE -> ga.injuk.commentor.models.MarkType.UNDERLINE
        else -> null
    }

private fun MarkAttrsType?.convertToGeneratedMarkAttrsType()
    = when(this) {
        MarkAttrsType.SUB -> ga.injuk.commentor.models.MarkAttrsType.SUB
        MarkAttrsType.SUP -> ga.injuk.commentor.models.MarkAttrsType.SUP
        else -> null
    }