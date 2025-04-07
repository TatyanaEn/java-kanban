package ru.practicum.java_kanban.service;

import ru.practicum.java_kanban.model.Epic;
import ru.practicum.java_kanban.model.StatusTask;
import ru.practicum.java_kanban.model.Subtask;
import ru.practicum.java_kanban.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private Integer count = 0;

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Subtask> subtasks;
    private HashMap<Integer, Epic> epics;

    private HistoryManager historyManager;

    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

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
     *
     * @return ArrayList<Task> срисок задач
     */
    @Override
    public ArrayList<Task> getTasks() {
        ArrayList<Task> result = (ArrayList<Task>) tasks.values().stream()
                .map(task -> {
                    Task newTask = new Task(task);
                    newTask.setId(task.getId());
                    return newTask;
                })
                .collect(Collectors.toList());
        return result;
    }

    /**
     * метод получения списка подзадач
     *
     * @return ArrayList<Subtask> срисок подзадач
     */
    @Override
    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> result = (ArrayList<Subtask>) subtasks.values().stream()
                .map(subtask -> {
                    Subtask newSubTask = new Subtask(subtask);
                    newSubTask.setId(subtask.getId());
                    return newSubTask;
                })
                .collect(Collectors.toList());
        return result;
    }

    /**
     * метод получения списка эпиков
     *
     * @return ArrayList<Epic> срисок эпиков
     */
    @Override
    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> result = (ArrayList<Epic>) epics.values().stream()
                .map(epic -> {
                    Epic newEpic = new Epic(epic);
                    newEpic.setId(epic.getId());
                    return newEpic;
                })
                .collect(Collectors.toList());
        return result;

    }

    /**
     * метод удаления всех задач из менеджера
     */
    @Override
    public void deleteTasks() {
        for (Task item : getHistory()) {
            prioritizedTasks.remove(item.getId());
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
            calculateDuration(epics.get(subtask.getEpicId()));
            calculateStartTime(epics.get(subtask.getEpicId()));
            calculateEndTime(epics.get(subtask.getEpicId()));
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask.getId());
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
                prioritizedTasks.remove(subtasks.get(id));
                subtasks.remove(id);
                historyManager.remove(id);
            }
            calculateStatus(epic);
            calculateDuration(epic);
            calculateStartTime(epic);
            calculateEndTime(epic);
            historyManager.remove(epic.getId());
        }
        epics.clear();
    }

    /**
     * метод получения задачи по идентификатору
     *
     * @param id идентификатор задачи
     * @return задача
     */
    @Override
    public Task getTaskById(Integer id) {
        if (tasks.get(id) != null) {
            historyManager.add(tasks.get(id));
            Task newTask = new Task(tasks.get(id));
            newTask.setId(id);
            return newTask;
        } else
            return null;
    }

    /**
     * метод получения подзадачи по идентификатору
     *
     * @param id идентификатор подзадачи
     * @return подзадача
     */
    @Override
    public Subtask getSubtaskById(Integer id) {
        if (subtasks.get(id) != null) {
            historyManager.add(subtasks.get(id));
            Subtask newSubtask = new Subtask(subtasks.get(id));
            newSubtask.setId(id);
            return newSubtask;
        } else
            return null;
    }

    /**
     * метод получения эпика по идентификатору
     *
     * @param id идентификатор эпика
     * @return эпик
     */
    @Override
    public Epic getEpicById(Integer id) {
        if (epics.get(id) != null) {
            historyManager.add(epics.get(id));
            Epic newEpic = new Epic(epics.get(id));
            newEpic.setId(id);
            return newEpic;
        } else
            return null;
    }

    /**
     * метод создания(добавления) задачи в менеджере
     *
     * @param task входная задача
     * @return идентификатор созданной в менеджере задачи
     */
    @Override
    public Integer createTask(Task task) {
        Integer newId = -1;
        if (checkOnCross(task))
            return newId;
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
            addToPrioritizedTasks(newTask);
        }
        return newId;
    }

    /**
     * метод создания(добавления) подзадачи в менеджере
     *
     * @param subtask входная подзадача
     * @return идентификатор созданной в менеджере подзадачи
     */
    @Override
    public Integer createSubtask(Subtask subtask) {
        Integer newId = -1;
        if (checkOnCross(subtask))
            return newId;
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
            calculateDuration(epics.get(newSubtask.getEpicId()));
            calculateStartTime(epics.get(newSubtask.getEpicId()));
            calculateEndTime(epics.get(newSubtask.getEpicId()));
            addToPrioritizedTasks(newSubtask);
        }
        return newId;
    }

    /**
     * метод создания(добавления) эпика в менеджере
     *
     * @param epic входной эпик
     * @return идентификатор созданного в менеджере эпика
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
        if (!checkOnCross(task))
            if (task != null) {
                Task newTask = new Task(task);
                newTask.setId(task.getId());
                tasks.put(newTask.getId(), newTask);
                prioritizedTasks.remove(newTask.getId());
                addToPrioritizedTasks(newTask);
            }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!checkOnCross(subtask))
            if (subtask != null) {
                Subtask newSubtask = new Subtask(subtask);
                newSubtask.setId(subtask.getId());
                subtasks.put(newSubtask.getId(), newSubtask);
                prioritizedTasks.remove(newSubtask.getId());
                addToPrioritizedTasks(newSubtask);

                Epic parentEpic = epics.get(newSubtask.getEpicId());
                if (parentEpic != null) {
                    parentEpic.addSubtask(newSubtask);
                    calculateStatus(parentEpic);
                    calculateDuration(parentEpic);
                    calculateStartTime(parentEpic);
                    calculateEndTime(parentEpic);
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
        prioritizedTasks.remove(tasks.get(id));
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
        calculateDuration(epic);
        calculateStartTime(epic);
        calculateEndTime(epic);
        prioritizedTasks.remove(subtasks.get(id));
        subtasks.remove(id);
        historyManager.remove(id);
    }


    @Override
    public void deleteEpicById(Integer id) {
        if (epics == null)
            return;
        if (epics.containsKey(id)) {

            for (Integer itemId : epics.get(id).getSubtasks()) {
                prioritizedTasks.remove(subtasks.get(itemId));
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
            result = (ArrayList<Subtask>) epic.getSubtasks().stream()
                    .map(itemId -> new Subtask(subtasks.get(itemId)))
                    .collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    /**
     * метод определения статуса эпика
     *
     * @param epic эпик
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
        countDone = (int) epic.getSubtasks().stream()
                .filter(id -> this.subtasks.get(id).getStatus().equals(StatusTask.DONE))
                .count();
        if (countDone == epic.getSubtasks().size()) {
            epic.setStatus(StatusTask.DONE);
            return;
        }
        int countNew = 0;
        countNew = (int) epic.getSubtasks().stream()
                .filter(id -> this.subtasks.get(id).getStatus().equals(StatusTask.NEW))
                .count();
        if (countNew == epic.getSubtasks().size()) {
            epic.setStatus(StatusTask.NEW);
            return;
        }
        epic.setStatus(StatusTask.IN_PROGRESS);
    }

    /**
     * метод определения длительности эпика
     *
     * @param epic эпик
     */
    private void calculateDuration(Epic epic) {
        Duration duration = Duration.ofMinutes(0);
        for (Integer id : epic.getSubtasks()) {
            duration = duration.plus(this.subtasks.get(id).getDuration());
        }
        epic.setDuration(duration);
    }

    /**
     * метод определения даты и времени старта эпика
     *
     * @param epic эпик
     */
    private void calculateStartTime(Epic epic) {
        LocalDateTime startTime = null;
        for (Integer id : epic.getSubtasks()) {
            if (startTime == null)
                startTime = this.subtasks.get(id).getStartTime();
            if (this.subtasks.get(id).getStartTime().isBefore(startTime))
                startTime = this.subtasks.get(id).getStartTime();
        }
        epic.setStartTime(startTime);
    }

    /**
     * метод определения даты и времени окончания эпика
     *
     * @param epic эпик
     */
    private void calculateEndTime(Epic epic) {
        LocalDateTime endTime = null;
        if (epic != null) {
            for (Integer id : epic.getSubtasks()) {
                if (this.subtasks.get(id).getEndTime() != null) {
                    if (endTime == null || endTime.isBefore(this.subtasks.get(id).getEndTime())) {
                        endTime = this.subtasks.get(id).getEndTime();
                    }
                }
            }
        }
        epic.setEndTime(endTime);
    }

    /**
     * метод определения максимального идентификатора
     *
     * @param tasks список задач(подзадач, эпиков)
     * @param idMax максимальный идентификатор
     * @return новое макисмальное значение
     */
    private static <T extends Task> Integer getMaxId(List<T> tasks, Integer idMax) {
        for (T task : tasks) {
            if (task.getId() > idMax) {
                idMax = task.getId();
            }
        }
        return idMax;
    }

    private void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null)
            prioritizedTasks.add(task);
    }

    private Boolean checkOnCross(Task task) {
        if ((task.getStartTime() == null) || (task.getEndTime() == null))
            return false;
        for (Task taskP : prioritizedTasks) {
            if (taskP.getEndTime() == null)
                continue;
            if (task.getStartTime().isAfter(taskP.getStartTime()) && task.getStartTime().isBefore(taskP.getEndTime()))
                return true;
            if (task.getEndTime().isAfter(taskP.getStartTime()) && task.getEndTime().isBefore(taskP.getStartTime()))
                return true;

        }
        return false;
    }
}
