package net.archasmiel.dnd5backend.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource("classpath:.env")
class EnvConfig