import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import nodeClasses.Commands;
import nodeClasses.DataServerManager;
import nodeClasses.DataStorageInt;
import nodeClasses.DataUnit;
import nodeClasses.MasterServer;
import nodeClasses.WorkerInt;


public class ClientStart {
    public static DataStorageInt master;
    public static WorkerInt wMaster;
    private static String arg = "";
	static Logger logger = Logger.getLogger(String.valueOf(ClientStart.class));
    private static FileHandler fHandler;
    private static ConsoleHandler ch;
    
    public static void main(String[] args) {
        try {
        	logger.setUseParentHandlers(false);
        	fHandler = new FileHandler("logs");
        	logger.addHandler(fHandler);
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        	        	
            Formatter formatter = new Formatter() {
                public String format(LogRecord rec) {
                    return sdf.format(rec.getMillis()) + ": "  + rec.getSourceClassName() + " :" + rec.getMessage() + "\n";
                }
            };
            fHandler.setFormatter(formatter);

            ch = new ConsoleHandler();
            ch.setFormatter(formatter);
            logger.addHandler(ch);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        URL host = null;
        master = null;
        try {
            host = new URL("http://" + args[0]);
            int peerPort = host.getPort() == -1 ? 7000 : host.getPort();

            Registry registry = LocateRegistry.getRegistry(host.getHost(), peerPort);
            master = (DataStorageInt) registry.lookup("MasterServer");

            DataServerManager.getInstance().setServers("storages");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        host = null;
        wMaster = null;
        try {
            host = new URL("http://127.0.0.1:10000");
            int Port = host.getPort() == -1 ? 7000 : host.getPort();

            Registry wRegistry = LocateRegistry.getRegistry(host.getHost(), Port);
            wMaster = (WorkerInt) wRegistry.lookup("MapReduceServer");

            DataServerManager.getInstance().setServers("workers");
        } catch (Exception e) {
            e.printStackTrace();
        }

        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        String command = "";
        try {
            System.out.println("<<waiting for command>>");
            while ((command = consoleInput.readLine()) != null) {
                try {
                	command = command.trim();
                    String [] cmd = command.split(" ");
                    
                    Commands action = Commands.valueOf(cmd[0]);
                    
                    switch (action) {
                	
                	case dir:                    	
                		arg = cmd.length > 1 ? cmd[1] : "/" ;
                        ArrayList <String> files = master.dir(Character.toString(arg.charAt(0)) == "/" ? arg : "/" + arg);
                        if(files.size()<1){logger.info("The folder is empty!");}
                        for (int i=0; i < files.size(); i++){
                            System.out.println(files.get(i));
                        }
                        break;
                    
                	case cp:
                		if(cmd.length < 3){
                			logger.info("Not enough parameters!");
                			break;
                		}
                		
                		copyFile(cmd[1], Character.toString(cmd[2].charAt(0)) == "/" ? cmd[2] : "/" + cmd[2]);
	                    logger.info("File "+cmd[1] + " was copied");
	                    break;
	
                	case mkdir:
                        try {
                        	if(cmd.length < 2){
                    			logger.info("Specify folder name");
                    			break;
                    		}
                        	if (master.mkdir(Character.toString(cmd[1].charAt(0)) == "/" ? cmd[1] : "/" + cmd[1])){
                            	logger.info("Folder created");
                            }
                            else {
                                logger.info("Folder was not created");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
	
                	case get:
                		if(cmd.length < 3){
                			logger.info("Not enough parameters!");
                			break;
                		}
                		
                		getFile(Character.toString(cmd[1].charAt(0)) == "/" ? cmd[1] : "/" + cmd[1] , cmd[2]);
                    	logger.info("File " + cmd[1] + " has ended.");
                    	break;

	
                	case rm:
                		if(cmd.length < 2){
                			logger.info("Not enough parameters!");
                			break;
                		}
                		
                		master.rm(Character.toString(cmd[1].charAt(0)) == "/" ? cmd[1] : "/" + cmd[1]);
                        logger.info(cmd[1] + "was succesfully removed");
                        break;
	
                	case init:
                        master.initialize();
                    	logger.info("Storage reinitialised");
                    	break;
                    	
                	case mapreduce:
                		String fileToMR = Character.toString(cmd[1].charAt(0)) == "/" ? cmd[1] : "/" + cmd[1];
                		String newfileToMR = Character.toString(cmd[2].charAt(0)) == "/" ? cmd[2] : "/" + cmd[2];
                		if(cmd.length < 2){
            			logger.info("Not enough parameters!");
            			break;
                        }
                		if(wMaster.mapreduce(fileToMR, newfileToMR)){
                			logger.info("Mapreduce for the file " + fileToMR + " has been completed successfully");
                		}else{
                			logger.info("Mapreduce for the file " + fileToMR + " has failed");
                		}
                			
                    	break;
                    	
                	case help:
                        logger.info("Recognizable commands:\n dir,\n cp,\n mkdir,\n get,\n rm,\n init,\n open,\n help");
                    	break;
                        
	                default:
	                	 logger.info("Bad command");
	                	 break;
                        
                    }
                }
                catch (Exception e){
                	logger.info("Unrecognized command");
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(String path, String remotePath){
        try {
            master.open(remotePath);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        File file = new File(path);
        FileInputStream f = null;
        BufferedInputStream b = null;

        try {
            f = new FileInputStream(file);
            b = new BufferedInputStream(f);

            byte[] buffer = new byte[MasterServer.data_default_size];
            int bytesRead = 0;

            while ((bytesRead = b.read(buffer)) != -1) {
                DataUnit data = new DataUnit(buffer, bytesRead);
                master.addData(remotePath, data);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                f.close();
                b.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void getFile(String remotePath, String localPath){
        try {
            ArrayList<String> ids = master.getDataOfObject(remotePath);
            for (int i=0; i < ids.size(); i++){
            	DataUnit data = DataServerManager.getInstance().getData(ids.get(i));
                FileOutputStream output = new FileOutputStream(localPath, true);
                output.write(data.data());
                output.close();
                System.out.println(data.getId());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
