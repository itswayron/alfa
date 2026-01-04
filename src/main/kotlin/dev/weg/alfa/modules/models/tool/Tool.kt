package dev.weg.alfa.modules.models.tool

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dev.weg.alfa.modules.models.simpleModels.Subgroup
import jakarta.persistence.*

@Entity
@Table(name = "tool")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class Tool(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val name: String,
    val description: String,
    val maximumUsages: Int,
    var actualUsages: Int,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subgroup_id", nullable = false)
    val subgroup: Subgroup,
    var isLoan: Boolean,
) {

    fun setLent(): Tool {
        if (isLoan || actualUsages >= maximumUsages) {
            // TODO: create a better way to validate toolUsage (i.e. a toolStatus variable)
            throw IllegalStateException("Tool $name ID=${id} is already loaned.")
            // TODO: Create custom exception for this case
        }
        isLoan = true
        return this
    }

    fun unsetLent(): Tool {
        if (!isLoan) {
            throw IllegalStateException("Can't return an available tool")
            // TODO: Create custom exception for this case
        }

        actualUsages++
        isLoan = false
        return this
    }
}
