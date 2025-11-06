package dev.weg.alfa.modules.repositories

import dev.weg.alfa.modules.models.businessPartner.BusinessPartner
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BusinessPartnerRepository : JpaRepository<BusinessPartner, Int>
