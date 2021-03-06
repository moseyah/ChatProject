import javax.swing.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Receive implements Runnable{
    private DataInputStream dis;
    private UI ui;
    private JList jList=new JList();
    private Socket client;
    private DefaultListModel<String> onLines = new DefaultListModel<>();
    private volatile boolean isRunning = true;

    public Receive(UI ui, Socket client) {
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

    public void receive() {
        try {
            String msg=dis.readUTF();
            ui.getMessageBox().append(msg + "\n");
//            if (msg.contains("^name&")) {
//                String name = msg.substring(msg.indexOf("&") + 1);
//                this.onLines.addElement(name);
//            }
        } catch (IOException e) {
            isRunning = false;
            CloseUtil.closeAll(dis);
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            receive();
//            this.jList.setModel(onLines);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
