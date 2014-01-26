package code.satyagraha.gfm.ui;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import code.satyagraha.gfm.ui.api.EditorPartListener;
import code.satyagraha.gfm.ui.impl.PageEditorTrackerDefault;

@RunWith(MockitoJUnitRunner.class)
public class PageEditorTrackerTest {

    @Mock
    private IWorkbenchPage workbenchPage;

    @InjectMocks
    private PageEditorTrackerDefault pageEditorTracker;

    @Mock
    private EditorPartListener listener;

    @Mock
    private IEditorReference editorRef;

    @Mock
    private IEditorPart editorPart;

    @Test
    public void shouldSubscribe() throws Exception {
        // given
        
        // when
        pageEditorTracker.subscribe(listener);
        
        // then
        verify(workbenchPage, times(1)).addPartListener(pageEditorTracker);
    }

    @Test
    public void shouldUnsubscribe() throws Exception {
        // given
        
        // when
        pageEditorTracker.subscribe(listener);
        pageEditorTracker.unsubscribe(listener);
        
        // then
        verify(workbenchPage, times(1)).removePartListener(pageEditorTracker);
    }

    @Test
    public void shouldNotifyOnEditorOpened() throws Exception {
        // given
        given(editorRef.getPage()).willReturn(workbenchPage);
        given(editorRef.getEditor(true)).willReturn(editorPart);
        
        // when
        pageEditorTracker.subscribe(listener);
        pageEditorTracker.partOpened(editorRef);
        
        // then
        verify(listener).editorShown(editorPart);
    }

    @Test
    public void shouldGetActiveEditor() throws Exception {
        // given
        given(workbenchPage.getActiveEditor()).willReturn(editorPart);
        
        // when
        IEditorPart activeEditor = pageEditorTracker.getActiveEditor();
        
        // then
        assertThat(activeEditor, sameInstance(editorPart));
    }

}
