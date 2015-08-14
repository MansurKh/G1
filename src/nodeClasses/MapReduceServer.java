package nodeClasses;
import java.awt.List;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class MapReduceServer extends RMICore implements WorkerInt{
    public static final int data_default_size = 1048576; 
    public static DataStorageInt master;
    
    private String rootPath;

    public MapReduceServer(int port, String service, String rootPath) throws RemoteException {
        super(port, service);
        this.rootPath = rootPath;
    }


	@Override
	public boolean mapreduce(String file, String newFile) throws RemoteException {
		URL host = null;
        master = null;
        try {
            host = new URL("http://127.0.0.1:7000");
            int peerPort = host.getPort() == -1 ? 7000 : host.getPort();

            Registry registry = LocateRegistry.getRegistry(host.getHost(), peerPort);
            master = (DataStorageInt) registry.lookup("MasterServer");

            DataServerManager.getInstance().setServers("storages");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            ArrayList<String> ids = master.getDataOfObject(file);
            for (int i = 0; i < ids.size(); i++){
            	DataUnit data = DataServerManager.getInstance().getData(ids.get(i));
            	ArrayList <KeyValue> fileContent = WorkerManager.getInstance().mapReduce(data.data());
            	
	            String str = new String();
	            
	            for (int j = 0; j < fileContent.size(); j++)
	            {
	            	str = str + fileContent.get(j).key + " : " + fileContent.get(j).value + "\r\n";
	            }
            	
                master.open(newFile);
                
                byte[] b = str.getBytes(Charset.forName("UTF-8"));
                
                int addedBytes = 0;
                while (addedBytes <= b.length) {
      	
                	
                	DataUnit du = new DataUnit(b, data_default_size);
                	master.addData(newFile, du);
                	addedBytes += data_default_size;
                }

//                byte[] buffer = new byte[MasterServer.data_default_size];
//                int bytesRead = 0;
//
//                while ((bytesRead = b.read(buffer)) != -1) {
//                    DataUnit data = new DataUnit(buffer, bytesRead);
//                    master.addData(newFile, data);
           	
            	
            	System.out.println(data.getId());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

		return true;
	}

	@Override
	public ArrayList<KeyValue> map(String part) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}


    
    public void setServers(String fileStorage){
        WorkerManager.getInstance().setServers(fileStorage);
    }
    
	@Override
	public void setRole(MRRoles rl, int numInRole) throws RemoteException {
		// TODO Auto-generated method stub
	}


	@Override
	public KeyValue reduce(String key, ArrayList val) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}



}

