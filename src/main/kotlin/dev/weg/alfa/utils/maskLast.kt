package dev.weg.alfa.utils

fun String.maskLast(n: Int = 4): String {
  if (this.length <= n) return this
  val maskLength = this.length - n
  return "*".repeat(maskLength) + this.takeLast(n)
}
