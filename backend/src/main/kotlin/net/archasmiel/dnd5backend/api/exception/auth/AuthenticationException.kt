package net.archasmiel.dnd5backend.api.exception.auth

import net.archasmiel.dnd5backend.api.exception.ApiException

class AuthenticationException(
    errorCode: String,
    msg: String = "Authentication error"
) : ApiException(
    errorCode,
    msg
)