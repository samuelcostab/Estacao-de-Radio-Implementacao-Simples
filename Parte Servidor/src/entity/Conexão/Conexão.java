﻿package entity.Conexão;

import interfaces.Observable;
import interfaces.Observer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Conexão implements Observable{
	/*
	Esta Classe é responsável por estabelecer a conexão do Cliente com o Servidor.
	Basicamente ela recebe a solicitação do Cliente (via TCP) e repassa para o
	Observador, a classe de RequisiçãoCliente, informando:"Tem um novo cliente 
	conectado, você ja pode receber as requisições dele".

	*/

    private Thread t;
    private Observer observador;
    private ServerSocket server;
    
    public Conexão(ServerSocket socketServer){
        this.server = socketServer;
           t = new Thread(){
            @Override
            public void run(){
                try {
                    while(true){
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

    //Geters
    public Thread getThread(){
	    return this.t;
    }
    public Observer getObservador{
	    return this.observador;
    }
    public ServerSocket getServerSocket(){
	    return this.server;
    }
    
    
    
}
