package mvc

import slick.models.User
import play.api.mvc.WrappedRequest
import play.api.mvc.Request

class UserRequest[A](
  val user: User,
  request:  Request[A]
) extends WrappedRequest[A](request)
