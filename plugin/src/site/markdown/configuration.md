

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

Regarding the temporary formatted HTML files, when stored in the original markdown file directory note:

* You may wish to add the exclusion pattern `.*.md.html` to your projects `.gitignore` file, which will prevent
their inclusion in version control operations
* You can manage the visibility of these files in Eclipse tree views via the _View_ menu (small triangle icon in
top right of view), then select the _Filters..._ entry, and then set or clear the _.* resources_ check box
