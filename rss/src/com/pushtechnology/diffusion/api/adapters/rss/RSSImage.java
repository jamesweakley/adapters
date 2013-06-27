package com.pushtechnology.diffusion.api.adapters.rss;

import org.w3c.dom.Element;

/**
 * RSS Image element details.
 */
@Deprecated
public final class RSSImage extends RSSElement {

	private static final String WIDTH = "width";

	private static final String HEIGHT = "height";

	/**
	 * Constructor 
	 * @param element
	 */
	RSSImage(Element element) {
		super(element);
	}

	/**
	 * Get 'width' value.
	 * @return 'width' value.
	 */
	public String getWidth() {
		return getElementText(WIDTH);
	}

	/**
	 * Get 'height' value. 
	 * @return 'height' value. 
	 */
	public String getHeight() {
		return getElementText(HEIGHT);
	}

	/**
	 * Get 'url' value. 
	 * @return 'url' value. 
	 */
	public String getURL() {
		return getElementText(RSSDocument.URL);
	}

}
