package br.com.webpublico.entidades;

import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilGeradorContaAuxiliar;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.TreeMap;

/**
 * Created by mga on 28/07/2017.
 */
@Entity
@Audited
@Table(name = "DESDOBRAMENTOEMPESTORNO")
@GrupoDiagrama(nome = "Contábil")
public class DesdobramentoEmpenhoEstorno extends SuperEntidade implements IGeraContaAuxiliar, EntidadeContabil {

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
    public TreeMap getMapContaAuxiliarSistema(TipoContaAuxiliar tipoContaAuxiliar) {
        return UtilGeradorContaAuxiliar.gerarContaAuxiliarDesdobramentoEmpenhoEstorno(this);
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        AcaoPPA acaoPPA = empenhoEstorno.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        if (empenhoEstorno.getEmpenho().getCodigoCO() != null && empenhoEstorno.getEmpenho().getContaDeDestinacao() != null) {
            empenhoEstorno.getEmpenho().getContaDeDestinacao().setCodigoCOEmenda(empenhoEstorno.getEmpenho().getCodigoCO().getCodigo());
        }
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(empenhoEstorno.getEmpenho().getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada2(empenhoEstorno.getEmpenho().getUnidadeOrganizacional(),
                    contaContabil.getSubSistema());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada4(empenhoEstorno.getEmpenho().getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    empenhoEstorno.getEmpenho().getContaDeDestinacao(),
                    empenhoEstorno.getEmpenho().getExercicio());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada5(empenhoEstorno.getEmpenho().getUnidadeOrganizacional(),
                    empenhoEstorno.getEmpenho().getContaDeDestinacao(),
                    empenhoEstorno.getEmpenho().getExercicio());
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada7(empenhoEstorno.getEmpenho().getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    empenhoEstorno.getEmpenho().getContaDeDestinacao(),
                    empenhoEstorno.getEmpenho().getContaDespesa(),
                    (empenhoEstorno.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        empenhoEstorno.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            empenhoEstorno.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            empenhoEstorno.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "99":
                if (CategoriaOrcamentaria.RESTO.equals(empenhoEstorno.getEmpenho().getCategoriaOrcamentaria())) {
                    return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada9(empenhoEstorno.getEmpenho().getUnidadeOrganizacional(),
                        acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                        empenhoEstorno.getEmpenho().getContaDeDestinacao(),
                        empenhoEstorno.getEmpenho().getContaDespesa(),
                        (empenhoEstorno.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                            empenhoEstorno.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                                empenhoEstorno.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                                empenhoEstorno.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0),
                        empenhoEstorno.getEmpenho().getEmpenho().getExercicio().getAno(),
                        empenhoEstorno.getEmpenho().getExercicio(),
                        empenhoEstorno.getEmpenho().getEmpenho().getExercicio());
                }
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
        AcaoPPA acaoPPA = empenhoEstorno.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        if (empenhoEstorno.getEmpenho().getCodigoCO() != null && empenhoEstorno.getEmpenho().getContaDeDestinacao() != null) {
            empenhoEstorno.getEmpenho().getContaDeDestinacao().setCodigoCOEmenda(empenhoEstorno.getEmpenho().getCodigoCO().getCodigo());
        }
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(empenhoEstorno.getEmpenho().getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar2(empenhoEstorno.getEmpenho().getUnidadeOrganizacional(),
                    contaContabil.getSubSistema());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar4(empenhoEstorno.getEmpenho().getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    empenhoEstorno.getEmpenho().getContaDeDestinacao());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar5(empenhoEstorno.getEmpenho().getUnidadeOrganizacional(),
                    empenhoEstorno.getEmpenho().getContaDeDestinacao());
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar7(empenhoEstorno.getEmpenho().getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    empenhoEstorno.getEmpenho().getContaDeDestinacao(),
                    (empenhoEstorno.getEmpenho().getContaDespesa().getCodigoSICONFI() != null ?
                        empenhoEstorno.getEmpenho().getContaDespesa().getCodigoSICONFI() :
                        empenhoEstorno.getEmpenho().getContaDespesa().getCodigo().replace(".", "")),
                    (empenhoEstorno.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        empenhoEstorno.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            empenhoEstorno.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            empenhoEstorno.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "99":
                if (CategoriaOrcamentaria.RESTO.equals(empenhoEstorno.getEmpenho().getCategoriaOrcamentaria())) {
                    return UtilGeradorContaAuxiliar.gerarContaAuxiliar9(empenhoEstorno.getEmpenho().getUnidadeOrganizacional(),
                        acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                        empenhoEstorno.getEmpenho().getContaDeDestinacao(),
                        empenhoEstorno.getEmpenho().getContaDespesa(),
                        (empenhoEstorno.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                            empenhoEstorno.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                                empenhoEstorno.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                                empenhoEstorno.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0),
                        empenhoEstorno.getEmpenho().getEmpenho().getExercicio().getAno(),
                        empenhoEstorno.getEmpenho().getExercicio());
                }
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

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return ((EntidadeContabil) empenhoEstorno).getReferenciaArquivoPrestacaoDeContas();
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }
}
