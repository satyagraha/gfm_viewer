package code.satyagraha.gfm.support.impl;

import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
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

import code.satyagraha.gfm.support.api.GfmConfig;
import code.satyagraha.gfm.support.api.GfmTransformer;
import code.satyagraha.gfm.support.api.GfmWebServiceClient;

public class GfmTransformerDefault implements GfmTransformer {

    private static final Charset UTF_8 = Charset.forName(CharEncoding.UTF_8);

    private final GfmConfig gfmConfig;
    private final Logger logger;
    private final GfmWebServiceClient webServiceClient;
    
    public GfmTransformerDefault(GfmConfig gfmConfig, Logger logger, GfmWebServiceClient webServiceClient) {
        this.gfmConfig = gfmConfig;
        this.logger = logger;
        this.webServiceClient = webServiceClient;
        this.logger.info("initializing");
    }
    
    @Override
    public boolean isMarkdownFile(File file) {
        return file.isFile() && isMarkdownFileExtension(file.getName());
    }

    private boolean isMarkdownFileExtension(String path) {
        String extension = FilenameUtils.getExtension(path);
        return extension.equals("") || extension.equalsIgnoreCase("md");
    }
    
    @Override
    public void transformMarkdownFile(File mdFile, File htFile) throws IOException {
        String mdText = FileUtils.readFileToString(mdFile, UTF_8);
        String htText = transformMarkdownText(mdText);
        CompiledTemplate htmlTemplate = TemplateCompiler.compileTemplate(gfmConfig.getHtmlTemplate());
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("title", htFile.toString());
        vars.put("content", htText);
        vars.put("cssText", gfmConfig.getCssText());
        vars.put("cssUris", gfmConfig.getCssUris());
        vars.put("jsText", gfmConfig.getJsText());
        vars.put("jsUris", gfmConfig.getJsUris());
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

    private boolean useFilteredLinks() {
        return !gfmConfig.useTempDir();
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
        return path != null && isMarkdownFileExtension(path);
    }

    private String makeHtmlPath(URI uri) {
        String fragment = uri.getFragment() != null ? "#" + uri.getFragment() : "";
        File mdPath = new File(uri.getPath());
        File htPath = new File(mdPath.getParent(), htFilename(mdPath.getName()));
        return htPath.getPath() + fragment;
    }
    
}
