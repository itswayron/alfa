package dev.weg.alfa.modules.repositories.simpleEntities

import dev.weg.alfa.modules.models.simpleModels.MeasurementUnity
import org.springframework.data.jpa.repository.JpaRepository

interface MeasurementUnityRepository : JpaRepository<MeasurementUnity, Int>