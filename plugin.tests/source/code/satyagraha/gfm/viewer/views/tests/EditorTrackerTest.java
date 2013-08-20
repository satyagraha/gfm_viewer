package code.satyagraha.gfm.viewer.views.tests;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
    private IPath editorPath;

    @Mock
    private FileNature fileNature;

    private EditorTracker editorTracker;

    private ArgumentCaptor<IPropertyListener> propertyListenerCaptor;
   
    @Test
    public void shouldNotifyOnEditorPartOpenedTrackableFile() throws Exception {
        // given
        given(workbenchWindow.getPartService()).willReturn(partService);
        given(partRef.getPart(true)).willReturn(editorPart);
        given(editorPart.getEditorInput()).willReturn(editorInput);
        propertyListenerCaptor = ArgumentCaptor.forClass(IPropertyListener.class);
        willDoNothing().given(editorPart).addPropertyListener(propertyListenerCaptor.capture());
        given(editorInput.getAdapter(IFile.class)).willReturn(editorIFile);
        given(editorIFile.getFullPath()).willReturn(editorPath);
        given(fileNature.isTrackableFile(editorIFile)).willReturn(true);
        editorTracker = new EditorTracker(workbenchWindow, gfmListener, fileNature);
        
        // when
        editorTracker.partOpened(partRef);
        
        // then
        verify(gfmListener).showIFile(editorIFile);
        verifyNoMoreInteractions(gfmListener);
    }
    
    @Test
    public void shouldNotifyOnEditorPartClosed() throws Exception {
        // given
        shouldNotifyOnEditorPartOpenedTrackableFile();
        
        // when
        editorTracker.partClosed(partRef);
        
        // then
        verify(gfmListener).showIFile(null);
        verifyNoMoreInteractions(gfmListener);
    }

    @Test
    public void shouldNotNotifyOnEditorPartOpenedNonTrackableFile() throws Exception {
        // given
        given(workbenchWindow.getPartService()).willReturn(partService);
        given(partRef.getPart(true)).willReturn(editorPart);
        given(editorPart.getEditorInput()).willReturn(editorInput);
        given(editorInput.getAdapter(IFile.class)).willReturn(editorIFile);
        given(editorIFile.getFullPath()).willReturn(editorPath);
        given(fileNature.isTrackableFile(editorIFile)).willReturn(false);
        editorTracker = new EditorTracker(workbenchWindow, gfmListener, fileNature);
        
        // when
        editorTracker.partOpened(partRef);
        
        // then
        verifyNoMoreInteractions(gfmListener);
    }

    @Test
    public void shouldNotifyOnEditorPartSaved() throws Exception {
        // given
        shouldNotifyOnEditorPartOpenedTrackableFile();
        
        IPropertyListener propertyListener = propertyListenerCaptor.getValue();
        assertNotNull(propertyListener);
        
        given(editorPart.isDirty()).willReturn(false);
        
        // when
        propertyListener.propertyChanged(editorPart, IEditorPart.PROP_DIRTY);
        
        // then
        verify(gfmListener, times(2)).showIFile(editorIFile);
    }

    @Test
    public void shouldNotNotifyOnEditorPartSavedWhenNotEnabled() throws Exception {
        // given
        shouldNotifyOnEditorPartOpenedTrackableFile();
        
        IPropertyListener propertyListener = propertyListenerCaptor.getValue();
        assertNotNull(propertyListener);
        
        given(editorPart.isDirty()).willReturn(false);
        
        // when
        editorTracker.setNotificationsEnabled(false);
        propertyListener.propertyChanged(editorPart, IEditorPart.PROP_DIRTY);
        
        // then
        verify(gfmListener, times(1)).showIFile(editorIFile);
    }
    
    @Test
    public void shouldNotifyOnNotifyAlwaysWhenNotEnabled() throws Exception {
        // given
        shouldNotifyOnEditorPartOpenedTrackableFile();
        
        // when
        editorTracker.setNotificationsEnabled(false);
        editorTracker.notifyGfmListenerAlways();
        
        // then
        verify(gfmListener, times(2)).showIFile(editorIFile);
    }
    
}
