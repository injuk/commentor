package ga.injuk.commentor.common

interface IdConverter {
    fun encode(plain: Long): String
    fun decode(cipher: String): Long?
}