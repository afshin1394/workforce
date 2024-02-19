package utils

import domain.usecase.ResultStatus

sealed interface AsyncStatus{
    data object  SUCCESS : AsyncStatus
    data object  ERROR : AsyncStatus
    data object  LOADING : AsyncStatus

}


sealed class AsyncResult <out T> (val status: AsyncStatus, val data: T?, val message:String?,val resultStatus:  ResultStatus?) {

    data class Success<out R>(val _data: R?,val _resultStatus : ResultStatus): AsyncResult<R>(
        status = AsyncStatus.SUCCESS,
        data = _data,
        message = null,
        resultStatus = _resultStatus
    )

    data class Error(val exception: String,val _resultStatus : ResultStatus): AsyncResult<Nothing>(
        status = AsyncStatus.ERROR,
        data = null,
        message = exception,
        resultStatus = _resultStatus
    )

    data class Loading<out R>(val _data: R?, val isLoading: Boolean): AsyncResult<R>(
        status = AsyncStatus.LOADING,
        data = _data,
        message = null,
        resultStatus = null
    )
}