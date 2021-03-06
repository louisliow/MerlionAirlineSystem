
package MAS.WebService.FFPAlliance;

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
@WebServiceClient(name = "FFPAllianceService", targetNamespace = "http://WebService.MAS/", wsdlLocation = "http://localhost:8080/WebService_war_exploded/services/FFPAlliance?wsdl")
public class FFPAllianceService
    extends Service
{

    private final static URL FFPALLIANCESERVICE_WSDL_LOCATION;
    private final static WebServiceException FFPALLIANCESERVICE_EXCEPTION;
    private final static QName FFPALLIANCESERVICE_QNAME = new QName("http://WebService.MAS/", "FFPAllianceService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://localhost:8080/WebService_war_exploded/services/FFPAlliance?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        FFPALLIANCESERVICE_WSDL_LOCATION = url;
        FFPALLIANCESERVICE_EXCEPTION = e;
    }

    public FFPAllianceService() {
        super(__getWsdlLocation(), FFPALLIANCESERVICE_QNAME);
    }

    public FFPAllianceService(WebServiceFeature... features) {
        super(__getWsdlLocation(), FFPALLIANCESERVICE_QNAME, features);
    }

    public FFPAllianceService(URL wsdlLocation) {
        super(wsdlLocation, FFPALLIANCESERVICE_QNAME);
    }

    public FFPAllianceService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, FFPALLIANCESERVICE_QNAME, features);
    }

    public FFPAllianceService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public FFPAllianceService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns FFPAlliance
     */
    @WebEndpoint(name = "FFPAlliancePort")
    public FFPAlliance getFFPAlliancePort() {
        return super.getPort(new QName("http://WebService.MAS/", "FFPAlliancePort"), FFPAlliance.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns FFPAlliance
     */
    @WebEndpoint(name = "FFPAlliancePort")
    public FFPAlliance getFFPAlliancePort(WebServiceFeature... features) {
        return super.getPort(new QName("http://WebService.MAS/", "FFPAlliancePort"), FFPAlliance.class, features);
    }

    private static URL __getWsdlLocation() {
        if (FFPALLIANCESERVICE_EXCEPTION!= null) {
            throw FFPALLIANCESERVICE_EXCEPTION;
        }
        return FFPALLIANCESERVICE_WSDL_LOCATION;
    }

}
