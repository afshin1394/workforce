package domain.models.task


data class ActivityListDomain (
        val id: Long? = 0,
        val title: String? = null,
        val instancePrefix :String= "",

    )

{
    override fun toString(): String {
        return "Detail(id=$id,title=$title ,instancePrefix=$instancePrefix)"
    }
}

