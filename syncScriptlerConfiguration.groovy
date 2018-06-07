import org.apache.commons.io.FileUtils;
import org.jenkinsci.plugins.scriptler.ScriptlerManagement
import org.jenkinsci.plugins.scriptler.config.Parameter;
import org.jenkinsci.plugins.scriptler.config.Script;
import org.jenkinsci.plugins.scriptler.config.ScriptlerConfiguration;
import org.jenkinsci.plugins.scriptler.share.ScriptInfo;
import org.jenkinsci.plugins.scriptler.util.ScriptHelper;

cfg=ScriptlerConfiguration.getConfiguration()
println ScriptlerManagement.getScriptlerHomeDirectory()//io.File
executionPermission=false
if (allowExecution=='true'){
  executionPermission=true
}

println ScriptlerConfiguration.getXmlFile()//hudson.XmlFile
scriptFolder=new File('D:/DEVTOOLS/Jenkins/scriptler/scripts')
availablePhysicalScripts=getAvailableScripts(scriptFolder)

println '--'*10+'NEW SCRIPTS'+'--'*10

        // check if all physical files are available in the configuration
        // if not, add it to the configuration
        availablePhysicalScripts.each {file->
            if (cfg.getScriptById(file.getName()) == null) {
                final ScriptInfo info = ScriptHelper.extractScriptInfo(FileUtils.readFileToString(file, "UTF-8"));
                if (info != null) {
                    final List<String> paramList = info.getParameters();
                    Parameter[] parameters = new Parameter[paramList.size()];
                    for (int i = 0; i < parameters.length; i++) {
                        parameters[i] = new Parameter(paramList.get(i), null);
                    }
                  if (updateAndSave=='true')
                  {
                    cfg.addOrReplace(new Script(file.getName(), info.getName(), info.getComment(), executionPermission, parameters, false));
                    println 'Imported scripts with Metadata Header'
                  }
                  println "${file.getName()} \n\t\t\tMetadata Header Present  \n\t\t\t${info.getComment()}"
                } else {
                  if (updateAndSave=='true')
                  {
                    cfg.addOrReplace(new Script(file.getName(), file.getName(), Messages.script_loaded_from_directory(), executionPermission, null, false));
                    println 'Imported scripts found in scripts directory'
                  }                    
                  println "${file.getName()} \n\t\t\tLoaded from Directory"
                }

            }
        }
/*
write new configuration to disk
*/
if (updateAndSave=='true'){
  cfg.save()
  println 'Imported scripts configured and saved'
}

return null

def List<File> getAvailableScripts(File scriptDirectory) throws IOException {
        //LOGGER.log(Level.FINE, "Listing files of {0}", scriptDirectory.getAbsoluteFile());

        File[] scriptFiles = scriptDirectory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".groovy");
            }
        });

        List<File> fileList;
        if (scriptFiles == null) {
            fileList = new ArrayList<File>();
        } else {
            fileList = Arrays.asList(scriptFiles);
        }

        return fileList;
    }