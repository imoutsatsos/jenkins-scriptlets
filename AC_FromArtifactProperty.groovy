/*** BEGIN META {
  "name" : "AC_FromArtifactProperty",
  "comment" : "Generates a choice list from a build/archived properties file. vBuildRef in JOBNAME#BUILD_NUMBER format",
  "parameters" : [ 'propFileName','propKey','vBuildRef'],
  "core": "2.100",
  "authors" : [
    { name : "Ioannis K. Moutsatsos" }
  ]
} END META**/

import hudson.model.*
jenkinsURL=jenkins.model.Jenkins.instance.getRootUrl()
def runParam=vBuildRef.split('#')
  def jobName=runParam[0]
  def buildNum=runParam[1]

def propAddress="${jenkinsURL}job/$jobName/$buildNum/artifact/$propFileName"
//def propKey= propKey //'metadata'

def props= new Properties()
props.load(new URL(propAddress).openStream())
def choices=[]

if (props.containsKey(propKey)){  
props.get(propKey).split(",").each{
      choices.add(it)
        }
}else{
 choices.add(vBuildRef) 
}

return choices