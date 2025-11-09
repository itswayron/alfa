package dev.weg.alfa.config

object ApiRoutes {

    // Autenticação e usuário
    const val AUTH = "/auth"
    const val USER = "/user"

    // Recursos humanos
    const val EMPLOYEE = "/employee"

    // Cadastros básicos
    const val GROUP = "/group"
    const val SUBGROUP = "/subgroup"
    const val SECTOR = "/sector"
    const val MEASUREMENT_UNITS = "/measurement_unit"

    // Parceiros de negócio
    const val PARTNER = "/partner"

    // Produção e estoque
    const val ITEM = "/item"
    const val POSITION = "/position"
    const val STOCK = "/stock"
    const val PRODUCTION_ORDER = "/production_order"

    // Empréstimos
    const val TOOL = "/tool"
    const val LENDING = "/lending"
    const val LENDING_STATUS = "/lending_status"

    // Movements
    const val MOVEMENT = "/movement"
    const val MOVEMENT_STATUS = "/movement_status"
    const val MOVEMENT_TYPES = "/movement_types"
}
