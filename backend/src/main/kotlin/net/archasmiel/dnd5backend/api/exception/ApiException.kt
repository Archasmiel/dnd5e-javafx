package net.archasmiel.dnd5backend.api.exception

abstract class ApiException(
    val errorCode: String,
    val msg: String,
    cause: Throwable? = null
) : RuntimeException(
    msg,
    cause
)