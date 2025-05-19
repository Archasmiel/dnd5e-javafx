package net.archasmiel.dnd5backend.service

import net.archasmiel.dnd5backend.api.model.User
import net.archasmiel.dnd5backend.repository.UserRepository
import net.archasmiel.dnd5backend.api.exception.user.ResourceNotFoundException
import net.archasmiel.dnd5backend.api.exception.user.UserExistException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserService(
    private val repository: UserRepository
) {

    val userDetailsService: UserDetailsService
        get() = UserDetailsService { this.getByUsername(it) }

    fun save(user: User): User = repository.save(user)

    @Throws(UserExistException::class)
    fun create(user: User): User {
        when {
            repository.existsByUserName(user.userName) ->
                throw UserExistException(user.userName, false)

            repository.existsByEmail(user.email) ->
                throw UserExistException(user.email, true)
        }
        return save(user)
    }

    fun hasGoogleUserWithUsername(username: String): Boolean =
        repository.existsByUserNameAndGoogleUser(username, true)

    fun hasGoogleUserWithEmail(email: String): Boolean =
        repository.existsByEmailAndGoogleUser(email, true)

    fun hasUserWithUsername(username: String): Boolean =
        repository.existsByUserName(username)

    fun hasUserWithEmail(email: String): Boolean =
        repository.existsByEmail(email)

    fun findGoogleUser(email: String): User? =
        repository.findByEmailAndGoogleUser(email, true)
            .orElseGet { null }

    @Throws(ResourceNotFoundException::class)
    fun getByUsername(username: String): User =
        repository.findByUserName(username)
            .orElseThrow { ResourceNotFoundException("username", username) }

    @Throws(ResourceNotFoundException::class)
    fun getByEmail(email: String): User =
        repository.findByEmail(email)
            .orElseThrow { ResourceNotFoundException("email", email) }
}