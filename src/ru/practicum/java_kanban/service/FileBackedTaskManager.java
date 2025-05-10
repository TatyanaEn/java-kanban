package ru.practicum.java_kanban.service;

import ru.practicum.java_kanban.exception.ManagerSaveException;
import ru.practicum.java_kanban.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final String filename;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public FileBackedTaskManager(String filename) {
        super();
        this.filename = filename;
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public Integer createTask(Task task) {
        int newId = super.createTask(task);
        save();
        return newId;
    }

    @Override
    public Integer createSubtask(Subtask subtask) {
        int newId = super.createSubtask(subtask);
        save();
        return newId;
    }

    @Override
    public Integer createEpic(Epic epic) {
        int newId = super.createEpic(epic);
        save();
        return newId;
    }

    @Override
    public Boolean updateTask(Task task) {
        Boolean result = false;
        result = super.updateTask(task);
        save();
        return result;
    }

    @Override
    public Boolean updateSubtask(Subtask subtask) {
        Boolean result = false;
        result = super.updateSubtask(subtask);
        save();
        return result;
    }

    @Override
    public Boolean updateEpic(Epic epic) {
        Boolean result = false;
        result = super.updateEpic(epic);
        save();
        return result;
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(Integer id) {
        super.deleteEpicById(id);
        save();
    }

    /**
     * метод восстановления данных менеджера из файла
     *
     * @param file файл из которого загружается
     */
    public static FileBackedTaskManager loadFromFile(File file) {
        final FileBackedTaskManager taskManagerFromFile = new FileBackedTaskManager(file.toPath().toString());
        try {
            String[] strings = Files.readString(file.toPath()).split("\n");
            for (int i = 1; i < strings.length; i++) {
                Task task = fromString(strings[i]);
                switch (task.getType()) {
                    case TASK:
                        taskManagerFromFile.createTask(task);
                        break;
                    case SUBTASK:
                        taskManagerFromFile.createSubtask((Subtask) task);
                        break;
                    case EPIC:
                        taskManagerFromFile.createEpic((Epic) task);
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
        return taskManagerFromFile;
    }

    public static void main(String[] args) {
        String fileName = System.getProperty("user.home") + File.separator + "listTask.csv";
        FileBackedTaskManager taskManager = new FileBackedTaskManager(fileName);

        Task task1 = new Task("Помыть машину", "Записаться на мойку или помыть машину самому",
                StatusTask.NEW, Duration.ofMinutes(30), LocalDateTime.parse("04.04.2025 11:00", DATE_TIME_FORMATTER), 1);
        Task task2 = new Task("Заехать в МФЦ", "Заехать в МФЦ за документами (окно №3)",
                StatusTask.NEW, Duration.ofMinutes(120), LocalDateTime.parse("04.04.2025 15:00", DATE_TIME_FORMATTER), 2);
        int id1 = taskManager.createTask(task1);
        int id2 = taskManager.createTask(task2);

        Epic epic1 = new Epic("Важный эпик 1", "очень очень очень важный список задач", 3);
        int id3 = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1",
                StatusTask.NEW, Duration.ofMinutes(35), LocalDateTime.parse("04.04.2025 14:00", DATE_TIME_FORMATTER), taskManager.getEpicById(id3), 4);
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2",
                StatusTask.NEW, Duration.ofMinutes(20), LocalDateTime.parse("04.04.2025 17:00", DATE_TIME_FORMATTER), taskManager.getEpicById(id3), 5);

        Subtask subtask3 = new Subtask("Подзадача3", "Описание подзадачи3",
                StatusTask.NEW, Duration.ofMinutes(40), LocalDateTime.parse("04.04.2025 14:10", DATE_TIME_FORMATTER), taskManager.getEpicById(id3), 6);
        int id4 = taskManager.createSubtask(subtask1);
        int id5 = taskManager.createSubtask(subtask2);
        int id6 = taskManager.createSubtask(subtask3);

        Epic epic2 = new Epic("Эпик без подзадачи", "Эпик без подзадачи", 7);
        int id7 = taskManager.createEpic(epic2);

        FileBackedTaskManager newTaskManager = new FileBackedTaskManager(fileName);
        File file = new File(fileName);
        newTaskManager = taskManager.loadFromFile(file);

        for (Task task : newTaskManager.getTasks()) {
            System.out.println(task.toString());
        }
        for (Epic epic : newTaskManager.getEpics()) {
            System.out.println(epic.toString());
        }
        for (Subtask subtask : newTaskManager.getSubtasks()) {
            System.out.println(subtask.toString());
        }
        System.out.println("Задачки в порядке приоритета:");
        for (Task task : newTaskManager.getPrioritizedTasks()) {
            System.out.println(task.toString());
        }
        System.out.println("getSubtasksFromEpic:");
        for (Subtask subtask : taskManager.getSubtasksFromEpic(taskManager.getEpicById(id3))) {
            System.out.println(subtask.toString());
        }


    }

    /**
     * метод сохранения состояния менеджера в указанном файле
     */
    private void save() {
        try (Writer fileWriter = new FileWriter(filename)) {
            fileWriter.write("id,type,name,status,description,duration,starttime,epic\n");
            for (Task item : this.getTasks()) {
                fileWriter.write(item.toString() + '\n');
            }
            for (Epic item : this.getEpics()) {
                fileWriter.write(item.toString() + '\n');
            }
            for (Subtask item : this.getSubtasks()) {
                fileWriter.write(item.toString() + '\n');
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    /**
     * метод создания задачи из строки
     *
     * @param value строковое представление входящих данных файла
     * @return созданная задача
     */
    private static Task fromString(String value) {
        String[] items = value.split(",");
        Task newTask = null;
        Duration duration = Duration.ofMinutes(Long.parseLong(items[5]));
        LocalDateTime startTime = null;
        if (!items[6].equals("null"))
            startTime = LocalDateTime.parse(items[6], DATE_TIME_FORMATTER);
        switch (TypeTask.valueOf(items[1])) {
            case TASK:
                newTask = new Task(items[2], items[4], StatusTask.valueOf(items[3]), duration, startTime);
                break;
            case SUBTASK:
                newTask = new Subtask(items[2], items[4],
                        StatusTask.valueOf(items[3]), duration, startTime);
                ((Subtask) newTask).setEpicId(Integer.valueOf(items[7]));
                break;
            case EPIC:
                newTask = new Epic(items[2], items[4]);
                newTask.setStatus(StatusTask.valueOf(items[3]));
                newTask.setStartTime(startTime);
                newTask.setDuration(duration);
                break;
        }
        if (newTask != null)
            newTask.setId(Integer.valueOf(items[0]));
        return newTask;
    }

}
