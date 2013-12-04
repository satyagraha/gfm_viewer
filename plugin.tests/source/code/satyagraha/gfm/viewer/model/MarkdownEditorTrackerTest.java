package code.satyagraha.gfm.viewer.model;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import code.satyagraha.gfm.support.api.MarkdownFileNature;
import code.satyagraha.gfm.ui.api.PageEditorTracker;
import code.satyagraha.gfm.viewer.model.api.MarkdownListener;
import code.satyagraha.gfm.viewer.model.impl.MarkdownEditorTrackerDefault;

@RunWith(MockitoJUnitRunner.class)
public class MarkdownEditorTrackerTest {

    @Mock
    private IWorkbenchPage workbenchPage;

    @Mock
    private MarkdownListener markdownListener;

    @Mock
    private IEditorReference editorRef;

    @Mock
    private IEditorPart editorPart;

    @Mock
    private IEditorInput editorInput;

    @Mock
    private IFile editorIFile;

    @Mock
    private IPath editorPath;

    @Mock
    private MarkdownFileNature fileNature;

    @Mock
    private PageEditorTracker pageEditorTracker;

    @InjectMocks
    private MarkdownEditorTrackerDefault editorTracker;

    @Captor
    private ArgumentCaptor<IPropertyListener> propertyListenerCaptor;

    @Test
    public void shouldStartCorrectly() {
        // given

        // when
        editorTracker.start();

        // then
        verify(pageEditorTracker, times(1)).subscribe(editorTracker);
    }

    @Test
    public void shouldStopCorrectly() {
        // given

        // when
        editorTracker.start();
        editorTracker.stop();

        // then
        verify(pageEditorTracker, times(1)).unsubscribe(editorTracker);
    }

    @Test
    public void shouldNotifyOnEditorPartOpenedTrackableFile() throws Exception {
        // given
        given(editorPart.getEditorInput()).willReturn(editorInput);
        given(editorInput.getAdapter(IFile.class)).willReturn(editorIFile);
        given(fileNature.isTrackableFile(editorIFile)).willReturn(true);

        // when
        editorTracker.start();
        editorTracker.addListener(markdownListener);
        editorTracker.editorShown(editorPart);

        // then
        verify(markdownListener, times(1)).notifyEditorFile(editorIFile);
        verifyNoMoreInteractions(markdownListener);
    }

    @Test
    public void shouldNotNotifyOnEditorPartOpenedNoEditorInput() throws Exception {
        // given
        given(editorPart.getEditorInput()).willReturn(null);

        // when
        editorTracker.start();
        editorTracker.addListener(markdownListener);
        editorTracker.editorShown(editorPart);

        // then
        verify(markdownListener, never()).notifyEditorFile(editorIFile);
        verifyNoMoreInteractions(markdownListener);
    }

    @Test
    public void shouldNotNotifyOnEditorPartOpenedNoEditorFile() throws Exception {
        // given
        given(editorPart.getEditorInput()).willReturn(editorInput);
        given(editorInput.getAdapter(IFile.class)).willReturn(null);

        // when
        editorTracker.start();
        editorTracker.addListener(markdownListener);
        editorTracker.editorShown(editorPart);

        // then
        verify(markdownListener, never()).notifyEditorFile(editorIFile);
        verifyNoMoreInteractions(markdownListener);
    }

    @Test
    public void shouldNotNotifyOnEditorPartOpenedNonTrackableFile() throws Exception {
        // given
        given(editorPart.getEditorInput()).willReturn(editorInput);
        given(editorInput.getAdapter(IFile.class)).willReturn(editorIFile);
        given(fileNature.isTrackableFile(editorIFile)).willReturn(false);

        // when
        editorTracker.start();
        editorTracker.addListener(markdownListener);
        editorTracker.editorShown(editorPart);

        // then
        verify(markdownListener, never()).notifyEditorFile(editorIFile);
        verifyNoMoreInteractions(markdownListener);
    }

    @Test
    public void shouldNotNotifyOnOnceStopped() throws Exception {
        // given
        given(editorPart.getEditorInput()).willReturn(editorInput);
        given(editorInput.getAdapter(IFile.class)).willReturn(editorIFile);
        given(fileNature.isTrackableFile(editorIFile)).willReturn(true);

        // when
        editorTracker.start();
        editorTracker.addListener(markdownListener);
        editorTracker.editorShown(editorPart);
        editorTracker.stop();
        editorTracker.editorShown(editorPart);

        // then
        verify(markdownListener, times(1)).notifyEditorFile(editorIFile);
        verifyNoMoreInteractions(markdownListener);
    }

    @Test
    public void shouldNotifyOnceOnlyOnEditorPartReOpened() throws Exception {
        // given
        given(editorPart.getEditorInput()).willReturn(editorInput);
        given(editorInput.getAdapter(IFile.class)).willReturn(editorIFile);
        given(fileNature.isTrackableFile(editorIFile)).willReturn(true);

        // when
        editorTracker.start();
        editorTracker.addListener(markdownListener);
        editorTracker.editorShown(editorPart);
        editorTracker.editorShown(editorPart);

        // then
        verify(markdownListener, times(1)).notifyEditorFile(editorIFile);
        verifyNoMoreInteractions(markdownListener);
    }

    @Test
    public void shouldNotifyOnceOnlyOnEditorPartClosed() throws Exception {
        // given
        given(editorPart.getEditorInput()).willReturn(editorInput);
        given(editorInput.getAdapter(IFile.class)).willReturn(editorIFile);
        given(fileNature.isTrackableFile(editorIFile)).willReturn(true);

        // when
        editorTracker.start();
        editorTracker.addListener(markdownListener);
        editorTracker.editorShown(editorPart);
        editorTracker.editorClosed(editorPart);

        // then
        verify(markdownListener, times(1)).notifyEditorFile(editorIFile);
        verifyNoMoreInteractions(markdownListener);
    }

    @Test
    public void shouldNotNotifyOnEditorPartClosedNoEditorFile() throws Exception {
        // given
        given(editorPart.getEditorInput()).willReturn(null);

        // when
        editorTracker.start();
        editorTracker.addListener(markdownListener);
        editorTracker.editorShown(editorPart);
        editorTracker.editorClosed(editorPart);

        // then
        verify(markdownListener, never()).notifyEditorFile(editorIFile);
        verifyNoMoreInteractions(markdownListener);
    }

    @Test
    public void shouldNotNotifyOnEditorPartClosedNeverOpened() throws Exception {
        // given
        given(editorPart.getEditorInput()).willReturn(editorInput);
        given(editorInput.getAdapter(IFile.class)).willReturn(editorIFile);
        given(fileNature.isTrackableFile(editorIFile)).willReturn(true);

        // when
        editorTracker.start();
        editorTracker.addListener(markdownListener);
        editorTracker.editorClosed(editorPart);

        // then
        verify(markdownListener, never()).notifyEditorFile(editorIFile);
        verifyNoMoreInteractions(markdownListener);
    }

    @Test
    public void shouldNotNotifyOnEditorPartNeverOpened() throws Exception {
        // given
        given(editorPart.getEditorInput()).willReturn(editorInput);
        given(editorInput.getAdapter(IFile.class)).willReturn(editorIFile);
        given(fileNature.isTrackableFile(editorIFile)).willReturn(true);

        // when
        editorTracker.start();
        editorTracker.editorShown(editorPart);

        // then
        verify(markdownListener, never()).notifyEditorFile(editorIFile);
        verifyNoMoreInteractions(markdownListener);
    }

    @Test
    public void shouldNotifyOnEditorPartSaved() throws Exception {
        // given
        given(editorPart.getEditorInput()).willReturn(editorInput);
        willDoNothing().given(editorPart).addPropertyListener(propertyListenerCaptor.capture());
        given(editorInput.getAdapter(IFile.class)).willReturn(editorIFile);
        given(fileNature.isTrackableFile(editorIFile)).willReturn(true);

        // when
        editorTracker.start();
        editorTracker.addListener(markdownListener);
        editorTracker.editorShown(editorPart);
        IPropertyListener propertyListener = propertyListenerCaptor.getValue();
        given(editorPart.isDirty()).willReturn(false);
        propertyListener.propertyChanged(editorPart, IEditorPart.PROP_DIRTY);

        // then
        verify(markdownListener, times(2)).notifyEditorFile(editorIFile);
        verifyNoMoreInteractions(markdownListener);
    }

    @Test
    public void shouldNotNotifyOnEditorPartInput() throws Exception {
        // given
        given(editorPart.getEditorInput()).willReturn(editorInput);
        willDoNothing().given(editorPart).addPropertyListener(propertyListenerCaptor.capture());
        given(editorInput.getAdapter(IFile.class)).willReturn(editorIFile);
        given(fileNature.isTrackableFile(editorIFile)).willReturn(true);

        // when
        editorTracker.start();
        editorTracker.addListener(markdownListener);
        editorTracker.editorShown(editorPart);
        IPropertyListener propertyListener = propertyListenerCaptor.getValue();
        given(editorPart.isDirty()).willReturn(false);
        propertyListener.propertyChanged(editorPart, IEditorPart.PROP_INPUT);

        // then
        verify(markdownListener, times(1)).notifyEditorFile(editorIFile);
        verifyNoMoreInteractions(markdownListener);
    }

    @Test
    public void shouldNotNotifyOnEditorPartialSave() throws Exception {
        // given
        given(editorPart.getEditorInput()).willReturn(editorInput);
        willDoNothing().given(editorPart).addPropertyListener(propertyListenerCaptor.capture());
        given(editorInput.getAdapter(IFile.class)).willReturn(editorIFile);
        given(fileNature.isTrackableFile(editorIFile)).willReturn(true);

        // when
        editorTracker.start();
        editorTracker.addListener(markdownListener);
        editorTracker.editorShown(editorPart);
        IPropertyListener propertyListener = propertyListenerCaptor.getValue();
        given(editorPart.isDirty()).willReturn(true);
        propertyListener.propertyChanged(editorPart, IEditorPart.PROP_DIRTY);

        // then
        verify(markdownListener, times(1)).notifyEditorFile(editorIFile);
        verifyNoMoreInteractions(markdownListener);
    }

}
