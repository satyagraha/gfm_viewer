package code.satyagraha.gfm.support.impl;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;

import code.satyagraha.gfm.di.Component;
import code.satyagraha.gfm.di.Component.Scope;
import code.satyagraha.gfm.support.api.MarkdownFileNature;
import code.satyagraha.gfm.support.api.Transformer;

@Component(Scope.PLUGIN)
public class MarkdownFileNatureDefault implements MarkdownFileNature {

    private final Transformer transformer;

    public MarkdownFileNatureDefault(Transformer transformer) {
        this.transformer = transformer;
    }
    
    @Override
    public boolean isTrackableFile(IFile iFile) {
        IPath location = iFile != null ? iFile.getLocation() : null;
        return location != null && transformer.isMarkdownFile(location.toFile());
    }

}
