package nodeClasses;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class MasterServer extends RMICore implements DataStorageInt{
    public static final int data_default_size = 1048576; 

    private String rootPath;

    public MasterServer(int port, String service, String rootPath) throws RemoteException {
        super(port, service);
        this.rootPath = rootPath;
    }

    @Override
    public ArrayList<String> dir(String path) throws RemoteException {
        ArrayList<String> array = new ArrayList<String>();
        File folder = new File(rootPath + path);
        for (final File fileEntry : folder.listFiles()) {
            array.add("     " + ((fileEntry.isDirectory()) ? fileEntry.getName() + "/" : fileEntry.getName()));
        }	
        return array;
    }

    @Override
    public boolean mkdir(String path) throws RemoteException {
        File folder = new File(rootPath + path);
        if (folder.exists()){
            return true;
        }
        return folder.mkdirs();
    }

    @Override
    public void open(String path) throws RemoteException {
        System.out.println("Save the file " + path);

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(rootPath + path, "UTF-8");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList <String> getDataOfObject(String path) throws RemoteException {
        ArrayList<String> units = new ArrayList<String>();
        try {
            BufferedReader buff = new BufferedReader(new FileReader(rootPath + "/" + path));
            String line;
            while((line = buff.readLine()) != null) {
            	units.add(line);
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return units;
    }

    @Override
    public void addData(String path, DataUnit data) throws RemoteException {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(rootPath + "/" + path, true)));
            out.println(data.getId());
            out.close();
            DataServerManager.getInstance().putData(data);
        } catch (IOException e) {
        }
    }

    @Override
    public boolean rm(String path) throws RemoteException {
        if (path.equals("/") || path.equals("\\")){
            throw new RemoteException("Can not remove root");
        }
        try {
            File folder = new File(rootPath + path);
            if (!folder.isDirectory()){
                BufferedReader br = new BufferedReader(new FileReader(rootPath + path));
                String line;
                while((line = br.readLine()) != null) {
                    System.out.println("Removing data ");
                    DataServerManager.getInstance().deleteData(line);
                }
                br.close();
            }

            System.out.println("Removing file from root");
            Files.delete(Paths.get(rootPath + path));
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void initialize() throws RemoteException {
        deleteLocalFolder(new File(rootPath + "/"));
        File folder = new File(rootPath + "/");
        folder.mkdir();
    }


    public void setServers(String fileStorage){
        DataServerManager.getInstance().setServers(fileStorage);
    }

    private static void deleteLocalFolder(File folder) {
        File[] files = folder.listFiles();
        if(files != null) { 
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteLocalFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }
}

