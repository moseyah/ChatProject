import javax.swing.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class UpdateList implements Runnable{
    private DataInputStream dis;
    private UI ui;
    private JList jList;
    private Socket client;
    private DefaultListModel<String> onLines;
    private volatile boolean isRunning = true;

    public UpdateList(UI ui, Socket client) {
        this.ui = ui;
        this.client=client;
        this.onLines = ui.getOnlines();
        this.jList=ui.getjList();
        try {
            dis = new DataInputStream(client.getInputStream());
        } catch (IOException e) {
            isRunning = false;
            CloseUtil.closeAll(dis);
        }
    }

    public void update() {
        String msg= null;
        try {
            msg = dis.readUTF();
            System.out.println("asd");
            if (msg.contains("^name&")) {
                String name = msg.substring(msg.indexOf("&") + 1);
                ui.getOnlines().addElement(name);
                ui.getjList().setModel(ui.getOnlines());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void run() {
        while (isRunning) {
            update();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
