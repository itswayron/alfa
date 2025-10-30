package dev.weg.alfa.modules.repositories.simpleEntities

import dev.weg.alfa.modules.models.simpleModels.Group
import org.springframework.data.jpa.repository.JpaRepository

interface GroupRepository : JpaRepository<Group, Int>