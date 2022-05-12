package server;

public interface Wrapperable extends Comparable<Wrapperable> {
    void setId(long id);
    long getId();
    double countWeight();
    default int compareTo(Wrapperable org) {
        return Double.compare(this.countWeight(), org.countWeight());
    }
}
