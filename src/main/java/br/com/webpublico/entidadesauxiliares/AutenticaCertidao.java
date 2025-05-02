package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoCadastroDoctoOficial;
import br.com.webpublico.enums.TipoCertidao;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.ws.model.WsAutenticaCertidao;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 31/01/15
 * Time: 14:37
 * To change this template use File | Settings | File Templates.
 */
public class AutenticaCertidao {

    private TipoCertidao tipoCertidao;
    private TipoCadastroDoctoOficial tipoCadastro;
    private Date dataEmissao;
    private Date dataVencimento;
    private String codigoVerificacao;
    private Integer numeroDocumento;
    private String cpfCnpj;

    public AutenticaCertidao(WsAutenticaCertidao autenticaCertidao) {
        this.setTipoCertidao(autenticaCertidao.getTipoCertidao());
        this.setTipoCadastro(autenticaCertidao.getTipoCadastro());
        this.setDataEmissao(autenticaCertidao.getDataEmissao());
        this.setDataVencimento(autenticaCertidao.getDataVencimento());
        this.setNumeroDocumento(autenticaCertidao.getNumeroDocumento());
        this.setCodigoVerificacao(autenticaCertidao.getCodigoVerificacao());
        this.setCpfCnpj(autenticaCertidao.getCpfCnpj());
    }

    public AutenticaCertidao() {
    }

    public Date getDataEmissao() {
        if (dataEmissao != null) {
            return DataUtil.dataSemHorario(dataEmissao);
        }
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }


    public Date getDataVencimento() {
        if (dataVencimento != null) {
            return DataUtil.dataSemHorario(dataVencimento);
        }
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

