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

        int id1 = taskManager.createTask(task1);
        int id2 = taskManager.createTask(task2);

        Epic epic1 = new Epic("Важный эпик 1", "очень очень очень важный список задач");
        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1",
                StatusTask.NEW, epic1);
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2",
                StatusTask.NEW, epic1);

        Subtask subtask3 = new Subtask("Подзадача3", "Описание подзадачи3",
                StatusTask.NEW, epic1);
        int id3 =taskManager.createEpic(epic1);
        int id4 =taskManager.createSubtask(subtask1);
        int id5 =taskManager.createSubtask(subtask2);
        int id6 =taskManager.createSubtask(subtask3);

        Epic epic2 = new Epic("Эпик без подзадачи", "Эпик без подзадачи");
        int id7 =taskManager.createEpic(epic2);


        taskManager.getTaskById(id1);
        taskManager.getTaskById(id2);

        taskManager.getSubtaskById(id5);
        taskManager.getEpicById(id7);

        taskManager.getSubtaskById(id6);
        taskManager.getSubtaskById(id4);
        taskManager.getEpicById(id3);
        taskManager.getSubtaskById(id6);

        taskManager.deleteTaskById(id1);
        taskManager.deleteEpicById(id3);
        System.out.println("\n");
        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task.toString());
        }


    }
}
