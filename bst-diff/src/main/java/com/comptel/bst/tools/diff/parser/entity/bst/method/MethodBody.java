package com.comptel.bst.tools.diff.parser.entity.bst.method;

import java.util.List;
import java.util.function.Consumer;

import com.comptel.bst.tools.diff.parser.entity.generic.Conversible;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.Tag;
import com.comptel.bst.tools.diff.parser.entity.jaxb.method.JAXBExec;
import com.comptel.bst.tools.diff.parser.entity.jaxb.method.JAXBMethodBody;

/*
 * The container element for the nodes of the method
 */
public class MethodBody extends Element implements Conversible<JAXBMethodBody> {

    private static final long serialVersionUID = 1L;

    // Body is hidden in the output even though it exists in the XML due to being confusing for the user
    public static final Tag TAG = Tag.unique("body").hide();

    public MethodBody() {
        super(TAG);
    }

    public MethodBody(JAXBMethodBody body) {
        this();
        this.convert(body);
    }

    // Does the conversion from XML object to internal element object
    @Override
    public void convert(JAXBMethodBody obj) {
        List<JAXBExec> execs = obj.getExec();
        /*
         * Loops over each of the 'exec' elements in the XML object.
         * Note that it is important to do these as two separate loops,
         * since otherwise all values are not necessarily set when the node
         * elements are initiated
         */
        execs.forEach(ex -> preProcess(ex, execs)); // Pre-process the node references
        execs.forEach(ex -> this.addElement(new Node(ex))); // Convert the XML elements as they are added
    }

    /*
     *  Pre-processing step that converts the connector values to absolute
     *  and adds incoming references to each node
     */
    private void preProcess(JAXBExec source, List<JAXBExec> execs) {
        // Sets the position in the node list so that it is preserved as far as possible (no functional purpose)
        source.setPosition(Integer.toString(execs.indexOf(source)));

        // Convert the primary and secondary connector attributes
        convertLinkId(source, execs, source.getJump0(), s -> source.setSecondaryJumpId(s), s -> source.setSecondaryTargetName(s));
        convertLinkId(source, execs, source.getJump1(), s -> source.setPrimaryJumpId(s), s -> source.setPrimaryTargetName(s));

        // Convert any additional connector elements
        source.getJump().forEach(j -> {
            convertLinkId(source, execs, j.getIndex(), s -> j.setJumpId(s), s -> j.setTargetName(s));
            j.setJumpPos(Integer.toString(source.getJump().indexOf(j)));
        });
    }

    /*
     * Converts the given connector value of a source node to an absolute value.
     * Two setters are expected, that are used to set the identifier and name values
     * to the source node (would need two separate methods if they were hard coded).
     */
    private void convertLinkId(JAXBExec source, List<JAXBExec> execs, String jumpValue, Consumer<String> idSetter, Consumer<String> nameSetter) {
        // Jump value should never be null or empty in practice, but some tests do not set it explicitly
        if (jumpValue != null && !jumpValue.isEmpty()) {
            int sourceIndex = execs.indexOf(source); // Index of the source node in the node list
            int jump = getJumpValue(source.getId(), jumpValue); // Parse the string value to and integer

            int targetIndex = jump + sourceIndex; // Get the absolute target index using the source index and relative jump

            if (targetIndex < 0 || targetIndex >= execs.size()) {
                // Should never happen, but have to check it
                throw new IllegalArgumentException("Jump value '" + jump + "'out of bounds in exec '" + source.getId() + "'");
            } else {
                JAXBExec target = execs.get(targetIndex); // Get the target XML element based on the absolute index
                idSetter.accept(target.getId()); // Set the target ID in source
                nameSetter.accept(target.getName()); // Set the target name in source
                if (jump != 0) {
                    // If the reference is to another node, add reference objects to it
                    target.getReferences().put(source.getId(), source.getName());
                }
            }
        }
    }

    // Convenience method for parsing the jump value from string
    private int getJumpValue(String id, String jump) {
        try {
            return Integer.parseInt(jump);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid jump value '" + jump + "' in exec '" + id + "'", e);
        }
    }

}
