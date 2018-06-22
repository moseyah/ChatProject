import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Send implements Runnable{
    private String info;
    private String name;
    private DataOutputStream dos;
    private UI ui;
    private volatile boolean isRunning = true;

    public Send(UI ui) {
        this.ui = ui;
        info=ui.getInputBox().getText();
    }

    public Send(UI ui, Socket client, String name) {
        this.ui = ui;
        try {
            dos = new DataOutputStream(client.getOutputStream());
            this.name = name;
            ui.setSendFlag(true);
            send(this.name);
            ui.setSendFlag(false);
        } catch (IOException e) {
            isRunning = false;
            CloseUtil.closeAll(dos);
        }

    }

    public void send(String info) {
        if (info != null && !info.equals("") && ui.isSendFlag()) {
            try {
                dos.writeUTF(info);
                if (info != this.name) {
                    long time = System.currentTimeMillis();
                    Date date = new Date(time);
                    SimpleDateFormat sdf = new SimpleDateFormat(" MM-dd HH:mm:ss");
                    ui.getMessageBox().append(this.name + sdf.format(date) + "\n" + info + "\n\n");
                }
                dos.flush();
                ui.setSendFlag(false);
                ui.getInputBox().setText("");
            } catch (IOException e) {
                isRunning = false;
                CloseUtil.closeAll(dos);
            }
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            send(ui.getInputBox().getText());
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
