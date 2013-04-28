# GitHub Flavored Markdown viewer plugin for Eclipse

This project provides an Eclipse view which provides a reasonably accurate presentation of
[GithHub Flavored Markdown](http://github.github.com/github-flavored-markdown/) files.

## Usage

After [installation](#installation), execute menu _Window_ -> _Show View_ -> _Other..._ and
select entry _GFM Support_ -> _GFM Viewer_. Then, when files with an extension of `.md` are saved
from an Eclipse editor, this view is updated as would be seen when browsing the file
via the GitHub website.   
 
## Installation

No Eclipse update site is currently available, so the following build and install process may be
followed:

* Ensure you have [Maven](http://maven.apache.org/) executables installed for your OS
* Clone this project's repository to a convenient location (a path not containing special characters
like space is advised)
* In a shell or command window, change working to `gfm_viewer/ext-deps`, and set enviroment variable
`JAVA_HOME` to point to an installed JDK 6+
* Execute the command: `mvn clean package`
* Verify the last step created a jar file around 4Mb in size in directory `gfm_viewer/plugin/lib`
* Now, in Eclipse started with a new workspace, import all four directories below `gfm_viewer` as
existing Eclipse projects, namely `ext-deps`, `feature`, `plugin`, and `update-site` 
* In the newly visible project `GFM Viewer update site`, open the file `site.xml`
* On the presented editor's `Site Map` tab, click the `Build All` button, and the plugin,
feature, and update site will be built
* Verify that, in the update site project, directories `features` and `plugins` have been created,
containing jar files
* Exit this Eclipse instance
* Start a new Eclipse instance with a different workspace
* Execute menu _Help_ -> _Install New Software..._ and in the resulting dialog click the _Add..._
button to present a further dialog, and here enter `GFM Viewer` as the _Name_ and click the _Local..._
button, then navigate to the directory `gfm_viewer/update_site` and press _OK_
* Select the _GFM Viewer_ category in the install view, and proceed to install the software
in the usual manner accepting all defaults
* Eclipse will prompt for a restart, accept this, then the GFM viewer is usable as documented
[above](#usage)

## Uninstall

* If desired, the GFM viewer plugin may be uninstalled via menu _Help_ -> _About Eclipse Platform_
* In the resulting dialog click button _Installation Details_, then select entry _GFM Viewer_,
then click button _Uninstall..._ and proceed accepting all defaults 

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

* On construction, the view instantiates a web browser SWT component (which will the default
for the machine/OS)
* An event listener is registered which detects the opening and saving of markdown files
in a relevant editor
* When such an event is detected, then the editor's text contents are captured and sent
to the GitHub rendering API resulting an an HTML segment 
* The resulting HTML segment is embedded in an HTML template to produce a 
standards conformant HTML document
* The resulting HTML document is then sent to the browser component for display in the view

## Development

The GFM Viewer plugin functionality may be exercised prior to installation as follows:

* Activate the context menu on the `GFM Viewer plugin` project, then select entry
_Run As_ -> _Eclipse Application_ which will start a transient Eclipse instance with the 
GFM viewer plugin available as per [usage](#usage)
* Additional debug information is available by editing the newly created run configuration, and
on the resulting dialog select the _Tracing_ tab and enable the entry for `code.satyagraha.gfm.viewer`

## Markdown Editors

The [Winterstein Eclipse Markdown Editor](https://github.com/winterstein/Eclipse-Markdown-Editor-Plugin)
plugin is recommended as an excellent general markdown editor which complements this viewer plugin well.

## License

Eclipse Public License 1.0
