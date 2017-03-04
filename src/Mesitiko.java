
import cryptography.RSA;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Giwrgos
 */
public class Mesitiko extends javax.swing.JFrame {

    /**
     * Creates new form Mesitiko
     */
    /*ALL VARIABLES */
    static ResultSet result = null;

    //ORACLE DATABASE CONNECTION VARIABLES
    static String OdriverClassName = "oracle.jdbc.OracleDriver";
    static String Ourl = "";
    static Connection OdbConnection = null;
    static String Ousername = "";
    static String Opasswd = "";
    static Statement Ostatement = null;

    //POSTGRESQL DATABASE CONNECTION VARIABLES
    static String PdriverClassName = "org.postgresql.Driver";
    static String Purl = "";
    static String Pusername = "";
    static String Ppasswd = "";
    static Connection PdbConnection = null;
    static Statement Pstatement = null;

    String mesites[][];
    String houses[][];
    String pelates[][];
    String enoikiaseis[][];
    String agores[][];
    String endiaferon[][];

    Object house_icon_info[] = new Object[2];
    ImageIcon img;
    /*
     Main start up method
     */

    public Mesitiko() {
        initComponents();

        //set ola ta text sto perivallon gia na leitourgisei to connect button
        customXMLParser("./configs/postgresql.xml");
        customXMLParser("./configs/oracle.xml");

        setSize(550, 300);
        setResizable(false);

    }

    /*
     Variable test methods
     */
    /*
     elenxei an mia metavliti einai tipou integer
     dokimazei thn parsarei kai elenxei an apotixei h oxi
     */
    public boolean is_integer(String value) {
        try {
            Integer.parseInt(value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /*
     elenxei an mia metavliti einai tipou long
     dokimazei thn parsarei kai elenxei an apotixei h oxi
     */
    public boolean is_long(String value) {
        try {
            Long.parseLong(value);
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    /*
     elenxei an mia metavliti einai tipou double
     dokimazei thn parsarei kai elenxei an apotixei h oxi
     */
    public boolean is_double(String value) {
        try {
            Double.parseDouble(value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    /*
     pairnei mono ta not null spitia apo ton pinaka
     */

    public String[][] getOnlyNotNull(String[][] temp) {
        int counter = 0;
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] != null) {
                counter++;
            }
        }
        String clear[][] = new String[counter][9];

        counter = 0;
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] != null) {
                clear[counter] = temp[i];
                counter++;
            }
        }

        return clear;
    }
    /*
     checkarei ta stoixeia pou dwthikan an tiroun tis proipotheseis
     */

    public boolean isStoixeiaOk(String at, String name, String surname, String number, String afm, String address) {

        if (at.length() != 8) {
            message_optionpane.showMessageDialog(null, "Παρακαλώ ελένξτε τoν αριθμο ταυτοτητας και ξανα προσπαθήστε (Σωστο θεωρειται ενα με 8 ψηφια ) !!!");
            return false;
        } else if (name.length() < 3) {
            message_optionpane.showMessageDialog(null, "Παρακαλώ ελένξτε τo ονομα και ξανα προσπαθήστε (Σωστο θεωρειται ενα με >= 3 γραμματα ) !!!");
            return false;
        } else if (surname.length() < 3) {
            message_optionpane.showMessageDialog(null, "Παρακαλώ ελένξτε τo επωνυμο και ξανα προσπαθήστε (Σωστο θεωρειται ενα με >= 3 γραμματα ) !!!");
            return false;
        } else if (number.length() < 10) {
            message_optionpane.showMessageDialog(null, "Παρακαλώ ελένξτε τoν αριθμο τηλεφωνου και ξανα προσπαθήστε (Σωστο θεωρειται ενα με > 9 γραμματα ) !!!");
            return false;
        } else if (!is_long(number)) {
            message_optionpane.showMessageDialog(null, "Παρακαλώ ελένξτε τoν αριθμο τηλεφωνου πρεπει να εχει μονο αριθμους !!!");
            return false;
        } else if (afm.length() != 9) {
            message_optionpane.showMessageDialog(null, "Παρακαλώ ελένξτε το ΑΦΜ και ξανα προσπαθήστε (Σωστο θεωρειται ενα με 9 ψηφια )!!!");
            return false;
        } else if (address.length() < 5) {
            message_optionpane.showMessageDialog(null, "Παρακαλώ ελένξτε τoν οδος κατοικιας και ξανα προσπαθήστε (Σωστο θεωρειται ενα με > 5 γραμματα ) !!!");

            return false;
        }

        return true;

    }

    /*
     checkarei an enas mesitis exei ola tou ta stoixeia entaksei
     */
    public boolean isMesitisOk(String at, String name, String surname, String number, String afm, String address) {
        return isStoixeiaOk(at, name, surname, number, afm, address);
    }

    /* 
     checkarei an enas pelatis exei ola tou ta stoixeia entaksei
     */
    public boolean isPelatisOk(String at, String name, String surname, String number, String afm, String address) {
        return isStoixeiaOk(at, name, surname, number, afm, address);
    }

    /*
     checkarei an ena spiti exei ola tou ta stoixeia ok
     */
    public boolean isHouseOk(String tm, String region, String address, JRadioButton yes_niki, JRadioButton no_niki, JRadioButton yes_polisi, JRadioButton no_polisi, String domatia, String xronia, String picture) {
        /*
         default value se periptwsh mh epilogis toy xristi
         */
        File source = new File(picture);

        if (!yes_niki.isSelected() && !no_niki.isSelected()) {
            message_optionpane.showMessageDialog(null, "Το σπιτι θα εχει αυτοματα την επιλογη μη ικανοτητας ενοικιασης !!!");
            no_niki.setSelected(true);
        } else if (!yes_polisi.isSelected() && !no_polisi.isSelected()) {
            message_optionpane.showMessageDialog(null, "Το σπιτι θα εχει αυτοματα την επιλογη μη ικανοτητας πωλησης !!! ");
            no_polisi.setSelected(true);
        }

        if (tm.length() > 0 && (!is_integer(tm) && !is_double(tm))) {
            message_optionpane.showMessageDialog(null, "Παρακαλώ ελένξτε τα τετραγωνικα μετρα και ξανα προσπαθήστε (Σωστο θεωρειται ενα με > 0 αριθμητικα ψηφια )");
            return false;
        } else if (region.length() < 4) {
            message_optionpane.showMessageDialog(null, "Παρακαλώ ελένξτε την περιοχη και ξανα προσπαθήστε (Σωστο θεωρειται ενα με > 3 ψηφια )");
            return false;
        } else if (address.length() < 4) {
            message_optionpane.showMessageDialog(null, "Παρακαλώ ελένξτε την διευθυνση και ξανα προσπαθήστε (Σωστο θεωρειται ενα με > 3 ψηφια )");
            return false;
        } else if (!is_integer(domatia)) {
            message_optionpane.showMessageDialog(null, "Παρακαλώ ελένξτε τον αριθμο δωματιων και ξανα προσπαθήστε.Θα πρεπει να ναι μονο αριθμοι");
            return false;
        } else if (Integer.parseInt(domatia) <= 0 || Integer.parseInt(domatia) > 20) {
            message_optionpane.showMessageDialog(null, "Παρακαλώ ελένξτε τα δωματια και ξανα προσπαθήστε (Σωστο θεωρειται ενα μεταξυ 0 - 20 δωματια )");
            return false;
        } else if (Integer.parseInt(xronia) < 1900 || Integer.parseInt(xronia) > Calendar.getInstance().get(Calendar.YEAR)) {
            message_optionpane.showMessageDialog(null, "Παρακαλώ ελένξτε την χρονια και ξανα προσπαθήστε (Σωστο θεωρειται μια μεταξυ 1900 - σημερα )");
            return false;
        } else if (picture.length() < 4) {
            message_optionpane.showMessageDialog(null, "Παρακαλώ επιλεξτε μια φωτογραφια για το σπιτι αυτο )");
            return false;
        } else if (!source.exists()) {
            error_optionpane.showMessageDialog(null, "Η εικονα του σπιτιου που δωσατε δε βρεθηκε στο συστημα σας !!!");
            return false;
        }
        return true;
    }
    /*
     checkarei an ena spiti kata tin anazitisi toy exei ola tou ta stoixeia ok
     */

    public boolean isHouseSearchOk(String tm_from, String tm_mexri, String domatia_from, String domatia_mexri, String xronia_from, String xronia_mexri) {

        if (!is_double(tm_from) || !is_double(tm_mexri)) {
            error_optionpane.showMessageDialog(null, "Παρακαλουμε ελέξτε τα τετραγωνικα μετρα του σπιτιου και ξανα προσπαθηστε !!! ( Μονο αριθμοι πραγματικοι !!! )");
            return false;
        } else if (Double.parseDouble(tm_from) <= 0 || Double.parseDouble(tm_from) > 1000
                || Double.parseDouble(tm_mexri) <= 0 || Double.parseDouble(tm_mexri) > 1000) {
            error_optionpane.showMessageDialog(null, "Παρακαλουμε ελέξτε τα τετραγωνικα μετρα του σπιτιου και ξανα προσπαθηστε !!! ( σωστο > 0 - 1000 )");
            return false;
        } else if (Double.parseDouble(tm_from) > Double.parseDouble(tm_mexri)) {
            error_optionpane.showMessageDialog(null, "Παρακαλουμε ελέξτε τα τετραγωνικα μετρα του σπιτιου και ξανα προσπαθηστε !!! ( Το απο ναναι <= απο το μεχρι )");
            return false;
        } //domatia check
        else if (!is_integer(domatia_from) || !is_integer(domatia_mexri)) {
            error_optionpane.showMessageDialog(null, "Παρακαλουμε ελέξτε τον αριθμο δωματιων του σπιτιου και ξανα προσπαθηστε !!! ( Μονο αριθμοι ακεραιοι!!! )");
            return false;
        } else if (Integer.parseInt(domatia_from) < 0 || Integer.parseInt(domatia_from) > 20
                || Integer.parseInt(domatia_mexri) < 0 || Integer.parseInt(domatia_mexri) > 20) {
            error_optionpane.showMessageDialog(null, "Παρακαλουμε ελέξτε τον αριθμο δωματιων του σπιτιου και ξανα προσπαθηστε !!! ( σωστο 0 - 20 )");
            return false;
        } else if (Integer.parseInt(domatia_from) > Integer.parseInt(domatia_mexri)) {
            error_optionpane.showMessageDialog(null, "Παρακαλουμε ελέξτε τον αριθμο δωματιων του σπιτιου και ξανα προσπαθηστε !!! ( Το απο ναναι <= απο το μεχρι )");
            return false;
        } //xronia check
        else if (!is_integer(xronia_from) || !is_integer(xronia_mexri)) {
            error_optionpane.showMessageDialog(null, "Παρακαλουμε ελέξτε την χρονια του σπιτιου και ξανα προσπαθηστε !!! ( Μονο αριθμοι ακεραιοι!!! )");
            return false;
        } else if (Integer.parseInt(xronia_from) < 1900 || Integer.parseInt(xronia_from) > Calendar.getInstance().get(Calendar.YEAR)
                || Integer.parseInt(xronia_mexri) < 1900 || Integer.parseInt(xronia_mexri) > Calendar.getInstance().get(Calendar.YEAR)) {
            error_optionpane.showMessageDialog(null, "Παρακαλουμε ελέξτε την χρονια του σπιτιου και ξανα προσπαθηστε !!! ( σωστο 1900 - σημερα )");
            return false;
        } else if (Integer.parseInt(xronia_from) > Integer.parseInt(xronia_mexri)) {
            error_optionpane.showMessageDialog(null, "Παρακαλουμε ελέξτε την χρονια του σπιτιου και ξανα προσπαθηστε !!! ( Το απο ναναι <= απο το μεχρι )");
            return false;
        } //ola ok
        return true;
    }

    public boolean isOkayXreosiEnoikiasisInfo(String poso, String iban) {
        if (!is_double(poso) || poso.length() <= 0) {
            error_optionpane.showMessageDialog(null, "Παρακαλουμε ελενξτε το ποσο ενοικιασης και ξανα προσπαθηστε !!! ( Μονο αριθμητικα ψηφια )");
            return false;
        } else if (Double.parseDouble(poso) <= 0) {
            error_optionpane.showMessageDialog(null, "Παρακαλουμε ελενξτε το ποσο ενοικιασης και ξανα προσπαθηστε !!! ( Ποσο > 0 απαραιτητα )");
            return false;
        } else if (iban.length() != 34) {
            error_optionpane.showMessageDialog(null, "Παρακαλουμε ελενξτε τον αριθμο iban και ξανα προσπαθηστε !!! ( Aπαραιτητα 34 ψηφια )");
            return false;
        }
        return true;
    }

    public boolean isOkayXreosiAgoraInfo(String poso, String iban) {
        if (!is_double(poso) || poso.length() <= 0) {
            error_optionpane.showMessageDialog(null, "Παρακαλουμε ελενξτε το ποσο αγορας και ξανα προσπαθηστε !!! ( Μονο αριθμητικα ψηφια )");
            return false;
        } else if (Double.parseDouble(poso) <= 0) {
            error_optionpane.showMessageDialog(null, "Παρακαλουμε ελενξτε το ποσο αγορας και ξανα προσπαθηστε !!! ( Ποσο > 0 απαραιτητα )");
            return false;
        } else if (iban.length() != 34) {
            error_optionpane.showMessageDialog(null, "Παρακαλουμε ελενξτε τον αριθμο iban και ξανα προσπαθηστε !!! ( Aπαραιτητα 34 ψηφια )");
            return false;
        }
        return true;
    }

    /*
     an to spiti einai diathesimo gia agora h enoikiasi
     */
    public boolean isHouseAvailable(String id) {
        boolean available = true;
        for (int i = 0; i < agores.length; i++) {
            if (Integer.parseInt(id) == Integer.parseInt(agores[i][1])) {
                available = false;
            }
        }
        for (int j = 0; j < enoikiaseis.length; j++) {
            if (Integer.parseInt(id) == Integer.parseInt(enoikiaseis[j][1])) {
                available = false;
            }
        }
        return available;
    }

    /*
     Confirm connection methods
     */
    //AN EXOUME SUMPLIRWTHEI OLA TA PEDIA ENERGOPOIEITAI TO CONNECT TO DATABASE
    public void checkConfigs() {
        if (user_oracle.getText().length() > 0 && pass_oracle.getText().length() > 0 && service_oracle.getText().length() > 0 && server_oracle.getText().length() > 0 && port_oracle.getText().length() > 0) {
            oracle_configs_button.setForeground(Color.blue);
        } else {
            oracle_configs_button.setForeground(Color.RED);
        }

        if (user_postgresql.getText().length() > 0 && pass_postgresql.getText().length() > 0 && service_postgresql.getText().length() > 0 && server_postgresql.getText().length() > 0 && port_postgresql.getText().length() > 0) {
            postgresql_configs_button.setForeground(Color.blue);
        } else {
            postgresql_configs_button.setForeground(Color.RED);
        }

        if (oracle_configs_button.getForeground() == Color.blue && postgresql_configs_button.getForeground() == Color.blue) {
            connect_database_button.setForeground(Color.blue);
        } else {
            connect_database_button.setForeground(Color.RED);
        }
    }

    /*
     parsarei diabazei ousiastika to xml file kai to kataxwrei sta config menu
     */
    public void customXMLParser(String file_loc) {
        try {
            File file = new File(file_loc);
            if (file.exists() && file.getTotalSpace() > 0) {
                BufferedReader br = new BufferedReader(new FileReader(file));

                int index = 0;
                String temp = br.readLine();
                String configs[] = new String[4];
                while (temp != null) {
                    temp = temp.substring(temp.indexOf('=') + 1, temp.length());
                    configs[index] = temp;
                    temp = br.readLine();
                    index++;
                }
                br.close();

                RSA rsa;

                if (file_loc.contains("postgresql")) {
                    rsa = new RSA("postgresql");
                    user_postgresql.setText(configs[0]);
                    pass_postgresql.setText(rsa.getDecrypted());
                    service_postgresql.setText(configs[1]);
                    server_postgresql.setText(configs[2]);
                    port_postgresql.setText(configs[3]);
                } else if (file_loc.contains("oracle")) {
                    rsa = new RSA("oracle");
                    user_oracle.setText(configs[0]);
                    pass_oracle.setText(rsa.getDecrypted());
                    service_oracle.setText(configs[1]);
                    server_oracle.setText(configs[2]);
                    port_oracle.setText(configs[3]);
                }
                checkConfigs();
            }

        } catch (Exception e) {

        }
    }

    /*
     diabazei ta config menu kai ta apothikeuei se ena xml file gia thn epomenh fora poy tha anoiksei h efarmogh
     */
    public void customXMLMaker(String file_loc) {
        try {
            File file = new File(file_loc);
            if (file.exists()) {
                file.delete();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));

            //kriptografisi tou kwdikou
            RSA rsa;

            if (file_loc.contains("postgresql")) {
                bw.write("Username=" + user_postgresql.getText());
                bw.newLine();
                //kriptografisi
                rsa = new RSA(pass_postgresql.getText(), "postgresql");

                bw.write("Service=" + service_postgresql.getText());
                bw.newLine();
                bw.write("Server=" + server_postgresql.getText());
                bw.newLine();
                bw.write("Port=" + port_postgresql.getText());
            } else if (file_loc.contains("oracle")) {
                bw.write("Username=" + user_oracle.getText());
                bw.newLine();
                //kriptografisi
                rsa = new RSA(pass_oracle.getText(), "oracle");

                bw.write("Service=" + service_oracle.getText());
                bw.newLine();
                bw.write("Server=" + server_oracle.getText());
                bw.newLine();
                bw.write("Port=" + port_oracle.getText());
            }

            checkConfigs();

            bw.close();
        } catch (Exception e) {

        }
    }

    /*
     Database connection and  queries results methods
     */
    // h methodos h opoia tha rithmisei katallila oles tis metavlites simfwna me ta config files
    public boolean connectToDatabase() {

        try {

            //oracle rithmiseis
            Ourl = "jdbc:oracle:thin:@" + server_oracle.getText() + ":" + port_oracle.getText() + ":" + service_oracle.getText();
            Ousername = user_oracle.getText();
            Opasswd = pass_oracle.getText();
            //connect to oracle
            Class.forName(OdriverClassName);
            OdbConnection = DriverManager.getConnection(Ourl, Ousername, Opasswd);
            Ostatement = OdbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            //postgress rithmiseis
            Purl = "jdbc:postgresql://" + server_postgresql.getText() + ":" + port_postgresql.getText() + "/" + service_postgresql.getText();
            Pusername = user_postgresql.getText();
            Ppasswd = pass_postgresql.getText();
            //connect to postgresql
            Class.forName(PdriverClassName);
            PdbConnection = DriverManager.getConnection(Purl, Pusername, Ppasswd);
            Pstatement = PdbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /*
     pernei tous mesites apo thn vash dedomenwn kai tous pernaei se pinaka mesites
     */
    public boolean getMesites(JComboBox lista, int pedio1, int pedio2) {
        try {
            if (lista != null) {
                lista.removeAllItems();

                result = Pstatement.executeQuery("SELECT * FROM mesitesList() ");

                mesites = new String[getQueryResultRows(result)][6];
                result.beforeFirst();
                int index = 0;
                while (result.next()) {
                    for (int i = 0; i < 6; i++) {
                        mesites[index][i] = result.getString(i + 1);
                    }
                    lista.addItem(result.getString(pedio1) + " " + result.getString(pedio2));
                    index++;
                }
            } else {
                result = Pstatement.executeQuery("SELECT * FROM mesitesList() ");
                mesites = new String[getQueryResultRows(result)][6];
                int index = 0;
                while (result.next()) {
                    for (int i = 0; i < 6; i++) {
                        mesites[index][i] = result.getString(i + 1);
                    }
                    index++;
                }
            }
            //opou kai na mpei an ftasei edw gg true
            return true;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα με τους μεσιτες. Δεν εχετε καποιον καταχωρημενο στο σερβερ της postgres !!!");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }
        } catch (Exception ex) {
            error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
            System.out.println("Message: " + ex.getMessage());
        }
        return false;
    }

    public boolean getPelates(JComboBox lista, int pedio1, int pedio2) {
        try {
            if (lista != null) {
                lista.removeAllItems();

                result = Ostatement.executeQuery("SELECT * from table(pelatesList()) ");
                pelates = new String[getQueryResultRows(result)][6];
                result.beforeFirst();
                int index = 0;
                while (result.next()) {
                    for (int i = 0; i < 6; i++) {
                        pelates[index][i] = result.getString(i + 1);
                    }
                    lista.addItem(result.getString(pedio1) + " " + result.getString(pedio2));
                    index++;
                }
            } else {
                result = Ostatement.executeQuery("SELECT * from table(pelatesList()) ");
                pelates = new String[getQueryResultRows(result)][6];
                int index = 0;
                while (result.next()) {
                    for (int i = 0; i < 6; i++) {
                        pelates[index][i] = result.getString(i + 1);
                    }
                    index++;
                }
            }
            return true;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα με τους μεσιτες. Δεν εχετε καποιον καταχωρημενο στο σερβερ της postgres !!!");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }

        } catch (Exception ex) {
            error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
            System.out.println("Message: " + ex.getMessage());
        }
        return false;
    }

    public boolean getHouses(JComboBox lista, int pedio1, int pedio2) {

        try {
            if (lista != null) {
                lista.removeAllItems();
                result = Pstatement.executeQuery("SELECT * FROM housesList() ");

                houses = new String[getQueryResultRows(result)][9];
                result.beforeFirst();
                int index = 0;

                while (result.next()) {
                    for (int i = 0; i < 9; i++) {
                        houses[index][i] = result.getString(i + 1);
                    }
                    lista.addItem(result.getString(pedio1) + " " + result.getString(pedio2));
                    index++;
                }

            } else {
                result = Pstatement.executeQuery("SELECT * FROM housesList() ");
                houses = new String[getQueryResultRows(result)][9];
                int index = 0;
                while (result.next()) {
                    for (int i = 0; i < 9; i++) {
                        houses[index][i] = result.getString(i + 1);
                    }
                    index++;
                }
            }
            return true;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα με τα houses. Δεν εχετε καποιο καταχωρημενο στο σερβερ της postgres !!!");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }

        } catch (Exception ex) {
            error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
            System.out.println("Message: " + ex.getMessage());
        }
        return false;

    }

    public boolean getEnoikiaseis(JComboBox lista, int pedio1, int pedio2) {
        try {
            if (lista != null) {
                lista.removeAllItems();

                result = Ostatement.executeQuery("SELECT * from table(enoikiaseisList()) ");
                enoikiaseis = new String[getQueryResultRows(result)][5];
                result.beforeFirst();
                int index = 0;
                while (result.next()) {
                    for (int i = 0; i < 5; i++) {
                        enoikiaseis[index][i] = result.getString(i + 1);
                    }
                    lista.addItem(result.getString(pedio1) + " " + result.getString(pedio2));
                    index++;
                }
            } else {
                result = Ostatement.executeQuery("SELECT * from table(enoikiaseisList()) ");
                enoikiaseis = new String[getQueryResultRows(result)][5];
                int index = 0;
                while (result.next()) {
                    for (int i = 0; i < 5; i++) {
                        enoikiaseis[index][i] = result.getString(i + 1);
                    }
                    index++;
                }
            }
            return true;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα με τις ενοικιασεις. Δεν εχετε καποιον καταχωρημενο στο σερβερ της oracle !!!");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }

        } catch (Exception ex) {
            error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
            System.out.println("Message: " + ex.getMessage());
        }
        return false;
    }

    public boolean getEndiaferontes(JComboBox lista, JComboBox spitia, int pedio1, int pedio2) {
        try {
            if (lista != null) {
                lista.removeAllItems();
                int selected = spitia.getSelectedIndex();
                result = Ostatement.executeQuery("SELECT * from table(endiaferonList(" + Integer.parseInt(houses[selected][0]) + ")) ");

                endiaferon = new String[getQueryResultRows(result)][3];
                result.beforeFirst();
                int index = 0;
                while (result.next()) {
                    for (int i = 0; i < 3; i++) {
                        endiaferon[index][i] = result.getString(i + 1);
                    }
                    lista.addItem(result.getString(pedio1) + " " + result.getString(pedio2));
                    index++;
                }
            } else {
                int selected = spitia.getSelectedIndex();
                result = Ostatement.executeQuery("SELECT * from table(endiaferonList(" + Integer.parseInt(houses[selected][0]) + ")) ");

                endiaferon = new String[getQueryResultRows(result)][3];
                int index = 0;
                while (result.next()) {
                    for (int i = 0; i < 3; i++) {
                        endiaferon[index][i] = result.getString(i + 1);
                    }
                    index++;
                }
            }
            return true;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα με τoυς ενδιαφεροντες. Δεν εχετε καποιον καταχωρημενο στο σερβερ της oracle !!!");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }

        }
        return false;
    }

    public boolean getAgores(JComboBox lista, int pedio1, int pedio2) {
        try {
            if (lista != null) {
                lista.removeAllItems();

                result = Ostatement.executeQuery("SELECT * from table(agoresList()) ");
                agores = new String[getQueryResultRows(result)][5];
                result.beforeFirst();
                int index = 0;
                while (result.next()) {
                    for (int i = 0; i < 5; i++) {
                        agores[index][i] = result.getString(i + 1);
                    }
                    lista.addItem(result.getString(pedio1) + " " + result.getString(pedio2));
                    index++;
                }
            } else {
                result = Ostatement.executeQuery("SELECT * from table(agoresList()) ");
                agores = new String[getQueryResultRows(result)][5];
                int index = 0;
                while (result.next()) {
                    for (int i = 0; i < 5; i++) {
                        agores[index][i] = result.getString(i + 1);
                    }
                    index++;
                }
            }
            return true;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα με τις αγορες. Δεν εχετε καποιον καταχωρημενο στο σερβερ της oracle !!!");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }

        } catch (Exception ex) {
            error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
            System.out.println("Message: " + ex.getMessage());
        }
        return false;
    }
    /*
     kataxwrei ena spiti sthn vash dedeomenwn
     */

    public boolean insertHouseIntoDatabase(String tm, String region, String address, JRadioButton yes_niki, JRadioButton no_niki, JRadioButton yes_polisi, JRadioButton no_polisi, String domatia, String xronia, String picture, JComboBox mesitis) {
        int nikiazetai = 0;
        int poleitai = 0;

        if (nai_poleitai_spiti.isSelected()) {
            poleitai = 1;
        }
        if (yes_nikiazetai_spiti.isSelected()) {
            nikiazetai = 1;
        }

        try {
            result = Pstatement.executeQuery("Select insertSpiti(" + Double.parseDouble(tm) + ",'" + region + "','"
                    + address + "'," + nikiazetai + "," + poleitai + ","
                    + Integer.parseInt(domatia) + "," + Integer.parseInt(xronia)
                    + ",'" + mesites[mesitis.getSelectedIndex()][0] + "')");

            result.next();

            File source = new File(picture);

            if (is_integer(result.getString(1)) && Integer.parseInt(result.getString(1)) >= 0) {
                //antigrafw to arxeio tou xristi
                //kai paei pictures/houses/houst_id.file_extension

                File destination = new File("./pictures/houses/" + result.getString(1) + ".jpg");
                try {
                    copyFile(source, destination);
                } catch (Exception e) {
                    System.out.println(e);
                }

                return true;
            } else {
                error_optionpane.showMessageDialog(null, "Δεν μπορεσαμε να βρουμε τον πινακα με τα σπιτια.Χαθηκε η επικοινωνια με τον σερβερ !!!");
            }

        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {

                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }
        } catch (Exception ex) {
            error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
            System.out.println("Message: " + ex.getMessage());
        }
        return false;
    }

    /*
     kataxwrei enan mesiti sthn vash dedeomenwn
     */
    public boolean insertMesitisIntoDatabase(String at, String name, String surname, String number, String afm, String address) {
        try {
            result = Pstatement.executeQuery("Select insertMesitis('" + at + "','" + name + "','" + surname + "'," + Long.parseLong(number) + ",'" + afm + "','" + address + "')");

            result.next();
            if (result.getString(1).contains("Ok")) {
                getMesites(mesites_lista_spitiou, 2, 3);
                return true;
            } else {
                message_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
            }
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                if (ex.getMessage().contains("duplicate key value violates unique constraint \"mesites_pkey\"")) {
                    message_optionpane.showMessageDialog(null, "Υπαρχει ήδη μεσίτης με Αριθμο Ταυτοτητας : " + at_mesiti.getText() + " !!!");
                } else if (ex.getMessage().contains("unique constraint")) {
                    message_optionpane.showMessageDialog(null, "Υπαρχει ήδη μεσιτης με ΑΦΜ : " + afm_mesiti.getText() + " !!!");
                } else {
                    message_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
                }
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }
        } catch (Exception ex) {
            message_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
            System.out.println("Message: " + ex.getMessage());
        }
        return false;
    }

    /*
     kataxwrei enan pelati sthn vash dedemenwn
     */
    public boolean insertPelatisIntoDatabase(String at, String name, String surname, String number, String afm, String address) {
        try {
            result = Ostatement.executeQuery("Select insertPelatis('" + at + "','" + name + "','" + surname + "'," + Long.parseLong(number) + ",'" + afm + "','" + address + "')  from dual ");

            result.next();
            if (result.getString(1).contains("Ok")) {
                return true;
            } else {
                message_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
            }
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                if (ex.getMessage().contains("duplicate key value violates unique constraint \"mesites_pkey\"")) {
                    error_optionpane.showMessageDialog(null, "Υπαρχει ήδη πελατης με Αριθμο Ταυτοτητας : " + at_pelati.getText() + " !!!");
                } else if (ex.getMessage().contains("unique constraint")) {
                    error_optionpane.showMessageDialog(null, "Υπαρχει ήδη πελατης με ΑΦΜ : " + afm_pelati.getText() + " !!!");
                } else {
                    error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
                }
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }
        } catch (Exception ex) {
            error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
            System.out.println("Message: " + ex.getMessage());
        }
        return false;
    }

    /*
     kataxwrei mia enoikiasi sthn vash dedemenwn
     */
    public boolean insertEnoikiasiSpitiou(JComboBox lista_pelates, JComboBox lista_houses, String poso, String iban) {
        try {
            int index_pelatis = lista_pelates.getSelectedIndex();
            int index_house = lista_houses.getSelectedIndex();
            result = Ostatement.executeQuery("call insertEnoikiasi('" + pelates[index_pelatis][0] + "'," + Integer.parseInt(houses[index_house][0])
                    + "," + Float.parseFloat(poso) + ",'" + iban + "') ");

            message_optionpane.showMessageDialog(null, "Το σπιτι εχει ενοικιαστει με επιτυχεια !!!");

            search_results.setVisible(false);
            enoikiasi.setVisible(false);

            return true;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }
        } catch (Exception ex) {
            error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
            System.out.println("Message: " + ex.getMessage());
        }
        return false;
    }

    /*
     kataxwrei enan endiaferomeno sthn vash dedemenwn
     */
    public boolean insertEndiaferon(JComboBox lista_pelates, JComboBox lista_houses) {
        try {
            int index_pelatis = lista_pelates.getSelectedIndex();
            int index_house = lista_houses.getSelectedIndex();
            result = Ostatement.executeQuery("call insertEndiaferon('" + pelates[index_pelatis][0] + "'," + Integer.parseInt(houses[index_house][0])
                    + ") ");

            message_optionpane.showMessageDialog(null, "O ενδιαφερομενος εχει καταχωρηθεί με επιτυχεια !!!");

            kataxorisi_endiaferon.setVisible(false);

            return true;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                if (ex.getMessage().contains("unique constraint")) {
                    error_optionpane.showMessageDialog(null, "Ο πελατης δε μπορει να ενδιαφερθεί ξανα για το ιδιο σπιτι !!!");
                } else {
                    error_optionpane.showMessageDialog(null, "Κατι πηγε στραβα με την καταχωρηση ! !!");
                }
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }
        } catch (Exception ex) {
            error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
            System.out.println("Message: " + ex.getMessage());
        }
        return false;
    }

    /*
     kataxwrei mia agora sthn vash dedemenwn
     */
    public boolean insertAgoraSpitiou(JComboBox lista_pelates, JComboBox lista_houses, String poso, String iban) {
        try {
            int index_pelatis = lista_pelates.getSelectedIndex();
            int index_house = lista_houses.getSelectedIndex();
            result = Ostatement.executeQuery("call insertAgores('" + pelates[index_pelatis][0] + "'," + Integer.parseInt(houses[index_house][0])
                    + "," + Float.parseFloat(poso) + ",'" + iban + "') ");

            message_optionpane.showMessageDialog(null, "Το σπιτι εχει αγοραστει με επιτυχεια !!!");

            search_results.setVisible(false);
            agora.setVisible(false);

            return true;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }
        } catch (Exception ex) {
            error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
            System.out.println("Message: " + ex.getMessage());
        }
        return false;
    }
    /*
     tha kanei update ton pinaka me tous mesites simfwna me to index
     tha pernei apo ton pinaka mesites to column index
     kai tha to kataxwrei sthn vash where arithmos tautotitas = mesites[index][0]
     */

    private boolean updateMesitisInfo(String at, String name, String surname, String number, String afm, String address, JComboBox lista) {
        try {
            int index = lista.getSelectedIndex();
            result = Pstatement.executeQuery("SELECT updateMesites('"
                    + at + "','" + name + "','" + surname + "',"
                    + Long.parseLong(number) + ",'" + afm + "','" + address
                    + "','" + mesites[index][0] + "')"
            );

            getMesites(lista, 2, 3);
            /*
             de xreiazetai allagi mesitis menu gia to logo oti kata to getmesites 
             stelnw thn lista edit_mesites_list kai etsi tha thn katharisei arxika prin thn gemisei
             opote tha treksei item state event opou tha klhthei apo ekei
             arxika de tha thn treksei logo oti tha einai katharismenh alla tha thn treksei thn stigmi pou tha mpoyne ta items
             thn prwth fora mono opou kai tha allaksei to selected index apo -1 se 0
            
             //changeEditMesitisInfo();
             */
            message_optionpane.showMessageDialog(null, "Ο μεσιτης ενημερωθηκε με επιτυχεια !!! ");
            return true;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα με τους μεσιτες. Δεν μπορειτε να ενημερωσετε τον συγκεκριμενο στον σερβερ της postgres !!!");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }

        } catch (Exception ex) {
            error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
            System.out.println("Message: " + ex.getMessage());
        }
        return false;
    }

    /*
     tha kanei update ton pinaka me ta spitia simfwna me to index
     tha pernei apo ton pinaka spitia to column index
     kai tha to kataxwrei sthn vash where arithmos tautotitas = spitia[index][0]
     */
    public boolean updateHouseInfo(String tm, String region, String address, JRadioButton yes_niki, JRadioButton no_niki, JRadioButton yes_polisi, JRadioButton no_polisi, String domatia, String xronia, String picture, JComboBox mesitis_list, JComboBox houses_list) {
        try {
            int enoikiasi = 0;
            int polisi = 0;
            if (yes_niki.isSelected()) {
                enoikiasi = 1;
            }
            if (yes_polisi.isSelected()) {
                polisi = 1;
            }

            int index_mesiti = mesitis_list.getSelectedIndex();
            int index_house = houses_list.getSelectedIndex();

            result = Pstatement.executeQuery("SELECT updateHouse("
                    + Double.parseDouble(tm) + ",'" + region + "','" + address + "',"
                    + enoikiasi + "," + polisi + "," + Integer.parseInt(domatia) + "," + Integer.parseInt(xronia)
                    + ",'" + mesites[index_mesiti][0] + "'," + houses[index_house][0] + ")"
            );

            getMesites(mesitis_list, 2, 3);
            getHouses(houses_list, 3, 4);
            /*
             de xreiazetai allagi mesitis menu gia to logo oti kata to getmesites 
             stelnw thn lista edit_mesites_list kai etsi tha thn katharisei arxika prin thn gemisei
             opote tha treksei item state event opou tha klhthei apo ekei
             arxika de tha thn treksei logo oti tha einai katharismenh alla tha thn treksei thn stigmi pou tha mpoyne ta items
             thn prwth fora mono opou kai tha allaksei to selected index apo -1 se 0
            
             //changeEditMesitisInfo();
             */
            File source = new File(picture);
            File dest = new File("./pictures/houses/" + houses[index_house][0] + ".jpg");

            copyFile(source, dest);
            message_optionpane.showMessageDialog(null, "Το σπιτι ενημερωθηκε με επιτυχεια !!! ");
            return true;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα με τα σπιτια. Δεν μπορειτε να ενημερωσετε τον συγκεκριμενο στον σερβερ της postgres !!!");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }

        } catch (Exception ex) {

            error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
            System.out.println("Message: " + ex.getMessage());
        }
        return false;
    }
    /*
     enimerwnei ta stoixeia tou pelati
     */

    public boolean updatePelatisInfo(String at, String name, String surname, String number, String afm, String address, JComboBox lista) {

        try {
            int index = lista.getSelectedIndex();
            result = Ostatement.executeQuery("call updatePelates('"
                    + at + "','" + name + "','" + surname + "',"
                    + Long.parseLong(number) + ",'" + afm + "','" + address
                    + "','" + pelates[index][0] + "') "
            );
            getPelates(lista, 2, 3);
            /*
             de xreiazetai allagi mesitis menu gia to logo oti kata to getPelates
             stelnw thn lista edit_mesites_list kai etsi tha thn katharisei arxika prin thn gemisei
             opote tha treksei item state event opou tha klhthei apo ekei
             arxika de tha thn treksei logo oti tha einai katharismenh alla tha thn treksei thn stigmi pou tha mpoyne ta items
             thn prwth fora mono opou kai tha allaksei to selected index apo -1 se 0
            
             //changeEditPelatesInfo();
             */

            message_optionpane.showMessageDialog(null, "Ο πελατης ενημερωθηκε με επιτυχεια !!! ");
            return true;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα με τους πελατες . Δεν μπορειτε να ενημερωσετε τον συγκεκριμενο στον σερβερ της oracle !!!");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }

        }
        return false;
    }
    /*
     enimerwnei ta stoixeia mias enoikiashs
     */

    public boolean updateEnoikiaseisInfo(String poso_mina, String iban, JComboBox lista) {

        try {
            int index = lista.getSelectedIndex();
            result = Ostatement.executeQuery("call updateEnoikiaseis("
                    + Integer.parseInt(enoikiaseis[index][1]) + "," + Double.parseDouble(poso_mina) + ",'" + iban + "') "
            );
            getEnoikiaseis(lista, 1, 2);

            message_optionpane.showMessageDialog(null, "Η ενοικιαση ενημερωθηκε με επιτυχεια !!! ");
            return true;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα με τις ενοικιασεις . Δεν μπορειτε να ενημερωσετε τον συγκεκριμενο στον σερβερ της oracle !!!");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }

        }
        return false;
    }

    /*
     enimerwnei ta stoixeia mias enoikiashs
     */
    public boolean updateAgoresInfo(String kostos, String iban, JComboBox lista) {

        try {
            int index = lista.getSelectedIndex();
            result = Ostatement.executeQuery("call updateAgores("
                    + Integer.parseInt(agores[index][1]) + "," + Double.parseDouble(kostos) + ",'" + iban + "') "
            );
            getAgores(lista, 1, 2);

            message_optionpane.showMessageDialog(null, "Η αγορα ενημερωθηκε με επιτυχεια !!! ");
            return true;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα με τις αγορες . Δεν μπορειτε να ενημερωσετε τον συγκεκριμενο στον σερβερ της oracle !!!");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }

        }
        return false;
    }

    /*
     enimerwnei ta stoixeia mias enoikiashs
     */
    public boolean updateEndiaferonInfo(String pelatis, String house, JComboBox lista) {

        try {

            int index = lista.getSelectedIndex();
            result = Ostatement.executeQuery("call updateEndiaferon('"
                    + pelatis + "'," + Integer.parseInt(house) + ",'"
                    + endiaferon[index][0] + "') "
            );
            getEndiaferontes(lista, spitia_result, 1, 2);

            message_optionpane.showMessageDialog(null, "Ο ενδιαφερομενος ενημερωθηκε με επιτυχεια !!! ");
            return true;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                error_optionpane.showMessageDialog(null, "Το σπιτι ή ο πελατης δε υπαρχει. Δεν μπορειτε να ενημερωσετε τον συγκεκριμενο στον σερβερ της oracle με αυτα τα στοιχεια !!!");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }

        }
        return false;
    }

    public boolean deleteMesitisFromDatabase(JComboBox lista) {
        try {
            int index = lista.getSelectedIndex();
            result = Pstatement.executeQuery("SELECT deleteMesiti('"
                    + mesites[index][0] + "')"
            );

            getMesites(lista, 2, 3);
            /*
             de xreiazetai allagi mesitis menu gia to logo oti kata to getmesites 
             stelnw thn lista edit_mesites_list kai etsi tha thn katharisei arxika prin thn gemisei
             opote tha treksei item state event opou tha klhthei apo ekei
             arxika de tha thn treksei logo oti tha einai katharismenh alla tha thn treksei thn stigmi pou tha mpoyne ta items
             thn prwth fora mono opou kai tha allaksei to selected index apo -1 se 0
            
             //changeEditMesitisInfo();
             */
            message_optionpane.showMessageDialog(null, "Ο μεσιτης διαγραφτηκε με επιτυχεια !!! ");
            return true;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα με τους μεσιτες. Δεν μπορειτε να ενημερωσετε τον συγκεκριμενο στον σερβερ της postgres !!!");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }

        } catch (Exception ex) {
            error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
            System.out.println("Message: " + ex.getMessage());
        }
        return false;
    }

    public boolean deleteHouseFromDatabase(JComboBox houses_list) {
        try {
            int index_spiti = houses_list.getSelectedIndex();

            deleteFile("./pictures/houses/" + houses[index_spiti][0] + ".jpg");

            result = Pstatement.executeQuery("SELECT deleteHouseUseId("
                    + houses[index_spiti][0] + ")"
            );

            deleteEndiaferonFromDatabase(null, "", Integer.parseInt(houses[index_spiti][0]));

            deleteAgoraFromDatabase(null, Integer.parseInt(houses[index_spiti][0]));
            deleteEnoikiasiFromDatabase(null, Integer.parseInt(houses[index_spiti][0]));
            
            getHouses(houses_list, 3, 4);
            /*
             de xreiazetai allagi mesitis menu gia to logo oti kata to getmesites 
             stelnw thn lista edit_mesites_list kai etsi tha thn katharisei arxika prin thn gemisei
             opote tha treksei item state event opou tha klhthei apo ekei
             arxika de tha thn treksei logo oti tha einai katharismenh alla tha thn treksei thn stigmi pou tha mpoyne ta items
             thn prwth fora mono opou kai tha allaksei to selected index apo -1 se 0
            
             //changeEditMesitisInfo();
             */
            message_optionpane.showMessageDialog(null, "Το σπιτι διαγραφτηκε με επιτυχεια !!! ");
            search_results.setVisible(false);
            return true;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα με τα σπιτια. Δεν μπορειτε να ενημερωσετε τον συγκεκριμενο στον σερβερ της postgres !!!");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }

        } catch (Exception ex) {
            error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
            System.out.println("Message: " + ex.getMessage());
        }
        return false;
    }

    /*
     diagrafei pelati apo thn vasi dedomenwn
    
     */
    public boolean deletePelatisFromDatabase(JComboBox lista) {
        try {
            int index = lista.getSelectedIndex();
            result = Ostatement.executeQuery("call deletePelatis('"
                    + pelates[index][0] + "') "
            );

            deleteEndiaferonFromDatabase(null, pelates[index][0], -1);

            getPelates(lista, 2, 3);
            message_optionpane.showMessageDialog(null, "Ο πελατης διαγραφτηκε με επιτυχεια !!! ");

            return true;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα με τους πελατες. Δεν μπορειτε να ενημερωσετε τον συγκεκριμενο στον σερβερ της postgres !!!");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }

        } catch (Exception ex) {
            error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
            System.out.println("Message: " + ex.getMessage());
        }
        return false;
    }

    /*
     diagrafei endiaferon apo thn vasi dedomenwn
    
     */
    public boolean deleteEndiaferonFromDatabase(JComboBox lista, String pelatis, int spiti) {
        try {
            if (lista != null) {
                int index = lista.getSelectedIndex();
                result = Ostatement.executeQuery("call deleteEndiaferonUseAll('"
                        + endiaferon[index][0] + "'," + Integer.parseInt(endiaferon[index][1]) + ") "
                );

                getEndiaferontes(lista, spitia_result, 1, 2);

                message_optionpane.showMessageDialog(null, "O ενδιαφερομενος διαγραφτηκε με επιτυχεια !!! ");

                if (lista.getItemCount() == 0) {
                    endiaferon_edit.setVisible(false);
                }
            } else if (!pelatis.isEmpty()) {
                result = Ostatement.executeQuery("call deleteEndiaferonUseArt('"
                        + pelatis + "') "
                );
                message_optionpane.showMessageDialog(null, "Μαζι με τον πελατη διαγράφθηκαν και τα ενδιαφεροντα του στα σπιτια !!! ");
            } else if (spiti != -1) {
                result = Ostatement.executeQuery("call deleteEndiaferonUseHouse("
                        + spiti + ") "
                );
                message_optionpane.showMessageDialog(null, "Μαζι με το σπιτι διαγραφηκαν και οι ενδιαφερομενοι του !!! ");
            }

            return true;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα με τους ενδιαφερομενους. Δεν μπορειτε να ενημερωσετε τον συγκεκριμενο στον σερβερ της postgres !!!");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }

        } catch (Exception ex) {
            error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
            System.out.println("Message: " + ex.getMessage());
        }
        return false;
    }

    /*
     diagrafei enoikiasi apo thn vasi dedomenwn
    
     */
    public boolean deleteEnoikiasiFromDatabase(JComboBox lista, int id) {
        try {
            if (lista != null) {
                int index = lista.getSelectedIndex();
                result = Ostatement.executeQuery("call deleteEnoikiasi('"
                        + Integer.parseInt(enoikiaseis[index][1]) + "') "
                );

                getEnoikiaseis(lista, 1, 2);
                if (lista.getItemCount() == 0) {
                    enoikiaseis_edit.setVisible(false);
                }
                message_optionpane.showMessageDialog(null, "Η ενοικιαση διαγραφτηκε με επιτυχεια !!! ");
            } else if (id != -1) {
                result = Ostatement.executeQuery("call deleteEnoikiasi("
                        + id + ") "
                );
                message_optionpane.showMessageDialog(null, "Mαζι με το σπίτι διαγράφηκαν και οι ενοικιάσεις αν έγιναν !!! ");
            }
            return true;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα με τις ενοικιασεις. Δεν μπορειτε να ενημερωσετε τον συγκεκριμενο στον σερβερ της postgres !!!");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }

        } catch (Exception ex) {
            error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
            System.out.println("Message: " + ex.getMessage());
        }
        return false;
    }
    /*
     diagrafei mia agora apo thn vasi dedomenwn
    
     */

    public boolean deleteAgoraFromDatabase(JComboBox lista, int id) {
        try {
            if (lista != null) {
                int index = lista.getSelectedIndex();
                result = Ostatement.executeQuery("call deleteAgora('"
                        + Integer.parseInt(agores[index][1]) + "') "
                );

                getAgores(lista, 1, 2);
                if (lista.getItemCount() == 0) {
                    agores_edit.setVisible(false);
                }
                message_optionpane.showMessageDialog(null, "Η αγορα διαγραφτηκε με επιτυχεια !!! ");
            } else if (id != -1) {
                result = Ostatement.executeQuery("call deleteAgora("
                        + id + ") "
                );
                message_optionpane.showMessageDialog(null, "Mαζι με το σπίτι διαγράφηκαν και οι αγορες αν εγιναν !!! ");
            }

            return true;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα με τις αγορες. Δεν μπορειτε να ενημερωσετε τον συγκεκριμενο στον σερβερ της postgres !!!");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }

        } catch (Exception ex) {
            error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
            System.out.println("Message: " + ex.getMessage());
        }

        return false;
    }

    private int getQueryResultRows(ResultSet result) {
        try {
            int rowCounter = 0;
            while (result.next()) {
                rowCounter++;
            }
            result.beforeFirst();
            return rowCounter;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα με το query στο getQueryResultRows method  !!! ");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }
            return 0;
        } catch (Exception ex) {
            error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα με το getQueryResultRows method ξανα προσπαθήστε επικοινωνηστε με τον διαχειριστη !!!");
            System.out.println("Message: " + ex.getMessage());
            return 0;
        }
    }

    private int getAvailableHousesQueryRows(ResultSet result) {
        try {
            int rowCounter = 0;
            boolean vrethike;

            while (result.next()) {
                vrethike = false;
                for (int i = 0; i < agores.length; i++) {
                    if (Integer.parseInt(result.getString(1)) == Integer.parseInt(agores[i][1])) {
                        vrethike = true;
                    }
                }
                for (int j = 0; j < enoikiaseis.length; j++) {
                    if (Integer.parseInt(result.getString(1)) == Integer.parseInt(enoikiaseis[j][1])) {
                        vrethike = true;
                    }
                }
                if (!vrethike) {
                    rowCounter++;
                }
            }
            result.beforeFirst();
            return rowCounter;
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {
                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα με το query στο getQueryResultRows method  !!! ");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }
            return 0;
        } catch (Exception ex) {
            error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα με το getQueryResultRows method ξανα προσπαθήστε επικοινωνηστε με τον διαχειριστη !!!");
            System.out.println("Message: " + ex.getMessage());
            return 0;
        }
    }


    /*
     search house me vasi mesiti
     */
    public boolean searchHouseMeVasiMesiti(String at_fsearch, JComboBox lista_houses) {
        lista_houses.removeAllItems();

        if (at_fsearch.length() != 8) {
            error_optionpane.showMessageDialog(null, "Παρακαλω ελενξτε τον αριθμο ταυτοτητας του μεσιτη. ( Σωστο θεωρειται ενας με 8 ακριβως χαρακτηρες !!! )");
        } else {

            try {
                boolean vrethike = false;
                //psaxnoume mesa ston pinaka poy exei ananewthei kata to anoigma toy menou
                //an exei vrethei enas tetoios mesitis
                //oste na mhn ginei tzampa request kai olh h diadikasia auth
                for (int i = 0; i < mesites.length; i++) {
                    if (mesites[i][0].compareTo(at_fsearch) == 0) {
                        vrethike = true;
                    }
                }

                //an den brethike mynhma kai akirwsh diadikasias
                if (!vrethike) {
                    error_optionpane.showMessageDialog(null, "Δεν βρεθηκε μεσιτης με αυτον τον αριθμο ταυτοτητας !!!");
                    return false;
                }

                getAgores(null, 1, 2);
                getEnoikiaseis(null, 1, 2);

                result = Pstatement.executeQuery("Select * from meVasiMesitiHouseSearch('" + at_fsearch + "')"
                );
                //stelnoume to apotelesma sto getQueryResultRows kai mas leei poses grammes exei epistrepsei o server
                //ftiaxnoume etsi kai ton pinaka
                houses = new String[getQueryResultRows(result)][9];
                //index gia to array
                int index = 0;
                //arxizw na pernaw twra to result set
                while (result.next()) {
                    //kataxwrw ola ta pedia apo kathe row sto pinaka houses
                    for (int i = 0; i < 9; i++) {
                        houses[index][i] = result.getString(i + 1);
                    }
                    //vazw 2 pedia pou thelw sto list tou result frame
                    lista_houses.addItem(result.getString("odos") + " " + result.getString("region"));
                    //auksanw deikth
                    index++;
                }

                //an twra to list exei items mesa tote emfanizw to result frame
                if (lista_houses.getItemCount() > 0) {
                    changeResultSearchInfo();

                    return true;
                } //alliws sfalma kai liksi diadikasias
                else {
                    mesites_search_errors.setText("Δεν βρεθηκαν αποτελεσματα με αυτα τα πεδια που ορισατε !!!");

                }

            } catch (SQLException ex) {
                System.out.println("\n -- SQL Exception --- \n");
                while (ex != null) {

                    error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
                    System.out.println("Message: " + ex.getMessage());
                    System.out.println("SQLState: " + ex.getSQLState());
                    System.out.println("ErrorCode: " + ex.getErrorCode());
                    ex = ex.getNextException();
                    System.out.println("");
                }
            }

        }
        return false;
    }

    public boolean searchHouseMeVasiPelati(String at_fsearch, JComboBox lista_houses) {
        lista_houses.removeAllItems();
        if (at_fsearch.length() != 8) {
            error_optionpane.showMessageDialog(null, "Παρακαλω ελενξτε τον αριθμο ταυτοτητας του πελατη. ( Σωστο θεωρειται ενας με 8 ακριβως χαρακτηρες !!! )");
        } else {

            try {
                boolean vrethike = false;
                //psaxnoume mesa ston pinaka poy exei ananewthei kata to anoigma toy menou
                //an exei vrethei enas tetoios mesitis
                //oste na mhn ginei tzampa request kai olh h diadikasia auth
                for (int i = 0; i < pelates.length; i++) {
                    if (pelates[i][0].compareTo(at_fsearch) == 0) {
                        vrethike = true;
                    }
                }

                //an den brethike mynhma kai akirwsh diadikasias
                if (!vrethike) {
                    error_optionpane.showMessageDialog(null, "Δεν βρεθηκε πελατης με αυτον τον αριθμο ταυτοτητας !!!");
                    return false;
                }

                getAgores(null, 1, 2);
                getEnoikiaseis(null, 1, 2);
                getHouses(null, 1, 2);

                String temp[][] = houses;

                int index = 0;
                for (int i = 0; i < houses.length; i++) {
                    vrethike = false;
                    for (int a = 0; a < agores.length; a++) {
                        if (houses[i][0].compareTo(agores[a][1]) == 0 && agores[a][0].compareTo(at_fsearch) == 0) {
                            vrethike = true;
                        }
                    }
                    for (int b = 0; b < enoikiaseis.length; b++) {
                        if (houses[i][0].compareTo(enoikiaseis[b][1]) == 0 && enoikiaseis[b][0].compareTo(at_fsearch) == 0) {
                            vrethike = true;
                        }
                    }
                    if (vrethike) {
                        lista_houses.addItem(houses[i][2] + " " + houses[i][3]);
                        temp[index] = houses[i];
                        index++;
                    }
                }

                houses = getOnlyNotNull(temp);

                //an twra to list exei items mesa tote emfanizw to result frame
                if (lista_houses.getItemCount() > 0) {
                    changeResultSearchInfo();

                    return true;
                } //alliws sfalma kai liksi diadikasias
                else {
                    use_pelates_search_errors.setText("Δεν βρεθηκαν αποτελεσματα με αυτα τα πεδια που ορισατε !!!");

                }

            } catch (Exception ex) {
                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
                System.out.println("Message: " + ex.getMessage());
            }

        }
        return false;
    }
    /*
     custom house search
     */

    public boolean customHouseSearch(String tm_from, String tm_mexri, String domatia_from, String domatia_mexri, String xronia_from, String xronia_mexri, JComboBox lista_me_spitia) {
        try {
            lista_me_spitia.removeAllItems();

            getAgores(null, 1, 2);
            getEnoikiaseis(null, 1, 2);

            result = Pstatement.executeQuery("Select * from customHouseSearch(" + Double.parseDouble(tm_from) + "," + Double.parseDouble(tm_mexri)
                    + "," + Integer.parseInt(domatia_from) + "," + Integer.parseInt(domatia_mexri)
                    + "," + Integer.parseInt(xronia_from) + "," + Integer.parseInt(xronia_mexri) + ")"
            );

            /*
             // kwdikas emfanisis mono twn spitiwn poy einai available
             houses = new String[getAvailableHousesQueryRows(result)][9];

             int index = 0;

             while (result.next()) {

             if (isHouseAvailable(result.getString(1))) {
             for (int i = 0; i < 9; i++) {
             houses[index][i] = result.getString(i + 1);
             }

             lista_me_spitia.addItem(result.getString("odos") + " " + result.getString("region"));
             index++;
             }
             }
             */
            //stelnoume to apotelesma sto getQueryResultRows kai mas leei poses grammes exei epistrepsei o server
            //ftiaxnoume etsi kai ton pinaka
            houses = new String[getQueryResultRows(result)][9];
            //index gia to array
            int index = 0;
            //arxizw na pernaw twra to result set
            while (result.next()) {
                //kataxwrw ola ta pedia apo kathe row sto pinaka houses
                for (int i = 0; i < 9; i++) {
                    houses[index][i] = result.getString(i + 1);
                }
                //vazw 2 pedia pou thelw sto list tou result frame
                lista_me_spitia.addItem(result.getString("odos") + " " + result.getString("region"));
                //auksanw deikth
                index++;
            }
            if (lista_me_spitia.getItemCount() > 0) {
                changeResultSearchInfo();
                return true;
            }

        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception --- \n");
            while (ex != null) {

                error_optionpane.showMessageDialog(null, "Kατι πηγε στραβα ξανα προσπαθήστε !!!");
                System.out.println("Message: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.out.println("");
            }
        }
        return false;
    }

    /*
     Menu help methods
     */
    /*
     allazei to frame twn apotelesmatwn twn spitiwn poy vrethikane
     */
    private void changeResultSearchInfo() {
        //default set
        endiaferetai_result_search_button.setVisible(false);
        display_house_search_result_message.setText("");
        int index = spitia_result.getSelectedIndex();
        tm_result.setText(houses[index][1]);
        region_result.setText(houses[index][2]);
        address_result.setText(houses[index][3]);
        domatia_result.setText(houses[index][6]);
        xronia_result.setText(houses[index][7]);
        mesitis_result.setText(houses[index][8]);

        if (isHouseAvailable(houses[index][0])) {
            if (Integer.parseInt(houses[index][4]) == 1) {
                enoikiasi_result_search_button.setVisible(true);
            } else {
                enoikiasi_result_search_button.setVisible(false);
            }
            if (Integer.parseInt(houses[index][5]) == 1) {
                agora_result_search_button.setVisible(true);
            } else {
                agora_result_search_button.setVisible(false);
            }
            endiaferetai_result_search_button.setVisible(true);
        } else {
            enoikiasi_result_search_button.setVisible(false);
            agora_result_search_button.setVisible(false);
            display_house_search_result_message.setText("To σπιτι δεν ειναι διαθεσιμο για αγορα ή ενοικιαση ! ");
        }

        File file = new File("./pictures/houses/" + houses[index][0] + ".png");
        if (file.exists()) {
            img = new ImageIcon("./pictures/houses/" + houses[index][0] + ".png");
        } else {
            file = new File("./pictures/houses/" + houses[index][0] + ".jpg");
            img = new ImageIcon("./pictures/houses/" + houses[index][0] + ".jpg");
        }
        try {
            BufferedImage bimg = ImageIO.read(file);
            //kanw kathe eikona na einai suitable se 500x500 size label
            Image scaled = bimg.getScaledInstance(500, 500, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaled);
            photo_result.setIcon(icon);

        } catch (Exception e) {
        }
    }

    private void changeSearchForSpitiaUseMesitisInfo() {
        if (search_for_spitia_use_mesiti_mesites_list.getSelectedIndex() < 0) {
            return;
        }
        int index = search_for_spitia_use_mesiti_mesites_list.getSelectedIndex();
        search_at_mesiti.setText(mesites[index][0]);
    }

    private void changeSearchForSpitiaUsePelatisInfo() {
        if (search_for_spitia_use_pelatis_pelates_list.getSelectedIndex() < 0) {
            return;
        }
        int index = search_for_spitia_use_pelatis_pelates_list.getSelectedIndex();
        search_at_pelati.setText(pelates[index][0]);
    }
    /*
     allazei to frame twn spitiwn poy enimerwnontai
     */

    private void changeEditHousesInfo() {
        int index = spitia_lista_edit_house.getSelectedIndex();

        tm_edit_house.setText(houses[index][1]);
        region_edit_house.setText(houses[index][2]);
        address_edit_house.setText(houses[index][3]);
        domatia_edit_house.setText(houses[index][6]);
        xronia_edit_house.setText(houses[index][7]);
        //an uparxei to houses[index][8] kai de exei diagrafei o mesitis tha tethei sto sigekrimeno mesiti h lista
        //alliws d tha ginei tipota de petaei exception h methodos
        mesites_lista_edit_house.setSelectedItem(houses[index][8]);

        if (Integer.parseInt(houses[index][4]) == 1) {
            yes_nikiazeta_edit_house.setSelected(true);
        } else {
            no_noikiazetai_edit_house.setSelected(true);
        }
        if (Integer.parseInt(houses[index][5]) == 1) {
            nai_poleitai_edit_house.setSelected(true);
        } else {
            no_poleita_edit_house.setSelected(true);
        }

        File file = new File("./pictures/houses/" + houses[index][0] + ".png");
        if (file.exists()) {
            img = new ImageIcon("./pictures/houses/" + houses[index][0] + ".png");
        } else {
            file = new File("./pictures/houses/" + houses[index][0] + ".jpg");
            img = new ImageIcon("./pictures/houses/" + houses[index][0] + ".jpg");
        }
        try {
            //kanw kathe eikona na einai suitable se 500x500 size label
            BufferedImage bimg = ImageIO.read(file);
            Image scaled = bimg.getScaledInstance(500, 500, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaled);
            photo_show_edit_house.setIcon(icon);
        } catch (Exception e) {
        }
    }

    /*
     allazei to frame thw enimerwshs twn pliroforiwn twn mesitwn
     */
    private void changeEditMesitisInfo() {
        int index = edit_mesites_list.getSelectedIndex();
        edit_at_mesiti.setText(mesites[index][0]);
        edit_name_mesiti.setText(mesites[index][1]);
        edit_surname_mesiti.setText(mesites[index][2]);
        edit_number_mesiti.setText(mesites[index][3]);
        edit_afm_mesiti.setText(mesites[index][4]);
        edit_address_mesiti.setText(mesites[index][5]);
    }
    /*
     allazei to frame twn pelatwn poy vrethikane
     */

    private void changePelatesEditInfo() {
        int index = pelates_lista_edit_pelatis.getSelectedIndex();
        at_edit_pelatis.setText(pelates[index][0]);
        name_edit_pelatis.setText(pelates[index][1]);
        surname_edit_pelatis.setText(pelates[index][2]);
        number_edit_pelatis.setText(pelates[index][3]);
        afm_edit_pelatis.setText(pelates[index][4]);
        address_edit_pelatis.setText(pelates[index][5]);
    }
    /*
     allazei to frame twn enoikiasewn poy vrethikane
     */

    private void changeEnoikiaseisEditInfo() {
        if (enoikiaseis_list_edit_enoikiaseis.getSelectedIndex() < 0) {
            return;
        }
        int index = enoikiaseis_list_edit_enoikiaseis.getSelectedIndex();
        mesitis_edit_enoikiaseis.setText(enoikiaseis[index][0]);
        house_edit_enoikiaseis.setText(enoikiaseis[index][1]);
        poso_edit_enoikiaseis.setText(enoikiaseis[index][2]);
        iban_edit_enoikiaseis.setText(enoikiaseis[index][3]);
        imera_edit_enoikiaseis.setText(enoikiaseis[index][4]);
    }
    /*
     allazei to frame twn agorwn poy vrethikane
     */

    private void changeAgoresEditInfo() {
        if (agores_list_edit_agores.getSelectedIndex() < 0) {
            return;
        }
        int index = agores_list_edit_agores.getSelectedIndex();
        mesitis_edit_agores.setText(agores[index][0]);
        house_edit_agores.setText(agores[index][1]);
        kostos_edit_agores.setText(agores[index][2]);
        iban_edit_agores.setText(agores[index][3]);
        imera_edit_agores.setText(agores[index][4]);
    }

    /*
     allazei to frame twn endiaferontwn poy vrethikane
     */
    private void changeEndiaferonEditInfo() {
        if (endiaferon_list_edit_endiaferon.getSelectedIndex() < 0) {
            return;
        }
        int index = endiaferon_list_edit_endiaferon.getSelectedIndex();
        pelatis_edit_endiaferon.setText(endiaferon[index][0]);
        house_edit_endiaferon.setText(endiaferon[index][1]);
    }

    /*
     allazei to frame twn stoixeiwn xreoshs enoikoiashs enos spitiou
     */
    public void changeEnoikiasiPelatesXrewshInfo() {
        if (enoikiasi_pelates_list.getSelectedIndex() < 0) {
            return;
        }
        int index = enoikiasi_pelates_list.getSelectedIndex();
        at_enoikiasi.setText(pelates[index][0]);
        onoma_enoikiasi.setText(pelates[index][1]);
        surname_enoikiasi.setText(pelates[index][2]);
    }
    /*
     allazei to frame twn stoixeiwn xreoshs agoras enos spitiou
     */

    public void changeAgoraPelatesXrewshInfo() {
        if (agora_pelates_list.getSelectedIndex() < 0) {
            return;
        }
        int index = agora_pelates_list.getSelectedIndex();
        at_agora.setText(pelates[index][0]);
        onoma_agora.setText(pelates[index][1]);
        surname_agora.setText(pelates[index][2]);
    }
    /*
     thetei tis times sto frame custom search se default egires values
     */

    private void setCustomSearchMenuToDefault() {
        tm_from_cs.setText("1");
        tm_mexri_cs.setText("1000");
        domatia_from_cs.setText("0");
        domatia_mexri_cs.setText("20");
        xronia_from_cs.setText("1900");
        xronia_mexri_cs.setText("" + Calendar.getInstance().get(Calendar.YEAR));
    }

    /*
     katharizei to house insert menu
     */
    private void clearHouseInsertMenu() {
        tm_spitiou.setText("");
        region_spitiou.setText("");
        address_spitiou.setText("");
        domatia_spitiou.setText("");
        xronia_spitiou.setText("");
        picture_spitiou.setText("");
        nai_poleitai_spiti.setSelected(false);
        no_poleitai_spiti.setSelected(false);
        yes_nikiazetai_spiti.setSelected(false);
        no_noikiazetai_spiti.setSelected(false);
    }

    /*
     katharizei to mesitis insert menu
     */
    private void clearMesitisInsertMenu() {
        at_mesiti.setText("");
        name_mesiti.setText("");
        surname_mesiti.setText("");
        number_mesiti.setText("");
        afm_mesiti.setText("");
        address_mesiti.setText("");
    }

    /*
     katharizei to pelatis insert menu
     */
    private void clearPelatisInsertMenu() {
        at_pelati.setText("");
        name_pelati.setText("");
        surname_pelati.setText("");
        number_pelati.setText("");
        afm_pelati.setText("");
        address_pelati.setText("");
    }


    /*
     Extra methods
     */
    /*
     antigrafi tou arxeiou eikonas tou spitiou se periptwsh iparksis delete.exist file
     */
    private void copyFile(File source, File dest) throws IOException {

        if (dest.exists()) {
            /*
             edw einai ena bug
             se periptwsh pou uparxei to arxeio ousiastika kata to update enos house
             tote krataw ena temp tou uparxon house picture giati an o xrhsths 
             paei na valei thn idia tha svhstei h mia kai kata to copy tha dwsei 
             exception 
             */
            File temp = new File("./pictures/houses/temp.jpg");
            Files.copy(dest.toPath(), temp.toPath());

            //epeita aneta kanw to delete
            dest.delete();

            //afou elenksw an akoma uparxei o source tote kanw antigrafi
            //alliws arxika kanw copy ksana
            if (!source.exists()) {
                Files.copy(temp.toPath(), source.toPath());
            }

            //diagrafi tou axristou temp file
            temp.delete();

            //emfanisi minima pros gnwsh gia ton xristi
            message_optionpane.showMessageDialog(null, "Υπηρχε ηδη ενα αρχειο στην κατευθυνση " + dest.getPath() + " !!! Οποτε εχει αντικατασταθει !!!");

        }
        //pleon ginetai h mia entolh pou xreiazetai gia olh authn thn aplh diadikasia
        Files.copy(source.toPath(), dest.toPath());

    }
    /*
     diagrafi enos arxeiou efoson uparxei
     */

    public boolean deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    /*
     de exei oloklhrwthei
     oysiastika tha ekana kataxwrhsh olwn twn byte mias eikonas se bytea type postgres column
     */
    public int getBytesInteger(String path) {
        File file;

        try {
            // All LargeObject API calls must be within a transaction
            // PdbConnection.setAutoCommit(false);

            // Get the Large Object Manager to perform operations with
            LargeObjectManager lobj = ((org.postgresql.PGConnection) PdbConnection).getLargeObjectAPI();

            //create a new large object
            int oid = lobj.create(LargeObjectManager.READ | LargeObjectManager.WRITE);

            //open the large object for write
            LargeObject obj = lobj.open(oid, LargeObjectManager.WRITE);

            // Now open the file
            file = new File(path);
            FileInputStream fis = new FileInputStream(file);

            // copy the data from the file to the large object
            byte buf[] = new byte[2048];
            int s, tl = 0;
            while ((s = fis.read(buf, 0, 2048)) > 0) {
                obj.write(buf, 0, s);
                tl += s;
            }

            // Close the large object
            obj.close();

            house_icon_info[0] = file.getName();
            house_icon_info[1] = oid;
            return oid;
            //Now insert the row into imagesLO

        } catch (Exception e) {
        }
        return -1;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        file_chooser = new javax.swing.JFileChooser();
        home_page = new javax.swing.JFrame();
        jPanel1 = new javax.swing.JPanel();
        logo = new javax.swing.JLabel();
        kataxorisi_button = new javax.swing.JButton();
        anazitisi_button = new javax.swing.JButton();
        agora_button = new javax.swing.JButton();
        kataxorisi_menou = new javax.swing.JFrame();
        kataxorisi_spitioy_button = new javax.swing.JButton();
        kataxorisi_pelati_button = new javax.swing.JButton();
        kataxorisi_mesiti_button = new javax.swing.JButton();
        anazitisi_menou = new javax.swing.JFrame();
        me_vasi_pedia_search_button = new javax.swing.JButton();
        me_vasi_mesitwn_search_button = new javax.swing.JButton();
        me_vasi_pelatwn_search_button = new javax.swing.JButton();
        edit_menu = new javax.swing.JFrame();
        edit_meni_mesitis_button = new javax.swing.JButton();
        edit_meni_houses_button = new javax.swing.JButton();
        edit_meni_pelates_button = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        mesites_edit = new javax.swing.JFrame();
        edit_mesites_list = new javax.swing.JComboBox();
        jLabel51 = new javax.swing.JLabel();
        edit_afm_mesiti = new javax.swing.JTextField();
        edit_number_mesiti = new javax.swing.JTextField();
        edit_surname_mesiti = new javax.swing.JTextField();
        edit_name_mesiti = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        edit_at_mesiti = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        edit_mesitis_save_button = new javax.swing.JButton();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        edit_address_mesiti = new javax.swing.JTextField();
        jLabel57 = new javax.swing.JLabel();
        edit_mesitis_delete_button = new javax.swing.JButton();
        houses_edit = new javax.swing.JFrame();
        jLabel58 = new javax.swing.JLabel();
        spitia_lista_edit_house = new javax.swing.JComboBox();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        no_poleita_edit_house = new javax.swing.JRadioButton();
        tm_edit_house = new javax.swing.JTextField();
        nai_poleitai_edit_house = new javax.swing.JRadioButton();
        xronia_edit_house = new javax.swing.JTextField();
        jLabel62 = new javax.swing.JLabel();
        region_edit_house = new javax.swing.JTextField();
        domatia_edit_house = new javax.swing.JTextField();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        address_edit_house = new javax.swing.JTextField();
        edit_house_save_button = new javax.swing.JButton();
        jLabel65 = new javax.swing.JLabel();
        picture_chooser_edit_house_button = new javax.swing.JButton();
        yes_nikiazeta_edit_house = new javax.swing.JRadioButton();
        picture_edit_house = new javax.swing.JTextField();
        jLabel66 = new javax.swing.JLabel();
        no_noikiazetai_edit_house = new javax.swing.JRadioButton();
        mesites_lista_edit_house = new javax.swing.JComboBox();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        edit_house_delete_button = new javax.swing.JButton();
        photo_show_edit_house = new javax.swing.JLabel();
        pelates_edit = new javax.swing.JFrame();
        jLabel69 = new javax.swing.JLabel();
        pelates_lista_edit_pelatis = new javax.swing.JComboBox();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        afm_edit_pelatis = new javax.swing.JTextField();
        at_edit_pelatis = new javax.swing.JTextField();
        jLabel72 = new javax.swing.JLabel();
        address_edit_pelatis = new javax.swing.JTextField();
        edit_pelatis_save_button = new javax.swing.JButton();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        name_edit_pelatis = new javax.swing.JTextField();
        number_edit_pelatis = new javax.swing.JTextField();
        surname_edit_pelatis = new javax.swing.JTextField();
        edit_pelatis_delete_button = new javax.swing.JButton();
        enoikiaseis_edit = new javax.swing.JFrame();
        jLabel92 = new javax.swing.JLabel();
        enoikiaseis_list_edit_enoikiaseis = new javax.swing.JComboBox();
        jLabel93 = new javax.swing.JLabel();
        house_edit_enoikiaseis = new javax.swing.JTextField();
        jLabel94 = new javax.swing.JLabel();
        mesitis_edit_enoikiaseis = new javax.swing.JTextField();
        poso_edit_enoikiaseis = new javax.swing.JTextField();
        jLabel95 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        iban_edit_enoikiaseis = new javax.swing.JTextField();
        jLabel97 = new javax.swing.JLabel();
        imera_edit_enoikiaseis = new javax.swing.JTextField();
        edit_enoikiaseis_save_button = new javax.swing.JButton();
        edit_enoikiaseis_delete_button = new javax.swing.JButton();
        agores_edit = new javax.swing.JFrame();
        mesitis_edit_agores = new javax.swing.JTextField();
        kostos_edit_agores = new javax.swing.JTextField();
        house_edit_agores = new javax.swing.JTextField();
        jLabel98 = new javax.swing.JLabel();
        edit_agores_delete_button = new javax.swing.JButton();
        jLabel99 = new javax.swing.JLabel();
        agores_list_edit_agores = new javax.swing.JComboBox();
        edit_agores_save_button = new javax.swing.JButton();
        imera_edit_agores = new javax.swing.JTextField();
        jLabel100 = new javax.swing.JLabel();
        iban_edit_agores = new javax.swing.JTextField();
        jLabel101 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        oracle_configs = new javax.swing.JFrame();
        save_oracle_configs_button = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        user_oracle = new javax.swing.JTextField();
        service_oracle = new javax.swing.JTextField();
        pass_oracle = new javax.swing.JPasswordField();
        jLabel7 = new javax.swing.JLabel();
        server_oracle = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        port_oracle = new javax.swing.JTextField();
        postgresql_configs = new javax.swing.JFrame();
        jLabel4 = new javax.swing.JLabel();
        save_postgresql_configs_button = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        user_postgresql = new javax.swing.JTextField();
        service_postgresql = new javax.swing.JTextField();
        pass_postgresql = new javax.swing.JPasswordField();
        jLabel9 = new javax.swing.JLabel();
        server_postgresql = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        port_postgresql = new javax.swing.JTextField();
        error_optionpane = new javax.swing.JOptionPane();
        message_optionpane = new javax.swing.JOptionPane();
        kataxorisi_mesiti = new javax.swing.JFrame();
        jLabel11 = new javax.swing.JLabel();
        at_mesiti = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        name_mesiti = new javax.swing.JTextField();
        surname_mesiti = new javax.swing.JTextField();
        number_mesiti = new javax.swing.JTextField();
        afm_mesiti = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        address_mesiti = new javax.swing.JTextField();
        teliki_kataxwrisi_mesiti_button = new javax.swing.JButton();
        kataxorisi_spitiou = new javax.swing.JFrame();
        jLabel23 = new javax.swing.JLabel();
        tm_spitiou = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        region_spitiou = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        address_spitiou = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        yes_nikiazetai_spiti = new javax.swing.JRadioButton();
        no_noikiazetai_spiti = new javax.swing.JRadioButton();
        jLabel27 = new javax.swing.JLabel();
        nai_poleitai_spiti = new javax.swing.JRadioButton();
        no_poleitai_spiti = new javax.swing.JRadioButton();
        jLabel28 = new javax.swing.JLabel();
        domatia_spitiou = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        xronia_spitiou = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        mesites_lista_spitiou = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        picture_chooser_button = new javax.swing.JButton();
        picture_spitiou = new javax.swing.JTextField();
        kataxorisi_pelati = new javax.swing.JFrame();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        teliki_kataxwrisi_pelati_button = new javax.swing.JButton();
        address_pelati = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        at_pelati = new javax.swing.JTextField();
        afm_pelati = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        name_pelati = new javax.swing.JTextField();
        surname_pelati = new javax.swing.JTextField();
        number_pelati = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        custom_search = new javax.swing.JFrame();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        tm_from_cs = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        tm_mexri_cs = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        domatia_mexri_cs = new javax.swing.JTextField();
        domatia_from_cs = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        xronia_mexri_cs = new javax.swing.JTextField();
        xronia_from_cs = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        error_cs = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        search_results = new javax.swing.JFrame();
        spitia_result = new javax.swing.JComboBox();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        photo_result = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        tm_result = new javax.swing.JTextField();
        region_result = new javax.swing.JTextField();
        address_result = new javax.swing.JTextField();
        domatia_result = new javax.swing.JTextField();
        xronia_result = new javax.swing.JTextField();
        mesitis_result = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        enoikiasi_result_search_button = new javax.swing.JButton();
        agora_result_search_button = new javax.swing.JButton();
        display_house_search_result_message = new javax.swing.JLabel();
        endiaferetai_result_search_button = new javax.swing.JButton();
        endiaferthikan_result_search_button = new javax.swing.JButton();
        search_me_vasi_mesiti = new javax.swing.JFrame();
        jLabel46 = new javax.swing.JLabel();
        search_at_mesiti = new javax.swing.JTextField();
        search_spitia_mesiti_button = new javax.swing.JButton();
        search_for_spitia_use_mesiti_mesites_list = new javax.swing.JComboBox();
        jLabel48 = new javax.swing.JLabel();
        mesites_search_errors = new javax.swing.JLabel();
        search_me_vasi_pelati = new javax.swing.JFrame();
        jLabel90 = new javax.swing.JLabel();
        search_at_pelati = new javax.swing.JTextField();
        search_spitia_use_pelatis_button = new javax.swing.JButton();
        search_for_spitia_use_pelatis_pelates_list = new javax.swing.JComboBox();
        jLabel91 = new javax.swing.JLabel();
        use_pelates_search_errors = new javax.swing.JLabel();
        enoikiasi = new javax.swing.JFrame();
        jLabel76 = new javax.swing.JLabel();
        enoikiasi_pelates_list = new javax.swing.JComboBox();
        jLabel77 = new javax.swing.JLabel();
        poso_enoikiasi = new javax.swing.JTextField();
        jLabel78 = new javax.swing.JLabel();
        iban_enoikiasi = new javax.swing.JTextField();
        jLabel79 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel80 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        enoikiasi_spitiou_button = new javax.swing.JButton();
        at_enoikiasi = new javax.swing.JTextField();
        onoma_enoikiasi = new javax.swing.JTextField();
        surname_enoikiasi = new javax.swing.JTextField();
        agora = new javax.swing.JFrame();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel83 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        agora_spitiou_button = new javax.swing.JButton();
        at_agora = new javax.swing.JTextField();
        poso_agora = new javax.swing.JTextField();
        onoma_agora = new javax.swing.JTextField();
        jLabel87 = new javax.swing.JLabel();
        surname_agora = new javax.swing.JTextField();
        agora_pelates_list = new javax.swing.JComboBox();
        jLabel88 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        iban_agora = new javax.swing.JTextField();
        configs_manager = new javax.swing.JFrame();
        jPanel2 = new javax.swing.JPanel();
        oraclelogo = new javax.swing.JLabel();
        oracle_configs_button = new javax.swing.JButton();
        postgresqllogo = new javax.swing.JLabel();
        postgresql_configs_button = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        aboutDialog = new javax.swing.JDialog();
        jScrollPane2 = new javax.swing.JScrollPane();
        aboutTextArea = new javax.swing.JTextArea();
        endiaferon_edit = new javax.swing.JFrame();
        jLabel105 = new javax.swing.JLabel();
        endiaferon_list_edit_endiaferon = new javax.swing.JComboBox();
        jLabel106 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        pelatis_edit_endiaferon = new javax.swing.JTextField();
        house_edit_endiaferon = new javax.swing.JTextField();
        jLabel107 = new javax.swing.JLabel();
        kataxorisi_endiaferon = new javax.swing.JFrame();
        jLabel108 = new javax.swing.JLabel();
        endiaferon_list_kataxorisi_endiaferon = new javax.swing.JComboBox();
        jButton11 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        connect_database_button = new javax.swing.JButton();
        jLabel104 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();

        home_page.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        home_page.setMinimumSize(new java.awt.Dimension(850, 350));
        home_page.setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 153));

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/mesitiko.png"))); // NOI18N

        kataxorisi_button.setText("Καταχωρηση");
        kataxorisi_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kataxorisi_buttonActionPerformed(evt);
            }
        });

        anazitisi_button.setText("Αναζητηση");
        anazitisi_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                anazitisi_buttonActionPerformed(evt);
            }
        });

        agora_button.setText("Επεξεργασια");
        agora_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agora_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(kataxorisi_button, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(100, 100, 100)
                .addComponent(agora_button, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(93, 93, 93)
                .addComponent(anazitisi_button, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(44, Short.MAX_VALUE)
                .addComponent(logo, javax.swing.GroupLayout.PREFERRED_SIZE, 792, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(logo, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 113, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(kataxorisi_button, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(agora_button, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(anazitisi_button, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(103, 103, 103))
        );

        javax.swing.GroupLayout home_pageLayout = new javax.swing.GroupLayout(home_page.getContentPane());
        home_page.getContentPane().setLayout(home_pageLayout);
        home_pageLayout.setHorizontalGroup(
            home_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, home_pageLayout.createSequentialGroup()
                .addContainerGap(89, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        home_pageLayout.setVerticalGroup(
            home_pageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, home_pageLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        kataxorisi_menou.setResizable(false);

        kataxorisi_spitioy_button.setText("Καταχωρηση Σπιτιου");
        kataxorisi_spitioy_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kataxorisi_spitioy_buttonActionPerformed(evt);
            }
        });

        kataxorisi_pelati_button.setText("Καταχωρηση Πελατη");
        kataxorisi_pelati_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kataxorisi_pelati_buttonActionPerformed(evt);
            }
        });

        kataxorisi_mesiti_button.setText("Καταχωρηση Μεσιτη");
        kataxorisi_mesiti_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kataxorisi_mesiti_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout kataxorisi_menouLayout = new javax.swing.GroupLayout(kataxorisi_menou.getContentPane());
        kataxorisi_menou.getContentPane().setLayout(kataxorisi_menouLayout);
        kataxorisi_menouLayout.setHorizontalGroup(
            kataxorisi_menouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kataxorisi_menouLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(kataxorisi_menouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(kataxorisi_spitioy_button)
                    .addComponent(kataxorisi_pelati_button)
                    .addComponent(kataxorisi_mesiti_button))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        kataxorisi_menouLayout.setVerticalGroup(
            kataxorisi_menouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kataxorisi_menouLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(kataxorisi_mesiti_button, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(kataxorisi_spitioy_button)
                .addGap(18, 18, 18)
                .addComponent(kataxorisi_pelati_button)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        anazitisi_menou.setResizable(false);

        me_vasi_pedia_search_button.setText("ΜΕ ΒΑΣΗ ΠΕΔΙΑ");
        me_vasi_pedia_search_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                me_vasi_pedia_search_buttonActionPerformed(evt);
            }
        });

        me_vasi_mesitwn_search_button.setText("ΜΕ ΒΑΣΗ ΜΕΣΙΤΩΝ");
        me_vasi_mesitwn_search_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                me_vasi_mesitwn_search_buttonActionPerformed(evt);
            }
        });

        me_vasi_pelatwn_search_button.setText("ΜΕ ΒΑΣΗ ΠΕΛΑΤΩΝ");
        me_vasi_pelatwn_search_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                me_vasi_pelatwn_search_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout anazitisi_menouLayout = new javax.swing.GroupLayout(anazitisi_menou.getContentPane());
        anazitisi_menou.getContentPane().setLayout(anazitisi_menouLayout);
        anazitisi_menouLayout.setHorizontalGroup(
            anazitisi_menouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(anazitisi_menouLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(anazitisi_menouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(me_vasi_pedia_search_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(me_vasi_mesitwn_search_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(me_vasi_pelatwn_search_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        anazitisi_menouLayout.setVerticalGroup(
            anazitisi_menouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(anazitisi_menouLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(me_vasi_pedia_search_button)
                .addGap(18, 18, 18)
                .addComponent(me_vasi_mesitwn_search_button)
                .addGap(18, 18, 18)
                .addComponent(me_vasi_pelatwn_search_button)
                .addContainerGap(56, Short.MAX_VALUE))
        );

        edit_menu.setResizable(false);

        edit_meni_mesitis_button.setText("Μεσιτες");
        edit_meni_mesitis_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_meni_mesitis_buttonActionPerformed(evt);
            }
        });

        edit_meni_houses_button.setText("Σπιτια");
        edit_meni_houses_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_meni_houses_buttonActionPerformed(evt);
            }
        });

        edit_meni_pelates_button.setText("Πελατες");
        edit_meni_pelates_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_meni_pelates_buttonActionPerformed(evt);
            }
        });

        jButton2.setText("Ενοικιασεις");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Αγορες");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout edit_menuLayout = new javax.swing.GroupLayout(edit_menu.getContentPane());
        edit_menu.getContentPane().setLayout(edit_menuLayout);
        edit_menuLayout.setHorizontalGroup(
            edit_menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(edit_menuLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(edit_menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                    .addComponent(edit_meni_pelates_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(edit_meni_houses_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(edit_meni_mesitis_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        edit_menuLayout.setVerticalGroup(
            edit_menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(edit_menuLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(edit_meni_mesitis_button)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(edit_meni_houses_button)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(edit_meni_pelates_button)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3)
                .addContainerGap(67, Short.MAX_VALUE))
        );

        edit_mesites_list.setModel(new javax.swing.DefaultComboBoxModel());
        edit_mesites_list.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                edit_mesites_listItemStateChanged(evt);
            }
        });

        jLabel51.setText("Μεσιτες");

        jLabel52.setText("ΑΦΜ");

        jLabel53.setText("Ονομα");

        edit_mesitis_save_button.setText("Αποθηκευση");
        edit_mesitis_save_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_mesitis_save_buttonActionPerformed(evt);
            }
        });

        jLabel54.setText("Επωνυμο");

        jLabel55.setText("Τηλεφωνο");

        jLabel56.setText("Διευθυνση");

        jLabel57.setText("Αριθμος Ταυτοτητας");

        edit_mesitis_delete_button.setText("Διαγραφη");
        edit_mesitis_delete_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_mesitis_delete_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mesites_editLayout = new javax.swing.GroupLayout(mesites_edit.getContentPane());
        mesites_edit.getContentPane().setLayout(mesites_editLayout);
        mesites_editLayout.setHorizontalGroup(
            mesites_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mesites_editLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(mesites_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mesites_editLayout.createSequentialGroup()
                        .addComponent(edit_mesitis_save_button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(edit_mesitis_delete_button))
                    .addGroup(mesites_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(mesites_editLayout.createSequentialGroup()
                            .addComponent(jLabel56)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(edit_address_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(mesites_editLayout.createSequentialGroup()
                            .addGroup(mesites_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel57)
                                .addComponent(jLabel53)
                                .addComponent(jLabel54)
                                .addComponent(jLabel55)
                                .addComponent(jLabel52))
                            .addGap(58, 58, 58)
                            .addGroup(mesites_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(edit_afm_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(edit_number_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(edit_surname_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(edit_name_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(edit_at_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(mesites_editLayout.createSequentialGroup()
                        .addComponent(jLabel51)
                        .addGap(50, 50, 50)
                        .addComponent(edit_mesites_list, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        mesites_editLayout.setVerticalGroup(
            mesites_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mesites_editLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mesites_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(edit_mesites_list, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51))
                .addGap(51, 51, 51)
                .addGroup(mesites_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel57)
                    .addComponent(edit_at_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mesites_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel53)
                    .addComponent(edit_name_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mesites_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54)
                    .addComponent(edit_surname_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mesites_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel55)
                    .addComponent(edit_number_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mesites_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52)
                    .addComponent(edit_afm_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mesites_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56)
                    .addComponent(edit_address_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mesites_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(edit_mesitis_save_button)
                    .addComponent(edit_mesitis_delete_button))
                .addContainerGap(57, Short.MAX_VALUE))
        );

        jLabel58.setText("Σπιτια");

        spitia_lista_edit_house.setModel(new javax.swing.DefaultComboBoxModel());
        spitia_lista_edit_house.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                spitia_lista_edit_houseItemStateChanged(evt);
            }
        });

        jLabel59.setText("Tετραγωνικα Μετρα");

        jLabel60.setText("Δωματια");

        jLabel61.setText("Περιοχη");

        buttonGroup2.add(no_poleita_edit_house);
        no_poleita_edit_house.setText("Οχι");

        buttonGroup2.add(nai_poleitai_edit_house);
        nai_poleitai_edit_house.setText("Ναι");

        jLabel62.setText("Μεσιτης");

        jLabel63.setText("Διευθυνση");

        jLabel64.setText("Χρονια");

        edit_house_save_button.setText("Αποθηκευση");
        edit_house_save_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_house_save_buttonActionPerformed(evt);
            }
        });

        jLabel65.setText("Ενοικιαζεται");

        picture_chooser_edit_house_button.setText("Επιλογη");
        picture_chooser_edit_house_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                picture_chooser_edit_house_buttonActionPerformed(evt);
            }
        });

        buttonGroup1.add(yes_nikiazeta_edit_house);
        yes_nikiazeta_edit_house.setText("Ναι");

        picture_edit_house.setEditable(false);

        jLabel66.setText("Φωτογραφια");

        buttonGroup1.add(no_noikiazetai_edit_house);
        no_noikiazetai_edit_house.setText("Οχι");

        jLabel67.setText("Πωλειται");

        jLabel68.setText("Φωτογραφια");

        edit_house_delete_button.setText("Διαγραφη");
        edit_house_delete_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_house_delete_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout houses_editLayout = new javax.swing.GroupLayout(houses_edit.getContentPane());
        houses_edit.getContentPane().setLayout(houses_editLayout);
        houses_editLayout.setHorizontalGroup(
            houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(houses_editLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(houses_editLayout.createSequentialGroup()
                        .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(108, 108, 108)
                        .addComponent(spitia_lista_edit_house, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(437, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, houses_editLayout.createSequentialGroup()
                        .addGroup(houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(houses_editLayout.createSequentialGroup()
                                .addComponent(edit_house_save_button)
                                .addGap(18, 18, 18)
                                .addComponent(edit_house_delete_button))
                            .addGroup(houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(houses_editLayout.createSequentialGroup()
                                        .addComponent(jLabel59)
                                        .addGap(44, 44, 44)
                                        .addComponent(tm_edit_house, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(houses_editLayout.createSequentialGroup()
                                        .addComponent(jLabel61)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(region_edit_house, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, houses_editLayout.createSequentialGroup()
                                        .addGroup(houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel63)
                                            .addComponent(jLabel65))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(address_edit_house, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(houses_editLayout.createSequentialGroup()
                                                .addComponent(yes_nikiazeta_edit_house)
                                                .addGap(18, 18, 18)
                                                .addComponent(no_noikiazetai_edit_house))
                                            .addGroup(houses_editLayout.createSequentialGroup()
                                                .addComponent(nai_poleitai_edit_house)
                                                .addGap(18, 18, 18)
                                                .addComponent(no_poleita_edit_house))
                                            .addComponent(domatia_edit_house, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jLabel67)
                                    .addComponent(jLabel60)
                                    .addComponent(jLabel64))
                                .addGroup(houses_editLayout.createSequentialGroup()
                                    .addGroup(houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(houses_editLayout.createSequentialGroup()
                                            .addComponent(jLabel66)
                                            .addGap(78, 78, 78))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, houses_editLayout.createSequentialGroup()
                                            .addComponent(jLabel62)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                    .addGroup(houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(picture_chooser_edit_house_button)
                                        .addGroup(houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(xronia_edit_house)
                                            .addComponent(picture_edit_house, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(mesites_lista_edit_house, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel68)
                            .addComponent(photo_show_edit_house, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(36, 36, 36))))
        );
        houses_editLayout.setVerticalGroup(
            houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(houses_editLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel58)
                    .addComponent(spitia_lista_edit_house, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(jLabel68)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(houses_editLayout.createSequentialGroup()
                        .addGroup(houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel59)
                            .addComponent(tm_edit_house, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel61)
                            .addComponent(region_edit_house, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel63)
                            .addComponent(address_edit_house, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel65)
                            .addComponent(yes_nikiazeta_edit_house)
                            .addComponent(no_noikiazetai_edit_house))
                        .addGap(18, 18, 18)
                        .addGroup(houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel67)
                            .addComponent(nai_poleitai_edit_house)
                            .addComponent(no_poleita_edit_house))
                        .addGap(18, 18, 18)
                        .addGroup(houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel60)
                            .addComponent(domatia_edit_house, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel64)
                            .addComponent(xronia_edit_house, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel66)
                            .addComponent(picture_edit_house, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(picture_chooser_edit_house_button)
                        .addGap(18, 18, 18)
                        .addGroup(houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(mesites_lista_edit_house, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel62))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                        .addGroup(houses_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(edit_house_save_button)
                            .addComponent(edit_house_delete_button))
                        .addGap(86, 86, 86))
                    .addGroup(houses_editLayout.createSequentialGroup()
                        .addComponent(photo_show_edit_house, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jLabel69.setText("Πελατες");

        pelates_lista_edit_pelatis.setModel(new javax.swing.DefaultComboBoxModel());
        pelates_lista_edit_pelatis.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                pelates_lista_edit_pelatisItemStateChanged(evt);
            }
        });

        jLabel70.setText("ΑΦΜ");

        jLabel71.setText("Αριθμος Ταυτοτητας");

        jLabel72.setText("Διευθυνση");

        edit_pelatis_save_button.setText("Αποθηκευση");
        edit_pelatis_save_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_pelatis_save_buttonActionPerformed(evt);
            }
        });

        jLabel73.setText("Ονομα");

        jLabel74.setText("Επωνυμο");

        jLabel75.setText("Τηλεφωνο");

        edit_pelatis_delete_button.setText("Διαγραφη");
        edit_pelatis_delete_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_pelatis_delete_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pelates_editLayout = new javax.swing.GroupLayout(pelates_edit.getContentPane());
        pelates_edit.getContentPane().setLayout(pelates_editLayout);
        pelates_editLayout.setHorizontalGroup(
            pelates_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pelates_editLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(pelates_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pelates_editLayout.createSequentialGroup()
                        .addComponent(edit_pelatis_save_button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(edit_pelatis_delete_button))
                    .addGroup(pelates_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(pelates_editLayout.createSequentialGroup()
                            .addComponent(jLabel72)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(address_edit_pelatis, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(pelates_editLayout.createSequentialGroup()
                            .addGroup(pelates_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel71)
                                .addComponent(jLabel73)
                                .addComponent(jLabel74)
                                .addComponent(jLabel75)
                                .addComponent(jLabel70))
                            .addGap(58, 58, 58)
                            .addGroup(pelates_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(afm_edit_pelatis, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(number_edit_pelatis, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(surname_edit_pelatis, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(name_edit_pelatis, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(at_edit_pelatis, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(pelates_editLayout.createSequentialGroup()
                        .addComponent(jLabel69)
                        .addGap(60, 60, 60)
                        .addComponent(pelates_lista_edit_pelatis, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(62, Short.MAX_VALUE))
        );
        pelates_editLayout.setVerticalGroup(
            pelates_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pelates_editLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pelates_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel69)
                    .addComponent(pelates_lista_edit_pelatis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(pelates_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel71)
                    .addComponent(at_edit_pelatis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pelates_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel73)
                    .addComponent(name_edit_pelatis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pelates_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel74)
                    .addComponent(surname_edit_pelatis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pelates_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel75)
                    .addComponent(number_edit_pelatis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pelates_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel70)
                    .addComponent(afm_edit_pelatis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pelates_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel72)
                    .addComponent(address_edit_pelatis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pelates_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(edit_pelatis_save_button)
                    .addComponent(edit_pelatis_delete_button))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        jLabel92.setText("Ενοικιασεις");

        enoikiaseis_list_edit_enoikiaseis.setModel(new javax.swing.DefaultComboBoxModel());
        enoikiaseis_list_edit_enoikiaseis.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                enoikiaseis_list_edit_enoikiaseisItemStateChanged(evt);
            }
        });

        jLabel93.setText("Σπιτι");

        house_edit_enoikiaseis.setEditable(false);

        jLabel94.setText("Μεσιτης");

        mesitis_edit_enoikiaseis.setEditable(false);

        poso_edit_enoikiaseis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                poso_edit_enoikiaseisActionPerformed(evt);
            }
        });

        jLabel95.setText("Ποσο Μηνα");

        jLabel96.setText("IBAN");

        iban_edit_enoikiaseis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iban_edit_enoikiaseisActionPerformed(evt);
            }
        });

        jLabel97.setText("Ημερα");

        imera_edit_enoikiaseis.setEditable(false);
        imera_edit_enoikiaseis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imera_edit_enoikiaseisActionPerformed(evt);
            }
        });

        edit_enoikiaseis_save_button.setText("Aποθηκευση");
        edit_enoikiaseis_save_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_enoikiaseis_save_buttonActionPerformed(evt);
            }
        });

        edit_enoikiaseis_delete_button.setText("Διαγραφη");
        edit_enoikiaseis_delete_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_enoikiaseis_delete_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout enoikiaseis_editLayout = new javax.swing.GroupLayout(enoikiaseis_edit.getContentPane());
        enoikiaseis_edit.getContentPane().setLayout(enoikiaseis_editLayout);
        enoikiaseis_editLayout.setHorizontalGroup(
            enoikiaseis_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(enoikiaseis_editLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(enoikiaseis_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(enoikiaseis_editLayout.createSequentialGroup()
                        .addGroup(enoikiaseis_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel97, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel96, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel95, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel92, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel94, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel93, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(67, 67, 67)
                        .addGroup(enoikiaseis_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mesitis_edit_enoikiaseis, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(enoikiaseis_list_edit_enoikiaseis, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(house_edit_enoikiaseis, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(poso_edit_enoikiaseis, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(iban_edit_enoikiaseis, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(imera_edit_enoikiaseis, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(enoikiaseis_editLayout.createSequentialGroup()
                        .addComponent(edit_enoikiaseis_save_button)
                        .addGap(18, 18, 18)
                        .addComponent(edit_enoikiaseis_delete_button)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        enoikiaseis_editLayout.setVerticalGroup(
            enoikiaseis_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(enoikiaseis_editLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(enoikiaseis_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel92)
                    .addComponent(enoikiaseis_list_edit_enoikiaseis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(enoikiaseis_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel93)
                    .addComponent(house_edit_enoikiaseis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(enoikiaseis_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel94)
                    .addComponent(mesitis_edit_enoikiaseis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(enoikiaseis_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel95)
                    .addComponent(poso_edit_enoikiaseis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(enoikiaseis_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel96)
                    .addComponent(iban_edit_enoikiaseis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(enoikiaseis_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel97)
                    .addComponent(imera_edit_enoikiaseis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addGroup(enoikiaseis_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(edit_enoikiaseis_save_button)
                    .addComponent(edit_enoikiaseis_delete_button))
                .addContainerGap(130, Short.MAX_VALUE))
        );

        mesitis_edit_agores.setEditable(false);

        kostos_edit_agores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kostos_edit_agoresActionPerformed(evt);
            }
        });

        house_edit_agores.setEditable(false);

        jLabel98.setText("Μεσιτης");

        edit_agores_delete_button.setText("Διαγραφη");
        edit_agores_delete_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_agores_delete_buttonActionPerformed(evt);
            }
        });

        jLabel99.setText("Σπιτι");

        agores_list_edit_agores.setModel(new javax.swing.DefaultComboBoxModel());
        agores_list_edit_agores.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                agores_list_edit_agoresItemStateChanged(evt);
            }
        });

        edit_agores_save_button.setText("Aποθηκευση");
        edit_agores_save_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_agores_save_buttonActionPerformed(evt);
            }
        });

        imera_edit_agores.setEditable(false);
        imera_edit_agores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imera_edit_agoresActionPerformed(evt);
            }
        });

        jLabel100.setText("Ημερα");

        iban_edit_agores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iban_edit_agoresActionPerformed(evt);
            }
        });

        jLabel101.setText("IBAN");

        jLabel102.setText("Αγορες");

        jLabel103.setText("Κοστος");

        javax.swing.GroupLayout agores_editLayout = new javax.swing.GroupLayout(agores_edit.getContentPane());
        agores_edit.getContentPane().setLayout(agores_editLayout);
        agores_editLayout.setHorizontalGroup(
            agores_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, agores_editLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(agores_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(agores_editLayout.createSequentialGroup()
                        .addGroup(agores_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel100, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel101, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel103, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel102, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel98, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel99, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(67, 67, 67)
                        .addGroup(agores_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mesitis_edit_agores, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(agores_list_edit_agores, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(house_edit_agores, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(kostos_edit_agores, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(iban_edit_agores, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(imera_edit_agores, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(agores_editLayout.createSequentialGroup()
                        .addComponent(edit_agores_save_button)
                        .addGap(18, 18, 18)
                        .addComponent(edit_agores_delete_button)))
                .addContainerGap())
        );
        agores_editLayout.setVerticalGroup(
            agores_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(agores_editLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(agores_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel102)
                    .addComponent(agores_list_edit_agores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(agores_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel99)
                    .addComponent(house_edit_agores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(agores_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel98)
                    .addComponent(mesitis_edit_agores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(agores_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel103)
                    .addComponent(kostos_edit_agores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(agores_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel101)
                    .addComponent(iban_edit_agores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(agores_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel100)
                    .addComponent(imera_edit_agores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addGroup(agores_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(edit_agores_save_button)
                    .addComponent(edit_agores_delete_button))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        oracle_configs.setResizable(false);

        save_oracle_configs_button.setText("Save");
        save_oracle_configs_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_oracle_configs_buttonActionPerformed(evt);
            }
        });

        jLabel1.setText("Username");

        jLabel2.setText("Password");

        jLabel3.setText("Schema");

        service_oracle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                service_oracleActionPerformed(evt);
            }
        });

        jLabel7.setText("Server");

        jLabel8.setText("Port");

        javax.swing.GroupLayout oracle_configsLayout = new javax.swing.GroupLayout(oracle_configs.getContentPane());
        oracle_configs.getContentPane().setLayout(oracle_configsLayout);
        oracle_configsLayout.setHorizontalGroup(
            oracle_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(oracle_configsLayout.createSequentialGroup()
                .addGroup(oracle_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(oracle_configsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(save_oracle_configs_button, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(oracle_configsLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(oracle_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(oracle_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(63, 63, 63)
                        .addGroup(oracle_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(user_oracle, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(service_oracle)
                            .addComponent(pass_oracle)
                            .addComponent(server_oracle)
                            .addComponent(port_oracle, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE))))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        oracle_configsLayout.setVerticalGroup(
            oracle_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, oracle_configsLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(oracle_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(user_oracle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(oracle_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(pass_oracle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(oracle_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(service_oracle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(oracle_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(server_oracle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(oracle_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(port_oracle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(save_oracle_configs_button)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        postgresql_configs.setResizable(false);

        jLabel4.setText("Username");

        save_postgresql_configs_button.setText("Save");
        save_postgresql_configs_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_postgresql_configs_buttonActionPerformed(evt);
            }
        });

        jLabel5.setText("Password");

        jLabel6.setText("Schema");

        service_postgresql.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                service_postgresqlActionPerformed(evt);
            }
        });

        jLabel9.setText("Server");

        jLabel10.setText("Port");

        javax.swing.GroupLayout postgresql_configsLayout = new javax.swing.GroupLayout(postgresql_configs.getContentPane());
        postgresql_configs.getContentPane().setLayout(postgresql_configsLayout);
        postgresql_configsLayout.setHorizontalGroup(
            postgresql_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(postgresql_configsLayout.createSequentialGroup()
                .addGroup(postgresql_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(postgresql_configsLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(postgresql_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(postgresql_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(postgresql_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(77, 77, 77)
                        .addGroup(postgresql_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(user_postgresql, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                            .addComponent(service_postgresql)
                            .addComponent(pass_postgresql, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(server_postgresql)
                            .addComponent(port_postgresql, javax.swing.GroupLayout.Alignment.LEADING)))
                    .addGroup(postgresql_configsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(save_postgresql_configs_button, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        postgresql_configsLayout.setVerticalGroup(
            postgresql_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, postgresql_configsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(postgresql_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(user_postgresql, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(postgresql_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(pass_postgresql, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(postgresql_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(service_postgresql, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(postgresql_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(server_postgresql, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(postgresql_configsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(port_postgresql, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(18, 18, 18)
                .addComponent(save_postgresql_configs_button)
                .addGap(0, 11, Short.MAX_VALUE))
        );

        jLabel11.setText("Αριθμος Ταυτοτητας");

        jLabel12.setText("Ονομα");

        jLabel13.setText("Επωνυμο");

        jLabel14.setText("Τηλεφωνο");

        jLabel15.setText("ΑΦΜ");

        jLabel16.setText("Διευθυνση");

        teliki_kataxwrisi_mesiti_button.setText("Καταχωρηση");
        teliki_kataxwrisi_mesiti_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teliki_kataxwrisi_mesiti_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout kataxorisi_mesitiLayout = new javax.swing.GroupLayout(kataxorisi_mesiti.getContentPane());
        kataxorisi_mesiti.getContentPane().setLayout(kataxorisi_mesitiLayout);
        kataxorisi_mesitiLayout.setHorizontalGroup(
            kataxorisi_mesitiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kataxorisi_mesitiLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(kataxorisi_mesitiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(teliki_kataxwrisi_mesiti_button)
                    .addGroup(kataxorisi_mesitiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(kataxorisi_mesitiLayout.createSequentialGroup()
                            .addComponent(jLabel16)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(address_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(kataxorisi_mesitiLayout.createSequentialGroup()
                            .addGroup(kataxorisi_mesitiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel11)
                                .addComponent(jLabel12)
                                .addComponent(jLabel13)
                                .addComponent(jLabel14)
                                .addComponent(jLabel15))
                            .addGap(58, 58, 58)
                            .addGroup(kataxorisi_mesitiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(afm_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(number_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(surname_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(name_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(at_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(64, Short.MAX_VALUE))
        );
        kataxorisi_mesitiLayout.setVerticalGroup(
            kataxorisi_mesitiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kataxorisi_mesitiLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(kataxorisi_mesitiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(at_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(kataxorisi_mesitiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(name_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(kataxorisi_mesitiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(surname_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(kataxorisi_mesitiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(number_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(kataxorisi_mesitiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(afm_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(kataxorisi_mesitiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(address_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(teliki_kataxwrisi_mesiti_button)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jLabel23.setText("Tετραγωνικα Μετρα");

        jLabel24.setText("Περιοχη");

        jLabel25.setText("Διευθυνση");

        jLabel26.setText("Ενοικιαζεται");

        buttonGroup1.add(yes_nikiazetai_spiti);
        yes_nikiazetai_spiti.setText("Ναι");

        buttonGroup1.add(no_noikiazetai_spiti);
        no_noikiazetai_spiti.setText("Οχι");

        jLabel27.setText("Πωλειται");

        buttonGroup2.add(nai_poleitai_spiti);
        nai_poleitai_spiti.setText("Ναι");

        buttonGroup2.add(no_poleitai_spiti);
        no_poleitai_spiti.setText("Οχι");

        jLabel28.setText("Δωματια");

        jLabel29.setText("Χρονια");

        jLabel30.setText("Μεσιτης");

        jLabel31.setText("Φωτογραφια");

        jButton1.setText("Καταχωρηση");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        picture_chooser_button.setText("Επιλογη");
        picture_chooser_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                picture_chooser_buttonActionPerformed(evt);
            }
        });

        picture_spitiou.setEditable(false);

        javax.swing.GroupLayout kataxorisi_spitiouLayout = new javax.swing.GroupLayout(kataxorisi_spitiou.getContentPane());
        kataxorisi_spitiou.getContentPane().setLayout(kataxorisi_spitiouLayout);
        kataxorisi_spitiouLayout.setHorizontalGroup(
            kataxorisi_spitiouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kataxorisi_spitiouLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(kataxorisi_spitiouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kataxorisi_spitiouLayout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addContainerGap(297, Short.MAX_VALUE))
                    .addGroup(kataxorisi_spitiouLayout.createSequentialGroup()
                        .addGroup(kataxorisi_spitiouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(kataxorisi_spitiouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(kataxorisi_spitiouLayout.createSequentialGroup()
                                    .addComponent(jLabel23)
                                    .addGap(44, 44, 44)
                                    .addComponent(tm_spitiou, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(kataxorisi_spitiouLayout.createSequentialGroup()
                                    .addComponent(jLabel24)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(region_spitiou, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kataxorisi_spitiouLayout.createSequentialGroup()
                                    .addGroup(kataxorisi_spitiouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel25)
                                        .addComponent(jLabel26))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(kataxorisi_spitiouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(address_spitiou, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                                        .addGroup(kataxorisi_spitiouLayout.createSequentialGroup()
                                            .addComponent(yes_nikiazetai_spiti)
                                            .addGap(18, 18, 18)
                                            .addComponent(no_noikiazetai_spiti))
                                        .addGroup(kataxorisi_spitiouLayout.createSequentialGroup()
                                            .addComponent(nai_poleitai_spiti)
                                            .addGap(18, 18, 18)
                                            .addComponent(no_poleitai_spiti))
                                        .addComponent(domatia_spitiou)))
                                .addComponent(jLabel27)
                                .addComponent(jLabel28)
                                .addComponent(jLabel29)
                                .addComponent(jLabel30))
                            .addGroup(kataxorisi_spitiouLayout.createSequentialGroup()
                                .addComponent(jLabel31)
                                .addGap(78, 78, 78)
                                .addGroup(kataxorisi_spitiouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(picture_chooser_button)
                                    .addGroup(kataxorisi_spitiouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(xronia_spitiou, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                                        .addComponent(picture_spitiou))
                                    .addComponent(mesites_lista_spitiou, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        kataxorisi_spitiouLayout.setVerticalGroup(
            kataxorisi_spitiouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kataxorisi_spitiouLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(kataxorisi_spitiouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(tm_spitiou, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(kataxorisi_spitiouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(region_spitiou, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(kataxorisi_spitiouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(address_spitiou, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(kataxorisi_spitiouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(yes_nikiazetai_spiti)
                    .addComponent(no_noikiazetai_spiti))
                .addGap(18, 18, 18)
                .addGroup(kataxorisi_spitiouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(nai_poleitai_spiti)
                    .addComponent(no_poleitai_spiti))
                .addGap(18, 18, 18)
                .addGroup(kataxorisi_spitiouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(domatia_spitiou, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(kataxorisi_spitiouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(xronia_spitiou, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(kataxorisi_spitiouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(picture_spitiou, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(picture_chooser_button)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(kataxorisi_spitiouLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(mesites_lista_spitiou, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addGap(35, 35, 35))
        );

        jLabel17.setText("Επωνυμο");

        jLabel18.setText("Ονομα");

        teliki_kataxwrisi_pelati_button.setText("Καταχωρηση");
        teliki_kataxwrisi_pelati_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teliki_kataxwrisi_pelati_buttonActionPerformed(evt);
            }
        });

        jLabel19.setText("Διευθυνση");

        jLabel20.setText("Αριθμος Ταυτοτητας");

        jLabel21.setText("ΑΦΜ");

        jLabel22.setText("Τηλεφωνο");

        javax.swing.GroupLayout kataxorisi_pelatiLayout = new javax.swing.GroupLayout(kataxorisi_pelati.getContentPane());
        kataxorisi_pelati.getContentPane().setLayout(kataxorisi_pelatiLayout);
        kataxorisi_pelatiLayout.setHorizontalGroup(
            kataxorisi_pelatiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kataxorisi_pelatiLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(kataxorisi_pelatiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(teliki_kataxwrisi_pelati_button)
                    .addGroup(kataxorisi_pelatiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(kataxorisi_pelatiLayout.createSequentialGroup()
                            .addComponent(jLabel19)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(address_pelati, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(kataxorisi_pelatiLayout.createSequentialGroup()
                            .addGroup(kataxorisi_pelatiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel20)
                                .addComponent(jLabel18)
                                .addComponent(jLabel17)
                                .addComponent(jLabel22)
                                .addComponent(jLabel21))
                            .addGap(58, 58, 58)
                            .addGroup(kataxorisi_pelatiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(afm_pelati, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(number_pelati, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(surname_pelati, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(name_pelati, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(at_pelati, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(64, Short.MAX_VALUE))
        );
        kataxorisi_pelatiLayout.setVerticalGroup(
            kataxorisi_pelatiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kataxorisi_pelatiLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(kataxorisi_pelatiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(at_pelati, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(kataxorisi_pelatiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(name_pelati, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(kataxorisi_pelatiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(surname_pelati, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(kataxorisi_pelatiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(number_pelati, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(kataxorisi_pelatiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(afm_pelati, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(kataxorisi_pelatiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(address_pelati, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(teliki_kataxwrisi_pelati_button)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jLabel32.setText("Tετραγωνικα Μετρα");

        jLabel33.setText("Απο");

        jLabel34.setText("Μεχρι");

        jLabel35.setText("Μεχρι");

        jLabel36.setText("Αριθμος δωματιων");

        jLabel37.setText("Απο");

        jLabel38.setText("Μεχρι");

        jLabel39.setText("Χρονια");

        jLabel40.setText("Απο");

        jButton6.setText("Αναζητηση");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        error_cs.setForeground(new java.awt.Color(255, 0, 0));

        javax.swing.GroupLayout custom_searchLayout = new javax.swing.GroupLayout(custom_search.getContentPane());
        custom_search.getContentPane().setLayout(custom_searchLayout);
        custom_searchLayout.setHorizontalGroup(
            custom_searchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(custom_searchLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(custom_searchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(custom_searchLayout.createSequentialGroup()
                        .addGroup(custom_searchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(custom_searchLayout.createSequentialGroup()
                                .addComponent(jLabel33)
                                .addGap(18, 18, 18)
                                .addComponent(tm_from_cs, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(custom_searchLayout.createSequentialGroup()
                                .addComponent(jLabel37)
                                .addGap(18, 18, 18)
                                .addComponent(domatia_from_cs, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(custom_searchLayout.createSequentialGroup()
                                .addComponent(jLabel40)
                                .addGap(18, 18, 18)
                                .addComponent(xronia_from_cs, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        .addGroup(custom_searchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(custom_searchLayout.createSequentialGroup()
                                .addComponent(jLabel34)
                                .addGap(18, 18, 18)
                                .addComponent(tm_mexri_cs, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(custom_searchLayout.createSequentialGroup()
                                .addComponent(jLabel35)
                                .addGap(18, 18, 18)
                                .addComponent(domatia_mexri_cs, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(custom_searchLayout.createSequentialGroup()
                                .addComponent(jLabel38)
                                .addGap(18, 18, 18)
                                .addComponent(xronia_mexri_cs, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(100, 100, 100))
                    .addGroup(custom_searchLayout.createSequentialGroup()
                        .addComponent(jLabel32)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(custom_searchLayout.createSequentialGroup()
                        .addGroup(custom_searchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel36)
                            .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton6)
                            .addComponent(error_cs, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        custom_searchLayout.setVerticalGroup(
            custom_searchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, custom_searchLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel32)
                .addGap(18, 18, 18)
                .addGroup(custom_searchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(custom_searchLayout.createSequentialGroup()
                        .addGroup(custom_searchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel33)
                            .addComponent(tm_from_cs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel36)
                        .addGap(18, 18, 18)
                        .addGroup(custom_searchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel37)
                            .addComponent(domatia_from_cs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(custom_searchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel40)
                            .addComponent(xronia_from_cs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(custom_searchLayout.createSequentialGroup()
                        .addGroup(custom_searchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel34)
                            .addComponent(tm_mexri_cs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(50, 50, 50)
                        .addGroup(custom_searchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel35)
                            .addComponent(domatia_mexri_cs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(51, 51, 51)
                        .addGroup(custom_searchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel38)
                            .addComponent(xronia_mexri_cs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(37, 37, 37)
                .addComponent(jButton6)
                .addGap(18, 18, 18)
                .addComponent(error_cs, javax.swing.GroupLayout.DEFAULT_SIZE, 14, Short.MAX_VALUE)
                .addContainerGap())
        );

        jButton5.setText("jButton5");

        spitia_result.setModel(new javax.swing.DefaultComboBoxModel());
        spitia_result.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                spitia_resultItemStateChanged(evt);
            }
        });

        jLabel41.setText("Σπιτια :");

        jLabel42.setText("Περιοχη");

        jLabel43.setText("Χρονια");

        jLabel44.setText("Διευθυνση");

        jLabel45.setText("Tετραγωνικα Μετρα");

        jLabel47.setText("Μεσιτης");

        jLabel50.setText("Δωματια");

        tm_result.setEditable(false);

        region_result.setEditable(false);

        address_result.setEditable(false);

        domatia_result.setEditable(false);

        xronia_result.setEditable(false);

        mesitis_result.setEditable(false);

        jLabel49.setText("Φωτογραφια");

        enoikiasi_result_search_button.setText("Eνοικιαση");
        enoikiasi_result_search_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enoikiasi_result_search_buttonActionPerformed(evt);
            }
        });

        agora_result_search_button.setText("Αγορα");
        agora_result_search_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agora_result_search_buttonActionPerformed(evt);
            }
        });

        display_house_search_result_message.setForeground(new java.awt.Color(255, 0, 0));

        endiaferetai_result_search_button.setText("Ενδιαφερεται");
        endiaferetai_result_search_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endiaferetai_result_search_buttonActionPerformed(evt);
            }
        });

        endiaferthikan_result_search_button.setText("Ενδιαφερθηκαν");
        endiaferthikan_result_search_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endiaferthikan_result_search_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout search_resultsLayout = new javax.swing.GroupLayout(search_results.getContentPane());
        search_results.getContentPane().setLayout(search_resultsLayout);
        search_resultsLayout.setHorizontalGroup(
            search_resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(search_resultsLayout.createSequentialGroup()
                .addGroup(search_resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(search_resultsLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jLabel41)
                        .addGap(49, 49, 49)
                        .addComponent(spitia_result, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(search_resultsLayout.createSequentialGroup()
                        .addGroup(search_resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, search_resultsLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(search_resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(search_resultsLayout.createSequentialGroup()
                                        .addGroup(search_resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel45)
                                            .addComponent(jLabel42))
                                        .addGap(18, 18, 18)
                                        .addGroup(search_resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(tm_result, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(region_result, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(search_resultsLayout.createSequentialGroup()
                                        .addGroup(search_resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel47)
                                            .addComponent(jLabel43)
                                            .addComponent(jLabel50)
                                            .addComponent(jLabel44))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(search_resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(address_result, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(domatia_result, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(xronia_result, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(mesitis_result, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(145, 145, 145))
                            .addGroup(search_resultsLayout.createSequentialGroup()
                                .addGroup(search_resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(search_resultsLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(display_house_search_result_message, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(search_resultsLayout.createSequentialGroup()
                                        .addGap(27, 27, 27)
                                        .addComponent(enoikiasi_result_search_button)
                                        .addGap(18, 18, 18)
                                        .addComponent(agora_result_search_button, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(endiaferetai_result_search_button)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(endiaferthikan_result_search_button)))
                                .addGap(16, 16, 16)))
                        .addGroup(search_resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel49)
                            .addComponent(photo_result, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        search_resultsLayout.setVerticalGroup(
            search_resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(search_resultsLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(search_resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(search_resultsLayout.createSequentialGroup()
                        .addGroup(search_resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spitia_result, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel41))
                        .addGap(47, 47, 47)
                        .addGroup(search_resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel45)
                            .addComponent(tm_result, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(search_resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel42)
                            .addComponent(region_result, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(search_resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel44)
                            .addComponent(address_result, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(search_resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel50)
                            .addComponent(domatia_result, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(search_resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(xronia_result, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16)
                        .addGroup(search_resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel47)
                            .addComponent(mesitis_result, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34)
                        .addGroup(search_resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(enoikiasi_result_search_button)
                            .addComponent(agora_result_search_button)
                            .addComponent(endiaferetai_result_search_button)
                            .addComponent(endiaferthikan_result_search_button))
                        .addGap(18, 18, 18)
                        .addComponent(display_house_search_result_message, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(search_resultsLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel49)
                        .addGap(18, 18, 18)
                        .addComponent(photo_result, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(171, Short.MAX_VALUE))
        );

        jLabel46.setText("Aριθμος Ταυτοτητας");

        search_spitia_mesiti_button.setText("Αναζητηση Σπιτιων");
        search_spitia_mesiti_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_spitia_mesiti_buttonActionPerformed(evt);
            }
        });

        search_for_spitia_use_mesiti_mesites_list.setModel(new javax.swing.DefaultComboBoxModel());
        search_for_spitia_use_mesiti_mesites_list.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                search_for_spitia_use_mesiti_mesites_listItemStateChanged(evt);
            }
        });

        jLabel48.setText("Ταυτοτητες Μεσιτων");

        mesites_search_errors.setForeground(new java.awt.Color(255, 0, 0));

        javax.swing.GroupLayout search_me_vasi_mesitiLayout = new javax.swing.GroupLayout(search_me_vasi_mesiti.getContentPane());
        search_me_vasi_mesiti.getContentPane().setLayout(search_me_vasi_mesitiLayout);
        search_me_vasi_mesitiLayout.setHorizontalGroup(
            search_me_vasi_mesitiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(search_me_vasi_mesitiLayout.createSequentialGroup()
                .addGroup(search_me_vasi_mesitiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(search_me_vasi_mesitiLayout.createSequentialGroup()
                        .addGroup(search_me_vasi_mesitiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel46)
                            .addComponent(jLabel48))
                        .addGap(45, 45, 45)
                        .addGroup(search_me_vasi_mesitiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(search_for_spitia_use_mesiti_mesites_list, 0, 170, Short.MAX_VALUE)
                            .addComponent(search_at_mesiti)))
                    .addComponent(search_spitia_mesiti_button)
                    .addComponent(mesites_search_errors, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(87, Short.MAX_VALUE))
        );
        search_me_vasi_mesitiLayout.setVerticalGroup(
            search_me_vasi_mesitiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(search_me_vasi_mesitiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(search_me_vasi_mesitiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(search_for_spitia_use_mesiti_mesites_list, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48))
                .addGap(48, 48, 48)
                .addGroup(search_me_vasi_mesitiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46)
                    .addComponent(search_at_mesiti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(search_spitia_mesiti_button)
                .addGap(18, 18, 18)
                .addComponent(mesites_search_errors, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(112, Short.MAX_VALUE))
        );

        jLabel90.setText("Aριθμος Ταυτοτητας");

        search_spitia_use_pelatis_button.setText("Αναζητηση Σπιτιων");
        search_spitia_use_pelatis_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_spitia_use_pelatis_buttonActionPerformed(evt);
            }
        });

        search_for_spitia_use_pelatis_pelates_list.setModel(new javax.swing.DefaultComboBoxModel());
        search_for_spitia_use_pelatis_pelates_list.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                search_for_spitia_use_pelatis_pelates_listItemStateChanged(evt);
            }
        });

        jLabel91.setText("Ταυτοτητες Πελατων");

        use_pelates_search_errors.setForeground(new java.awt.Color(255, 0, 0));

        javax.swing.GroupLayout search_me_vasi_pelatiLayout = new javax.swing.GroupLayout(search_me_vasi_pelati.getContentPane());
        search_me_vasi_pelati.getContentPane().setLayout(search_me_vasi_pelatiLayout);
        search_me_vasi_pelatiLayout.setHorizontalGroup(
            search_me_vasi_pelatiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(search_me_vasi_pelatiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(search_me_vasi_pelatiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(use_pelates_search_errors, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(search_me_vasi_pelatiLayout.createSequentialGroup()
                        .addGroup(search_me_vasi_pelatiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel90)
                            .addComponent(jLabel91))
                        .addGap(45, 45, 45)
                        .addGroup(search_me_vasi_pelatiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(search_for_spitia_use_pelatis_pelates_list, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(search_at_pelati, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(search_spitia_use_pelatis_button))
                .addContainerGap(74, Short.MAX_VALUE))
        );
        search_me_vasi_pelatiLayout.setVerticalGroup(
            search_me_vasi_pelatiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(search_me_vasi_pelatiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(search_me_vasi_pelatiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(search_for_spitia_use_pelatis_pelates_list, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel91))
                .addGap(48, 48, 48)
                .addGroup(search_me_vasi_pelatiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel90)
                    .addComponent(search_at_pelati, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(search_spitia_use_pelatis_button)
                .addGap(18, 18, 18)
                .addComponent(use_pelates_search_errors, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(112, Short.MAX_VALUE))
        );

        jLabel76.setText("Πελατες");

        enoikiasi_pelates_list.setModel(new javax.swing.DefaultComboBoxModel());
        enoikiasi_pelates_list.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                enoikiasi_pelates_listItemStateChanged(evt);
            }
        });

        jLabel77.setText("Ποσο ενοικιασης");

        jLabel78.setText("Αριθμ. Iban : ");

        jLabel79.setText("ΣΤΟΙΧΕΙΑ ΧΡΕΩΣΗΣ");

        jLabel80.setText("AT");

        jLabel81.setText("Oνομα");

        jLabel82.setText("Επωνυμο");

        enoikiasi_spitiou_button.setText("Ενοικιαση");
        enoikiasi_spitiou_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enoikiasi_spitiou_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout enoikiasiLayout = new javax.swing.GroupLayout(enoikiasi.getContentPane());
        enoikiasi.getContentPane().setLayout(enoikiasiLayout);
        enoikiasiLayout.setHorizontalGroup(
            enoikiasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(enoikiasiLayout.createSequentialGroup()
                .addGroup(enoikiasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(enoikiasiLayout.createSequentialGroup()
                        .addGap(128, 128, 128)
                        .addComponent(jLabel79))
                    .addGroup(enoikiasiLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(enoikiasi_spitiou_button)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(enoikiasiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(enoikiasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(enoikiasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel82, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel80, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel77, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel78, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, enoikiasiLayout.createSequentialGroup()
                            .addGap(17, 17, 17)
                            .addComponent(jLabel76)))
                    .addGroup(enoikiasiLayout.createSequentialGroup()
                        .addComponent(jLabel81, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(44, 44, 44)))
                .addGroup(enoikiasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(enoikiasiLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                        .addComponent(enoikiasi_pelates_list, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(29, Short.MAX_VALUE))
                    .addGroup(enoikiasiLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(enoikiasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(surname_enoikiasi, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(enoikiasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(at_enoikiasi)
                                .addComponent(onoma_enoikiasi, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(poso_enoikiasi, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(iban_enoikiasi, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        enoikiasiLayout.setVerticalGroup(
            enoikiasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(enoikiasiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(enoikiasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel76)
                    .addComponent(enoikiasi_pelates_list, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel79)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(enoikiasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel80)
                    .addGroup(enoikiasiLayout.createSequentialGroup()
                        .addComponent(at_enoikiasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(enoikiasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(onoma_enoikiasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel81))))
                .addGap(18, 18, 18)
                .addGroup(enoikiasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(surname_enoikiasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel82))
                .addGap(18, 18, 18)
                .addGroup(enoikiasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel77)
                    .addComponent(poso_enoikiasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(enoikiasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iban_enoikiasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel78))
                .addGap(28, 28, 28)
                .addComponent(enoikiasi_spitiou_button)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jLabel83.setText("AT");

        jLabel84.setText("Oνομα");

        jLabel85.setText("Επωνυμο");

        jLabel86.setText("Ποσο αγορας");

        agora_spitiou_button.setText("Αγορα");
        agora_spitiou_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agora_spitiou_buttonActionPerformed(evt);
            }
        });

        jLabel87.setText("Πελατες");

        agora_pelates_list.setModel(new javax.swing.DefaultComboBoxModel());
        agora_pelates_list.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                agora_pelates_listItemStateChanged(evt);
            }
        });

        jLabel88.setText("ΣΤΟΙΧΕΙΑ ΧΡΕΩΣΗΣ");

        jLabel89.setText("Αριθμ. Iban : ");

        javax.swing.GroupLayout agoraLayout = new javax.swing.GroupLayout(agora.getContentPane());
        agora.getContentPane().setLayout(agoraLayout);
        agoraLayout.setHorizontalGroup(
            agoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2)
            .addGroup(agoraLayout.createSequentialGroup()
                .addGroup(agoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(agoraLayout.createSequentialGroup()
                        .addGap(128, 128, 128)
                        .addComponent(jLabel88))
                    .addGroup(agoraLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(agora_spitiou_button)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(agoraLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(agoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(agoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel85, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel83, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel86, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel89, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, agoraLayout.createSequentialGroup()
                            .addGap(17, 17, 17)
                            .addComponent(jLabel87)))
                    .addGroup(agoraLayout.createSequentialGroup()
                        .addComponent(jLabel84, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(44, 44, 44)))
                .addGroup(agoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(agoraLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 140, Short.MAX_VALUE)
                        .addComponent(agora_pelates_list, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(155, Short.MAX_VALUE))
                    .addGroup(agoraLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(agoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(surname_agora, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(agoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(at_agora)
                                .addComponent(onoma_agora, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(poso_agora, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(iban_agora, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        agoraLayout.setVerticalGroup(
            agoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(agoraLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(agoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel87)
                    .addComponent(agora_pelates_list, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel88)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(agoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel83)
                    .addGroup(agoraLayout.createSequentialGroup()
                        .addComponent(at_agora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(agoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(onoma_agora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel84))))
                .addGap(18, 18, 18)
                .addGroup(agoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(surname_agora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel85))
                .addGap(18, 18, 18)
                .addGroup(agoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel86)
                    .addComponent(poso_agora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(agoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iban_agora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel89))
                .addGap(28, 28, 28)
                .addComponent(agora_spitiou_button)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        configs_manager.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        configs_manager.setResizable(false);

        jPanel2.setBackground(new java.awt.Color(255, 255, 153));

        oraclelogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/oracle.png"))); // NOI18N

        oracle_configs_button.setForeground(new java.awt.Color(255, 0, 0));
        oracle_configs_button.setText("Config");
        oracle_configs_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oracle_configs_buttonActionPerformed(evt);
            }
        });

        postgresqllogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/postgresql.png"))); // NOI18N

        postgresql_configs_button.setForeground(new java.awt.Color(255, 0, 0));
        postgresql_configs_button.setText("Config");
        postgresql_configs_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                postgresql_configs_buttonActionPerformed(evt);
            }
        });

        jButton4.setText("Done");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(oraclelogo, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(postgresqllogo, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 169, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(oracle_configs_button, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(postgresql_configs_button, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(oraclelogo, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(oracle_configs_button, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(postgresqllogo, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(postgresql_configs_button, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(28, 28, 28)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(89, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout configs_managerLayout = new javax.swing.GroupLayout(configs_manager.getContentPane());
        configs_manager.getContentPane().setLayout(configs_managerLayout);
        configs_managerLayout.setHorizontalGroup(
            configs_managerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        configs_managerLayout.setVerticalGroup(
            configs_managerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        aboutDialog.setTitle("About");
        aboutDialog.setResizable(false);

        aboutTextArea.setEditable(false);
        aboutTextArea.setBackground(new java.awt.Color(255, 255, 153));
        aboutTextArea.setColumns(20);
        aboutTextArea.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        aboutTextArea.setLineWrap(true);
        aboutTextArea.setRows(5);
        aboutTextArea.setText("Name : George\nSurname : Tzinos\nBorn : 1994\nA.M : 123896\nContact : gtzinos@it.teithe.gr\nWebSite : aetos.it.teithe.gr/~gtzinos");
        jScrollPane2.setViewportView(aboutTextArea);

        javax.swing.GroupLayout aboutDialogLayout = new javax.swing.GroupLayout(aboutDialog.getContentPane());
        aboutDialog.getContentPane().setLayout(aboutDialogLayout);
        aboutDialogLayout.setHorizontalGroup(
            aboutDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        aboutDialogLayout.setVerticalGroup(
            aboutDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        jLabel105.setText("Eνδιαφερον");

        endiaferon_list_edit_endiaferon.setModel(new javax.swing.DefaultComboBoxModel());
        endiaferon_list_edit_endiaferon.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                endiaferon_list_edit_endiaferonItemStateChanged(evt);
            }
        });

        jLabel106.setText("Πελατης");

        jButton8.setText("Αποθήκευση");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("Διαγραφη");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        house_edit_endiaferon.setEditable(false);

        jLabel107.setText("Σπιτι");

        javax.swing.GroupLayout endiaferon_editLayout = new javax.swing.GroupLayout(endiaferon_edit.getContentPane());
        endiaferon_edit.getContentPane().setLayout(endiaferon_editLayout);
        endiaferon_editLayout.setHorizontalGroup(
            endiaferon_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(endiaferon_editLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(endiaferon_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(endiaferon_editLayout.createSequentialGroup()
                        .addComponent(jButton8)
                        .addGap(18, 18, 18)
                        .addComponent(jButton9))
                    .addGroup(endiaferon_editLayout.createSequentialGroup()
                        .addGroup(endiaferon_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel105, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel106, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel107, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(93, 93, 93)
                        .addGroup(endiaferon_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(house_edit_endiaferon, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(endiaferon_list_edit_endiaferon, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pelatis_edit_endiaferon, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        endiaferon_editLayout.setVerticalGroup(
            endiaferon_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(endiaferon_editLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(endiaferon_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel105)
                    .addComponent(endiaferon_list_edit_endiaferon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(endiaferon_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel106)
                    .addComponent(pelatis_edit_endiaferon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(endiaferon_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel107)
                    .addComponent(house_edit_endiaferon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41)
                .addGroup(endiaferon_editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton8)
                    .addComponent(jButton9))
                .addContainerGap(129, Short.MAX_VALUE))
        );

        jLabel108.setText("Ενδιαφερόμενοι");

        endiaferon_list_kataxorisi_endiaferon.setModel(new javax.swing.DefaultComboBoxModel());
        endiaferon_list_kataxorisi_endiaferon.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                endiaferon_list_kataxorisi_endiaferonItemStateChanged(evt);
            }
        });

        jButton11.setText("Καταχώρηση");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout kataxorisi_endiaferonLayout = new javax.swing.GroupLayout(kataxorisi_endiaferon.getContentPane());
        kataxorisi_endiaferon.getContentPane().setLayout(kataxorisi_endiaferonLayout);
        kataxorisi_endiaferonLayout.setHorizontalGroup(
            kataxorisi_endiaferonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kataxorisi_endiaferonLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(kataxorisi_endiaferonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kataxorisi_endiaferonLayout.createSequentialGroup()
                        .addComponent(jButton11)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(kataxorisi_endiaferonLayout.createSequentialGroup()
                        .addComponent(jLabel108)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                        .addComponent(endiaferon_list_kataxorisi_endiaferon, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37))))
        );
        kataxorisi_endiaferonLayout.setVerticalGroup(
            kataxorisi_endiaferonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kataxorisi_endiaferonLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(kataxorisi_endiaferonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel108)
                    .addComponent(endiaferon_list_kataxorisi_endiaferon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44)
                .addComponent(jButton11)
                .addContainerGap(184, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel3.setBackground(new java.awt.Color(255, 255, 153));

        connect_database_button.setForeground(new java.awt.Color(255, 0, 0));
        connect_database_button.setText("ΣΥΝΔΕΣΗ ΜΕ ΤΟ ΓΡΑΦΕΙΟ");
        connect_database_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connect_database_buttonActionPerformed(evt);
            }
        });

        jLabel104.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/welcome.png"))); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(jLabel104))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(173, 173, 173)
                        .addComponent(connect_database_button, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(157, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel104, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(connect_database_button, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(88, Short.MAX_VALUE))
        );

        jMenu1.setText("Configs");

        jMenuItem1.setText("Edit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Help");

        jMenuItem2.setText("About");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void kataxorisi_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kataxorisi_buttonActionPerformed
        // TODO add your handling code here:
        kataxorisi_menou.setVisible(true);
        kataxorisi_menou.setSize(160, 180);
    }//GEN-LAST:event_kataxorisi_buttonActionPerformed

    private void anazitisi_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_anazitisi_buttonActionPerformed
        // TODO add your handling code here:

        anazitisi_menou.setVisible(true);
        anazitisi_menou.setSize(200, 200);
    }//GEN-LAST:event_anazitisi_buttonActionPerformed

    private void agora_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agora_buttonActionPerformed
        // TODO add your handling code here:
        edit_menu.setVisible(true);
        edit_menu.setSize(150, 250);
    }//GEN-LAST:event_agora_buttonActionPerformed

    private void service_oracleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_service_oracleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_service_oracleActionPerformed

    private void save_oracle_configs_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_oracle_configs_buttonActionPerformed
        // TODO add your handling code here:
        customXMLMaker("./configs/oracle.xml");
        oracle_configs.setVisible(false);
    }//GEN-LAST:event_save_oracle_configs_buttonActionPerformed

    private void save_postgresql_configs_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_postgresql_configs_buttonActionPerformed
        // TODO add your handling code here:
        customXMLMaker("./configs/postgresql.xml");
        postgresql_configs.setVisible(false);
    }//GEN-LAST:event_save_postgresql_configs_buttonActionPerformed

    private void service_postgresqlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_service_postgresqlActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_service_postgresqlActionPerformed

    private void kataxorisi_mesiti_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kataxorisi_mesiti_buttonActionPerformed
        // TODO add your handling code here:

        kataxorisi_mesiti.setSize(400, 400);
        kataxorisi_mesiti.setVisible(true);
    }//GEN-LAST:event_kataxorisi_mesiti_buttonActionPerformed

    private void teliki_kataxwrisi_mesiti_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teliki_kataxwrisi_mesiti_buttonActionPerformed
        // TODO add your handling code here:

        //an ola ta pedia kataxorithikan sosta tote
        if (isMesitisOk(at_mesiti.getText(), name_mesiti.getText(), surname_mesiti.getText(),
                number_mesiti.getText(), afm_mesiti.getText(), address_mesiti.getText())) {

            if (insertMesitisIntoDatabase(at_mesiti.getText(), name_mesiti.getText(), surname_mesiti.getText(),
                    number_mesiti.getText(), afm_mesiti.getText(), address_mesiti.getText())) {
                message_optionpane.showMessageDialog(null, "Ο μεσιτης καταχωρηθηκε με επιτυχεια !!!");
                clearMesitisInsertMenu();
                kataxorisi_mesiti.setVisible(false);
            }
        }
    }//GEN-LAST:event_teliki_kataxwrisi_mesiti_buttonActionPerformed

    private void teliki_kataxwrisi_pelati_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teliki_kataxwrisi_pelati_buttonActionPerformed
        // TODO add your handling code here:

        if (isPelatisOk(at_pelati.getText(), name_pelati.getText(), surname_pelati.getText(),
                number_pelati.getText(), afm_pelati.getText(), address_pelati.getText())) {

            if (insertPelatisIntoDatabase(at_pelati.getText(), name_pelati.getText(), surname_pelati.getText(),
                    number_pelati.getText(), afm_pelati.getText(), address_pelati.getText())) {

                message_optionpane.showMessageDialog(null, "Ο πελατης καταχωρηθηκε με επιτυχεια !!!");
                clearPelatisInsertMenu();
                kataxorisi_pelati.setVisible(false);
            }

        }
    }//GEN-LAST:event_teliki_kataxwrisi_pelati_buttonActionPerformed

    private void kataxorisi_pelati_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kataxorisi_pelati_buttonActionPerformed
        // TODO add your handling code here:
        kataxorisi_pelati.setSize(400, 400);
        kataxorisi_pelati.setVisible(true);
    }//GEN-LAST:event_kataxorisi_pelati_buttonActionPerformed

    private void kataxorisi_spitioy_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kataxorisi_spitioy_buttonActionPerformed
        // TODO add your handling code here:
        if (getMesites(mesites_lista_spitiou, 2, 3)) {
            kataxorisi_spitiou.setSize(400, 500);
            kataxorisi_spitiou.setVisible(true);
        }
    }//GEN-LAST:event_kataxorisi_spitioy_buttonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:

        if (isHouseOk(tm_spitiou.getText(), region_spitiou.getText(), address_spitiou.getText(), yes_nikiazetai_spiti, no_noikiazetai_spiti, nai_poleitai_spiti, no_poleitai_spiti, domatia_spitiou.getText(), xronia_spitiou.getText(), picture_spitiou.getText())) {

            if (insertHouseIntoDatabase(tm_spitiou.getText(), region_spitiou.getText(), address_spitiou.getText(), yes_nikiazetai_spiti,
                    no_noikiazetai_spiti, nai_poleitai_spiti, no_poleitai_spiti, domatia_spitiou.getText(),
                    xronia_spitiou.getText(), picture_spitiou.getText(), mesites_lista_spitiou)) {

                clearHouseInsertMenu();
                message_optionpane.showMessageDialog(null, "Το σπιτι καταχωρηθηκε με επιτυχεια !!!");
                kataxorisi_spitiou.setVisible(false);
            }

        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void picture_chooser_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_picture_chooser_buttonActionPerformed
        // TODO add your handling code here:

        file_chooser.showOpenDialog(null);
        if (file_chooser.getSelectedFile() != null) {
            File file = file_chooser.getSelectedFile();
            String path = file.getAbsolutePath();

            picture_spitiou.setText(path);
        }

    }//GEN-LAST:event_picture_chooser_buttonActionPerformed

    private void me_vasi_pedia_search_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_me_vasi_pedia_search_buttonActionPerformed
        // TODO add your handling code here:

        setCustomSearchMenuToDefault();
        custom_search.setVisible(true);
        custom_search.setSize(400, 400);
    }//GEN-LAST:event_me_vasi_pedia_search_buttonActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        //default error set

        error_cs.setText("");
        //ola ok
        if (isHouseSearchOk(tm_from_cs.getText(), tm_mexri_cs.getText(), domatia_from_cs.getText(),
                domatia_mexri_cs.getText(), xronia_from_cs.getText(), xronia_mexri_cs.getText())) {

            if (customHouseSearch(tm_from_cs.getText(), tm_mexri_cs.getText(), domatia_from_cs.getText(),
                    domatia_mexri_cs.getText(), xronia_from_cs.getText(), xronia_mexri_cs.getText(), spitia_result)) {

                search_results.setVisible(true);
                search_results.setSize(950, 530);

            } else {
                error_cs.setText("Δεν βρεθηκαν αποτελεσματα με αυτα τα πεδια που ορισατε !!!");
            }

        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void spitia_resultItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_spitia_resultItemStateChanged
        // TODO add your handling code here:
        /*
         distixws diagrafetai h lista kai to caret possition tou getselectedindex deixnei sto -1
         opote anagazetai na treksei to event itemstate change kai na krasarei to siban
         opote tha xreiastei se kathe item state changed elenxos
         */
        if (spitia_result.getItemCount() > 0) {
            changeResultSearchInfo();
            enoikiasi.setVisible(false);
            agora.setVisible(false);
            kataxorisi_endiaferon.setVisible(false);
            endiaferon_edit.setVisible(false);
        }
    }//GEN-LAST:event_spitia_resultItemStateChanged

    private void me_vasi_mesitwn_search_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_me_vasi_mesitwn_search_buttonActionPerformed
        // TODO add your handling code here:
        if (getMesites(search_for_spitia_use_mesiti_mesites_list, 1, 2)) {
            search_me_vasi_mesiti.setSize(400, 250);
            search_me_vasi_mesiti.setVisible(true);
        }
    }//GEN-LAST:event_me_vasi_mesitwn_search_buttonActionPerformed

    private void search_spitia_mesiti_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_spitia_mesiti_buttonActionPerformed
        // TODO add your handling code here:
        mesites_search_errors.setText("");

        String at_fsearch = search_at_mesiti.getText();
        if (searchHouseMeVasiMesiti(at_fsearch, spitia_result)) {
            search_results.setVisible(true);
            search_results.setSize(950, 530);
        }


    }//GEN-LAST:event_search_spitia_mesiti_buttonActionPerformed

    private void me_vasi_pelatwn_search_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_me_vasi_pelatwn_search_buttonActionPerformed
        // TODO add your handling code here:
        if (getPelates(search_for_spitia_use_pelatis_pelates_list, 1, 2)) {
            search_me_vasi_pelati.setSize(400, 250);
            search_me_vasi_pelati.setVisible(true);
        }
    }//GEN-LAST:event_me_vasi_pelatwn_search_buttonActionPerformed

    private void edit_meni_mesitis_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_meni_mesitis_buttonActionPerformed

        // TODO add your handling code here:
        if (getMesites(edit_mesites_list, 2, 3) && edit_mesites_list.getItemCount() > 0) {
            changeEditMesitisInfo();

            mesites_edit.setSize(500, 440);
            mesites_edit.setVisible(true);
        } else {
            error_optionpane.showMessageDialog(null, "Δεν βρεθηκαν διαθεσιμοι μεσιτες στο συστημα σας !!!");
        }
    }//GEN-LAST:event_edit_meni_mesitis_buttonActionPerformed

    private void edit_mesitis_save_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_mesitis_save_buttonActionPerformed
        // TODO add your handling code here:

        if (isMesitisOk(edit_at_mesiti.getText(), edit_name_mesiti.getText(), edit_surname_mesiti.getText(),
                edit_number_mesiti.getText(), edit_afm_mesiti.getText(), edit_address_mesiti.getText())) {

            updateMesitisInfo(edit_at_mesiti.getText(), edit_name_mesiti.getText(), edit_surname_mesiti.getText(),
                    edit_number_mesiti.getText(), edit_afm_mesiti.getText(), edit_address_mesiti.getText(), edit_mesites_list);
        }
    }//GEN-LAST:event_edit_mesitis_save_buttonActionPerformed

    private void edit_mesitis_delete_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_mesitis_delete_buttonActionPerformed
        // TODO add your handling code here:
        int index = edit_mesites_list.getSelectedIndex();
        int deleteConfirm = message_optionpane.showConfirmDialog(null, "Ειστε σιγουρος πως θελετε να διαγραφει ο μεσιτης με AT : " + mesites[index][0] + " ?");
        if (deleteConfirm == message_optionpane.YES_OPTION) {
            deleteMesitisFromDatabase(edit_mesites_list);
        }

    }//GEN-LAST:event_edit_mesitis_delete_buttonActionPerformed

    private void edit_mesites_listItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_edit_mesites_listItemStateChanged
        // TODO add your handling code here:
        /*
         fovero error alla kata thn enhmerwsh ths vashs eixa valei na enhmerwnei kai ton pinaka 
         mesites o opoios tha enhmerwne kai thn lista poy tha estelna sthn methodo getmesites
         etsi kata thn enhmerwsh ginetai diagrafh arxika ths listas epeita gia kapoio logo h lista
         eperne auto to event kai thn wra poy tan se update h vash auto krasare epeidh h methodos
         xrhshmopoiouse mesites[-1][0] opou -1 index de ifistatai !!! logo oti index=lista.getselectedindex
         opou tan adeia
         */
        if (edit_mesites_list.getSelectedIndex() >= 0) {
            changeEditMesitisInfo();
        }
    }//GEN-LAST:event_edit_mesites_listItemStateChanged

    private void edit_meni_houses_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_meni_houses_buttonActionPerformed
        // TODO add your handling code here:
        if (getHouses(spitia_lista_edit_house, 4, 3)) {
            getMesites(mesites_lista_edit_house, 2, 3);

            if (spitia_lista_edit_house.getItemCount() == 0) {
                error_optionpane.showMessageDialog(null, "Δεν βρεθηκαν σπιτια στο συστημα σας");
            } else if (mesites_lista_edit_house.getItemCount() == 0) {
                error_optionpane.showMessageDialog(null, "Δεν βρεθηκαν μεσιτες στο συστημα σας");
            } else {
                changeEditHousesInfo();

                houses_edit.setSize(1000, 700);
                houses_edit.setVisible(true);
            }
        }
    }//GEN-LAST:event_edit_meni_houses_buttonActionPerformed

    private void edit_house_save_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_house_save_buttonActionPerformed
        // TODO add your handling code here: 
        /*
         ayto to kanw gia na valw thn default eikona an o xristis de thelei na thn allaksei
         pernw to path tou project
         */
        File currentDirFile = new File(".");
        String helper = currentDirFile.getAbsolutePath();

        //an de exei allaksei thn eikona tote kane set thn default opws tis apothikeuw egw sto fakelo pictures/houses/id_house
        if (picture_edit_house.getText().length() == 0) {
            picture_edit_house.setText(helper + "/pictures/houses/" + houses[spitia_lista_edit_house.getSelectedIndex()][0] + ".jpg");
        }
        //test tis metavlites
        if (isHouseOk(tm_edit_house.getText(), region_edit_house.getText(), address_edit_house.getText(),
                yes_nikiazeta_edit_house, no_noikiazetai_edit_house, nai_poleitai_edit_house, no_poleita_edit_house,
                domatia_edit_house.getText(), xronia_edit_house.getText(), picture_edit_house.getText())) {
            //an ola ok tote update your house men
            updateHouseInfo(tm_edit_house.getText(), region_edit_house.getText(), address_edit_house.getText(),
                    yes_nikiazeta_edit_house, no_noikiazetai_edit_house, nai_poleitai_edit_house, no_poleita_edit_house,
                    domatia_edit_house.getText(), xronia_edit_house.getText(), picture_edit_house.getText(), mesites_lista_edit_house, spitia_lista_edit_house);
        }

    }//GEN-LAST:event_edit_house_save_buttonActionPerformed

    private void picture_chooser_edit_house_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_picture_chooser_edit_house_buttonActionPerformed
        // TODO add your handling code here:
        file_chooser.showOpenDialog(null);
        if (file_chooser.getSelectedFile() != null) {
            File file = file_chooser.getSelectedFile();
            String path = file.getAbsolutePath();

            picture_edit_house.setText(path);
        }
    }//GEN-LAST:event_picture_chooser_edit_house_buttonActionPerformed

    private void edit_house_delete_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_house_delete_buttonActionPerformed
        // TODO add your handling code here:
        int index_mesiti = mesites_lista_edit_house.getSelectedIndex();
        int index_spiti = spitia_lista_edit_house.getSelectedIndex();
        int deleteConfirm = message_optionpane.showConfirmDialog(null,
                "Ειστε σιγουρος πως θελετε να διαγραφει το σπιτι με id : " + houses[index_spiti][0]
                + " και ΑΤ ΜΕΣΙΤΗ : " + mesites[index_mesiti][0] + " ?");

        if (deleteConfirm == message_optionpane.YES_OPTION) {
            deleteHouseFromDatabase(spitia_lista_edit_house);
        }
    }//GEN-LAST:event_edit_house_delete_buttonActionPerformed

    private void spitia_lista_edit_houseItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_spitia_lista_edit_houseItemStateChanged
        // TODO add your handling code here:
        if (spitia_lista_edit_house.getSelectedIndex() >= 0) {
            changeEditHousesInfo();
        }
    }//GEN-LAST:event_spitia_lista_edit_houseItemStateChanged

    private void edit_meni_pelates_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_meni_pelates_buttonActionPerformed
        // TODO add your handling code here:
        if (getPelates(pelates_lista_edit_pelatis, 2, 3)) {
            if (pelates_lista_edit_pelatis.getItemCount() == 0) {
                error_optionpane.showMessageDialog(null, "Δεν βρεθηκαν πελατες στο συστημα σας !!!");
            } else {
                changePelatesEditInfo();
                pelates_edit.setSize(450, 420);
                pelates_edit.setVisible(true);
            }
        }
    }//GEN-LAST:event_edit_meni_pelates_buttonActionPerformed

    private void edit_pelatis_save_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_pelatis_save_buttonActionPerformed
        // TODO add your handling code here:
        if (isPelatisOk(at_edit_pelatis.getText(), name_edit_pelatis.getText(), surname_edit_pelatis.getText(),
                number_edit_pelatis.getText(), afm_edit_pelatis.getText(), address_edit_pelatis.getText())) {

            updatePelatisInfo(at_edit_pelatis.getText(), name_edit_pelatis.getText(), surname_edit_pelatis.getText(),
                    number_edit_pelatis.getText(), afm_edit_pelatis.getText(), address_edit_pelatis.getText(), pelates_lista_edit_pelatis);

        }
    }//GEN-LAST:event_edit_pelatis_save_buttonActionPerformed

    private void edit_pelatis_delete_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_pelatis_delete_buttonActionPerformed
        // TODO add your handling code here:
        int index = pelates_lista_edit_pelatis.getSelectedIndex();
        int deleteConfirm = message_optionpane.showConfirmDialog(null,
                "Ειστε σιγουρος πως θελετε να διαγραφει ο πελατης με ΑΤ : " + pelates[index][0] + " ?");

        if (deleteConfirm == message_optionpane.YES_OPTION) {
            deletePelatisFromDatabase(pelates_lista_edit_pelatis);
        }
    }//GEN-LAST:event_edit_pelatis_delete_buttonActionPerformed

    private void pelates_lista_edit_pelatisItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_pelates_lista_edit_pelatisItemStateChanged
        // TODO add your handling code here:
        if (pelates_lista_edit_pelatis.getItemCount() > 0) {
            changePelatesEditInfo();
        }
    }//GEN-LAST:event_pelates_lista_edit_pelatisItemStateChanged

    private void enoikiasi_result_search_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enoikiasi_result_search_buttonActionPerformed
        // TODO add your handling code here:
        if (getPelates(enoikiasi_pelates_list, 2, 3) && enoikiasi_pelates_list.getItemCount() > 0) {
            changeEnoikiasiPelatesXrewshInfo();
            enoikiasi.setSize(500, 450);
            enoikiasi.setVisible(true);
        } else {
            error_optionpane.showMessageDialog(null, "Δεν βρεθηκαν πελατες στο συστημα σας !!! Καταχωρηστε εναν για να γινει η ενοικιαση !!!");
        }
    }//GEN-LAST:event_enoikiasi_result_search_buttonActionPerformed

    private void enoikiasi_pelates_listItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_enoikiasi_pelates_listItemStateChanged
        // TODO add your handling code here:
        if (enoikiasi_pelates_list.getItemCount() > 0) {
            changeEnoikiasiPelatesXrewshInfo();
        }
    }//GEN-LAST:event_enoikiasi_pelates_listItemStateChanged

    private void enoikiasi_spitiou_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enoikiasi_spitiou_buttonActionPerformed
        // TODO add your handling code here:
        if (isOkayXreosiEnoikiasisInfo(poso_enoikiasi.getText(), iban_enoikiasi.getText())) {
            insertEnoikiasiSpitiou(enoikiasi_pelates_list, spitia_result, poso_enoikiasi.getText(), iban_enoikiasi.getText());
        }
    }//GEN-LAST:event_enoikiasi_spitiou_buttonActionPerformed

    private void agora_result_search_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agora_result_search_buttonActionPerformed
        // TODO add your handling code here:
        if (getPelates(agora_pelates_list, 2, 3) && agora_pelates_list.getItemCount() > 0) {
            changeAgoraPelatesXrewshInfo();
            agora.setSize(500, 450);
            agora.setVisible(true);
        } else {
            error_optionpane.showMessageDialog(null, "Δεν βρεθηκαν πελατες στο συστημα σας !!! Καταχωρηστε εναν για να γινει η πωληση !!!");
        }
    }//GEN-LAST:event_agora_result_search_buttonActionPerformed

    private void agora_spitiou_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agora_spitiou_buttonActionPerformed
        // TODO add your handling code here:
        if (isOkayXreosiAgoraInfo(poso_agora.getText(), iban_agora.getText())) {
            insertAgoraSpitiou(agora_pelates_list, spitia_result, poso_agora.getText(), iban_agora.getText());
        }
    }//GEN-LAST:event_agora_spitiou_buttonActionPerformed

    private void agora_pelates_listItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_agora_pelates_listItemStateChanged
        // TODO add your handling code here:
        if (agora_pelates_list.getItemCount() > 0) {
            changeAgoraPelatesXrewshInfo();
        }
    }//GEN-LAST:event_agora_pelates_listItemStateChanged

    private void search_for_spitia_use_mesiti_mesites_listItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_search_for_spitia_use_mesiti_mesites_listItemStateChanged
        // TODO add your handling code here:
        if (search_for_spitia_use_mesiti_mesites_list.getItemCount() > 0) {
            changeSearchForSpitiaUseMesitisInfo();
        }
    }//GEN-LAST:event_search_for_spitia_use_mesiti_mesites_listItemStateChanged

    private void search_spitia_use_pelatis_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_spitia_use_pelatis_buttonActionPerformed
        // TODO add your handling code here:
        use_pelates_search_errors.setText("");

        String at_fsearch = search_at_pelati.getText();
        if (searchHouseMeVasiPelati(at_fsearch, spitia_result)) {
            search_results.setVisible(true);
            search_results.setSize(950, 530);
        }
    }//GEN-LAST:event_search_spitia_use_pelatis_buttonActionPerformed

    private void search_for_spitia_use_pelatis_pelates_listItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_search_for_spitia_use_pelatis_pelates_listItemStateChanged
        // TODO add your handling code here:
        if (search_for_spitia_use_pelatis_pelates_list.getItemCount() > 0) {
            changeSearchForSpitiaUsePelatisInfo();
        }
    }//GEN-LAST:event_search_for_spitia_use_pelatis_pelates_listItemStateChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        if (getEnoikiaseis(enoikiaseis_list_edit_enoikiaseis, 1, 2)) {
            if (enoikiaseis_list_edit_enoikiaseis.getItemCount() == 0) {
                error_optionpane.showMessageDialog(null, "Δεν βρεθηκαν ενοικιασεις στο συστημα σας");
            } else {
                changeEnoikiaseisEditInfo();
                enoikiaseis_edit.setSize(450, 420);
                enoikiaseis_edit.setVisible(true);
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void poso_edit_enoikiaseisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_poso_edit_enoikiaseisActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_poso_edit_enoikiaseisActionPerformed

    private void iban_edit_enoikiaseisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iban_edit_enoikiaseisActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_iban_edit_enoikiaseisActionPerformed

    private void imera_edit_enoikiaseisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imera_edit_enoikiaseisActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_imera_edit_enoikiaseisActionPerformed

    private void kostos_edit_agoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kostos_edit_agoresActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kostos_edit_agoresActionPerformed

    private void imera_edit_agoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imera_edit_agoresActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_imera_edit_agoresActionPerformed

    private void iban_edit_agoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iban_edit_agoresActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_iban_edit_agoresActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        if (getAgores(agores_list_edit_agores, 1, 2)) {
            if (agores_list_edit_agores.getItemCount() == 0) {
                error_optionpane.showMessageDialog(null, "Δεν βρεθηκαν αγορες στο συστημα σας");
            } else {
                changeAgoresEditInfo();
                agores_edit.setSize(450, 420);
                agores_edit.setVisible(true);
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void enoikiaseis_list_edit_enoikiaseisItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_enoikiaseis_list_edit_enoikiaseisItemStateChanged
        // TODO add your handling code here:
        changeEnoikiaseisEditInfo();
    }//GEN-LAST:event_enoikiaseis_list_edit_enoikiaseisItemStateChanged

    private void agores_list_edit_agoresItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_agores_list_edit_agoresItemStateChanged
        // TODO add your handling code here:
        changeAgoresEditInfo();
    }//GEN-LAST:event_agores_list_edit_agoresItemStateChanged

    private void edit_enoikiaseis_save_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_enoikiaseis_save_buttonActionPerformed
        // TODO add your handling code here:
        if (isOkayXreosiEnoikiasisInfo(poso_edit_enoikiaseis.getText(), iban_edit_enoikiaseis.getText())) {

            updateEnoikiaseisInfo(poso_edit_enoikiaseis.getText(), iban_edit_enoikiaseis.getText(), enoikiaseis_list_edit_enoikiaseis);

        }
    }//GEN-LAST:event_edit_enoikiaseis_save_buttonActionPerformed

    private void edit_agores_save_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_agores_save_buttonActionPerformed
        // TODO add your handling code here:
        if (isOkayXreosiAgoraInfo(kostos_edit_agores.getText(), iban_edit_agores.getText())) {

            updateAgoresInfo(kostos_edit_agores.getText(), iban_edit_agores.getText(), agores_list_edit_agores);

        }
    }//GEN-LAST:event_edit_agores_save_buttonActionPerformed

    private void edit_enoikiaseis_delete_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_enoikiaseis_delete_buttonActionPerformed
        // TODO add your handling code here:
        int index = enoikiaseis_list_edit_enoikiaseis.getSelectedIndex();
        int deleteConfirm = message_optionpane.showConfirmDialog(null,
                "Ειστε σιγουρος πως θελετε να διαγραφει η ενοικιαση με ΑΤ Πελατη : " + enoikiaseis[index][0] + " ?");

        if (deleteConfirm == message_optionpane.YES_OPTION) {
            deleteEnoikiasiFromDatabase(enoikiaseis_list_edit_enoikiaseis, -1);
        }
    }//GEN-LAST:event_edit_enoikiaseis_delete_buttonActionPerformed

    private void edit_agores_delete_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_agores_delete_buttonActionPerformed
        // TODO add your handling code here:
        int index = agores_list_edit_agores.getSelectedIndex();
        int deleteConfirm = message_optionpane.showConfirmDialog(null,
                "Ειστε σιγουρος πως θελετε να διαγραφει η αγορα με ΑΤ Πελατη : " + agores[index][0] + " ?");

        if (deleteConfirm == message_optionpane.YES_OPTION) {
            deleteAgoraFromDatabase(agores_list_edit_agores,-1);
        }
    }//GEN-LAST:event_edit_agores_delete_buttonActionPerformed

    private void oracle_configs_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oracle_configs_buttonActionPerformed
        // TODO add your handling code here:
        customXMLParser("./configs/oracle.xml");
        oracle_configs.setVisible(true);
        oracle_configs.setSize(340, 310);
    }//GEN-LAST:event_oracle_configs_buttonActionPerformed

    private void connect_database_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connect_database_buttonActionPerformed
        // TODO add your handling code here:
        if (oracle_configs_button.getForeground() == Color.blue && postgresql_configs_button.getForeground() == Color.blue) {
            if (connectToDatabase()) {
                this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                setVisible(false);
                home_page.setVisible(true);
                message_optionpane.showMessageDialog(null, "Connected Succefully !!! Welcome to your office application !!!");
            } else {
                error_optionpane.showMessageDialog(null, "Something going wrong with your configs.Check them and try again !!!");
            }
        } else {
            error_optionpane.showMessageDialog(null, "You need to make your configs for oracle and postgresql first !!!");
        }
    }//GEN-LAST:event_connect_database_buttonActionPerformed

    private void postgresql_configs_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_postgresql_configs_buttonActionPerformed
        // TODO add your handling code here:
        customXMLParser("./configs/postgresql.xml");
        postgresql_configs.setVisible(true);
        postgresql_configs.setSize(340, 310);
    }//GEN-LAST:event_postgresql_configs_buttonActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        configs_manager.setVisible(false);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        configs_manager.setSize(700, 300);
        configs_manager.setVisible(true);

    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        aboutDialog.setVisible(true);
        aboutDialog.setSize(270, 150);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void endiaferon_list_edit_endiaferonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_endiaferon_list_edit_endiaferonItemStateChanged
        // TODO add your handling code here:
        if (endiaferon_list_edit_endiaferon.getSelectedIndex() >= 0) {
            changeEndiaferonEditInfo();
        }
    }//GEN-LAST:event_endiaferon_list_edit_endiaferonItemStateChanged

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        //elenxos timwn
        if (is_integer(house_edit_endiaferon.getText()) && pelatis_edit_endiaferon.getText().length() > 0) {

            updateEndiaferonInfo(pelatis_edit_endiaferon.getText(), house_edit_endiaferon.getText(), endiaferon_list_edit_endiaferon);
        } else {
            error_optionpane.showMessageDialog(null, "Ελενξτε τα στοιχεια που καταχωρείτε και ξανα προσπαθήστε !!!");
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        int index = endiaferon_list_edit_endiaferon.getSelectedIndex();
        int deleteConfirm = message_optionpane.showConfirmDialog(null, "Ειστε σιγουρος πως θελετε να διαγραφει ο ενδιαφερομενος με AT : " + endiaferon[index][0] + " ?");
        if (deleteConfirm == message_optionpane.YES_OPTION) {
            deleteEndiaferonFromDatabase(endiaferon_list_edit_endiaferon, "", -1);
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void endiaferetai_result_search_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endiaferetai_result_search_buttonActionPerformed
        // TODO add your handling code here:
        if (getPelates(endiaferon_list_kataxorisi_endiaferon, 2, 3)) {

            if (endiaferon_list_kataxorisi_endiaferon.getItemCount() == 0) {
                error_optionpane.showMessageDialog(null, "Δεν βρεθηκαν πελατες στο συστημα σας !!!");
            } else {
                kataxorisi_endiaferon.setSize(400, 200);
                kataxorisi_endiaferon.setVisible(true);
            }
        }


    }//GEN-LAST:event_endiaferetai_result_search_buttonActionPerformed

    private void endiaferon_list_kataxorisi_endiaferonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_endiaferon_list_kataxorisi_endiaferonItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_endiaferon_list_kataxorisi_endiaferonItemStateChanged

    private void endiaferthikan_result_search_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endiaferthikan_result_search_buttonActionPerformed
        // TODO add your handling code here:
        if (getEndiaferontes(endiaferon_list_edit_endiaferon, spitia_result, 1, 2)) {

            if (endiaferon_list_edit_endiaferon.getItemCount() == 0) {
                error_optionpane.showMessageDialog(null, "Δεν βρεθηκαν ενδιαφεροντες στο συστημα σας !!!");
            } else {
                changeEndiaferonEditInfo();
                endiaferon_edit.setSize(600, 250);
                endiaferon_edit.setVisible(true);
            }
        }
    }//GEN-LAST:event_endiaferthikan_result_search_buttonActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
        insertEndiaferon(endiaferon_list_kataxorisi_endiaferon, spitia_result);
    }//GEN-LAST:event_jButton11ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Mesitiko.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Mesitiko.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Mesitiko.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Mesitiko.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Mesitiko().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog aboutDialog;
    private javax.swing.JTextArea aboutTextArea;
    private javax.swing.JTextField address_edit_house;
    private javax.swing.JTextField address_edit_pelatis;
    private javax.swing.JTextField address_mesiti;
    private javax.swing.JTextField address_pelati;
    private javax.swing.JTextField address_result;
    private javax.swing.JTextField address_spitiou;
    private javax.swing.JTextField afm_edit_pelatis;
    private javax.swing.JTextField afm_mesiti;
    private javax.swing.JTextField afm_pelati;
    private javax.swing.JFrame agora;
    private javax.swing.JButton agora_button;
    private javax.swing.JComboBox agora_pelates_list;
    private javax.swing.JButton agora_result_search_button;
    private javax.swing.JButton agora_spitiou_button;
    private javax.swing.JFrame agores_edit;
    private javax.swing.JComboBox agores_list_edit_agores;
    private javax.swing.JButton anazitisi_button;
    private javax.swing.JFrame anazitisi_menou;
    private javax.swing.JTextField at_agora;
    private javax.swing.JTextField at_edit_pelatis;
    private javax.swing.JTextField at_enoikiasi;
    private javax.swing.JTextField at_mesiti;
    private javax.swing.JTextField at_pelati;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JFrame configs_manager;
    private javax.swing.JButton connect_database_button;
    private javax.swing.JFrame custom_search;
    private javax.swing.JLabel display_house_search_result_message;
    private javax.swing.JTextField domatia_edit_house;
    private javax.swing.JTextField domatia_from_cs;
    private javax.swing.JTextField domatia_mexri_cs;
    private javax.swing.JTextField domatia_result;
    private javax.swing.JTextField domatia_spitiou;
    private javax.swing.JTextField edit_address_mesiti;
    private javax.swing.JTextField edit_afm_mesiti;
    private javax.swing.JButton edit_agores_delete_button;
    private javax.swing.JButton edit_agores_save_button;
    private javax.swing.JTextField edit_at_mesiti;
    private javax.swing.JButton edit_enoikiaseis_delete_button;
    private javax.swing.JButton edit_enoikiaseis_save_button;
    private javax.swing.JButton edit_house_delete_button;
    private javax.swing.JButton edit_house_save_button;
    private javax.swing.JButton edit_meni_houses_button;
    private javax.swing.JButton edit_meni_mesitis_button;
    private javax.swing.JButton edit_meni_pelates_button;
    private javax.swing.JFrame edit_menu;
    private javax.swing.JComboBox edit_mesites_list;
    private javax.swing.JButton edit_mesitis_delete_button;
    private javax.swing.JButton edit_mesitis_save_button;
    private javax.swing.JTextField edit_name_mesiti;
    private javax.swing.JTextField edit_number_mesiti;
    private javax.swing.JButton edit_pelatis_delete_button;
    private javax.swing.JButton edit_pelatis_save_button;
    private javax.swing.JTextField edit_surname_mesiti;
    private javax.swing.JButton endiaferetai_result_search_button;
    private javax.swing.JFrame endiaferon_edit;
    private javax.swing.JComboBox endiaferon_list_edit_endiaferon;
    private javax.swing.JComboBox endiaferon_list_kataxorisi_endiaferon;
    private javax.swing.JButton endiaferthikan_result_search_button;
    private javax.swing.JFrame enoikiaseis_edit;
    private javax.swing.JComboBox enoikiaseis_list_edit_enoikiaseis;
    private javax.swing.JFrame enoikiasi;
    private javax.swing.JComboBox enoikiasi_pelates_list;
    private javax.swing.JButton enoikiasi_result_search_button;
    private javax.swing.JButton enoikiasi_spitiou_button;
    private javax.swing.JLabel error_cs;
    private javax.swing.JOptionPane error_optionpane;
    private javax.swing.JFileChooser file_chooser;
    private javax.swing.JFrame home_page;
    private javax.swing.JTextField house_edit_agores;
    private javax.swing.JTextField house_edit_endiaferon;
    private javax.swing.JTextField house_edit_enoikiaseis;
    private javax.swing.JFrame houses_edit;
    private javax.swing.JTextField iban_agora;
    private javax.swing.JTextField iban_edit_agores;
    private javax.swing.JTextField iban_edit_enoikiaseis;
    private javax.swing.JTextField iban_enoikiasi;
    private javax.swing.JTextField imera_edit_agores;
    private javax.swing.JTextField imera_edit_enoikiaseis;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton kataxorisi_button;
    private javax.swing.JFrame kataxorisi_endiaferon;
    private javax.swing.JFrame kataxorisi_menou;
    private javax.swing.JFrame kataxorisi_mesiti;
    private javax.swing.JButton kataxorisi_mesiti_button;
    private javax.swing.JFrame kataxorisi_pelati;
    private javax.swing.JButton kataxorisi_pelati_button;
    private javax.swing.JFrame kataxorisi_spitiou;
    private javax.swing.JButton kataxorisi_spitioy_button;
    private javax.swing.JTextField kostos_edit_agores;
    private javax.swing.JLabel logo;
    private javax.swing.JButton me_vasi_mesitwn_search_button;
    private javax.swing.JButton me_vasi_pedia_search_button;
    private javax.swing.JButton me_vasi_pelatwn_search_button;
    private javax.swing.JFrame mesites_edit;
    private javax.swing.JComboBox mesites_lista_edit_house;
    private javax.swing.JComboBox mesites_lista_spitiou;
    private javax.swing.JLabel mesites_search_errors;
    private javax.swing.JTextField mesitis_edit_agores;
    private javax.swing.JTextField mesitis_edit_enoikiaseis;
    private javax.swing.JTextField mesitis_result;
    private javax.swing.JOptionPane message_optionpane;
    private javax.swing.JRadioButton nai_poleitai_edit_house;
    private javax.swing.JRadioButton nai_poleitai_spiti;
    private javax.swing.JTextField name_edit_pelatis;
    private javax.swing.JTextField name_mesiti;
    private javax.swing.JTextField name_pelati;
    private javax.swing.JRadioButton no_noikiazetai_edit_house;
    private javax.swing.JRadioButton no_noikiazetai_spiti;
    private javax.swing.JRadioButton no_poleita_edit_house;
    private javax.swing.JRadioButton no_poleitai_spiti;
    private javax.swing.JTextField number_edit_pelatis;
    private javax.swing.JTextField number_mesiti;
    private javax.swing.JTextField number_pelati;
    private javax.swing.JTextField onoma_agora;
    private javax.swing.JTextField onoma_enoikiasi;
    private javax.swing.JFrame oracle_configs;
    private javax.swing.JButton oracle_configs_button;
    private javax.swing.JLabel oraclelogo;
    private javax.swing.JPasswordField pass_oracle;
    private javax.swing.JPasswordField pass_postgresql;
    private javax.swing.JFrame pelates_edit;
    private javax.swing.JComboBox pelates_lista_edit_pelatis;
    private javax.swing.JTextField pelatis_edit_endiaferon;
    private javax.swing.JLabel photo_result;
    private javax.swing.JLabel photo_show_edit_house;
    private javax.swing.JButton picture_chooser_button;
    private javax.swing.JButton picture_chooser_edit_house_button;
    private javax.swing.JTextField picture_edit_house;
    private javax.swing.JTextField picture_spitiou;
    private javax.swing.JTextField port_oracle;
    private javax.swing.JTextField port_postgresql;
    private javax.swing.JTextField poso_agora;
    private javax.swing.JTextField poso_edit_enoikiaseis;
    private javax.swing.JTextField poso_enoikiasi;
    private javax.swing.JFrame postgresql_configs;
    private javax.swing.JButton postgresql_configs_button;
    private javax.swing.JLabel postgresqllogo;
    private javax.swing.JTextField region_edit_house;
    private javax.swing.JTextField region_result;
    private javax.swing.JTextField region_spitiou;
    private javax.swing.JButton save_oracle_configs_button;
    private javax.swing.JButton save_postgresql_configs_button;
    private javax.swing.JTextField search_at_mesiti;
    private javax.swing.JTextField search_at_pelati;
    private javax.swing.JComboBox search_for_spitia_use_mesiti_mesites_list;
    private javax.swing.JComboBox search_for_spitia_use_pelatis_pelates_list;
    private javax.swing.JFrame search_me_vasi_mesiti;
    private javax.swing.JFrame search_me_vasi_pelati;
    private javax.swing.JFrame search_results;
    private javax.swing.JButton search_spitia_mesiti_button;
    private javax.swing.JButton search_spitia_use_pelatis_button;
    private javax.swing.JTextField server_oracle;
    private javax.swing.JTextField server_postgresql;
    private javax.swing.JTextField service_oracle;
    private javax.swing.JTextField service_postgresql;
    private javax.swing.JComboBox spitia_lista_edit_house;
    private javax.swing.JComboBox spitia_result;
    private javax.swing.JTextField surname_agora;
    private javax.swing.JTextField surname_edit_pelatis;
    private javax.swing.JTextField surname_enoikiasi;
    private javax.swing.JTextField surname_mesiti;
    private javax.swing.JTextField surname_pelati;
    private javax.swing.JButton teliki_kataxwrisi_mesiti_button;
    private javax.swing.JButton teliki_kataxwrisi_pelati_button;
    private javax.swing.JTextField tm_edit_house;
    private javax.swing.JTextField tm_from_cs;
    private javax.swing.JTextField tm_mexri_cs;
    private javax.swing.JTextField tm_result;
    private javax.swing.JTextField tm_spitiou;
    private javax.swing.JLabel use_pelates_search_errors;
    private javax.swing.JTextField user_oracle;
    private javax.swing.JTextField user_postgresql;
    private javax.swing.JTextField xronia_edit_house;
    private javax.swing.JTextField xronia_from_cs;
    private javax.swing.JTextField xronia_mexri_cs;
    private javax.swing.JTextField xronia_result;
    private javax.swing.JTextField xronia_spitiou;
    private javax.swing.JRadioButton yes_nikiazeta_edit_house;
    private javax.swing.JRadioButton yes_nikiazetai_spiti;
    // End of variables declaration//GEN-END:variables
}
