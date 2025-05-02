package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.nfse.enums.SituacaoNota;
import br.com.webpublico.nfse.enums.TipoDocumentoNfse;
import br.com.webpublico.util.DataUtil;
import org.apache.logging.log4j.util.Strings;

import java.util.Date;

public class FiltroRelatorioTomadoresServico {

    private Date dataInicial;
    private Date dataFinal;
    private String cnpjInicial;
    private String cnpjFinal;
    private Pessoa pessoa;
    private TipoDocumentoNfse tipoDocumentoNfse;
    private SituacaoNota situacaoNota;
    private Boolean mostrarDataEmissao;

    public FiltroRelatorioTomadoresServico() {
        dataInicial = new Date();
        dataFinal = new Date();
        mostrarDataEmissao = Boolean.TRUE;
    }

    public String montarDescricaoFiltros() {
        StringBuilder retorno = new StringBuilder();
        if (dataInicial != null) {
            retorno.append(" Data Inicial: ").append(DataUtil.getDataFormatada(dataInicial)).append("; ");
        }
        if (dataFinal != null) {
            retorno.append(" Data Final: ").append(DataUtil.getDataFormatada(dataFinal)).append("; ");
        }
        if (Strings.isNotEmpty(cnpjInicial)) {
            retorno.append(" CNPJ Inicial: ").append(cnpjInicial).append("; ");
        }
        if (Strings.isNotEmpty(cnpjFinal)) {
            retorno.append(" CNPJ Final: ").append(cnpjFinal).append("; ");
        }
        if (pessoa != null) {
            retorno.append(" Pessoa: ").append(pessoa.getNome()).append("; ");
        }
        if (tipoDocumentoNfse != null) {
            retorno.append(" Tipo de Documento: ").append(tipoDocumentoNfse.getDescricao()).append("; ");
        }
        if (situacaoNota != null) {
            retorno.append(" Situação: ").append(situacaoNota.getDescricao()).append("; ");
        }
        return retorno.toString();
    }

    public void validarCampos() throws ValidacaoException {
        ValidacaoException coe = new ValidacaoException();
        if (dataInicial == null) {
            coe.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado");
        }
        if (dataFinal == null) {
            coe.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado");
        }
        if (dataInicial.after(dataFinal)) {
            coe.adicionarMensagemDeOperacaoNaoPermitida("O campo Data Final deve ser posterior que a Data Inicial.");
        }
        coe.lancarException();
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getCnpjInicial() {
        return cnpjInicial;
    }

    public void setCnpjInicial(String cnpjInicial) {
        this.cnpjInicial = cnpjInicial;
    }

    public String getCnpjFinal() {
        return cnpjFinal;
    }

    public void setCnpjFinal(String cnpjFinal) {
        this.cnpjFinal = cnpjFinal;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public TipoDocumentoNfse getTipoDocumentoNfse() {
        return tipoDocumentoNfse;
    }

    public void setTipoDocumentoNfse(TipoDocumentoNfse tipoDocumentoNfse) {
        this.tipoDocumentoNfse = tipoDocumentoNfse;
    }

    public SituacaoNota getSituacaoNota() {
        return situacaoNota;
    }

    public void setSituacaoNota(SituacaoNota situacaoNota) {
        this.situacaoNota = situacaoNota;
    }

    public Boolean getMostrarDataEmissao() {
        return mostrarDataEmissao;
    }

    public void setMostrarDataEmissao(Boolean mostrarDataEmissao) {
        this.mostrarDataEmissao = mostrarDataEmissao;
    }
}
