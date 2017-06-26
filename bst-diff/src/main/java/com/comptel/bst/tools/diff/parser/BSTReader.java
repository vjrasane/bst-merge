package com.comptel.bst.tools.diff.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import com.comptel.bst.tools.diff.parser.entity.Yaml;
import com.comptel.bst.tools.diff.parser.entity.bst.method.Method;
import com.comptel.bst.tools.diff.parser.entity.bst.step.Step;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.jaxb.method.JAXBMethod;
import com.comptel.bst.tools.diff.parser.entity.jaxb.step.JAXBStep;
import com.comptel.bst.tools.diff.utils.FileOperations;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

public class BSTReader {

    public static Yaml readYaml(File yamlFile) {
        Yaml yaml = new Yaml();
        Map<String, String> xmls = readXmls(yamlFile);

        for (String key : xmls.keySet()) {
            String xml = xmls.get(key);
            Element elem = xmlToElement(key, xml);

            yaml.addElement(elem);
        }

        return yaml;
    }

    public static Element xmlToElement(String fileName, String xml) {
        XmlType type = FileOperations.readXmlType(xml);
        Object obj = FileOperations.unmarshalXml(xml, type.jaxbClass);

        switch (type) {
        case STEP:
            JAXBStep step = (JAXBStep) obj;
            return new Step(fileName, step);
        case METHOD:
            JAXBMethod method = (JAXBMethod) obj;
            return new Method(fileName, method);
        default:
            break;
        }

        return null;
    }

    private static Map<String, String> readXmls(File yaml) {
        try {
            YamlReader reader = new YamlReader(new FileReader(yaml));
            Object obj = reader.read();
            if (!(obj instanceof Map))
                throw new IllegalStateException("Failed to parse YAML file '" + yaml.getAbsolutePath() + "'");

            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) obj;
            return map;
        } catch (FileNotFoundException | YamlException e) {
            throw new IllegalStateException("Error parsing YAML file '" + yaml.getAbsolutePath() + "'", e);
        }
    }

    public enum XmlType {
        STEP(JAXBStep.class), METHOD(JAXBMethod.class);

        public final Class<?> jaxbClass;

        private XmlType(Class<?> jaxbClass) {
            this.jaxbClass = jaxbClass;
        }

    }
}
