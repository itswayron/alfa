package dev.weg.alfa.modules.controllers

import dev.weg.alfa.config.ApiRoutes
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping(ApiRoutes.PING)
class PingController {

    @GetMapping
    fun ping(): Any = object {
        val message = "pong!"
        val time = Instant.now()
    }
}
