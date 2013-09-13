package org.zephyr.visibility;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.accumulo.core.security.Authorizations;
import org.apache.accumulo.core.security.VisibilityEvaluator;
import org.apache.accumulo.core.security.VisibilityParseException;

public class VisibilityIterator<T extends Visible> implements Iterator<T> {

	private int cursor = 0;
	private int expectedItems = 0;
	private int lastReturned = -1;
	private List<T> values;
	private VisibilityEvaluator ve;

	public VisibilityIterator(final List<T> values, Authorizations authorizations) {
		this.values = values;
		this.ve = new VisibilityEvaluator(authorizations);
		this.expectedItems = values.size();
	}

	public boolean hasNext() {
		if (cursor != this.values.size()) {
			for (int i = cursor; i < this.values.size(); i++) {
				try {
					if (ve.evaluate(this.values.get(i).getColumnVisibility())) {
						return true;
					}
				} catch (VisibilityParseException e) {
					throw new RuntimeException(e);
				}
			}
			return false;
		} else {
			return false;
		}
	}

	public T next() {
		checkForComodification();
		try {
			while (cursor != this.values.size()) {
				T next = values.get(cursor);
				try {
					if (ve.evaluate(next.getColumnVisibility())) {
						lastReturned = cursor++;
						return next;
					}
				} catch (VisibilityParseException e) {
					throw new RuntimeException(e);
				}
				cursor++;
			}
			throw new NoSuchElementException();
		} catch (IndexOutOfBoundsException e) {
			checkForComodification();
			throw new NoSuchElementException();
		}
	}

	public void remove() {
		if (lastReturned == -1) {
			throw new IllegalStateException();
		}
		checkForComodification();
		try {
			values.remove(lastReturned);
			if (lastReturned < cursor) {
				cursor--;
			}
			lastReturned = -1; // we're telling this iterator to be very
								// displeased if we try to remove two elements
								// at a time
			expectedItems = values.size();
		} catch (IndexOutOfBoundsException e) {
			throw new ConcurrentModificationException();
		}
	}

	private void checkForComodification() {
		if (values.size() != expectedItems) {
			throw new ConcurrentModificationException();
		}
	}

}
