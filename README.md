# GitHub Flavored Markdown viewer plugin for Eclipse

This project provides an Eclipse view which provides a reasonably accurate presentation of
[GithHub Flavored Markdown](http://github.github.com/github-flavored-markdown/) files.

<a href="https://help.github.com/articles/github-flavored-markdown">GitHub Flavored</a>
 [Markdown](http://daringfireball.net/projects/markdown/) viewer plugin (GFM Viewer) adds View that displays .md files as they would be on GitHub.

Created by <a href="https://github.com/satyagraha">Satyagraha</a>. Existing listing is <a href="/content/github-flavored-markdown-viewer-plugin-eclipse" title="GitHub Flavored Markdown viewer plugin for Eclipse">GitHub Flavored Markdown viewer plugin for Eclipse </a>. Wtih this entry you can download GFM into your Eclipse without manual build as described in author's README.

Goes well together with <a href="/content/markdown-text-editor" title="Markdown Text Editor">Markdown Text Editor 0.9.0</a> by <a href="/user/4860/listings" title="Daniel Winterstein">Daniel Winterstein</a>, which also can be installed via <a href="/content/nodeclipse" title="Nodeclipse">Nodeclipse</a> update site.


## Usage

After [installation](#installation), execute menu _Window_ -> _Show View_ -> _Other..._ and
select entry _GFM Support_ -> _GFM Viewer_. Then, when files with an extension of `.md` are saved
from an Eclipse editor, this view is updated as would be seen when browsing the file
via the GitHub website.

You may click internal and external hyperlinks in the presented web page, and then click the
left and right arrow navigation icons in the top right of the view to navigate backwards and
forward in the browsing history.   
 
## Installation

Update site: `www.nodeclipse.org/updates/staging/`

Alternatively the following build and install process can be followed:

* Ensure you have [Maven](http://maven.apache.org/) executables installed for your OS
* Clone this project's repository to a convenient location (a path not containing special characters
like space is advised)
* In a shell or command window, change working directory to `gfm_viewer/parent`, and set enviroment variable
`JAVA_HOME` to point to an installed JDK 6+
* Build the plugin by executing the command: `mvn clean package`
* Start your preferred Eclipse version in the normal way
* In Eclipse, execute menu _Help_ -> _Install New Software..._ and in the resulting dialog click the _Add..._
button to present a further dialog, and here enter `GFM Viewer` as the _Name_ and click the _Local..._
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

## Configuration

The GFM viewer plugin should work adequately without further configuration, however its operation may be
customised via menu _Window_ -> _Preferences_ -> _GFM Viewer_. On that dialog page, properties may be set
as follows:

* _API URL_: specifies an alternate URL for GitHub API calls (typically for corporate users only)
* _Username/Password_: a limit of 60 updates per hour is imposed by GitHub for unauthenticated
usage of their markdown rendering API, so provide GitHub credentials in these fields to have this
limit raised, see [here](http://developer.github.com/v3/#rate-limiting) for background
* _Template File_: an alternate HTML template file to the default may be provided
* _CSS URL 1-3_: an alternate CSS presentation to the default may be provided
* _JS URL 1-3_: an alternate JS implementation to the default may be provided

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

* You will need an Eclipse instance which does *not* have the GFM viewer installed, but does have the
following PDE-related plugins installed (check via _Help_ -> _About Eclipse Platform_ -> _Installation Details_):
 * Eclipse Plug-in Development Environment
 * Eclipse RCP Plug-in Development Resources
 * Eclipse RCP SDK
* If any of these are absent, you must install them from the relevant main Eclipse update site, e.g. for
[Juno](http://download.eclipse.org/releases/juno)
* Ensure the _Group items by category_ checkbox is unticked in the installer _Available Software_ dialog,
otherwise these plugins may be hard to find

Then you can proceed as follows:
* In the PDE Eclipse started with a new workspace, it is advisable to use the _Git Repositories_ view
to clone the GitHub repo by pasting the GitHub URL as this will avoid line termination issues later
* Then  perform a simple (not Maven) import of all six directories
below `gfm_viewer` as existing Eclipse projects, namely `ext-deps`, `feature`, `p2-repo`, `parent`, `plugin`,
and `update-site` 
* If necessary, via the context menu on the new projects, select _Team_ -> _Share Project..._ -> _Git_
and click through accepting the defaults to connect the projects to version control
* Via the context menu on projects `ext-deps` and `parent` _only_, select _Configure_ -> _Convert to Maven Project_
* Create an _Eclipse Maven_ run configuration for the _GFM Viewer ext-deps_ project with
goals `clean package` and workspace refresh, and then run it 
* Verify the last step created a jar file around 4Mb in size in the `lib` directory of project _GFM Viewer plugin_
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

## To Do

Areas meriting further attention include:

* It appears that GitHub may use some kind of dynamic CSS generation: the consequence is that the coverage
provided by this plugin's simple static CSS may well not include some important entries (simply because they
were not apparent on the test content used in development)

## Markdown Editors

The [Winterstein Eclipse Markdown Editor](https://github.com/winterstein/Eclipse-Markdown-Editor-Plugin)
plugin is recommended as an excellent general markdown editor which complements this viewer plugin well.

## License

Eclipse Public License 1.0
