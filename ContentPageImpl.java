package [REPLACE_ME];

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.factory.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.export.json.SlingModelFilter;
import com.[REPLACE_ME].aem.[REPLACE_ME].[REPLACE_ME].ContentPage;
import com.[REPLACE_ME].aem.[REPLACE_ME].utils.AssetUtil;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;
import com.drew.lang.annotations.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Content page Sling model implementation
 * 
 */
@Model(adaptables = SlingHttpServletRequest.class, adapters = { ContentPage.class,
		ContainerExporter.class }, resourceType = ContentPageImpl.RESOURCE_TYPE_V1, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ContentPageImpl implements ContentPage {

	protected static final String RESOURCE_TYPE_V1 = "[REPLACE_ME]/[REPLACE_ME]/components/page/contentpage";

	private static final Logger logger = LoggerFactory.getLogger(ContentPageImpl.class);

	protected Page page;
	protected Node node;

	private static final String LANDINGTILE_NODE = "landingTile";
	private static final String TILE_HEADLINE = "headline";
	private static final String TILE_EYEBROW = "eyebrow";
	private static final String TILE_DESCRIPTION = "description";
	private static final String TILE_PUBLISH_DATE = "originalPublishDate";
	private static final String TILE_IMAGEURL = "image";
	private static final String MEGAMENU_NODE = "megaMenu";
	private static final String MENU_LAYOUT_TYPE = "layoutType";
	private static final String MENU_CTA_LABEL = "ctaLabel";
	private static final String MENU_CTA_LINK = "ctaLink";
	private static final String MENU_CTA_TYPE = "ctaType";
	private static final String MENU_IMAGE = "image";
	private static final String MENU_IMAGE_LINK = "imageLink";
	private static final String MENU_COLUMN = "column";
	private static final String MENU_COLUMN_DEFAULT = "1";
	private static final String SLING_RESOURCE_TYPE="sling:resourceType";
	private static final String TEXT_RESOURCE_TYPE = "[REPLACE_ME]/components/content/text";
	private static final String SEARCH_LIMIT = "-1";
	private String imageAltText;

	@Inject
	private SlingModelFilter slingModelFilter;
	@Inject
	private ModelFactory modelFactory;

	@ScriptVariable
	protected Page currentPage;

	@ScriptVariable
	protected ValueMap pageProperties;

	@ScriptVariable
	@JsonIgnore
	protected Design currentDesign;

	@ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
	@JsonIgnore
	protected Style currentStyle;

	@ScriptVariable
	@JsonIgnore
	protected ResourceResolver resolver;

	@Self
	private SlingHttpServletRequest request;

	protected String[] keywords = new String[0];
	protected String[] pageTagPath = new String[0];
	protected String designPath;
	protected String staticDesignPath;
	protected String pagetitle;
	protected String pageuuid;
	protected String path;
	protected String pagedescription;

	protected String[] clientLibCategories = new String[0];
	protected Calendar lastModifiedDate;
	protected String templateName;

	protected static final String DEFAULT_TEMPLATE_EDITOR_CLIENTLIB = "wcm.foundation.components.parsys.allowedcomponents";
	protected static final String PN_CLIENTLIBS = "clientlibs";

	protected static final String PN_BRANDSLUG = "brandSlug";

	private Map<String, ComponentExporter> childModels = null;
	private String resourceType;

	@JsonIgnore
	protected Map<String, String> favicons = new HashMap<>();

	@SuppressWarnings("deprecation")
	@PostConstruct
	protected void initModel() {
		if (Objects.nonNull(currentPage)) {
			pagetitle = currentPage.getTitle();
			Object uuid = currentPage.getProperties().get("jcr:uuid");
			if (Objects.nonNull(uuid))
				pageuuid = uuid.toString();
			else
				pageuuid = StringUtils.EMPTY;
			pagedescription = currentPage.getDescription();
			path = currentPage.getPath();
		}
		if (StringUtils.isBlank(pagetitle)) {
			pagetitle = currentPage.getName();
		}
		Tag[] tags = currentPage.getTags();
		keywords = new String[tags.length];
		pageTagPath = new String[tags.length];
		int index = 0;
		int pageTagIndex = 0;
		for (Tag tag : tags) {
			pageTagPath[pageTagIndex++] = tag.getPath();
			keywords[index++] = tag.getTitle(currentPage.getLanguage(false));

		}

		if (currentDesign != null) {
			String designPath = currentDesign.getPath();
			if (!Designer.DEFAULT_DESIGN_PATH.equals(designPath)) {
				this.designPath = designPath;
				if (resolver.getResource(designPath + "/static.css") != null) {
					staticDesignPath = designPath + "/static.css";
				}
				loadFavicons(designPath);
			}
		}
		populateClientlibCategories();
		templateName = extractTemplateName();
	}

	/**
	 * Added no pass constructor and added page null condition
	 * 
	 * to make .model.json url work
	 * 
	 */
	public ContentPageImpl() {
		this.page = currentPage;
	}

	/**
	 * Default constructor
	 * 
	 * @param request
	 * @param page
	 */
	public ContentPageImpl(@NotNull SlingHttpServletRequest request, Page page) {
		this.request = request;
		this.page = page;
		if (page.getContentResource() != null)
			node = page.getContentResource().adaptTo(Node.class);
	}

	protected String extractTemplateName() {
		String templateName = null;
		String templatePath = pageProperties.get(NameConstants.PN_TEMPLATE, String.class);
		if (StringUtils.isNotEmpty(templatePath)) {
			int i = templatePath.lastIndexOf('/');
			if (i > 0) {
				templateName = templatePath.substring(i + 1);
			}
		}
		return templateName;
	}

	@Override
	@JsonIgnore
	public String getLanguage() {
		return currentPage == null ? Locale.getDefault().toLanguageTag()
				: currentPage.getLanguage(false).toLanguageTag();
	}

	@Override
	public Calendar getLastModifiedDate() {
		if (lastModifiedDate == null) {
			lastModifiedDate = pageProperties.get(NameConstants.PN_PAGE_LAST_MOD, Calendar.class);
		}
		return lastModifiedDate;
	}

	@Override
	@JsonIgnore
	public String[] getKeywords() {
		return Arrays.copyOf(keywords, keywords.length);
	}

	@Override
	public String[] getTags() {
		return Arrays.copyOf(pageTagPath, pageTagPath.length);
	}

	@Override
	public String getUUID() {
		return pageuuid;
	}

	@Override
	@JsonIgnore
	public String getDesignPath() {
		return designPath;
	}

	@Override
	@JsonIgnore
	public String getStaticDesignPath() {
		return staticDesignPath;
	}

	@Override
	@JsonIgnore
	@Deprecated
	public Map<String, String> getFavicons() {
		return favicons;
	}

	@Override
	@JsonIgnore
	public String getTemplateName() {
		return templateName;
	}

	@Override
	@JsonIgnore
	public String[] getClientLibCategories() {
		return Arrays.copyOf(clientLibCategories, clientLibCategories.length);
	}

	@NotNull
	@Override
	@JsonIgnore
	public Map<String, ? extends ComponentExporter> getExportedItems() {
		if (childModels == null) {
			childModels = getChildModels(request, ComponentExporter.class);
		}

		return childModels;
	}

	@NotNull
	@Override
	@JsonIgnore
	public String[] getExportedItemsOrder() {
		Map<String, ? extends ComponentExporter> models = getExportedItems();

		if (models.isEmpty()) {
			return ArrayUtils.EMPTY_STRING_ARRAY;
		}
		return models.keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY);

	}

	@NotNull
	@Override
	public String getExportedType() {
		if (StringUtils.isEmpty(resourceType)) {
			resourceType = pageProperties.get(ResourceResolver.PROPERTY_RESOURCE_TYPE, String.class);
			if (StringUtils.isEmpty(resourceType)) {
				resourceType = currentPage.getContentResource().getResourceType();
			}
		}
		return resourceType;
	}

	/**
	 * Returns a map (resource name => Sling Model class) of the given resource
	 * children's Sling Models that can be adapted to {@link T}.
	 *
	 * @param slingRequest the current request
	 * @param modelClass   the Sling Model class to be adapted to
	 * @return a map (resource name => Sling Model class) of the given resource
	 *         children's Sling Models that can be adapted to {@link T}
	 */
	@NotNull
	private <T> Map<String, T> getChildModels(@NotNull SlingHttpServletRequest slingRequest,
			@NotNull Class<T> modelClass) {
		Map<String, T> itemWrappers = new LinkedHashMap<>();

		for (final Resource child : slingModelFilter.filterChildResources(request.getResource().getChildren())) {
			itemWrappers.put(child.getName(), modelFactory.getModelFromWrappedRequest(slingRequest, child, modelClass));
		}

		return itemWrappers;
	}

	protected void loadFavicons(String designPath) {
		favicons.put(PN_FAVICON_ICO, getFaviconPath(designPath, FN_FAVICON_ICO));
		favicons.put(PN_FAVICON_PNG, getFaviconPath(designPath, FN_FAVICON_PNG));
		favicons.put(PN_TOUCH_ICON_120, getFaviconPath(designPath, FN_TOUCH_ICON_120));
		favicons.put(PN_TOUCH_ICON_152, getFaviconPath(designPath, FN_TOUCH_ICON_152));
		favicons.put(PN_TOUCH_ICON_60, getFaviconPath(designPath, FN_TOUCH_ICON_60));
		favicons.put(PN_TOUCH_ICON_76, getFaviconPath(designPath, FN_TOUCH_ICON_76));
	}

	protected String getFaviconPath(String designPath, String faviconName) {
		String path = designPath + "/" + faviconName;
		if (resolver.getResource(path) == null) {
			return null;
		}
		return path;
	}

	protected void populateClientlibCategories() {
		List<String> categories = new ArrayList<>();
		Template template = currentPage.getTemplate();
		if (template != null && template.hasStructureSupport()) {
			Resource templateResource = template.adaptTo(Resource.class);
			if (templateResource != null) {
				addDefaultTemplateEditorClientLib(templateResource, categories);
				addPolicyClientLibs(categories);
			}
		}
		clientLibCategories = categories.toArray(new String[0]);
	}

	protected void addDefaultTemplateEditorClientLib(Resource templateResource, List<String> categories) {
		if (currentPage.getPath().startsWith(templateResource.getPath())) {
			categories.add(DEFAULT_TEMPLATE_EDITOR_CLIENTLIB);
		}
	}

	protected void addPolicyClientLibs(List<String> categories) {
		if (currentStyle != null) {
			Collections.addAll(categories, currentStyle.get(PN_CLIENTLIBS, ArrayUtils.EMPTY_STRING_ARRAY));
		}
	}

	/**
	 * 
	 */
	@Override
	public String getTitle() {
		String title = getTileProperty(TILE_HEADLINE);
		if (Objects.nonNull(page)) {
			if (title == null) {
				title = page.getNavigationTitle();
			}
			if (title == null) {
				title = page.getPageTitle();
			}
			if (title == null) {
				title = page.getTitle();
			}
			if (title == null) {
				title = page.getName();
			}
		}
		return title != null ? title : pagetitle;
	}

	/**
	 *
	 */
	@Override
	public String getDescription() {
		String description = getTileProperty(TILE_DESCRIPTION);
		if (description == null && page != null) {
			description = page.getDescription();
		}
		
		if (Objects.nonNull(currentPage) && currentPage.getContentResource() != null) {
			node = currentPage.getContentResource().adaptTo(Node.class);
		}
		
		description =  description != null ? description : pagedescription;
		
		return description != null ? description : StringUtils.EMPTY;
	}

	@Override
	public String getPath() {
		if (Objects.nonNull(currentPage) &&StringUtils.isNotBlank(path)) {
			return path;
		}
		return StringUtils.EMPTY;
	}

	@Override
	public Object[] getTextItems() {
		Map<String, Map<String, String>> resourceMap = new TreeMap<String, Map<String, String>>();
		if (Objects.nonNull(currentPage) &&StringUtils.isNotBlank(path) && path != null && Objects.nonNull(request)) {
			Iterator<Resource> resources = getQueryResults(path + "/jcr:content", TEXT_RESOURCE_TYPE);
			int index = 1;
			while (resources.hasNext()) {
				Map<String, String> itemMap = new HashMap<String, String>();
				Resource resource = request.getResourceResolver().getResource(resources.next().getPath());
				if (Objects.nonNull(resource) && !(resource instanceof NonExistingResource)) {
					ValueMap properties = ResourceUtil.getValueMap(resource);
					String text = properties.get("text", String.class);
					itemMap.put("text", text);
					itemMap.put("richtext", "true");
					itemMap.put(":type", TEXT_RESOURCE_TYPE);
					resourceMap.put("item_" + index, itemMap);
					index++;
				}
			}
		}
		return resourceMap.entrySet().toArray(ArrayUtils.EMPTY_OBJECT_ARRAY);
	}
	
	@Override
	public Map<String, Object> getLandingTile() {
		Map<String, Object> resourceMap = new TreeMap<String, Object>();
		ArrayList<String> eyebrowList =new ArrayList<String>();
		if (Objects.nonNull(currentPage)) {
			if (Objects.isNull(node)) {
				node = currentPage.getContentResource().adaptTo(Node.class);
			}
			try {
				if (Objects.nonNull(node) && node.hasNode(LANDINGTILE_NODE)) {
					Node landingTile = node.getNode(LANDINGTILE_NODE);
					
					if (landingTile.hasProperty(MENU_IMAGE)) {
						resourceMap.put(MENU_IMAGE, landingTile.getProperty(MENU_IMAGE).getString());
					} else {
						resourceMap.put(MENU_IMAGE, StringUtils.EMPTY);
					}
					
				
					if (landingTile.hasProperty(TILE_EYEBROW) && landingTile.getProperty(TILE_EYEBROW).isMultiple()) {
							Value[] valueProps = landingTile.getProperty(TILE_EYEBROW).getValues();
							  TagManager tagManager = request.getResourceResolver().adaptTo(TagManager.class);
							  for (Value value : valueProps ) {
									if (value.getString() != null) {
										Tag tag = tagManager.resolve(value.getString());
										if (tag != null) {
											eyebrowList.add(tag.getTitle());
										}
									}
							 }
						
					}
					
					if(CollectionUtils.isNotEmpty(eyebrowList)) {
						resourceMap.put(TILE_EYEBROW,eyebrowList);
					}
					else {
						resourceMap.put(TILE_EYEBROW, StringUtils.EMPTY);
					}
					
					if (landingTile.hasProperty(TILE_DESCRIPTION)) {
						resourceMap.put(TILE_DESCRIPTION, landingTile.getProperty(TILE_DESCRIPTION).getString());
					} else {
						resourceMap.put(TILE_DESCRIPTION, StringUtils.EMPTY);
					}
					
				}
			} catch (RepositoryException e) {
				e.printStackTrace();
			}

		}
		logger.debug("resourceMap.entrySet().toArray(ArrayUtils.EMPTY_OBJECT_ARRAY) {}",resourceMap);
		return resourceMap;
	}

	/**
	 *
	 */
	@Override
	@JsonIgnore
	public String getEyebrow() {
		Eyebrow eyebrow = this.getTag();
		if (eyebrow != null) {
			return eyebrow.getEyebrowTitle();
		}
		return "";
	}

	/**
	 *
	 */
	@Override
	@JsonIgnore
	public String getEyebrowName() {

		Eyebrow eyebrow = this.getTag();
		if (eyebrow != null) {
			return eyebrow.getEyebrowName();
		}
		return "";
	}

	/**
	 * @return
	 */
	private Eyebrow getTag() {
		Eyebrow eyebrow = null;
		try {
			if(Objects.isNull(node)) {
				node = currentPage.getContentResource().adaptTo(Node.class);
			}
			if (Objects.nonNull(node) && node.hasNode(LANDINGTILE_NODE)) {
				Node landingTile = node.getNode(LANDINGTILE_NODE);
				if (landingTile.hasProperty(TILE_EYEBROW)) {
					String tagVal = "";
					if (landingTile.getProperty(TILE_EYEBROW).isMultiple()) {
						Value[] valueProps = landingTile.getProperty(TILE_EYEBROW).getValues();
						tagVal = valueProps[0].getString();
					} else {
						tagVal = landingTile.getProperty(TILE_EYEBROW).getString();
					}
					TagManager tagManager = request.getResourceResolver().adaptTo(TagManager.class);
					if (tagVal != null) {
						Tag tag = tagManager.resolve(tagVal);
						if (tag != null) {
							eyebrow = new Eyebrow(tag.getTitle(), tag.getName());
						}
					}
				}
			}
			
		} catch (RepositoryException e) {
			logger.error("Error while fetching the page property", e);
		}
		return eyebrow;
	}

	/**
	 * @param propertyName
	 * @return
	 */
	public String getTileProperty(String propertyName) {
		String tileProperty = null;
		try {
			if (Objects.nonNull(node)  && node.hasNode(LANDINGTILE_NODE)) {
				Node landingTile = node.getNode(LANDINGTILE_NODE);
				if (landingTile.hasProperty(propertyName)) {

					if (landingTile.getProperty(propertyName).isMultiple()) {
						tileProperty = landingTile.getProperty(propertyName).getValues()[0].getString();
					} else {
						tileProperty = landingTile.getProperty(propertyName).getString();
					}

				}
			}
		} catch (RepositoryException e) {
			logger.error("Error while fetching the page property", e);
		}
		return tileProperty;
	}

	/**
	 *
	 */
	@Override
	@JsonIgnore
	public Calendar getOriginalPublishDate() {
		Calendar originalPublishDate = null;
		if (Objects.nonNull(page) ) {
			ValueMap pageProperties = page.getProperties();
			Date _originalPublishDate = pageProperties.get(TILE_PUBLISH_DATE, Date.class);
			if (_originalPublishDate != null) {
				originalPublishDate = Calendar.getInstance();
				originalPublishDate.setTime(_originalPublishDate);
			}
		}
		return originalPublishDate;
	}

	/**
	 *
	 */
	public String getURL() {
		// return Utils.getURL(request, page);
		String url = null;
		if (Objects.nonNull(page)) {
			String vanityURL = page.getVanityUrl();
			url = request.getContextPath() + vanityURL;
			if (StringUtils.isEmpty(vanityURL)) {
				url = request.getContextPath() + page.getPath() + ".html";
			}
		}
		logger.debug("Get URL : " + url);
		return url;
	}

	/**
	 *
	 */
	public String getImageURL() {
		return getTileProperty(TILE_IMAGEURL);
	}

	public String getImageAlt() {

		// init imageAltText
		if (imageAltText == null || imageAltText.trim().equals("")) {
			String fileReference = getImageURL();
			if (fileReference != null && request != null && request.getResourceResolver() != null)
				imageAltText = AssetUtil.getImageAlt(fileReference, request.getResourceResolver());
		}
		return imageAltText;
	}

	/**
	 * @param propertyName
	 * @return String - value of the corresponding property under the 'megaMenu'
	 *         node
	 */
	public String getMegaMenuProperty(String propertyName) {
		String megaMenuProperty = null;
		try {
			if (Objects.nonNull(node) && node.hasNode(MEGAMENU_NODE)) {
				Node megaMenu = node.getNode(MEGAMENU_NODE);
				if (megaMenu.hasProperty(propertyName)) {
					megaMenuProperty = megaMenu.getProperty(propertyName).getString();
				}
			}
		} catch (RepositoryException e) {
			logger.error("Error while fetching the page property", e);
		}
		return megaMenuProperty;
	}

	@Override
	@JsonIgnore
	public String getMenuLayoutType() {
		return getMegaMenuProperty(MENU_LAYOUT_TYPE);
	}

	@Override
	@JsonIgnore
	public String getMenuCTALabel() {
		return getMegaMenuProperty(MENU_CTA_LABEL);
	}

	@Override
	@JsonIgnore
	public String getMenuCTALink() {
		return getMegaMenuProperty(MENU_CTA_LINK);
	}

	@Override
	@JsonIgnore
	public String getMenuCTAType() {
		return getMegaMenuProperty(MENU_CTA_TYPE);
	}

	@Override
	@JsonIgnore
	public String getMenuImage() {
		return getMegaMenuProperty(MENU_IMAGE);
	}

	@Override
	@JsonIgnore
	public String getMenuImageLink() {
		return getMegaMenuProperty(MENU_IMAGE_LINK);
	}

	@Override
	@JsonIgnore
	public String getMenuColumn() {
		if (Objects.nonNull(page)) {
			ValueMap propeties = page.getProperties();
			return (propeties != null && propeties.containsKey(MENU_COLUMN)) ? propeties.get(MENU_COLUMN, String.class)
					: MENU_COLUMN_DEFAULT;
		}
		return null;
	}

	public Iterator<Resource> getQueryResults(String searchRoot, String searchResourceType) {
		Map<String, String> predicateMap = new HashMap<String, String>();

		predicateMap.put("path", searchRoot);
		predicateMap.put("property", SLING_RESOURCE_TYPE);
		predicateMap.put("property.value", searchResourceType);
		predicateMap.put("p.limit",SEARCH_LIMIT);

		QueryBuilder queryBuilder = request.getResourceResolver().adaptTo(QueryBuilder.class);
		Session session = request.getResourceResolver().adaptTo(Session.class);
		Query query = queryBuilder.createQuery(PredicateGroup.create(predicateMap), session);
		SearchResult result = query.getResult();

		Iterator<Resource> resources = result.getResources();

		return resources;
	}

}
