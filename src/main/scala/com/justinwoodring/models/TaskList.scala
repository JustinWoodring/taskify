package com.justinwoodring.models

import java.util.UUID
import scala.scalajs.js.Date

object TaskList {
  def apply() : TaskList = new TaskList(Seq())
  def apply(tasks : Seq[Task]) : TaskList = new TaskList(tasks)
}

class TaskList(val tasks : Seq[Task]) {
  def toggleTaskCompletion(id : UUID) =
    TaskList(tasks.map(task => {if (task.id == id) task.copy(completed = !task.completed) else task}))

  def modifyTaskValue(id: UUID, value: String) =
    TaskList(tasks.map(task => {
      if (task.id == id) task.copy(value = value) else task
    }))

  def addTask(value: String): TaskList = {
    val temp = tasks.sortBy(_.order)
    val temp2 = for {i <- 0 until temp.size
                     task = temp(i)
                     } yield task.copy(order=i)
    TaskList(temp2 :+ Task(UUID.randomUUID(), Date.now().toInt, value, false))
  }

  def deleteTask(id: UUID): TaskList = {
    val temp = tasks.filterNot(_.id == id).sortBy(_.order)
    val temp2 = for {i <- 0 until temp.size
                     task = temp(i)
                     } yield task.copy(order=i)

    println(temp2)
    TaskList(temp2)
  }

  def size() = tasks.size
}
