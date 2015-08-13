import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

public class RMICore extends UnicastRemoteObject {

	static Logger logger = Logger.getLogger(String.valueOf(RMICore.class));
    public String service;
    public int port;
    
    public RMICore(int port, String serviceName) throws RemoteException {
        super();
        this.port=port;
        this.service=serviceName;
    }

    public void RMIStart(){
        try {
            LocateRegistry.createRegistry(port).rebind(service, this);
            logger.info("RMI has been started");
        }catch (Exception e)
        {
        	logger.warning(e.getMessage());
        }
    }

    public void RMIStop(){
        try {
            LocateRegistry.getRegistry(port).unbind(service);
            logger.info("RMI has been stopped");
        }catch (Exception e)
        {
        	logger.warning(e.getMessage());
        }
    }
}
