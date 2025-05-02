package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OrigemSubstituicaoObjetoCompra;

public class FiltroSubstituicaoObjetoCompra {

    private Licitacao licitacao;
    private Contrato contrato;
    private DispensaDeLicitacao dispensaLicitacao;
    private RegistroSolicitacaoMaterialExterno registroSolicitacaoMaterialExterno;
    private ObjetoCompra objetoCompra;
    private Cotacao cotacao;
    private FormularioCotacao formularioCotacao;
    private SolicitacaoMaterial solicitacaoCompra;
    private IntencaoRegistroPreco intencaoRegistroPreco;
    private OrigemSubstituicaoObjetoCompra origemSubstituicao;
    private Boolean contratoRegistroPrecoExterno;
    private String condicaoSql;

    public FiltroSubstituicaoObjetoCompra() {
        contratoRegistroPrecoExterno = false;
    }

    public String getCondicaoSql() {
        return condicaoSql;
    }

    public void setCondicaoSql(String condicaoSql) {
        this.condicaoSql = condicaoSql;
    }

    public Boolean getContratoRegistroPrecoExterno() {
        return contratoRegistroPrecoExterno;
    }

    public void setContratoRegistroPrecoExterno(Boolean contratoRegistroPrecoExterno) {
        this.contratoRegistroPrecoExterno = contratoRegistroPrecoExterno;
    }

    public OrigemSubstituicaoObjetoCompra getOrigemSubstituicao() {
        return origemSubstituicao;
    }

    public void setOrigemSubstituicao(OrigemSubstituicaoObjetoCompra origemSubstituicao) {
        this.origemSubstituicao = origemSubstituicao;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public DispensaDeLicitacao getDispensaLicitacao() {
        return dispensaLicitacao;
    }

    public void setDispensaLicitacao(DispensaDeLicitacao dispensaLicitacao) {
        this.dispensaLicitacao = dispensaLicitacao;
    }

    public RegistroSolicitacaoMaterialExterno getRegistroSolicitacaoMaterialExterno() {
        return registroSolicitacaoMaterialExterno;
    }

    public void setRegistroSolicitacaoMaterialExterno(RegistroSolicitacaoMaterialExterno registroSolicitacaoMaterialExterno) {
        this.registroSolicitacaoMaterialExterno = registroSolicitacaoMaterialExterno;
    }

    public Cotacao getCotacao() {
        return cotacao;
    }

    public void setCotacao(Cotacao cotacao) {
        this.cotacao = cotacao;
    }

    public FormularioCotacao getFormularioCotacao() {
        return formularioCotacao;
    }

    public void setFormularioCotacao(FormularioCotacao formularioCotacao) {
        this.formularioCotacao = formularioCotacao;
    }

    public SolicitacaoMaterial getSolicitacaoCompra() {
        return solicitacaoCompra;
    }

    public void setSolicitacaoCompra(SolicitacaoMaterial solicitacaoCompra) {
        this.solicitacaoCompra = solicitacaoCompra;
    }

    public IntencaoRegistroPreco getIntencaoRegistroPreco() {
        return intencaoRegistroPreco;
    }

    public void setIntencaoRegistroPreco(IntencaoRegistroPreco intencaoRegistroPreco) {
        this.intencaoRegistroPreco = intencaoRegistroPreco;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }


    public void limparFiltros() {
        setIntencaoRegistroPreco(null);
        setFormularioCotacao(null);
        setCotacao(null);
        setSolicitacaoCompra(null);
        setLicitacao(null);
        setDispensaLicitacao(null);
        setRegistroSolicitacaoMaterialExterno(null);
        setContrato(null);
        setObjetoCompra(null);
    }
}
