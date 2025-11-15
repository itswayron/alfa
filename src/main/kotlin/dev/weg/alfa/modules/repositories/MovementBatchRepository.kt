package dev.weg.alfa.modules.repositories

import dev.weg.alfa.modules.models.movementBatch.MovementBatch
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface MovementBatchRepository : JpaRepository<MovementBatch, Int>, JpaSpecificationExecutor<MovementBatch> {
    fun findByCode(code: String): MovementBatch?
}
