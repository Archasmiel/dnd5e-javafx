package net.archasmiel.dnd5backend.api.exception.token

import net.archasmiel.dnd5backend.api.exception.ApiException

class GoogleTokenException(
    msg: String = "Google token error"
) : ApiException(
    "INVALID_GOOGLE_TOKEN",
    msg
)