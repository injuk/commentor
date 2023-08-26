package ga.injuk.commentor.domain

data class User(
    val id: String,
    val authorization: String,
    val district: District,
) {
    data class District(
        val organization: Organization?,
        val project: Project,
    )

    companion object {
        fun builder() = UserBuilder()
    }

    class UserBuilder {
        private var userId: String? = null
        private var authorization: String? = null
        private var organization: Organization? = null
        private var project: Project? = null

        fun setAuthorization(authorization: String) = this.apply {
            this.authorization = authorization
            this.userId = parseAuthorization(authorization)
        }

        // TODO: authorization으로 필요한 데이터를 뽑아올 수 있도록 고도화
        private fun parseAuthorization(authorization: String) = "SYSTEM"

        fun setOrganization(organizationId: String?) = this.apply {
            organizationId?.let {
                this.organization = Organization(it)
            }
        }

        fun setProject(projectId: String) = this.apply {
            this.project = Project(projectId)
        }

        fun build() = this.run {
            User(
                id = this.userId ?: throw RuntimeException("userId is required"),
                authorization = this.authorization ?: throw RuntimeException("userId is required"),
                district = District(
                    organization = this.organization,
                    project = this.project ?: throw RuntimeException("project is required"),
                ),
            )
        }
    }
}