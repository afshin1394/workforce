package data.network

interface INetworkObject {
    fun hasNullProperty() : Boolean
    fun areAllMembersNull() : Boolean
}