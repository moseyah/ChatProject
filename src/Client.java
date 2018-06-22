import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private UI ui;

    public static void main(String[] args) {
        new Client().showUI();
    }

    public void showUI() {
        String inputName = JOptionPane.showInputDialog("请输入昵称：");
        if (inputName.equals("")) {
            return;
        }
        UI ui = new UI();
        this.ui = ui;
        ui.setTitle("用户" + inputName + "的客户端");
        ui.ShowUI();

        try {
            Socket client = new Socket("localhost", 2345);
            new Thread(new Send(ui, client, inputName)).start();
            new Thread(new Receive(ui, client)).start();
            new Thread(new UpdateList(ui,client)).start();

        } catch (IOException e) {
            ui.getMessageBox().append("服务器未上线！");
        }
    }
}
