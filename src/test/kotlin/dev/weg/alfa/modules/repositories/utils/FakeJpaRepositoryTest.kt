package dev.weg.alfa.modules.repositories.utils

import org.springframework.data.domain.Example
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.query.FluentQuery
import java.util.*
import java.util.function.Function

open class FakeJpaRepository<T : Any, ID : Any>(private val findByIdAnswer: (ID) -> Optional<T>) : JpaRepository<T, ID>,
    JpaSpecificationExecutor<T> {

    override fun findById(id: ID): Optional<T> = findByIdAnswer(id)

    override fun getReferenceById(id: ID): T {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    @Deprecated("Deprecated in Java")
    override fun getById(id: ID): T {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    @Deprecated("Deprecated in Java")
    override fun getOne(id: ID): T {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun <S : T> save(entity: S): S {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun <S : T> saveAndFlush(entity: S): S {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun <S : T> saveAll(entities: MutableIterable<S>): MutableList<S> {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun <S : T> saveAllAndFlush(entities: MutableIterable<S>): MutableList<S> {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun flush() {
        // keep Unit return type (avoid Nothing inference)
    }

    override fun deleteAllInBatch() {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun deleteAllInBatch(entities: MutableIterable<T>) {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun deleteAllByIdInBatch(ids: MutableIterable<ID>) {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    @Deprecated("Deprecated in Java")
    override fun deleteInBatch(entities: MutableIterable<T>) {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun delete(entity: T) {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun deleteAllById(ids: Iterable<ID>) {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun deleteAll(entities: MutableIterable<T>) {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun deleteAll() {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun deleteById(id: ID) {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun findAll(): MutableList<T> {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun findAll(sort: Sort): MutableList<T> {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun findAll(pageable: Pageable): Page<T> {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun findAllById(ids: MutableIterable<ID>): MutableList<T> {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun count(): Long {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun existsById(id: ID): Boolean {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun <S : T> findAll(example: Example<S>): MutableList<S> {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun <S : T> findAll(example: Example<S>, sort: Sort): MutableList<S> {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun <S : T> findOne(example: Example<S>): Optional<S> {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun <S : T> count(example: Example<S>): Long {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun <S : T> exists(example: Example<S>): Boolean {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun <S : T> findAll(example: Example<S>, pageable: Pageable): Page<S> {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun <S : T, R : Any> findBy(
        example: Example<S>,
        queryFunction: Function<FluentQuery.FetchableFluentQuery<S>, R>
    ): R {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun findOne(spec: Specification<T>): Optional<T?> {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun findAll(spec: Specification<T>?): List<T?> {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun findAll(
        spec: Specification<T>?,
        pageable: Pageable
    ): Page<T?> {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun findAll(
        spec: Specification<T>?,
        countSpec: Specification<T>?,
        pageable: Pageable
    ): Page<T?> {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun findAll(
        spec: Specification<T>?,
        sort: Sort
    ): List<T?> {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun count(spec: Specification<T>?): Long {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun exists(spec: Specification<T>): Boolean {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun delete(spec: Specification<T>?): Long {
        throw UnsupportedOperationException("Not needed for these tests")
    }

    override fun <S : T?, R : Any?> findBy(
        spec: Specification<T?>,
        queryFunction: Function<in JpaSpecificationExecutor.SpecificationFluentQuery<S>, R?>
    ): R & Any {
        throw UnsupportedOperationException("Not needed for these tests")
    }
}
