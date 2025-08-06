/*
package paas.framework.starter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChinawuliuMain {

    public static void main(String[] args) throws IOException {
        getDomain3();
    }

    private static void getDomain3() {
        JsoupExecutor executor = new JsoupExecutor("https://www.chinaports.com");
        //List<String> lines = new ArrayList<>();
        //lines.add("/chuanqibiao/1/null/null/null/query");
        List<String> links = executor.initPage(10, "chuanqibiao/1/null/null/null/query", "chuanqibiao/${2}/null/null/null/query");

        List<Map<String, String>> value = executor.getTableValue(links, "body > div.shipcontent.w1200 > div.shipscheduleSpread > div.shiplist > table > tbody", "tr", "td");
        value.forEach(item -> {
            System.out.println(item);
        });
    }

    private static void getDomain2() {
        JsoupExecutor executor = new JsoupExecutor("http://wlbz.chinawuliu.com.cn");
        List<String> pages = executor.initPage(10, "bzxmjh/index.shtml", "bzxmjh/index_${2}.shtml");
        System.out.println("页面大小：" + pages.size());
        List<String> links = executor.getLinks(pages, "div[class='list_R'] > ul > li > a", "href");
        System.out.println("链接数量：" + links.size());

        List<JsoupContent> contentList = new ArrayList<>();
        contentList.add(new JsoupContent(null, "title", GetType.Text, "div[class='box_con'] > h2"));
        contentList.add(new JsoupContent(null, "createTime", GetType.Text, "div[class='box_con'] > h3"));
        contentList.add(new JsoupContent(null, "content", GetType.Text, "div[class='box_con'] > div"));
        //contentList.add(new JsoupContent(null, "img", GetType.Attr, "src", "body > div.box > div.list_R > div > div > div > p > a > img"));
        List<Map<String, String>> result = executor.getFormValue(links, "div[class='list_R']", contentList);
        for (Map<String, String> item : result) {
            for (String key : item.keySet()) {
                System.out.print(key + " = " + item.get(key));
                System.out.print(", ");
            }
            System.out.println("");
        }
    }


    private static void getDomain1() {
        JsoupExecutor executor = new JsoupExecutor("http://qypg.chinawuliu.com.cn");
        List<String> pages = executor.initPage(10, "pgjg/index.shtml", "pgjg/index_${2}.shtml");
        System.out.println("页面大小：" + pages.size());
        List<String> links = executor.getLinks(pages, "div[class='media-body media-body-inner'] > ul[class='list-box list-box--pre'] > li > a", "href");
        System.out.println("链接数量：" + links.size());

        List<JsoupContent> contentList = new ArrayList<>();
        contentList.add(new JsoupContent(null, "title", GetType.Text, "div[class='col-8'] > div[class='ul-title'] > span[class='bg-title']"));
        contentList.add(new JsoupContent(null, "createTime", GetType.Text, "div[class='col-8'] > div[class='ul-title'] > p[class='new-time'] > span"));
        contentList.add(new JsoupContent(null, "content", GetType.Text, "div[class='col-8'] > div[class='text mb-50']"));
        List<Map<String, String>> result = executor.getFormValue(links, "div[class='col-8']", contentList);
        for (Map<String, String> item : result) {
            for (String key : item.keySet()) {
                System.out.print(key + " = " + item.get(key));
                System.out.print(", ");
            }
            System.out.println("");
        }
    }
}
*/
