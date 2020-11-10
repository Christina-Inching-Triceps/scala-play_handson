package slick.models

import java.time.LocalDateTime

case class User(
  id:        Option[Long] = None,
  name:      String,
  email:     String,
  password:  String,
  createdAt: LocalDateTime = LocalDateTime.now,
  updatedAt: LocalDateTime = LocalDateTime.now
)

