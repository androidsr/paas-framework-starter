package paas.framework.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.InputStream;

public class YamlJsonConverter {

    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    public static String yamlToJson(String yamlStr) {
        try {
            JsonNode tree = yamlMapper.readTree(yamlStr);
            return jsonMapper.writeValueAsString(tree);
        } catch (Exception e) {
            throw new RuntimeException("YAML 转 JSON 失败: " + e.getMessage(), e);
        }
    }

    public static String yamlFileToJson(File yamlFile) {
        try {
            JsonNode tree = yamlMapper.readTree(yamlFile);
            return jsonMapper.writeValueAsString(tree);
        } catch (Exception e) {
            throw new RuntimeException("YAML 文件转 JSON 失败: " + e.getMessage(), e);
        }
    }

    public static String yamlStreamToJson(InputStream inputStream) {
        try {
            JsonNode tree = yamlMapper.readTree(inputStream);
            return jsonMapper.writeValueAsString(tree);
        } catch (Exception e) {
            throw new RuntimeException("YAML 流转 JSON 失败: " + e.getMessage(), e);
        }
    }

    public static String jsonToYaml(String jsonStr) {
        try {
            JsonNode tree = jsonMapper.readTree(jsonStr);
            return yamlMapper.writeValueAsString(tree);
        } catch (Exception e) {
            throw new RuntimeException("JSON 转 YAML 失败: " + e.getMessage(), e);
        }
    }

    public static String jsonFileToYaml(File jsonFile) {
        try {
            JsonNode tree = jsonMapper.readTree(jsonFile);
            return yamlMapper.writeValueAsString(tree);
        } catch (Exception e) {
            throw new RuntimeException("JSON 文件转 YAML 失败: " + e.getMessage(), e);
        }
    }

    public static String jsonStreamToYaml(InputStream inputStream) {
        try {
            JsonNode tree = jsonMapper.readTree(inputStream);
            return yamlMapper.writeValueAsString(tree);
        } catch (Exception e) {
            throw new RuntimeException("JSON 流转 YAML 失败: " + e.getMessage(), e);
        }
    }
}
