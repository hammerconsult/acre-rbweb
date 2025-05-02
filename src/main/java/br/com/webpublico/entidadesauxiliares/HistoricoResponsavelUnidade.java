package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

/**
 * Created by HardRock on 24/05/2017.
 */
public class HistoricoResponsavelUnidade {

    private String nome;
    private Date inicioVigencia;
    private Date fimVigencia;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }
}
