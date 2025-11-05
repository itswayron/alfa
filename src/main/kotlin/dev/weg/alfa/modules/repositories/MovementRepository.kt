package dev.weg.alfa.modules.repositories

import dev.weg.alfa.modules.models.movement.Movement
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MovementRepository : JpaRepository<Movement, Int>
