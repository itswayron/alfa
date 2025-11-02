package dev.weg.alfa.modules.repositories

import dev.weg.alfa.modules.models.position.Position
import org.springframework.data.jpa.repository.JpaRepository

interface PositionRepository : JpaRepository<Position, Int>{
    fun findByFloorField(floor: String): List<Position>
    fun findBySideField(side: String): List<Position>
    fun findByColumnField(floor: String): List<Position>
    fun findByBoxField(floor: String): List<Position>
}
