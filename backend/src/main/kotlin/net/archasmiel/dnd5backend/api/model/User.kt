package net.archasmiel.dnd5backend.api.model

import jakarta.persistence.*
import net.archasmiel.dnd5backend.api.request.auth.RegisterRequest
import net.archasmiel.dnd5backend.api.model.enums.Role
import net.archasmiel.dnd5backend.util.UUIDGenerator
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder

@Entity
@Table(name = "users")
data class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "username", unique = true, nullable = false)
    val userName: String,

    @Column(name = "email", unique = true, nullable = false)
    val email: String,

    @Column(name = "password", nullable = false)
    private val passWord: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    val role: Role,

    @Column(name = "is_google", nullable = false)
    val googleUser: Boolean = false

) : UserDetails {

    companion object {
        fun fromGoogleEmail(
            email: String,
            passwordEncoder: PasswordEncoder,
            uuidGenerator: UUIDGenerator
        ): User {
            return User(
                id = null,
                userName = uuidGenerator.username(),
                email = email,
                passWord = passwordEncoder.encode(uuidGenerator.password()),
                role = Role.ROLE_USER,
                googleUser = true
            )
        }

        fun fromRegisterUser(
            request: RegisterRequest,
            passwordEncoder: PasswordEncoder
        ): User {
            return User(
                id = null,
                userName = request.username,
                email = request.email,
                passWord = passwordEncoder.encode(request.password),
                role = Role.ROLE_USER,
                googleUser = false
            )
        }
    }


    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority(role.name))
    }

    override fun getPassword(): String = passWord

    override fun getUsername(): String = userName

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}