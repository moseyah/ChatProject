import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Receive implements Runnable{
    private DataInputStream dis;
    private UI ui;
    private volatile boolean isRunning = true;

    public Receive(UI ui, Socket client) {
        this.ui = ui;
        try {
            dis = new DataInputStream(client.getInputStream());
        } catch (IOException e) {
            isRunning = false;
            CloseUtil.closeAll(dis);
        }
    }

    public void receive() {
        try {
            String msg=dis.readUTF();
            ui.getMessageBox().append(msg + "\n");
        } catch (IOException e) {
            isRunning = false;
            CloseUtil.closeAll(dis);
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            receive();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
