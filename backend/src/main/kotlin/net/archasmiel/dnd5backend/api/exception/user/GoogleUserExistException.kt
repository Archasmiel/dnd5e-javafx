package net.archasmiel.dnd5backend.api.exception.user

import net.archasmiel.dnd5backend.api.exception.ApiException

class GoogleUserExistException(
    usernameOrEmail: String,
    isEmail: Boolean
) : ApiException(
    "GOOGLE_USER_EXIST",
    "user by ${if (isEmail) "email" else "username"}: $usernameOrEmail"
)