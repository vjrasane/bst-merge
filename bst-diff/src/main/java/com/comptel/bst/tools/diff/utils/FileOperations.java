package com.comptel.bst.tools.diff.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.comptel.bst.tools.diff.parser.BSTReader.XmlType;


public final class FileOperations {

    private static final String FEATURE_LOAD_DTD_GRAMMAR = "http://apache.org/xml/features/nonvalidating/load-dtd-grammar";
    public static final String RESOURCES_DIR = buildPath(false, "src", "main", "resources");
    private static final String FEATURE_NAMESPACES = "http://xml.org/sax/features/namespaces";
    private static final String FEATURE_NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
    private static final String FEATURE_LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

    public static String buildPath(boolean isDir, String... parts) {
        String s = StringUtils.join(File.separator, parts);
        if (isDir)
            s += File.separator;
        return s;
    }

    public static String buildPath(String... parts) {
        return buildPath(true, parts);
    }

    public static final File extractJar(File jarFile) throws IOException {
        JarFile jar = null;
        try {
            jar = new JarFile(jarFile);

            File targetDir = getExtractedDir(jarFile);
            targetDir.mkdir();

            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                File target = new File(targetDir.getAbsolutePath() + File.separator + entry.getName());
                if (entry.isDirectory()) {
                    target.mkdir();
                    continue;
                }

                InputStream is = jar.getInputStream(entry);
                FileOutputStream fos = new FileOutputStream(target);
                while (is.available() > 0)
                    fos.write(is.read());

                fos.close();
                is.close();
            }

            return targetDir;
        } finally {
            if (jar != null)
                try {
                    jar.close();
                } catch (IOException e) {
                    // Ignore
                }
        }
    }

    public static final String fileToString(File xmlFile) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(xmlFile.getAbsolutePath()));
            return new String(encoded, "UTF-8");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> String marshalXml(T obj, String schemaName) {
        StringWriter writer = new StringWriter();
        marshalXml(writer, obj, schemaName);
        return writer.toString();
    }

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

    public static final XmlType readXmlType(Document doc) {
        return XmlType.valueOf(doc.getDoctype().getName().toUpperCase());
    }

    public static final XmlType readXmlType(File xmlFile) {
        return readXmlType(parseXml(xmlFile));
    }

    public static final XmlType readXmlType(String xmlString) {
        return readXmlType(parseXml(xmlString));
    }

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

    public static final void writeJar(File jarFile, List<File> files) throws FileNotFoundException, IOException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        JarOutputStream target = new JarOutputStream(new FileOutputStream(jarFile), manifest);
        for (File file : files) {
            addFileToJar(file, target);
        }
        target.close();
    }

    private static final void addFileToJar(File file, JarOutputStream stream) throws IOException {
        BufferedInputStream in = null;
        try {
            if (file.isDirectory()) {
                String name = file.getPath().replace("\\", "/");
                if (!name.isEmpty()) {
                    if (!name.endsWith("/"))
                        name += "/";
                    JarEntry entry = new JarEntry(name);
                    entry.setTime(file.lastModified());
                    stream.putNextEntry(entry);
                    stream.closeEntry();
                }
                for (File nestedFile : file.listFiles())
                    addFileToJar(nestedFile, stream);
                return;
            }

            JarEntry entry = new JarEntry(file.getPath().replace("\\", "/"));
            entry.setTime(file.lastModified());
            stream.putNextEntry(entry);
            in = new BufferedInputStream(new FileInputStream(file));

            byte[] buffer = new byte[1024];
            while (true) {
                int count = in.read(buffer);
                if (count == -1)
                    break;
                stream.write(buffer, 0, count);
            }
            stream.closeEntry();
        } finally {
            if (in != null)
                in.close();
        }
    }

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

    private static File getExtractedDir(File jarFile) {
        return new File(jarFile.getParent() + File.separator + FilenameUtils.removeExtension(jarFile.getName()) + "_extracted"
                + File.separator);
    }

}
