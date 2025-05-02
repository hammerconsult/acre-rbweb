package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.EstadoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.SituacaoConservacaoBem;
import com.google.common.base.Strings;

import java.math.BigDecimal;

/**
 * Created by mga on 23/05/2018.
 */
public class VOItemSolicitacaoTransferencia implements Comparable<VOItemSolicitacaoTransferencia> {

    private Long idItem;
    private Long idBem;
    private String registroPatrimonial;
    private String descricao;
    private String unidadeOrcOrigem;
    private String unidadeOrcDestino;
    private HierarquiaOrganizacional unidadeOrcamentariaOrigem;
    private HierarquiaOrganizacional unidadeOrcamentariaDestino;
    private HierarquiaOrganizacional unidadeAdministrativaOrigem;
    private HierarquiaOrganizacional unidadeAdministrativaDestino;
    private EstadoConservacaoBem estadoConservacaoBem;
    private SituacaoConservacaoBem situacaoConservacaoBem;
    private BigDecimal valorOriginal;
    private BigDecimal valorAjuste;
    private EstadoBem estadoBem;

    public VOItemSolicitacaoTransferencia() {
        valorAjuste = BigDecimal.ZERO;
        valorOriginal = BigDecimal.ZERO;
    }

    public EstadoConservacaoBem getEstadoConservacaoBem() {
        return estadoConservacaoBem;
    }

    public void setEstadoConservacaoBem(EstadoConservacaoBem estadoConservacaoBem) {
        this.estadoConservacaoBem = estadoConservacaoBem;
    }

    public SituacaoConservacaoBem getSituacaoConservacaoBem() {
        return situacaoConservacaoBem;
    }

    public void setSituacaoConservacaoBem(SituacaoConservacaoBem situacaoConservacaoBem) {
        this.situacaoConservacaoBem = situacaoConservacaoBem;
    }

    public Long getIdItem() {
        return idItem;
    }

    public void setIdItem(Long idItem) {
        this.idItem = idItem;
    }

    public Long getIdBem() {
        return idBem;
    }

    public void setIdBem(Long idBem) {
        this.idBem = idBem;
    }

    public String getRegistroPatrimonial() {
        return registroPatrimonial;
    }

    public void setRegistroPatrimonial(String registroPatrimonial) {
        this.registroPatrimonial = registroPatrimonial;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public BigDecimal getValorAjuste() {
        return valorAjuste;
    }

    public void setValorAjuste(BigDecimal valorAjuste) {
        this.valorAjuste = valorAjuste;
    }


    public HierarquiaOrganizacional getUnidadeOrcamentariaOrigem() {
        return unidadeOrcamentariaOrigem;
    }

    public void setUnidadeOrcamentariaOrigem(HierarquiaOrganizacional unidadeOrcamentariaOrigem) {
        this.unidadeOrcamentariaOrigem = unidadeOrcamentariaOrigem;
    }

    public HierarquiaOrganizacional getUnidadeOrcamentariaDestino() {
        return unidadeOrcamentariaDestino;
    }

    public void setUnidadeOrcamentariaDestino(HierarquiaOrganizacional unidadeOrcamentariaDestino) {
        this.unidadeOrcamentariaDestino = unidadeOrcamentariaDestino;
    }

    public HierarquiaOrganizacional getUnidadeAdministrativaOrigem() {
        return unidadeAdministrativaOrigem;
    }

    public void setUnidadeAdministrativaOrigem(HierarquiaOrganizacional unidadeAdministrativaOrigem) {
        this.unidadeAdministrativaOrigem = unidadeAdministrativaOrigem;
    }

    public HierarquiaOrganizacional getUnidadeAdministrativaDestino() {
        return unidadeAdministrativaDestino;
    }

    public void setUnidadeAdministrativaDestino(HierarquiaOrganizacional unidadeAdministrativaDestino) {
        this.unidadeAdministrativaDestino = unidadeAdministrativaDestino;
    }

    public String getUnidadeOrcOrigem() {
        return unidadeOrcOrigem;
    }

    public void setUnidadeOrcOrigem(String unidadeOrcOrigem) {
        this.unidadeOrcOrigem = unidadeOrcOrigem;
    }

    public String getUnidadeOrcDestino() {
        return unidadeOrcDestino;
    }

    public void setUnidadeOrcDestino(String unidadeOrcDestino) {
        this.unidadeOrcDestino = unidadeOrcDestino;
    }

    @Override
    public int compareTo(VOItemSolicitacaoTransferencia o) {
        if (!Strings.isNullOrEmpty(this.registroPatrimonial) && !Strings.isNullOrEmpty(o.getRegistroPatrimonial())) {
            Integer registro1 = Integer.parseInt(this.registroPatrimonial);
            Integer registro2 = Integer.parseInt(o.getRegistroPatrimonial());
            return registro1.compareTo(registro2);
        }
        return 0;
    }

    public String getBem(){
        return registroPatrimonial + " -  " + descricao;

    }

    public EstadoBem getEstadoBem() {
        return estadoBem;
    }

    public void setEstadoBem(EstadoBem estadoBem) {
        this.estadoBem = estadoBem;
    }
}
