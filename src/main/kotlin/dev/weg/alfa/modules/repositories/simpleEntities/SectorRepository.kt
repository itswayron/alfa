package dev.weg.alfa.modules.repositories.simpleEntities

import dev.weg.alfa.modules.models.simpleModels.Sector
import org.springframework.data.jpa.repository.JpaRepository

interface SectorRepository : JpaRepository< Sector, Int>

