import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class Reader {

    public static void main(String[] args) {


        String serverIP = args[0];
        String ID = args[1];
        String maxNumAcc = args[2];
        String rmiRegistry = args[3];

        try {
            Registry registry = LocateRegistry.getRegistry(serverIP, Integer.parseInt(rmiRegistry));
            read(ID, maxNumAcc, registry, rmiRegistry);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return;
    }

    private static void read(String ID, String maxNumAcc, Registry registry, String rmiRegistry) {

        int repeats = Integer.parseInt(maxNumAcc);
        PrintWriter log = null;
        String rSeq, sSeq, oVal;
        String serverName = rmiRegistry;

        try {
            log = new PrintWriter("log"+ID+".txt", "UTF-8");
            log.println("Client type: Reader");
            log.println("Client Name: " + ID);
            log.println("rSeq"+"\t\t"+"sSeq"+"\t\t"+"oVal");
            log.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {

            for (int i = 0; i < repeats; i++) {

                System.out.println("Reader ...");
                //send read request
                System.out.println("Sending read request ...");
                INews client = (INews) registry.lookup(serverName);

                String response = client.access("read"+"\t\t"+ID);

                //receive read data
                String[] segments = response.split("\t\t");

                oVal = segments[0];
                sSeq = segments[1];
                rSeq = segments[2];

                //writing logs
                log.println(rSeq+"\t\t"+sSeq+"\t\t"+oVal);
                log.flush();

                //sleep between operations
                try {
                    Thread.sleep(getRandomSec());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

        System.out.println("Finished reader " + ID + " ...");
        log.close();
    }

    private synchronized static long getRandomSec() {

        Random r = new Random();
        int Low = 0;
        int High = 10000;
        return r.nextInt(High - Low) + Low;
    }
}
