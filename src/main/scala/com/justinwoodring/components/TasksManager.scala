package com.justinwoodring.components

import com.justinwoodring.models.{Task, TaskList}
import org.scalajs.dom
import org.scalajs.dom.{Event, document}
import org.scalajs.dom.raw.{
  EventTarget,
  HTMLButtonElement,
  HTMLDivElement,
  HTMLInputElement,
  KeyboardEvent
}
import org.w3c.dom.events.UIEvent
import slinky.core.{
  ExternalComponent,
  ExternalComponentNoProps,
  FunctionalComponent,
  ReactComponentClass,
  SyntheticEvent,
  TagElement
}
import slinky.core.annotations.react
import slinky.core.facade.Hooks.{useEffect, useState}
import slinky.web.SyntheticMouseEvent
import slinky.web.html.{span, _}

import java.util.UUID
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.tools.nsc.doc.html.HtmlTags.Input

@JSImport("react-confetti", JSImport.Default)
@js.native
object ReactConfetti extends js.Object {}

object Confetti extends ExternalComponent {
  case class Props(width: Int, height: Int)
  override val component = ReactConfetti
}

@react object TasksManager {
  type Props = Unit
  val component = FunctionalComponent[Unit] { props =>
    val (taskList, setTaskList) = useState(
      TaskList(
        Seq(
          Task(UUID.randomUUID(), 0, "Try out Taskify", false),
          Task(
            UUID.randomUUID(),
            1,
            "Create a Task about Starting the Party and Press Enter",
            false
          ),
          Task(UUID.randomUUID(), 2, "Check off this Task", false),
          Task(
            UUID.randomUUID(),
            3,
            "Select this, Fix the splling errers, and Select again",
            false
          ),
          Task(UUID.randomUUID(), 4, "Complete and Delete this Task", false),
          Task(UUID.randomUUID(), 5, "Complete all Tasks and Party", false)
        )
      )
    )

    val (text, setText) = useState("");
    val (selectedId, setSelectedId) = useState[Option[UUID]](None)

    val createTask = (e: SyntheticEvent[HTMLInputElement, KeyboardEvent]) => {
      if (selectedId == None) {
        if (
          e.target.value != "" && e.nativeEvent.key == "Enter" && e.target == document.activeElement
        ) {
          val tasks = taskList
          setTaskList(tasks.addTask(e.target.value))
          setText("")
        }
      }
    }

    val modifyTask = (e: SyntheticEvent[HTMLInputElement, Event]) => {
      setText(e.target.value)
      taskList.tasks.find(task => task.id == selectedId.orNull) match {
        case Some(task) =>
          setTaskList(taskList.modifyTaskValue(task.id, e.target.value))
        case None => println("This shouldn't happen!")
      }
    }

    val selectTask = (e: SyntheticMouseEvent[HTMLDivElement]) => {
      if (selectedId != Some(UUID.fromString(e.target.dataset("id")))) {
        setSelectedId(Some(UUID.fromString(e.target.dataset("id"))))
        taskList.tasks.find(task =>
          task.id == UUID.fromString(e.target.dataset("id"))
        ) match {
          case Some(task) => setText(task.value)
          case None       => setText("")
        }
      } else {
        setSelectedId(None)
        setText("")
      }
    }

    val deleteTask = (e: SyntheticMouseEvent[HTMLButtonElement]) => {
      val tasks = taskList
      setTaskList(tasks.deleteTask(UUID.fromString(e.target.dataset("id"))))
      setSelectedId(None)
      setText("")
    }

    val renderedTasks = for {
      i <- 0 until taskList.size
      task = taskList.tasks(i)
    } yield {
      div(
        key := task.id.toString,
        className := s"task ${if (task.completed) "completed" else ""} ${if (task.id == selectedId.orNull) "selected"
          else ""}",
        data - "id" := s"${task.id}",
        onClick := (e => {
          selectTask(e)
        })
      )(
        span(
          className := "checkbox",
          onClick := (e => {
            e.stopPropagation();
            setTaskList(taskList.toggleTaskCompletion(task.id))
          })
        )(),
        h2(className := "task-header", data - "id" := s"${task.id}")(
          task.value
        ),
        button(
          className := s"button ${if (task.completed) "completed" else ""}",
          data - "id" := s"${task.id}",
          onClick := ((e) => { e.stopPropagation(); deleteTask(e) })
        )("Remove")
      )
    }

    div(className := "tasks-container")(
      h2()(s"${if (taskList.size > 0) taskList.size else "No"} Tasks"),
      input(
        className := "input",
        `type` := "input",
        placeholder := "Type a task to get started...",
        value := text,
        onChange := (e => modifyTask(e)),
        onKeyUp := (e => createTask(e))
      ),
      div(className := "tasks-list")(
        renderedTasks
      ),
      (if (
         !taskList.tasks.exists(task => !task.completed) && taskList.size > 0
       ) {
         Confetti(
           Confetti.Props(
             org.scalajs.dom.window.outerWidth,
             org.scalajs.dom.window.outerHeight
           )
         )
       } else { None })
    )
  }
}
