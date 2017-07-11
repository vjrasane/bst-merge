package com.comptel.bst.tools.diff.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import com.comptel.bst.tools.diff.parser.entity.bst.method.Method;
import com.comptel.bst.tools.diff.parser.entity.bst.step.Step;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.Yaml;
import com.comptel.bst.tools.diff.parser.entity.jaxb.method.JAXBMethod;
import com.comptel.bst.tools.diff.parser.entity.jaxb.step.JAXBStep;
import com.comptel.bst.tools.diff.utils.FileOperations;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

/*
 * Class for reading the contents of YAML and XML files and
 * converting them into the appropriate internal objects
 */
public class BSTReader {

    // Takes the given YAML file and returns an internal tree object
    public static Yaml readYaml(File yamlFile) {
        Yaml yaml = new Yaml();
        Map<String, String> xmls = readXmls(yamlFile); // Read the XMLs to a map with file names as keys

        // Loop over the XML strings, convert them to elements and add to the YAML object
        for (String key : xmls.keySet()) {
            String xml = xmls.get(key);
            Element elem = xmlToElement(key, xml);

            yaml.addElement(elem);
        }
        return yaml;
    }

    // Converts a single XML to an element object
    public static Element xmlToElement(String fileName, String xml) {
        // Get the type of the XML to determine which object to convert to
        XmlType type = FileOperations.readXmlType(xml);

        // Unmarshal into a Java object. We have the class information but the compiler sadly doesn't
        Object obj = FileOperations.unmarshalXml(xml, type.jaxbClass);
        // Tell the compiler which class it is and convert to internal object accordingly
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

    // Reads a single YAML file into a map of XMLs
    private static Map<String, String> readXmls(File yaml) {
        try {
            // We use the third party library here
            YamlReader reader = new YamlReader(new FileReader(yaml));
            Object obj = reader.read();
            if (!(obj instanceof Map))
                /*
                 *  BST logics are stored as key-value pairs of file names and XML contents.
                 *  Thus we can expect that they can be parsed into a map object
                 */
                throw new IllegalStateException("Failed to parse YAML file '" + yaml.getAbsolutePath() + "'");

            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) obj;
            return map;
        } catch (FileNotFoundException | YamlException e) {
            // Something went wrong with the parsing
            throw new IllegalStateException("Error parsing YAML file '" + yaml.getAbsolutePath() + "'", e);
        }
    }

    /*
     *  The two currently supported types of XML that are used in BST logics
     *  These include the information about the JAXB classes that they should be parsed to
     */
    public enum XmlType {
        STEP(JAXBStep.class), METHOD(JAXBMethod.class);

        public final Class<?> jaxbClass;

        private XmlType(Class<?> jaxbClass) {
            this.jaxbClass = jaxbClass;
        }

    }
}
