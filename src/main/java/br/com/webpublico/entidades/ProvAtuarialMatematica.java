/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoAtuarial;
import br.com.webpublico.enums.TipoPlano;
import br.com.webpublico.enums.TipoProvisao;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.util.DataUtil;
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
 * @author major
 */
@Entity
@Audited
@Etiqueta(value = "Provisão Matemática Previdênciária")

public class ProvAtuarialMatematica implements Serializable, EntidadeContabil, IGeraContaAuxiliar {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta(value = "Número")
    @Pesquisavel
    @ErroReprocessamentoContabil
    private String numero;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Tabelavel
    @Etiqueta(value = "Data")
    @Pesquisavel
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    private Date dataLancamento;
    @Obrigatorio
    @Tabelavel
    @Etiqueta(value = "Tipo de Lançamento")
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @ErroReprocessamentoContabil
    private TipoLancamento tipoLancamento;
    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Etiqueta(value = "Tipo Passivo Atuarial")
    @Pesquisavel
    @ErroReprocessamentoContabil
    private TipoPassivoAtuarial tipoPassivoAtuarial;
    @ManyToOne
    @Tabelavel
    @Etiqueta(value = "Unidade Organizacional")
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacional;
    @Obrigatorio
    @Tabelavel
    @Etiqueta(value = "Tipo de Operação")
    @Enumerated(EnumType.STRING)
    @ErroReprocessamentoContabil
    private TipoOperacaoAtuarial tipoOperacaoAtuarial;
    @Obrigatorio
    @Tabelavel
    @Etiqueta(value = "Tipo de Plano")
    @Enumerated(EnumType.STRING)
    @ErroReprocessamentoContabil
    private TipoPlano tipoPlano;
    @Obrigatorio
    @Tabelavel
    @Etiqueta(value = "Tipo de Provisão")
    @Enumerated(EnumType.STRING)
    @ErroReprocessamentoContabil
    private TipoProvisao tipoProvisao;
    @Obrigatorio
    @Etiqueta(value = "Histórico")
    @ErroReprocessamentoContabil
    private String historico;
    @Obrigatorio
    @Tabelavel
    @Etiqueta(value = "Valor (R$)")
    @Monetario
    @ErroReprocessamentoContabil
    private BigDecimal valorLancamento;
    @Obrigatorio
    @Tabelavel
    @Etiqueta(value = "Dívida Publica")
    @ManyToOne
    @ErroReprocessamentoContabil
    private DividaPublica dividaPublica;
    @ManyToOne
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    private EventoContabil eventoContabil;
    private String historicoNota;
    private String historicoRazao;

    public ProvAtuarialMatematica() {
    }

    public ProvAtuarialMatematica(String numero, TipoLancamento tipoLancamento, TipoPassivoAtuarial tipoPassivoAtuarial, UnidadeOrganizacional unidadeOrganizacional, TipoOperacaoAtuarial tipoOperacaoAtuarial, TipoPlano tipoPlano, TipoProvisao tipoProvisao, String historico, BigDecimal valorLancamento, DividaPublica dividaPublica, EventoContabil eventoContabil) {
        this.numero = numero;
        this.tipoLancamento = tipoLancamento;
        this.tipoPassivoAtuarial = tipoPassivoAtuarial;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.tipoOperacaoAtuarial = tipoOperacaoAtuarial;
        this.tipoPlano = tipoPlano;
        this.tipoProvisao = tipoProvisao;
        this.historico = historico;
        this.valorLancamento = valorLancamento;
        this.dividaPublica = dividaPublica;
        this.eventoContabil = eventoContabil;
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

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public TipoOperacaoAtuarial getTipoOperacaoAtuarial() {
        return tipoOperacaoAtuarial;
    }

    public void setTipoOperacaoAtuarial(TipoOperacaoAtuarial tipoOperacaoAtuarial) {
        this.tipoOperacaoAtuarial = tipoOperacaoAtuarial;
    }

    public TipoPassivoAtuarial getTipoPassivoAtuarial() {
        return tipoPassivoAtuarial;
    }

    public void setTipoPassivoAtuarial(TipoPassivoAtuarial tipoPassivoAtuarial) {
        this.tipoPassivoAtuarial = tipoPassivoAtuarial;
    }

    public TipoPlano getTipoPlano() {
        return tipoPlano;
    }

    public void setTipoPlano(TipoPlano tipoPlano) {
        this.tipoPlano = tipoPlano;
    }

    public TipoProvisao getTipoProvisao() {
        return tipoProvisao;
    }

    public void setTipoProvisao(TipoProvisao tipoProvisao) {
        this.tipoProvisao = tipoProvisao;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public BigDecimal getValorLancamento() {
        return valorLancamento;
    }

    public void setValorLancamento(BigDecimal valorLancamento) {
        this.valorLancamento = valorLancamento;
    }

    public DividaPublica getDividaPublica() {
        return dividaPublica;
    }

    public void setDividaPublica(DividaPublica dividaPublica) {
        this.dividaPublica = dividaPublica;
    }

    public void gerarHistoricoNota() {
        historicoNota = "";
        if (this.getNumero() != null) {
            historicoNota += "N°: " + this.getNumero() + "/" + Util.getAnoDaData(this.getDataLancamento()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getDividaPublica() != null) {
            historicoNota += " Dívida Pública: " + this.getDividaPublica() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getTipoProvisao() != null) {
            historicoNota += " Tipo de Provisão: " + this.getTipoProvisao().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getTipoOperacaoAtuarial() != null) {
            historicoNota += " Operação: " + this.getTipoOperacaoAtuarial().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getTipoPlano() != null) {
            historicoNota += " Tipo de Plano: " + this.getTipoPlano().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getTipoPassivoAtuarial() != null) {
            historicoNota += " Tipo Passivo Atuarial: " + this.getTipoPassivoAtuarial().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        historicoNota = historicoNota + " " + this.getHistorico();
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil().getClpHistoricoContabil() != null) {
            historicoEvento = "Evento Contábil: " + this.getEventoContabil().getClpHistoricoContabil().toString();
        }
        this.historicoRazao = historicoEvento + " " + this.historicoNota;
        this.historicoRazao = Util.cortarString(this.historicoRazao, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricos() {
        gerarHistoricoNota();
        gerarHistoricoRazao();
    }

    @Override
    public String toString() {
        return numero + " - " + Util.formataValor(valorLancamento) + " (" + DataUtil.getDataFormatada(dataLancamento) + ")";
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return numero;
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
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada2(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema());
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar2(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema());
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }
}
