package dev.weg.alfa.security.models.role

import jakarta.persistence.*

@Entity
@Table(name = "roles")
data class Role(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(name = "name", unique = true, nullable = false)
    val name: String,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "role_permissions",
        joinColumns = [JoinColumn(name = "role_id")],
    )
    @Column(name = "permission", nullable = false)
    @Enumerated(EnumType.STRING)
    val permissions: Set<Permission> = emptySet()
)
