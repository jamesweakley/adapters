package com.pushtechnology.diffusion.api.adapters.rss;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.api.IOUtils;
import com.pushtechnology.diffusion.api.Logs;
import com.pushtechnology.diffusion.api.XMLUtils;

/**
 * RSS Document.
 * <P>
 * This encapsulates the content that is returned from a fetch from an RSS feed
 * and provides methods for simplifying access rather than having to parse the
 * content XML.
 * <P>
 * Having constructed an RSS document you can use the {@link #fetch()} method
 * after which the content retrieved can be accessed using {@link #getChannel()}
 * or if XML is required then {@link #getXMLDocument()} or {@link #getXML()}.
 */
@Deprecated
public final class RSSDocument {

    private final URL theURL;

    private Document theDocument = null;

    private RSSChannel theChannel = null;

    static final String DOCUMENT_ROOT = "rss";

    static final String CHANNEL = "channel";

    static final String TITLE = "title";

    static final String LINK = "link";

    static final String DESCRIPTION = "description";

    static final String ITEM = "item";

    static final String LANGUAGE = "language";

    static final String COPYRIGHT = "copyright";

    static final String MANAGING_EDITOR = "managingEditor";

    static final String WEB_MASTER = "webMaster";

    static final String PUB_DATE = "pubDate";

    static final String LAST_BUILD_DATE = "lastBuildDate";

    static final String CATEGORY = "category";

    static final String GENERATOR = "generator";

    static final String DOCS = "docs";

    static final String CLOUD = "cloud";

    static final String IMAGE = "image";

    static final String RATING = "rating";

    static final String TEXT_INPUT = "textInput";

    static final String SKIP_HOURS = "skipHours";

    static final String SKIP_DAYS = "skipDays";

    static final String URL = "url";

    static final String NAME = "name";

    static final String AUTHOR = "author";

    static final String ENCLOSURE = "enclosure";

    static final String GUID = "guid";

    static final String SOURCE = "source";

    /**
     * Constructor.
     * <P>
     * 
     * @param url the URL of the RSS feed to connect to.
     * 
     * @throws APIException if the URL is invalid.
     */
    public RSSDocument(String url) throws APIException {
        try {
            theURL = new URL(url);
        }
        catch (Throwable ex) {
            throw new APIException("Invalid url",ex);
        }
    }

    /**
     * Returns the URL.
     * <P>
     * 
     * @return the URL.
     */
    public URL getURL() {
        return theURL;
    }

    /**
     * Connects to RSS feed and downloads content.
     * <P>
     * The channel information downloaded can be obtained using
     * {@link #getChannel}.
     * <P>
     * Alternatively the downloaded content may be accessed as XML using either
     * {@link #getXMLDocument()} or {@link #getXML()}.
     * 
     * @throws APIException if unable to fetch from feed.
     */
    public synchronized void fetch() throws APIException {
        String finalDoc;
        BufferedReader rd = null;
        try {
            URLConnection connection = theURL.openConnection();

            // We have to do this to trim any white space..
            rd = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));

            StringBuilder builder = new StringBuilder(100);
            String line;
            while ((line = rd.readLine())!=null) {
                builder.append(line);
            }

            finalDoc = builder.toString().trim();
        }
        catch (Throwable ex) {
            throw new APIException("RSS Fetch failure",ex);
        }
        finally {
            IOUtils.close(rd);
        }

        try {
            theDocument = XMLUtils.createDocument(finalDoc);
            Element docRoot =
                (Element)theDocument.getElementsByTagName(
                    DOCUMENT_ROOT).item(0);
            theChannel =
                new RSSChannel(
                    (Element)docRoot.getElementsByTagName(CHANNEL).item(0));
        }
        catch (Throwable ex) {
            Logs.finest("Parsing "+finalDoc);
            throw new APIException("RSS Parse error",ex);
        }
    }

    /**
     * Returns the channel object representing the retrieved content.
     * <P>
     * 
     * @return the channel obtained by the last {@link #fetch()} or null if no
     * fetch has been executed.
     */
    public RSSChannel getChannel() {
        return theChannel;
    }

    /**
     * Get the downloaded XML, formatted into a String.
     * <P>
     * 
     * @return the XML representing the content obtained by the last
     * {@link #fetch()} or null if no fetch has been executed.
     * 
     * @throws APIException if a failure occurs in XML serialisation.
     */
    public String getXML() throws APIException {
        if (theDocument!=null) {
            return XMLUtils.serialize(theDocument,true);
        }
        return null;
    }

    /**
     * Gets the downloaded XML document.
     * <P>
     * 
     * @return the document representing the content returned by the last
     * {@link #fetch()} or null if no fetch has been executed.
     */
    public Document getXMLDocument() {
        return theDocument;
    }
}
