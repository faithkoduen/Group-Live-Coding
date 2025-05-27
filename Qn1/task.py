

class Task:
    def __init__(self, task_id, title, description, priority, deadline, completed=False):
        self.task_id = task_id
        self.title = title
        self.description = description
        self.priority = priority
        self.deadline = deadline
        self.completed = completed

    def mark_complete(self):
        self.completed = True

    def edit_task(self, title=None, description=None, priority=None, deadline=None):
        if title:
            self.title = title
        if description:
            self.description = description
        if priority:
            self.priority = priority
        if deadline:
            self.deadline = deadline

class TaskManager:
    def __init__(self):
        self.tasks = []

    def add_task(self, task):
        self.tasks.append(task)

    def edit_task(self, task_id, title=None, description=None, priority=None, deadline=None):
        for task in self.tasks:
            if task.task_id == task_id:
                task.edit_task(title, description, priority, deadline)
                return True
        return False

    def delete_task(self, task_id):
        self.tasks = [task for task in self.tasks if task.task_id != task_id]

    def get_tasks(self):
        return self.tasks

    def get_upcoming_tasks(self):
         now = datetime.now()
         upcoming_tasks = [task for task in self.tasks if task.deadline > now and not task.completed]
         upcoming_tasks.sort(key=lambda task: task.deadline)
         return upcoming_tasks

    def save_tasks(self, filename="tasks.json"):
        task_data = [task.__dict__ for task in self.tasks]
        with open(filename, "w") as f:
            json.dump(task_data, f, default=str)

    def load_tasks(self, filename="tasks.json"):
        try:
            with open(filename, "r") as f:
                task_data = json.load(f)
                self.tasks = [Task(**data) for data in task_data]
        except FileNotFoundError:
            self.tasks = []

    def send_notification(self, title, message):
        plyer.notification.notify(title=title, message=message, timeout=10)

    def check_reminders(self):
        upcoming_tasks = self.get_upcoming_tasks()
        for task in upcoming_tasks:
             if task.deadline - datetime.now() <= timedelta(minutes=15):
                 self.send_notification(title="Task Reminder", message=f"Task {task.title} is due soon")

if __name__ == '__main__':
    task_manager = TaskManager()
    task_manager.load_tasks()

    
    task1 = Task(task_id=1, title="Grocery Shopping", description="Buy groceries for the week", priority="High", deadline=datetime(2025, 5, 27, 12, 0))
    task2 = Task(task_id=2, title="Book Appointment", description="Book a doctor appointment", priority="Medium", deadline=datetime(2025, 5, 28, 15, 0))
    task_manager.add_task(task1)
    task_manager.addounit(task2)

    schedule.every(1).minutes.do(task_manager.check_reminders)

    while True:
        schedule.run_pending()
        time.sleep(1)