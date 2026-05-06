package server.db;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListSet;
import model.StudyGroup;

public class DBCollection extends AbstractCollection<StudyGroup>{
    private ConcurrentSkipListSet<StudyGroup> groups;

    public DBCollection() {
        this.groups = new ConcurrentSkipListSet<>();
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
    public boolean add(StudyGroup group) {
        return false;
    }
    public boolean remove(StudyGroup o, int id) throws IllegalCallerException {
        if (o == null || o.getId() == null) {
            return false;
        }
        if (o.getId() != id) {
            throw new IllegalCallerException("ID группы не совпадает с переданным аргументом");
        }
        return groups.remove(o);
    }
}
