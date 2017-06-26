package com.comptel.bst.tools.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;


public class CommonUtils {

    public static File createTempDir() throws IOException {
        return createTempDir("");
    }

    public static File createTempDir(String name) throws IOException {
        Path path = Files.createTempDirectory(name + "-" + CommonConstants.TMP_DIR_PREFIX + "-" + Long.toString(System.currentTimeMillis()));
        return path.toFile();
    }

    public static String decorateOutput(String output, char c) {
        int length = output.length() + 4;
        String delimiter = StringUtils.repeat(c, length);
        StringBuilder b = new StringBuilder(delimiter + "\n");
        b.append(c + " " + output + " " + c + "\n");
        b.append(delimiter);
        return b.toString();
    }

    public static String addPadding(String s, char c) {
        int len = CommonConstants.DEFAULT_PADDING_LENGTH - s.length();
        String pad = StringUtils.repeat(c, len);
        String pre = pad.substring(0, len / 2);
        String post = pad.substring(len / 2);
        return pre + " " + s + " " + post;
    }

    public static String addPadding(String s, char c, int length) {
        int len = length - s.length();
        String pad = StringUtils.repeat(c, len);
        String pre = pad.substring(0, len / 2);
        String post = pad.substring(len / 2);
        return pre + " " + s + " " + post;
    }

    public static <K, V> Collection<V> combineCategories(Map<K, List<V>> map) {
        Collection<V> coll = new ArrayList<V>();
        for (K key : map.keySet()) {
            coll.addAll(map.get(key));
        }
        return coll;
    }

    public static <P, R> R nullSafeApply(P elem, Function<P, R> func) {
        return elem != null ? func.apply(elem) : null;
    }

    public static <T> boolean nullSafeEquals(T first, T second) {
        if (first == null || second == null) {
            if (first == second)
                return true;
        } else if (first.equals(second))
            return true;
        return false;
    }

    public static <T> T findFirst(Collection<T> elems, Function<T, Boolean> condition) {
        return elems != null ? elems.stream().filter(e -> condition.apply(e)).findFirst().orElse(null) : null;
    }

    @SafeVarargs
    public static <T> T getFirstMatch(Function<T, Boolean> matcher, T... array) {
        return Arrays.stream(array).filter(t -> matcher.apply(t)).findFirst().orElse(null);
    }

    public static <K, V> String mapToString(Map<K, V> map, boolean newLines) {
        StringBuilder b = new StringBuilder();
        for (K key : map.keySet()) {
            b.append(key + "=" + map.get(key) + (newLines ? "\n" : " "));
        }
        return b.toString();
    }

    public static <T, K, V> Map<K, V> toMap(Collection<T> coll, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        Map<K, V> map = new HashMap<K, V>();
        coll.forEach(t -> map.put(keyMapper.apply(t), valueMapper.apply(t)));
        return map;
    }

    public static String indent(String string, int baseInd) {
        return StringUtils.repeat(' ', baseInd) + string;
    }

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

    @SafeVarargs
    public static <E> E firstNotNull(E... elems) {
        for (E e : elems) {
            if (e != null)
                return e;
        }
        return null;
    }


    public static <K, V> String mapToString(Map<K, V> map) {
        return mapToString(map, false);
    }

    public static <T> T defaultIfNull(T t, T d) {
        return t != null ? t : d;
    }

    public static <T> void forAllKeys(Map<String, T> first, Map<String, T> second, BiConsumer<T, T> func) {
        Set<String> allKeys = getAllKeys(first, second);
        for (String key : allKeys) {
            T firstObj = first.get(key);
            T secondObj = second.get(key);
            func.accept(firstObj, secondObj);
        }
    }

    private static <K> Set<K> getAllKeys(Map<K, ?> orig, Map<K, ?> changed) {
        Set<K> allKeys = new HashSet<K>(orig.keySet());
        allKeys.addAll(changed.keySet());
        return allKeys;
    }

    public static <T> String collectionToString(Collection<T> list, boolean newLines) {
        StringBuilder b = new StringBuilder();
        for (T value : list) {
            b.append(value + (newLines ? "\n" : ""));
        }
        return b.toString();
    }

    public static void printPhase(String phase) {
        System.out.println(CommonConstants.PHASE_MARKER + phase);
    }

    public static <K, V> void putList(K key, V value, Map<K, List<V>> map) {
        List<V> list = map.get(key);
        if (list == null) {
            list = new ArrayList<V>();
            map.put(key, list);
        }
        list.add(value);
    }

    public static <T> List<T> filter(Collection<T> list, Function<T, Boolean> filter) {
        return list.stream().filter(t -> filter.apply(t)).collect(Collectors.toList());
    }

    public static <K, V> List<K> filterKeys(Map<K, V> map, BiFunction<K, V, Boolean> filter) {
        return map.keySet().stream().filter(k -> filter.apply(k, map.get(k))).collect(Collectors.toList());
    }

    public static <T, R> List<R> mapList(Collection<T> list, Function<T, R> mapper) {
        return list.stream().map(t -> mapper.apply(t)).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static <S, T extends S> List<T> filter(List<S> list, Class<T> clazz) {
        List<T> ret = new ArrayList<T>();
        list.forEach(s -> {
            if (s.getClass().isAssignableFrom(clazz))
                ret.add((T) s);
        });
        return ret;
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

    @FunctionalInterface
    public interface QuadConsumer<A, B, C, D> {
        public void accept(A a, B b, C c, D d);
    }

    public static void printError(String error) {
        System.err.println(CommonConstants.ERROR_MARKER + error);
    }

    public static void printTitle(String title) {
        System.out.println("\n" + CommonUtils.decorateOutput(title, CommonConstants.TITLE_DECOR_MARKER)  + "\n");
    }

    public static void printStartupMsg(String programName) {
        System.out.println("\n" + CommonUtils.decorateOutput("Running " + programName, '#') + "\n");
    }
}
