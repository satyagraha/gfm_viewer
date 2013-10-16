

# Adding Help to Eclipse plugin

by [Paul Verest](http://with-eclipse.github.io/)

( If you don't have an Eclipse plugin, you can quickly create one via [maven](http://maven.apache.org/) archetype
`mvn archetype:generate -DarchetypeCatalog=http://open-archetypes.github.io/maven-repo/snapshots/`  
in a list select `tycho-eclipse-plugin-archetype` ,
continue reading at <https://github.com/open-archetypes/tycho-eclipse-plugin-archetype> )

Open `plugin.xml` add

```xml
   <extension
         point="org.eclipse.help.toc">
      <toc
            file="HelpToc.xml"
            primary="true">
      </toc>
   </extension>
```

Add `HeplToc.xml` and help folder into `build.properties`, e.g.:

```txt
	source.. = source/
	output.. = target/classes/
	bin.includes = plugin.xml,\
	               META-INF/,\
	               .,\
	               icons/,\
	               HelpToc.xml,\
	               help/
```

Next add file `HelpToc.xml` with content similar to this:

```xml
	<?xml version="1.0" encoding="UTF-8"?>
	<?NLS TYPE="org.eclipse.help.toc"?>
	
	<toc label="GitHub Flavored Markdown Viewer Help" topic="help/.index.md.html">
		<topic label="Markdown"  href="help/.markdown.md.html"/>
		<topic label="Github Flavored Markdown (GFM)"  href="help/.github-flavored-markdown.md.html"/>
		<topic label="Usage"  href="help/.usage.md.html"/>
		<topic label="Configuration"  href="help/.configuration.md.html"/>
	</toc>
```

That is per every Help page that you author with Markdown, you add one line.

There should also be `index.md` with content like:

```txt
	##Table of Contents
	
	- [Markdown](.markdown.md.html)
	- [Github Flavored Markdown (GFM)](.github-flavored-markdown.md.html)
	- [Usage](.usage.md.html)
	- [Configuration](.configuration.md.html)
```

Now right-click `help` folder and select `Generate Markdown Preview`. You get all `.*.md.html` files needed.

