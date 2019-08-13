/*** BEGIN META {
  "name" : "AC_getJobProperty",
  "comment" : "Retrieves a property key (single, list or map) residing in a static properties file, usually in the job/buildProps folder",
  "parameters" : [ 'vPropFile','vPropKey','vPropFilePath'],
  "core": "2.121",
  "authors" : [
    { name : "Ioannis Moutsatsos" }
  ]
} END META**/

jenkinsPath=jenkins.model.Jenkins.instance.getRootPath()
def propFile=vPropFile 
def propKey=vPropKey
def propFilePath=vPropFilePath
def propAddress="${jenkinsPath}/${vPropFilePath}/$propFile"

def props= new Properties()

props.load(new File(propAddress).newDataInputStream())
keyVals=props.get(propKey).tokenize(",")
println keyVals
def choices=[:]
if (keyVals[0].contains(':')){
  keyVals.collect{choices.put(it.tokenize(':')[0],it.tokenize(':')[1])}
  return choices
      
}else{
 return keyVals 
}

