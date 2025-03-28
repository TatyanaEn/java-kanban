import ru.practicum.java_kanban.model.Epic;
import ru.practicum.java_kanban.model.StatusTask;
import ru.practicum.java_kanban.model.Subtask;
import ru.practicum.java_kanban.model.Task;
import ru.practicum.java_kanban.service.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Поехали!");

        String fileName = System.getProperty("user.home") + File.separator + "listTask.csv";
        FileBackedTaskManager taskManager = new FileBackedTaskManager(fileName);

        Task task1 = new Task("Помыть машину", "Записаться на мойку или помыть машину самому",
                StatusTask.NEW, 1);
        Task task2 = new Task("Заехать в МФЦ", "Заехать в МФЦ за документами (окно №3)",
                StatusTask.NEW, 2);
        int id1 = taskManager.createTask(task1);
        int id2 = taskManager.createTask(task2);

        Epic epic1 = new Epic("Важный эпик 1", "очень очень очень важный список задач", 3);
        int id3 = taskManager.createEpic(epic1);
        // int id3 = 1;
        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1",
                StatusTask.NEW, taskManager.getEpicById(id3), 4);
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2",
                StatusTask.NEW, taskManager.getEpicById(id3), 5);

        Subtask subtask3 = new Subtask("Подзадача3", "Описание подзадачи3",
                StatusTask.NEW, taskManager.getEpicById(id3), 6);
        int id4 = taskManager.createSubtask(subtask1);
        int id5 = taskManager.createSubtask(subtask2);
        int id6 = taskManager.createSubtask(subtask3);

        Epic epic2 = new Epic("Эпик без подзадачи", "Эпик без подзадачи", 7);
        int id7 = taskManager.createEpic(epic2);

        taskManager.deleteTaskById(id1);
        taskManager.deleteEpicById(id3);
        System.out.println("\n");
        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task.toString());
        }


    }
}
