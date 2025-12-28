package dev.weg.alfa.modules.models.movement

enum class MovementType(
    val label: String,
    val affectsAveragePrice: Boolean,
    val quantitySign: Int,
) {
    ENTRY("Entry", true, 1),
    ENTRY_ADJUSTMENT("Entry adjustment", false, 1),
    EXIT("Exit", true, -1),
    CONSUMPTION("Consumption", false, -1),
    EXIT_ADJUSTMENT("Exit adjustment", false, -1),
    INTERNAL_MOVEMENT("Internal Movement", false, 0),
    AUDIT("Audit", false, 0);

    companion object
}

fun MovementType.Companion.getAffectingAveragePrice(): List<MovementType> {
    return MovementType.entries.filter { it.affectsAveragePrice }
}
