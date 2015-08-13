package nodeClasses;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WorkerInt  extends Remote {
    public void map(DataUnit dfsunit) throws RemoteException;
    public DataUnit reduce(String id) throws RemoteException;
    public boolean removeTemp(String id) throws RemoteException;
}