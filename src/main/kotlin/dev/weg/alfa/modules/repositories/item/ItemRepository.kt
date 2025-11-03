package dev.weg.alfa.modules.repositories.item

import dev.weg.alfa.modules.exceptions.ExceptionProvider
import dev.weg.alfa.modules.models.item.Item
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemRepository: JpaRepository<Item, Int>, ExceptionProvider<Int>
