package dev.weg.alfa.modules.repositories.simpleEntities
import dev.weg.alfa.modules.models.MovementStatus.Status
import dev.weg.alfa.modules.models.MovementStatus.MovementStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MovementStatausRepository : JpaRepository<MovementStatus, Int>{
    fun findByStatus(status: Status): List<MovementStatus>
}
