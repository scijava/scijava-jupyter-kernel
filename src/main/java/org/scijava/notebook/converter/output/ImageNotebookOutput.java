package org.scijava.notebook.converter.output;

/**
 * For displaying images as output.
 *
 * @author Alison Walter
 */
public abstract class ImageNotebookOutput extends NotebookOutput {

    public ImageNotebookOutput(MIME mimeTypeObj, String content) {
        super(mimeTypeObj, content);
    }
}
