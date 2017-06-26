package com.comptel.bst.tools.common;

import java.io.File;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

public class FileExistsValidator implements IValueValidator<File> {
    @Override
    public void validate(String name, File file) throws ParameterException {
        if(file == null || !file.exists())
            throw new ParameterException("Parameter '" + name + "' does not point to an existing file.");
    }
}