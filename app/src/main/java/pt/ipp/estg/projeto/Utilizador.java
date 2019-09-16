package pt.ipp.estg.projeto;

import java.util.List;

public class Utilizador {

    private String email;
    private List<Viagem> viagens;

    public Utilizador() {
    }

    public Utilizador(String email, List<Viagem> viagens) {
        this.email = email;
        this.viagens = viagens;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public List<Viagem> getViagens() {
        return viagens;
    }

    public void setViagens(List<Viagem> viagens) {
        this.viagens = viagens;
    }
}
