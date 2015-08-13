import java.rmi.RemoteException;
import java.io.*;

public class MasterServerStart {
    public static void main(String[] args) {

        int port = Integer.valueOf(args[0]);
        try {
            MasterServer master = new MasterServer(port, "MasterServer", args[1]);
            master.setServers("storages", "workers");
            master.RMIStart();

            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

            String command = "";

            try {
                while ((command = consoleInput.readLine())!=null) {
                	command = command.toLowerCase().trim();
                    String comnd = command.substring(0, (command.indexOf(" ") == -1) ? command.length() :
                    	command.indexOf(" "));
                    if (comnd.equals("exit")) {
                        break;
                    }
                }
                master.RMIStop();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (RemoteException e) {
           System.out.println(e.getMessage());
        }
    }
}
