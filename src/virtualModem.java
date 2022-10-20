import java.io.*;
import java.util.*;
import ithakimodem.*;

public class virtualModem{

    public static void main(String[] args) {
        virtualModem app = new virtualModem();
        app.demo();
    }

    public void demo(){
        int k;

        Modem modem;
        modem = new Modem();
        modem.setSpeed(1000);
        modem.setTimeout(2000);
        modem.open("ithaki");    // action, thelo na sinomiliso me thn ithaki

        String rxmessage = ""; // buffer for writing the message from ithaki
        String txmessage = "test\r";

        // Listening loop, diabazo gramma gramma, byte after byte
        for(;;){
            try{
                k = modem.read();   // k 0-255, blocking entolh
                if(k==-1) break;
                System.out.print((char)k);
                rxmessage = rxmessage+(char)k;
                if (rxmessage.indexOf("\r\n\n\n")>-1){
                    System.out.println("end of welcome message");
                    break;
                }
            } catch (Exception x){
                break;
            }
        }

        rxmessage="";
        System.
        // h methodos getBytes epistrefei to txmessage se array me ta grammata
        // kai h mehodos write fortoonei ton transimt register me olous tous xarakthres enan pros enan
        modem.write(txmessage.getBytes());

        // mpaino se mode na akouso thn ithaki
        // seriaka grafo auta pou moy stelnei h ithaki
        for(;;){
            try{
                k = modem.read();   // k 0-255, blocking entolh
                if(k==-1) {
                    System.out.println("connection failed");
                    break;
                }
                rxmessage = rxmessage+(char)k;
                System.out.println(rxmessage);
                if (rxmessage.indexOf("PSTOP\r\n")>-1){
                    System.out.println("packet is here");
                    break;
                }
                } catch (Exception x){
                    System.out.println(x);
                    break;
            }

        

        modem.close();


}
