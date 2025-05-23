/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.financeiro.SuperEntidadeContabilFinanceira;
import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensEstoque;
import br.com.webpublico.enums.TipoTransferenciaBensEstoque;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.UtilGeradorContaAuxiliar;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.TreeMap;

/**
 * @author juggernaut
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity

@Etiqueta("Transferência de Bens de Estoque")
@Table(name = "TRANSFBENSESTOQUE")
public class TransferenciaBensEstoque extends SuperEntidadeContabilFinanceira implements Serializable, EntidadeContabil, IGeraContaAuxiliar {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    private String numero;
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data")
    @Tabelavel
    @Pesquisavel
    @ReprocessamentoContabil
    private Date dataTransferencia;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Lançamento")
    @Tabelavel
    @Pesquisavel
    private TipoLancamento tipoLancamento;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Transferência")
    private TipoTransferenciaBensEstoque tipoTransferencia;
    @ManyToOne
    @Etiqueta("Unidade de Origem")
    @ReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrgOrigem;
    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Grupo Material de Origem")
    private GrupoMaterial grupoMaterial;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo Estoque de Origem")
    private TipoEstoque tipoEstoqueOrigem;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Unidade de Destino")
    @ReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrgDestino;
    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Grupo Material de Destino")
    private GrupoMaterial grupoMaterialDestino;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo Estoque de Destino")
    private TipoEstoque tipoEstoqueDestino;
    @Etiqueta("Operação de Origem")
    @Obrigatorio
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoOperacaoBensEstoque operacaoOrigem;
    @Etiqueta("Operação de Destino")
    @Obrigatorio
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoOperacaoBensEstoque operacaoDestino;
    @Obrigatorio
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    @Monetario
    @Etiqueta("Valor (R$)")
    private BigDecimal valor;
    @Obrigatorio
    @Etiqueta("Histórico")
    private String historico;
    @ManyToOne
    @Etiqueta("Evento Contábil Origem")
    private EventoContabil eventoContabilOrigem;
    @ManyToOne
    @Etiqueta("Evento Contábil Destino")
    @ReprocessamentoContabil
    private EventoContabil eventoContabilDestino;
    @Etiqueta("Exercício")
    @ManyToOne
    private Exercicio exercicio;
    private String historicoNota;
    private String historicoRazao;

    public TransferenciaBensEstoque() {
        this.dataTransferencia = new Date();
        this.tipoLancamento = TipoLancamento.NORMAL;
        this.valor = BigDecimal.ZERO;
    }

    public TransferenciaBensEstoque(String numero, Date dataTransferencia, TipoLancamento tipoLancamento, UnidadeOrganizacional unidadeOrgOrigem, UnidadeOrganizacional unidadeOrgDestino, GrupoMaterial grupoMaterial, String historico, BigDecimal valor) {
        this.numero = numero;
        this.dataTransferencia = dataTransferencia;
        this.tipoLancamento = tipoLancamento;
        this.unidadeOrgOrigem = unidadeOrgOrigem;
        this.unidadeOrgDestino = unidadeOrgDestino;
        this.grupoMaterial = grupoMaterial;
        this.historico = historico;
        this.valor = valor;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getHistoricoNota() {
        return historicoNota;
    }

    public void setHistoricoNota(String historicoNota) {
        this.historicoNota = historicoNota;
    }

    public String getHistoricoRazao() {
        return historicoRazao;
    }

    public void setHistoricoRazao(String historicoRazao) {
        this.historicoRazao = historicoRazao;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public Date getDataTransferencia() {
        return dataTransferencia;
    }

    public void setDataTransferencia(Date dataTransferencia) {
        this.dataTransferencia = dataTransferencia;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public UnidadeOrganizacional getUnidadeOrgDestino() {
        return unidadeOrgDestino;
    }

    public void setUnidadeOrgDestino(UnidadeOrganizacional unidadeOrganizacionalDestino) {
        this.unidadeOrgDestino = unidadeOrganizacionalDestino;
    }

    public UnidadeOrganizacional getUnidadeOrgOrigem() {
        return unidadeOrgOrigem;
    }

    public void setUnidadeOrgOrigem(UnidadeOrganizacional unidadeOrganizacionalOrigem) {
        this.unidadeOrgOrigem = unidadeOrganizacionalOrigem;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public TipoTransferenciaBensEstoque getTipoTransferencia() {
        return tipoTransferencia;
    }

    public void setTipoTransferencia(TipoTransferenciaBensEstoque tipoTransferencia) {
        this.tipoTransferencia = tipoTransferencia;
    }

    public GrupoMaterial getGrupoMaterialDestino() {
        return grupoMaterialDestino;
    }

    public void setGrupoMaterialDestino(GrupoMaterial grupoMaterialDestino) {
        this.grupoMaterialDestino = grupoMaterialDestino;
    }

    public EventoContabil getEventoContabilOrigem() {
        return eventoContabilOrigem;
    }

    public void setEventoContabilOrigem(EventoContabil eventoContabilOrigem) {
        this.eventoContabilOrigem = eventoContabilOrigem;
    }

    public EventoContabil getEventoContabilDestino() {
        return eventoContabilDestino;
    }

    public void setEventoContabilDestino(EventoContabil eventoContabilDestino) {
        this.eventoContabilDestino = eventoContabilDestino;
    }

    public TipoEstoque getTipoEstoqueOrigem() {
        return tipoEstoqueOrigem;
    }

    public void setTipoEstoqueOrigem(TipoEstoque tipoEstoqueOrigem) {
        this.tipoEstoqueOrigem = tipoEstoqueOrigem;
    }

    public TipoEstoque getTipoEstoqueDestino() {
        return tipoEstoqueDestino;
    }

    public void setTipoEstoqueDestino(TipoEstoque tipoEstoqueDestino) {
        this.tipoEstoqueDestino = tipoEstoqueDestino;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public TipoOperacaoBensEstoque getOperacaoOrigem() {
        return operacaoOrigem;
    }

    public void setOperacaoOrigem(TipoOperacaoBensEstoque operacaoOrigem) {
        this.operacaoOrigem = operacaoOrigem;
    }

    public TipoOperacaoBensEstoque getOperacaoDestino() {
        return operacaoDestino;
    }

    public void setOperacaoDestino(TipoOperacaoBensEstoque operacaoDestino) {
        this.operacaoDestino = operacaoDestino;
    }

    public void gerarHistoricoNota() {
        historicoNota = "";
        if (this.getNumero() != null) {
            historicoNota += "N°: " + this.getNumero() + "/" + Util.getAnoDaData(this.getDataTransferencia()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getTipoEstoqueOrigem() != null) {
            historicoNota += " Tipo de Estoque Origem: " + this.getTipoEstoqueOrigem().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getTipoEstoqueDestino() != null) {
            historicoNota += " Tipo de Estoque Destino: " + this.getTipoEstoqueDestino().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getGrupoMaterial() != null) {
            historicoNota += " Grupo Material Origem: " + this.getGrupoMaterial() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getGrupoMaterialDestino() != null) {
            historicoNota += " Grupo Material Destino: " + this.getGrupoMaterialDestino() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getOperacaoOrigem() != null) {
            historicoNota += " Operação Origem: " + this.getOperacaoOrigem() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getOperacaoDestino() != null) {
            historicoNota += " Operação Destino: " + this.getOperacaoDestino() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        historicoNota = historicoNota + " " + historico;
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabilOrigem() != null && this.getEventoContabilOrigem().getClpHistoricoContabil() != null) {
            historicoEvento = "Evento Contábil Origem: " + this.getEventoContabilOrigem().getClpHistoricoContabil().toString();
        }
        if (this.getEventoContabilDestino() != null && this.getEventoContabilDestino().getClpHistoricoContabil() != null) {
            historicoEvento = "Evento Contábil Destino: " + this.getEventoContabilDestino().getClpHistoricoContabil().toString();
        }
        this.historicoRazao = historicoEvento + " " + this.historicoNota;
        this.historicoRazao = Util.cortarString(this.historicoRazao, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricos() {
        gerarHistoricoNota();
        gerarHistoricoRazao();
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
        if (!(object instanceof TransferenciaBensEstoque)) {
            return false;
        }
        TransferenciaBensEstoque other = (TransferenciaBensEstoque) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return numero + " " + valor;
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return numero + " - " + grupoMaterial.toString();
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }

    @Override
    public TreeMap getMapContaAuxiliarSistema(TipoContaAuxiliar tipoContaAuxiliar) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(unidadeOrgDestino);
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada2(unidadeOrgDestino, contaContabil.getSubSistema());
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(unidadeOrgOrigem);
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada2(unidadeOrgOrigem, contaContabil.getSubSistema());
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(unidadeOrgDestino);
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar2(unidadeOrgDestino,
                    contaContabil.getSubSistema());
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(unidadeOrgOrigem);
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar2(unidadeOrgOrigem,
                    contaContabil.getSubSistema());
        }
        return null;
    }
}
