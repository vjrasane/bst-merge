package com.comptel.bst.tools.diff.parser.entity.bst.method;

import java.util.List;
import java.util.function.Consumer;

import com.comptel.bst.tools.diff.parser.entity.generic.Conversible;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.Tag;
import com.comptel.bst.tools.diff.parser.entity.jaxb.method.JAXBExec;
import com.comptel.bst.tools.diff.parser.entity.jaxb.method.JAXBMethodBody;


public class MethodBody extends Element implements Conversible<JAXBMethodBody> {

    private static final long serialVersionUID = 1L;

    public static final Tag TAG = Tag.unique("body").hide();

    public MethodBody() {
        super(TAG);
    }

    public MethodBody(JAXBMethodBody body) {
        this();
        this.convert(body);
    }

    @Override
    public void convert(JAXBMethodBody obj) {
        List<JAXBExec> execs = obj.getExec();
        execs.forEach(ex -> addAdditionalInfo(ex, execs));
        execs.forEach(ex -> this.addElement(new Node(ex)));
    }

    private void convertLinkId(JAXBExec source, List<JAXBExec> execs, String jumpValue, Consumer<String> idSetter, Consumer<String> nameSetter) {
        if (jumpValue != null && !jumpValue.isEmpty()) {
            int sourceIndex = execs.indexOf(source);

            int jump = getJumpValue(source.getId(), jumpValue);

            int targetIndex = jump + sourceIndex;

            if (targetIndex < 0 || targetIndex >= execs.size()) {
                throw new IllegalArgumentException("Jump value '" + jump + "'out of bounds in exec '" + source.getId() + "'");
            } else {
                JAXBExec target = execs.get(targetIndex);
                idSetter.accept(target.getId());
                nameSetter.accept(target.getName());
                if (jump != 0) {
                    target.getReferences().put(source.getId(), source.getName());
                }
            }
        }
    }

    private void addAdditionalInfo(JAXBExec source, List<JAXBExec> execs) {
        source.setPosition(Integer.toString(execs.indexOf(source)));

        convertLinkId(source, execs, source.getJump0(), s -> source.setSecondaryJumpId(s), s -> source.setSecondaryTargetName(s));
        convertLinkId(source, execs, source.getJump1(), s -> source.setPrimaryJumpId(s), s -> source.setPrimaryTargetName(s));

        source.getJump().forEach(j -> {
            convertLinkId(source, execs, j.getIndex(), s -> j.setJumpId(s), s -> j.setTargetName(s));
            j.setJumpPos(Integer.toString(source.getJump().indexOf(j)));
        });
    }

    private int getJumpValue(String id, String jump) {
        try {
            return Integer.parseInt(jump);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid jump value '" + jump + "' in exec '" + id + "'", e);
        }
    }

}
