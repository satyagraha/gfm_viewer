package code.satyagraha.gfm.support.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.CharEncoding;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import code.satyagraha.gfm.di.Component;
import code.satyagraha.gfm.di.Component.Scope;
import code.satyagraha.gfm.support.api.Config;
import code.satyagraha.gfm.support.api.Transformer;
import code.satyagraha.gfm.support.api.WebServiceClient;

@Component(Scope.PLUGIN)
public class TransformerDefault implements Transformer {

    private static Logger LOGGER = Logger.getLogger(TransformerDefault.class.getPackage().getName());
    
    private static final Charset UTF_8 = Charset.forName(CharEncoding.UTF_8);
    private static List<String> MARKDOWN_EXTENSIONS = unmodifiableList(asList("md", "markdown"));

    private final Config config;
    private final WebServiceClient webServiceClient;
    
    public TransformerDefault(Config config, WebServiceClient webServiceClient) {
        this.config = config;
        this.webServiceClient = webServiceClient;
        LOGGER.fine("");
    }
    
    @Override
    public List<String> markdownExtensions() {
        return MARKDOWN_EXTENSIONS;
    }
    
    @Override
    public boolean isMarkdownFile(File file) {
        return file != null && file.isFile() && isMarkdownFileExtension(file.getName());
    }

    private boolean isMarkdownFileExtension(String path) {
        String extension = FilenameUtils.getExtension(path).toLowerCase();
        return MARKDOWN_EXTENSIONS.contains(extension);
    }
    
    private boolean isEmptyFileExtension(String path) {
        String extension = FilenameUtils.getExtension(path);
        return extension.length() == 0;
    }
    
    @Override
    public void transformMarkdownFile(File mdFile, File htFile) throws IOException {
        String mdText = FileUtils.readFileToString(mdFile, UTF_8);
        String htText = transformMarkdownText(mdText);
        CompiledTemplate htmlTemplate = TemplateCompiler.compileTemplate(config.getHtmlTemplate());
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("title", htFile.toString());
        vars.put("content", htText);
        vars.put("cssText", config.getCssText());
        vars.put("cssUris", config.getCssUris());
        vars.put("jsText", config.getJsText());
        vars.put("jsUris", config.getJsUris());
        String rendered = (String) TemplateRuntime.execute(htmlTemplate, vars);
        FileUtils.writeStringToFile(htFile, rendered, UTF_8);
    }

    @Override
    public String transformMarkdownText(String mdText) {
        String responseText = webServiceClient.transform(mdText);
        return useFilteredLinks() ? filterLinks(responseText) : responseText;
    }
    
    @Override
    public String htFilename(String mdFilename) {
        return String.format(".%s.md.html", getBaseName(mdFilename));
    }

    @Override
    public File createHtmlFile(File mdFile) {
        String htDir = config.useTempDir() ? System.getProperty("java.io.tmpdir") : mdFile.getParent();
        return new File(htDir, htFilename(mdFile.getName()));
    }
    
    @Override
    public boolean canSkipTransformation(File mdFile, File htFile) {
        return isUpToDate(mdFile, htFile);
    }
    
    private boolean isUpToDate(File mdFile, File htFile) {
        long mdFileTimestamp = mdFile.lastModified();
        long htFileTimestamp = htFile.canRead() ? htFile.lastModified() : 0;
        return mdFileTimestamp < htFileTimestamp;
    }
    
    private boolean useFilteredLinks() {
        return !config.useTempDir();
    }

    private String filterLinks(String responseText) {
        Document doc = Jsoup.parseBodyFragment(responseText);
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String linkHref = link.attr("href");
            URI uri;
            try {
                uri = new URI(linkHref);
            } catch (URISyntaxException e) {
                continue;
            }
            if (isLocalURI(uri) && isMarkdownPath(uri.getPath())) {
                link.attr("href", makeHtmlPath(uri));
            }
        }
        return doc.body().html();
    }

    private boolean isLocalURI(URI uri) {
        return !uri.isAbsolute()
                && isBlank(uri.getAuthority())
                && isBlank(uri.getQuery());
    }

    private boolean isMarkdownPath(String path) {
        return path != null && (isMarkdownFileExtension(path) || isEmptyFileExtension(path));
    }

    private String makeHtmlPath(URI uri) {
        String fragment = uri.getFragment() != null ? "#" + uri.getFragment() : "";
        File mdPath = new File(uri.getPath());
        File htPath = new File(mdPath.getParent(), htFilename(mdPath.getName()));
        return htPath.getPath() + fragment;
    }
    
}
