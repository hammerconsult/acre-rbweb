/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.contabil.ContaAuxiliarDetalhada;
import br.com.webpublico.entidadesauxiliares.manad.ManadRegistro;
import br.com.webpublico.enums.TipoBalancete;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.IManadRegistro;
import br.com.webpublico.negocios.manad.ManadContabilFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.ErroReprocessamentoContabil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity

public class LancamentoContabil extends SuperEntidade implements Serializable, IManadRegistro, EntidadeContabil {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @ErroReprocessamentoContabil
    @Etiqueta("Data de Lançamento")
    private Date dataLancamento;
    @Tabelavel
    @Monetario
    @ErroReprocessamentoContabil
    @Etiqueta("Valor")
    private BigDecimal valor;
    @Tabelavel
    @ManyToOne
    @ErroReprocessamentoContabil
    @Etiqueta("Conta de Crédito")
    private Conta contaCredito;
    @Tabelavel
    @ManyToOne
    @ErroReprocessamentoContabil
    @Etiqueta("Conta de Débito")
    private Conta contaDebito;
    @ManyToOne
    @ErroReprocessamentoContabil
    private ContaAuxiliar contaAuxiliarCredito;
    @Tabelavel
    @ManyToOne
    @Etiqueta("Conta Auxiliar de Crédito")
    private ContaAuxiliar contaAuxiliarCredSiconfi;
    @ManyToOne
    private ContaAuxiliarDetalhada contaAuxCreDetalhadaSiconfi;
    @ManyToOne
    @ErroReprocessamentoContabil
    private ContaAuxiliar contaAuxiliarDebito;
    @Tabelavel
    @ManyToOne
    @Etiqueta("Conta Auxiliar de Débito")
    private ContaAuxiliar contaAuxiliarDebSiconfi;
    @ManyToOne
    private ContaAuxiliarDetalhada contaAuxDebDetalhadaSiconfi;
    @ManyToOne
    @ErroReprocessamentoContabil
    private ClpHistoricoContabil clpHistoricoContabil;
    @ErroReprocessamentoContabil
    private String complementoHistorico;
    @ManyToOne
    @ErroReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacional;
    @Tabelavel
    @ManyToOne
    @ErroReprocessamentoContabil
    @Etiqueta("LCP")
    private LCP lcp;
    @ManyToOne
    private ItemParametroEvento itemParametroEvento;
    @Tabelavel
    @Etiqueta("Número")
    private Long numero;
    @Transient
    private TipoBalancete tipoBalancete;

    public LancamentoContabil() {
    }

    public LancamentoContabil(Date dataLancamento, BigDecimal valor, Conta contaCredito, Conta contaDebito, ClpHistoricoContabil clpHistoricoContabil, String complementoHistorico, UnidadeOrganizacional unidadeOrganizacional, LCP lcp, ItemParametroEvento itemParametroEvento, ContaAuxiliar contaAuxiliarCredito, ContaAuxiliar contaAuxiliarDebito, ContaAuxiliar contaAuxiliarCredSiconfi, ContaAuxiliar contaAuxiliarDebSiconfi, ContaAuxiliarDetalhada contaAuxCreDetalhadaSiconfi, ContaAuxiliarDetalhada contaAuxDebDetalhadaSiconfi) {
        this.dataLancamento = dataLancamento;
        this.valor = valor.compareTo(BigDecimal.ZERO) < 0 ? valor.multiply(BigDecimal.valueOf(-1.0)) : valor;
        this.contaCredito = contaCredito;
        this.contaDebito = contaDebito;
        this.clpHistoricoContabil = clpHistoricoContabil;
        this.complementoHistorico = complementoHistorico;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.lcp = lcp;
        this.itemParametroEvento = itemParametroEvento;
        this.contaAuxiliarCredito = contaAuxiliarCredito;
        this.contaAuxiliarDebito = contaAuxiliarDebito;
        this.tipoBalancete = itemParametroEvento.getParametroEvento().getEventoContabil().getTipoBalancete();
        this.contaAuxiliarCredSiconfi = contaAuxiliarCredSiconfi;
        this.contaAuxiliarDebSiconfi = contaAuxiliarDebSiconfi;
        this.contaAuxDebDetalhadaSiconfi = contaAuxDebDetalhadaSiconfi;
        this.contaAuxCreDetalhadaSiconfi = contaAuxCreDetalhadaSiconfi;
    }

    public ItemParametroEvento getItemParametroEvento() {
        return itemParametroEvento;
    }

    public void setItemParametroEvento(ItemParametroEvento itemParametroEvento) {
        this.itemParametroEvento = itemParametroEvento;
    }

    public ClpHistoricoContabil getClpHistoricoContabil() {
        return clpHistoricoContabil;
    }

    public void setClpHistoricoContabil(ClpHistoricoContabil clpHistoricoContabil) {
        this.clpHistoricoContabil = clpHistoricoContabil;
    }

    public String getComplementoHistorico() {
        return complementoHistorico;
    }

    public void setComplementoHistorico(String complementoHistorico) {
        this.complementoHistorico = complementoHistorico;
    }

    public Conta getContaCredito() {
        return contaCredito;
    }

    public void setContaCredito(Conta contaCredito) {
        this.contaCredito = contaCredito;
    }

    public Conta getContaDebito() {
        return contaDebito;
    }

    public void setContaDebito(Conta contaDebito) {
        this.contaDebito = contaDebito;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LCP getLcp() {
        return lcp;
    }

    public void setLcp(LCP lcp) {
        this.lcp = lcp;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public ContaAuxiliar getContaAuxiliarCredito() {
        return contaAuxiliarCredito;
    }

    public void setContaAuxiliarCredito(ContaAuxiliar contaAuxiliarCredito) {
        this.contaAuxiliarCredito = contaAuxiliarCredito;
    }

    public ContaAuxiliar getContaAuxiliarDebito() {
        return contaAuxiliarDebito;
    }

    public void setContaAuxiliarDebito(ContaAuxiliar contaAuxiliarDebito) {
        this.contaAuxiliarDebito = contaAuxiliarDebito;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public TipoBalancete getTipoBalancete() {
        if (tipoBalancete == null) {
            tipoBalancete = itemParametroEvento.getParametroEvento().getTipoBalancete();
        }
        return tipoBalancete;
    }

    public ContaAuxiliar getContaAuxiliarCredSiconfi() {
        return contaAuxiliarCredSiconfi;
    }

    public void setContaAuxiliarCredSiconfi(ContaAuxiliar contaAuxiliarCredSiconfi) {
        this.contaAuxiliarCredSiconfi = contaAuxiliarCredSiconfi;
    }

    public ContaAuxiliar getContaAuxiliarDebSiconfi() {
        return contaAuxiliarDebSiconfi;
    }

    public void setContaAuxiliarDebSiconfi(ContaAuxiliar contaAuxiliarDebSiconfi) {
        this.contaAuxiliarDebSiconfi = contaAuxiliarDebSiconfi;
    }

    public ContaAuxiliarDetalhada getContaAuxCreDetalhadaSiconfi() {
        return contaAuxCreDetalhadaSiconfi;
    }

    public void setContaAuxCreDetalhadaSiconfi(ContaAuxiliarDetalhada contaAuxCreDetalhadaSiconfi) {
        this.contaAuxCreDetalhadaSiconfi = contaAuxCreDetalhadaSiconfi;
    }

    public ContaAuxiliarDetalhada getContaAuxDebDetalhadaSiconfi() {
        return contaAuxDebDetalhadaSiconfi;
    }

    public void setContaAuxDebDetalhadaSiconfi(ContaAuxiliarDetalhada contaAuxDebDetalhadaSiconfi) {
        this.contaAuxDebDetalhadaSiconfi = contaAuxDebDetalhadaSiconfi;
    }

    @Override
    public String toString() {
        return "LancamentoContabil{" + "id=" + id + '}';
    }

    @Override
    public ManadRegistro.ManadModulo getModulo() {
        return ManadRegistro.ManadModulo.CONTABIL;
    }

    @Override
    public ManadRegistro.ManadRegistroTipo getTipoRegistro() {
        switch (getTipoEventoContabil()) {
            case EMPENHO_DESPESA:
                return ManadRegistro.ManadRegistroTipo.L050;
            case RESTO_PAGAR:
                return ManadRegistro.ManadRegistroTipo.L050;
            case LIQUIDACAO_DESPESA:
                return ManadRegistro.ManadRegistroTipo.L100;
            case LIQUIDACAO_RESTO_PAGAR:
                return ManadRegistro.ManadRegistroTipo.L100;
            case PAGAMENTO_DESPESA:
                return ManadRegistro.ManadRegistroTipo.L150;
            case PAGAMENTO_RESTO_PAGAR:
                return ManadRegistro.ManadRegistroTipo.L150;
            case PREVISAO_INICIAL_RECEITA:
                return ManadRegistro.ManadRegistroTipo.L200;
            case CREDITO_INICIAL:
                return ManadRegistro.ManadRegistroTipo.L250;
            case CREDITO_ADICIONAL:
                return ManadRegistro.ManadRegistroTipo.L300;
            case PREVISAO_ADICIONAL_RECEITA:
                return ManadRegistro.ManadRegistroTipo.L300;
            default:
                throw new RuntimeException("Nenhum tipo de registro encontrado");
        }
    }

    @Override
    public void getConteudoManad(ManadContabilFacade facade, StringBuilder conteudo) {
        //isso está implementado nos movimentos(Empenho, Liquidação, Pagamento, etc...)
    }

    public TipoEventoContabil getTipoEventoContabil() {
        return getItemParametroEvento().getParametroEvento().getEventoContabil().getTipoEventoContabil();
    }

    public Boolean isLancamentoNormal() {
        return getItemParametroEvento().getParametroEvento().getEventoContabil().getTipoLancamento().equals(TipoLancamento.NORMAL) ? true : false;
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return numero + "/" + DataUtil.getDataFormatada(dataLancamento);
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }
}
