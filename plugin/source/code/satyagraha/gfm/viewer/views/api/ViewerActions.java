package code.satyagraha.gfm.viewer.views.api;

public interface ViewerActions {

    public void goForward();

    public void goBackward();

    public void setLinkedState(boolean state);

    public void reload();

}