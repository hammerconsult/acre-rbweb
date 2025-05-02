package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ItemProcessoDeCompra;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 07/03/15
 * Time: 17:16
 * To change this template use File | Settings | File Templates.
 */
public class ArquivoLicitacaoPropostaFornecedor {

    private Long idLicitacao;
    private Long idFornecedor;
    private Long idRepresentante;
    private Date data;
    private String licitacao;
    private String nomeArquivo;
    private String cpfCnpjFornecedor;
    private String fornecedor;
    private String cpfCnpjRepresentante;
    private String representante;
    private String instrumento;
    private String tipoEmpresa;
    private Long prazoValidade;
    private String tipoPrazoValidade;
    private Long prazoExecucao;
    private String tipoPrazoExecucao;
    private ArquivoItemLicitacaoPropostaFornecedor[] itens;

    public ArquivoLicitacaoPropostaFornecedor(ArquivoPropostaFornecedor selecionado) {
        idLicitacao = selecionado.getLicitacao().getId();
        licitacao = selecionado.getLicitacao().getNumeroLicitacao() + " - " + selecionado.getLicitacao().getModalidadeLicitacao().getDescricao() + "/" + selecionado.getLicitacao().getExercicio().getAno();
        nomeArquivo = selecionado.getLicitacao().getNumeroLicitacao().toString();
        idFornecedor = selecionado.getFornecedor() !=null ? selecionado.getFornecedor().getId() : null;
        fornecedor = selecionado.getFornecedor() !=null ? selecionado.getFornecedor().getNome() : "";
        cpfCnpjFornecedor = selecionado.getFornecedor() !=null ? selecionado.getFornecedor().getCpf_Cnpj() : "";
        idRepresentante = selecionado.getRepresentante() !=null ? selecionado.getRepresentante().getId() : null;
        representante = selecionado.getRepresentante() !=null ? selecionado.getRepresentante().getNome() : "";
        cpfCnpjRepresentante = selecionado.getRepresentante() !=null ? selecionado.getRepresentante().getCpf() : "";
        data = selecionado.getData();
        tipoEmpresa = (selecionado.getFornecedor() != null && selecionado.getFornecedor().getTipoEmpresa() != null) ? selecionado.getFornecedor().getTipoEmpresa().name() : "INDEFINIDO";
        preparaItens(selecionado.getItens());
    }

    public ArquivoLicitacaoPropostaFornecedor() {
    }

    private void preparaItens(List<ItemProcessoDeCompra> itens) {
        this.itens = new ArquivoItemLicitacaoPropostaFornecedor[itens.size()];
        int posicao = 0;
        for (ItemProcessoDeCompra itemDaVez : itens) {
            ArquivoItemLicitacaoPropostaFornecedor item = new ArquivoItemLicitacaoPropostaFornecedor();
            item.setNumeroLote(itemDaVez.getLoteProcessoDeCompra().getNumero());
            item.setDescricaoLote(itemDaVez.getLoteProcessoDeCompra().getDescricao());
            item.setDescricao(itemDaVez.getDescricao());
            item.setDescricaoProduto("");
            item.setModelo("");
            item.setNumeroItem(itemDaVez.getNumero());
            item.setQuantidade(itemDaVez.getQuantidade().intValue());
            this.itens[posicao] = item;
            posicao++;
        }

    }

    public String getCpfCnpjFornecedor() {
        return cpfCnpjFornecedor;
    }

    public void setCpfCnpjFornecedor(String cpfCnpjFornecedor) {
        this.cpfCnpjFornecedor = cpfCnpjFornecedor;
    }

    public String getCpfCnpjRepresentante() {
        return cpfCnpjRepresentante;
    }

    public void setCpfCnpjRepresentante(String cpfCnpjRepresentante) {
        this.cpfCnpjRepresentante = cpfCnpjRepresentante;
    }

    public Long getIdLicitacao() {
        return idLicitacao;
    }

    public void setIdLicitacao(Long idLicitacao) {
        this.idLicitacao = idLicitacao;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getRepresentante() {
        return representante;
    }

    public void setRepresentante(String representante) {
        this.representante = representante;
    }

    public String getInstrumento() {
        return instrumento;
    }

    public void setInstrumento(String instrumento) {
        this.instrumento = instrumento;
    }

    public String getTipoEmpresa() {
        return tipoEmpresa;
    }

    public void setTipoEmpresa(String tipoEmpresa) {
        this.tipoEmpresa = tipoEmpresa;
    }

    public Long getPrazoValidade() {
        return prazoValidade;
    }

    public void setPrazoValidade(Long prazoValidade) {
        this.prazoValidade = prazoValidade;
    }

    public String getTipoPrazoValidade() {
        return tipoPrazoValidade;
    }

    public void setTipoPrazoValidade(String tipoPrazoValidade) {
        this.tipoPrazoValidade = tipoPrazoValidade;
    }

    public Long getPrazoExecucao() {
        return prazoExecucao;
    }

    public void setPrazoExecucao(Long prazoExecucao) {
        this.prazoExecucao = prazoExecucao;
    }

    public String getTipoPrazoExecucao() {
        return tipoPrazoExecucao;
    }

    public void setTipoPrazoExecucao(String tipoPrazoExecucao) {
        this.tipoPrazoExecucao = tipoPrazoExecucao;
    }

    public ArquivoItemLicitacaoPropostaFornecedor[] getItens() {
        return itens;
    }

    public void setItens(ArquivoItemLicitacaoPropostaFornecedor[] itens) {
        this.itens = itens;
    }

    public String getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(String licitacao) {
        this.licitacao = licitacao;
    }

    public Long getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(Long idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public Long getIdRepresentante() {
        return idRepresentante;
    }

    public void setIdRepresentante(Long idRepresentante) {
        this.idRepresentante = idRepresentante;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }
}
