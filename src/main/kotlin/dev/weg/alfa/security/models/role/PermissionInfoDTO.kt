package dev.weg.alfa.security.models.role

data class PermissionInfoDTO(
    val name: String,
    val dependencies: Set<String>,
)

data class PermissionScopeDTO(
    val scope: PermissionScope,
    val permissions: List<PermissionInfoDTO>,
)

fun Permission.toInfoDTO(): PermissionInfoDTO {
    return PermissionInfoDTO(
        name = this.name,
        dependencies = PermissionPolicy.getDependencies(this).map { it.name }.toSet()
    )
}
