package domain.repository

import data.network.response.WorksNetworkResponse

interface IWorkRepository {
  suspend  fun fetchWorks() : List<WorksNetworkResponse>
}