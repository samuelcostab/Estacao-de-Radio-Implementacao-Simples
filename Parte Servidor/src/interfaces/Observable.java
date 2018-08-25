package interfaces;


public interface Observable {
    
    public void registerObserver(Observer ob);
    public void removeObserver(Observer ob);
    public void notifyObservers(Object ob);
    public void notifyObservers(Object ob, Object ob2);

    
}
