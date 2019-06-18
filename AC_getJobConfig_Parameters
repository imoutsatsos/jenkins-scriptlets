/*** BEGIN META {
  "name" : "AC_getJobConfig_Parameters",
  "comment" : "Returns a list of the parameters of a FreeStyleJob",
  "parameters" : [ 'JOB_NAME'],
  "core": "2.121",
  "authors" : [
    { name : "Ioannis K. Moutsatsos" }
  ]
} END META**/

jobName=JOB_NAME
options=[]
job=jenkins.model.Jenkins.instance.getJob(jobName)
job.properties.each{
    if( it.value.class==hudson.model.ParametersDefinitionProperty){
    println 'Job Parameters'
      println '-'.multiply(15)
      it.value.	getParameterDefinitions().each{
       paramName= it.name!=''?it.name:'unnamed'
       println paramName
       println '-'.multiply(paramName.length())
       println '\t'+it.description
        options.add(paramName as String)
      }
  }
}

return options
