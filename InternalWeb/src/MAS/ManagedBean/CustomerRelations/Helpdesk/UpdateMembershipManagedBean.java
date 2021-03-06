package MAS.ManagedBean.CustomerRelations.Helpdesk;

import MAS.Bean.CustomerBean;
import MAS.Common.Constants;
import MAS.Entity.Customer;
import MAS.Exception.NotFoundException;
import MAS.ManagedBean.CustomerRelations.HelpdeskManagedBean;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@ManagedBean
@ViewScoped
public class UpdateMembershipManagedBean implements Serializable {
    @EJB
    CustomerBean customerBean;

    private Customer customer;
    private Customer customerUpdated;

    @PostConstruct
    public void init() {
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            customer = customerBean.getCustomer(Long.parseLong(params.get("customerId")));
            customerUpdated = customerBean.getCustomer(Long.parseLong(params.get("customerId")));
        } catch (Exception e) {
            // Cannot find customer!
        }
    }

    public HashMap<String, Integer> getTierList() {
        HashMap<String, Integer> tierList = new HashMap<>();
        tierList.put(Constants.FFP_TIER_BLUE_LABEL, Constants.FFP_TIER_BLUE);
        tierList.put(Constants.FFP_TIER_SILVER_LABEL, Constants.FFP_TIER_SILVER);
        tierList.put(Constants.FFP_TIER_GOLD_LABEL, Constants.FFP_TIER_GOLD);
        return tierList;
    }

    public void save() {
        try {
            if (customer.getTier() == Constants.FFP_TIER_BLUE) {
                customer.setStatusExpiry(null);
            }

            customerBean.updateCustomer(customer);

            // Update card to reflect new changes
            customerUpdated = customerBean.getCustomer(customer.getId());

            FacesMessage m = new FacesMessage("Customer membership updated successfully.");
            m.setSeverity(FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().addMessage("status", m);

        } catch (NotFoundException e) {
            // Cannot find customer!
        }
    }

    public void statusExpiryAjaxListener(AjaxBehaviorEvent event) {
        customer.setQualificationEndDate(customer.getStatusExpiry());
    }

    public void qualificationEndDateResetAjaxListener(AjaxBehaviorEvent event) {
        customer.setQualificationEndDate(null);
    }

    public double random() {
        return Math.random();
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomerUpdated() {
        return customerUpdated;
    }

    public void setCustomerUpdated(Customer customerUpdated) {
        this.customerUpdated = customerUpdated;
    }
}
