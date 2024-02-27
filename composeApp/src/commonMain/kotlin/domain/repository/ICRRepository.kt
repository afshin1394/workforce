package domain.repository

import data.network.response.cr.CRNetworkResponse

interface ICRRepository {

    suspend fun fetchAllCR(queryParam : String = "close_cr,open_cr") : List<CRNetworkResponse>
}