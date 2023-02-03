package [REPLACE_ME];

import java.util.Calendar;
import java.util.Map;

import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.drew.lang.annotations.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;

@ConsumerType
public interface ContentPage extends ContainerExporter {
	/**
	 * Key used for the regular favicon file.
	 *
	 * @see #getFavicons()
	 * @since com.adobe.cq.wcm.core.components.models 11.1.0
	 */
	String PN_FAVICON_ICO = "faviconIco";

	/**
	 * Key for the PNG-format favicon file.
	 *
	 * @see #getFavicons()
	 * @since com.adobe.cq.wcm.core.components.models 11.1.0
	 */
	String PN_FAVICON_PNG = "faviconPng";

	/**
	 * Key for the touch-enabled 60px square favicon file.
	 *
	 * @see #getFavicons()
	 * @since com.adobe.cq.wcm.core.components.models 11.1.0
	 */
	String PN_TOUCH_ICON_60 = "touchIcon60";

	/**
	 * Key for the touch-enabled 76px square favicon file.
	 *
	 * @see #getFavicons()
	 * @since com.adobe.cq.wcm.core.components.models 11.1.0
	 */
	String PN_TOUCH_ICON_76 = "touchIcon76";

	/**
	 * Key for the touch-enabled 120px square favicon file.
	 *
	 * @see #getFavicons()
	 * @since com.adobe.cq.wcm.core.components.models 11.1.0
	 */
	String PN_TOUCH_ICON_120 = "touchIcon120";

	/**
	 * Key for the touch-enabled 152px square favicon file.
	 *
	 * @see #getFavicons()
	 * @since com.adobe.cq.wcm.core.components.models 11.1.0
	 */
	String PN_TOUCH_ICON_152 = "touchIcon152";

	/**
	 * Name of the configuration policy property that will store the category of the
	 * client library from which web application resources will be served.
	 *
	 * @since com.adobe.cq.wcm.core.components.models 12.2.0
	 */
	String PN_APP_RESOURCES_CLIENTLIB = "appResourcesClientlib";

	/**
	 * Expected file name for the regular favicon file.
	 *
	 * @see #getFavicons()
	 * @since com.adobe.cq.wcm.core.components.models 11.1.0
	 */
	String FN_FAVICON_ICO = "favicon.ico";

	/**
	 * Expected file name for the PNG-format favicon file.
	 *
	 * @see #getFavicons()
	 * @since com.adobe.cq.wcm.core.components.models 11.1.0
	 */
	String FN_FAVICON_PNG = "favicon_32.png";

	/**
	 * Expected file name for the touch 60px square favicon file.
	 *
	 * @see #getFavicons()
	 * @since com.adobe.cq.wcm.core.components.models 11.1.0
	 */
	String FN_TOUCH_ICON_60 = "touch-icon_60.png";

	/**
	 * Expected file name for the touch 76px square favicon file.
	 *
	 * @see #getFavicons()
	 * @since com.adobe.cq.wcm.core.components.models 11.1.0
	 */
	String FN_TOUCH_ICON_76 = "touch-icon_76.png";

	/**
	 * Expected file name for the touch 120px square favicon file.
	 *
	 * @see #getFavicons()
	 * @since com.adobe.cq.wcm.core.components.models 11.1.0
	 */
	String FN_TOUCH_ICON_120 = "touch-icon_120.png";

	/**
	 * Expected file name for the touch 152px square favicon file.
	 *
	 * @see #getFavicons()
	 * @since com.adobe.cq.wcm.core.components.models 11.1.0
	 */
	String FN_TOUCH_ICON_152 = "touch-icon_152.png";

	String PN_CATEGORY = "category";

	/**
	 * Returns the language of this page, if one has been defined. Otherwise the
	 * default {@link java.util.Locale} will be used.
	 *
	 * @return the language code (IETF BCP 47) for this page
	 * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked
	 *        <code>default</code> in 12.1.0
	 */
	default String getLanguage() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the last modified date of this page.
	 *
	 * @return {@link Calendar} representing the last modified date of this page
	 * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked
	 *        <code>default</code> in 12.1.0
	 */
	default Calendar getLastModifiedDate() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns an array with the page's keywords.
	 *
	 * @return an array of keywords represented as {@link String}s; the array can be
	 *         empty if no keywords have been defined for the page
	 * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked
	 *        <code>default</code> in 12.1.0
	 */
	@JsonIgnore
	default String[] getKeywords() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Retrieves the page's design path.
	 *
	 * @return the design path as a {@link String}
	 * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked
	 *        <code>default</code> in 12.1.0
	 */
	default String getDesignPath() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Retrieves the static design path if {@code static.css} exists in the design
	 * path.
	 *
	 * @return the static design path if it exists, {@code null} otherwise
	 * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked
	 *        <code>default</code> in 12.1.0
	 */
	default String getStaticDesignPath() {
		throw new UnsupportedOperationException();
	}

	/**
	 * <p>
	 * Retrieves the paths to the various favicons for the website as
	 * <code>&lt;favicon_name&gt;:&lt;path&gt;</code> pairs.
	 * </p>
	 * <p>
	 * If a file corresponding to a particular type of favicon is found under the
	 * page's design path, then the &lt;favicon_name&gt;:&lt;path&gt; pair is added
	 * to the map, otherwise that type of favicon is ignored. The following list
	 * defines the currently supported favicons along with their brief descriptions:
	 * </p>
	 * <ul>
	 * <li>{@link #PN_FAVICON_ICO}: The favicon.ico favicon</li>
	 * <li>{@link #PN_FAVICON_PNG}: The png version of the favicon</li>
	 * <li>{@link #PN_TOUCH_ICON_60}: The touch icon with size 60px</li>
	 * <li>{@link #PN_TOUCH_ICON_76}: The touch icon with size 76px</li>
	 * <li>{@link #PN_TOUCH_ICON_120}: The touch icon with size 120px</li>
	 * <li>{@link #PN_TOUCH_ICON_152}: The touch icon with size 152px</li>
	 * </ul>
	 *
	 * @return {@link Map} containing the names of the favicons and their
	 *         corresponding paths
	 * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked
	 *        <code>default</code> in 12.1.0
	 * @deprecated since 12.2.0
	 */
	@Deprecated
	default Map<String, String> getFavicons() {
		throw new UnsupportedOperationException();
	}

	/**
	 * If this page is associated with a Template, then this method will return the
	 * Template's client library categories to be included in the page as defined by
	 * the user in the policy.
	 *
	 * @return an array of client library categories to be included; the array can
	 *         be empty if the page doesn't have an associated template or if the
	 *         template has no client libraries defined.
	 * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked
	 *        <code>default</code> in 12.1.0
	 */
	@JsonIgnore
	default String[] getClientLibCategories() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the template name of the currently used template.
	 *
	 * @return the template name of the current template
	 * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked
	 *        <code>default</code> in 12.1.0
	 */
	default String getTemplateName() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ContainerExporter#getExportedItemsOrder()
	 * @since com.adobe.cq.wcm.core.components.models 12.2.0
	 */
	@NotNull
	@Override
	default String[] getExportedItemsOrder() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ContainerExporter#getExportedItems()
	 * @since com.adobe.cq.wcm.core.components.models 12.2.0
	 */
	@NotNull
	@Override
	default Map<String, ? extends ComponentExporter> getExportedItems() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ContainerExporter#getExportedType()
	 * @since com.adobe.cq.wcm.core.components.models 12.2.0
	 */
	@NotNull
	@Override
	default String getExportedType() {
		throw new UnsupportedOperationException();
	}

	default String getTitle() {
		throw new UnsupportedOperationException();
	}

	default String getDescription() {
		throw new UnsupportedOperationException();
	}

	default String getEyebrow() {
		throw new UnsupportedOperationException();
	}

	default String getEyebrowName() {
		throw new UnsupportedOperationException();
	}

	default Calendar getOriginalPublishDate() {
		throw new UnsupportedOperationException();
	}

	default String getURL() {
		throw new UnsupportedOperationException();
	}

	default String getImageURL() {
		throw new UnsupportedOperationException();
	}

	default String getImageAlt() {
		throw new UnsupportedOperationException();
	}

	default String getMenuLayoutType() {
		throw new UnsupportedOperationException();
	}

	default String getMenuCTALabel() {
		throw new UnsupportedOperationException();
	}

	default String getMenuCTALink() {
		throw new UnsupportedOperationException();
	}

	default String getMenuCTAType() {
		throw new UnsupportedOperationException();
	}

	default String getMenuImage() {
		throw new UnsupportedOperationException();
	}

	default String getMenuImageLink() {
		throw new UnsupportedOperationException();
	}

	default String getMenuColumn() {
		throw new UnsupportedOperationException();
	}
	
	default String[] getTags() {
		throw new UnsupportedOperationException();
	}
	
	default String getUUID() {
		throw new UnsupportedOperationException();
	}
	default String getPath() {
		throw new UnsupportedOperationException();
	}
	default Object[] getTextItems() {
		throw new UnsupportedOperationException();
	}
	default Map<String,Object> getLandingTile() {
		throw new UnsupportedOperationException();
	}
}