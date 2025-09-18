package dev.weg.alfa.modules.exceptions

enum class ExceptionErrorMessages(val message: String, val details: String? = null) {
  FORBIDDEN_ACCESS("The user don't have permission to perform this action."),
  INVALID_IMAGE("The image sent is not valid."),
  USER_NOT_FOUND("User not found."),
  USER_NOT_VALID("User is not valid."),
  EXPIRED_RESET_PASSWORD_TOKEN("Reset token has already expired."),
  INVALID_RESET_PASSWORD_TOKEN("Provided reset token is invalid."),
  INVALID_PASSWORD("Password is not valid.")
}
