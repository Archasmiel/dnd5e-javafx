package net.archasmiel.dnd5backend.controller

import net.archasmiel.dnd5backend.api.request.auth.GoogleAuthRequest
import net.archasmiel.dnd5backend.api.request.auth.LoginRequest
import net.archasmiel.dnd5backend.api.request.auth.RegisterRequest
import net.archasmiel.dnd5backend.api.response.ApiResponse
import net.archasmiel.dnd5backend.api.response.auth.MeResponse
import net.archasmiel.dnd5backend.api.exception.auth.AuthenticationException
import net.archasmiel.dnd5backend.api.exception.token.GoogleTokenException
import net.archasmiel.dnd5backend.api.exception.token.JwtTokenException
import net.archasmiel.dnd5backend.api.exception.user.GoogleUserExistException
import net.archasmiel.dnd5backend.api.exception.user.UserExistException
import net.archasmiel.dnd5backend.config.security.SecurityContext
import net.archasmiel.dnd5backend.service.AuthenticationService
import net.archasmiel.dnd5backend.service.RegistrationService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthenticationService,
    private val registerService: RegistrationService
) {

    @Throws(
        JwtTokenException::class,
        UserExistException::class)
    @PostMapping("/register")
    fun signup(@RequestBody request: RegisterRequest): ResponseEntity<ApiResponse> {
        return ResponseEntity.ok(
            registerService.signUp(request)
        )
    }

    @Throws(
        AuthenticationException::class,
        UsernameNotFoundException::class,
        JwtTokenException::class,
        GoogleUserExistException::class)
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<ApiResponse> {
        return ResponseEntity.ok(
            authService.signIn(request)
        )
    }

    @Throws(
        UserExistException::class,
        JwtTokenException::class,
        GoogleTokenException::class)
    @PostMapping("/google")
    fun loginGoogle(@RequestBody request: GoogleAuthRequest): ResponseEntity<ApiResponse> {
        return ResponseEntity.ok(
            authService.signIn(request)
        )
    }

    @GetMapping("/me")
    fun getMe(@SecurityContext user: UserDetails): ResponseEntity<ApiResponse> {
        return ResponseEntity.ok(
            MeResponse(user.username, "Authorities: ${user.authorities}")
        )
    }

    @GetMapping("/logout")
    fun logout(@SecurityContext user: UserDetails): ResponseEntity<ApiResponse> {
        return ResponseEntity.ok(
            MeResponse(user.username, "Successfully logged out")
        )
    }
}