import java.rmi.Remote;
import java.rmi.RemoteException;

public interface INews extends Remote {

    String access(String message) throws RemoteException;
}
