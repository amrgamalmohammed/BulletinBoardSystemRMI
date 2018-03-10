import java.io.*;
import java.net.Socket;
import java.util.Random;

public class BoardServer implements Runnable{

    private BulletInBoard board;
    private Log log;
    private String message, response;

    public BoardServer(BulletInBoard board, Log log, String message) {

        this.board = board;
        this.log = log;
        this.message = message;
    }

    @Override
    public void run() {

        try {

            //read message from client
            String[] segments = message.split("\t\t");
            String type = segments[0];
            String ID = segments[1];

            //assign received sequence number
            int rSeq = board.getrSeq();

            int rNum = -1;
            //if reader get and increment number of readers
            if (type.equalsIgnoreCase("read")) {
                rNum = board.getrNum();
            }

            //simulate delay
            Thread.sleep(getRandomSec());

            //assign served sequence number
            int sSeq = board.getsSeq();

            switch (type) {
                case "read":
                    board.decrNum();
                    break;
                case "write":
                    board.setoVal(Integer.parseInt(ID));
                    break;
                default:
                    //generate error
            }

            int oVal = board.getoVal();

            //respond to client
            StringBuilder stringBuilder = new StringBuilder( String.valueOf(oVal) + "\t\t" +
                                    String.valueOf(sSeq) + "\t\t" +
                                    String.valueOf(rSeq));
            this.response = stringBuilder.toString();

            log.write(sSeq, oVal, rNum, ID, type);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private long getRandomSec() {

        Random r = new Random();
        int Low = 0;
        int High = 10000;
        return r.nextInt(High - Low) + Low;
    }

    public String getResponse() {

        return this.response;
    }
}
