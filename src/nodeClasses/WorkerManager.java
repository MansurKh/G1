package nodeClasses;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.io.*;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.registry.*;

public class WorkerManager {
    private static volatile WorkerManager instance;
	static Logger logger = Logger.getLogger(String.valueOf(RMICore.class));
	private int mapCount;
	private int redCount;
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
            
    public void setRoles(){
        try {
    	    redCount = Workers.size() / 3 > 0 ? Workers.size() / 3 : 1;
    	    mapCount = Workers.size() - redCount;
    	    for(int i = 0; i < Workers.size(); i++){
    	    	if( i < Workers.size() - redCount - 1){
    	    		Workers.get(i).setRole(MRRoles.mapper, i);
    	    	}
    	    	else{
    	    		Workers.get(i).setRole(MRRoles.reducer, Workers.size() - i - 1);
    	    	}
    	    }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    public ArrayList mapReduce(byte[] _bytes){
    	setRoles();
    	ArrayList <KeyValue> list = new ArrayList<KeyValue>();
        try {
        	String text[] = byteToString(_bytes).split("\r\n");
        	
        	for(int i = 0; i < text.length; i++)
        	{
        		list.addAll(Workers.get(i % mapCount).map(text[i]));
        	}
        	logger.info("Mapping completed!");
        	
        	
        	Map<String, ArrayList<Integer>> unilist = new HashMap<String, ArrayList<Integer>>();
        	
        	for (int i = 0; i < list.size(); i++)
        	{
        		if(unilist.containsKey(list.get(i).key)){
        			unilist.get(list.get(i).key).add(list.get(i).value);
        		}else{
        			ArrayList<Integer> l = new ArrayList<Integer>();
        			l.add(list.get(i).value);
        			unilist.put(list.get(i).key, l);
        		}
        	}
        	
        	list = new ArrayList<KeyValue>();
        	int i = 0;
			for (Map.Entry<String, ArrayList<Integer>> entry : unilist.entrySet())
			{
				list.add(Workers.get(i % redCount).reduce(entry.getKey(),entry.getValue()));
				i++;
			}

        	logger.info("Reducing completed!");
        	
        	
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return list;
    }

    private int getServerNumber(String id){
        return Integer.decode("0x" + id.substring(id.length()-2)) % Workers.size();
    }
    
	private String byteToString(byte[] _bytes)
	{
	    String file_string = "";

	    for(int i = 0; i < _bytes.length; i++)
	    {
	        file_string += (char)_bytes[i];
	    }

	    return file_string;    
	}
}