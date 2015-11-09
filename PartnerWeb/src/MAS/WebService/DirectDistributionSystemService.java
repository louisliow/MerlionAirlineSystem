
package MAS.WebService;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "DirectDistributionSystemService", targetNamespace = "http://WebService.MAS/", wsdlLocation = "http://localhost:8080/WebService_war_exploded/services/DirectDistributionSystem?wsdl")
public class DirectDistributionSystemService
    extends Service
{

    private final static URL DIRECTDISTRIBUTIONSYSTEMSERVICE_WSDL_LOCATION;
    private final static WebServiceException DIRECTDISTRIBUTIONSYSTEMSERVICE_EXCEPTION;
    private final static QName DIRECTDISTRIBUTIONSYSTEMSERVICE_QNAME = new QName("http://WebService.MAS/", "DirectDistributionSystemService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://localhost:8080/WebService_war_exploded/services/DirectDistributionSystem?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        DIRECTDISTRIBUTIONSYSTEMSERVICE_WSDL_LOCATION = url;
        DIRECTDISTRIBUTIONSYSTEMSERVICE_EXCEPTION = e;
    }

    public DirectDistributionSystemService() {
        super(__getWsdlLocation(), DIRECTDISTRIBUTIONSYSTEMSERVICE_QNAME);
    }

    public DirectDistributionSystemService(WebServiceFeature... features) {
        super(__getWsdlLocation(), DIRECTDISTRIBUTIONSYSTEMSERVICE_QNAME, features);
    }

    public DirectDistributionSystemService(URL wsdlLocation) {
        super(wsdlLocation, DIRECTDISTRIBUTIONSYSTEMSERVICE_QNAME);
    }

    public DirectDistributionSystemService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, DIRECTDISTRIBUTIONSYSTEMSERVICE_QNAME, features);
    }

    public DirectDistributionSystemService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public DirectDistributionSystemService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns DirectDistributionSystem
     */
    @WebEndpoint(name = "DirectDistributionSystemPort")
    public DirectDistributionSystem getDirectDistributionSystemPort() {
        return super.getPort(new QName("http://WebService.MAS/", "DirectDistributionSystemPort"), DirectDistributionSystem.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns DirectDistributionSystem
     */
    @WebEndpoint(name = "DirectDistributionSystemPort")
    public DirectDistributionSystem getDirectDistributionSystemPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://WebService.MAS/", "DirectDistributionSystemPort"), DirectDistributionSystem.class, features);
    }

    private static URL __getWsdlLocation() {
        if (DIRECTDISTRIBUTIONSYSTEMSERVICE_EXCEPTION!= null) {
            throw DIRECTDISTRIBUTIONSYSTEMSERVICE_EXCEPTION;
        }
        return DIRECTDISTRIBUTIONSYSTEMSERVICE_WSDL_LOCATION;
    }

}
