package pt.ipp.estg.projeto;

public interface PassarDados {

    public void onSetViagem(Boolean emViagem);

    public void onSetMorada(String morada);

    public void onSetMatricula(String matricula);

    public void onSetLatLng(double latitude, double longitude);
}
