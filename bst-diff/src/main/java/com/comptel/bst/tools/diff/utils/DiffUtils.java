package com.comptel.bst.tools.diff.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.comptel.bst.tools.common.CommonUtils;
import com.comptel.bst.tools.diff.comparison.differences.Difference;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.Tag;

public class DiffUtils {

    public static <T extends Element> boolean nullSafeMatches(T first, T second) {
        if (first == null || second == null) {
            if (first == second)
                return true;
        } else if (first.matches(second))
            return true;
        return false;
    }

    public static String nullSafeValue(Element elem) {
        return elem != null ? elem.getValue() : null;
    }

    public static Element getUniqueElem(Tag tag, Collection<Element> elems) {
        if (elems == null || elems.size() == 0)
            return null;
        if (elems.size() > 1)
            throw new IllegalStateException("More than one instance of the unique element " + tag);
        return elems.stream().findFirst().get();
    }

    public static List<Difference> getDifferences(Element orig, Element modified) {
        return orig.compare(null, modified);
    }

    public static String getAttributeId(Element elem, String attrName) {
        String attr = elem.getAttributes().get(attrName);
        if (attr == null)
            throw new IllegalStateException("Missing identifier attribute '" + attrName + "' in element of type '" + elem.getTag() + "'");
        return attr;
    }

    public static Map<String, Element> mapById(Collection<Element> elems) {
        return elems != null ? elems.stream().collect(Collectors.toMap(e -> e.getId(), e -> e)) : Collections.emptyMap();
    }

    public static Collection<Difference> compareCollections(Element parent, Collection<Element> thisElems, Collection<Element> otherElems) {
        List<Element> allElems = new ArrayList<Element>(thisElems);
        allElems.addAll(otherElems);
        List<Difference> diffs = new ArrayList<Difference>();
        for (Element elem : allElems) {
            Element thisElem = thisElems.contains(elem) ? elem : null;
            Element otherElem = otherElems.contains(elem) ? elem : null;
            diffs.addAll(compareElements(parent, thisElem, otherElem));
        }
        return diffs;
    }

    public static Collection<Difference> compareElements(Element context, Element oldObj, Element newObj) {
        List<Difference> diffs = new ArrayList<Difference>();
        if (oldObj == null)
            diffs.add(Difference.added(context, newObj));
        else if (newObj == null)
            diffs.add(Difference.removed(context, oldObj));
        else
            diffs.addAll(oldObj.compare(context, newObj));
        return diffs;
    }

    public static <E extends Element> Collection<Difference> compareMaps(Element context, Map<String, E> orig, Map<String, E> changed) {
        final List<Difference> diffs = new ArrayList<Difference>();
        CommonUtils.forAllKeys(orig, changed, (f, s) -> diffs.addAll(compareElements(context, f, s)));
        return diffs;
    }

    public static <K, V> Collection<Difference> compareMaps(Map<K, V> origMap, Map<K, V> modMap,
            Function<K, Collection<Difference>> comparer) {
        Set<K> allKeys = new HashSet<K>(origMap.keySet());
        allKeys.addAll(modMap.keySet());

        List<Difference> diffs = new ArrayList<Difference>();
        for (K key : allKeys) {
            diffs.addAll(comparer.apply(key));
        }
        return diffs;
    }

    public static String shortNodeId(String id) {
        if (id == null)
            return null;
        String[] split = id.split("_");
        if (split.length < 3)
            return id;
        return split[1].substring(split[1].length() - 2, split[1].length()) + "_" + split[2];
    }

    public static List<Element> getElementPath(Element elem) {
        return getPath(elem, e -> e, true);
    }

    public static List<Element> getElementPath(Element elem, boolean reverse) {
        return getPath(elem, e -> e, reverse);
    }

    public static List<Tag> getTagPath(Element elem) {
        return getPath(elem, e -> e.getTag(), true);
    }

    public static List<Tag> getTagPath(Element elem, boolean reverse) {
        return getPath(elem, e -> e.getTag(), reverse);
    }

    public static <T> List<T> getPath(Element elem, Function<Element, T> mapper, boolean reverse) {
        List<T> path = new ArrayList<T>();
        Element current = elem;
        do {
            path.add(mapper.apply(current));
        } while ((current = current.getParent()) != null);
        if (reverse)
            Collections.reverse(path);
        return path;
    }

    public static Collection<Difference> compareAttr(Element orig, Element mod, Map<String, String> origAttrs, Map<String, String> modAttrs,
            String attrName) {
        String origAttr = origAttrs.get(attrName);
        String modAttr = modAttrs.get(attrName);
        if (!CommonUtils.nullSafeEquals(origAttr, modAttr))
            return Arrays.asList(Difference.attrChanged(orig, mod, attrName));
        return Collections.emptyList();
    }

    public static Collection<Difference> compareAttributes(Element orig, Element mod) {
        Map<String, String> origAttrs = orig.getAttributes();
        Map<String, String> modAttrs = mod.getAttributes();
        return DiffUtils.compareMaps(origAttrs, modAttrs, s -> compareAttr(orig, mod, origAttrs, modAttrs, s));
    }

    public static Collection<Difference> compareByTag(Element parent, Map<Tag, List<Element>> origElems, Map<Tag, List<Element>> modElems, Tag tag) {
        List<Difference> diffs = new ArrayList<Difference>();

        Collection<Element> origList = CommonUtils.defaultIfNull(origElems.get(tag), Collections.<Element> emptyList());
        Collection<Element> modList = CommonUtils.defaultIfNull(modElems.get(tag), Collections.<Element> emptyList());
        if (tag.isIdentifiable()) {
            Map<String, Element> origMap = DiffUtils.mapById(origList);
            Map<String, Element> modMap = DiffUtils.mapById(modList);

            diffs.addAll(DiffUtils.compareMaps(parent, origMap, modMap));
        } else {
            if (tag.isUnique()) {
                Element origElem = DiffUtils.getUniqueElem(tag, origList);
                Element modElem = DiffUtils.getUniqueElem(tag, modList);
                diffs.addAll(DiffUtils.compareElements(parent, origElem, modElem));
            } else
                diffs.addAll(DiffUtils.compareCollections(parent, origList, modList));
        }
        return diffs;
    }

    public static Collection<Difference> compareChildElements(Element parent, Map<Tag, List<Element>> origElems, Map<Tag, List<Element>> modElems) {
        return DiffUtils.compareMaps(origElems, modElems, t -> compareByTag(parent, origElems, modElems, t));
    }

    public static Collection<Difference> compareValues(Element orig, Element mod) {
        if (!CommonUtils.nullSafeEquals(orig.getValue(), mod.getValue()))
            return Arrays.asList(Difference.valueChanged(orig, mod));
        return Collections.emptyList();
    }
}
