package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.TipoDividaDiversa;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import com.google.common.collect.Lists;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 22/07/15
 * Time: 13:50
 * To change this template use File | Settings | File Templates.
 */
public class FiltroRelacaoLancamentoDividaDiversa extends AbstractFiltroRelacaoLancamentoDebito {


    private Integer numeroProcessoInicial;
    private Integer numeroProcessoFinal;
    private TipoDividaDiversa tipoDividaDiversa;
    private TipoCadastroTributario tipoCadastroTributario;
    private String inscricaoInicial;
    private String inscricaoFinal;

    public FiltroRelacaoLancamentoDividaDiversa() {
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

    public Integer getNumeroProcessoInicial() {
        return numeroProcessoInicial;
    }

    public void setNumeroProcessoInicial(Integer numeroProcessoInicial) {
        this.numeroProcessoInicial = numeroProcessoInicial;
    }

    public Integer getNumeroProcessoFinal() {
        return numeroProcessoFinal;
    }

    public void setNumeroProcessoFinal(Integer numeroProcessoFinal) {
        this.numeroProcessoFinal = numeroProcessoFinal;
    }

    public TipoDividaDiversa getTipoDividaDiversa() {
        return tipoDividaDiversa;
    }

    public void setTipoDividaDiversa(TipoDividaDiversa tipoDividaDiversa) {
        this.tipoDividaDiversa = tipoDividaDiversa;
    }

    public void processaMudancaTipoCadastro() {
        inscricaoInicial = "";
        inscricaoFinal = "";
        setPessoa(null);
        setContribuintes(Lists.<Pessoa>newArrayList());
    }
}
