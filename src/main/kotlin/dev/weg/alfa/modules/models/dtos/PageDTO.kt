package dev.weg.alfa.modules.models.dtos

data class PageDTO<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int,
)