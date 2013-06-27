package com.pushtechnology.diffusion.api.adapters.rss;

import org.w3c.dom.Element;

/**
 * RSS Text Input Element Details.
 *
 */
public final class RSSTextInput extends RSSElement {

	/**
	 * Constructor 
	 * @param element
	 */
	RSSTextInput(Element element) {
		super(element);
	}

	/**
	 * Get 'description' value. 
	 * @return 'description' value. 
	 */
	public String getDescription() {
		return getElementText(RSSDocument.DESCRIPTION);
	}

	/**
	 * Get 'name' value. 
	 * @return 'name' value.
	 */
	public String getName() {
		return getElementText(RSSDocument.NAME);
	}
}
