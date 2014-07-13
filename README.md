# GitHub Flavored Markdown Viewer plugin for Eclipse

![](plugin/src/site/markdown/gfm-viewer-logo.png)
<a href="http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=900708" 
title="Drag and drop into a running Eclipse to install GitHub Flavored Markdown viewer plugin">
  <img src="http://marketplace.eclipse.org/sites/all/modules/custom/marketplace/images/installbutton.png"/>
</a>
 satyagraha: [![Build Status](https://secure.travis-ci.org/satyagraha/gfm_viewer.png)](http://travis-ci.org/satyagraha/gfm_viewer)
 Nodeclipse: [![Build Status](https://secure.travis-ci.org/Nodeclipse/gfm_viewer.png)](http://travis-ci.org/Nodeclipse/gfm_viewer)


This project provides an Eclipse view which provides a reasonably accurate presentation of
[GithHub Flavored Markdown](http://github.github.com/github-flavored-markdown/) files.

It is also new way to create Eclipse plugin help contents.
Check project sources and developer instructions at GitHub
<https://github.com/satyagraha/gfm_viewer> .

## Usage

**Note: Usage & Configuration were copied into built-in Help. When changing please delete here
 and add links into `plugin/src/site/markdown`**

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
	* ![settings16_yellow.png](plugin/icons/settings16_yellow.png) - show preferences page
	* ![linked.gif](plugin/icons/online.gif) - on/off-line mode, switch off when not connected to internet
	* ![linked.gif](plugin/icons/linked.gif) - linked mode, use to keep GFM View aligned with active editor
	* ![realod.gif](plugin/icons/reload.gif) - reload, to manually update GFM View from last linked file
	* ![nav_backward.gif](plugin/icons/nav_backward.gif) - navigate backwards in the browsing history
	* ![nav_forward.gif](plugin/icons/nav_forward.gif) - navigate forwards in the browsing history

The On/Off-line and Editor linked modes are by default on, but if set off their state
will be automatically saved and preserved in the Eclipse workspace provided the GFM view is not closed.

When operating in Off-line mode, if the local cached HTML version of the markup file is
out-of-date with respect to the markdown file, then the title bar of the browser has
a '*' character preceding the file name, similar to the "dirty" indicator in editors.

The plugin uses Eclipse's default browser, e.g. Internet Explorer on Windows; if you use
Ubuntu or other Linux distros, we recommend the WebKit browser; see:
* http://www.eclipse.org/swt/faq.php#browserlinuxrcp
* http://www.eclipse.org/swt/faq.php#browserlinux

If you want to view two (or more) markdown files simultaneously, simply open a new Eclipse window via menu
_Window_ -> _New Window_ and there open another instance of the viewer: then any markdown files edited
in that window will display in that window's viewer, independently of the original window and its 
editors and viewer.

If non-ASCII characters are used in the markdown file, the current implementation assumes
[UTF-8](http://en.wikipedia.org/wiki/UTF-8) character encoding, see also
[here](http://www.martinahrer.at/2007/06/03/eclipse-encoding-settings/).

## Configuration

The GFM viewer plugin should work adequately without further configuration, however its operation may be
customized via menu _Window_ -> _Preferences_ -> _GFM Viewer_. On that dialog page, properties may be set
as follows:

* _Use Temp dir_: by default formatted HTML files are stored in the original markdown file's directory,
thus enabling relative local links to images to work properly;
select this option to instead have them in the user's temporary directory
* _API URL_: specifies an alternate URL for GitHub API calls (typically for corporate users only)
* _Username/Password_: a limit of 60 updates per hour is imposed by GitHub for unauthenticated
usage of their markdown rendering API, so provide GitHub credentials in these fields to have this
limit raised, see [here](http://developer.github.com/v3/#rate-limiting) for background
* _Template File_: an alternate HTML template file to the default may be provided
* _CSS URL 1-3_: an alternate CSS presentation to the default may be provided
* _JS URL 1-3_: an alternate JS implementation to the default may be provided
* _Use Eclipse Console_: shows debugging output in Console view
* _Markdown Extensions_: recognized markdown extensions

Regarding the temporary formatted HTML files, when stored in the original markdown file directory note:

* You may wish to add the exclusion pattern `.*.md.html` to your projects `.gitignore` file, which will prevent
their inclusion in version control operations
* You can manage the visibility of these files in Eclipse tree views via the _View_ menu (small triangle icon in
top right of view), then select the _Filters..._ entry, and then set or clear the _.* resources_ check box

## Installation from Update Site

In Eclipse, do the following:
* Go to menu _Help_ -> _Install New Software..._ and in the resulting dialog click the _Add..._
button to present a further dialog, and here enter `GFM Viewer` as the _Name_ and 
this [link](https://raw.github.com/satyagraha/gfm_viewer/master/p2-composite/) as the _Location_ and press _OK_
* Select the _GFM Viewer_ category in the install view, and alter the checkbox settings
there as necessary 
* Proceed to install the software in the usual manner accepting all defaults
* Eclipse will prompt for a restart, accept this, then the GFM viewer is usable as documented
[above](#usage)

## Installation of Alternate Versions

The above installation mechanism will install the latest official version of the plugin. It is possible to install
older or newer beta versions of the software by using http://dl.bintray.com/satyagraha/generic/x.y.z as the
update site URL, where x.y.z is the desired version. You can see the available versions at http://dl.bintray.com/satyagraha/generic/.

## Installation from Source Code

The following build and install process can be followed to build the plugin locally should there be an issue with the
update site:

* Ensure you have [Maven](http://maven.apache.org/) executables installed for your OS
and enviroment variable `JAVA_HOME` points to an installed JDK 6+
* Clone this project's repository to a convenient location (a path not containing special characters
like space is advised)
* In a shell or command window, change working directory to project root ( `gfm_viewer` with this README.md), 
* Build the plugin by executing the command: `mvn clean package`
* If building under Linux, take a look at the `.travis.yml` file in the base directory to see any
additional browser-related requirements
* Start your preferred Eclipse version in the normal way
* In Eclipse, execute menu _Help_ -> _Install New Software..._ and in the resulting dialog click the _Add..._
button to present a further dialog, and here enter `GFM Viewer (local)` as the _Name_ and click the _Local..._
button, then navigate to the directory `gfm_viewer/update-site/target/site` and press _OK_
* Select the _GFM Viewer_ category in the install view, and proceed to install the software
in the usual manner accepting all defaults
* Eclipse will prompt for a restart, accept this, then the GFM viewer is usable as documented
[above](#usage)

## Uninstall

* If desired, the GFM viewer plugin may be uninstalled via by selecting menu _Help_ -> _About Eclipse Platform_
* In the resulting dialog click button _Installation Details_, then select entry _GFM Viewer_,
then click button _Uninstall..._ and proceed accepting all defaults
* Provided the plugin was the last one installed, alternatively you can select the _Installation History_ tab
in the dialog, pick the previous history entry, and then click the _Revert_ button  

## Implementation

The GFM Viewer Eclipse view part implementation is quite straight-forward, and operates as follows:

* On construction, the view instantiates a web browser SWT component (which will be the default
for the host OS, e.g. IE for Windows)
* An event listener is registered which detects the opening and saving of markdown files
in a relevant editor
* When such an event is detected, then the editor's text contents are captured and sent
to the GitHub rendering API resulting an an HTML segment 
* The resulting HTML segment is embedded in an HTML template to produce a 
standards conformant HTML document
* The resulting HTML document is then sent to the browser component for display in the view
* Updates to the viewed HTML document aim to preserve the current scroll position (via Javascript)

## Development

If you want to do development on the plugin, proceed as follows:

* get fresh new Eclipse Standard from <http://www.eclipse.org/downloads/>, or

* You will need an Eclipse instance which does *not* have the GFM viewer installed, but does have the
following PDE-related plugins installed (check via _Help_ -> _About Eclipse Platform_ -> _Installation Details_):
 * Eclipse Plug-in Development Environment
 * Eclipse RCP Plug-in Development Resources
 * Eclipse RCP SDK
* If any of these are absent, you must install them from the relevant main Eclipse update site, e.g. for
[Kepler](http://download.eclipse.org/releases/kepler)
* Ensure the _Group items by category_ checkbox is unticked in the installer _Available Software_ dialog,
otherwise these plugins may be hard to find

Then you can proceed as follows:
* In the PDE Eclipse started with a new workspace, it is advisable to use the _Git Repositories_ view
to clone the GitHub repo by pasting the GitHub URL as this will avoid line termination issues later

* Then switch to the _Plug-in Development_ perspective, then activate the context menu in the _Package Explorer_ panel
and select entry _Import_ -> _Maven_ -> _Existing Maven Projects_ and navigate to the top-level project clone folder
* Click through and accept all the defaults to complete the import process
* If necessary (e.g. before EGit 3.0), via the context menu on the new projects, select _Team_ -> _Share Project..._ -> _Git_
and click through accepting the defaults to connect the projects to version control
* Create an _Eclipse Maven_ run configuration for the _GFM Viewer parent_ project with
goals `clean package` and workspace refresh, and then run it 
* Verify the last step created a jar file around 4Mb in size in the `lib` directory of project _GFM Viewer plugin_,
although there will still be build errors at this point
* Expand the _GFM Viewer plugin_ child project and open its `plugin.xml` file, select the _Overview_ tab and click the
_Update the classpath settings_ link; the projects should now build successfully
* Create an _Eclipse Application_ run configuration for the _GFM Viewer plugin_ project, and then run it
* If the child Eclipse instance has memory problems, consider adding `-XX:MaxPermSize=128m` to the launch
configuration JVM arguments
* In the child instance, follow the [usage](#usage) instructions to show the GFM view 
* Additional debug information is available by editing the plugin run configuration, and
on the dialog select the _Tracing_ tab and then enable the entry for `code.satyagraha.gfm.viewer.plugin`

You can build all GFM project components with Tycho in Eclipse by creating a Maven build configuration in project
_GFM Viewer parent_ with goals `clean package` and workspace refresh, ensuring that on the _JRE_ tab
an _Alternate JRE_ is specified which refers to a full JDK. However, as the directory structure for
Maven builds differs from that in Eclipse (by the using a `target` subdirectory) this may be more
confusing than useful. 
 
You can create an update site in the traditional way as follows:
* In the project _GFM Viewer update site_, open the file `site.xml`
* On the presented editor's _Site Map_ tab, click the _Build All_ button, and the plugin,
feature, and update site will be built
* Verify that, in the update site project, directories `features` and `plugins` have been created,
containing jar files

An additional project _GFM Viewer p2-repo_ is provided which is an experimental use
of the Tycho P2 Repository generation facility, prompted by the
[deprecation of the update site mechanism](http://wiki.eclipse.org/Tycho/Reference_Card#Update_Site). 
After a Maven Tycho build you may select the directory `gfm_viewer/p2-repo/target/repository` as a local update
site as an alternative to the other one.

In order to update version:

	mvn -Dtycho.mode=maven org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=1.X.0
	
Make sure to do a search through all files to find any occurrences of the old version, as they will be present
in some Eclipse plugin manifest and build properties files.

The repository has Travis CI configured (see buttons about), so every commit will be built and the result seen.	

## To Do

Areas possibly meriting further attention include:

* It appears that GitHub may use some kind of dynamic CSS generation: the consequence is that the coverage
provided by this plugin's simple static CSS may well not include some important entries (simply because they
were not apparent on the test content used in development)

## Markdown Editors

The [Winterstein Eclipse Markdown Editor](https://github.com/winterstein/Eclipse-Markdown-Editor-Plugin)
plugin is recommended as an excellent general markdown editor which complements this viewer plugin well.

## License

Eclipse Public License 1.0

## Acknowledgements

* Thanks to [Paul Verest](https://github.com/PaulVI) for sustained input into this project
* Thanks to [Olivier Laviale](https://github.com/olvlvl) for CSS contributions
* The [Picocontainer](http://picocontainer.com/) dependency injection framework works well inside
the Eclipse 3.x environment, and makes object management far easier than otherwise
* The [EventBus](http://eventbus.org/) library provides a neat loosely coupled publish/subscribe
framework
* The [ObjectAid](http://www.objectaid.com/) Eclipse plugin allows one easily to create UML class 
diagrams as an aid to understanding the relationships between the entities
* The [ispace](http://www.stribor.de/) Eclipse plugin creates a dependency graph of packages and
classes in a project, highlighting circular dependencies which typically indicate poor design
* For Java unit testing, [Mockito](https://code.google.com/p/mockito/) is superb
* For Eclipse UI testing, [SWTBot](http://eclipse.org/swtbot/) works pretty well

## History

* 1.9.0 - Web proxy fixes
* 1.8.5 - Web proxy support beta
* 1.8.4 - Support Eclipse proxy settings
* 1.8.3 - Fix broken release
* 1.8.2 - Minor fixes and feature improvements
* 1.8.1 - UI tests, fixes
* 1.8.0 - On/Off-line mode
* 1.7.0 - Various fixes, Ubuntu supported via Webkit browser
* 1.6.0 - Skip HTML regeneration by default, fixes
* 1.5.0 - Help page how to add Help to an Eclipse plugin, console logging
* 1.4.0 - hierarchical project structure, dependency injection, built-in Help, fix NPE when changing perspective
* 1.3.0 - filtered links, batch mode, and bintray update site
* 1.2.1 - fix update site link
* 1.2.0 - link with editor/reload option, tests module
* 1.1.1 - added update site
* 1.1.0 - transformation done in background job, provide context menu, temp dir option
* 1.0.1 - use UTF-8 text encoding, e.g. 快乐  ハッピー  ευτυχισμένος
* 1.0.0 - initial version

<a href="http://with-eclipse.github.io/" target="_blank"><img alt="with-Eclipse logo" src="http://with-eclipse.github.io/with-eclipse-1.jpg" /></a>
