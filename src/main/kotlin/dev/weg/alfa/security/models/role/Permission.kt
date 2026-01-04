package dev.weg.alfa.security.models.role

enum class PermissionScope {
    BUSINESS_PARTNER,
    EMPLOYEE,
    ITEM,
    LENDING,
    USER,
    MOVEMENT,
    MOVEMENT_BATCH,
    POSITION,
    GROUP,
    MEASUREMENT_UNIT,
    SECTOR,
    SUBGROUP,
    STOCK,
    ROLES_AND_PERMISSIONS,
    AUDIT,
}

enum class Permission(
    val scope: PermissionScope,
) {
    VIEW_PARTNER(PermissionScope.BUSINESS_PARTNER),
    MANAGE_PARTNER(PermissionScope.BUSINESS_PARTNER),

    VIEW_EMPLOYEE(PermissionScope.EMPLOYEE),
    MANAGE_EMPLOYEE(PermissionScope.EMPLOYEE),

    VIEW_ITEM(PermissionScope.ITEM),
    MANAGE_ITEM(PermissionScope.ITEM),

    VIEW_LENDING(PermissionScope.LENDING),
    CREATE_AND_RETURN_LENDING(PermissionScope.LENDING),
    MANAGE_LENDING(PermissionScope.LENDING),

    VIEW_USER(PermissionScope.USER),
    MANAGE_USER(PermissionScope.USER),

    VIEW_MOVEMENT(PermissionScope.MOVEMENT),
    CREATE_MOVEMENT(PermissionScope.MOVEMENT),
    UPDATE_MOVEMENT(PermissionScope.MOVEMENT),
    DELETE_MOVEMENT(PermissionScope.MOVEMENT),

    VIEW_BATCH(PermissionScope.MOVEMENT_BATCH),
    CREATE_BATCH(PermissionScope.MOVEMENT_BATCH),
    UPDATE_BATCH(PermissionScope.MOVEMENT_BATCH),
    DELETE_BATCH(PermissionScope.MOVEMENT_BATCH),

    VIEW_POSITION(PermissionScope.POSITION),
    MANAGE_POSITION(PermissionScope.POSITION),

    VIEW_GROUP(PermissionScope.GROUP),
    MANAGE_GROUP(PermissionScope.GROUP),

    VIEW_MEASUREMENT_UNIT(PermissionScope.MEASUREMENT_UNIT),
    MANAGE_MEASUREMENT_UNIT(PermissionScope.MEASUREMENT_UNIT),

    VIEW_SECTOR(PermissionScope.SECTOR),
    MANAGE_SECTOR(PermissionScope.SECTOR),

    VIEW_SUBGROUP(PermissionScope.SUBGROUP),
    MANAGE_SUBGROUP(PermissionScope.SUBGROUP),

    VIEW_STOCK(PermissionScope.STOCK),
    CREATE_STOCK(PermissionScope.STOCK),
    UPDATE_STOCK(PermissionScope.STOCK),
    DELETE_STOCK(PermissionScope.STOCK),

    COMPOSITE_ITEM_CREATION(PermissionScope.SECTOR),

    VIEW_PERMISSIONS(PermissionScope.ROLES_AND_PERMISSIONS),
    VIEW_ROLES(PermissionScope.ROLES_AND_PERMISSIONS),
    MANAGE_ROLES(PermissionScope.ROLES_AND_PERMISSIONS),

    VIEW_AUDITS(PermissionScope.AUDIT),

    ;
}
