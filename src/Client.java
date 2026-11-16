import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;



public class Client  implements Runnable {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;

    public static void main(String[] args) {
        new Thread(new Client()).start();
    }

    @Override
    public void run() {
        try {
            client = new Socket("127.0.0.1", 9999);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            Thread t = new Thread(new InputHandler());
            t.start();

            String inMessage;
            while ((inMessage = in.readLine()) != null) {
                System.out.println(inMessage);
            }
        } catch (IOException e) {
            shutdown();
        } finally {
            shutdown();
        }
    }


    public void shutdown() {
        done = true;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (client != null && !client.isClosed()) client.close();
        } catch (IOException ignored) {
        }
    }

    class InputHandler implements Runnable {

        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while (!done) {
                    String message = inReader.readLine();
                    if (message == null) {
                        shutdown();
                        break;
                    }
                    out.println(message);
                    if (message.equals("/quit")) {
                        shutdown();
                        break;
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }
    }
}
