package dev.weg.alfa.modules.models.MovementStatus

fun MovementStatus.toResponse(): MovementStatusResponse {

        return MovementStatusResponse(
            id = this.id,
            status = this.name
        )
}
fun MovementStatusRequest.toEntity(): MovementStatus {

    return MovementStatus(
        name = this.status
    )
}