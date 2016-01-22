jenkins-scriptlets
==================

Useful groovy scripts that can be used while using Jenkins-CI for workflow automation. Many of them support the generation of dynamic build parameters for  [Active Choices](https://wiki.jenkins-ci.org/display/JENKINS/Active+Choices+Plugin) Jenkins form controls
#### AC_React_ArtifactCollector
Populates [Active Choices](https://wiki.jenkins-ci.org/display/JENKINS/Active+Choices+Plugin) parameters with a selected list of artifacts from the builds of a project.
  * You can filter the artifacts by extension
  * The Active Choice selected artifacts are returned as URLs to the artifacts

#### getBuildByNumber_scriptlet
Returns a human read-able list of builds of a Jenkins project. The list can act as a surrogate 'Run-Type' Jenkins parameter, but with the following advantages:
  * it can return builds within a user defined build number range
  * it has a customizable format
  * it can return builds with a certain build status (the default is SUCCESS)
  
The scriptlet can be used with [Active Choices](https://wiki.jenkins-ci.org/display/JENKINS/Active+Choices+Plugin) Jenkins parameters, to provide a list of formatted build names within the specified build range.

#### UC_helper_detectFileType
Identifies a file extension and returns a fileType label
Sometimes you want to autodetect a file type. Given a file extension, the scriptlet will auto-select one of predefined options sucha as IMAGE, BINARY, TEXT, HTML, CSV_TABLE, TSV_TABLE. By extending the extensions arrays additional file types can be detected.

#### UC_helper_getStaticPropertyKey
Retrieves a property key residing in a static properties file, usually in the userContent/properties folder
In use with  [Active Choices](https://wiki.jenkins-ci.org/display/JENKINS/Active+Choices+Plugin) Jenkins parameters it provides selection options generated from the property value. A single property value will generate a single option, while a comma separated value list will generated multiple values.
#### UC_helper_GetBuildsByNumAsMap
Returns a java map of build references (for one or more Jenkins jobs) within a specified build number range. Used in combination with [Active Choices](https://wiki.jenkins-ci.org/display/JENKINS/Active+Choices+Plugin) parameters it provides a human readable display value backed by a standardized reference build represented by the map key in the format `JOB_NAME#BUILD_NUMBER`.

Similarly to the `getBuildByNumber_scriptlet` (above)
  * it can return builds within a user defined build number range
  * it has a customizable format
  * it can return builds with a certain build status (the default is SUCCESS)

In addition
  * it can display user-friendly choice options while internally returning the map key as a build parameter. The map key is in a standardized build reference format (JOB#BUILD_NUMBER)

The JOB#BUILD_NUMBER format is used as the stanadard input parameter format to other scriptlets to generate additional build information

See [here](https://github.com/imoutsatsos/jenkins-scriptlets/wiki/Example_UC_helper_GetBuildsByNumAsMap) for details on a `UC_helper_GetBuildsByNumAsMap` usage example.

#### writeXMLProperties_scriptlet
  * Generates the XML file required for rendering a [Summary Display Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Summary+Display+Plugin) report.
  * The XML file generation is customized from a report configuration file
  * For an example report configuration and detailed usage instructions see my BioUno blog entry [Consistent Reports for Data Analysis](http://biouno.org/2014/07/07/Consistent-Reports-for-Data-Analysis)

