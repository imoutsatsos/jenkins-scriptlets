/*** BEGIN META {
  "name" : "UC_helper_getStaticPropertyKey.groovy",
  "comment" : "Retrieves a property key residing in a static properties file, usually in the userContent/properties folder",
  "parameters" : [ 'vPropFile','vPropKey','vRelPropFileUrl'],
  "core": "1.593",
  "authors" : [
    { name : "Ioannis Moutsatsos" }
  ]
} END META**/

jenkinsURL=jenkins.model.Jenkins.instance.getRootUrl()

def propFile=vPropFile //'dataType.properties'
def propKey=vPropKey// "file.content"
def relPropFileUrl=vRelPropFileUrl // userContent/properties/
def propAddress="${jenkinsURL}${relPropFileUrl}$propFile"
def props= new Properties()
props.load(new URL(propAddress).openStream())
def choices=[]

    props.get(propKey.toString()).split(",").each{
      choices.add(it)
        }

return choices
