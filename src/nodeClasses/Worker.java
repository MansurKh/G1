package nodeClasses;
import java.awt.List;
import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Worker extends RMICore implements WorkerInt{
	
	private String location;
	private MRRoles role;
	private int numInRole;

    public Worker(int port, String service, String folder) throws RemoteException {
        super(port, service);
        this.location = folder;
    }

    
    @Override
    public boolean mapreduce(String file, String newFile) throws RemoteException {
    	if(role == MRRoles.mapper){
    		
    	}
    	if(role == MRRoles.reducer){
    		
    	}
    	return true;
    }
    
    @Override
    public void setRole(MRRoles rl, int numInRole) throws RemoteException {
        role = rl;
        this.numInRole = numInRole;
    }

   
	@Override
	public ArrayList<KeyValue> map(String part) throws RemoteException {
		String st[] = part.split(" ");
		ArrayList<KeyValue> tempAr = new ArrayList<KeyValue>();
		for (int i = 0; i < st.length; i++){
			KeyValue kv = new KeyValue();
			kv.key = st[i];
			kv.value = 1;
			tempAr.add(kv);
		}
		return tempAr;
	}


	@Override
	public KeyValue reduce(String key, ArrayList val) throws RemoteException {
		KeyValue kv = new KeyValue();
		kv.key = key;
		for (int i = 0; i < val.size(); i++)
    	{
			kv.value = kv.value + (int) val.get(i);    		
    	}
		return kv;
	}
}