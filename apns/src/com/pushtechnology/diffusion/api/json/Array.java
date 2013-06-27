package com.pushtechnology.diffusion.api.json;

import java.util.Collection;
import java.util.LinkedList;

/**
 * A List<Object> that serialises as a JSON array.
 * 
 * @author martincowie
 * @since 4.1
 */
@Deprecated
public final class Array extends LinkedList<Object> {

    /**
     * serial uid
     */
    private static final long serialVersionUID = -3406654463118648539L;

    /**
     * Construct an Array object from a list of Object arguments
     * <P>
     * 
     * @param objects Var-arg list of Objects inserted into the resulting object
     * 
     * @throws RuntimeException if any object is not an Array, Dictionary,
     * String or a (boxed) Java primitive
     */
    public Array(final Object... objects) {
        super();
        for (int i = 0;i<objects.length;i++) {
            add(objects[i]);
        }
    }

    /**
     * @see LinkedList#add(int, Object)
     */
    @Override
    public void add(final int index,final Object element) {
        if (JSONCommon.checkType(element)) {
            super.add(index,element);
        }
    }

    /**
     * @see LinkedList#add(Object)
     */
    @Override
    public boolean add(final Object element) {
        boolean added = false;
        if (JSONCommon.checkType(element)) {
            added = super.add(element);
        }
        return added;
    }

    /**
     * @see LinkedList#addAll(Collection)
     */
    @Override
    public boolean addAll(final Collection<? extends Object> collection) {
        for (Object obj:collection) {
            if (!JSONCommon.checkType(obj)) {
                return false;
            }
        }

        return super.addAll(collection);
    }

    /**
     * @see LinkedList#addAll(int, Collection)
     */
    @Override
    public boolean addAll(
    final int index,
    final Collection<? extends Object> collection) {
        for (Object obj:collection) {
            if (!JSONCommon.checkType(obj)) {
                return false;
            }
        }

        return super.addAll(index,collection);
    }

    /**
     * @see LinkedList#addFirst(Object)
     */
    @Override
    public void addFirst(final Object element) {
        if (JSONCommon.checkType(element)) {
            super.addFirst(element);
        }
    }

    /**
     * @see LinkedList#addLast(Object)
     */
    @Override
    public void addLast(final Object element) {
        if (JSONCommon.checkType(element)) {
            super.addLast(element);
        }
    }

    /**
     * @see LinkedList#set(int, Object)
     */
    @Override
    public Object set(final int index,final Object element) {
        if (JSONCommon.checkType(element)) {
            return super.set(index,element);
        }
        return null;
    }

    /**
     * Serialise this object as a JSON Array.
     * 
     * @return string representing the serialised JSON object
     */
    public String toString() {
        final StringBuilder result = new StringBuilder();

        for (int i = 0;i<this.size();i++) {
            if (i>0) {
                result.append(',');
            }
            result.append(JSONCommon.toString(get(i)));
        }

        result.insert(0,'[').append(']');
        return result.toString();
    }
}
