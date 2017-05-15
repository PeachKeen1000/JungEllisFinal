import java.util.*;
import java.io.*;
import java.net.InetAddress;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public interface ProcessServer extends Remote {
	// public static final String LOOKUP_Name = "Messenger_Service";
	// public void connect(Client d, String username) throws RemoteException;

	// ensure transfer and receive don't get invoked until all machines started
    //NOT NEEDED? - public void ProcessServer() throws RemoteException;
    
    //Instance variables 
    
    //private int id; //stores a unique id for each process
    //private InetAddress ip; //stores a unique ip for each process
    public String myHostName = new String (); 
    public String [] neighbors = new String [5];
    public boolean received = false; 
    public int balance = 200; 
    public int id = 100; 

    //private boolean processesStarted; //indicates that process is ready to transfer
    
    //getter methods?
   // public int getID (); 
    //public InetAddress getIP(); 
    //public InetAddress[] neighbors; 
    //public boolean isReady(); 
    
    public void getHostNames() throws RemoteException; 
    public void sendHostNames() throws RemoteException; 
    public void setNeighbors(String [] n) throws RemoteException; 
     
    public void alertToStart() throws RemoteException; 
    public void setReceived() throws RemoteException;
    
    public void setID() throws RemoteException; 
    public void transfer (int recipient, int amount, int color) throws RemoteException;
	public void deposit (int sender, int amount, int color) throws RemoteException;

    public void setLeader(int n) throws RemoteException;
    public void setLeaders(int n) throws RemoteException;

    public ProcessServer getInstance(String hs) throws RemoteException;
    
    public void sendLocalInfo() throws RemoteException;
    public void receiveLocalInfo(ArrayList<Integer>[] lSent, ArrayList<Integer>[] lReceived, double lSnapshot, int sender) throws RemoteException;

    public ArrayList<Integer> transit(int sender, int recipient) throws RemoteException;

    public void initiateGlobalSnapshot() throws RemoteException;
    
    //Collects all the hostnames of the peers using command line arguments
    //For each hostname, 
    //1. Get registry
    //2. Look up Process instances 
    //3. Add this instance to a list of neighbors 
	//public void loadHostNames();
	 
	//public void sendIPS(); 
	
	//public void alertToStart(); //instructs processes that they can get started transferring and receiving
	
	
	// public void transfer(int id, int amount);
	// public void receive(int id, int amount);
	// public void sendIP();


	// receive array of InetAddresses from last machine
	// public void receiveIP(ArrayList<InetAddress> ip);

	//NOT NEEDED? public ArrayList<InetAddress> ipList = new ArrayList<InetAddress>(5); 


	// public void sendMarker(); 
	// public void receiveMarker();
	// public void isThisLeader(); 
}

