package net.archasmiel.dnd5backend.api.request.auth

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import net.archasmiel.dnd5backend.api.request.ApiRequest

@Schema(description = "Sign up request")
data class RegisterRequest(
    @field:Schema(description = "Username", example = "johndoe")
    @field:Size(min = 5, max = 50, message = "Username can be only 5-50 characters long")
    @field:NotBlank(message = "Username can't be empty")
    val username: String,

    @field:Schema(description = "Email", example = "jondoe@gmail.com")
    @field:Size(min = 5, max = 255, message = "Email must be 5-255 characters long")
    @field:NotBlank(message = "Email can't be empty")
    @field:Email(message = "Email must follow rule user@example.com")
    val email: String,

    @field:Schema(description = "Password", example = "my_1secret1_password")
    @field:Size(max = 255, message = "Password limit is 255 characters")
    val password: String
) : ApiRequest