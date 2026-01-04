package dev.weg.alfa.infra.audit.aspects

import dev.weg.alfa.infra.audit.model.AuditDiff
import dev.weg.alfa.infra.audit.model.AuditPayload

object AuditContext {
    private val holder = ThreadLocal<AuditDiff?>()

    fun before(value: AuditPayload?) {
        if (holder.get() != null) {
            throw IllegalStateException("AuditContext before payload is already set")
        }
        holder.set(AuditDiff(before = value, after = null))
    }

    fun after(value: AuditPayload?) {
        if (holder.get() == null) {
            throw IllegalStateException("AuditContext before payload is not set")
        }
        if (holder.get()?.after != null) {
            throw IllegalStateException("AuditContext after payload is already set")
        }
        holder.get()?.after = value
    }

    fun created(value: AuditPayload?) {
        before(null)
        after(value)
    }

    fun deleted(value: AuditPayload?) {
        before(value)
        after(null)
    }

    fun updated(beforePayload: AuditPayload?, afterPayload: AuditPayload?) {
        before(beforePayload)
        after(afterPayload)
    }

    fun get(): AuditDiff? = holder.get()

    fun consume(): AuditDiff? {
        val diff = holder.get()
        holder.remove()
        return diff
    }
}
