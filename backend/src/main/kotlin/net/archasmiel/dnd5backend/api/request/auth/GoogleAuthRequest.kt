package net.archasmiel.dnd5backend.api.request.auth

import io.swagger.v3.oas.annotations.media.Schema
import net.archasmiel.dnd5backend.api.request.ApiRequest

@Schema(description = "Sign in request via Google")
data class GoogleAuthRequest(
    @field:Schema(description = "GoogleId token", example = "eyJhbGciOiJIUzUxMiJ9...")
    val token: String
) : ApiRequest