package dev.weg.alfa.modules.repositories

import dev.weg.alfa.modules.models.stock.Stock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockRepository : JpaRepository<Stock, Int>
