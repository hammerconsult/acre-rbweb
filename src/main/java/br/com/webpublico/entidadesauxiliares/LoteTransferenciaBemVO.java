package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.EventoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.enums.SituacaoDaSolicitacao;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoTransferenciaUnidadeBem;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class LoteTransferenciaBemVO implements Serializable {

    private SituacaoDaSolicitacao situacaoTransferenciaBem;
    private TipoBem tipoBem;
    private EventoBem eventoBem;
    private TipoTransferenciaUnidadeBem tipoTransferencia;
    private Date dataHoraCriacao;
    private HierarquiaOrganizacional hierarquiaOrigem;
    private PessoaFisica responsavelOrigem;
    private HierarquiaOrganizacional hierarquiaDestino;
    private HierarquiaOrganizacional hierarquiaOrcamentariaDestino;
    private PessoaFisica responsavelDestino;
    private String descricao;
    private List<HierarquiaOrganizacional> orcamentariasOrigem;
    private List<HierarquiaOrganizacional> orcamentariasDestino;
    private List<Bem> bens;

    public LoteTransferenciaBemVO() {
        bens = Lists.newArrayList();
    }

    public Date getDataHoraCriacao() {
        return dataHoraCriacao;
    }

    public void setDataHoraCriacao(Date dataHoraCriacao) {
        this.dataHoraCriacao = dataHoraCriacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public PessoaFisica getResponsavelOrigem() {
        return responsavelOrigem;
    }

    public void setResponsavelOrigem(PessoaFisica responsavelOrigem) {
        this.responsavelOrigem = responsavelOrigem;
    }

    public PessoaFisica getResponsavelDestino() {
        return responsavelDestino;
    }

    public void setResponsavelDestino(PessoaFisica responsavelDestino) {
        this.responsavelDestino = responsavelDestino;
    }

    public SituacaoDaSolicitacao getSituacaoTransferenciaBem() {
        return situacaoTransferenciaBem;
    }

    public void setSituacaoTransferenciaBem(SituacaoDaSolicitacao situacaoTransferenciaBem) {
        this.situacaoTransferenciaBem = situacaoTransferenciaBem;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    public TipoTransferenciaUnidadeBem getTipoTransferencia() {
        return tipoTransferencia;
    }

    public void setTipoTransferencia(TipoTransferenciaUnidadeBem tipoTransferencia) {
        this.tipoTransferencia = tipoTransferencia;
    }

    public HierarquiaOrganizacional getHierarquiaOrigem() {
        return hierarquiaOrigem;
    }

    public void setHierarquiaOrigem(HierarquiaOrganizacional hierarquiaOrigem) {
        this.hierarquiaOrigem = hierarquiaOrigem;
    }

    public HierarquiaOrganizacional getHierarquiaDestino() {
        return hierarquiaDestino;
    }

    public void setHierarquiaDestino(HierarquiaOrganizacional hierarquiaDestino) {
        this.hierarquiaDestino = hierarquiaDestino;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentariaDestino() {
        return hierarquiaOrcamentariaDestino;
    }

    public void setHierarquiaOrcamentariaDestino(HierarquiaOrganizacional hierarquiaOrcamentariaDestino) {
        this.hierarquiaOrcamentariaDestino = hierarquiaOrcamentariaDestino;
    }

    public List<HierarquiaOrganizacional> getOrcamentariasOrigem() {
        return orcamentariasOrigem;
    }

    public void setOrcamentariasOrigem(List<HierarquiaOrganizacional> orcamentariasOrigem) {
        this.orcamentariasOrigem = orcamentariasOrigem;
    }

    public List<Bem> getBens() {
        return bens;
    }

    public void setBens(List<Bem> bens) {
        this.bens = bens;
    }

    public List<HierarquiaOrganizacional> getOrcamentariasDestino() {
        return orcamentariasDestino;
    }

    public void setOrcamentariasDestino(List<HierarquiaOrganizacional> orcamentariasDestino) {
        this.orcamentariasDestino = orcamentariasDestino;
    }

    public EventoBem getEventoBem() {
        return eventoBem;
    }

    public void setEventoBem(EventoBem eventoBem) {
        this.eventoBem = eventoBem;
    }

    public BigDecimal getValorTotalAjuste() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (bens != null && !bens.isEmpty()) {
            for (Bem bem : bens) {
                valorTotal = valorTotal.add(bem.getValorDosAjustes());
            }
        }
        return valorTotal;
    }

    public BigDecimal getValorTotalOriginal() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (bens != null && !bens.isEmpty()) {
            for (Bem bem : bens) {
                valorTotal = valorTotal.add(bem.getValorOriginal());
            }
        }
        return valorTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoteTransferenciaBemVO that = (LoteTransferenciaBemVO) o;
        return Objects.equals(hierarquiaOrigem, that.hierarquiaOrigem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hierarquiaOrigem);
    }
}
