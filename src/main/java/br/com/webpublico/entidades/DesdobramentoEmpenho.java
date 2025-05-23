package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.manad.ManadRegistro;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.interfaces.IManadRegistro;
import br.com.webpublico.negocios.manad.ManadContabilFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.ManadUtil;
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
 * Created by mga on 07/07/2017.
 */
@Entity
@Audited
@Etiqueta("Desdobramento Empenho")
public class DesdobramentoEmpenho extends SuperEntidade implements IGeraContaAuxiliar, EntidadeContabil, IManadRegistro {

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
    public TreeMap getMapContaAuxiliarSistema(TipoContaAuxiliar tipoContaAuxiliar) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "3": //FonteDespesaOrc
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarFonteDespesaOrc(empenho.getFonteDespesaORC());
            case "4": //ProvisaoPPADespesa
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarProvisaoPPADespesa(empenho.getDespesaORC().getProvisaoPPADespesa());
            case "5"://Empenho
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDesdobramentoEmpenho(this);
            case "6"://FonteDeRecursos
            case "7"://FonteDeRecursos
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDestinacaoDeRecursos(empenho.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao());
            case "9"://Pessoa
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarPessoa(empenho.getFornecedor());
            case "12": //Conta de Despesa
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarConta(empenho.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa());
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        AcaoPPA acaoPPA = empenho.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        if (empenho.getCodigoCO() != null && empenho.getContaDeDestinacao() != null) {
            empenho.getContaDeDestinacao().setCodigoCOEmenda(empenho.getCodigoCO().getCodigo());
        }
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(empenho.getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada2(empenho.getUnidadeOrganizacional(), contaContabil.getSubSistema());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada4(empenho.getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    empenho.getContaDeDestinacao(),
                    empenho.getExercicio());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada5(empenho.getUnidadeOrganizacional(),
                    empenho.getContaDeDestinacao(),
                    empenho.getExercicio());
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada7(empenho.getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    empenho.getContaDeDestinacao(),
                    empenho.getContaDespesa(),
                    (empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "99":
                if (CategoriaOrcamentaria.RESTO.equals(empenho.getCategoriaOrcamentaria())) {
                    return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada9(empenho.getUnidadeOrganizacional(),
                        acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                        empenho.getContaDeDestinacao(),
                        empenho.getContaDespesa(),
                        (empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                            empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                                empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                                empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0),
                        empenho.getEmpenho().getExercicio().getAno(),
                        empenho.getExercicio(),
                        empenho.getEmpenho().getExercicio());
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
        AcaoPPA acaoPPA = empenho.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        if (empenho.getCodigoCO() != null && empenho.getContaDeDestinacao() != null) {
            empenho.getContaDeDestinacao().setCodigoCOEmenda(empenho.getCodigoCO().getCodigo());
        }
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(empenho.getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar2(empenho.getUnidadeOrganizacional(),
                    contaContabil.getSubSistema());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar4(empenho.getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    empenho.getContaDeDestinacao());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar5(empenho.getUnidadeOrganizacional(),
                    empenho.getContaDeDestinacao());
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar7(empenho.getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    empenho.getContaDeDestinacao(),
                    (empenho.getContaDespesa().getCodigoSICONFI() != null ?
                        empenho.getContaDespesa().getCodigoSICONFI() :
                        empenho.getContaDespesa().getCodigo().replace(".", "")),
                    (empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "99":
                if (CategoriaOrcamentaria.RESTO.equals(empenho.getCategoriaOrcamentaria())) {
                    return UtilGeradorContaAuxiliar.gerarContaAuxiliar9(empenho.getUnidadeOrganizacional(),
                        acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                        empenho.getContaDeDestinacao(),
                        empenho.getContaDespesa(),
                        (empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                            empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                                empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                                empenho.getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0),
                        empenho.getEmpenho().getExercicio().getAno(),
                        empenho.getExercicio());
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
}
