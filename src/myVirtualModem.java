import java.io.*;
import java.util.*;
import ithakimodem.*;


public class myVirtualModem{

    int speed = 50000;
    int timeOut = 2000;
    private Modem modem;
    public static void main(String[] args) {
        myVirtualModem userApplication = new myVirtualModem();
        //userApplication.currentTimeIs();
        //userApplication.echoPackage("E8948\r");
        userApplication.echoPackage("E1376\r");
        //userApplication.Image_request("M0215\r","/home/teras/IdeaProjects/Computer_Networks_I_Project/results/image.jpeg" );
    }

    // Constructor
    public void myVirtualModem(){

        int k;
        int k1=217;
        System.out.println((char)k1);
        String rxmessage = ""; // buffer for writing the message from ithaki

        modem = new Modem();
        modem.setSpeed(speed);
        modem.setTimeout(timeOut);
        modem.open("ithaki");    // action, thelo na sinomiliso me thn ithaki
        modem.write("atd2310ithaki\r".getBytes());  // Connect local modem to remote ithaki modem.

        System.out.println("end of welcome message");
        for(;;){
            try{
                k = modem.read();   // k 0-255, blocking entolh
                if(k==-1) {
                    System.out.println("error message");
                    break;
                }
                System.out.print((char)k);
                rxmessage = rxmessage+(char)k;
                if (rxmessage.indexOf("\r\n\n\n")>-1){
                    //System.out.println("end of welcome message");
                    break;
                }
            } catch (Exception x){
                break;
            }
        }
        //modem.close();
    }

    public void currentTimeIs(){
        long millis = System.currentTimeMillis();
        long sec = millis/1000;
        long csec = sec%60;
        long min = sec/60;
        long cmin = min%60;
        long hours = min/60;
        long chours = hours%24;
        System.out.println("The time is: "+chours+":"+cmin+":"+csec+"\n");

        /*
        System.out.println(millis);
        System.out.println(csec);
        System.out.println(cmin);
        System.out.println(chours);
        byte[] s = "atd2310ithaki\r".getBytes();
        for(int i=0;i<s.length;i++){
            System.out.println(s[i]);
        }
        */
    }

    public int echoPackage(String pack_code){

        this.myVirtualModem();
        BufferedWriter bw = null;
        File file ;
        FileWriter fw ;
        String myContent = "This String would be written" + " to the specified File";

        try{
            // connection timeout for echooackage
            long connectionStart = System.currentTimeMillis();
            long connectionFinish = connectionStart + 120000; // 60000/1000 = 60 seconds
            long packageTxTime = 0, packageRxTime = 0;  // transmit and receive times for a package
            int numOfPackages=0;
            long avgTime=0;  // Average time for packages
            String rxmessage = "";
            int k;

            // File and buffer io
            file = new File("/home/teras/IdeaProjects/Computer_Networks_I_Project/results/echo_package_time_results");
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            //bw.write(myContent);
            //System.out.println("File written Successfully");

            while((System.currentTimeMillis() < connectionFinish) && (numOfPackages<100)){
                packageTxTime = System.currentTimeMillis();
                modem.write(pack_code.getBytes());
                rxmessage = "";
                for(;;){
                    try{
                        k = modem.read();
                        //System.out.println(k);
                        if(k == -1) {
                            //System.out.println("Read a whole package");
                            break;
                        }
                        rxmessage = rxmessage + (char)k;
                        if(rxmessage.indexOf("\r\n\n\n")>-1){
                            System.out.println("packet is here");
                        }
                    }catch (Exception x){
                        System.out.println(x);
                        return 0;
                    }
                }
                    System.out.println("Maybe the packet is here\n");
                    packageRxTime = System.currentTimeMillis();
                    numOfPackages +=1;
                    avgTime = avgTime + (packageRxTime - packageTxTime);
                    String time = String.valueOf(packageRxTime - packageTxTime)+" \n";

                    bw.write(rxmessage +"     ........received in: "+ time );
                    bw.newLine();
            }
            avgTime = avgTime / numOfPackages;
            System.out.println("avg time is " + avgTime);
            System.out.println("number of packages received " + numOfPackages);
            System.out.println("in time " + avgTime * numOfPackages);
            bw.newLine();
            bw.write("avg time is " + avgTime + "\n");
            bw.write("number of packages received " + numOfPackages + "\n");
            bw.write("in time " + avgTime * numOfPackages + "\n");

            bw.flush();
            bw.close();

            modem.close();
        } catch (Exception x){
            System.out.println("\nException in echoPackage! ");
            return 0;
        }
        /*
        finally
        {
            try{
                if(bw!=null)
                    bw.close();
            }catch(Exception ex){
                System.out.println("Error in closing the BufferedWriter"+ex);
            }
        }
         */
        return 0;
    }

    public void Image_request(String pack_code, String file_path){
        this.myVirtualModem();

        try{
            File file = new File(file_path);
            OutputStream image = new FileOutputStream(file);
            modem.write(pack_code.getBytes());
            String rxmessage="";

            long packageTxTime = 0, packageRxTime = 0;  // transmit and receive times for a package
            packageTxTime = System.currentTimeMillis();
            int k,kPrevius=0;
            for(;;) {
                try {
                    k = modem.read();   // k 0-255, blocking entolh
                    /*if (k == 216 && kPrevius==255) {
                        //...
                    }*/
                    kPrevius = k;
                    rxmessage = rxmessage + (char)k;
                    System.out.print((char) k);
                    if (rxmessage.indexOf("ÿÙ") > -1) {
                        System.out.println("end of image");
                        break;
                    }
                } catch (Exception x) {
                    break;
                }
            }
            packageRxTime = System.currentTimeMillis();
            System.out.println("Finished receiving the image after:"+(packageRxTime - packageTxTime)/1000+"seconds!");
            System.out.println("true time:"+(packageRxTime - packageTxTime)/1000+"seconds!");

            image.close();
            modem.close();
        }catch (Exception x){
            System.out.println("Exception in Image request");
        }
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
    }
}