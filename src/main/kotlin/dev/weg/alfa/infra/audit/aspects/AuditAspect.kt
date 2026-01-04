package dev.weg.alfa.infra.audit.aspects

import dev.weg.alfa.infra.audit.annotations.Auditable
import dev.weg.alfa.infra.audit.services.AuditService
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class AuditAspect(
    private val service: AuditService,
) {
    @Around("@annotation(auditable)")
    fun audit(jointPoint: ProceedingJoinPoint, auditable: Auditable): Any? {
        try {
            jointPoint.args
            return jointPoint.proceed()
        } finally {
            val payload = AuditContext.consume()

            if (payload != null) {
                service.save(
                    action = auditable.action,
                    diff = payload
                )
            }
        }
    }
}
