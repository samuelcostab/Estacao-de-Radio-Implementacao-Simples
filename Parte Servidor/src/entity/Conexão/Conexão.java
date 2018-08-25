package entity.Conexão;

import interfaces.Observable;
import interfaces.Observer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Conexão implements Observable{
    Thread t;
    Observer observador;
    ServerSocket server;
    
    public Conexão(ServerSocket socketServer){
        this.server = socketServer;
           t = new Thread(){
            @Override
            public void run(){
                try {
                    while(true){//Aceita Conexões e Notifica ao Observador o socket de conexão
                        Socket socket = server.accept();
                        notifyObservers(socket);
                    }

                } catch (IOException ex) {
                    paraThread();
                }
            }

        };
        this.iniciaThread();

    }

    public void iniciaThread(){
     t.start();
    }
    
    public void paraThread(){
        if(t != null)
        t.interrupt();
    }
    

    @Override
    public void registerObserver(Observer ob) {
        this.observador = ob;
    }

    @Override
    public void removeObserver(Observer ob) {
        this.observador = null;
    }

    @Override
    public void notifyObservers(Object ob) {
        observador.upDate(ob);
    }

    @Override
    public void notifyObservers(Object ob, Object ob2) {
    }

    
    
    
}
