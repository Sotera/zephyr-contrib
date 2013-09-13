package org.zephyr.data;

import org.apache.accumulo.core.security.ColumnVisibility;
import org.zephyr.util.UUIDHelper;
import org.zephyr.visibility.Visible;

public class SecureCategorizedValue implements Visible {
	
	private String uuid;
	private String category;
	private String value;
	private String type;
	private String visibility;
	private String metadata;
	
	@SuppressWarnings("unused")
	private SecureCategorizedValue() {
		
	}
	
	public SecureCategorizedValue(final String category, final String value, final String type, final String visibility, final String metadata) {
		this.uuid = UUIDHelper.generateUUID();
		this.category = category;
		this.value = value;
		this.type = type;
		this.visibility = visibility;
		this.metadata = metadata;
	}
	
	public String getUuid() {
		return this.uuid;
	}

	public String getCategory() {
		return category;
	}

	public String getValue() {
		return value;
	}

	public String getType() {
		return type;
	}

	public String getVisibility() {
		return visibility;
	}

	public String getMetadata() {
		return metadata;
	}
	
	public void setMetadata(final String metadata) {
		this.metadata = metadata;
	}

	@Override
	public ColumnVisibility getColumnVisibility() {
		return new ColumnVisibility(visibility);
	}

}
