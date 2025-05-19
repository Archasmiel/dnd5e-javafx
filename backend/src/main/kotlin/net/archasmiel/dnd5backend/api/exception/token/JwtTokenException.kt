package net.archasmiel.dnd5backend.api.exception.token

import net.archasmiel.dnd5backend.api.exception.ApiException

class JwtTokenException(
    msg: String = "JWT token error"
) : ApiException(
    "INVALID_JWT_TOKEN",
    msg
)