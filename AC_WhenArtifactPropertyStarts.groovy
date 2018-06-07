/*** BEGIN META {
  "name" : "AC_WhenArtifactPropertyStarts.groovy",
  "comment" : "Generates a choice list from archived properties that start with propKeyStarts. vBuildRef in JOB_NAME#BUILD_NUMBER format",
  "parameters" : [ 'propFileName','propKeyStarts','vBuildRef'],
  "core": "1.596",
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

def props= new Properties()
props.load(new URL(propAddress).openStream())
def choices=[]
props.findAll{it.key.startsWith(propKeyStarts)}.each{
      choices.add(it as String)
        }
return choices