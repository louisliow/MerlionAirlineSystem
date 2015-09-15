package MAS.Bean;

import MAS.Entity.User;
import MAS.Entity.Workgroup;
import MAS.Exception.NotFoundException;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Stateless(name = "WorkgroupEJB")
@LocalBean
public class WorkgroupBean {
    @PersistenceContext
    private EntityManager em;

    @EJB
    private UserBean userBean;

    public WorkgroupBean() {
    }

    public long createWorkgroup(String name, String description, long ownerId, List<Long> userIds) throws NotFoundException {
        Workgroup workgroup = new Workgroup();
        workgroup.setName(name);
        workgroup.setDescription(description);
        User owner = userBean.getUser(ownerId);
        workgroup.setOwner(owner);
        User user;
        ArrayList<User> users = new ArrayList<>();
        for(Long userId : userIds) {
            user = em.find(User.class, userId);
            if (user != null) {
                users.add(user);
            }
        }
        workgroup.setUsers(users);
        em.persist(workgroup);
        em.flush();
        return workgroup.getId();
    }

    public List<Workgroup> getOwnedWorkgroups(long userId) throws NotFoundException {
        User owner = em.find(User.class, userId);
        if (owner == null) throw new NotFoundException();

        return em.createQuery("SELECT w from Workgroup w WHERE w.owner = :owner", Workgroup.class)
                .setParameter("owner", owner)
                .getResultList();
    }

}
