package dev.weg.alfa.infra.audit.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant

@Entity
data class Audit(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val actor: String,
    val action: String,

    @JdbcTypeCode(SqlTypes.JSON)
    val before: String?,

    @JdbcTypeCode(SqlTypes.JSON)
    val after: String?,

    val timestamp: Instant = Instant.now(),
)
