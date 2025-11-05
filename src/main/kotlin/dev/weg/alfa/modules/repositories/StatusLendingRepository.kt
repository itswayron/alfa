package dev.weg.alfa.modules.repositories

import dev.weg.alfa.modules.models.simpleModels.StatusLending
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StatusLendingRepository : JpaRepository <StatusLending, Int>