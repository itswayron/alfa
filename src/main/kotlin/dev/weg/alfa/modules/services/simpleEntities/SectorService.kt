package dev.weg.alfa.modules.services.simpleEntities

import dev.weg.alfa.modules.models.dtos.NameRequest
import dev.weg.alfa.modules.models.simpleModels.Sector
import dev.weg.alfa.modules.repositories.utils.findByIdOrThrow
import dev.weg.alfa.modules.repositories.simpleEntities.SectorRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SectorService(private val repository: SectorRepository) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createSector(request: NameRequest): Sector {
        logger.info("Creating Sector with name: ${request.name}")
        val response = repository.save(Sector(name = request.name))
        return response
    }

    fun getAllSectors(): List<Sector> {
        logger.info("Retrieving all sectors from the database")
        val response = repository.findAll()
        logger.info("Found ${response.size} sectors on the database")
        return response
    }

    fun editSector(command: Pair<Int, NameRequest>): Sector {
        val (id, newSector) = command
        logger.info("Update Sector with $id with name:${newSector.name}")
        val oldSector = repository.findByIdOrThrow(id)
        val response = repository.save(Sector(id = oldSector.id, name = newSector.name))
        logger.info("Sector name update to ${newSector.name}")
        return response
    }

    fun deleteSectorById(id: Int) {
        logger.info("Deleting Sector With Id $id")
        val delete = repository.findByIdOrThrow(id)
        repository.delete(delete)
    }
}
