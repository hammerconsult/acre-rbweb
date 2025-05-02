/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author major
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Evento Contábil")
public class EventoContabil implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Código")
    @Tabelavel
    @Pesquisavel
    private String codigo;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Evento Contábil")
    private TipoEventoContabil tipoEventoContabil;
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Operação da Conciliação")
    private TipoOperacaoConcilicaoBancaria tipoOperacaoConciliacao;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Lançamento")
    @Enumerated(EnumType.STRING)
    private TipoLancamento tipoLancamento;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Contabilização")
    private TipoBalancete tipoBalancete;
    @Etiqueta("Chave")
    @Obrigatorio
    private String chave;
    @ManyToOne
    private ClpHistoricoContabil clpHistoricoContabil;
    @Etiqueta("Evento TCE")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private String eventoTce;
    @Tabelavel
    @Etiqueta("CLPs")
    @OneToMany(mappedBy = "eventoContabil", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ItemEventoCLP> itemEventoCLPs;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;
    @Tabelavel
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Fim de Vigência")
    private Date fimVigencia;

    public EventoContabil() {
        itemEventoCLPs = new ArrayList<>();
    }

    public EventoContabil(String codigo, String descricao, String chave,
                          ClpHistoricoContabil clpHistoricoContabil, TipoBalancete tipoBalancete,
                          String eventoTce, TipoEventoContabil tipoEventoContabil) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.chave = chave;
        this.clpHistoricoContabil = clpHistoricoContabil;
        this.tipoBalancete = tipoBalancete;
        this.eventoTce = eventoTce;
        this.tipoEventoContabil = tipoEventoContabil;

    }

    public EventoContabil(Long id, String codigo, String descricao, TipoEventoContabil tipoEventoContabil, TipoLancamento tipoLancamento, TipoBalancete tipoBalancete, Date inicioVigencia, Date fimVigencia) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.tipoEventoContabil = tipoEventoContabil;
        this.tipoLancamento = tipoLancamento;
        this.tipoBalancete = tipoBalancete;
        this.inicioVigencia = inicioVigencia;
        this.fimVigencia = fimVigencia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ClpHistoricoContabil getClpHistoricoContabil() {
        return clpHistoricoContabil;
    }

    public void setClpHistoricoContabil(ClpHistoricoContabil clpHistoricoContabil) {
        this.clpHistoricoContabil = clpHistoricoContabil;
    }

    public TipoBalancete getTipoBalancete() {
        return tipoBalancete;
    }

    public void setTipoBalancete(TipoBalancete tipoBalancete) {
        this.tipoBalancete = tipoBalancete;
    }

    public String getEventoTce() {
        return eventoTce;
    }

    public void setEventoTce(String eventoTce) {
        this.eventoTce = eventoTce;
    }

    public List<ItemEventoCLP> getItemEventoCLPs() {
        return itemEventoCLPs;
    }

    public void setItemEventoCLPs(List<ItemEventoCLP> itemEventoCLPs) {
        this.itemEventoCLPs = itemEventoCLPs;
    }

    public TipoEventoContabil getTipoEventoContabil() {
        return tipoEventoContabil;
    }

    public void setTipoEventoContabil(TipoEventoContabil tipoEventoContabil) {
        this.tipoEventoContabil = tipoEventoContabil;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public TipoOperacaoConcilicaoBancaria getTipoOperacaoConciliacao() {
        return tipoOperacaoConciliacao;
    }

    public void setTipoOperacaoConciliacao(TipoOperacaoConcilicaoBancaria tipoOperacaoConciliacao) {
        this.tipoOperacaoConciliacao = tipoOperacaoConciliacao;
    }

    public Boolean isNormal() {
        if (getTipoLancamento().equals(TipoLancamento.NORMAL)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public CategoriaOrcamentaria getCategoriaOrcamentaria() {
        TipoEventoContabil tipoEventoContabil = this.getTipoEventoContabil();
        switch (tipoEventoContabil) {
            case EMPENHO_DESPESA:
                return CategoriaOrcamentaria.NORMAL;
            case LIQUIDACAO_DESPESA:
                return CategoriaOrcamentaria.NORMAL;
            case PAGAMENTO_DESPESA:
                return CategoriaOrcamentaria.NORMAL;
            case RESTO_PAGAR:
                return CategoriaOrcamentaria.RESTO;
            case LIQUIDACAO_RESTO_PAGAR:
                return CategoriaOrcamentaria.RESTO;
            case PAGAMENTO_RESTO_PAGAR:
                return CategoriaOrcamentaria.RESTO;
        }
        return null;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EventoContabil)) {
            return false;
        }
        EventoContabil other = (EventoContabil) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
