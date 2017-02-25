/*copyJobConfig.groovy
  Adapted for Scriptler Use and support of CloudBees folder namespace
*/

def env=System.getenv()
def JENKINS_HOME=env['JENKINS_HOME']
def WORKSPACE=vWorkspace //scriptler parameter
def jobName=vJobName //scriptlet parameter
def job_path=jobName.split('/')
if (job_path.length>1){
  jobName="${job_path[0]}/jobs/${job_path[1]}" 
  println("Adjusting for CloudBees Folder naming to: $jobName")
}else{
  jobName=jobName
}

println "\nCOPY (to Workspace) config.xml: $jobName\n"
def configPath="$JENKINS_HOME/jobs/${jobName}/config.xml"
def configFile=new File(configPath)
assert configFile.exists()

download(configFile, WORKSPACE,fileName)


/* a simple method to copy a file */
def download(configFile,destination, fileName)
{
    def file = new FileOutputStream("$destination/$fileName")
    def out = new BufferedOutputStream(file)
    out << configFile.newDataInputStream()
    out.close()
}
