package dev.weg.alfa.modules.validators

fun interface Validator<T> {
  fun validate(t: T)
}
