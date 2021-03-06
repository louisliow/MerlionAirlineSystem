package MAS.ManagedBean.SystemAdmin;

import MAS.Bean.RoleBean;
import MAS.Entity.Permission;
import MAS.ManagedBean.Auth.AuthManagedBean;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import java.util.*;

@ManagedBean
public class CreateRoleManagedBean {
    @EJB
    private RoleBean roleBean;

    @ManagedProperty(value="#{authManagedBean}")
    private AuthManagedBean authManagedBean;

    private String roleName;
    private List<Permission> permissions;
    private Map<Long, Boolean> permissionsMap;

    @PostConstruct
    public void init() {
        populatePermissions();
    }

    private void populatePermissions() {
        permissions = roleBean.getAllPermissions();
        permissionsMap = new HashMap<>();
        for (Permission permission : permissions) {
            permissionsMap.put(permission.getId(), Boolean.FALSE);
        }
    }

    public void createRole() {
        ArrayList<Long> permissionIds = new ArrayList<>();
        for (Object o : permissionsMap.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            if ((Boolean) pair.getValue()) {
                permissionIds.add((Long) pair.getKey());
            }
        }
        roleBean.createRole(roleName, permissionIds);

        authManagedBean.createAuditLog("Created new role: " + roleName, "create_role");

        setRoleName(null);
        populatePermissions();
        FacesMessage m = new FacesMessage("Role created successfully.");
        m.setSeverity(FacesMessage.SEVERITY_INFO);
        FacesContext.getCurrentInstance().addMessage("status", m);
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Map<Long, Boolean> getPermissionsMap() {
        return permissionsMap;
    }

    public void setPermissionsMap(Map<Long, Boolean> permissionsMap) {
        this.permissionsMap = permissionsMap;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public void setAuthManagedBean(AuthManagedBean authManagedBean) {
        this.authManagedBean = authManagedBean;
    }
}
