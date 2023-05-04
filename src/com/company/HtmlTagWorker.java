package com.company;

import com.google.gson.*;

import java.util.Objects;
import java.util.Set;

public class HtmlTagWorker {

    /**
     * Creates a html tag using JsonObject
     */
    public static String createHtmlTag (JsonObject jObj) {
        Set<String> attrs = jObj.keySet();
        StringBuilder str = new StringBuilder();
        str.append("<!DOCTYPE html>\n");
        String lang = "";
        lang = jObj.get("language").getAsString().replaceAll("\"", "");
        if (Objects.equals(lang, "")) {
            str.append("<html>\n");
        } else {
            str.append("<html lang=\"").append(lang).append("\">\n");
        }
        int i = 0;
        for (String a : attrs) {
            if (Objects.equals(a, "head")) {
                str.append(createHeadTag(jObj.get(a)));
            } else if (Objects.equals(a, "body")) {
                str.append(createBodyTag(jObj.get(a)));
            }
            i++;
        }
        str.append("</html>");
        return str.toString();
    }

    /**
     * Creates a head tag from JsonElement
     */
    private static String createHeadTag (JsonElement element) {
        JsonObject attrs = element.getAsJsonObject();
        Set<String> tags = attrs.keySet();
        StringBuilder str = new StringBuilder();
        str.append("<head>\n");
        int i = 0;
        for (String a : tags) {
            if (Objects.equals(a, "meta")) {
                str.append(readMetaData(attrs.getAsJsonObject(a)));
            } else if (Objects.equals(a, "link")) {
                str.append(addLinkTags(attrs.getAsJsonArray(a)));
            } else if (attrs.get(a) instanceof JsonPrimitive) {
                str.append(makePrimitiveTag(a, attrs.get(a).toString().replaceAll("\"", "")));
            }
            i++;
        }
        str.append("</head>\n");
        return str.toString();
    }

    /**
     * Creates a body tag using JsonElement
     */
    private static String createBodyTag (JsonElement element) {
        JsonObject attrs = element.getAsJsonObject();
        Set<String> tags = attrs.keySet();
        StringBuilder str = new StringBuilder();
        str.append("<body>\n");
        int i = 0;
        for (String a : tags) {
            if (Objects.equals(a, "attributes")) {
                str.replace(0, str.length(), "<body ");
                str.append(readAttributes(attrs.getAsJsonObject(a)));
                str.append(">\n");
            } else if (attrs.get(a) instanceof JsonPrimitive) {
                str.append(makePrimitiveTag(a, attrs.get(a).toString().replaceAll("\"", "")));
            } else if (attrs.get(a) instanceof JsonObject) {
                str.append(makeCustomTag(a, attrs.getAsJsonObject(a)));
                System.out.println("");
            }
            //str.append("<").append(a).append(" ");
            i++;
        }
        str.append("</body>\n");
        return str.toString();
    }

    /**
     * Reads meta tags from JsonObject
     */
    private static String readMetaData (JsonObject jObj) {
        Set<String> tags = jObj.keySet();
        StringBuilder str = new StringBuilder();
        int i = 0;
        for (String t : tags) {
            switch (t) {
                case "charset" -> str.append("<meta charset=").append(jObj.get(t).getAsString()).append(">\n");
                case "author" -> str.append("<meta name=\"author\" content=\"").append(jObj.get(t).getAsString()).append("\">\n");
                case "keywords" -> str.append("<meta name=\"keywords\" content=\"").append(jObj.get(t).getAsString()).append("\">\n");
                case "name" -> str.append("<meta name=\"viewport\" content=\"").append(jObj.get(t).getAsString()).append("\">\n"); //fix
            }
            i++;
        }
        return str.toString();
    }

    /**
     * Adds link tags from JsonArray
     */
    private static String addLinkTags (JsonArray jArray) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < jArray.size(); i++) {
            str.append("<link ");
            if (jArray.get(i) instanceof JsonObject) {
                var x = ((JsonObject) jArray.get(i)).getAsJsonObject();
                Set<String> multiple = x.keySet();
                for (String m : multiple) {
                    str.append(m).append("=\"").append(x.get(m).toString().replaceAll("\"", "")).append("\" ");
                }
            } else if (jArray.get(i) instanceof JsonPrimitive) {
                System.out.println("no such instance");
            }
            str.append(">\n");
        }
        return str.toString();
    }

    /**
     * Reads attribute content from JsonObject
     */
    private static String readAttributes (JsonObject jObj) {
        Set<String> attrs = jObj.keySet();
        StringBuilder str = new StringBuilder();
        for (String a : attrs) {
            if (jObj.get(a) instanceof JsonPrimitive) {
                str.append(a).append("=\"").append(jObj.get(a).getAsString()).append("\" ");
            } else if (jObj.get(a) instanceof JsonObject) {
                var x = jObj.getAsJsonObject(a);
                Set<String> multiple = x.keySet();
                StringBuilder mltp = new StringBuilder();
                mltp.append(a).append("=\"");
                for (String m : multiple) {
                    mltp.append(m).append(":").append(x.get(m).toString().replaceAll("\"", "")).append(";");
                }
                mltp.append("\"");
                str.append(mltp);
            }
        }
        return str.toString();
    }

    /**
     * Creates a custom tags from given tag name and tag value
     */
    private static String makeCustomTag (String tagName, JsonObject tagValue) {
        Set<String> tags = tagValue.keySet();
        StringBuilder str = new StringBuilder();

        for (String t : tags) {
            if (Objects.equals(t, "attributes")) {
                str.append("<").append(tagName).append(" ");
                String tag_attr = readAttributes(tagValue.getAsJsonObject(t));
                str.append(tag_attr).append(">\n");
            } else {
                str.append(makePrimitiveTag(t, tagValue.get(t).getAsString()));
            }
        }
        str.append("</").append(tagName).append(">\n");
        return str.toString();

        //return "<" + tagName + ">" + tagValue.toString().replaceAll("\"", "") + "</" + tagName + ">";
    }

    /**
     * Creates a primitive tag from tag name and tag value
     */
    private static String makePrimitiveTag (String tagName, String tagValue) {
        return "<" + tagName + ">" + tagValue + "</" + tagName + ">\n";
    }
}

