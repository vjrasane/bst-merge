package com.comptel.bst.tools.diff.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.comptel.bst.tools.diff.parser.BSTReader.XmlType;

/*
 * Contains methods for handling the parsing and composing of XML files
 */
public final class FileOperations {

    // XML reader features
    private static final String FEATURE_LOAD_DTD_GRAMMAR = "http://apache.org/xml/features/nonvalidating/load-dtd-grammar";
    private static final String FEATURE_NAMESPACES = "http://xml.org/sax/features/namespaces";
    private static final String FEATURE_NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
    private static final String FEATURE_LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

    // Writes the given object to a string as XML
    public static <T> String marshalXml(T obj, String schemaName) {
        StringWriter writer = new StringWriter();
        marshalXml(writer, obj, schemaName);
        return writer.toString();
    }

    // Writes the given object as XML with the given writer
    public static <T> void marshalXml(Writer writer, T obj, String schemaName) {
        try {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders", "<!DOCTYPE " + schemaName + " SYSTEM \"" + schemaName + ".dtd\">\n");
            marshaller.marshal(obj, writer);
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
    }

    /*
     * Methods for parsing the given XML file, input stream or string to a generic W3C document object
     */
    public static final Document parseXml(File xmlFile) {
        try {
            return parseXml(new FileInputStream(xmlFile));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public static final Document parseXml(InputStream input) {
        DocumentBuilderFactory factory = createDocumentBuilderFactory();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            byte[] xmlBytes = IOUtils.toByteArray(input);
            ByteArrayInputStream bais = new ByteArrayInputStream(xmlBytes);
            Document doc = builder.parse(bais);
            return doc;
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (SAXException e) {
            throw new IllegalStateException(e);
        }
    }

    public static final Document parseXml(String xmlString) {
        return parseXml(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));
    }

    /*
     * Methods for reading the XML type from the document object, file or string
     */
    public static final XmlType readXmlType(Document doc) {
        return XmlType.valueOf(doc.getDoctype().getName().toUpperCase());
    }

    public static final XmlType readXmlType(File xmlFile) {
        return readXmlType(parseXml(xmlFile));
    }

    public static final XmlType readXmlType(String xmlString) {
        return readXmlType(parseXml(xmlString));
    }

    /*
     * Methods for unmarshalling the XML to a JAXBObject from file, input stream or string
     */
    public static final <T> T unmarshalXml(File xmlFile, Class<T> clazz) {
        try {
            return unmarshalXml(new FileInputStream(xmlFile), clazz);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (XMLStreamException e) {
            throw new IllegalStateException(e);
        }
    }

    public static final <T> T unmarshalXml(InputStream input, Class<T> clazz) throws XMLStreamException {
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            XMLInputFactory xmlif = XMLInputFactory.newInstance();
            XMLStreamReader xmler = xmlif.createXMLStreamReader(input);


            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            xmlReader.setFeature(FEATURE_NAMESPACES, true);
            xmlReader.setFeature(FEATURE_NAMESPACE_PREFIXES, true);
            xmlReader.setFeature(FEATURE_LOAD_EXTERNAL_DTD, false);
            xmlReader.setFeature(FEATURE_LOAD_DTD_GRAMMAR, false);

            JAXBElement<T> elem = unmarshaller.unmarshal(xmler, clazz);
            return elem.getValue();
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        } catch (SAXException e) {
            throw new IllegalStateException(e);
        }
    }

    public static final <T> T unmarshalXml(String xmlString, Class<T> clazz) {
        try {
            return unmarshalXml(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)), clazz);
        } catch (XMLStreamException e) {
            throw new IllegalStateException(e);
        }
    }

    /*
     * Some convenience methods
     */

    private static DocumentBuilderFactory createDocumentBuilderFactory() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setExpandEntityReferences(false);
        factory.setNamespaceAware(true);

        try {
            factory.setFeature(FEATURE_LOAD_DTD_GRAMMAR, false);
            factory.setFeature("http://xml.org/sax/features/namespaces", false);
            factory.setFeature("http://xml.org/sax/features/validation", false);
            factory.setFeature(FEATURE_LOAD_EXTERNAL_DTD, false);
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException(e);
        }
        return factory;
    }

}
