import java.rmi.RemoteException;

import nodeClasses.Worker;

public class WorkerStart {

	public static void main(String[] args) {
        int port = Integer.valueOf(args[0]);
        try {
            Worker worker = new Worker(port,"Worker", args[1]);
            worker.RMIStart();
            System.out.println("Worker has been started: " + args[1] );
        } catch (RemoteException e) {
        	System.out.println("Error occured while starting the worker");
        }
	}

}
