package nodeClasses;
import java.io.*;
import java.rmi.RemoteException;

public class DataServer extends RMICore implements DataServerInt{
	
	private String location;

    public DataServer(int port, String service, String folder) throws RemoteException {
        super(port, service);
        this.location = folder;
    }

    @Override
    public void addDataUnit(DataUnit dataUnit) throws RemoteException {
        String id = dataUnit.getId();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(location + "/" + id);
            fos.write(dataUnit._data);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean removeDataUnit(String id) throws RemoteException {
        try {
            File file = new File(location + "/" + id);
            file.delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public DataUnit getDataUnit(String id) throws RemoteException {

    	DataUnit newUnit = new DataUnit();

        try {
            File file = new File(location + "/" + id);
            newUnit._data = new byte[(int) file.length()];
            newUnit.dataSize = (int) file.length();
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            dis.readFully(newUnit._data);
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return newUnit;
    }
}