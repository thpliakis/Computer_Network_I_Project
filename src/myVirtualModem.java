import java.io.*;
import java.util.*;
import ithakimodem.*;


public class myVirtualModem {

    int speed = 50000;
    int timeOut = 4000;
    private Modem modem;

    public static void main(String[] args) throws InterruptedException {
        myVirtualModem userApplication = new myVirtualModem();
        //userApplication.currentTimeIs();

        userApplication.echoPackage("E8228\r","/home/teras/IdeaProjects/Computer_Networks_I_Project/results/echo_package_time_results");
        userApplication.Image_request("M2716\r","/home/teras/IdeaProjects/Computer_Networks_I_Project/results/errorFreeImage.jpeg" );
        userApplication.Image_request("G0459\r","/home/teras/IdeaProjects/Computer_Networks_I_Project/results/errorImage.jpeg" );
        userApplication.arqRequest("Q1189\r", "R3134\r","/home/teras/IdeaProjects/Computer_Networks_I_Project/results/Arq.txt","/home/teras/IdeaProjects/Computer_Networks_I_Project/results/nackResults.txt" );
        userApplication.Image_request("P0596T=225735403737T=225740403735T=225742403733T=225735403733\r", "/home/teras/IdeaProjects/Computer_Networks_I_Project/results/gpsImage.jpeg");
        userApplication.Gps_request("P0596R=1000135\r","/home/teras/IdeaProjects/Computer_Networks_I_Project/results/gpsPackages.txt" );

    }

    // Constructor
    public void myVirtualModem() {

        int k;
        String rxmessage = ""; // buffer for writing the message from ithaki

        modem = new Modem();
        modem.setSpeed(speed);
        modem.setTimeout(timeOut);
        modem.open("ithaki");    // action, thelo na sinomiliso me thn ithaki
        modem.write("atd2310ithaki\r".getBytes());  // Connect local modem to remote ithaki modem.

        //System.out.println("end of welcome message");
        for (; ; ) {
            try {
                k = modem.read();   // k 0-255, blocking entolh
                if (k == -1) {
                    System.out.println("error message");
                    break;
                }
                //System.out.print((char) k);
                rxmessage = rxmessage + (char) k;
                if (rxmessage.indexOf("\r\n\n\n") > -1) {
                    System.out.println("end of welcome message");
                    break;
                }
            } catch (Exception x) {
                break;
            }
        }
        //modem.close();
    }

    public int echoPackage(String pack_code, String path) {
        System.out.println("EchoPackage");

        this.myVirtualModem();
        BufferedWriter bw = null;
        File file;
        FileWriter fw;

        try {
            // connection timeout for echooackage
            long connectionStart = System.currentTimeMillis();
            long connectionFinish = connectionStart + 30000; // 60000/1000 = 60 seconds
            long packageTxTime = 0, packageRxTime = 0;  // transmit and receive times for a package
            int numOfPackages = 0;
            long avgTime = 0;  // Average time for packages
            String rxmessage = "";
            int k;

            // File and buffer io
            file = new File(path);
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);

            while ((System.currentTimeMillis() < connectionFinish) && (numOfPackages < 100)) {
                packageTxTime = System.currentTimeMillis();
                modem.write(pack_code.getBytes());
                rxmessage = "";
                for (; ; ) {
                    try {
                        k = modem.read();
                        //System.out.print((char) k);
                        rxmessage = rxmessage + (char) k;
                        if (rxmessage.indexOf("PSTOP") > -1) {
                            //System.out.println("package is here");
                            break;
                        }
                        if (k == -1) {
                            //System.out.println("Read a whole package");
                            System.out.println("Maybe the packet is here\n");
                            break;
                        }
                    } catch (Exception x) {
                        System.out.println(x);
                        return 0;
                    }
                }
                //System.out.println("\n");
                //System.out.println(rxmessage);
                packageRxTime = System.currentTimeMillis();
                numOfPackages += 1;
                avgTime = avgTime + (packageRxTime - packageTxTime);
                String time = String.valueOf(packageRxTime - packageTxTime) + " \n";

                bw.write(rxmessage + "     ........received in: " + time);
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
        } catch (Exception x) {
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

    public void Image_request(String pack_code, String file_path) {
        System.out.println("Image Request");
        this.myVirtualModem();

        try {
            File file = new File(file_path);
            OutputStream image = new FileOutputStream(file);
            modem.write(pack_code.getBytes());
            String rxmessage = "";

            long packageTxTime = 0, packageRxTime = 0;  // transmit and receive times for a package
            packageTxTime = System.currentTimeMillis();
            int k;
            boolean flag = false;

            for (; ; ) {
                try {
                    k = modem.read();   // k 0-255, blocking entolh
                    if (k == -1) break;
                    rxmessage = rxmessage + (char) k;
                    //System.out.println(k);

                    if (rxmessage.indexOf("ÿØ") > -1) {
                        image.write(255);
                        image.write(216);
                        System.out.println("start reading of image");
                        rxmessage = "";
                        flag = true;
                    }
                    if (flag) {
                        image.write(k);
                    }
                    if (rxmessage.indexOf("ÿÙ") > -1) {
                        System.out.println("end of image");
                        image.write(k);
                        break;
                    }
                } catch (Exception x) {
                    break;
                }
            }
            packageRxTime = System.currentTimeMillis();
            System.out.println("Finished receiving the image after " + (packageRxTime - packageTxTime) / 1000 + "seconds!");
            System.out.println("true time " + (packageRxTime - packageTxTime) / 1000 + "seconds!");

            image.close();
            modem.close();
        } catch (Exception x) {
            System.out.println("Exception in Image request");
        }
    }

    public int Gps_request(String gps_code, String path) {
        System.out.println("Gps reuest");
        this.myVirtualModem();
        modem.write(gps_code.getBytes());

        BufferedWriter bw = null;
        File file;
        FileWriter fw;
        String rxmessage = ""; // buffer for writing the message from ithaki
        int k;
        try {
            file = new File(path);
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);

            for (; ; ) {
                try {
                    k = modem.read();   // k 0-255, blocking entolh
                    if (k == -1) break;
                    //System.out.print((char) k);
                    rxmessage = rxmessage + (char) k;
                    if (rxmessage.indexOf("STOP ITHAKI GPS TRACKING") > -1) {
                        System.out.println("\nend of gps message");
                        break;
                    }
                } catch (Exception x) {
                    break;
                }
            }
            //for (String a : traces)
            //    System.out.println(a);
            bw.write(rxmessage);
            bw.close();
            modem.close();
        }catch (Exception e){
            System.out.println("\nException in echoPackage! ");
            return 0;
        }
        return 0;
    }

    public void arqRequest(String ackCode, String nackCode, String pathArq, String pathResults) {
        System.out.println("arq Request");
        this.myVirtualModem();

        BufferedWriter bwArq = null, bwResults = null;
        File fileArq, fileResults;
        FileWriter fwArq, fwResults;
        String rxmessage = ""; // buffer for writing the message from ithaki
        long connectionStart = System.currentTimeMillis();
        long connectionEnd = connectionStart + 10000; // 60000/1000 = 60 seconds
        long packageTxTime = 0, packageRxTime = 0, packageTime;  // transmit and receive times for a package
        int numOfAttemps = 0;
        boolean correctTransmit = true;
        int[] arrayOfAttempts = new int[15];
        for(int i=0;i<15;i++)
            arrayOfAttempts[i]=0;
        long avgTime = 0;  // Average time for packages
        int k, fcs;
        String[] parseRxmessage;
        char[]  xxx16;
        byte x;
        int numOfPackages = 0;


        try {
            // File and buffer io
            fileArq = new File(pathArq);
            fileResults = new File(pathResults);
            fwArq = new FileWriter(fileArq);
            bwArq = new BufferedWriter(fwArq);
            fwResults = new FileWriter(fileResults);
            bwResults = new BufferedWriter(fwResults);

            while (System.currentTimeMillis() < connectionEnd) {

                if(correctTransmit){
                    numOfAttemps = 1;
                    packageTxTime = System.currentTimeMillis();
                    modem.write(ackCode.getBytes());
                }else {
                    numOfAttemps++;
                    modem.write(nackCode.getBytes());
                }
                rxmessage = "";
                for (;;) {
                    try {
                        k = modem.read();   // k 0-255, blocking entolh
                        //System.out.print((char) k);
                        rxmessage = rxmessage + (char) k;

                        if (rxmessage.indexOf("PSTOP") > -1 || k == -1)
                            break;

                    } catch (Exception e) {
                        System.out.println("Catched exception e");
                        break;
                    }
                }
                //System.out.println(rxmessage);
                packageRxTime = System.currentTimeMillis();
                packageTime = packageRxTime - packageTxTime;
                parseRxmessage = rxmessage.split(" ");

                //System.out.println("\n"+parseRxmessage[0]+"_"+parseRxmessage[1]+"_"+parseRxmessage[2]+"_"+parseRxmessage[3]+"_"+parseRxmessage[4]+"_"+parseRxmessage[5]+"_"+parseRxmessage[6]);

                fcs =  Integer.parseInt(parseRxmessage[5]);
                //System.out.println(fcs);
                xxx16 = parseRxmessage[4].toCharArray();
                //System.out.println(xxx16);
                x = (byte) xxx16[1];
                for(int i=2; i<xxx16.length-1; i++)
                    x = (byte) (x^xxx16[i]);

                //System.out.println(x);
                if((int)x == fcs){
                    numOfPackages++;
                    correctTransmit = true;
                    bwResults.write((numOfPackages + ")" + packageTime + " ; " + numOfAttemps + "\r\n"));
                    bwResults.flush();
                    bwArq.write(rxmessage + "\n");
                    bwArq.flush();
                    //arrayOfAttempts[numOfAttemps]++;
                }else{
                    correctTransmit = false;
                    bwArq.write( rxmessage + "  wrong package: " + parseRxmessage[3] + ") " + "\n");
                    bwArq.flush();
                }
            }
            bwArq.close();
            bwResults.close();

            modem.close();
        } catch (Exception e) {
            System.out.println("\nException in ArqRequest! ");
        }
    }
}