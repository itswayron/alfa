package dev.weg.alfa.modules.validators


enum class ValidationErrorMessages(val message: String) {
  IMAGE_EMPTY("File does not exists."),
  NOT_IMAGE("The file is not an image."),
  BIG_FILE("The file is too big"),
  SMALL_DIMENSIONS("The image dimensions are too small."),
  UNSUPPORTED_IMAGE("Unsupported image format or unable to read image."),
  BLANK_EMAIL("Email can not be blank."),
  INVALID_EMAIL("Email format is invalid."),
  BLANK_USERNAME("Username cannot be blank."),
  USERNAME_LENGTH("Username must be between 3 and 20 characters."),
  USERNAME_INVALID_CHARS("Username can only contain letters, digits, underscores (_), dashes (-), and periods (.)"),
  BLANK_PASSWORD("Password cannot be blank."),
  PASSWORD_TOO_SHORT("Password must be at least 8 characters long."),
  PASSWORD_NO_UPPERCASE("Password must contain at least one uppercase letter."),
  PASSWORD_NO_LOWERCASE("Password must contain at least one lowercase letter."),
  PASSWORD_NO_DIGIT("Password must contain at least one digit."),
  PASSWORD_NO_SPECIAL("Password must contain at least one special character."),
  USERNAME_NOT_AVAILABLE("Username is already in use."),
  EMAIL_NOT_AVAILABLE("Email is already registered."),
}
