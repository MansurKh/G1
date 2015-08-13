import java.util.ArrayList;
import java.util.logging.Logger;
import java.io.*;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.registry.*;

public class WorkerManager {
    private static volatile WorkerManager instance;
	static Logger logger = Logger.getLogger(String.valueOf(RMICore.class));

    public ArrayList <WorkerInt> Workers;
    private WorkerManager() { }

    public static WorkerManager getInstance() {
        if (instance == null ) {
            synchronized (WorkerManager.class) {
                if (instance == null) {
                    instance = new WorkerManager();
                }
            }
        }
        return instance;
    }

    public void setServers(String file){
    	Workers = new ArrayList <WorkerInt> ();
        try {
            BufferedReader buff = new BufferedReader(new FileReader(file));
            String line;
            while((line = buff.readLine()) != null) {
                URL address = new URL("http://" + line);
                int port = address.getPort();

                Registry registry = LocateRegistry.getRegistry(address.getHost(), port);
                WorkerInt Worker = (WorkerInt) registry.lookup("Worker");
                Workers.add(Worker);
            }
            logger.info("Connected to " + Workers.size() + " workers!");
        }
        catch (Exception e){
            logger.warning("Error while connecting to workers: " + e.getMessage());
        }
    }

    
    public DataUnit getData(String id){
        try {
            return Workers.get(getServerNumber(id)).reduce(id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void putData(DataUnit dataUnit){
        try {
        	Workers.get(getServerNumber(dataUnit.getId())).map(dataUnit);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteData(String id){
        try {
            return Workers.get(getServerNumber(id)).removeTemp(id);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getServerNumber(String id){
        return Integer.decode("0x" + id.substring(id.length()-2)) % Workers.size();
    }
}