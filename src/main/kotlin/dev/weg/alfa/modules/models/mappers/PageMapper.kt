package dev.weg.alfa.modules.models.mappers

import dev.weg.alfa.modules.models.dtos.PageDTO
import org.springframework.data.domain.Page

fun <T> Page<T>.toDTO(): PageDTO<T> = PageDTO(
    content = this.content,
    totalElements = this.totalElements,
    totalPages = this.totalPages,
    currentPage = this.number,
    pageSize = this.size
)
