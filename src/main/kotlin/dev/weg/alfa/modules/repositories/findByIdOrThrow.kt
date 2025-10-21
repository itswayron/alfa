package dev.weg.alfa.modules.repositories

import org.slf4j.LoggerFactory
import dev.weg.alfa.modules.exceptions.ExceptionProvider
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.jpa.repository.JpaRepository

inline fun <reified T : Any, ID : Any> JpaRepository<T, ID>.findByIdOrThrow(id: ID): T {
    val callerClassName = Throwable().stackTrace[1].className
    val logger = LoggerFactory.getLogger(Class.forName(callerClassName))
    val entityName = T::class.simpleName

    logger.debug("Searching for {} with ID: {}", entityName, id)
    return this.findById(id).orElseThrow {
        logger.error("$entityName with id: $id does not exist.")
        if (this is ExceptionProvider<*>) {
            @Suppress("UNCHECKED_CAST")
            (this as ExceptionProvider<ID>).notFoundException(id)
        } else {
            EntityNotFoundException("$entityName with id: $id does not exist.")
        }
    }.also {
        logger.debug("Successfully found {} with ID: {}", entityName, id)
    }
}
