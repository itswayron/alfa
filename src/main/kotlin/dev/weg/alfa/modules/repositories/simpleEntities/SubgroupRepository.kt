package dev.weg.alfa.modules.repositories.simpleEntities

import dev.weg.alfa.modules.models.simpleModels.Subgroup
import org.springframework.data.jpa.repository.JpaRepository

interface SubgroupRepository : JpaRepository<Subgroup, Int>
