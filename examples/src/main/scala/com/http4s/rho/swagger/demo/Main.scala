package com.http4s.rho.swagger.demo

import cats.data.Kleisli
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.implicits._
import org.http4s.rho.swagger.SwaggerSupport
import org.http4s.server.blaze.BlazeServerBuilder
import org.log4s.getLogger

object Main extends IOApp {
  private val logger = getLogger

  val port: Int = Option(System.getenv("HTTP_PORT"))
    .map(_.toInt)
    .getOrElse(8080)

  logger.info(s"Starting Swagger example on '$port'")

  def run(args: List[String]): IO[ExitCode] = {
    val swagger = new SwaggerSupport[Kleisli[IO, SimpleUser, ?]] {}
    val middleware = swagger.createRhoMiddleware()

    /*val myService: HttpRoutes[IO] =
      new MyRoutes[IO](ioSwagger) {}.toRoutes(middleware)*/

    val myAuthedService: HttpRoutes[IO] = ExampleAuth.simpleAuthMiddlware {
      middleware(new MyAuthedRoutes[IO]().getRoutes).toAuthedService()
    }

    BlazeServerBuilder[IO]
      .withHttpApp((StaticContentService.routes <+> myAuthedService).orNotFound)
      .bindLocal(port)
      .serve.compile.drain.as(ExitCode.Success)
  }
}
