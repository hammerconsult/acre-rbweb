package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.entidadesauxiliares.manad.ManadRegistro;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.IManadRegistro;
import br.com.webpublico.negocios.manad.ManadContabilFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.ManadUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by mga on 07/07/2017.
 */
@Entity
@Audited
@Etiqueta("Desdobramento Empenho")
public class DesdobramentoEmpenho extends SuperEntidadeContabilGerarContaAuxiliar implements EntidadeContabil, IManadRegistro {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Etiqueta")
    private Empenho empenho;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta de Despesa")
    private Conta conta;

    @ManyToOne
    @Etiqueta("Evento Contábil")
    private EventoContabil eventoContabil;

    @Monetario
    @Etiqueta("Valor")
    private BigDecimal valor;

    @Monetario
    @Etiqueta("Saldo")
    private BigDecimal saldo;

    @ManyToOne
    @Etiqueta("Desdobramento Obrigação a Pagar")
    private DesdobramentoObrigacaoPagar desdobramentoObrigacaoPagar;

    @OneToOne
    private DesdobramentoEmpenho desdobramentoEmpenho;

    public DesdobramentoEmpenho() {
        valor = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public DesdobramentoObrigacaoPagar getDesdobramentoObrigacaoPagar() {
        return desdobramentoObrigacaoPagar;
    }

    public void setDesdobramentoObrigacaoPagar(DesdobramentoObrigacaoPagar desdobramentoObrigacaoPagar) {
        this.desdobramentoObrigacaoPagar = desdobramentoObrigacaoPagar;
    }

    public DesdobramentoEmpenho getDesdobramentoEmpenho() {
        return desdobramentoEmpenho;
    }

    public void setDesdobramentoEmpenho(DesdobramentoEmpenho desdobramentoEmpenho) {
        this.desdobramentoEmpenho = desdobramentoEmpenho;
    }

    @Override
    public String toString() {
        try {
            return "Empenho: " + this.empenho + " - desdobramento.: " + conta + " - " + Util.formataValor(valor);
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return ((EntidadeContabil) this.getEmpenho()).getReferenciaArquivoPrestacaoDeContas();
    }

    @Override
    public ManadRegistro.ManadModulo getModulo() {
        return ManadRegistro.ManadModulo.CONTABIL;
    }

    @Override
    public ManadRegistro.ManadRegistroTipo getTipoRegistro() {
        return ManadRegistro.ManadRegistroTipo.L100;
    }

    @Override
    public void getConteudoManad(ManadContabilFacade facade, StringBuilder conteudo) {
        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.L100.name(), conteudo);
        ManadUtil.criaLinha(2, this.getEmpenho().getEmpenho().getNumero(), conteudo);
        ManadUtil.criaLinha(3, this.getEmpenho().getNumero(), conteudo);
        ManadUtil.criaLinha(4, ManadUtil.getDataSemBarra(this.getEmpenho().getDataEmpenho()), conteudo);
        ManadUtil.criaLinha(5, ManadUtil.getValor(valor), conteudo);
        ManadUtil.criaLinha(6, "D", conteudo);
        ManadUtil.criaLinha(7, this.getEmpenho().getComplementoHistorico(), conteudo);
        ManadUtil.quebraLinha(conteudo);
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }

    public String toStringAutoComplete() {
        try {
            return conta +
                ", " + Util.formataValor(valor) +
                ", Nº " + empenho.getNumero() +
                ", " + DataUtil.getDataFormatada(empenho.getDataEmpenho());
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public GeradorContaAuxiliarDTO gerarContaAuxiliarDTO(ParametroEvento.ComplementoId complementoId) {
        AcaoPPA acaoPPA = empenho.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        if (empenho.getCodigoCO() != null && empenho.getContaDeDestinacao() != null) {
            empenho.getContaDeDestinacao().setCodigoCOEmenda(empenho.getCodigoCO().getCodigo());
        }
        if (CategoriaOrcamentaria.RESTO.equals(empenho.getCategoriaOrcamentaria())) {
            return new GeradorContaAuxiliarDTO(empenho.getUnidadeOrganizacional(),
                acaoPPA.getCodigoFuncaoSubFuncao(),
                empenho.getContaDeDestinacao(),
                empenho.getContaDespesa(),
                empenho.getCodigoEs(),
                empenho.getEmpenho().getExercicio().getAno(),
                empenho.getContaDespesa(),
                empenho.getExercicio(),
                empenho.getEmpenho().getExercicio(),
                null, 0, null,
                empenho.getContaDespesa().getCodigoContaSiconf());
        } else {
            return new GeradorContaAuxiliarDTO(empenho.getUnidadeOrganizacional(),
                acaoPPA.getCodigoFuncaoSubFuncao(),
                empenho.getContaDeDestinacao(),
                empenho.getContaDespesa(),
                empenho.getCodigoEs(),
                null,
                empenho.getContaDespesa(),
                empenho.getExercicio(), null, null, 0,
                null,
                (empenho.getContaDespesa().getCodigoContaSiconf()));

        }
    }
}
