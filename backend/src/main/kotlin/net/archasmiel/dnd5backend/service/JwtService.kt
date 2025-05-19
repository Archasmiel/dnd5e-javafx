package net.archasmiel.dnd5backend.service

import net.archasmiel.dnd5backend.api.model.User
import net.archasmiel.dnd5backend.api.exception.token.JwtTokenException
import net.archasmiel.dnd5backend.api.exception.token.RsaKeyException
import org.jose4j.jws.AlgorithmIdentifiers
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.NumericDate
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.io.IOException
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

@Service
class JwtService(
    @Value("\${jwt.token.expiration}")
    private val jwtExpirationMs: Long,

    @Value("\${jwt.keys.path}")
    private val keyPath: String
) {
    private var privateKey: PrivateKey? = null
    private var publicKey: PublicKey? = null

    @Throws(RsaKeyException::class)
    private fun getSigningKey(): PrivateKey {
        return privateKey ?: try {
            ClassPathResource("$keyPath/private.pem").inputStream.use { keyStream ->
                val privateKeyContent = keyStream.readAllBytes()
                    .decodeToString()
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replace("\\s".toRegex(), "")

                val keyBytes = Base64.getDecoder().decode(privateKeyContent)
                val spec = PKCS8EncodedKeySpec(keyBytes)
                KeyFactory.getInstance("RSA").generatePrivate(spec).also {
                    privateKey = it
                }
            }
        } catch (e: InvalidKeySpecException) {
            throw RsaKeyException("Invalid private key")
        } catch (e: NoSuchAlgorithmException) {
            throw RsaKeyException("Algorithm not found")
        } catch (e: IOException) {
            throw RsaKeyException("Failed to read private key")
        }
    }

    @Throws(RsaKeyException::class)
    private fun getVerificationKey(): PublicKey {
        return publicKey ?: try {
            ClassPathResource("$keyPath/public.pem").inputStream.use { keyStream ->
                val publicKeyContent = keyStream.readAllBytes()
                    .decodeToString()
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replace("\\s".toRegex(), "")

                val keyBytes = Base64.getDecoder().decode(publicKeyContent)
                val spec = X509EncodedKeySpec(keyBytes)
                KeyFactory.getInstance("RSA").generatePublic(spec).also { publicKey = it }
            }
        } catch (e: InvalidKeySpecException) {
            throw RsaKeyException("Invalid public key")
        } catch (e: NoSuchAlgorithmException) {
            throw RsaKeyException("Algorithm not found")
        } catch (e: IOException) {
            throw RsaKeyException("Failed to read public key")
        }
    }

    @Throws(JwtTokenException::class)
    fun generateToken(userDetails: UserDetails): String {
        val claims = JwtClaims().apply {
            subject = userDetails.username
            issuedAt = NumericDate.now()
            expirationTime = NumericDate.fromMilliseconds(
                System.currentTimeMillis() + jwtExpirationMs
            )

            if (userDetails is User) {
                setClaim("id", userDetails.id)
                setClaim("email", userDetails.email)
                setClaim("role", userDetails.role)
            }
        }

        return JsonWebSignature().apply {
            payload = claims.toJson()
            key = getSigningKey()
            algorithmHeaderValue = AlgorithmIdentifiers.RSA_USING_SHA256
        }.compactSerialization ?: throw JwtTokenException("Failed to generate JWT")
    }

    @Throws(JwtTokenException::class)
    fun extractAllClaims(token: String): JwtClaims {
        return try {
            JwtConsumerBuilder()
                .setVerificationKey(getVerificationKey())
                .build()
                .processToClaims(token)
        } catch (e: Exception) {
            throw JwtTokenException("Could not extract claims")
        }
    }

    @Throws(JwtTokenException::class)
    private fun extractExpiration(token: String): NumericDate {
        return extractAllClaims(token).expirationTime
    }

    @Throws(JwtTokenException::class)
    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).isBefore(NumericDate.now())
    }

    @Throws(JwtTokenException::class)
    fun extractUsername(token: String): String {
        return extractAllClaims(token).subject
    }

    @Throws(JwtTokenException::class)
    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        return extractUsername(token) == userDetails.username && !isTokenExpired(token)
    }
}