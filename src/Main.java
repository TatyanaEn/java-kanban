import ru.practicum.java_kanban.model.Epic;
import ru.practicum.java_kanban.model.StatusTask;
import ru.practicum.java_kanban.model.Subtask;
import ru.practicum.java_kanban.model.Task;
import ru.practicum.java_kanban.service.TaskManager;


public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();


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

        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask3);


        System.out.println("\n");
        System.out.println("Задачи:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task.toString());
        }
        System.out.println("\n");
        System.out.println("Подзадачи:");
        for (Subtask subtask : taskManager.getSubtasks()) {
            System.out.println(subtask.toString());
        }
        System.out.println("\n");
        System.out.println("Эпики:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic.toString());
        }

        Subtask newSubtask1 = new Subtask("Подзадача1", "Описание подзадачи1",
                StatusTask.IN_PROGRESS, epic1);
        newSubtask1.setId(subtask1.getId());
        Subtask newSubtask2 = new Subtask("Новая Подзадача2", "Описание подзадачи2",
                StatusTask.NEW, epic1);
        newSubtask2.setId(subtask2.getId());
        Subtask newSubtask3 = new Subtask("Подзадача3", "Описание подзадачи3",
                StatusTask.DONE, epic2);
        newSubtask3.setId(subtask3.getId());

        taskManager.updateSubtask(newSubtask1);
        taskManager.updateSubtask(newSubtask2);
        taskManager.updateSubtask(newSubtask3);

        System.out.println("\n");
        System.out.println("После обновления:");
        System.out.println("\n");
        System.out.println("Подзадачи:");
        for (Subtask subtask : taskManager.getSubtasks()) {
            System.out.println(subtask.toString());
        }
        System.out.println("\n");
        System.out.println("Эпики:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic.toString());
        }

        taskManager.deleteSubtaskById(subtask1.getId());
        taskManager.deleteEpicById(epic2.getId());
        System.out.println("\n");
        System.out.println("После удаления:");
        System.out.println("\n");
        System.out.println("Подзадачи:");
        for (Subtask subtask : taskManager.getSubtasks()) {
            System.out.println(subtask.toString());
        }
        System.out.println("\n");
        System.out.println("Эпики:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic.toString());
        }
        taskManager.deleteSubtasks();
        System.out.println("Подзадачи:");
        for (Subtask subtask : taskManager.getSubtasks()) {
            System.out.println(subtask.toString());
        }
        System.out.println("\n");
        System.out.println("Эпики:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic.toString());
        }
    }
}
