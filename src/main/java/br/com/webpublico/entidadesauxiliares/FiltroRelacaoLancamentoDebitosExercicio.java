package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.ProcessoJudicial;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import com.google.common.collect.Lists;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 22/07/15
 * Time: 13:50
 * To change this template use File | Settings | File Templates.
 */
public class FiltroRelacaoLancamentoDebitosExercicio extends AbstractFiltroRelacaoLancamentoDebito {


    private TipoCadastroTributario tipoCadastroTributario;
    private String inscricaoInicial;
    private String inscricaoFinal;

    public FiltroRelacaoLancamentoDebitosExercicio() {
        setContribuintes(new ArrayList<Pessoa>());
        setSituacoes(new SituacaoParcela[]{SituacaoParcela.EM_ABERTO});
        setTipoRelatorio(TipoRelatorio.RESUMIDO);
        setSomenteTotalizador(Boolean.FALSE);
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

    public void processaMudancaTipoCadastro() {
        inscricaoInicial = "";
        inscricaoFinal = "";
        setPessoa(null);
        setContribuintes(Lists.<Pessoa>newArrayList());
    }

}
