package pt.ipp.estg.projeto;

public class Local {

    private double latitude;
    private double longitude;
    private double precisao;
    private double velocidade;
    private double altitude;
    private String morada;

    public Local() {
    }

    public Local(double latitude, double longitude, double precisao, double velocidade, double altitude, String morada) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.precisao = precisao;
        this.velocidade = velocidade;
        this.altitude = altitude;
        this.morada = morada;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getPrecisao() {
        return precisao;
    }

    public void setPrecisao(double precisao) {
        this.precisao = precisao;
    }

    public double getVelocidade() {
        return velocidade;
    }

    public void setVelocidade(double velocidade) {
        this.velocidade = velocidade;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getMorada() {
        return morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }
}
