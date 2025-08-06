package paas.framework.spring.ai;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import paas.framework.tools.PaasUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Slf4j
public class JsoupExecutor {
    private static String regex = "\\$\\{(\\d+)}";
    private Set<String> old = new HashSet<>();
    private int errorNotPage;
    private String root;

    public JsoupExecutor(String root) {
        this.root = root;
    }

    /**
     * 初始化要获取的页面信息
     *
     * @param urls
     * @return
     */
    public List<String> initPage(int max, String... urls) {
        List<String> pages = new ArrayList<>();
        for (int i = 0; i < urls.length; i++) {
            String url = urls[i];
            Pattern r = Pattern.compile(regex);
            Matcher m = r.matcher(url);
            if (m.find()) {
                String start = m.group();
                start = start.replaceAll("\\$\\{", "").replaceAll("}", "");
                for (int j = Integer.valueOf(start); j <= max; j++)
                    pages.add(url.replaceAll("\\$\\{" + start + "}", j + ""));
            } else {
                pages.add(url);
            }
        }
        return pages;
    }

    /**
     * 获取页面下所有的链接信息
     *
     * @param pages
     * @param findLabel
     * @param attr
     * @return
     */
    public List<String> getLinks(List<String> pages, String findLabel, String attr) {
        List<String> links = new ArrayList<>();
        for (String path : pages) {
            String url = String.join("/", root, path);
            if (old.contains(url)) {
                continue;
            }
            old.add(url);
            Document doc;
            try {
                doc = Jsoup.connect(url).get();
            } catch (Exception e) {
                //System.out.println(e.getMessage());
                errorNotPage++;
                if (errorNotPage > 5) {
                    break;
                }
                continue;
            }
            Elements nodes = doc.select(findLabel);
            if (nodes == null) {
                continue;
            }
            for (Element element : nodes) {
                String attrValue = element.attr(attr);
                links.add(attrValue);
            }
        }
        return links;
    }

    public Elements getLinkRecursion(String path, String findLabel, String attr) {
        if (errorNotPage > 5) {
            return null;
        }
        String url = String.join("/", root, path);
        if (old.contains(url)) {
            return null;
        }
        old.add(url);
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (Exception e) {
            errorNotPage++;
            return null;
        }
        Elements result = new Elements();
        Elements nodes = doc.select(findLabel);
        if (nodes == null) {
            return null;
        }
        for (Element element : nodes) {
            String link = element.attr(attr);
            Elements child = getLinkRecursion(link, findLabel, attr);
            if (child != null) {
                result.addAll(child);
            }
        }
        result.addAll(nodes);
        return nodes;
    }

    public List<Map<String, String>> getFormValue(List<String> links, String findLabel, List<JsoupContent> jsoupContents) {
        List<Map<String, String>> result = new ArrayList<>();
        for (String path : links) {
            String url = String.join("/", root, path);
            if (old.contains(url)) {
                continue;
            }
            old.add(url);
            Document doc;
            try {
                doc = Jsoup.connect(url).get();
            } catch (Exception e) {
                continue;
            }
            Elements nodes;
            if (PaasUtils.isNotEmpty(findLabel)) {
                nodes = doc.select(findLabel);
            } else {
                nodes = doc.getAllElements();
            }

            Map<String, String> item = new HashMap<>();
            item.put("url", url);
            for (JsoupContent extract : jsoupContents) {
                Elements select = nodes.select(extract.getFindLabel());
                String value = "";
                if (extract.getType() == GetType.Text) {
                    value = select.text();
                } else if (extract.getType() == GetType.Attr) {
                    value = select.attr(extract.getAttrName());
                } else if (extract.getType() == GetType.Html) {
                    value = select.outerHtml();
                }
                String text = contentHandler(extract, value);
                if (PaasUtils.isNotEmpty(text)) {
                    item.put(extract.getName(), text);
                } else {
                    break;
                }
            }
            result.add(item);
        }
        return result;
    }

    public List<Map<String, String>> getTableValue(List<String> links, String tableLabel, String trLabel, String tdLabel) {
        List<Map<String, String>> result = new ArrayList<>();
        for (String path : links) {
            String url = String.join("/", root, path);
            if (old.contains(url)) {
                continue;
            }
            old.add(url);
            Document doc;
            try {
                doc = Jsoup.connect(url).get();
            } catch (Exception e) {
                continue;
            }
            Elements tables = doc.select(tableLabel);
            if (tables.size() != 0) {
                Elements trElements = tables.get(0).select(trLabel);
                Map<String, String> item;
                for (Element tr : trElements) {
                    Elements tdElements = tr.select(tdLabel);
                    item = new HashMap<>();
                    for (int i = 0; i < tdElements.size(); i++) {
                        item.put("td" + i, tdElements.get(i).text());
                    }
                    result.add(item);
                }
            }
        }
        return result;
    }

    /**
     * 对值进行按规则处理
     *
     * @param extract
     * @param text
     * @return
     */
    private String contentHandler(JsoupContent extract, String text) {
        if (PaasUtils.isEmpty(text)) {
            return "";
        }
        if (extract.getTextFilter() == null) {
            return text;
        }
        TextFilter filter = extract.getTextFilter();
        switch (filter.getFilterType()) {
            case StartsWith:
                for (String item : filter.getFilterText()) {
                    if (text.startsWith(item)) {
                        return text.replace(text, item);
                    }
                }
                return text;
            case EndsWith:
                for (String item : filter.getFilterText()) {
                    if (text.endsWith(item)) {
                        return text.replace(text, item);
                    }
                }
                return text;
            case Contains:
                for (String item : filter.getFilterText()) {
                    if (text.contains(item)) {
                        return text;
                    }
                }
            case Text:
                return text.trim();
            default:
                return text;
        }
    }
}
