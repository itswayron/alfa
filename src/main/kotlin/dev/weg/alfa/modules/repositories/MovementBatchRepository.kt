package dev.weg.alfa.modules.repositories

import dev.weg.alfa.modules.models.movementBatch.MovementBatch
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MovementBatchRepository : JpaRepository<MovementBatch, Int> {
    fun findByCode(code: String): MovementBatch?

    @Query(
        """
        SELECT * FROM movement_batch mb
        WHERE (:code IS NULL OR mb.code = :code)
        AND (:document IS NULL OR mb.document = :document)
        AND (:text IS NULL OR (
            LOWER(mb.code) LIKE LOWER(CONCAT('%', :text, '%'))
            OR LOWER(mb.document) LIKE LOWER(CONCAT('%', :text, '%'))
        ))
        ORDER BY mb.date DESC
        """,
        nativeQuery = true
    )
    fun findFiltered(
        @Param("code") code: String?,
        @Param("document") document: String?,
        @Param("text") text: String?,
        pageable: Pageable,
    ): List<MovementBatch>
}
