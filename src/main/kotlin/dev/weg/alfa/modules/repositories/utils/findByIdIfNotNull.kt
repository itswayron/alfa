package dev.weg.alfa.modules.repositories.utils

import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.JpaRepository

// TODO: Unit Test : findByIdIfNotNull should return null when id is null and never call repository
// TODO: Unit Test : findByIdIfNotNull should return entity when repository returns value
// TODO: Unit Test : findByIdIfNotNull should return null when repository returns empty and log warning
inline fun <reified T : Any, ID : Any> JpaRepository<T, ID>.findByIdIfNotNull(id: ID?): T? {
    if (id == null) return null
    val callerClassName = Throwable().stackTrace[1].className
    val logger = LoggerFactory.getLogger(Class.forName(callerClassName))
    val entityName = T::class.simpleName

    logger.debug("Attempting to find {} with nullable ID: {}", entityName, id)

    val result = this.findById(id).orElse(null)

    return if (result != null) {
        logger.debug("Successfully found {} with ID: {}", entityName, id)
        result
    } else {
        logger.warn("{} with id: {} was not found (nullable lookup).", entityName, id)
        null
    }
}
