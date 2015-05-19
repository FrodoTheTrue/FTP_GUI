import java.awt.Dimension;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.io.Closeable;
import javax.swing.DefaultListModel;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class HelloWorld extends JFrame
{
public static String lwd = "/Users/Fedor/Documents/workspace/ftp/src/ftp/client/";
public static DataInputStream Reader;
public static DataOutputStream Writer;
public static String get_client_ls()
{
  String result = "";
      File fUserHome = new File(lwd);
      File [] files = fUserHome.listFiles();
      for (int iIdx = 0; iIdx < files.length; iIdx++)
      {
        File file = files[iIdx];
        String strMsg = file.toString();
        int indsl = 0;
        for (int i=strMsg.length() - 1;i>=0;i--)
        {
          if (strMsg.charAt(i) == '/')
          {
          indsl = i;
          break;
          }
        }
        strMsg = strMsg.substring(indsl + 1, strMsg.length()) + "\n";
        result += strMsg;
      }
      return result;
}
public static String changeDir(String file)
{
  if (file.equals(".."))
                  {
  String now_wd = "";
      int indsl = 0;
      boolean first = true;
      for (int i=lwd.length() - 1;i>=0;i--)
      {
        if (lwd.charAt(i) == '/')
        {
          if (first)
          {
            first = false;
          }
          else
          {
            indsl = i;
            break;
          }
        }
      }
      for (int i=0;i<indsl;i++)
      {
        now_wd += lwd.charAt(i);
      }
      lwd = now_wd + "/";
      return lwd;
    }
  else
  {
    File now_wd = new File(lwd + file);
    if (now_wd.isDirectory())
    {
      lwd = lwd + file + "/";
      return lwd;
    }
    else 
    {
      return lwd;
    }
  }
}
public static String[] getls(String ls)
{
  int ls_dl = 0;
    for (int i=0;i<ls.length();i++)
    {
      if (ls.charAt(i) == '\n')
      {
        ls_dl += 1;
      }
    }
    String[] data = new String[ls_dl + 1];
    int count = 0;
    data[count] = "..";
    count += 1;
    String now_word = "";
    for (int i=0;i<ls.length();i++)
    {
      if (ls.charAt(i) == '\n')
      {
        data[count] = now_word;
        now_word = "";
        count += 1;
      }
      else now_word += ls.charAt(i);
    }
    return data;
}

public static String[] getls_server(String ls)
{
  if (ls.equals("\n"))
  {
    String[] data = new String[1];
    int count = 0;
    data[count] = "..";
    count += 1;
    return data;
  }
  else
  {
  int ls_dl = 0;
    for (int i=0;i<ls.length();i++)
    {
      if (ls.charAt(i) == '\n' || i == ls.length() - 1)
      {
        ls_dl += 1;
      }
    }
    String[] data = new String[ls_dl + 1];
    int count = 0;
    data[count] = "..";
    count += 1;
    String now_word = "";
    for (int i=0;i<ls.length();i++)
    {
      if (ls.charAt(i) == '\n' || i == ls.length() - 1)
      {
        data[count] = now_word;
        if (i == ls.length() - 1)
          data[count] += ls.charAt(i);
        now_word = "";
        count += 1;
      }
      else now_word += ls.charAt(i);
    }
    return data;
  }
}

static JList<String> left_list;
static JList<String> right_list;
static JScrollPane right_list_scroll;
static JScrollPane left_list_scroll;
static String [] client_ls;
static String command = "";

public HelloWorld()
   {
      super();
      this.setSize(625, 550);
      this.getContentPane().setLayout(null);
      //Server connection
      try
      {
      InetAddress address = InetAddress.getByAddress(new byte [] {127,0,0,1});
    Socket socket = new Socket(address, 40000);
    Reader = new DataInputStream(socket.getInputStream());
    Writer = new DataOutputStream(socket.getOutputStream());
    //JList<String> left_list;
    //JList<String> right_list;

    Writer.writeUTF("pwd");
    String wd = Reader.readUTF();
    int sln = wd.indexOf("/server");
    wd = wd.substring(sln + 7);

    Writer.writeUTF("ls");
    String ls = Reader.readUTF();
    String [] server_ls = getls_server(ls);
    System.out.println(server_ls);


    String result = get_client_ls();
      client_ls = getls(result);



      JLabel ClientText = new JLabel();
      ClientText.setBounds(130, 5, 100, 50);
      ClientText.setText("Client");

      JLabel ServerText = new JLabel();
      ServerText.setBounds(450, 5, 100, 50);
      ServerText.setText("Server");

      JButton sendButton = new JButton();
      sendButton.setBounds(130, 475, 50, 30);
      sendButton.setText(">>");
      ActionListener actionListener = new PushSenderListener();
      sendButton.addActionListener(actionListener);

      JButton getButton = new JButton();
      getButton.setBounds(450, 475, 50, 30);
      getButton.setText("<<");
      ActionListener actionGetter = new PushGetterListener();
      getButton.addActionListener(actionGetter);



      left_list = new JList<String>(new DefaultListModel<String>());
      for (int i=0;i<client_ls.length;i++)
        ((DefaultListModel)left_list.getModel()).addElement(client_ls[i]);
      left_list.setLayoutOrientation(JList.VERTICAL);
      left_list.setVisibleRowCount(5);

      right_list = new JList<String>(new DefaultListModel<String>());
      for (int i=0;i<server_ls.length;i++)
        ((DefaultListModel)right_list.getModel()).addElement(server_ls[i]);
      right_list.setLayoutOrientation(JList.VERTICAL);
      right_list.setVisibleRowCount(5);

      JScrollPane left_list_scroll = new JScrollPane(left_list);
      left_list_scroll.setMaximumSize(new Dimension(310, 200));
      left_list_scroll.setBounds(10, 100, 300, 350);

      right_list_scroll = new JScrollPane(right_list);
      right_list_scroll.setMaximumSize(new Dimension(200, 200));
      right_list_scroll.setBounds(320, 100, 300, 350);

      JTextField leftPath = new JTextField();
      leftPath.setBounds(10, 50, 300, 25);
      leftPath.setText(lwd);

      JTextField rightPath = new JTextField();
      rightPath.setBounds(320, 50, 300, 25);
      rightPath.setText(wd);

      this.add(ClientText, null);
      this.add(ServerText, null);
      this.add(left_list_scroll, null);
      this.add(right_list_scroll, null);
      this.add(sendButton, null);
      this.add(getButton, null);
      this.add(leftPath, null);
      this.add(rightPath, null);
      this.setTitle("FTP Client");
      left_list.addMouseListener(new MouseAdapter() 
      {
      public void mouseClicked(MouseEvent e) 
      {
        if (e.getClickCount() > 1) 
          {
              Object[] arrayList = left_list.getSelectedValues();
        for(int i=0;i<arrayList.length;i++)
        {
          String select = (String)arrayList[i];
          lwd = changeDir(select);
                leftPath.setText(lwd);
              }
              String result_after = get_client_ls();
              String[] new_client_ls = getls(result_after);
              for (int j = 0; j<client_ls.length;j++)
              {
                ((DefaultListModel)left_list.getModel()).removeAllElements();
              }
              for (int j = 0; j<new_client_ls.length;j++)
              {
                ((DefaultListModel)left_list.getModel()).addElement(new_client_ls[j]);
              }
            left_list_scroll.updateUI();
          }
        }
    });




    right_list.addMouseListener(new MouseAdapter() 
      {
      public void mouseClicked(MouseEvent e) 
      {
        if (e.getClickCount() > 1) 
          {
          try
          {
              Object[] arrayList = right_list.getSelectedValues();
              String temp_ans = "";
              for(int i=0;i<arrayList.length;i++)
              {
                 Writer.writeUTF("cd " + (String)arrayList[i]);
                 temp_ans = Reader.readUTF();
              }
                if (temp_ans.equals("Done"))
                {
                  Writer.writeUTF("pwd");
                  String wd = Reader.readUTF();
                  int sln = wd.indexOf("/server");
                  wd = wd.substring(sln + 7);
                  rightPath.setText(wd);

                  Writer.writeUTF("ls");
                  String new_ls = Reader.readUTF();
                  String [] new_server_ls = getls_server(new_ls);
                  for (int j = 0; j<new_server_ls.length;j++)
                  {
                    ((DefaultListModel)right_list.getModel()).removeAllElements();
                  }
                  for (int j = 0; j<new_server_ls.length;j++)
                  {
                    ((DefaultListModel)right_list.getModel()).addElement(new_server_ls[j]);
                  }
                right_list_scroll.updateUI();
              }
          }
          catch (Exception ex)
            {
                ex.printStackTrace();
            }
          }
        }
    });


    }
    catch (Exception ex)
        {
            ex.printStackTrace();
        }
   }
private static class PushSenderListener implements ActionListener 
{
   public void actionPerformed(ActionEvent e) 
   {
     Object[] arrayList = left_list.getSelectedValues();
     String select = "";
       for(int i=0;i<arrayList.length;i++)
       {
         select = (String)arrayList[i];
       }
       String file_on_client = select;
       File myfile = new File(lwd + file_on_client);
       if (myfile.exists())
       {
         try
         {
           InputStream fStream = new FileInputStream(lwd + file_on_client);
           byte[] mybytearray = new byte[(int)myfile.length()];
           int bytesRead = fStream.read(mybytearray, 0, mybytearray.length);
           String need_to_send = new String(mybytearray, "utf-8");
           Writer.writeUTF("put " + file_on_client);
           Writer.writeUTF(Integer.toString(bytesRead));
           Writer.writeUTF(new String(mybytearray));
           Writer.writeUTF("ls");
           String new_ls = Reader.readUTF();
           String [] new_server_ls = getls_server(new_ls);
           for (int j = 0; j<new_server_ls.length;j++)
           {
             ((DefaultListModel)right_list.getModel()).removeAllElements();
           }
           for (int j = 0; j<new_server_ls.length;j++)
           {
             ((DefaultListModel)right_list.getModel()).addElement(new_server_ls[j]);
           }
         right_list_scroll.updateUI(); 
       }
         catch (IOException e1)
         {
           
         }
       }
    }
}



private static class PushGetterListener implements ActionListener 
{
   public void actionPerformed(ActionEvent e) 
   {
    Object[] arrayList = right_list.getSelectedValues();
     String select = "";
       for(int i=0;i<arrayList.length;i++)
       {
         select = (String)arrayList[i];
       }
       String file_on_server = select;
     try
     {
      Writer.writeUTF("get " + file_on_server);
      int CountBytes = Integer.parseInt(Reader.readUTF());
      if (CountBytes == -1)
      {
      }
      else
      {
        System.out.println("here");
          byte[] buf = new byte[CountBytes + 2];
          Reader.readFully(buf);
          String need_to_write = new String(buf);
          String well = need_to_write.substring(2);
          String file_on_client = select;
        String created_file = file_on_client;
        String plus = "";
        boolean is_done = false;
        while(!is_done)
      {
        if ((new File(lwd + plus + created_file)).exists()) 
        {
          int need;
          if (plus.equals("")) need = 1;
          else
          {
            need = Integer.parseInt(plus);
            need += 1;
          }
            plus = String.valueOf(need);
        }
        else is_done = true;
      }
        FileOutputStream fos = new FileOutputStream(lwd + plus + created_file);
        fos.write(well.getBytes());
        fos.flush();
        fos.close();
        String result_after = get_client_ls();
        String[] new_client_ls = getls(result_after);
        for (int j = 0; j<client_ls.length;j++)
        {
          ((DefaultListModel)left_list.getModel()).removeAllElements();
        }
        for (int j = 0; j<new_client_ls.length;j++)
        {
          ((DefaultListModel)left_list.getModel()).addElement(new_client_ls[j]);
        }
      left_list_scroll.updateUI();
      }
    }
     catch (IOException e1)
     {
       
     }
   }
}



   public static void main(String[] args)
   {
      HelloWorld w = new HelloWorld();
      w.setVisible(true);
   }
}
