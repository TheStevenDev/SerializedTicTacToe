import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Random;

public class TicTacToe extends JFrame implements ActionListener{
    Game g= new Game();
    private JLabel titleLabel = new JLabel();
    private Font titleFont = new Font("LEMON MILK",Font.BOLD,60);
    private JButton[] buttons = new JButton[9];
    private JPanel buttonsPanel = new JPanel(new GridLayout(3,3,25,25));

    //BARRA DEI MENU
    private JMenuBar menuBar = new JMenuBar(); //Barra generale
    private JMenu fileMenu = new JMenu("File"); //Opzione file e aiuto
    private JMenu helpMenu = new JMenu("Aiuto");

    //SottoOpzioni
    private JMenuItem openFile = new JMenuItem("Apri");
    private JMenuItem saveFile = new JMenuItem("Salva");
    private JMenuItem restart = new JMenuItem("Rigioca");
    private JMenuItem exit = new JMenuItem("Esci");
    private JMenuItem info = new JMenuItem("Info");

    //Matrice e variabili di Gioco
   
    private  String[] datas;
    private int mossacorrente=0;

    //Leggere e scrivere file
    private int winX=0,win0=0,tie=0;

    //Serializzazione e deserializzazione
    private JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    private JFileChooser fileChooser2 = new JFileChooser();


    //Constructor di setUp
    public TicTacToe() throws FileNotFoundException {
        this.setSize(800,820);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("TRIS SERIALIZZATO");
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.setResizable(false);
        this.setVisible(false);

        buttonsPanel.setBounds(10, 115, 780, 650);
        buttonsPanel.setLayout(new GridLayout(3, 3,5,5));

        for (int i=0; i<g.board.length;i++) g.board[i] =' ';

        //Creo i bottoni
        for (int i=0; i<9;i++){
            buttons[i] = new JButton();
            buttons[i].addActionListener(this);
            buttons[i].setFont(new Font("LEMON MILK",Font.PLAIN,55));

            if(g.turnoCorrente=='X') buttons[i].setForeground(Color.RED);
            else buttons[i].setForeground(Color.BLUE);

            buttonsPanel.add(buttons[i]);
        }

        //Imposto primo turno
        int randomNumber = new Random().nextInt(1,3);
        if(randomNumber==1) g.turnoCorrente='X';
        else g.turnoCorrente='0';

        //Imposto titolo
        titleLabel.setBounds(10, 10, 780, 100);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        if(g.turnoCorrente=='X') titleLabel.setForeground(Color.red);
        else titleLabel.setForeground(Color.BLUE);
        titleLabel.setFont(titleFont);
        titleLabel.setText("Turno di: "+g.turnoCorrente);


        //ActionListener alle opzioni
        saveFile.addActionListener(this);
        openFile.addActionListener(this);
        restart.addActionListener(this);
        info.addActionListener(this);
        exit.addActionListener(this);

        //Aggiungo i sottoMenu
        fileMenu.add(saveFile);
        fileMenu.add(openFile);
        helpMenu.add(restart);
        helpMenu.add(info);
        helpMenu.add(exit);

        //Leggo i files
        datas = readDatas();

        winX= Integer.parseInt(String.valueOf(datas[0]));
        win0 = Integer.parseInt(datas[1]);
        tie= Integer.parseInt(datas[2]);

        //Aggiungo i menu
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);



        this.setJMenuBar(menuBar);
        this.add(buttonsPanel);
        this.add(titleLabel);
    }


    private boolean cellaLibera(int n){
        if(g.board[n]!= 'X' && g.board[n]!= '0')  return true;
        return false;
    }

    //Cambio di turno e di colore
    private void cambioTurno(int nButton){
        if(g.turnoCorrente =='X'){
            titleLabel.setForeground(Color.BLUE);
            buttons[nButton].setForeground(Color.RED);

            g.turnoCorrente='0';
        }
        else{
            buttons[nButton].setForeground(Color.BLUE);
            titleLabel.setForeground(Color.RED);
            g.turnoCorrente='X';
        }

        titleLabel.setText("Turno di: "+g.turnoCorrente);

    }

    private boolean checkWinX(){
        if(g.board[0]=='X' && g.board[4]=='X' && g.board[8]=='X' ) return true;
        if (g.board[2]=='X' && g.board[4]=='X' && g.board[6]=='X') return true;

        if (g.board[0]=='X' && g.board[1]=='X' && g.board[2]=='X') return true;
        if (g.board[3]=='X' && g.board[4]=='X' && g.board[5]=='X') return true;
        if (g.board[6]=='X' && g.board[7]=='X' && g.board[8]=='X') return true;

        if (g.board[0]=='X' && g.board[3]=='X' && g.board[6]=='X') return true;
        if (g.board[1]=='X' && g.board[4]=='X' && g.board[7]=='X') return true;
        if (g.board[2]=='X' && g.board[5]=='X' && g.board[8]=='X') return true;
        return false;
    }

    private boolean checkWin0(){
        if(g.board[0]=='0' && g.board[4]=='0' && g.board[8]=='0' ) return true;
        if (g.board[2]=='0' && g.board[4]=='0' && g.board[6]=='0') return true;

        if (g.board[0]=='0' && g.board[1]=='0' && g.board[2]=='0') return true;
        if (g.board[3]=='0' && g.board[4]=='0' && g.board[5]=='0') return true;
        if (g.board[6]=='0' && g.board[7]=='0' && g.board[8]=='0') return true;

        if (g.board[0]=='0' && g.board[3]=='0' && g.board[6]=='0') return true;
        if (g.board[1]=='0' && g.board[4]=='0' && g.board[7]=='0') return true;
        if (g.board[2]=='0' && g.board[5]=='0' && g.board[8]=='0') return true;
        return false;
    }


    private boolean checkDraw(){
        //Controllo di pareggio
        int counter=0;
        for (int i=0; i<g.board.length;i++){
            if(!cellaLibera(i)) counter++;
        }

        if(!checkWinX() && !checkWin0() && counter==9) return true;
        return false;

    }

    //Mosse del bot casuali
    private int turno0(){
        int indiceFinale=0;
        Random r = new Random();

        while (true){
            int n = r.nextInt(1, 9);
            if(cellaLibera(n)){
                indiceFinale=n;
                break;
            }
        }


        return indiceFinale;

    }



    public void play(){
        this.setVisible(true);

        if(g.turnoCorrente=='0'){
            mossacorrente++;
            int indiceAI = turno0();
            g.board[indiceAI] = g.turnoCorrente;
            buttons[indiceAI].setText(String.valueOf(g.turnoCorrente));
            cambioTurno(indiceAI);
        }

    }

    //Leggo il file dei risultati
    public String[] readDatas(){
        //Leggo le statistiche
        String[] temp;
        try {
            BufferedReader br = new BufferedReader(new FileReader("src/result.txt"));
            temp = br.readLine().split(",");
            br.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return temp;
    }

    //Aggiungo le vittorie o le sconfitte o i pareggi
    private void updateDatas(int wx, int w0, int t){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/result.txt"));
            writer.write(wx+","+w0+","+t);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        //Condizioni barra dei menu
        if(e.getSource()==saveFile){
            // Imposta la directory iniziale sulla scrivania
            String desktopPath = FileSystemView.getFileSystemView().getHomeDirectory().getPath() + "/Desktop";
            fileChooser.setCurrentDirectory(new File(desktopPath));

            //File .ser
            FileNameExtensionFilter filter = new FileNameExtensionFilter("File SER (.ser)", "ser");
            fileChooser.setFileFilter(filter);

            int returnValue = fileChooser.showSaveDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                // L'utente ha selezionato un file
                String filePath = fileChooser.getSelectedFile().getPath(); //La stringa filepath contiene il path selezionato
                System.out.println(filePath);

                //Salvo su file il char della g.board e il turno corrente
                //Serializzo e salvo il file

                try {
                    FileOutputStream fileOut = new FileOutputStream(filePath);
                    ObjectOutputStream out = new ObjectOutputStream(fileOut);
                    out.writeObject(g); //Scrive nel file .ser la classe Game

                    out.close();

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }


                JOptionPane.showMessageDialog(null, "File salvato! ", "Salvataggio competato", JOptionPane.PLAIN_MESSAGE);

                try {
                    new TicTacToe();
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }


            }

        }
        if(e.getSource()==openFile) {
            String filePath2="";

            int returnValue = fileChooser2.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                // L'utente ha selezionato un file
                filePath2 = fileChooser2.getSelectedFile().getPath();
                System.out.println("Hai selezionato: " + filePath2);
            }

            //Operazioni per aprire il file .ser
            try {
                FileInputStream fileIn = new FileInputStream(filePath2);
                ObjectInputStream in = new ObjectInputStream(fileIn);

                g = (Game) in.readObject();
                in.close();

                for (int i=0; i<buttons.length;i++){
                    buttons[i].setText(String.valueOf(g.board[i]));
                    if(buttons[i].getText().equals(String.valueOf('X'))) buttons[i].setForeground(Color.RED);
                    if(buttons[i].getText().equals(String.valueOf('0'))) buttons[i].setForeground(Color.BLUE);

                }

            } catch (IOException | ClassNotFoundException e1) {
                throw new RuntimeException(e1);
            }

            JOptionPane.showMessageDialog(null, "Gioco Caricato! ", "Operazione completata", JOptionPane.PLAIN_MESSAGE);



        }




        //reset del gioco
        if(e.getSource()==restart) {
            try {
                dispose();
                new TicTacToe().play();
            } catch (FileNotFoundException ex) {throw new RuntimeException(ex);}

        }

        if(e.getSource()==exit) System.exit(0);
        if(e.getSource()==info){

            datas = readDatas();
            //Messaggio d'info
            JOptionPane.showMessageDialog(null,"Minigioco implementato da Steven con utilizzo di swing e serializzazione e file\nLe tue info:\nVittorie User: "+datas[0]+"\nVittorie Bot: "+datas[1]+"\nPareggi: "+datas[2] , "Tris Info", JOptionPane.PLAIN_MESSAGE);
        }


        for (int i=0; i<buttons.length;i++){

            //Controllo cella libera e bottone cliccato
            if(e.getSource()==buttons[i] && cellaLibera(i)){

                //Bottone cliccato
                g.board[i] = g.turnoCorrente;
                buttons[i].setText(String.valueOf(g.turnoCorrente));
                cambioTurno(i);
            }

            //Controllo vincite
            if(checkWinX()){
                titleLabel.setForeground(Color.RED);
                titleLabel.setText("X VINCE");
                for (int j=0; j<buttons.length;j++) buttons[j].setEnabled(false);
                updateDatas(winX+1, win0,tie);
                break;


            }
            if(checkWin0()){
                titleLabel.setForeground(Color.BLUE);
                titleLabel.setText("0 VINCE");
                for (JButton button : buttons) button.setEnabled(false);
                updateDatas(winX, win0+1,tie);
                break;


            }
            if(checkDraw()){
                titleLabel.setForeground(Color.GREEN);
                titleLabel.setText("PARI");
                for (JButton button : buttons) button.setEnabled(false);
                updateDatas(winX, win0,tie+1);
                break;


            }


            if(g.turnoCorrente=='0'){
                int indiceAI = turno0();
                g.board[indiceAI] = g.turnoCorrente;
                buttons[indiceAI].setText(String.valueOf(g.turnoCorrente));
                cambioTurno(indiceAI);
            }


        }



    }



}
