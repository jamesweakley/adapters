package com.pushtechnology.diffusion.api.adapters.rss;

import org.w3c.dom.Element;

/**
 * RSS Item Element Details.
 *
 */
@Deprecated
public final class RSSItem extends RSSElement {

	/**
	 * Constructor 
	 * @param element
	 */
	RSSItem(Element element) {
		super(element);
	}

	/**
	 * Get 'author' value. 
	 * @return 'author' value. 
	 */
	public String getAuthor() {
		return getElementText(RSSDocument.AUTHOR);
	}

	/**
	 * Get 'enclosure' value. 
	 * @return 'enclosure' value.
	 */
	public String getEnclosure() {
		return getElementText(RSSDocument.ENCLOSURE);
	}

	/**
	 * Get 'guid' value. 
	 * @return 'guid' value. 
	 */
	public String getGuid() {
		return getElementText(RSSDocument.GUID);
	}

	/**
	 * Get 'pubDate' value. 
	 * @return 'pubDate' value.
	 */
	public String getPubDate() {
		return getElementText(RSSDocument.PUB_DATE);
	}

	/**
	 * Get 'source' value. 
	 * @return 'source' value. 
	 */
	public String getSource() {
		return getElementText(RSSDocument.SOURCE);
	}
	
	/**
	 * Get 'description' value. 
	 * @return 'description' value.
	 */
	public String getDescription() {
		return getElementText(RSSDocument.DESCRIPTION);
	}
}
