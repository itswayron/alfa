package dev.weg.alfa.infra.audit.repository

import dev.weg.alfa.infra.audit.model.Audit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface AuditRepository : JpaRepository<Audit, Long>, JpaSpecificationExecutor<Audit>
