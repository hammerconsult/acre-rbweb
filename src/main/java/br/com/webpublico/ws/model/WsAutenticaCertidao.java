package br.com.webpublico.ws.model;

import br.com.webpublico.enums.TipoCadastroDoctoOficial;
import br.com.webpublico.enums.TipoCertidao;
import br.com.webpublico.enums.TipoValidacaoDoctoOficial;

import java.util.Date;

public class WsAutenticaCertidao {

    private String cpfCnpj;
    private TipoCertidao tipoCertidao;
    private TipoCadastroDoctoOficial tipoCadastro;
    private Date dataEmissao;
    private Date dataVencimento;
    private String codigoVerificacao;
    private Integer numeroDocumento;

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getCodigoVerificacao() {
        return codigoVerificacao;
    }

    public void setCodigoVerificacao(String codigoVerificacao) {
        this.codigoVerificacao = codigoVerificacao;
    }

    public Integer getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(Integer numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public TipoCertidao getTipoCertidao() {
        return tipoCertidao;
    }

    public void setTipoCertidao(TipoCertidao tipoCertidao) {
        this.tipoCertidao = tipoCertidao;
    }

    public TipoCadastroDoctoOficial getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(TipoCadastroDoctoOficial tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }
}

