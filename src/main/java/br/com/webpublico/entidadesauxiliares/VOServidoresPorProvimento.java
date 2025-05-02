package br.com.webpublico.entidadesauxiliares;

/**
 * Created by carlos on 28/01/16.
 */
public class VOServidoresPorProvimento {
    private String servidor;
    private String nome;
    private String tipoProvimento;
    private String dataProvimento;
    private String unidade;

    public VOServidoresPorProvimento() {
    }

    public String getDataProvimento() {
        return dataProvimento;
    }

    public void setDataProvimento(String dataProvimento) {
        this.dataProvimento = dataProvimento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getServidor() {
        return servidor;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    public String getTipoProvimento() {
        return tipoProvimento;
    }

    public void setTipoProvimento(String tipoProvimento) {
        this.tipoProvimento = tipoProvimento;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }
}
