import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DataServerInt  extends Remote {
    public void addDataUnit(DataUnit dfsunit) throws RemoteException;
    public boolean removeDataUnit(String id) throws RemoteException;
    public DataUnit getDataUnit(String id) throws RemoteException;
}
