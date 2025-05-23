package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.enums.TipoPrazo;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class OrdemCompraServicoVo {

    private Long idRequisicao;
    private Long numero;
    private Date dataRequisicao;
    private String descricao;
    private String localEntrega;
    private String situacaoRequisicaoCompra;
    private TipoObjetoCompra tipoObjetoCompra;
    private Integer prazoEntrega;
    private TipoPrazo tipoPrazoEntrega;
    private String descricaoPrazoEntrega;
    private String criadoPor;
    private List<OrdemCompraServicoItemVo> itens;

    private List<DocumentoFiscalOrdemCompra> documentosFiscais;
    private List<OrdemCompraResultadoVo> estornosOrdemCompraServico;
    private List<OrdemCompraResultadoVo> atestos;
    private List<OrdemCompraResultadoVo> estornosAtestos;

    public OrdemCompraServicoVo() {
        itens = Lists.newArrayList();
        estornosOrdemCompraServico = Lists.newArrayList();
        documentosFiscais = Lists.newArrayList();
        atestos = Lists.newArrayList();
        estornosAtestos = Lists.newArrayList();
    }

    public Long getIdRequisicao() {
        return idRequisicao;
    }

    public void setIdRequisicao(Long idRequisicao) {
        this.idRequisicao = idRequisicao;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataRequisicao() {
        return dataRequisicao;
    }

    public void setDataRequisicao(Date dataRequisicao) {
        this.dataRequisicao = dataRequisicao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLocalEntrega() {
        return localEntrega;
    }

    public void setLocalEntrega(String localEntrega) {
        this.localEntrega = localEntrega;
    }

    public String getSituacaoRequisicaoCompra() {
        return situacaoRequisicaoCompra;
    }

    public void setSituacaoRequisicaoCompra(String situacaoRequisicaoCompra) {
        this.situacaoRequisicaoCompra = situacaoRequisicaoCompra;
    }

    public List<OrdemCompraServicoItemVo> getItens() {
        return itens;
    }

    public void setItens(List<OrdemCompraServicoItemVo> itens) {
        this.itens = itens;
    }

    public Integer getPrazoEntrega() {
        return prazoEntrega;
    }

    public void setPrazoEntrega(Integer prazoEntrega) {
        this.prazoEntrega = prazoEntrega;
    }

    public TipoPrazo getTipoPrazoEntrega() {
        return tipoPrazoEntrega;
    }

    public void setTipoPrazoEntrega(TipoPrazo tipoPrazoEntrega) {
        this.tipoPrazoEntrega = tipoPrazoEntrega;
    }

    public String getDescricaoPrazoEntrega() {
        return descricaoPrazoEntrega;
    }

    public void setDescricaoPrazoEntrega(String descricaoPrazoEntrega) {
        this.descricaoPrazoEntrega = descricaoPrazoEntrega;
    }

    public String getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(String criadoPor) {
        this.criadoPor = criadoPor;
    }

    public String getTituloProcesso() {
        switch (tipoObjetoCompra) {
            case CONSUMO:
                return "Entrada por Compra";
            case PERMANENTE_MOVEL:
                return "Aquisição de Bens Móveis";
            default:
                return "Liquidação de Serviço";
        }
    }

    public boolean hasDocumentos(){
        return !Util.isListNullOrEmpty(documentosFiscais);
    }

    public boolean hasEstornosOrdemCompraSevico(){
        return !Util.isListNullOrEmpty(estornosOrdemCompraServico);
    }

    public boolean hasAtestos(){
        return !Util.isListNullOrEmpty(atestos);
    }

    public boolean hasEstornosAtestos(){
        return !Util.isListNullOrEmpty(estornosAtestos);
    }

    public BigDecimal getValorRequisicao() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrdemCompraServicoItemVo item : itens) {
            total = total.add(item.getValorTotal());
        }
        return total;
    }

    public BigDecimal getValorRequisicaoEstornado() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrdemCompraResultadoVo ordem : estornosOrdemCompraServico) {
            total = total.add(ordem.getValor());
        }
        return total;
    }

    public BigDecimal getValorAtesto() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrdemCompraResultadoVo ordem : atestos) {
            total = total.add(ordem.getValor());
        }
        return total;
    }

    public BigDecimal getValorAtestoEstornado() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrdemCompraResultadoVo ordem : estornosAtestos) {
            total = total.add(ordem.getValor());
        }
        return total;
    }

    public BigDecimal getSaldo() {
        return getValorRequisicao().subtract(getValorRequisicaoEstornado()).subtract(getValorAtesto()).add(getValorAtestoEstornado());
    }

    public Integer getQtdDocumentos() {
        if (!Util.isListNullOrEmpty(documentosFiscais)){
            return documentosFiscais.size();
        }
        return 0;
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }

    public List<OrdemCompraResultadoVo> getEstornosOrdemCompraServico() {
        return estornosOrdemCompraServico;
    }

    public void setEstornosOrdemCompraServico(List<OrdemCompraResultadoVo> estornosOrdemCompraServico) {
        this.estornosOrdemCompraServico = estornosOrdemCompraServico;
    }

    public List<DocumentoFiscalOrdemCompra> getDocumentosFiscais() {
        return documentosFiscais;
    }

    public void setDocumentosFiscais(List<DocumentoFiscalOrdemCompra> documentosFiscais) {
        this.documentosFiscais = documentosFiscais;
    }

    public List<OrdemCompraResultadoVo> getAtestos() {
        return atestos;
    }

    public void setAtestos(List<OrdemCompraResultadoVo> atestos) {
        this.atestos = atestos;
    }

    public List<OrdemCompraResultadoVo> getEstornosAtestos() {
        return estornosAtestos;
    }

    public void setEstornosAtestos(List<OrdemCompraResultadoVo> estornosAtestos) {
        this.estornosAtestos = estornosAtestos;
    }
}
