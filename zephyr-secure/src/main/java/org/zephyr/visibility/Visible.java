package org.zephyr.visibility;

import org.apache.accumulo.core.security.ColumnVisibility;

public interface Visible {
	
	ColumnVisibility getColumnVisibility();

}
