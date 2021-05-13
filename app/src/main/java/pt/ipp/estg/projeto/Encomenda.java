package pt.ipp.estg.projeto;

public class Encomenda {

    private String mCodigo;
    private String mDescricao;

    public Encomenda() {
    }

    public Encomenda(String mCodigo, String mDescricao) {
        this.mCodigo = mCodigo;
        this.mDescricao = mDescricao;
    }

    public String getmCodigo() {
        return mCodigo;
    }

    public void setmCodigo(String mCodigo) {
        this.mCodigo = mCodigo;
    }

    public String getmDescricao() {
        return mDescricao;
    }

    public void setmDescricao(String mDescricao) {
        this.mDescricao = mDescricao;
    }
}
