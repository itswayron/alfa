package dev.weg.alfa.infra.audit.model

object GroupAction {
    const val CREATED = "GROUP_CREATED"
    const val UPDATED = "GROUP_UPDATED"
    const val DELETED = "GROUP_DELETED"
}

object UnitAction {
    const val CREATED = "MEASUREMENT_UNIT_CREATED"
    const val UPDATED = "MEASUREMENT_UNIT_UPDATED"
    const val DELETED = "MEASUREMENT_UNIT_DELETED"
}

object SectorAction {
    const val CREATED = "SECTOR_CREATED"
    const val UPDATED = "SECTOR_UPDATED"
    const val DELETED = "SECTOR_DELETED"
}

object SubgroupAction {
    const val CREATED = "SUBGROUP_CREATED"
    const val UPDATED = "SUBGROUP_UPDATED"
    const val DELETED = "SUBGROUP_DELETED"
}

object PartnerAction {
    const val CREATED = "BUSINESS_PARTNER_CREATED"
    const val UPDATED = "BUSINESS_PARTNER_UPDATED"
    const val DELETED = "BUSINESS_PARTNER_DELETED"
}

object EmployeeAction {
    const val CREATED = "EMPLOYEE_CREATED"
    const val UPDATED = "EMPLOYEE_UPDATED"
    const val DELETED = "EMPLOYEE_DELETED"
}

object LendingAction {
    const val CREATED = "LENDING_CREATED"
    const val DELETED = "LENDING_DELETED"
    const val RETURNED = "LENDING_RETURNED"
}

object ToolAction {
    const val CREATED = "TOOL_CREATED"
    const val UPDATED = "TOOL_UPDATED"
    const val DELETED = "TOOL_DELETED"
}

object PositionAction {
    const val CREATED = "POSITION_CREATED"
    const val UPDATED = "POSITION_UPDATED"
    const val DELETED = "POSITION_DELETED"
}

object StockAction {
    const val CREATED = "STOCK_CREATED"
    const val UPDATED = "STOCK_UPDATED"
    const val DELETED = "STOCK_DELETED"
}

object MovementAction {
    const val CREATED = "MOVEMENT_CREATED"
    const val UPDATED = "MOVEMENT_UPDATED"
    const val DELETED = "MOVEMENT_DELETED"
}

object BatchAction {
    const val CREATED = "MOVEMENT_BATCH_CREATED"
    const val UPDATED = "MOVEMENT_BATCH_UPDATED"
    const val DELETED = "MOVEMENT_BATCH_DELETED"
}

object ItemAction {
    const val CREATED = "ITEM_CREATED"
    const val UPDATED = "ITEM_UPDATED"
    const val DELETED = "ITEM_DELETED"
    const val IMAGE_UPLOADED = "ITEM_IMAGE_UPLOADED"
    const val IMAGE_DELETED = "ITEM_IMAGE_DELETED"
}
