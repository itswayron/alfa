package dev.weg.alfa.security.models.role

import dev.weg.alfa.security.models.role.Permission.*

object PermissionPolicy {
    private val dependencies: Map<Permission, Set<Permission>> =
        mapOf(
            VIEW_MOVEMENT to setOf(VIEW_SECTOR),

            CREATE_MOVEMENT to setOf(
                VIEW_MOVEMENT, VIEW_STOCK, VIEW_EMPLOYEE, VIEW_SECTOR,
            ),
            UPDATE_MOVEMENT to setOf(
                VIEW_MOVEMENT, VIEW_STOCK, VIEW_EMPLOYEE, VIEW_SECTOR,
            ),
            DELETE_MOVEMENT to setOf(
                VIEW_MOVEMENT, VIEW_STOCK, VIEW_EMPLOYEE, VIEW_SECTOR
            ),

            CREATE_STOCK to setOf(
                VIEW_ITEM, VIEW_SECTOR, VIEW_POSITION, VIEW_STOCK,
            ),
            UPDATE_STOCK to setOf(
                VIEW_ITEM, VIEW_SECTOR, VIEW_POSITION, VIEW_STOCK,
            ),
            DELETE_STOCK to setOf(
                VIEW_ITEM, VIEW_SECTOR, VIEW_POSITION, VIEW_STOCK,
            ),

            COMPOSITE_ITEM_CREATION to setOf(
                CREATE_STOCK, MANAGE_ITEM, MANAGE_POSITION,
            ),

            VIEW_BATCH to setOf(VIEW_MOVEMENT),
            CREATE_BATCH to setOf(VIEW_BATCH, CREATE_MOVEMENT),
            UPDATE_BATCH to setOf(VIEW_BATCH, UPDATE_MOVEMENT),
            DELETE_BATCH to setOf(VIEW_BATCH, DELETE_MOVEMENT),

            CREATE_AND_RETURN_LENDING to setOf(VIEW_LENDING),
            MANAGE_LENDING to setOf(VIEW_LENDING),

            VIEW_ROLES to setOf(VIEW_PERMISSIONS),
            MANAGE_ROLES to setOf(VIEW_ROLES),

            MANAGE_PARTNER to setOf(VIEW_PARTNER),
            MANAGE_EMPLOYEE to setOf(VIEW_EMPLOYEE),
            MANAGE_ITEM to setOf(VIEW_ITEM),
            MANAGE_USER to setOf(VIEW_USER),
            MANAGE_POSITION to setOf(VIEW_POSITION),
            MANAGE_GROUP to setOf(VIEW_GROUP),
            MANAGE_MEASUREMENT_UNIT to setOf(VIEW_MEASUREMENT_UNIT),
            MANAGE_SECTOR to setOf(VIEW_SECTOR),
            MANAGE_SUBGROUP to setOf(VIEW_SUBGROUP),
        )

    fun getDependencies(permission: Permission): Set<Permission> =
        dependencies[permission].orEmpty()
}

fun getAllRequiredPermissions(permission: Permission): Set<Permission> {
    val visited = mutableSetOf<Permission>()

    fun dfs(p: Permission) {
        if (!visited.add(p)) return
        PermissionPolicy.getDependencies(p).forEach { dfs(it) }
    }

    dfs(permission)

    return visited - permission
}
