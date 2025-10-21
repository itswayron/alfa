package dev.weg.alfa.modules.repositories.businessPartner

import dev.weg.alfa.modules.exceptions.ExceptionProvider
import dev.weg.alfa.modules.models.businessPartner.BusinessPartner
import org.springframework.data.jpa.repository.JpaRepository

interface BusinessPartnerRepository : JpaRepository<BusinessPartner, Int>, ExceptionProvider<Int>
