/*** BEGIN META {
  "name" : "AC_GetCollectionPropertyKey.groovy",
  "comment" : "Retrieves a property key residing in a static properties file, key value can be list or map",
  "parameters" : [ 'vPropFile','vPropKey','vRelPropFileUrl'],
  "core": "1.593",
  "authors" : [
    { name : "Ioannis K. Moutsatsos" }
  ]
} END META**/

jenkinsURL=jenkins.model.Jenkins.instance.getRootUrl()

def propFile=vPropFile //'dataType.properties'
def propKey=vPropKey// "file.content"
def relPropFileUrl=vRelPropFileUrl // userContent/properties/
def propAddress="${jenkinsURL}${relPropFileUrl}$propFile"
def props= new Properties()
props.load(new URL(propAddress).openStream())
def choices=[:]

//logic handles a map to be evaluated or a simple string
if (props.get(propKey.toString()).startsWith('[') && props.get(propKey.toString()).endsWith(']')) {
  choices=evaluate(props.get(propKey.toString()))
  
}else{
      props.get(propKey.toString()).split(",").each{
      choices.put(it,it)
        }
} //assume a list



return choices