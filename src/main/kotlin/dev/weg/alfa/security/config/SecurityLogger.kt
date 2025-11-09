package dev.weg.alfa.security.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
object SecurityLogger {
    val log : Logger = LoggerFactory.getLogger("SECURITY")
}
