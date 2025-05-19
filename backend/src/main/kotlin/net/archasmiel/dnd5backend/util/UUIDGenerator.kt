package net.archasmiel.dnd5backend.util

import org.springframework.stereotype.Component
import java.util.*

@Component
class UUIDGenerator {

    private companion object {
        const val DEFAULT_SECTIONS = 2
    }

    fun fromUUID(sections: Int): String =
        (1..sections).joinToString(separator = "")
        { UUID.randomUUID().toString().take(8) }

    fun username(): String = "user_${fromUUID(DEFAULT_SECTIONS)}"

    fun password(): String = fromUUID(DEFAULT_SECTIONS)

}