package net.archasmiel.dnd5backend.service

import net.archasmiel.dnd5backend.api.request.auth.GoogleAuthRequest
import net.archasmiel.dnd5backend.api.request.auth.LoginRequest
import net.archasmiel.dnd5backend.api.response.auth.JwtAuthResponse
import net.archasmiel.dnd5backend.api.exception.auth.AuthenticationException
import net.archasmiel.dnd5backend.api.exception.token.GoogleTokenException
import net.archasmiel.dnd5backend.api.exception.token.JwtTokenException
import net.archasmiel.dnd5backend.api.exception.user.GoogleUserExistException
import net.archasmiel.dnd5backend.api.exception.user.UserExistException
import net.archasmiel.dnd5backend.util.GoogleTokenVerifier
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val registrationService: RegistrationService,
    private val userService: UserService,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val verifier: GoogleTokenVerifier
) {

    @Throws(
        GoogleUserExistException::class,
        AuthenticationException::class,
        UsernameNotFoundException::class,
        JwtTokenException::class,)
    fun signIn(request: LoginRequest): JwtAuthResponse {
        if (userService.hasGoogleUserWithUsername(request.username)) {
            throw GoogleUserExistException(request.username, false)
        }

        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    request.username,
                    request.password
                )
            )
        } catch (e: Exception) {
            throw AuthenticationException("AUTH_FAILED", e.localizedMessage)
        }

        val user = userService.userDetailsService.loadUserByUsername(request.username)
        val jwt = jwtService.generateToken(user)
        return JwtAuthResponse(jwt)
    }

    @Throws(
        GoogleTokenException::class,
        JwtTokenException::class,
        UserExistException::class)
    fun signIn(request: GoogleAuthRequest): JwtAuthResponse {
        val email = verifier.extractToken(request.token).payload.email

        val user = userService.findGoogleUser(email)
            ?: return registrationService.signUp(email)

        val jwt = jwtService.generateToken(user)
        return JwtAuthResponse(jwt)
    }
}