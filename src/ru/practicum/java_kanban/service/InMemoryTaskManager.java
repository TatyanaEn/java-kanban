package ru.practicum.java_kanban.service;

import ru.practicum.java_kanban.model.Epic;
import ru.practicum.java_kanban.model.StatusTask;
import ru.practicum.java_kanban.model.Subtask;
import ru.practicum.java_kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private Integer count = 0;

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Subtask> subtasks;
    private HashMap<Integer, Epic> epics;

    HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public Integer getNewId() {
        count++;
        return count;
    }

    private void setNewId(Integer newId) {
        count = newId;
    }

    /**
     * метод получения списка задач
     * @return  ArrayList<Task> срисок задач
     */
    @Override
    public ArrayList<Task> getTasks() {
        ArrayList<Task> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            result.add(new Task(task));
        }
        return result;
    }

    /**
     * метод получения списка подзадач
     * @return  ArrayList<Subtask> срисок подзадач
     */
    @Override
    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> result = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            result.add(new Subtask(subtask));
        }
        return result;
    }

    /**
     * метод получения списка эпиков
     * @return  ArrayList<Epic> срисок эпиков
     */
    @Override
    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> result = new ArrayList<>();
        for (Epic epic : epics.values()) {
            result.add(new Epic(epic));
        }
        return result;

    }

    /**
     * метод удаления всех задач из менеджера
     */
    @Override
    public void deleteTasks() {
        for (Task item : getHistory()) {
            historyManager.remove(item.getId());
        }
        tasks.clear();

    }

    /**
     * метод удаления всех подзадач из менеджера
     */
    @Override
    public void deleteSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            epics.get(subtask.getEpicId()).deleteSubtask(subtask);
            calculateStatus(epics.get(subtask.getEpicId()));
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
    }

    /**
     * метод удаления всех эпиков из менеджера
     */
    @Override
    public void deleteEpics() {
        for (Epic epic : epics.values()) {
            for (Integer id : epic.getSubtasks()) {
                subtasks.remove(id);
                historyManager.remove(id);
            }
            calculateStatus(epic);
            historyManager.remove(epic.getId());
        }
        epics.clear();
    }

    /**
     * метод получения задачи по идентификатору
     *
     * @param id идентификатор задачи
     * @return  задача
     */
    @Override
    public Task getTaskById(Integer id) {
        if (tasks.get(id) != null) {
            historyManager.add(tasks.get(id));
            return new Task(tasks.get(id));
        } else
            return null;
    }

    /**
     * метод получения подзадачи по идентификатору
     *
     * @param id идентификатор подзадачи
     * @return  подзадача
     */
    @Override
    public Subtask getSubtaskById(Integer id) {
        if (subtasks.get(id) != null) {
            historyManager.add(subtasks.get(id));
            return new Subtask(subtasks.get(id));
        } else
            return null;
    }

    /**
     * метод получения эпика по идентификатору
     *
     * @param id идентификатор эпика
     * @return  эпик
     */
    @Override
    public Epic getEpicById(Integer id) {
        if (epics.get(id) != null) {
            historyManager.add(epics.get(id));
            return new Epic(epics.get(id));
        } else
            return null;
    }

    /**
     * метод создания(добавления) задачи в менеджере
     *
     * @param task входная задача
     * @return  идентификатор созданной в менеджере задачи
     */
    @Override
    public Integer createTask(Task task) {
        Integer newId = -1;
        if (task != null) {
            Task newTask = new Task(task);
            newId = task.getId();
            if (newId == null) {
                // если ид не заполнен требуется сгенерировать новый
                newId = getNewId();
                newTask.setId(newId);
            } else {
                // требуется обновить счетчик
                Integer idMax = 0;
                idMax = getMaxId(getTasks(), idMax);
                idMax = getMaxId(getSubtasks(), idMax);
                idMax = getMaxId(getEpics(), idMax);
                setNewId(idMax + 1);
            }
            tasks.put(newTask.getId(), newTask);
        }
        return newId;
    }

    /**
     * метод создания(добавления) подзадачи в менеджере
     *
     * @param subtask входная подзадача
     * @return  идентификатор созданной в менеджере подзадачи
     */
    @Override
    public Integer createSubtask(Subtask subtask) {
        Integer newId = -1;
        if (subtask != null) {
            Subtask newSubtask = new Subtask(subtask);
            newId = subtask.getId();
            if (newId == null) {
                newId = getNewId();
                newSubtask.setId(newId);
            } else {
                // требуется обновить счетчик
                Integer idMax = 0;
                idMax = getMaxId(getTasks(), idMax);
                idMax = getMaxId(getSubtasks(), idMax);
                idMax = getMaxId(getEpics(), idMax);
                setNewId(idMax + 1);
            }
            subtasks.put(newSubtask.getId(), newSubtask);
            epics.get(newSubtask.getEpicId()).addSubtask(newSubtask);
            calculateStatus(epics.get(newSubtask.getEpicId()));

        }
        return newId;
    }

    /**
     * метод создания(добавления) эпика в менеджере
     *
     * @param epic входной эпик
     * @return  идентификатор созданного в менеджере эпика
     */
    @Override
    public Integer createEpic(Epic epic) {
        Integer newId = -1;
        if (epic != null) {
            Epic newEpic = new Epic(epic);
            newId = epic.getId();
            if (newId == null) {
                newId = getNewId();
                newEpic.setId(newId);
            } else {
                // требуется обновить счетчик
                Integer idMax = 0;
                idMax = getMaxId(getTasks(), idMax);
                idMax = getMaxId(getSubtasks(), idMax);
                idMax = getMaxId(getEpics(), idMax);
                setNewId(idMax + 1);
            }
            epics.put(newEpic.getId(), newEpic);
        }
        return newId;
    }

    @Override
    public void updateTask(Task task) {
        if (task != null) {
            Task newTask = new Task(task);
            newTask.setId(task.getId());
            tasks.put(newTask.getId(), newTask);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null) {
            Subtask newSubtask = new Subtask(subtask);
            newSubtask.setId(subtask.getId());
            subtasks.put(newSubtask.getId(), newSubtask);
            Epic parentEpic = epics.get(newSubtask.getEpicId());
            if (parentEpic != null) {
                parentEpic.addSubtask(newSubtask);
                calculateStatus(parentEpic);
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null) {
            Epic newEpic = new Epic(epic);
            newEpic.setId(epic.getId());
            epics.put(newEpic.getId(), newEpic);
        }
    }

    @Override
    public void deleteTaskById(Integer id) {
        if (tasks == null) return;
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        if (subtasks == null) return;
        Subtask subtask = subtasks.get(id);
        if (subtask == null) return;
        Epic epic = epics.get(subtask.getEpicId());
        epic.deleteSubtask(subtask);
        calculateStatus(epic);
        subtasks.remove(id);
        historyManager.remove(id);
    }


    @Override
    public void deleteEpicById(Integer id) {
        if (epics == null)
            return;
        if (epics.containsKey(id)) {

            for (Integer itemId : epics.get(id).getSubtasks()) {
                subtasks.remove(itemId);
            }
            epics.remove(id);
            historyManager.remove(id);
        }

    }

    @Override
    public ArrayList<Subtask> getSubtasksFromEpic(Epic epic) {
        ArrayList<Subtask> result = new ArrayList<Subtask>();
        if (epic != null) {
            for (Integer itemId : epic.getSubtasks()) {
                result.add(new Subtask(subtasks.get(itemId)));
            }
            return result;
        } else
            return result;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    /**
     * метод определения статуса эпика
     *
     * @param epic  эпик
     */
    private void calculateStatus(Epic epic) {
        int countDone = 0;
        if (epic.getSubtasks() == null) {
            epic.setStatus(StatusTask.NEW);
            return;
        }
        if (subtasks.size() == 0) {
            epic.setStatus(StatusTask.NEW);
            return;
        }
        for (Integer id : epic.getSubtasks()) {
            if (this.subtasks.get(id).getStatus().equals(StatusTask.DONE)) {
                countDone++;
            }
        }
        if (countDone == epic.getSubtasks().size()) {
            epic.setStatus(StatusTask.DONE);
            return;
        }
        int countNew = 0;
        for (Integer id : epic.getSubtasks()) {
            if (this.subtasks.get(id).getStatus().equals(StatusTask.NEW)) {
                countNew++;
            }
        }
        if (countNew == epic.getSubtasks().size()) {
            epic.setStatus(StatusTask.NEW);
            return;
        }
        epic.setStatus(StatusTask.IN_PROGRESS);
    }
    /**
     * метод определения максимального идентификатора
     *
     * @param tasks  список задач(подзадач, эпиков)
     * @param idMax  максимальный идентификатор
     * @return       новое макисмальное значение
     */
    private static <T extends Task> Integer getMaxId(List<T> tasks, Integer idMax) {
        for (T task : tasks) {
            if (task.getId() > idMax) {
                idMax = task.getId();
            }
        }
        return idMax;
    }
}
