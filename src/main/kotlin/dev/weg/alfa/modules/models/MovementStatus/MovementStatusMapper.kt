package dev.weg.alfa.modules.models.MovementStatus

fun MovementStatus.toResponse(): MovementStatusResponse {

        return MovementStatusResponse(
            id = this.id,
            status = this.status
        )
}
fun MovementStatusRequest.toEntity(): MovementStatus {

    return MovementStatus(
        status = this.status
    )
}