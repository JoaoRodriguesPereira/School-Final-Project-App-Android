package pt.ipp.estg.projeto;

import java.util.List;

public class Viagem {

    private String data_inicio;
    private String data_fim;
    private List<Local> locais;

    public Viagem() {
    }

    public Viagem(String data_inicio, String data_fim, List<Local> locais) {
        this.data_inicio = data_inicio;
        this.data_fim = data_fim;
        this.locais = locais;
    }

    public String getData_inicio() {
        return data_inicio;
    }

    public void setData_inicio(String data_inicio) {
        this.data_inicio = data_inicio;
    }

    public String getData_fim() {
        return data_fim;
    }

    public void setData_fim(String data_fim) {
        this.data_fim = data_fim;
    }

    public List<Local> getLocais() {
        return locais;
    }

    public void setLocais(List<Local> locais) {
        this.locais = locais;
    }
}
