package net.archasmiel.dnd5backend.api.exception.token

import net.archasmiel.dnd5backend.api.exception.ApiException

class BearerTokenException(
    msg: String = "Bearer token error"
) : ApiException(
    "INVALID_BEARER_TOKEN",
    msg
)