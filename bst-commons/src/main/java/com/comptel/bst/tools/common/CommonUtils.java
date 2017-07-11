package com.comptel.bst.tools.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/*
 * Contains common methods or functionality that doesnt fit elsewhere
 */
public class CommonUtils {

    // Constructs a string where the given output is surrounded by the given character
    public static String decorateOutput(String output, char c) {
        int length = output.length() + 4;
        String delimiter = StringUtils.repeat(c, length);
        StringBuilder b = new StringBuilder(delimiter + "\n");
        b.append(c + " " + output + " " + c + "\n");
        b.append(delimiter);
        return b.toString();
    }

    /*
     *  Padds the given message with the given character so that the total length
     *  of the string equals the given length.
     */
    public static String addPadding(String s, char c, int length) {
        int len = length - s.length();
        String pad = StringUtils.repeat(c, len);
        String pre = pad.substring(0, len / 2);
        String post = pad.substring(len / 2);
        return pre + " " + s + " " + post;
    }

    public static String addPadding(String s, char c) {
        return addPadding(s, c, CommonConstants.DEFAULT_PADDING_LENGTH);
    }

    // Flattens a map of collections into a single collection
    public static <K, V> Collection<V> combineCategories(Map<K, List<V>> map) {
        Collection<V> coll = new ArrayList<V>();
        for (K key : map.keySet()) {
            coll.addAll(map.get(key));
        }
        return coll;
    }

    // Applies the given function safeguarding for null pointers
    public static <P, R> R nullSafeApply(P elem, Function<P, R> func) {
        return elem != null ? func.apply(elem) : null;
    }

    // Determines whether the given elements are equal. Null safe.
    public static <T> boolean nullSafeEquals(T first, T second) {
        if (first == null || second == null) {
            if (first == second)
                return true;
        } else if (first.equals(second))
            return true;
        return false;
    }

    // Finds the first occurrence of an element that satisfies the given condition
    public static <T> T findFirst(Collection<T> elems, Function<T, Boolean> condition) {
        return elems != null ? elems.stream().filter(e -> condition.apply(e)).findFirst().orElse(null) : null;
    }

    // Constructs a string representation of the given map
    public static <K, V> String mapToString(Map<K, V> map, boolean newLines) {
        StringBuilder b = new StringBuilder();
        for (K key : map.keySet()) {
            b.append(key + "=" + map.get(key) + (newLines ? "\n" : " "));
        }
        return b.toString();
    }


    public static <K, V> String mapToString(Map<K, V> map) {
        return mapToString(map, false);
    }

    // Creates a deep copy of a given serializable object
    @SuppressWarnings("unchecked")
    public static <T> T deepCopy(T obj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);

            oos.flush();

            oos.close();
            bos.close();
            byte[] byteData = bos.toByteArray();

            ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
            return (T) new ObjectInputStream(bais).readObject();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (ClassNotFoundException e) {
            // Not possible
            throw new IllegalStateException(e);
        }
    }

    // Returns the first value if not null, the second value otherwise
    public static <T> T defaultIfNull(T t, T d) {
        return t != null ? t : d;
    }

    // Retrieves a pair of objects for each key and executes the given function for them
    public static <T> void forAllKeys(Map<String, T> first, Map<String, T> second, BiConsumer<T, T> func) {
        Set<String> allKeys = getAllKeys(first, second);
        for (String key : allKeys) {
            T firstObj = first.get(key);
            T secondObj = second.get(key);
            func.accept(firstObj, secondObj);
        }
    }

    // Get a set containing all keys of the two maps
    private static <K> Set<K> getAllKeys(Map<K, ?> orig, Map<K, ?> changed) {
        Set<K> allKeys = new HashSet<K>(orig.keySet());
        allKeys.addAll(changed.keySet());
        return allKeys;
    }

    // Construct a string representation of the given collection
    public static <T> String collectionToString(Collection<T> list, boolean newLines) {
        StringBuilder b = new StringBuilder();
        for (T value : list) {
            b.append(value + (newLines ? "\n" : ""));
        }
        return b.toString();
    }

    // Maps the given list into another list with the given mapper function
    public static <T, R> List<R> mapList(Collection<T> list, Function<T, R> mapper) {
        return list.stream().map(t -> mapper.apply(t)).collect(Collectors.toList());
    }

    @FunctionalInterface
    public interface BiConsumer<A, B> {
        public void accept(A a, B b);
    }

    @FunctionalInterface
    public interface TriFunc<A, B, C, D> {
        public D apply(A a, B b, C c);
    }

    @FunctionalInterface
    public interface TriConsumer<A, B, C> {
        public void accept(A a, B b, C c);
    }

    // Print a notification of a single phase execution
    public static void printPhase(String phase) {
        System.out.println(CommonConstants.PHASE_MARKER + phase);
    }

    // Print an error message
    public static void printError(String error) {
        System.err.println(CommonConstants.ERROR_MARKER + error);
    }

    // Print the header message of a program execution
    public static void printTitle(String title) {
        System.out.println("\n" + CommonUtils.decorateOutput("Running " + title, CommonConstants.TITLE_DECOR_MARKER)  + "\n");
    }

}
