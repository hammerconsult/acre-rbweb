package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.EstadoBem;
import br.com.webpublico.entidades.ItemAquisicao;
import br.com.webpublico.entidades.MovimentoBloqueioBem;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.administrativo.AgrupadorMovimentoBem;
import com.google.common.collect.Lists;

import java.util.List;

public class ItemDoctoFiscalAquisicao {

    private ItemAquisicao itemAquisicao;
    private Bem bem;
    private EstadoBem estadoBem;
    private Long idItemAquisicao;
    private SituacaoEventoBem situacaoEventoBem;
    private MovimentoBloqueioBem movimentoBloqueioBem;
    private List<EstadoBem> estadosBem;

    public ItemDoctoFiscalAquisicao() {
        estadosBem = Lists.newArrayList();
    }

    public Long getIdItemAquisicao() {
        return idItemAquisicao;
    }

    public void setIdItemAquisicao(Long idItemAquisicao) {
        this.idItemAquisicao = idItemAquisicao;
    }

    public ItemAquisicao getItemAquisicao() {
        return itemAquisicao;
    }

    public void setItemAquisicao(ItemAquisicao itemAquisicao) {
        this.itemAquisicao = itemAquisicao;
    }

    public String getDescricaoCurta() {
        if (bem.getDescricao().length() > 70) {
            return bem.getDescricao().substring(0, 70);
        }
        return bem.getDescricao();
    }

    public SituacaoEventoBem getSituacaoEventoBem() {
        return situacaoEventoBem;
    }

    public void setSituacaoEventoBem(SituacaoEventoBem situacaoEventoBem) {
        this.situacaoEventoBem = situacaoEventoBem;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public MovimentoBloqueioBem getMovimentoBloqueioBem() {
        return movimentoBloqueioBem;
    }

    public void setMovimentoBloqueioBem(MovimentoBloqueioBem movimentoBloqueioBem) {
        this.movimentoBloqueioBem = movimentoBloqueioBem;
    }

    public EstadoBem getEstadoBem() {
        return estadoBem;
    }

    public void setEstadoBem(EstadoBem estadoBem) {
        this.estadoBem = estadoBem;
    }

    public Boolean getBloqueado() {
        return movimentoBloqueioBem != null && !AgrupadorMovimentoBem.NAO_APLICAVEL.equals(movimentoBloqueioBem.getAgrupador());
    }

    public List<EstadoBem> getEstadosBem() {
        return estadosBem;
    }

    public void setEstadosBem(List<EstadoBem> estadosBem) {
        this.estadosBem = estadosBem;
    }
}
