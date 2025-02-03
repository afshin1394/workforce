package domain.usecase.usecase.ticket



import data.network.response.task.activity.ActivityListResponse
import domain.mappers.toDomainList
import domain.mappers.toEntityList
import domain.models.task.ActivityListDomain
import domain.models.task.TaskDomain
import domain.repository.ITaskRepository
import domain.usecase.BaseUseCase

class GetActivityListUseCase(private val iTaskRepository: ITaskRepository,
) : BaseUseCase<List<ActivityListDomain>, Unit>() {
    override suspend fun run(params:Unit): List<ActivityListDomain> {

        val allEntities = iTaskRepository.getAllActivityList()


        val distinctEntities = allEntities.distinctBy { it.title }

        println("Comparing: instancePrefix UseCase=    ${distinctEntities}")
        return distinctEntities.toDomainList()
    }
}


/*
class GetActivityListUseCase(
    private val iTaskRepository: ITaskRepository
) : BaseUseCase<List<ActivityListDomain>, List<TaskDomain>>() {

    override suspend fun run(params: List<TaskDomain>): List<ActivityListDomain> {
        // 1) ابتدا داده‌های دیتابیس را می‌خوانیم
        val allEntities = iTaskRepository.getAllActivityList()

        // 2) اگر دیتابیس خالی است (یا اگر منطق خاصی برای "به‌روزرسانی اجباری" دارید)،
        //    فقط در این صورت به سرور وصل می‌شویم
        if (allEntities.isEmpty()) {
            // گرفتن داده از سرور (فقط یک بار، وقتی دیتابیس خالی است)
            val activityResponseList: List<ActivityListResponse> = iTaskRepository.fetchActivityList()

            // نگاشت ActivityListResponse به ActivityListDomain
            val activityDomainList: List<ActivityListDomain> = activityResponseList.map { response ->
                ActivityListDomain(
                    id = response.id,
                    title = response.title,
                    instancePrefix = ""  // مقدار اولیه
                )
            }

            // مپ کردن instancePrefix با استفاده از پارامترهای ورودی (اگر نیاز است)
            val taskDomainMap: Map<String, TaskDomain> = params.associateBy { it.activity__title }
            val updatedActivityList: List<ActivityListDomain> = activityDomainList.map { domain ->
                taskDomainMap[domain.title]?.let { matchingTask ->
                    domain.copy(instancePrefix = matchingTask.instancePrefix)
                } ?: domain
            }

            // درج در دیتابیس
            iTaskRepository.insertAllActivityList(updatedActivityList.toEntityList())

            // حالا مجدداً دیتابیس را می‌خوانیم
            val finalEntities = iTaskRepository.getAllActivityList()
            // مثلا distinctBy بر اساس title
            val distinctEntities = finalEntities.distinctBy { it.title }
            return distinctEntities.toDomainList()
        } else {
            // 3) در صورتی که دیتابیس خالی نیست، دیگر درخواست به سرور نمی‌زنیم.
            val distinctEntities = allEntities.distinctBy { it.title }
            return distinctEntities.toDomainList()
        }
    }
}*/
