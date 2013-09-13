package org.zephyr.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.accumulo.core.security.Authorizations;
import org.zephyr.visibility.VisibilityIterator;

public class SecureEvent {
    /**
     * The UUID of this event
     */
    private String uuid;
    /**
     * The feed name this event came from
     */
    private String feedName;
    /**
     * The ordered list of categorized values that make up this event
     */
    private List<SecureCategorizedValue> orderedCategorizedValues;

    /**
     * A private, no-argument constructor that will allow this object to be built by Kryo
     */
    @SuppressWarnings("unused")
    private SecureEvent() {

    }

    public SecureEvent(final String uuid, final String feedName) {
        this.uuid = uuid;
        this.feedName = feedName;
        this.orderedCategorizedValues = new ArrayList<SecureCategorizedValue>();
    }

    /**
     * @return The UUID of this event
     */
    public String getUuid() {
        return this.uuid;
    }
    
    /**
     * @return The feed name this event belongs to
     */    
    public String getFeedName() {
    	return this.feedName;
    }

    /**
     * Takes in 1..n CategorizedValues and adds them, one at a time, in order, to the orderedCategorizedValues List.
     * @param categorizedValues
     */
    public void add(final SecureCategorizedValue... categorizedValues) {
        for (SecureCategorizedValue cv : categorizedValues) {
            this.orderedCategorizedValues.add(cv);
        }
    }

    /**
     * Takes in a List<CategorizedValue> item that guarantees order, and adds all of the items into the event in order
     * @param categorizedValues
     */
    public void add(final List<SecureCategorizedValue> categorizedValues) {
        for (SecureCategorizedValue cv : categorizedValues) {
            this.orderedCategorizedValues.add(cv);
        }
    }

    /**
     * Searches through the ordered CategorizedValues, respecting all Authorizations by using the Accumulo VisibilityEvaluator,
     * that make up this event and returns a List<CategorizedValue> of elements that match the catalog term we are looking for.  This is very
     * likely to be a subset of the items this Event holds, depending on security level.  The List is an unmodifiableList and can be used
     * for read-only purposes only.
     * @param category
     * @param authorizations
     * @return
     * @throws VisibilityParseException
     */
    public List<SecureCategorizedValue> getByCategory(final String category, final Authorizations authorizations) {
        List<SecureCategorizedValue> returnList = new ArrayList<SecureCategorizedValue>();
        Iterator<SecureCategorizedValue> itr = getIterator(authorizations);
        while (itr.hasNext()) {
        	SecureCategorizedValue cv = itr.next();
            if (cv.getCategory().equals(category)) {
                returnList.add(cv);
            }
        }
        return Collections.unmodifiableList(returnList);
    }

    /**
     * Searches through the ordered CategorizedValues, respecting all Authorizations by using the Accumulo VisibilityEvaluator,
     * that make up this event and returns a List<CategorizedValue> of elements that match the metadata we are looking for.  This is very
     * likely to be a subset of the items this Event holds, depending on security level.  The List is an unmodifiableList and can be used
     * for read-only purposes only.
     * @param metadata
     * @param authorizations
     * @return
     * @throws VisibilityParseException
     */
    public List<SecureCategorizedValue> getByMetadata(final String metadata, final Authorizations authorizations) {
        List<SecureCategorizedValue> returnList = new ArrayList<SecureCategorizedValue>();
        Iterator<SecureCategorizedValue> itr = getIterator(authorizations);
        while (itr.hasNext()) {
        	SecureCategorizedValue cv = itr.next();
            if (cv.getMetadata().equals(metadata)) {
                returnList.add(cv);
            }
        }
        return Collections.unmodifiableList(returnList);
    }

    /**
     * Creates an unmodifiableSet of the Metadata elements present in this Event (provided your Authorization levels allow you to see the item in question)
     * @param authorizations
     * @return
     * @throws VisibilityParseException
     */
    public Set<String> getMetadataKeySet(final Authorizations authorizations) {
        Set<String> returnSet = new TreeSet<String>();
        Iterator<SecureCategorizedValue> itr = getIterator(authorizations);
        while (itr.hasNext()) {
            returnSet.add(itr.next().getMetadata());
        }
        return Collections.unmodifiableSet(returnSet);
    }

    /**
     * Returns an Iterator<CategorizedValue> based upon your Authorization levels.  Due to the fact that our Authorizations directly impact what elements you
     * can see, we had to pass in Authorizations to the creation of our Iterator implementation, which means our Event cannot implement Iterable
     * @param authorizations
     * @return
     */
    public Iterator<SecureCategorizedValue> getIterator(Authorizations authorizations) {
        return new VisibilityIterator<SecureCategorizedValue>(this.orderedCategorizedValues, authorizations);
    }
    
    /**
     * Creates an unmodifiableList of CategorizedValues based on the iterator you receive when you pass in your Authorizations;
     * This is likely not that useful (or not more useful than the getIterator method), but it was far more useful when Unit testing.
     * With it being an unmodifiableList, it allows the for-each loop to be used, but isn't all that much different from using a while
     * loop around the Iterator.  Still, it is there if you want it.
     * @param authorizations
     * @return
     */
    public List<SecureCategorizedValue> getAsList(Authorizations authorizations) {
        List<SecureCategorizedValue> returnList = new ArrayList<SecureCategorizedValue>();
        Iterator<SecureCategorizedValue> itr = getIterator(authorizations);
        while (itr.hasNext()) {
            returnList.add(itr.next());
        }
        return Collections.unmodifiableList(returnList);
    }
    
    /**
     * Removes the provided CategorizedValue provided you have the authorization level to do so (there is never a case where this shouldn't work, but
     * as we are using the Iterator to remove this item, we have to pass them through.  Plus, for some reason if you got a CV with a different
     * Authorization, you shouldn't be able to remove it unless you use a valid authorization.
     * 
     * @param value
     * @param authorizations
     * @return
     */
    public boolean remove(SecureCategorizedValue value, Authorizations authorizations) {
        Iterator<SecureCategorizedValue> itr = getIterator(authorizations);
        while (itr.hasNext()) {
        	SecureCategorizedValue cv = itr.next();
            if (cv.getUuid().equals(value.getUuid())) {
                itr.remove();
                return true;
            }
        }
        return false;
    }

}
