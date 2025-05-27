
 class Priority { LOW, MEDIUM, HIGH }


data class Task(
    val id: Int,
    var title: String,
    var description: String,
    var priority: Priority = Priority.MEDIUM,
    var deadline: Date,
    var isRecurring: Boolean = false,
    var recurrenceIntervalMillis: Long? = null // for recurring tasks
)


class TaskManager {
     val tasks = mutableMapOf<Int, Task>()
     val timers = mutableMapOf<Int, Timer>()
     var nextId = 1


    fun addTask(
        title: String,
        description: String? = null,
        priority: Priority = Priority.MEDIUM,
        deadline: Date? = null,
        isRecurring: Boolean = false,
        recurrenceIntervalMillis: Long? = null
    ): Task {
        val task = Task(nextId++, title, description, priority, deadline, isRecurring, recurrenceIntervalMillis)
        tasks[task.id] = task
        scheduleReminder(task)
        return task
    }

    
    fun editTask(
        id: Int,
        title: String,
        description: String,
        priority: Priority,
        deadline: Date,
        isRecurring: Boolean,
        recurrenceIntervalMillis: Long 
    ): Boolean {
        val task = tasks[id] ?: return false
        title?.let { task.title = it }
        description?.let { task.description = it }
        priority?.let { task.priority = it }
        deadline?.let { task.deadline = it }
        isRecurring?.let { task.isRecurring = it }
        recurrenceIntervalMillis?.let { task.recurrenceIntervalMillis = it }
        cancelReminder(id)
        scheduleReminder(task)
        return true
    }

    
    fun deleteTask(id: Int): Boolean {
        cancelReminder(id)
        return tasks.remove(id) != null
    }

    
    fun listTasks(): List<Task> = tasks.values.toList()


    fun scheduleReminder(task: Task) {
        task.deadline?.let { deadline ->
            val now = Date()
            val delay = deadline.time - now.time
            if (delay > 0) {
                val timer = Timer()
                if (task.isRecurring && task.recurrenceIntervalMillis != null) {
                    
                    timer.scheduleAtFixedRate(delay, task.recurrenceIntervalMillis!!) {
                        println("Reminder: Task '${task.title}' is due now or recurring.")
                    }
                } else {
                    
                    timer.schedule(delay) {
                        println("Reminder: Task '${task.title}' is due now.")
                    }
                }
                timers[task.id] = timer
            }
        }
    }

    
    fun cancelReminder(taskId: Int) {
        timers[taskId]?.cancel()
        timers.remove(taskId)
    }
}


fun main() {
    val taskManager = TaskManager()


    val deadline = Date(System.currentTimeMillis() + 10_000)
    val task1 = taskManager.addTask(
        title = "Submit report",
        priority = Priority.HIGH,
        deadline = deadline
    )

    
    val recurringDeadline = Date(System.currentTimeMillis() + 5_000)
    val task2 = taskManager.addTask(
        title = "Water plants",
        priority = Priority.LOW,
        deadline = recurringDeadline,
        isRecurring = true,
        recurrenceIntervalMillis = 15_000
    )

    
    taskManager.editTask(task1.id, description = "Sprint planning")

    
    taskManager.listTasks().forEach { println(it) }

    Thread.sleep(60_000)
}
