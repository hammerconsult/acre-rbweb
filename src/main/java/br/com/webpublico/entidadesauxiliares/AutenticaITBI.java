package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.ws.model.WsAutenticaITBI;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 04/02/15
 * Time: 09:10
 * To change this template use File | Settings | File Templates.
 */
public class AutenticaITBI {
    private Date dataVencimento;
    private Integer ano;
    private Integer sequencia;
    private String codigoAutenticacao;

    public AutenticaITBI(WsAutenticaITBI autenticaITBI) {
        this.setDataVencimento(autenticaITBI.getDataVencimento());
        this.setAno(autenticaITBI.getAno());
        this.setSequencia(autenticaITBI.getSequencia());
        this.setCodigoAutenticacao(autenticaITBI.getCodigoAutenticacao());
    }

    public AutenticaITBI() {
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getCodigoAutenticacao() {
        return codigoAutenticacao;
    }

    public void setCodigoAutenticacao(String codigoAutenticacao) {
        this.codigoAutenticacao = codigoAutenticacao;
    }

    public Integer getSequencia() {
        return sequencia;
    }

    public void setSequencia(Integer sequencia) {
        this.sequencia = sequencia;
    }
}
