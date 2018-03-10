import java.rmi.AccessException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class News implements INews {

    private BulletInBoard board;
    private Log log;
    private int maxItrs;
    private Registry locateRegistry, registry;
    private int rmiRegistry;

    public News (BulletInBoard board, Log log, int maxItrs, int rmiRegistry, Registry locateRegistry, Registry registry) {

        this.board = board;
        this.log = log;
        this.maxItrs = maxItrs;
        this.rmiRegistry = rmiRegistry;
        this.locateRegistry = locateRegistry;
        this.registry = registry;
    }

    @Override
    public String access(String message) throws RemoteException {

        BoardServer boardServer = new BoardServer(this.board, this.log, message);
        Thread serverThread = new Thread(boardServer);
        serverThread.start();

        try {
            serverThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if ((--maxItrs) == 0) {
            //served all requests, terminate
            new Thread(new Termination(this)).start();
        }

        return boardServer.getResponse();
    }

    private class Termination implements  Runnable{

        News news;

        Termination (News news) {
            this.news = news;
        }

        @Override
        public void run() {

            try {
                Thread.sleep(5000);
                registry.unbind(String.valueOf(rmiRegistry));
                UnicastRemoteObject.unexportObject(locateRegistry, true);
                UnicastRemoteObject.unexportObject(news, true);
                System.exit(0);
            } catch (NoSuchObjectException e) {
                e.printStackTrace();
            } catch (AccessException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
