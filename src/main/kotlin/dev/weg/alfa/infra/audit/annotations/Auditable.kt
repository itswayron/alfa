package dev.weg.alfa.infra.audit.annotations

/**
 * Marks a function as auditable, causing its execution to be intercepted
 * by [dev.weg.alfa.infra.audit.aspects.AuditAspect] in order to persist an audit entry after the method
 * completes.
 *
 * ### Audit data contract
 * This annotation **does not collect audit data by itself**.
 *  The annotated function (or lower layers it calls)
 *  must explicitly populate [dev.weg.alfa.infra.audit.aspects.AuditContext]
 *  by invoking one of its methods. Failing to populate the context results in no audit record being saved.
 *
 *  Audit persistence is attempted after method execution, even if an exception
 *  is thrown.
 *
 * @param action A domain-level identifier describing the state-changing operation being performed.
 * This value is persisted verbatim and is expected to be stable, explicit,
 * and suitable for long-term auditing.
 *
 * @see dev.weg.alfa.infra.audit.aspects.AuditAspect
 * @see dev.weg.alfa.infra.audit.aspects.AuditContext
 * @see dev.weg.alfa.infra.audit.services.AuditService
 * */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Auditable(
    val action: String
)
