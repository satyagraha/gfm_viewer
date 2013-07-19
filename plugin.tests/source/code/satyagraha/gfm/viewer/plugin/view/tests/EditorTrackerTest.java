package code.satyagraha.gfm.viewer.plugin.view.tests;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import code.satyagraha.gfm.support.api.FileNature;
import code.satyagraha.gfm.viewer.views.EditorTracker;
import code.satyagraha.gfm.viewer.views.GfmListener;

@RunWith(MockitoJUnitRunner.class)
public class EditorTrackerTest {
    
    @Mock
    private IWorkbenchWindow workbenchWindow;
    
    @Mock
    private GfmListener gfmListener;
    
    @Mock
    private IPartService partService;
    
    @Mock
    private IWorkbenchPartReference partRef;
    
    @Mock
    private IEditorPart editorPart;
    
    @Mock
    private IEditorInput editorInput;

    @Mock
    private IFile editorIFile;

    @Mock
    private FileNature fileNature;
   
    @Test
    public void shouldNotifyOnEditorPartOpenedTrackableFile() throws Exception {
        // given
        when(workbenchWindow.getPartService()).thenReturn(partService);
        when(partRef.getPart(true)).thenReturn(editorPart);
        when(editorPart.getEditorInput()).thenReturn(editorInput);
        when(editorInput.getAdapter(IFile.class)).thenReturn(editorIFile);
        when(fileNature.isTrackableFile(editorIFile)).thenReturn(true);
        EditorTracker editorTracker = new EditorTracker(workbenchWindow, gfmListener, fileNature);
        
        // when
        editorTracker.partOpened(partRef);
        
        // then
        verify(gfmListener).showIFile(editorIFile);
        verifyNoMoreInteractions(gfmListener);
    }
}
