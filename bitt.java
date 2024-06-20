import javax.swing.*;   //For creating the GUI of the Chat App
import javax.swing.border.*;  //For customising borders
import java.awt.*;      //This package contains various colors
import java.awt.event.*; //To perform actions on certain clicks we have functions in this package
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;  //To add the real time on the messages
import java.util.Random;
import java.text.*;

//JFrame is a class of swing package also interface actionlister functions we will implement
//We are extending Jframe which means we can use properties of this class.

public class bitt
{
    JTextField s3;      //We are declaring it global to extend its scope till ActionPerformed function
    static JPanel s2;                                  //Also to use the textdisplayArea we extending its scope.
    static JPanel s1;
    static JLabel status;
    static Box vertical = Box.createVerticalBox();     //To adjust messgaes vertically on top of each other
    static JFrame f = new JFrame();
    static int ack;
    static JScrollPane pane;
    public DataOutputStream dou;
    static MessageCrypto mg = new MessageCrypto();
    static float keyC;
    
    public void chatApp(String username,int ack1,DataOutputStream dou1,float key)
    {
        keyC = key;
        ack = ack1;
        dou = dou1;
        f.setLayout(null);
        //If setLayout is set NULL, then we have to manually give coordinates of the panels where to set it on the Frame.

        s1 = new JPanel();   //To work on the frame like dividing it or apply function on specific areas of Frame
        s1.setBackground(new Color(7, 94, 84));   //we can use direct color also by "setBackground(Color.GREEN)"
        s1.setBounds(0, 0, 450, 60);  
        s1.setLayout(null); //we set null to manually set things on the panel like we did on the frame.
        f.add(s1);   //Add this Panel to the Frame

        //We used ImageIcon class to create a icon
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("cross.png"));
        //Use image class to scale the image to our required size,
        //also use Image.Scale_default to use default scaling method.
        Image i2 = i1.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT);
        //we again used Imageicon so we can pass that to JLabel class.(Image class object cannot be passed to Jlable)
        ImageIcon i3 = new ImageIcon(i2);
        JLabel back = new JLabel(i3); //using this class object we can now modify the image.
        back.setBounds(415, 19, 25, 25);   //define the position and size of the label
        s1.add(back);   //add the icon to the panel

        back.addMouseListener(new MouseAdapter() {  //a inner anonymous class is created which extend MouseAdapter Class.
            public void mouseClicked(MouseEvent ae)  //mouseCLicked function of MouseAdapter class is overwritten to perform on click action .
            {
                System.exit(0);  //or we can use Visible(false) too
            }
        });

        //Now we will add a photo of the user depending upon the Gender.
        ImageIcon i4 = new ImageIcon(ClassLoader.getSystemResource("man.png"));
        Image i5 = i4.getImage().getScaledInstance(45, 45, Image.SCALE_DEFAULT);
        ImageIcon i6 = new ImageIcon(i5);
        JLabel back2 = new JLabel(i6);
        back2.setBounds(10, 10, 45, 45);
        s1.add(back2);

        //Now we will add text using Jlabel
        JLabel name = new JLabel(username);         //Text cannot be added directly on the Panel.
        name.setForeground(Color.WHITE);
        name.setBounds(65, 16, 200, 24);
        name.setFont(new Font("San_Serif",Font.BOLD, 20));
        s1.add(name);

        //Now we will add the status of the User
        status = new JLabel("Active Now");
        status.setForeground(Color.WHITE);
        status.setBounds(65, 38, 100, 18);
        status.setFont(new Font("San_Serif",Font.PLAIN, 11));
        s1.add(status);

        //Initialize the text area
        s2 = new JPanel(); //a new panel is added on the frame
        s2.setBounds(0, 60, 450, 585);
        s2.setBackground(new Color(238, 238, 238));  //set background to off-white
        pane = new JScrollPane(s2, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setBounds(0, 60, 450, 585);
        pane.getVerticalScrollBar().setPreferredSize(new Dimension(0,0));
        f.add(pane);

        //We will set the text field where the messages will be entered
        s3 = new JTextField();  //JTextField class is used to place a textbox
        s3.setBounds(3, 645, 368, 55);
        s3.setFont(new Font("San_Serif",Font.PLAIN, 18));
        s3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent a)
            {
                sendMessage();
            }
        });
        f.add(s3);

        //Now we will place a SEND button right of textfield
        JButton s4 = new JButton("Send");  //Jbutton is also a component that performs certain actions.
        s4.setBounds(370, 645, 80, 54);
        s4.setForeground(Color.WHITE);
        s4.setBackground(new Color(7, 94, 84));
        s4.setFont(new Font("San_Serif",Font.PLAIN, 15));
        s4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                sendMessage();
            }
        });
        f.add(s4);

        f.setSize(450, 700);  //Size of the frame is setted
        f.setLocation(750, 50);        //Where the frame will appear, we can set that
        f.setUndecorated(true);
        f.getContentPane().setBackground(Color.WHITE);    //using white color from COLOR class; getContentPane to get the Frame
        f.setVisible(true);   //Making the frame appear else nothing will show up after compiling
    }

    public void sendMessage()
    {
        try
        {
        String m1 = s3.getText();         //We are extracting the message of the textfield s3
        m1 = mg.encrypt(m1,keyC);
        if(m1.length() >= 1)
        {
            JPanel pan1 = formatPanel(m1,1);

            s2.setLayout(new BorderLayout());   //We are changing the layout to BOrder(top,right,BL)
            JPanel right = new JPanel(new BorderLayout());      //Creating a panel than can be placed on the uppermost panel
            right.add(pan1,BorderLayout.LINE_END);          //we are adding the text embedded panel to the line_end(right)

            vertical.add(right);
            vertical.add(Box.createVerticalStrut(15));    //Creating a vertical distance of 15 between messages
            s2.add(vertical,BorderLayout.PAGE_START);           //Put the box at the start of the page
            
            dou.writeUTF(m1);
            dou.writeInt(mg.calculatedValues.length);
            for (int i = 0; i < mg.calculatedValues.length; i++) 
                dou.writeFloat(mg.calculatedValues[i]);
            s3.setText("");         //TO empty the text area for next message

            Rectangle bottomRect = s2.getBounds();
            bottomRect.setLocation(0, bottomRect.y + bottomRect.height);
            pane.getViewport().scrollRectToVisible(bottomRect);

            f.repaint();              //To reload the Topmost frame for updated information
            f.revalidate();
            f.validate();
        }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }  

    public static JPanel formatPanel(String mess,int who) 
    //To customise the message we are creating a method that takes a String 
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS)); //create box layout object that puts components vertically
        JLabel mess1 = new JLabel(mess);
        mess1.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        if(who == 1)
            mess1.setBackground(new Color(194, 203, 147));
        else
            mess1.setBackground(new Color(244, 237, 218));
        mess1.setOpaque(true);
        mess1.setBorder(new EmptyBorder(10,15,10,20));
        panel.add(mess1);

        Calendar cal = Calendar.getInstance();                          //initialising a instance of CAlender   
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");   //changing the format of the time using sdf object
        JLabel time = new JLabel();
        time.setText(sdf.format(cal.getTime()));        //change the time accord. to the time and format as specified
        panel.add(time);

        return panel;
    }
    public static void main(String[] args) 
    {
        Authenticate a1 = new Authenticate();
        while(ack != 1)
        {
            System.out.println(ack);
        }
        System.out.println("Welcome");
        try
        {
            while (true)
            {                                           //to display and read messages from the input steam
                String message = a1.dio.readUTF();         //readUTP is a protocol that retrieve the messages
                int arrayLength = a1.dio.readInt();
                float[] floatArray = new float[arrayLength];        // Read the integers into an array
                for (int i = 0; i < arrayLength; i++) 
                    floatArray[i] = a1.dio.readFloat();
                message = mg.decrypt(floatArray,keyC);
                JPanel panel1 = formatPanel(message,0);
                s2.setLayout(new BorderLayout());
                JPanel left = new JPanel(new BorderLayout());
                left.add(panel1,BorderLayout.LINE_START);
                vertical.add(left);
                vertical.add(Box.createVerticalStrut(15));
                s2.add(vertical,BorderLayout.PAGE_START);

                Rectangle bottomRect = s2.getBounds();
                bottomRect.setLocation(0, bottomRect.y + bottomRect.height);
                pane.getViewport().scrollRectToVisible(bottomRect);

                f.validate();
            }
        } 
        catch(Exception e)
        {
            JLabel status1 = new JLabel("Offline");
            status1.setForeground(Color.WHITE);
            status1.setBounds(65, 38, 100, 18);
            status1.setFont(new Font("San_Serif",Font.PLAIN, 11));
            s1.remove(status);
            s1.add(status1);
            f.repaint();
            JOptionPane.showMessageDialog(f, "Server has gone offline..", "Connection Terminated",
                    JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }
}

class MessageCrypto
{
    float[] calculatedValues;
    int g = 2, n = 31271;

    public String encrypt(String sen,float key)
    {
        String encryptedMes;
        int[] asciiArray = new int[sen.length()];
        for (int i = 0; i < sen.length(); i++) 
        {
            asciiArray[i] = (int)sen.charAt(i);
        }
        calculatedValues = new float[asciiArray.length];
        for (int i = 0; i < asciiArray.length; i++) 
        {
            if (asciiArray[i] != 32) // Exclude spaces
            {
                calculatedValues[i] = key / (float)asciiArray[i];
                System.out.println(calculatedValues[i]);}
        }
        System.out.println("bitch");
        char enc[] = new char[calculatedValues.length];
        for(int i = 0; i<calculatedValues.length; i++)
        {
            if(calculatedValues[i] == 32)
                enc[i] = (' ');
            else
                enc[i] = (char)calculatedValues[i];
        }
        encryptedMes = new String(enc);
        return encryptedMes;
    }

    public String decrypt(float[] calValues,float key)
    {
        String decryMess;
        char[] mess = new char[calValues.length];
        for (int i = 0; i < calValues.length; i++) 
        {
            if (calValues[i] != 0) { // Exclude division by zero
                int asciiChar = (int) (key / calValues[i] + 0.5); // Adding 0.5 for rounding
                mess[i] = (char)asciiChar;
            } 
            else
                mess[i] = ' ';
        }
        decryMess = new String(mess);
        return decryMess;
    }

    public float keyExchange(DataInputStream din, DataOutputStream dio)
    {
        float sharedSecret;
        while (true) 
        {
            try 
            {
                Random r1 = new Random();
                int privateKey = r1.nextInt(n);
                int publicKey = modPow(g, privateKey, n);
                dio.writeInt(publicKey);
                int publicKey2 = din.readInt();
                sharedSecret = (float)modPow(publicKey2, privateKey, n);
                if(sharedSecret > 126.0)
                {
                    System.out.println(sharedSecret);
                    break;
                }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return sharedSecret;
    }

    private static int modPow(int base, int exponent, int modulus) {
        int result = 1;
        base = base % modulus;
        while (exponent > 0) {
            if (exponent % 2 == 1) {
                result = (result * base) % modulus;
            }
            exponent = exponent >> 1;
            base = (base * base) % modulus;
        }
        return result;
    }
}


class Authenticate extends JFrame
{
    String name;        //Store the username
    String password;    //store the password
    JFrame frame;     
    String portnum;  
    JTextField text1,text2,text3;
    static DataOutputStream dou;
    DataInputStream dio;
    //for deffie helman,choose g and n where g is primitive root of large prime number n.
    long g,n;        

    Authenticate()      //Constructor for automatic Login Page
    {
        frame = this;   //To create a reference variable of our JFrame
        //We are designing the authentication page similarly
        setLayout(null);
        JPanel p1 = new JPanel();
        p1.setBackground(Color.RED);
        p1.setBounds(0,0,550,200);
        p1.setLayout(null);
        add(p1);

        //Logo of the application is placed
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("logo.png"));
        Image i2 = i1.getImage().getScaledInstance(546, 200, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel s1 = new JLabel(i3);
        s1.setBounds(0, 0, 550, 200);
        p1.add(s1);

        //We will create a panel for getting name and password
        JPanel p2 = new JPanel();
        p2.setLayout(null);
        p2.setBounds(50, 200, 450, 300);
        p2.setBackground(Color.lightGray);
        add(p2);

        //Logo of the registeration is placed
        ImageIcon i4 = new ImageIcon(ClassLoader.getSystemResource("notes.png"));
        Image i5 = i4.getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT);
        ImageIcon i6 = new ImageIcon(i5);
        JLabel s2 = new JLabel(i6);
        s2.setBounds(185, 10, 80, 80);
        p2.add(s2);

        //Now we will add the text field for username and password
        JLabel username = new JLabel(" Username: ");
        JLabel pass = new JLabel(" IP Address: ");
        JLabel port = new JLabel(" Port: ");
        username.setBounds(20, 100, 72, 30);
        pass.setBounds(20, 150, 75, 30);
        port.setBounds(300,150,40,30);
        Border lineBorder = new LineBorder(Color.BLACK, 1);
        Border matteBorder = new MatteBorder(1, 1, 1, 1, Color.RED);  //it add colors with padding
        Border compoundBorder = new CompoundBorder(matteBorder, lineBorder);  //compound border is used to add two borders
        username.setBorder(compoundBorder);   //another way to add border is (new LineBorder(color.black))
        pass.setBorder(compoundBorder);
        port.setBorder(compoundBorder);
        p2.add(username);
        p2.add(pass);
        p2.add(port);

        //A textfield is created for taking inputs of the name and password.
        text1 = new JTextField();
        text2 = new JTextField();
        text3 = new JTextField();
        text1.setBounds(100, 100, 320, 30);
        text2.setBounds(100, 150, 190, 30);
        text3.setBounds(345, 150, 70, 30);
        text1.setFont(new Font("San_serif", Font.ROMAN_BASELINE, 15));
        p2.add(text1);
        p2.add(text2);
        p2.add(text3);
        text3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent a)
            {
                loginAction();
            }
        });

        //Login button is created to proceed to the chatting window & check for invalid input
        JButton log = new JButton("Login");
        log.setBounds(185, 200, 80, 40);
        log.setFont(new Font("Serif", Font.BOLD, 18));
        log.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                loginAction();
            }
        });
        p2.add(log);

        setSize(550, 500);
        setLocation(500, 120);
        setUndecorated(true);
        getContentPane().setBackground(Color.cyan);
        setVisible(true);
    }

    public void loginAction()
    {
        name = text1.getText();
        password = text2.getText();
        portnum = text3.getText();
        if(name.length() >= 1 && password.length() >= 1 && portnum.length() >= 1)
        {
            if(isconnect() == true)
            {
                setVisible(false);
            }
            else
            {
                JOptionPane.showMessageDialog(frame, "Connection Refused", "Please enter IP & Port again",
                JOptionPane.INFORMATION_MESSAGE);
            }
        }
        else
        {
            JOptionPane.showMessageDialog(frame, "Please enter again", "Invalid Input",
            JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public boolean isconnect()
    {
        try
        {
            Socket sock = new Socket(password,Integer.parseInt(portnum));
            dio = new DataInputStream(sock.getInputStream());
            dou = new DataOutputStream(sock.getOutputStream());
            MessageCrypto me = new MessageCrypto();
            float keyy = me.keyExchange(dio, dou);
            if(keyy > 126.0)
            {
                bitt a1 = new bitt();
                a1.chatApp(name,1,dou,keyy);
            }
            return true;
        }    
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
}