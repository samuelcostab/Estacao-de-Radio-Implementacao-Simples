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

    ArrayList<Observer> observadores = new ArrayList<>();
    Thread t;
    Map<Integer, Socket> clientes = new HashMap<Integer, Socket>();
    String[] request;

    private void iniciaModulo(Socket socket) {
        t = new Thread() {
            @Override
            public void run() {
                //System.out.println("2)Mod de Requisição iniciado!");
                // System.out.println("Requisições do Cliente:" + socket.getPort());
                DataInputStream input;
                try {
                    input = new DataInputStream(socket.getInputStream());//Objeto para receber as msg's ;
                    while (clientes.containsKey(socket.getPort())) {
                        String msgCliente = recebeMensagem(input);//Recebe a Msg do Cliente
                        request = msgCliente.split(" ");
                        notifyObservers(socket, request);//Notifica Módulo 3
                    }

                } catch (IOException ex) {
                    //System.out.println("2)Cliente:" + socket.getPort() + " foi Desconectado!");
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
