package ru.practicum.java_kanban.service;

import ru.practicum.java_kanban.model.Node;
import ru.practicum.java_kanban.model.Task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager<T extends Task> implements HistoryManager {
    private Node<T> head;
    private Node<T> tail;
    private HashMap<Integer, Node> historyHashMap;
    private int size = 0;


    public InMemoryHistoryManager() {
        historyHashMap = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (historyHashMap.containsKey(task.getId())) {
                removeNode(historyHashMap.get(task.getId()));
            }
            linkLast((T) task);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(historyHashMap.get(id));
    }


    @Override
    public List<T> getHistory() {
        return getTasks();
    }

    public void linkLast(T t) {
        Node<T> tmpTail = this.tail;
        Node<T> newNode = new Node(t);
        this.tail = newNode;
        historyHashMap.put(t.getId(), newNode);
        if (tmpTail == null) {
            this.head = newNode;
        } else {
            tmpTail.setNext(newNode);
            newNode.setPrevious(tmpTail);
        }
        ++this.size;
    }

    public List<T> getTasks() {
        List<T> tasks = new LinkedList<>();
        Node<T> currentNode = head;
        while (currentNode != null) {
            tasks.add(currentNode.getData());
            currentNode = currentNode.getNext();
        }
        return tasks;
    }

    public void removeNode(Node node) {
        if (node == null) return;
        Node nextNode = node.getNext();
        Node prevNode = node.getPrevious();
        if (node == head && node == tail) {
            head = null;
            tail = null;
            --this.size;
        } else if (node == head && node != tail) {
            nextNode.setPrevious(null);
            head = nextNode;
            --this.size;
        } else if (node == tail && node != head) {
            prevNode.setPrevious(null);
            tail = prevNode;
            --this.size;
        } else {
            if (prevNode != null)
                prevNode.setNext(nextNode);
            if (nextNode != null)
                nextNode.setPrevious(prevNode);
            --this.size;
        }
    }


}
