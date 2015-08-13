package nodeClasses;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.io.*;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.registry.*;


public class DataServerManager {
    private static volatile DataServerManager instance;
	static Logger logger = Logger.getLogger(String.valueOf(RMICore.class));

    public ArrayList <DataServerInt> DataServers;
    private DataServerManager() { }

    public static DataServerManager getInstance() {
        if (instance == null ) {
            synchronized (DataServerManager.class) {
                if (instance == null) {
                    instance = new DataServerManager();
                }
            }
        }
        return instance;
    }

    public void setServers(String file){
    	DataServers = new ArrayList <DataServerInt> ();
        try {
            BufferedReader buff = new BufferedReader(new FileReader(file));
            String line;
            while((line = buff.readLine()) != null) {
                URL address = new URL("http://" + line);
                int port = address.getPort();

                Registry registry = LocateRegistry.getRegistry(address.getHost(), port);
                DataServerInt dataServer = (DataServerInt) registry.lookup("DataServer");
                DataServers.add(dataServer);
            }
            logger.info("Connected to " + DataServers.size() + " dataservers!");
        }
        catch (Exception e){
            logger.warning("Error while connecting to dataservers: " + e.getMessage());
        }
    }

    public DataUnit getData(String id){
        try {
            return DataServers.get(getServerNumber(id)).getDataUnit(id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void putData(DataUnit dataUnit){
        try {
        	DataServers.get(getServerNumber(dataUnit.getId())).addDataUnit(dataUnit);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteData(String id){
        try {
            return DataServers.get(getServerNumber(id)).removeDataUnit(id);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getServerNumber(String id){
        return Integer.decode("0x" + id.substring(id.length()-2)) % DataServers.size();
    }
}