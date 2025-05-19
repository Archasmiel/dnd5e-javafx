package net.archasmiel.dnd5backend.api.response.auth

import io.swagger.v3.oas.annotations.media.Schema
import net.archasmiel.dnd5backend.api.response.ApiResponse

@Schema(description = "Response with user details")
data class MeResponse(
    @field:Schema(description = "Username", example = "john_doe")
    val username: String,

    @field:Schema(description = "Some details, not exactly UserDetails", example = "User is logged in")
    val details: String
) : ApiResponse