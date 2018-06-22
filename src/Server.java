import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Server {
    private List<MyChannel> all = new ArrayList<MyChannel>();

    public static void main(String[] args) throws IOException {
        new Server().start();
    }

    private void start() throws IOException {
        ServerSocket server = new ServerSocket(2345);
        UI ui = new UI();
        ui.setTitle("服务器");
        ui.ShowUI();

        while (true) {
            Socket client = server.accept();
//            ui.getMessageBox().append("客户端连接成功！\n");
//            String msg = "welcome";
            new Thread(new ServerSend(client, ui)).start();
            MyChannel channel = new MyChannel(client, ui);
            all.add(channel);
            new Thread(channel).start();
        }

    }

    private class ServerSend implements Runnable {
        private DataOutputStream dos;
        private DataInputStream dis;
        private UI ui;
        private volatile boolean isRunning = true;

        public ServerSend(Socket client, UI ui) {
            this.ui = ui;
            try {
                dos=new DataOutputStream(client.getOutputStream());
            } catch (IOException e) {
                isRunning = false;
                CloseUtil.closeAll(dis, dos);
            }

        }

        public void serverSend(String info) {
            if (info != null && !info.equals("") && ui.isSendFlag()) {
                try {
                    long time = System.currentTimeMillis();
                    Date date = new Date(time);
                    SimpleDateFormat sdf = new SimpleDateFormat(" MM-dd HH:mm:ss");
                    dos.writeUTF("来自服务器消息：" + sdf.format(date) + "\n" + info + "\n\n");
                    ui.getMessageBox().append("服务器本地消息 "+ sdf.format(date) + "\n" + info + "\n\n");
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
                serverSend(ui.getInputBox().getText());
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private class MyChannel implements Runnable {
        private DataOutputStream dos;
        private DataInputStream dis;
        private UI ui;
        private String name;
        private volatile boolean isRunning = true;
        private DefaultListModel<String> onLines;

        public MyChannel(Socket client,UI ui) {
            this.ui = ui;
            try {
                dis=new DataInputStream(client.getInputStream());
                dos=new DataOutputStream(client.getOutputStream());

                this.name = dis.readUTF();
                ui.getMessageBox().append("通知：" + this.name + "上线了。\n\n");
                this.send("欢迎进入聊天室！\n");
                sendOthers(this.name + "上线了。\n",true);
                ui.getOnlines().addElement(this.name);

            } catch (IOException e) {
                isRunning = false;
                CloseUtil.closeAll(dis, dos);
            }
        }

        private String receive() {
            try {
                String msg=dis.readUTF();
                long time = System.currentTimeMillis();
                Date date = new Date(time);
                SimpleDateFormat sdf = new SimpleDateFormat(" MM-dd HH:mm:ss");

                ui.getMessageBox().append(this.name + sdf.format(date) + "\n" + msg + "\n\n");
                return msg;
            } catch (IOException e) {
                isRunning = false;
                CloseUtil.closeAll(dis);
                sendOthers(this.name+"下线了。\n",true);
                ui.getMessageBox().append("通知：" + this.name + "下线了。\n\n");
                ui.getOnlines().removeElement(this.name);
                all.remove(this);
            }
            return "";
        }

        private void sendOthers(String msg,boolean sys) {
//            if (msg.startsWith("@")&&msg.contains(":")) {
//                String name = msg.substring(1, msg.indexOf(":"));
//                String content = msg.substring(msg.indexOf(":") + 1);
//                for (MyChannel others :
//                        all) {
//                    if (others.name == name) {
//                        others.send(this.name + "对你悄悄地说:\n" + content);
//                    }
//                }
//            }
//            else {
                for (MyChannel others :
                    all) {
                    if (others == this) {
                        continue;
                    }
                    else {

                        if (!sys && !msg.equals("") && msg != null) {
                            long time = System.currentTimeMillis();
                            Date date = new Date(time);
                            SimpleDateFormat sdf = new SimpleDateFormat(" MM-dd HH:mm:ss");

                            others.send(this.name + sdf.format(date) + "\n" + msg + "\n");
                        } else if (sys && !msg.equals("") && msg != null) {
                            others.send("系统信息：" + msg);
                        }
                    }
                }
//            }
        }

        private void updateList() {
            for (MyChannel others :
                    all) {
                others.send("^name&" + others.name);

            }
        }

        private void send(String msg) {
            if (msg == null || msg.equals(""))
                return;
                try {
                    dos.writeUTF(msg);
                    dos.flush();
                } catch (IOException e) {
                    isRunning = false;
                    CloseUtil.closeAll(dos);
                    all.remove(this);
                }
        }

        @Override
        public void run() {
            while (isRunning) {
                sendOthers(this.receive(),false);
                updateList();
            }

        }
    }


}
