package net.archasmiel.dnd5backend.util

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import lombok.RequiredArgsConstructor
import net.archasmiel.dnd5backend.api.exception.token.GoogleTokenException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.IOException
import java.security.GeneralSecurityException

@Component
@RequiredArgsConstructor
class GoogleTokenVerifier(
    @Value("\${spring.security.oauth2.client.registration.google.client-id}")
    private val googleClientId: String,

    @Value("\${spring.security.oauth2.client.registration.google.client-secret}")
    private val googleClientSecret: String
) {

    private val googleVerifier by lazy {
        GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory())
            .setAudience(listOf(googleClientId))
            .build()
    }

    fun getGoogleTokenVerifier(): GoogleIdTokenVerifier = googleVerifier

    @Throws(GoogleTokenException::class)
    fun extractToken(googleToken: String): GoogleIdToken {
        try {
            val idToken = googleVerifier.verify(googleToken)
                ?: throw GoogleTokenException("Token malformed, expired, system time difference")
            return idToken
        } catch (ex: GeneralSecurityException) {
            throw GoogleTokenException("Server security problem")
        } catch (ex: IOException) {
            throw GoogleTokenException("General I/O problem")
        }
    }

}