package code.satyagraha.gfm.viewer.model;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import code.satyagraha.gfm.support.api.Transformer;
import code.satyagraha.gfm.ui.api.Scheduler;
import code.satyagraha.gfm.ui.api.Scheduler.Callback;
import code.satyagraha.gfm.viewer.model.api.MarkdownBrowser;
import code.satyagraha.gfm.viewer.model.api.MarkdownEditorTracker;
import code.satyagraha.gfm.viewer.model.api.MarkdownView;
import code.satyagraha.gfm.viewer.model.api.ViewerSupport;
import code.satyagraha.gfm.viewer.model.impl.ViewerModelDefault;
import code.satyagraha.test.support.SimpleLogging;

@RunWith(MockitoJUnitRunner.class)
public class ViewerModelTest {

    @Mock
    private Transformer transformer;

    @Mock
    private Scheduler scheduler;

    @Mock
    private ViewerSupport viewSupport;

    @Mock
    private MarkdownEditorTracker editorTracker;

    @Mock
    private MarkdownView markdownView;

    @Mock
    private MarkdownBrowser browser;
    
    @InjectMocks
    private ViewerModelDefault model;

    @BeforeClass
    public static void setupClass() {
        SimpleLogging.setupLogging();
    }

    @Test
    public void shouldStartTrackerOnStart() throws Exception {
        // given

        // when
        model.start(markdownView, browser);

        // then
        verify(editorTracker, times(1)).start();
        verify(editorTracker, times(1)).addListener(eq(model));
    }

    @Test
    public void shouldStopTrackerOnStop() throws Exception {
        // given

        // when
        model.start(markdownView, browser);
        model.stop();

        // then
        verify(editorTracker, times(1)).stop();
    }

    @Test
    public void showMarkdownFileShouldDoNothingIfNoPathWhenOnline() throws Exception {
        // given
        given(viewSupport.isOnline()).willReturn(true);

        IFile iFile = mock(IFile.class);
        given(iFile.getRawLocation()).willReturn(null);

        // when
        model.start(markdownView, browser);
        model.showMarkdownFile(iFile);
        model.stop();

        // then
        verifyZeroInteractions(transformer, scheduler, browser);
    }

    @Test
    public void showMarkdownFileShouldDoNothingIfNoPathWhenOffline() throws Exception {
        // given
        given(viewSupport.isOnline()).willReturn(false);

        IFile iFile = mock(IFile.class);
        given(iFile.getRawLocation()).willReturn(null);

        // when
        model.start(markdownView, browser);
        model.showMarkdownFile(iFile);
        model.stop();

        // then
        verifyZeroInteractions(transformer, scheduler, markdownView, browser);
    }

    @Test
    public void showMarkdownFileShouldDoNothingIfNoFileWhenOnline() throws Exception {
        // given
        given(viewSupport.isOnline()).willReturn(true);

        IPath iPath = mock(IPath.class);
        IFile iFile = mock(IFile.class);
        given(iFile.getRawLocation()).willReturn(iPath);
        given(iPath.toFile()).willReturn(null);

        // when
        model.start(markdownView, browser);
        model.showMarkdownFile(iFile);
        model.stop();

        // then
        verifyZeroInteractions(transformer, scheduler, markdownView, browser);
    }

    @Test
    public void showMarkdownFileShouldDoNothingIfNoFileWhenOffline() throws Exception {
        // given
        given(viewSupport.isOnline()).willReturn(false);

        IPath iPath = mock(IPath.class);
        IFile iFile = mock(IFile.class);
        given(iFile.getRawLocation()).willReturn(iPath);
        given(iPath.toFile()).willReturn(null);

        // when
        model.start(markdownView, browser);
        model.showMarkdownFile(iFile);
        model.stop();

        // then
        verifyZeroInteractions(transformer, scheduler, markdownView, browser);
    }

    @Test
    public void showMarkdownFileShouldNotGenerateIfUpToDateWhenOnline() throws Exception {
        showMarkdownFileScenario(true, true, true, 0, 1, 0, true);
    }

    @Test
    public void showMarkdownFileShouldGenerateIfNotUpToDateWhenOnline() throws Exception {
        showMarkdownFileScenario(true, true, false, 1, 1, 0, true);
    }

    @Test
    public void showMarkdownFileShouldGenerateIfNoHtmlFileWhenOnline() throws Exception {
        showMarkdownFileScenario(true, false, false, 1, 1, 0, true);
    }

    @Test
    public void showMarkdownFileShouldNotGenerateIfUpToDateWhenOffine() throws Exception {
        showMarkdownFileScenario(false, true, true, 0, 1, 0, true);
    }

    @Test
    public void showMarkdownFileShouldNotGenerateIfNotUpToDateWhenOffline() throws Exception {
        showMarkdownFileScenario(false, true, false, 0, 1, 0, false);
    }

    @Test
    public void showMarkdownFileShouldNotGenerateIfNoHtmlFileWhenOffline() throws Exception {
        showMarkdownFileScenario(false, false, false, 0, 0, 1, false);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void showMarkdownFileScenario(boolean isOnline, boolean htFileReadable, boolean canSkipTransformation, int scheduleCount, int showCount, int setTextCount, boolean isUpToDate)
            throws IOException {
        // given
        given(viewSupport.isOnline()).willReturn(isOnline);

        File mdFile = mock(File.class);
        IPath iPath = mock(IPath.class);
        IFile iFile = mock(IFile.class);
        given(iFile.getRawLocation()).willReturn(iPath);
        given(iPath.toFile()).willReturn(mdFile);
        given(mdFile.canRead()).willReturn(true);

        File htFile = mock(File.class);
        given(htFile.canRead()).willReturn(htFileReadable);

        given(transformer.createHtmlFile(mdFile)).willReturn(htFile);
        given(transformer.canSkipTransformation(mdFile, htFile)).willReturn(canSkipTransformation);

        ArgumentCaptor<Callback> schedulerCallbackCaptor = ArgumentCaptor.forClass(Callback.class);
        willDoNothing().given(scheduler).scheduleTransformation(eq(mdFile), eq(htFile), schedulerCallbackCaptor.capture());

        // when
        model.start(markdownView, browser);
        model.showMarkdownFile(iFile);
        if (scheduleCount > 0) {
            Callback callback = schedulerCallbackCaptor.getValue();
            callback.onComplete(htFile);
        }
        model.stop();

        // then
        verify(viewSupport, never()).isLinked();
        verify(scheduler, times(scheduleCount)).scheduleTransformation(eq(mdFile), eq(htFile), any(Scheduler.Callback.class));
        verify(markdownView, times(showCount)).nowShowing(mdFile, isUpToDate);
        verify(browser, times(showCount)).showHtmlFile(htFile);
        verify(browser, times(setTextCount)).setText(anyString());
    }

    @Test
    public void notifyEditorFileShouldDoNothingWhenUnLinked() throws Exception {
        // given
        given(viewSupport.isLinked()).willReturn(false);

        IFile iFile = mock(IFile.class);

        // when
        model.start(markdownView, browser);
        model.notifyEditorFile(iFile);
        model.stop();

        // then
        verify(viewSupport, never()).isOnline();
        verifyZeroInteractions(transformer, scheduler, browser);
    }

    @Test
    public void notifyEditorFileShouldDoNothingIfNoPathWhenLinked() throws Exception {
        // given
        given(viewSupport.isLinked()).willReturn(true);

        IFile iFile = mock(IFile.class);
        given(iFile.getRawLocation()).willReturn(null);

        // when
        model.start(markdownView, browser);
        model.notifyEditorFile(iFile);
        model.stop();

        // then
        verify(viewSupport, never()).isOnline();
        verifyZeroInteractions(transformer, scheduler, browser);
    }

    @Test
    public void notifyEditorFileShouldDoNothingIfNoFileWhenLinked() throws Exception {
        // given
        given(viewSupport.isLinked()).willReturn(true);

        IPath iPath = mock(IPath.class);
        IFile iFile = mock(IFile.class);
        given(iFile.getRawLocation()).willReturn(iPath);
        given(iPath.toFile()).willReturn(null);

        // when
        model.start(markdownView, browser);
        model.notifyEditorFile(iFile);
        model.stop();

        // then
        verify(viewSupport, never()).isOnline();
        verifyZeroInteractions(transformer, scheduler, browser);
    }

    @Test
    public void notifyEditorFileShouldNotGenerateIfUpToDateWhenLinkedAndOnline() throws Exception {
        notifyEditorFileScenario(true, true, true, true, 0, 1, true);
    }

    @Test
    public void notifyEditorFileShouldGenerateIfNotUpToDateWhenLinkedAndOnline() throws Exception {
        notifyEditorFileScenario(true, true, true, false, 1, 1, true);
    }

    @Test
    public void notifyEditorFileShouldGenerateIfNoHtmlFileWhenLinkedAndOnline() throws Exception {
        notifyEditorFileScenario(true, true, false, false, 1, 1, true);
    }

    @Test
    public void notifyEditorFileShouldNotGenerateIfUpToDateWhenLinkedAndOffine() throws Exception {
        notifyEditorFileScenario(true, false, true, true, 0, 1, true);
    }

    @Test
    public void notifyEditorFileShouldNotGenerateIfNotUpToDateWhenLinkedAndOffline() throws Exception {
        notifyEditorFileScenario(true, false, true, false, 0, 1, false);
    }

    @Test
    public void notifyEditorFileShouldNotGenerateIfNoHtmlFileWhenLinkedAndOffline() throws Exception {
        notifyEditorFileScenario(true, false, false, false, 0, 0, false);
    }

    @Test
    public void notifyEditorFileShouldNotGenerateIfUpToDateWhenUnlinkedAndOnline() throws Exception {
        notifyEditorFileScenario(false, true, true, true, 0, 0, false);
    }

    @Test
    public void notifyEditorFileShouldNotGenerateIfNotUpToDateWhenUnlinkedAndOnline() throws Exception {
        notifyEditorFileScenario(false, true, true, false, 0, 0, false);
    }

    @Test
    public void notifyEditorFileShouldNotGenerateIfNoHtmlFileWhenUnlinkedAndOnline() throws Exception {
        notifyEditorFileScenario(false, true, false, false, 0, 0, false);
    }

    @Test
    public void notifyEditorFileShouldNotGenerateIfUpToDateWhenUnlinkedAndOffine() throws Exception {
        notifyEditorFileScenario(false, false, true, true, 0, 0, false);
    }

    @Test
    public void notifyEditorFileShouldNotGenerateIfNotUpToDateWhenUnlinkedAndOffline() throws Exception {
        notifyEditorFileScenario(false, false, true, false, 0, 0, false);
    }

    @Test
    public void notifyEditorFileShouldNotGenerateIfNoHtmlFileWhenUnlinkedAndOffline() throws Exception {
        notifyEditorFileScenario(false, false, false, false, 0, 0, false);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void notifyEditorFileScenario(boolean isLinked, boolean isOnline, boolean htFileReadable, boolean canSkipTransformation, int scheduleCount,
            int showCount, boolean isUpToDate) throws IOException {
        // given
        given(viewSupport.isOnline()).willReturn(isOnline);
        given(viewSupport.isLinked()).willReturn(isLinked);

        File mdFile = mock(File.class);
        IPath iPath = mock(IPath.class);
        IFile iFile = mock(IFile.class);
        given(iFile.getRawLocation()).willReturn(iPath);
        given(iPath.toFile()).willReturn(mdFile);
        given(mdFile.canRead()).willReturn(true);

        File htFile = mock(File.class);
        given(htFile.canRead()).willReturn(htFileReadable);

        given(transformer.createHtmlFile(mdFile)).willReturn(htFile);
        given(transformer.canSkipTransformation(mdFile, htFile)).willReturn(canSkipTransformation);

        ArgumentCaptor<Callback> schedulerCallbackCaptor = ArgumentCaptor.forClass(Callback.class);
        willDoNothing().given(scheduler).scheduleTransformation(eq(mdFile), eq(htFile), schedulerCallbackCaptor.capture());

        // when
        model.start(markdownView, browser);
        model.notifyEditorFile(iFile);
        if (scheduleCount > 0) {
            Callback callback = schedulerCallbackCaptor.getValue();
            callback.onComplete(htFile);
        }
        model.stop();

        // then
        verify(scheduler, times(scheduleCount)).scheduleTransformation(eq(mdFile), eq(htFile), any(Scheduler.Callback.class));
        verify(browser, times(showCount)).showHtmlFile(htFile);
    }

    @Test
    public void shouldGoForward() {
        // given

        // when
        model.start(markdownView, browser);
        model.goForward();
        model.stop();

        // then
        verify(browser, times(1)).forward();
    }

    @Test
    public void shouldGoBackward() {
        // given

        // when
        model.start(markdownView, browser);
        model.goBackward();
        model.stop();

        // then
        verify(browser, times(1)).back();
    }

    @Test
    public void reloadShouldDoNothingWhenNoPreviousAction() throws Exception {
        // when
        model.start(markdownView, browser);
        model.reload();
        model.stop();
        
        // then
        verifyZeroInteractions(transformer, scheduler, browser);
    }

    @Test
    public void reloadShouldAlwaysRegenerateWhenOnline() throws Exception {
        reloadScenario(true, true, 1, 2, true);
    }
    
    @Test
    public void reloadShouldNotRegenerateWhenOffline() throws Exception {
        reloadScenario(false, true, 0, 2, false);
    }
    
    @Test
    public void reloadShouldUseExistingHTMLWhenOffline() throws Exception {
        // this is a bit dubious
        reloadScenario(false, false, 0, 1, false);
    }
    
    @Test
    public void reloadWhenOfflineWithNoExistingHTMLShouldNotify() throws Exception {
        // given
        given(viewSupport.isOnline()).willReturn(false);

        File mdFile = mock(File.class);
        IPath iPath = mock(IPath.class);
        IFile iFile = mock(IFile.class);
        given(iFile.getRawLocation()).willReturn(iPath);
        given(iPath.toFile()).willReturn(mdFile);
        given(mdFile.canRead()).willReturn(true);

        File htFile = mock(File.class);
        given(htFile.canRead()).willReturn(false);
        
        given(editorTracker.getActiveEditorMarkdownFile()).willReturn(iFile);
        given(transformer.createHtmlFile(mdFile)).willReturn(htFile);

        // when
        model.start(markdownView, browser);
        model.reload();
        model.stop();

        // then
        verify(viewSupport, never()).isLinked();
        verify(browser, times(1)).setText(anyString());
        verifyNoMoreInteractions(browser, scheduler);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void reloadScenario(boolean isOnline, boolean htFileReadable, int scheduleCount, int showCount, boolean isUpToDate) throws IOException {
        // given
        given(viewSupport.isOnline()).willReturn(isOnline);

        File mdFile = mock(File.class);
        IPath iPath = mock(IPath.class);
        IFile iFile = mock(IFile.class);
        given(iFile.getRawLocation()).willReturn(iPath);
        given(iPath.toFile()).willReturn(mdFile);
        given(mdFile.canRead()).willReturn(true);

        File htFile = mock(File.class);
        given(htFile.canRead()).willReturn(htFileReadable);
        
        given(transformer.createHtmlFile(mdFile)).willReturn(htFile);
        given(transformer.canSkipTransformation(mdFile, htFile)).willReturn(true);

        ArgumentCaptor<Callback> schedulerCallbackCaptor = ArgumentCaptor.forClass(Callback.class);
        willDoNothing().given(scheduler).scheduleTransformation(eq(mdFile), eq(htFile), schedulerCallbackCaptor.capture());

        // when
        model.start(markdownView, browser);
        model.showMarkdownFile(iFile);
        model.reload();
        if (scheduleCount > 0) {
            Callback callback = schedulerCallbackCaptor.getValue();
            callback.onComplete(htFile);
        }
        model.stop();

        // then
        verify(viewSupport, never()).isLinked();
        verify(scheduler, times(scheduleCount)).scheduleTransformation(eq(mdFile), eq(htFile), any(Scheduler.Callback.class));
        verify(browser, times(showCount)).showHtmlFile(htFile);
    }

}
