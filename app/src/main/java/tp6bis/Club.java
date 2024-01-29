package tp6bis;

public class Club {
    
    private long id;
    private int version;
    private String fabricant;
    private double poids;
    
    public Club(long id, String fabricant){
        this.id = id;
        this.fabricant = fabricant;
    }

    public Club() {
    }

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
        return fabricant;
    }
    public void setFabricat(String fabricant) {
        this.fabricant = fabricant;
    }
    public double getPoids() {
        return poids;
    }
    public void setPoids(double poids) {
        this.poids = poids;
    }
}
