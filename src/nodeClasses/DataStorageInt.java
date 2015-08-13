package nodeClasses;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface DataStorageInt extends Remote{
    public void initialize() throws RemoteException;
    public ArrayList<String> dir(String path) throws RemoteException;
    public boolean 			 mkdir(String path) throws RemoteException;
    public void 		     open(String path) throws RemoteException;
    public ArrayList<String> getDataOfObject(String path) throws RemoteException;
    public void 			 addData(String path, DataUnit data) throws RemoteException;
    public boolean 			 rm(String path) throws RemoteException;
}
