package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by mga on 28/07/2017.
 */
@Entity
@Audited
@Table(name = "DESDOBRAMENTOEMPESTORNO")
@GrupoDiagrama(nome = "Contábil")
public class DesdobramentoEmpenhoEstorno extends SuperEntidadeContabilGerarContaAuxiliar implements EntidadeContabil {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Estorno de Empenho")
    private EmpenhoEstorno empenhoEstorno;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta de Despesa")
    private Conta conta;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Evento Contábil")
    private EventoContabil eventoContabil;

    @Monetario
    @Etiqueta("Valor")
    private BigDecimal valor;

    @ManyToOne
    @Etiqueta("Desdobramento Obrigação a Pagar")
    private DesdobramentoObrigacaoPagar desdobramentoObrigacaoPagar;
    @ManyToOne
    private DesdobramentoEmpenho desdobramentoEmpenho;

    public DesdobramentoEmpenhoEstorno() {
        valor = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EmpenhoEstorno getEmpenhoEstorno() {
        return empenhoEstorno;
    }

    public void setEmpenhoEstorno(EmpenhoEstorno empenhoEstorno) {
        this.empenhoEstorno = empenhoEstorno;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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
            return "Estorno de Empenho: " + empenhoEstorno + " - desdobramento: " + conta + ",  " + Util.formataValor(valor);
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return ((EntidadeContabil) empenhoEstorno).getReferenciaArquivoPrestacaoDeContas();
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }

    @Override
    public GeradorContaAuxiliarDTO gerarContaAuxiliarDTO(ParametroEvento.ComplementoId complementoId) {
        AcaoPPA acaoPPA = empenhoEstorno.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        if (empenhoEstorno.getEmpenho().getCodigoCO() != null && empenhoEstorno.getEmpenho().getContaDeDestinacao() != null) {
            empenhoEstorno.getEmpenho().getContaDeDestinacao().setCodigoCOEmenda(empenhoEstorno.getEmpenho().getCodigoCO().getCodigo());
        }
        if (CategoriaOrcamentaria.RESTO.equals(empenhoEstorno.getEmpenho().getCategoriaOrcamentaria())) {
            return new GeradorContaAuxiliarDTO(empenhoEstorno.getEmpenho().getUnidadeOrganizacional(),
                acaoPPA.getCodigoFuncaoSubFuncao(),
                empenhoEstorno.getEmpenho().getContaDeDestinacao(),
                empenhoEstorno.getEmpenho().getContaDespesa(),
                empenhoEstorno.getEmpenho().getCodigoEs(),
                empenhoEstorno.getEmpenho().getEmpenho().getExercicio().getAno(),
                empenhoEstorno.getEmpenho().getContaDespesa(),
                empenhoEstorno.getEmpenho().getExercicio(),
                empenhoEstorno.getEmpenho().getEmpenho().getExercicio(),
                null, 0, null,
                empenhoEstorno.getEmpenho().getContaDespesa().getCodigoContaSiconf());
        } else {
            return new GeradorContaAuxiliarDTO(empenhoEstorno.getEmpenho().getUnidadeOrganizacional(),
                acaoPPA.getCodigoFuncaoSubFuncao(),
                empenhoEstorno.getEmpenho().getContaDeDestinacao(),
                empenhoEstorno.getEmpenho().getContaDespesa(),
                empenhoEstorno.getEmpenho().getCodigoEs(),
                null,
                empenhoEstorno.getEmpenho().getContaDespesa(),
                empenhoEstorno.getEmpenho().getExercicio(), null, null, 0,
                null,
                (empenhoEstorno.getEmpenho().getContaDespesa().getCodigoContaSiconf()));

        }
    }
}
