
import java.util.*;
import java.io.*;
import java.io.PrintWriter;
import java.io.File;
import java.net.InetAddress;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Process implements ProcessServer {
	
	private String myHostName; 
    private String [] neighbors; 
    public InetAddress address; 
    public boolean received;
    public int balance;  
    public int id; 
    private int leader; 


	private int color = 0; //a value of 0 is white a value of 1 is red, defaults to white by the algorithm’s specifications. 
	private double localSnapshot; 

	private ArrayList<Integer> [] localSent;
	// index = process sent to
	// ArrayList<Integer> = amounts sent

	//Instances variables for initiator:
	private ArrayList<Integer>[] localReceived;

	private ArrayList<Integer> [][] globalSent;
	
	private ArrayList<Integer> [][] globalReceived;

	private String [] processBalances;

	public int numProcesses;

	public int reportLocalSnapshots;


    public Process () {
            
        //this.myHostName address.getHostName(); 
        this.neighbors = new String [5]; // initialize to five neighbors 
        this.received = false;  
        this.balance = 200; 
        this.reportLocalSnapshots = 0; 
        //System.out.println("Host name: " + myHostName); 
                   
        numProcesses = 5;
		localSent = new ArrayList[numProcesses];
		for(int i=0; i < localSent.length ;i++){
            localSent[i] = new ArrayList<Integer>();
      	}

		localReceived = new ArrayList[numProcesses];
		for(int i=0; i < localReceived.length ;i++){
            localReceived [i] = new ArrayList<Integer>();
      	} 

		globalSent = new ArrayList[numProcesses][numProcesses];
		for (int i=0; i < globalSent.length; i++) {
			for (int j=0; j < globalSent[0].length; j++) {
				globalSent[i][j] = new ArrayList<Integer>();
			}
		}
		
		globalReceived = new ArrayList[numProcesses][numProcesses]; 
		for (int i=0; i < globalReceived.length; i++) {
			for (int j=0; j < globalReceived[0].length; j++) {
				globalReceived[i][j] = new ArrayList<Integer>();
			}
		}

		processBalances = new String[numProcesses];
		for (int i=0; i < processBalances.length; i++) {
			processBalances[i] = new String();
		}
	}

	//getters and setters 
	public void setColor(int newColor) {
		this.color = newColor;
	}

	public int getColor() {
		return this.color; 
	}
	
	
	public void getHostNames() throws RemoteException {
	
			Scanner sc = new Scanner(System.in); //Could change sample.txt to System.in
			 	
			// User enters five hostnames
			for (int i=0; i < 5; i++) {
			 	System.out.println("Please enter hostname " + i); 
				String host = sc.nextLine();  
                neighbors[i] = host; 
                
           } 
           
	}

	public ProcessServer getInstance(String hostname) throws RemoteException{
		try {
			Registry r = LocateRegistry.getRegistry(hostname);
			ProcessServer ps = (ProcessServer) r.lookup("ProcessServer");
			
			return ps; 
		} catch (Exception e) {
		  System.err.println("Exception: " + e.toString());
          e.printStackTrace();

          	return null; 
		} //end try-catch

	}
	
	
	public void sendHostNames ()  throws RemoteException{
		for (String n : neighbors) {
			getInstance(n).setNeighbors(neighbors); 
			getInstance(n).setID();
		}
	} //end sendHostNames 
	
	public void setNeighbors(String [] n)  throws RemoteException{
     	this.neighbors = n;
	}
	
	
	public void alertToStart ()  throws RemoteException{
		for (String n: neighbors) {
			getInstance(n).setReceived();
		}

	} //end alertToStart
	
	//acknowledges that it knows all its peers 
	public void setReceived ()  throws RemoteException{
		this.received = true;
	} //end setReceived 
	
	//this method MUST be invoked after the registries but before alert to start
	public void setID() {
		for (int i = 0;i < numProcesses; i++) {
			boolean nameFound = neighbors[i].equals(myHostName); 
			if (nameFound) {
				this.id = i; 
				return; //exit once found
			}
		}
	} //end setID


	public void transfer (int recipient, int amount, int color) throws RemoteException{
		        
		if (received == true) {

			if ((this.color == 0) && (color == 1)) {
			// white process sends red message

			} else if ((this.color == 1) && (color == 0)) {
			// red process sends white message

			} else if ((this.color == 1) && (color == 1)) {
			// red process sends red message

			}

			System.out.println("Process " + this.id + "is about to transfer $" + amount + " to process " + recipient);

			this.balance -= amount; // update local balance
			if ((this.color == 0) && (color == 0)) {
			// white process sends white message
				ArrayList<Integer> a1 = localSent[recipient];
				a1.add(amount);
				localSent[recipient] = a1; 

			}

			getInstance(neighbors[recipient]).deposit(this.id, amount, color);
		}
	} //end transfer method 


	public void setLeader(int n)  throws RemoteException{
		this.leader = n;
	}

	public void setLeaders(int n)  throws RemoteException{
     	for(String neighbor: neighbors){
     		getInstance(neighbor).setLeader(n); 
     	}
	}

	
    public void deposit (int sender, int amount, int color) throws RemoteException{
	
		if (received == true) {

			if ((this.color == 0) && (color == 1)) {
			// white process receives red messages
				System.out.println("Process " + this.id + "is about to record local snapshot");
				this.setColor(1); // change process to red
				this.localSnapshot = this.balance; 
				sendLocalInfo(); 
				this.setColor(0); // change process back to white
				
			} else if ((this.color == 1) && (color == 0)) {
			// red process receives white message

			} else if ((this.color == 1) && (color == 1)) {
			// red process receives red message
			}

			//delay message processing to simulate non-FIFO behavior 
			// delays between 0 and 10 seconds
			try { 
				Random r = new Random();
				Thread.sleep(r.nextInt(10001));
				this.balance += amount;
				System.out.println("Process " + this.id + "is receiving $" + amount + " from process " + sender);

				if ((this.color == 0) && (color == 0)) {
				//white process receives white message 
					ArrayList<Integer> a1 = localReceived[sender];
				    a1.add(amount);
					localReceived[sender] = a1; 

				} 

			} catch (Exception e) {
				System.err.println("Exception: " + e.toString());
          		e.printStackTrace();
			}
		}

	} //end deposit method 

	public void sendLocalInfo() throws RemoteException {
		// each process knows which process is the leader (int)
		// local information sent: localSent, localReceived, localSnapShot
		// neighbor array holds all other peers in system -- use to get instance of leader
      	//neighbors might end returning a string, so will have to look in registry 
		System.out.println("Sending local info...");
		String message = "Process " + this.id + " has balance: " +  this.balance; 
		this.processBalances[this.id] = message;
		ProcessServer l = getInstance(neighbors[this.leader]);
		l.receiveLocalInfo(this.localSent, this.localReceived, this.localSnapshot, this.id); 	
		System.out.println("Leader has received local info");
	}

	public void receiveLocalInfo(ArrayList<Integer>[] lSent, ArrayList<Integer>[] lReceived, double lSnapshot, int sender) throws RemoteException {

		this.globalSent[sender] = lSent;
		// update global sent messages with information sent by sender of local sent messages
		this.globalReceived[sender] = lReceived;

		/*String message = "Sender " + sender + " balance: " +  lSnapshot; 
		this.processBalances[sender] = message; */
      
		System.out.println("Initiator has received local info from sender " + sender);
		//THIS IS THE WAY WE ARE CHECKING TO SEE THAT OUR 
		//GLOBAL SNAPSHOT IS FINISHED
		this.reportLocalSnapshots++;
		if (reportLocalSnapshots == numProcesses) {
			this.reportLocalSnapshots = 0; 
			this.globalSnapshotToFile();
		}
	}

	public ArrayList<Integer> transit(int sender, int recipient) throws RemoteException {
		
		ArrayList<Integer> messagesSent = globalSent[sender][recipient]; 
		
		ArrayList<Integer> messagesReceived = globalReceived[recipient][sender]; 

		for(Integer i : messagesSent) {
			if (messagesReceived.contains(i)) messagesReceived.remove(i);
		}

		return messagesReceived;
	}

	// global snapshot initiated by initiator process (sends red message)
	public void initiateGlobalSnapshot() throws RemoteException {
		//for each neighbor, peer in the system
		for (int i = 0; i < numProcesses; i++) {
			transfer(i,0,1);//sends red message (int color = 1) with no amount (second paramater = 0)
		}
	}

	Timer timer = new Timer();

	class sendMoney extends TimerTask {

		int balance;
		int id;
		int color;
		Process p;

		public sendMoney(int balance, int id, int color, Process p) {
			this.balance = balance;
			this.id = id;
			this.color = color;
			this.p = p;
		}
		
		public void run() {

			Random rand = new Random();

			// M is a random number between 1 and local balance of i
			int M = rand.nextInt(this.balance)+1;

			// P is a random number between 1 and 4, not including i
			// mapping of IP address --> number (use index in ArrayList<InetAddress> addresses)
				
			int P = -1; 
					
			while ((P == -1) || (P == this.id)){
				P = rand.nextInt(4)+1;
			}

			// After R milliseconds, transfer $M to process P
			try { 
				//Thread.sleep(R); -- now handled with timer
				p.transfer(P, M, this.color);
			} catch (Exception e) {
				System.err.println("Exception: " + e.toString());
		    	e.printStackTrace();
			}
				
		} // end of run()
	
	} //end of sendMoney class


	public void start() {
		Random rand = new Random();
		// R is a random number --> wait between 0 and 5 seconds
		// nextInt(n) generates random number between 0 (inclusive) and n (exclusive)
		int R = rand.nextInt(5001);
		sendMoney sM = new sendMoney(balance, id, color, this);
		this.timer.schedule(sM, R);

	}


	public File globalSnapshotToFile(){
		//PRINT ALL PROCESS BALANCES 

		try {
			File file = new File("global-snapshot.txt");
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			writer.println("Process balances:");
			for (int i = 0; i < processBalances.length; i++) {
				writer.println(processBalances[i]);
			}
			writer.println("Channels:");
			for (int j = 0; j < numProcesses; j++) {
				for (int k = 0; k < numProcesses; k++) {
					if (j!=k) {
						ArrayList<Integer> messagesOnChannel = transit(j,k);
						writer.println("Channel " + j + " to channel " + k + ": " + Arrays.toString(messagesOnChannel.toArray()));
					}
				}
			}
			writer.close();

			System.out.println("Global-snapshot.txt should be ready.");
			return file;
		} catch (Exception e) {
			System.err.println("Exception: " + e.toString());
		    e.printStackTrace();
		    return null;
		}
		

	}

	
	public static void main (String [] args)  throws RemoteException{

		/*
			1. Last process will set up registries by a terminal command 
            2. Once that is complete. Each process declares itself as leader.
            3. Leader invokes first sendMarker 
        */
        
         //export the process to the server
         
        Process obj = new Process();

        try {
			ProcessServer stub = (ProcessServer) UnicastRemoteObject.exportObject(obj, 0);
			//register the process in its local registry 
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("ProcessServer", stub);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }


        Scanner scan = new Scanner (System.in); 
        System.out.println("Are you the last computer started? If so, enter \"yes\""+ "\n"
        	               + "Otherwise type something other than \"yes\".");
        String input = scan.next(); 
        if (input.equals("yes")){
        	 obj.getHostNames();
        	 System.out.println("Hostnames have been received");
        	 obj.sendHostNames();
        	 System.out.println("Hostnames have been sent.");
        	 obj.alertToStart(); 
        	 System.out.println("All neighbors have acknowledged that they know peers.");
        	 Random r = new Random();
        	 // generate an int betweeen 0 (inclusive) and 5 (exclusive)
        	 int leader = r.nextInt(5);	
        	 obj.setLeaders(leader);
        	 System.out.println("Everyone knows that process " + leader + " is the leader.");
        	 obj.start(); 
			 obj.getInstance(obj.neighbors[obj.leader]).initiateGlobalSnapshot(); 

        } else {
        	 obj.start(); 


        }
        
		// DONE: print global snapshot to file
		// TODO: test on instances
		// DONE: example with 3 processes
		// DONE: timer
		// DONE: make non FIFO

	} //end main method 
		

}
