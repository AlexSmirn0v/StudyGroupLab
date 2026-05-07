package server.db;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListSet;

import model.StudyGroup;

public class DBCollection extends AbstractCollection<StudyGroup> {
    private final ConcurrentSkipListSet<StudyGroup> groups;
    private final DBManager dbManager;

    public DBCollection(DBManager dbManager) {
        this.groups = new ConcurrentSkipListSet<>();
        this.dbManager = dbManager;
    }

    @Override
    public Iterator<StudyGroup> iterator() {
        return groups.iterator();
    }
    @Override
    public int size() {
        return groups.size();
    }

    @Override
    public synchronized boolean add(StudyGroup group) {
        if (group == null) {
            return false;
        }
        if (dbManager != null) {
            dbManager.persistAddStudyGroup(group);
        }
        return groups.add(group);
    }

    public synchronized boolean addLoaded(StudyGroup group) {
        return groups.add(group);
    }

    public synchronized boolean remove(StudyGroup o, String username) throws IllegalCallerException {
        if (o == null || o.getId() == null) {
            return false;
        }
        if (!username.equals(o.getAuthorName())) {
            throw new IllegalCallerException("Автор группы не совпадает с тем, кто вызывает удаление.");
        }
        if (dbManager != null && !dbManager.persistRemoveStudyGroup(o.getId(), username)) {
            return false;
        }
        return groups.remove(o);
    }

    public synchronized boolean clear(String username) {
        if (dbManager != null) {
            dbManager.persistClearStudyGroups(username);
        }
        boolean removedAny = false;
        for (StudyGroup group : groups) {
            if (username.equals(group.getAuthorName())) {
                boolean removed = groups.remove(group);
                removedAny = removedAny || removed;
            }
        }
        return removedAny;
    }
}