import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {

    private int maxItrs, rmiRegistry;
    private String serverIP;
    private Thread runningThread;
    private BulletInBoard board;
    private Log log;
    private News news;

    private Server(String serverIP, int maxItrs, int rmiRegistry) {

        this.serverIP = serverIP;
        this.maxItrs = maxItrs;
        this.rmiRegistry = rmiRegistry;
    }

    public static void main(String[] args) {

        //parsing arguments
        String serverIP = args[0];
        int maxItrs = Integer.parseInt(args[1]);
        int rmiRegistry = Integer.parseInt(args[2]);

        Server server = new Server(serverIP, maxItrs, rmiRegistry);

        System.out.println("Starting server ...");

        server.run();
    }

    public void run() {

        //create main thread
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }

        this.board = new BulletInBoard();
        this.log = new Log();

        Registry locateRegistry = null;
        Registry registry = null;

        //create server
        try {
            System.setProperty("java.rmi.server.hostname",serverIP);
            locateRegistry = LocateRegistry.createRegistry(rmiRegistry);
            registry = LocateRegistry.getRegistry(serverIP, rmiRegistry);
            news = new News(board, log, maxItrs, rmiRegistry, locateRegistry, registry);
            INews stub =
                    (INews) UnicastRemoteObject.exportObject(news, rmiRegistry);
            registry.rebind(String.valueOf(rmiRegistry), stub);
            System.out.println("server bound");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
