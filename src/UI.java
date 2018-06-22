import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UI extends JFrame {
    public JTextArea getMessageBox() {
        return messageBox;
    }

    public void setMessageBox(JTextArea messageBox) {
        this.messageBox = messageBox;
    }

    public JTextField getInputBox() {
        return inputBox;
    }

    public void setInputBox(JTextField inputBox) {
        this.inputBox = inputBox;
    }

    private JTextArea messageBox;
    private JTextArea noticeBox;
    private JTextField inputBox;
    private JButton sendButton;
    private ListModel listModel;
    private JList jList;
    private DefaultListModel<String> onlines;
    private volatile boolean stop;
    private volatile boolean sendFlag;

    public boolean isSendFlag() {
        return sendFlag;
    }

    public void setSendFlag(boolean sendFlag) {
        this.sendFlag = sendFlag;
    }

    public void ShowUI() {
        setSize(800, 600);

        messageBox = new JTextArea();
        messageBox.setEditable(false);
        getContentPane().add(new JScrollPane(messageBox), BorderLayout.CENTER);

        onlines = new DefaultListModel<>();
        JList<String> list = new JList<>(onlines);
        JScrollPane jsp = new JScrollPane(list);
        jsp.setBorder(new TitledBorder("当前在线"));
        jsp.setPreferredSize(new Dimension(100, getContentPane().getHeight()));
        getContentPane().add(jsp, BorderLayout.EAST);

        inputBox = new JTextField(30);
        sendButton = new JButton("send");
        JPanel panel = new JPanel();
        panel.add(inputBox);
        panel.add(sendButton);
        getContentPane().add(panel, BorderLayout.SOUTH);

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    sendFlag=true;
            }
        });
    }
}
