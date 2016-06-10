/*** BEGIN META {
  "name" : "Return Values per User Role",
  "comment" : "Use the Role Strategy plug-in classes, and according to the currently loggged-in user, return different set of parameters",
  "parameters" : [ ''],
  "core": "1.580.1",
  "authors" : [
    { name : "Bruno P. Kinoshita" }
  ]
} END META**/

/**
 * Use the Role Strategy Plug-in classes, and according to the currently loggged-in user,
 * return different set of parameters.
 * Author: Bruno P. Kinoshita
 * Last Update: 4/24/2016 rev 1
 * Required Script Parameters: 
 * Required Plug-ins: Role Strategy Plug-in
 */

 import hudson.model.User
import hudson.model.Hudson
import hudson.security.AuthorizationStrategy
import hudson.security.Permission
import com.michelin.cio.hudson.plugins.rolestrategy.RoleBasedAuthorizationStrategy
import com.michelin.cio.hudson.plugins.rolestrategy.RoleMap

AuthorizationStrategy strategy = Hudson.getInstance().getAuthorizationStrategy();

jobs = []
user = User.current()
userId = user.getId()

if (strategy != null && strategy instanceof com.michelin.cio.hudson.plugins.rolestrategy.RoleBasedAuthorizationStrategy) {
    roleStrategy = (RoleBasedAuthorizationStrategy) strategy;
    // not very straightforward to get the groups for a given user
    roles = roleStrategy.getGrantedRoles("globalRoles")
    for (entry in roles) {
        role = entry.key
        users = entry.value
        if (role.getName().equals("tester")) {
            if (userId in users)
                return ["PROJECT_FOR_TESTERS1", "PROJECT_FOR_TESTERS2"]
        } else if (role.getName().equals("admin")) {
            if (userId in users)
                return ["PROJECT_FOR_ADMINS1", "PROJECT_FOR_ADMINS2"]
        }
    }
}

return jobs
// TODO: handle anonymous user ;-)