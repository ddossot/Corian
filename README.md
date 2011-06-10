<h1><span class="no-sidebar" style="color: #000080; font-family: 'courier new', courier; font-size: x-large;">Corian </span></h1>
<h2>What is this? </h2>
<p>Corian, short for Commit Risk Analyzer, is a basic utility that extracts the test coverage values from Covertura reports for classes that were recently modified in Subversion.</p>
<h2>Usage</h2>
<pre> mvn exec:java -Dexec.args="..."</pre>
<p>Arguments (all mandatory):</p>
<p><span style="font-family: 'courier new', courier;">&nbsp;-daysBefore </span><span><span style="font-family: 'courier new', courier;">&lt;arg&gt;</span></span><span style="font-family: 'courier new', courier;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Days in the past for which risk is analyzed<br /> -projectPath &lt;arg&gt;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; SVN relative project path<br /> -projectSiteUrl &lt;arg&gt;&nbsp;&nbsp; Maven project site root URL<br /> -svnPassword &lt;arg&gt;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; SVN password<br /> -svnUrl &lt;arg&gt;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; SVN base URL<br /> -svnUser &lt;arg&gt;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; SVN user name</span></p>
<h2>Example</h2>
<pre>mvn exec:java -Dexec.args="-daysBefore 14 -svnUrl http://svn.foo.com -svnUser usr -svnPassword pwd -projectPath /prj/trunk -projectSiteUrl http://foo.com/projects/groupId/1.24-SNAPSHOT/prj"</pre>
<p>Result:</p>
<pre>Revision on Fri Aug 25 12:49:01 PDT 2008: 81721<br />Latest revision: 82344<br /><br />89% /prj/trunk/prj-module/src/main/java/com/foo/prj/module/ClassA.java<br />96% /prj/trunk/prj-module/src/main/java/com/foo/prj/module/ClassB.java<br />100% /prj/trunk/prj-module/src/main/java/com/foo/prj/module/ClassC.java</pre>
