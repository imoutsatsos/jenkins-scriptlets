/*** BEGIN META {
  "name" : "Jenkins_ReportJobUsage",
  "comment" : "Creates a report of a job usage (counts) by user over a selectable period of days (default is 365)",
  "parameters" : [ 'vJobName','vDaySpan'],
  "core": "1.596",
  "authors" : [
    { name : "Ioannis K. Moutsatsos" }
  ]
} END META**/

import hudson.model.*
def  jobName=vJobName
def job = hudson.model.Hudson.instance.getJob(jobName)
users=[]
uniqueUsers=[]
tSpan=(vDaySpan!='')?vDaySpan as Integer:365 //one year period
now= new Date()
build=job.getBuilds()

build.each{
  buildDate= new Date(it.getStartTimeInMillis())
  if(now-buildDate<=tSpan) {  
def allActions = it.getAllActions()

allActions.each{
  if(it.class== hudson.model.CauseAction){
 // println it.class
  causes= it.getCauses()
    causes.each{ c->
      if(c.class==hudson.model.Cause$UserIdCause){
        users.add( c.getShortDescription()-'Started by user ')
      }
    }
//    
  }
}
  }//end tSpan logic
}//end each


uniqueUsers=uniqueUsers+users
byNumList=[] //a list for sorting jobs by count

println "${job.name} Builds"
println "Reporting: Last $tSpan days from $now"
println '___________'
uniqueUsers.unique().each{us->
  occurs=users.count{it.startsWith(us)}
  byNumList.add( "${String.format('%03d', occurs)}\t$us")
}

byNumList.sort().reverse().each{
  println it
}

println '___________'
println "${users.size()}:TOTAL"
