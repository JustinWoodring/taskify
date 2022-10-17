package com.justinwoodring

import slinky.core._
import slinky.core.annotations.react
import slinky.web.html._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("resources/App.css", JSImport.Default)
@js.native
object AppCSS extends js.Object

@react object App{
  private val css = AppCSS

  case class Props()
  val component = FunctionalComponent[Props] {props =>
    div(className := "App")(
      header(className := "header")(h1("Taskify")),
      main(className := "main")(
        components.TasksManager(

        )
      )
    )
  }
}
