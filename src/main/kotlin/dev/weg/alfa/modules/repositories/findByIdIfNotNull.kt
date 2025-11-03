package dev.weg.alfa.modules.repositories

import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.JpaRepository

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
