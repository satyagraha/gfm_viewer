
I configured Travis CI build, but it fails there, maybe is calls 2 mvn commands:

	mvn install -SkipTests=true -B
	mvn test -B	
	
The second (test) fails:	
	
	[INFO] Reactor Summary:
	[INFO]
	[INFO] code.satyagraha.gfm.viewer.parent ................. SUCCESS [0.002s]
	[INFO] code.satyagraha.gfm.viewer.ext-deps ............... SUCCESS [1.047s]
	[INFO] code.satyagraha.gfm.viewer.plugin ................. SUCCESS [0.739s]
	[INFO] code.satyagraha.gfm.viewer.plugin.tests ........... FAILURE [7.645s]
	[INFO] code.satyagraha.gfm.viewer.feature ................ SKIPPED
	[INFO] code.satyagraha.gfm.viewer.update-site ............ SKIPPED
	[INFO] code.satyagraha.gfm.viewer.p2-repo ................ SKIPPED
	[INFO] ------------------------------------------------------------------------
	[INFO] BUILD FAILURE
	
	[INFO] ------------------------------------------------------------------------
	[ERROR] Failed to execute goal org.eclipse.tycho:tycho-surefire-plugin:0.17.0:test (default) on project
	 code.satyagraha.gfm.viewer.plugin.tests: Execution default of goal org.eclipse.tycho:tycho-surefire-plugin:0.17.0:test failed:
	  Exception parsing OSGi MANIFEST /home/travis/build/Nodeclipse/gfm_viewer/plugin/target/classes: Could not find a 
	  META-INF/MANIFEST.MF, plugin.xml or a fragment.xml in /home/travis/build/Nodeclipse/gfm_viewer/plugin/target/classes. -> [Help 1]
	[ERROR] 	
	
Locally `mvn package` is OK	
	
	[INFO] Reactor Summary:
	[INFO]
	[INFO] code.satyagraha.gfm.viewer.parent ................. SUCCESS [0.502s]
	[INFO] code.satyagraha.gfm.viewer.ext-deps ............... SUCCESS [1:41.820s]
	[INFO] code.satyagraha.gfm.viewer.plugin ................. SUCCESS [14.246s]
	[INFO] code.satyagraha.gfm.viewer.plugin.tests ........... SUCCESS [50.402s]
	[INFO] code.satyagraha.gfm.viewer.feature ................ SUCCESS [0.500s]
	[INFO] code.satyagraha.gfm.viewer.update-site ............ SUCCESS [9.346s]
	[INFO] code.satyagraha.gfm.viewer.p2-repo ................ SUCCESS [42.454s]
	[INFO] ------------------------------------------------------------------------
	[INFO] BUILD SUCCESS