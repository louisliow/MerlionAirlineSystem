package MAS.ManagedBean.Auth;

import MAS.Bean.AuditLogBean;
import MAS.Bean.UserBean;
import MAS.Common.Constants;
import MAS.Common.Utils;
import MAS.Entity.Permission;
import MAS.Entity.Role;
import MAS.Entity.User;
import MAS.Exception.NotFoundException;
import MAS.Structure.MainMenu;
import MAS.Structure.MenuEntry;
import MAS.Structure.Page;
import MAS.Structure.Pages;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

@ManagedBean
@SessionScoped
public class AuthManagedBean implements Serializable {
    @EJB
    private UserBean userBean;

    @EJB
    private AuditLogBean auditLogBean;

    private long userId;
    private String userDisplayName;
    private boolean authenticated = false;
    private Set<String> permissions;
    private MainMenu mainMenu;
    private Date lastActive;

    private HashMap<String, Page> pages;

    public AuthManagedBean() {
        pages = new HashMap<>();
        for(Field pagesField : Pages.class.getDeclaredFields()) {
            try {
                Page page = (Page) pagesField.get(null);
                pages.put(page.getPath(), page);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        lastActive = new Date();
    }

    public void inactivityTimeout() {
        if (Utils.minutesLater(lastActive, Constants.INACTIVITY_TIMEOUT).before(new Date())) {
            userId = -1;
            authenticated = false;
            FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        }
    }

    public void populatePermissions() {
        permissions = new HashSet<>();
        if (!authenticated) return;
        try {
            for (Role role : userBean.getUser(userId).getRoles()) {
                for (Permission permission : role.getPermissions()) {
                    permissions.add(permission.getName());
                }
            }
        } catch (NotFoundException e) {
        }
    }

    public boolean login(String username, String password) {
        try {
            userId = userBean.login(username, password);
            User user = userBean.getUser(userId);
            userDisplayName = user.getFirstName() + " " + user.getLastName();
            authenticated = true;
            lastActive = new Date();
            populatePermissions();
            generateMenu(permissions);

            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            createAuditLog("Logged in from " + request.getRemoteAddr(), "login");

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public void logout() {
        userId = -1;
        authenticated = false;
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        forwardToLogin();
    }

    public void checkPermission() {
        inactivityTimeout();
        lastActive = new Date();
        if (!authenticated) {
            forwardToLogin();
        } else {
            // Refresh permission on each page load
            populatePermissions();
            generateMenu(permissions);

            // Check if user is locked
            try {
                if(userBean.getUser(userId).isLocked()) {
                    logout();
                }
            } catch (NotFoundException e) {
                logout();
                e.printStackTrace();
            }

            String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
            if (viewId.substring(viewId.length() - 6).equals(".xhtml")) {
                viewId = viewId.substring(0, viewId.length() - 6);
            }
            Page page = pages.get(viewId);
            if (page != null && page.getPermissions().size() != 0 && Collections.disjoint(permissions, page.getPermissions())) {
                forwardToLogin();
            }
        }
    }

    public void forwardToLogin() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/Auth");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createAuditLog(String description, String category) {
        try {
            auditLogBean.createAuditLog(userId, description, category);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void generateMenu(Set<String> permissions) {
        mainMenu = new MainMenu();
        List<MenuEntry> menuEntries = mainMenu.getEntries();
        Iterator<MenuEntry> it = menuEntries.iterator();
        while (it.hasNext()) {
            MenuEntry menuEntry = it.next();
            menuEntry.stripInaccessibleChildren(permissions);
            if(menuEntry.getChildren().size() == 0) {
                it.remove();
            }
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Set<String> getPermissions() {
        return this.permissions;
    }


    public MainMenu getMainMenu() {
        return mainMenu;
    }

    public HashMap<String, Page> getPages() {
        return pages;
    }

    public void setPages(HashMap<String, Page> pages) {
        this.pages = pages;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public long getTimeout() {
        return Constants.INACTIVITY_TIMEOUT * 60 * 1000 - (new Date()).getTime() + lastActive.getTime();
    }

    public void sessionTimeoutAjax() {
        inactivityTimeout();
        String json;
        if(!isAuthenticated()) {
            json = "{\"authenticated\": false}";
        } else {
            json = "{\"authenticated\": true, \"timeout\": " + getTimeout() + "}";
        }

        FacesContext ctx = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
        response.setContentLength(json.length());
        response.setContentType("application/json");

        try {
            response.getOutputStream().write(json.getBytes());
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ctx.responseComplete();
    }
}
