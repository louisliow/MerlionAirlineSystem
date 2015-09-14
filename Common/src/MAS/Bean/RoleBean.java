package MAS.Bean;

import MAS.Entity.Permission;
import MAS.Entity.Role;
import MAS.Exception.NotFoundException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Stateless(name = "RoleEJB")
@LocalBean
public class RoleBean {
    @PersistenceContext
    private EntityManager em;

    public RoleBean() {
    }

    public long createPermission(String name) {
        Permission permission = new Permission();
        permission.setName(name);
        em.persist(permission);
        em.flush();
        return permission.getId();
    }

    public long createRole(String name, List<Long> permissionIds) {
        Role role = new Role();
        role.setName(name);
        Permission permission;
        ArrayList<Permission> permissions = new ArrayList<>();
        for(Long permissionId : permissionIds) {
            permission = em.find(Permission.class, permissionId);
            if (permission != null) {
                permissions.add(permission);
            }
        }
        role.setPermissions(permissions);
        em.persist(role);
        em.flush();
        return role.getId();
    }

    public boolean isNameUnique(String name) {
        return (Long) em.createQuery("SELECT COUNT(r) FROM Role r WHERE r.name = :name").setParameter("name", name).getSingleResult() == 0;
    }

    public void removeRole(long id) throws NotFoundException {
        Role role = em.find(Role.class, id);
        if (role == null) throw new NotFoundException();
        em.remove(role);
    }

    public List<Permission> getAllPermissions() {
        return em.createQuery("SELECT p from Permission p", Permission.class).getResultList();
    }

    public List<Role> getAllRoles() {
        return em.createQuery("SELECT r from Role r", Role.class).getResultList();
    }

}