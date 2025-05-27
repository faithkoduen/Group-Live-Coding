import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.scheduleAtFixedRate

// Task priority levels
enum class Priority { LOW, MEDIUM, HIGH }

// Data class representing a task
data class Task(
    val id: Int,
    var title: String,
    var description: String? = null,
    var priority: Priority = Priority.MEDIUM,
    var deadline: Date? = null,
    var isRecurring: Boolean = false,
    var recurrenceIntervalMillis: Long? = null // for recurring tasks
)

// Task manager class to handle tasks and scheduling
class TaskManager {
    private val tasks = mutableMapOf<Int, Task>()
    private val timers = mutableMapOf<Int, Timer>()
    private var nextId = 1

    // Add a new task
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

    // Edit an existing task by id
    fun editTask(
        id: Int,
        title: String? = null,
        description: String? = null,
        priority: Priority? = null,
        deadline: Date? = null,
        isRecurring: Boolean? = null,
        recurrenceIntervalMillis: Long? = null
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

    // Delete a task by id
    fun deleteTask(id: Int): Boolean {
        cancelReminder(id)
        return tasks.remove(id) != null
    }

    // List all tasks
    fun listTasks(): List<Task> = tasks.values.toList()

    // Schedule reminders and recurring execution for a task
    private fun scheduleReminder(task: Task) {
        task.deadline?.let { deadline ->
            val now = Date()
            val delay = deadline.time - now.time
            if (delay > 0) {
                val timer = Timer()
                if (task.isRecurring && task.recurrenceIntervalMillis != null) {
                    // Schedule recurring reminder
                    timer.scheduleAtFixedRate(delay, task.recurrenceIntervalMillis!!) {
                        println("Reminder: Task '${task.title}' is due now or recurring.")
                    }
                } else {
                    // Schedule one-time reminder
                    timer.schedule(delay) {
                        println("Reminder: Task '${task.title}' is due now.")
                    }
                }
                timers[task.id] = timer
            }
        }
    }

    // Cancel reminder timer for a task
    private fun cancelReminder(taskId: Int) {
        timers[taskId]?.cancel()
        timers.remove(taskId)
    }
}

// Example usage
fun main() {
    val taskManager = TaskManager()

    // Add a task with a deadline 10 seconds from now and a reminder
    val deadline = Date(System.currentTimeMillis() + 10_000)
    val task1 = taskManager.addTask(
        title = "Submit report",
        priority = Priority.HIGH,
        deadline = deadline
    )

    // Add a recurring task every 15 seconds starting 5 seconds from now
    val recurringDeadline = Date(System.currentTimeMillis() + 5_000)
    val task2 = taskManager.addTask(
        title = "Water plants",
        priority = Priority.LOW,
        deadline = recurringDeadline,
        isRecurring = true,
        recurrenceIntervalMillis = 15_000
    )

    // Edit a task
    taskManager.editTask(task1.id, description = "Monthly financial report")

    // List tasks
    taskManager.listTasks().forEach { println(it) }

    // Delete a task
    // taskManager.deleteTask(task1.id)

    // Keep the main thread alive to see reminders
    Thread.sleep(60_000)
}
