import java.rmi.RemoteException;

public class DataServerStart {

	public static void main(String[] args) {
        int port = Integer.valueOf(args[0]);
        try {
            DataServer dataServer = new DataServer(port,"DataServer", args[1]);
            dataServer.RMIStart();
            System.out.println("DataServers has been started: " + args[1] );
        } catch (RemoteException e) {
        	System.out.println("Error occured while starting the data server");
        }
	}

}
