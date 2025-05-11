package client;

import java.rmi.RemoteException;
import java.rmi.NotBoundException;

public class RunClient {
    public static void main(String[] args) {
        Client client = new Client();
        client.startClient();
    }
}
