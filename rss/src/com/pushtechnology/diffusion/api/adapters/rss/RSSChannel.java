package com.pushtechnology.diffusion.api.adapters.rss;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * RSS Channel.
 */
@Deprecated
public final class RSSChannel extends RSSElement {

	private List<RSSItem> theItems;

	/**
	 * Constructor 
	 * @param element
	 */
	RSSChannel(Element element) {
		super(element);

		// Build items
		theItems = new ArrayList<RSSItem>();

		NodeList itemList = getElement().getElementsByTagName(RSSDocument.ITEM);

		for (int i = 0; i < itemList.getLength(); i++) {
			theItems.add(new RSSItem((Element) itemList.item(i)));
		}
	}

	/**
	 * Return the list of items for the channel. 
	 * @return list of items.
	 */
	public List<RSSItem> getItems() {
		return theItems;
	}

	/**
	 * Get 'language' value. 
	 * @return 'language' value. 
	 */
	public String getLanguage() {
		return getElementText(RSSDocument.LANGUAGE);
	}

	/**
	 * Get 'copyright' value. 
	 * @return 'copyright' value.
	 */
	public String getCopyright() {
		return getElementText(RSSDocument.COPYRIGHT);
	}

	/**
	 * Get 'managingEditor' value. 
	 * @return 'managingEditor' value. 
	 */
	public String getManagingEditor() {
		return getElementText(RSSDocument.MANAGING_EDITOR);
	}

	/**
	 * Get 'pubDate' value. 
	 * @return 'pubDate' value.
	 */
	public String getPubDate() {
		return getElementText(RSSDocument.PUB_DATE);
	}

	/**
	 * Get 'lastBuildDate' value. 
	 * @return 'lastBuildDate' value. 
	 */
	public String getLastBuildDate() {
		return getElementText(RSSDocument.LAST_BUILD_DATE);
	}

	/**
	 * Get 'docs' value.
	 * @return 'docs' value.
	 */
	public String getDocs() {
		return getElementText(RSSDocument.DOCS);
	}

	/**
	 * Get 'cloud' value.
	 * @return 'cloud' value.
	 */
	public String getCloud() {
		return getElementText(RSSDocument.CLOUD);
	}

	/**
	 * Get Image.
	 * @return Image object.
	 */
	public RSSImage getImage() {
		Element element = 
		    (Element) getElement().getElementsByTagName(
		        RSSDocument.IMAGE).item(0);
		return new RSSImage(element);
	}

	/**
	 * Get 'rating' value.
	 * @return 'rating' value.
	 */
	public String getRating() {
		return getElementText(RSSDocument.RATING);
	}

	/**
	 * Get Get 'textInput'.
	 * @return 'textInput' object.
	 */
	public RSSTextInput getTextInput() {
		Element element = 
		    (Element)getElement().getElementsByTagName(
		        RSSDocument.TEXT_INPUT).
		            item(0);
		return new RSSTextInput(element);
	}

	/**
	 * Get 'skipHours' value. 
	 * @return Get 'skipHours' value.
	 */
	public String getSkipHours() {
		return getElementText(RSSDocument.SKIP_HOURS);
	}

	/**
	 * Get 'skipDays' value. 
	 * @return 'skipDays' value. 
	 */
	public String getSkipDays() {
		return getElementText(RSSDocument.SKIP_DAYS);
	}

	/**
	 * Get 'generator' value. 
	 * @return 'generator' value. 
	 */
	public String getGenerator() {
		return getElementText(RSSDocument.GENERATOR);
	}
	
	/**
	 * Get 'description' value. 
	 * @return 'description' value.
	 */
	public String getDescription() {
		return getElementText(RSSDocument.DESCRIPTION);
	}
}
