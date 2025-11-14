package dev.weg.alfa.modules.models.movementBatch

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dev.weg.alfa.modules.models.businessPartner.BusinessPartner
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "movement_batch")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class MovementBatch(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column(unique = true)
    val code: String,
    val document: String?,
    val observation: String? = null,
    val date: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_partner_id")
    val businessPartner: BusinessPartner? = null,
)
