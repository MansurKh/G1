package nodeClasses;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface WorkerInt  extends Remote {
    public boolean mapreduce(String files, String newFile) throws RemoteException;
    public void setRole(MRRoles rl, int numInRole) throws RemoteException;
    public ArrayList<KeyValue> map(String part) throws RemoteException;
    public KeyValue reduce(String key, ArrayList val) throws RemoteException;
}