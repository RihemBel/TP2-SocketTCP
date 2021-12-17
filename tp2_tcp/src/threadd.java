import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import static java.lang.System.out;

public class threadd extends Thread {
    Socket socket;
    compte compte;
    public threadd(Socket socket) {

        this.socket = socket;
    }
    public compte getCompte(String nom){
        for (int i = 0; i <server.comptes.size() ; i++) {
            if (server.comptes.get(i).name.equals(nom))
                return server.comptes.get(i);

        }
        return null;
    }
    public boolean search(String nom){
        for (int i = 0; i <server.comptes.size() ; i++) {
            if (server.comptes.get(i).name.equals(nom))
                return true;

        }
        return false;
    }
    public void run() {
        try {
            BufferedReader in =new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
            System.out.println("new connectionfrom "+socket.getPort());
            boolean connected=false;

            while (true) {
                String message = in.readLine().trim();
                String msgsend = "";
                System.out.println("message " + message);
                // out.println("hello from server");
                if (message.startsWith("logout")) {
                    // String msg= message.substring(1).trim();
                    // search();
                    //  System.out.println(msg+"//"+socket.getPort());
                    out.println("log");
                    socket.close();
                    continue;
                } else if (message.toUpperCase().startsWith("CREATION")) {
                    if (search(message.substring(9))) {
                        msgsend += "compte existe deja";

                    } else {
                        compte compte = new compte(message.substring(9), 0);
                        server.comptes.add(compte);
                        msgsend = "compte est cree avec succes";
                    }
                } else if (message.startsWith("connect")) {
                    connected = false;
                    if (search(message.substring(8))) {
                        connected = true;
                        compte = getCompte(message.substring(8));
                        msgsend = "connected ";
                    } else {
                        msgsend = "tout d'abord il faut deconnec";
                    }

                } else if (message.toUpperCase().startsWith("CREDIT")) {
                    try {
                        if (connected) {
                            System.out.println(message + " " + message.length());
                            System.out.println(message.substring(7) + " " + message.length());

                            float solde = Float.parseFloat(message.substring(7));
                            // System.out.println("solldeee"+message.substring(8));
                            System.out.println("solde credite " + solde);
                            compte.solde += solde;
                            operation operation = new operation(0, solde);
                            compte.list.add(operation);
                            msgsend = "compte credite avec sucses";
                        } else {
                            msgsend = "  connecter svp";
                        }
                    } catch (Exception e) {
                        msgsend = "erreur ";
                        e.printStackTrace();
                    }

                } else if (message.toUpperCase().startsWith("DEBIT")) {
                    try {
                        if (connected && compte.solde >= Float.parseFloat(message.substring(6))) {
                            compte.solde -= Float.parseFloat(message.substring(6));
                            operation operation = new operation(1, Float.parseFloat(message.substring(6)));
                            compte.list.add(operation);
                            msgsend += "compte debitee avec sucses";
                        } else {
                            msgsend += "solde insuf";
                        }
                    } catch (Exception e) {
                        msgsend += "erreur";
                    }
                } else if (message.toUpperCase().startsWith("SOLDE") && connected) {
                    msgsend += "" + compte.solde;

                }
                message = "";
                out.println(msgsend);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}