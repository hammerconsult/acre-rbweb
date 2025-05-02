package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoLancamentoContabil;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.util.UtilGeradorContaAuxiliar;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.TreeMap;

@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
@Etiqueta("Conta Lançamento Contábil Manual")
public class ContaLancamentoManual extends SuperEntidade implements Serializable, IGeraContaAuxiliar {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private LancamentoContabilManual lancamento;
    @Enumerated(EnumType.STRING)
    private TipoLancamentoContabil tipo;
    @ManyToOne
    private ContaContabil contaContabil;
    @ManyToOne
    private ContaDeDestinacao contaDeDestinacao;
    @ManyToOne
    private LCP lcp;
    @Transient
    private Conta contaSintetica;

    public ContaLancamentoManual() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LancamentoContabilManual getLancamento() {
        return lancamento;
    }

    public void setLancamento(LancamentoContabilManual lancamento) {
        this.lancamento = lancamento;
    }

    public TipoLancamentoContabil getTipo() {
        return tipo;
    }

    public void setTipo(TipoLancamentoContabil tipo) {
        this.tipo = tipo;
    }

    public ContaContabil getContaContabil() {
        return contaContabil;
    }

    public void setContaContabil(ContaContabil contaContabil) {
        this.contaContabil = contaContabil;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public LCP getLcp() {
        return lcp;
    }

    public void setLcp(LCP lcp) {
        this.lcp = lcp;
    }

    public Conta getContaSintetica() {
        return contaSintetica;
    }

    public void setContaSintetica(Conta contaSintetica) {
        this.contaSintetica = contaSintetica;
    }

    @Override
    public TreeMap getMapContaAuxiliarSistema(TipoContaAuxiliar tipoContaAuxiliar) {
        Conta conta = null;
        if (lancamento.getContaDespesa() != null) {
            conta = lancamento.getContaDespesa();
        }
        if (lancamento.getContaReceita() != null) {
            conta = lancamento.getContaReceita();
        }
        switch (tipoContaAuxiliar.getCodigo()) {
            case "6"://FonteDeRecursos
            case "7"://FonteDeRecursos
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDestinacaoDeRecursos(contaDeDestinacao);
            case "12": //Conta de Despesa/Receita
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarConta(conta);
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(lancamento.getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada2(lancamento.getUnidadeOrganizacional(), contaContabil.getSubSistema());
            case "93":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada3(lancamento.getUnidadeOrganizacional(), contaContabil.getSubSistema(), 0);
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada4(lancamento.getUnidadeOrganizacional(), contaContabil.getSubSistema(), contaDeDestinacao, lancamento.getExercicio());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada5(lancamento.getUnidadeOrganizacional(), contaDeDestinacao, lancamento.getExercicio());
            case "96":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada6(lancamento.getUnidadeOrganizacional(), contaDeDestinacao, lancamento.getContaReceita(), lancamento.getExercicio());
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada7(lancamento.getUnidadeOrganizacional(),
                    (lancamento.getFuncao() != null && lancamento.getSubFuncao() != null)
                        ? lancamento.getFuncao().getCodigo() + lancamento.getSubFuncao().getCodigo()
                        : "",
                    contaDeDestinacao, lancamento.getContaDespesa(), null);
            case "98":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada8(lancamento.getUnidadeOrganizacional(), contaContabil.getSubSistema(), 0, contaDeDestinacao);
            case "99":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada9(lancamento.getUnidadeOrganizacional(),
                    (lancamento.getFuncao() != null && lancamento.getSubFuncao() != null)
                        ? lancamento.getFuncao().getCodigo() + lancamento.getSubFuncao().getCodigo()
                        : "",
                    contaDeDestinacao,
                    lancamento.getContaDespesa(),
                    null,
                    lancamento.getExercicioResto() != null ? lancamento.getExercicioResto().getAno() : lancamento.getExercicio().getAno(),
                    lancamento.getExercicio(),
                    lancamento.getExercicioResto() != null ? lancamento.getExercicioResto() : lancamento.getExercicio());
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
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(lancamento.getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar2(lancamento.getUnidadeOrganizacional(), contaContabil.getSubSistema());
            case "93":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar3(lancamento.getUnidadeOrganizacional(), contaContabil.getSubSistema(), 0);
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar4(lancamento.getUnidadeOrganizacional(), contaContabil.getSubSistema(), contaDeDestinacao);
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar5(lancamento.getUnidadeOrganizacional(), contaDeDestinacao);
            case "96":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar6(lancamento.getUnidadeOrganizacional(), contaDeDestinacao,
                    (lancamento.getContaReceita() != null
                        ? lancamento.getContaReceita().getCodigoSICONFI() != null
                        ? lancamento.getContaReceita().getCodigoSICONFI().replace(".", "")
                        : lancamento.getContaReceita().getCodigo().replace(".", "")
                        : "")
                );
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar7(lancamento.getUnidadeOrganizacional(),
                    (lancamento.getFuncao() != null && lancamento.getSubFuncao() != null)
                        ? lancamento.getFuncao().getCodigo() + lancamento.getSubFuncao().getCodigo()
                        : "",
                    contaDeDestinacao,
                    (lancamento.getContaDespesa() != null
                        ? lancamento.getContaDespesa().getCodigoSICONFI() != null
                        ? lancamento.getContaDespesa().getCodigoSICONFI()
                        : lancamento.getContaDespesa().getCodigo().replace(".", "")
                        : ""),
                    null);
            case "98":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar8(lancamento.getUnidadeOrganizacional(), contaContabil.getSubSistema(), 0, contaDeDestinacao);
            case "99":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar9(lancamento.getUnidadeOrganizacional(),
                    (lancamento.getFuncao() != null && lancamento.getSubFuncao() != null)
                        ? lancamento.getFuncao().getCodigo() + lancamento.getSubFuncao().getCodigo()
                        : "",
                    contaDeDestinacao,
                    lancamento.getContaDespesa(),
                    null,
                    lancamento.getExercicioResto() != null ? lancamento.getExercicioResto().getAno() : null,
                    lancamento.getExercicio());
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
