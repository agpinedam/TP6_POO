package tp6bis;

public class Club {
    
    private long id;
    private int version;
    private String fabricat;
    private double poids;
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
    }
    public String getFabricat() {
        return fabricat;
    }
    public void setFabricat(String fabricat) {
        this.fabricat = fabricat;
    }
    public double getPoids() {
        return poids;
    }
    public void setPoids(double poids) {
        this.poids = poids;
    }
}
