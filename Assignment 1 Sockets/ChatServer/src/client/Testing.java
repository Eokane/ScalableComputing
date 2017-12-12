package client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Testing {

    private  String host; 
    private static final int portNumber = 5559; // Don't want to change this variable
    private String clientName;
    private String serverHost;
    private int serverPort;
    private Scanner userInputScanner;

    public static void main(String[] args){
        String readInName = null;
        Scanner scan = new Scanner(System.in);
        System.out.println("Please input Client Name to Close application type 'exit':");
        while(readInName == null || readInName.trim().equals("")) {
            readInName = scan.nextLine();
            if(readInName.trim().equals("")){
                System.out.println("Invalid. Please enter again:");
            }
            if(readInName.trim().equals("exit")){
                System.out.println("Closing Chat");
                System.exit(0);
            }
        }
        
        String hostName = null;
        Scanner hostscan = new Scanner(System.in);
        System.out.println("Please input host name i.e. Chat room name:");
        while(hostName == null || hostName.trim().equals("")) {
            hostName = hostscan.nextLine();
            if(hostName.trim().equals("")){
                System.out.println("Invalid. Please enter again:");
            }
        
        }
        Testing client = new Testing(readInName, hostName, portNumber);
     
    }


    private Testing(String clientName, String host, int portNumber){
        this.clientName = clientName;
        this.serverHost = host;
        this.serverPort = portNumber;
    }
}
        
  
