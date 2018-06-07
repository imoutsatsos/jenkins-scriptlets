import hudson.model.*
import org.jenkinsci.plugins.associatedfiles.*


def runParam=vBuildRef.split('#')
def jobName=runParam[0]
def buildNum=runParam[1]
  def options=[]

def job = hudson.model.Hudson.instance.getJob(jobName)
build=job.getBuildByNumber(buildNum as int)

 
def allActions = build.getAllActions()
def oldAssocFileAction=build.getActions(AssociatedFilesAction.class)
if (oldAssocFileAction !=null){
oldAssocFileAction.each{
  it.getBuildAssociatedFilesList().each{af->
    options.add(af)
  }
}
}

return options