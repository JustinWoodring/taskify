package com.justinwoodring.models

import java.util.UUID

case class Task(id: UUID, order: Int, value: String, completed: Boolean)
