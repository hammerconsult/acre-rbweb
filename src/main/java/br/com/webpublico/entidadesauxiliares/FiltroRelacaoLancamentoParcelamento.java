package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ParamParcelamento;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 22/07/15
 * Time: 13:50
 * To change this template use File | Settings | File Templates.
 */
public class FiltroRelacaoLancamentoParcelamento extends AbstractFiltroRelacaoLancamentoDebito {


    private Integer exercicioParcelamentoInicial;
    private Integer exercicioParcelamentoFinal;
    private Long numeroParcelamentoInicial;
    private Long numeroParcelamentoFinal;
    private Integer exercicioCertidaoInicial;
    private Integer exercicioCertidaoFinal;
    private String numeroCertidaoInicial;
    private String numeroCertidaoFinal;
    private Integer ajuizado;
    private String numeroAjuizamento;
    private TipoCadastroTributario tipoCadastroTributario;
    private String inscricaoInicial;
    private String inscricaoFinal;
    private Integer exercicioDividaAtivaInicial;
    private Integer exercicioDividaAtivaFinal;
    private ParamParcelamento paramParcelamento;
    private List<ParamParcelamento> parametrosParcelamento;

    public FiltroRelacaoLancamentoParcelamento() {
        setSituacoes(new SituacaoParcela[]{SituacaoParcela.EM_ABERTO});
        setTipoRelatorio(TipoRelatorio.RESUMIDO);
        setSomenteTotalizador(Boolean.FALSE);
        this.ajuizado = 2;
    }

    public Integer getExercicioParcelamentoInicial() {
        return exercicioParcelamentoInicial;
    }

    public void setExercicioParcelamentoInicial(Integer exercicioParcelamentoInicial) {
        this.exercicioParcelamentoInicial = exercicioParcelamentoInicial;
    }

    public Integer getExercicioDividaAtivaInicial() {
        return exercicioDividaAtivaInicial;
    }

    public void setExercicioDividaAtivaInicial(Integer exercicioDividaAtivaInicial) {
        this.exercicioDividaAtivaInicial = exercicioDividaAtivaInicial;
    }

    public Integer getExercicioDividaAtivaFinal() {
        return exercicioDividaAtivaFinal;
    }

    public void setExercicioDividaAtivaFinal(Integer exercicioDividaAtivaFinal) {
        this.exercicioDividaAtivaFinal = exercicioDividaAtivaFinal;
    }

    public Integer getExercicioParcelamentoFinal() {
        return exercicioParcelamentoFinal;
    }

    public void setExercicioParcelamentoFinal(Integer exercicioParcelamentoFinal) {
        this.exercicioParcelamentoFinal = exercicioParcelamentoFinal;
    }

    public Long getNumeroParcelamentoInicial() {
        return numeroParcelamentoInicial;
    }

    public void setNumeroParcelamentoInicial(Long numeroParcelamentoInicial) {
        this.numeroParcelamentoInicial = numeroParcelamentoInicial;
    }

    public Long getNumeroParcelamentoFinal() {
        return numeroParcelamentoFinal;
    }

    public void setNumeroParcelamentoFinal(Long numeroParcelamentoFinal) {
        this.numeroParcelamentoFinal = numeroParcelamentoFinal;
    }

    public Integer getExercicioCertidaoInicial() {
        return exercicioCertidaoInicial;
    }

    public void setExercicioCertidaoInicial(Integer exercicioCertidaoInicial) {
        this.exercicioCertidaoInicial = exercicioCertidaoInicial;
    }

    public Integer getExercicioCertidaoFinal() {
        return exercicioCertidaoFinal;
    }

    public void setExercicioCertidaoFinal(Integer exercicioCertidaoFinal) {
        this.exercicioCertidaoFinal = exercicioCertidaoFinal;
    }

    public String getNumeroCertidaoInicial() {
        return numeroCertidaoInicial;
    }

    public void setNumeroCertidaoInicial(String numeroCertidaoInicial) {
        this.numeroCertidaoInicial = numeroCertidaoInicial;
    }

    public String getNumeroCertidaoFinal() {
        return numeroCertidaoFinal;
    }

    public void setNumeroCertidaoFinal(String numeroCertidaoFinal) {
        this.numeroCertidaoFinal = numeroCertidaoFinal;
    }

    public Integer getAjuizado() {
        return ajuizado;
    }

    public void setAjuizado(Integer ajuizado) {
        this.ajuizado = ajuizado;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public String getInscricaoInicial() {
        return inscricaoInicial;
    }

    public void setInscricaoInicial(String inscricaoInicial) {
        this.inscricaoInicial = inscricaoInicial;
    }

    public String getInscricaoFinal() {
        return inscricaoFinal;
    }

    public void setInscricaoFinal(String inscricaoFinal) {
        this.inscricaoFinal = inscricaoFinal;
    }

    public String getNumeroAjuizamento() {
        return numeroAjuizamento;
    }

    public void setNumeroAjuizamento(String numeroAjuizamento) {
        this.numeroAjuizamento = numeroAjuizamento;
    }

    public ParamParcelamento getParamParcelamento() {
        return paramParcelamento;
    }

    public void setParamParcelamento(ParamParcelamento paramParcelamento) {
        this.paramParcelamento = paramParcelamento;
    }

    public List<ParamParcelamento> getParametrosParcelamento() {
        return parametrosParcelamento;
    }

    public void setParametrosParcelamento(List<ParamParcelamento> parametrosParcelamento) {
        this.parametrosParcelamento = parametrosParcelamento;
    }

    public void processaMudancaTipoCadastro() {
        inscricaoInicial = "";
        inscricaoFinal = "";
        setPessoa(null);
        setContribuintes(Lists.<Pessoa>newArrayList());
        exercicioDividaAtivaInicial = null;
        exercicioDividaAtivaFinal = null;
        paramParcelamento = null;
        parametrosParcelamento = Lists.newArrayList();
    }

    public void processarMudancaAjuizamento() {
        numeroAjuizamento = null;
    }

    public void adicionarParametroParcelamento() {
        if (parametrosParcelamento == null) {
            parametrosParcelamento = Lists.newArrayList();
        }
        if (!parametrosParcelamento.contains(paramParcelamento)) {
            parametrosParcelamento.add(paramParcelamento);
        }
        paramParcelamento = null;
    }

    public void removerParametroParcelamento(ParamParcelamento paramParcelamento) {
        parametrosParcelamento.remove(paramParcelamento);
    }

    public String inParametros() {
        StringBuilder in = new StringBuilder();
        String juncao = " ";

        if (parametrosParcelamento != null && !parametrosParcelamento.isEmpty()) {
            for (ParamParcelamento parametro : parametrosParcelamento) {
                in.append(juncao);
                in.append(parametro.getId());
                juncao = ", ";
            }
        }
        return in.toString();
    }

    public String filtroParametros() {
        StringBuilder selecionados = new StringBuilder();

        if (parametrosParcelamento != null && !parametrosParcelamento.isEmpty()) {
            for (ParamParcelamento parametro : parametrosParcelamento) {
                selecionados.append(parametro.getCodigo());
                selecionados.append("; ");
            }
        }
        return selecionados.toString();
    }
}
