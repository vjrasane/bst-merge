package com.comptel.bst.tools.mergetool.parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import com.comptel.bst.tools.common.CommonUtils;
import com.comptel.bst.tools.diff.parser.entity.bst.method.Method;
import com.comptel.bst.tools.diff.parser.entity.bst.step.Step;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.Tag;
import com.comptel.bst.tools.diff.parser.entity.jaxb.method.JAXBMethod;
import com.comptel.bst.tools.diff.parser.entity.jaxb.step.JAXBStep;
import com.comptel.bst.tools.diff.utils.FileOperations;
import com.esotericsoftware.yamlbeans.YamlWriter;

/*
 * Class for writing the given tree into a YAML file
 */
public class BSTWriter {

    public static void writeYaml(File yamlFile, Element yaml) throws IOException {
        // Combines the steps and methods into a single collection
        Collection<Element> elems = CommonUtils.combineCategories(yaml.getElementsByTag());
        Map<String, String> xmls = new TreeMap<String, String>();

        // Loop over the elements and convert them to XML
        for (Element e : elems) {
            String xml = elementToXml(e);
            xmls.put(e.getId(), xml);
        }

        // Use the third party library to write the XML map as a YAML
        YamlWriter writer = new YamlWriter(new FileWriter(yamlFile));
        writer.write(xmls);
        writer.close();
    }

    // Parses a given YAML element to an XML
    private static String elementToXml(Element e) {
        Tag tag = e.getTag();
        switch (tag.getName()) {
        case Method.TAG_NAME:
            JAXBMethod method = new JAXBMethod(e);
            return FileOperations.marshalXml(method, "method");
        case Step.TAG_NAME:
            JAXBStep step = new JAXBStep(e);
            return FileOperations.marshalXml(step, "step");
        default:
            break;
        }
        return null;
    }

}
