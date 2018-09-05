package entity.requisicaoCliente;

import interfaces.Observable;
import interfaces.Observer;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequisicaoCliente implements Observer, Observable {
    /*
    Esta Classe é responsável por receber as requisições dos Clientes e
    repassar para o Observador, a classe RespostaCliente, para então lá serem
    tratadas essas requisições.    
    */

    private ArrayList<Observer> observadores = new ArrayList<>();
    private Thread t;
    private Map<Integer, Socket> clientes = new HashMap<Integer, Socket>();
    private String[] request;

    private void iniciaModulo(Socket socket) {
        t = new Thread() {
            @Override
            public void run() {
                DataInputStream input;
                try {
                    input = new DataInputStream(socket.getInputStream());
                    while (clientes.containsKey(socket.getPort())) {
                        String msgCliente = recebeMensagem(input);//Recebe a mensagem que o Cliente enviar.
                        request = msgCliente.split(" ");
                        notifyObservers(socket, request);
                    }

                } catch (IOException ex) {
                    Socket s = clientes.remove(socket.getPort());
                    notifyObservers(s);
                } finally {
                    input = null;
                }
            }

        };
        this.iniciaThread();

    }

    public void iniciaThread() {
        t.start();
    }

    public void paraThread() {
        if (t != null) {
            t.interrupt();
        }
    }

    @Override
    public void upDate(Object ob) {
        Socket socket = (Socket) ob;
        if (clientes.containsKey(socket.getPort())) {
            iniciaModulo(clientes.get(socket.getPort()));
        } else {
            clientes.put(socket.getPort(), socket);
            iniciaModulo(clientes.get(socket.getPort()));
        }

    }

    @Override
    public void upDate(Object ob, Object ob2) {
        //aqui não faz nada
    }

    @Override
    public void registerObserver(Observer observador) {
        this.observadores.add(observador);
    }

    @Override
    public void removeObserver(Observer ob) {
        this.observadores.remove(ob);
    }

    @Override
    public void notifyObservers(Object ob) {//Notifica ao Estação
        for (int i = 1; i < observadores.size(); i++) {
            this.observadores.get(i).upDate(ob);
        }
    }

    @Override
    public void notifyObservers(Object ob, Object ob2) {//Notifica ao RespostaServidor
        this.observadores.get(0).upDate(ob, ob2);
    }

    public String recebeMensagem(DataInputStream input) throws IOException {
        String msg = input.readUTF();

        return msg;
    }

}
