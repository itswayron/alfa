package dev.weg.alfa.modules.repositories

import dev.weg.alfa.modules.models.lending.Lending
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LendingRepository : JpaRepository<Lending, Int>
