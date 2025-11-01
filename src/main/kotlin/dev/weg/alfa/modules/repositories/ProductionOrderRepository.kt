package dev.weg.alfa.modules.repositories

import dev.weg.alfa.modules.models.productionOrder.ProductionOrder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductionOrderRepository : JpaRepository<ProductionOrder, Int>
