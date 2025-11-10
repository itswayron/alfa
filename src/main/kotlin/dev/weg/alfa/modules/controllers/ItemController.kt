package dev.weg.alfa.modules.controllers

import dev.weg.alfa.config.ApiRoutes
import dev.weg.alfa.modules.models.dtos.PageDTO
import dev.weg.alfa.modules.models.item.ItemPatch
import dev.weg.alfa.modules.models.item.ItemRequest
import dev.weg.alfa.modules.models.item.ItemResponse
import dev.weg.alfa.modules.services.ItemService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(ApiRoutes.ITEM)
class ItemController(private val service: ItemService) {
    @PostMapping
    fun createItem(@RequestBody item: ItemRequest): ResponseEntity<ItemResponse> {
        val response = service.createItem(item)
        return ResponseEntity(response, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getItemById(@PathVariable id: Int): ResponseEntity<ItemResponse> {
        val response = service.getItemById(id)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping
    fun getAllItems(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "description") sort: String,
        @RequestParam(defaultValue = "DESC") direction: String,
    ): ResponseEntity<PageDTO<ItemResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort))
        val response = service.getItems(pageable)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PatchMapping("/{id}")
    fun updateItem(
        @PathVariable id: Int, @RequestBody patch: ItemPatch
    ): ResponseEntity<ItemResponse> {
        val response = service.updateItem(Pair(id, patch))
        return ResponseEntity(response, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteItem(
        @PathVariable id: Int
    ): ResponseEntity<Unit> {
        service.deleteItem(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @PostMapping("/{id}/image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadImage(
        @PathVariable id: Int,
        @RequestPart("image") image: MultipartFile,
    ): ResponseEntity<Unit> {
        service.uploadItemImage(id, image)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
