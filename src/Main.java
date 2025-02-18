import ru.practicum.java_kanban.model.Epic;
import ru.practicum.java_kanban.model.StatusTask;
import ru.practicum.java_kanban.model.Subtask;
import ru.practicum.java_kanban.model.Task;
import ru.practicum.java_kanban.service.InMemoryTaskManager;
import ru.practicum.java_kanban.service.TaskManager;


public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new InMemoryTaskManager();


        Task task1 = new Task("Помыть машину", "Записаться на мойку или помыть машину самому",
                 StatusTask.NEW);
        Task task2 = new Task("Заехать в МФЦ", "Заехать в МФЦ за документами (окно №3)",

                StatusTask.NEW);

        taskManager.createTask(task1);
        taskManager.createTask(task2);


        Epic epic1 = new Epic("Важный эпик 1", "очень очень очень важный список задач");
        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1",
                StatusTask.NEW, epic1);
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2",
                StatusTask.NEW, epic1);

        Epic epic2 = new Epic("Не очень важный эпик 2", "Не очень очень очень важный список задач");
        Subtask subtask3 = new Subtask("Подзадача3", "Описание подзадачи3",
                StatusTask.IN_PROGRESS, epic2);

        Subtask subtask4 = new Subtask("Подзадача4", "Описание подзадачи4",
                StatusTask.IN_PROGRESS, epic2);

        Subtask subtask5 = new Subtask("Подзадача5", "Описание подзадачи5",
                StatusTask.IN_PROGRESS, epic2);

        Subtask subtask6 = new Subtask("Подзадача6", "Описание подзадачи6",
                StatusTask.IN_PROGRESS, epic2);

        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask3);
        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);
        taskManager.createSubtask(subtask6);


        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        taskManager.getSubtaskById(subtask2.getId());

        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());


        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getSubtaskById(subtask4.getId());
        taskManager.getSubtaskById(subtask5.getId());
        taskManager.getSubtaskById(subtask6.getId());

        System.out.println("\n");
        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task.toString());
        }

    }
}
