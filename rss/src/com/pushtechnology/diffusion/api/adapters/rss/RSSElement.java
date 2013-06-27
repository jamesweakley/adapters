package com.pushtechnology.diffusion.api.adapters.rss;

import org.w3c.dom.Element;

/**
 * Base class for all RSS Elements.
 */
@Deprecated
public abstract class RSSElement {

	private final Element theElement;

	/**
	 * Constructor.
	 * @param element the encapsulated element.
	 */
	RSSElement(Element element) {
		theElement = element;
	}

	/**
	 * Get the element.
	 * @return the element.
	 */
	Element getElement() {
		return theElement;
	}
	
	/**
	 * Get the 'title' value. 
	 * <P>
	 * @return the 'title' value. 
	 */
	public final String getTitle() {
		return getElementText(RSSDocument.TITLE);
	}

	/**
	 * Get the 'link' value.
	 * <P>
	 * @return the 'link' value.
	 */
	public final String getLink() {
		return getElementText(RSSDocument.LINK);
	}

	/**
	 * Get the text content of a named child element.
	 * <P> 
	 * @param name tag name of element.
	 * @return the text content of the element.
	 */
	public final String getElementText(String name) {
		try {
			return 
			    theElement.getElementsByTagName(name).item(0).getTextContent();
		}
		catch (Exception ex) {
			return null;
		}
	}
}
