import java.io.*;
import java.rmi.RemoteException;

public class Worker extends RMICore implements WorkerInt{
	
	private String location;

    public Worker(int port, String service, String folder) throws RemoteException {
        super(port, service);
        this.location = folder;
    }

    @Override
    public void map(DataUnit dataUnit) throws RemoteException {
        String id = dataUnit.getId();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(location + "/" + id);
            fos.write(dataUnit.data);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean removeTemp(String id) throws RemoteException {
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
    public DataUnit reduce(String id) throws RemoteException {

    	DataUnit newUnit = new DataUnit();

        try {
            File file = new File(location + "/" + id);
            newUnit.data = new byte[(int) file.length()];
            newUnit.dataSize = (int) file.length();
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            dis.readFully(newUnit.data);
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return newUnit;
    }
}