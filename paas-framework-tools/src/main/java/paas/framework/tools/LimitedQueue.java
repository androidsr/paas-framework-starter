package paas.framework.tools;

import java.util.LinkedList;

public class LimitedQueue<E> extends LinkedList<E> {
    private final int limit;

    public LimitedQueue(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(E e) {
        boolean added = super.add(e);
        if (size() > limit) {
            super.remove();
        }
        return added;
    }
}