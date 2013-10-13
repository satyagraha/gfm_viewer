

## Usage

After installation (see below), the viewer may be accessed in any of the following ways:

* Execute menu _Window_ -> _Show View_ -> _Other..._ and select entry
_GFM Support_ -> _GFM Viewer_. Then, when files with an extension of `.md` are saved
from an Eclipse editor, this view is updated as would be seen when browsing the file
via the GitHub website.

* When a markdown file is selected in any tree navigation view, then a _Show in GFM view_
[context menu](http://en.wikipedia.org/wiki/Context_menu) entry is provided which when activated
will automatically open the view and display the formatted markdown content.

* When one or more directories and/or markdown files are selected in any tree navigation view,
then a _Generate Markdown Preview_ context menu entry is presented which when activated will
regenerate all associated markdown preview files (via background jobs recursively traversing 
the directory trees): this is effectively a batch mode update facility.     

* The GFM plugin view provides on its toolbar the following icons which may be clicked:
	* ![nav_backward.gif](nav_backward.gif) - navigate backwards in the browsing history
	* ![nav_forward.gif](nav_forward.gif) - navigate forwards in the browsing history
	* ![linked.gif](linked.gif) - link GFM View to editor to automatically update on save
	* ![realod.gif](reload.gif) - manually update GFM View from last linked file

The GFM View-editor linked state is by default on, but if set off its state will be automatically
saved and preserved in the Eclipse workspace provided the GFM view is not closed.

If non-ASCII characters are used in the markdown file, the current implementation assumes
[UTF-8](http://en.wikipedia.org/wiki/UTF-8) character encoding, see also
[here](http://www.martinahrer.at/2007/06/03/eclipse-encoding-settings/).

The plugin uses Eclipse default browser, e.g. Internet Explorer on Windows; if you use
Linux, you will need to set up Mozilla Firefox as follows:
* http://www.eclipse.org/swt/faq.php#browserlinuxrcp
* http://www.eclipse.org/swt/faq.php#browserlinux
