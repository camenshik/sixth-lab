package server;

import server.exceptions.WrapperException;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;

public class WrapperHashSet<T extends Wrapperable> {
    private final HashSet<T> set;
    private final HashSet<Long> listIdSet;
    private final ZonedDateTime creationDate;

    public WrapperHashSet() {
        this.set = new HashSet<>();
        this.listIdSet = new HashSet<>();
        this.creationDate = ZonedDateTime.now();
    }

    public HashSet<T> getSet() {
        return set;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public String getInfo() {
        return String.format(
                "Тип: %s \nДата создания: %s \nКоличество элементов: %s\n",
                this.set.getClass(),
                this.creationDate,
                this.set.size());
    }

    public void addElement(T element) throws WrapperException {
        if (element.getId() == 0L) {
            long newId = this.getNewId();
            element.setId(newId);
        }
        this.addId(element.getId());
        this.set.add(element);
    }

    public void removeElement(long id) throws WrapperException {
        T element = this.getElementById(id);
        if (!this.set.contains(element))
            throw new WrapperException("не найден элемент по id");
        this.removeId(element.getId());
        this.set.remove(element);
    }

    public void removeElement(T element) throws WrapperException {
        if (!this.set.contains(element))
            throw new WrapperException("не найден элемент");
        this.removeId(element.getId());
        this.set.remove(element);
    }

    public void updateElement(long id, T element) throws WrapperException {
        T elementById = this.getElementById(id);
        if (!this.set.contains(elementById))
            throw new WrapperException("не найден элемент по id");
        element.setId(id);
        this.set.remove(elementById);
        this.set.add(element);
    }

    public long getNewId() throws WrapperException {
        if (this.listIdSet.isEmpty())
            return 1L;
        long newId = this.listIdSet.stream().max(Long::compare).get() + 1;
        while (this.listIdSet.contains(newId)) {
            newId++;
        }
        return newId;
    }

    private void addId(long id) throws WrapperException {
        if (this.listIdSet.contains(id))
            throw new WrapperException("элемент с таким id уже существует");
        this.listIdSet.add(id);
    }

    private void removeId(long id) throws WrapperException {
        if (!this.listIdSet.contains(id))
            throw new WrapperException("не найден id элемента");
        this.listIdSet.remove(id);
    }

    public T getElementById(long id) throws WrapperException {
        for (T element : this.set) {
            if (element.getId() == id) {
                return element;
            }
        }
        throw new WrapperException("элемент не найден");
    }

    public boolean contains(long id) {
        for (T element : this.set) {
            if (element.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        this.set.clear();
        this.listIdSet.clear();
    }

    public ArrayList<T> sort() throws WrapperException {
        return this.set.stream().sorted(Wrapperable::compareTo).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<T> reversedSort() throws WrapperException {
        return this.set.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toCollection(ArrayList::new));
    }
}
