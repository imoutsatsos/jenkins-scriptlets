jenkins-scriptlets
==================

Useful groovy scripts that can be used while using Jenkins-CI for workflow automation
#### writeXMLProperties_scriptlet.groovy
  * Generates the XML file required for rendering a [Summary Display Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Summary+Display+Plugin) report.
  * The XML file generation is customized from a report configuration file
  * For an example report configuration and detailed usage instructions see my BioUno blog entry [Consistent Reports for Data Analysis](http://biouno.org/2014/07/07/Consistent-Reports-for-Data-Analysis)

#### getBuildByNumber_scriptlet.groovy
This scriptlet returns a human read-able list of builds of a Jenkins project. The list can act as a surrogate 'Run-Type' Jenkins parameter, but with the following advantages:
  * it can return builds within a user defined build number range
  * it has a customizable format
  * it can return builds with a certain build status (the default is SUCCESS)
  
The scriptlet is used in combination with a dynamic Jenkins choice control such as [uno-choice](https://github.com/biouno/uno-choice-plugin), where the scriptlet provides the formatted build names as choice values dynamically.

#### UC_helper_GetBuildsByNumAsMap.groovy
This scriptlet returns a java map of build references (for one or more Jenkins jobs) within a specified build number range. Used in combination with an Uno-Choice Dynamic parameter it provides a human readable display value backed by a standardized reference build represented by the map key in the format `JOB_NAME#BUILD_NUMBER`.

Similarly to the `getBuildByNumber_scriptlet` (above)
  * it can return builds within a user defined build number range
  * it has a customizable format
  * it can return builds with a certain build status (the default is SUCCESS)

In addition
  * it can display user-friendly choice options while internally using a standardized build reference format (the map key)
