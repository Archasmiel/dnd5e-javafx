package net.archasmiel.dnd5backend.api.exception.token

import net.archasmiel.dnd5backend.api.exception.ApiException

class RsaKeyException(
    msg: String = "RSA key fatal error"
) : ApiException(
    "RSA_KEY_EXCEPTION",
    msg
)