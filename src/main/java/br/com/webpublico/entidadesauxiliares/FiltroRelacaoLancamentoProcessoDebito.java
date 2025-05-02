package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoProcessoDebito;
import com.google.common.collect.Lists;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 11/05/17
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
public class FiltroRelacaoLancamentoProcessoDebito extends AbstractFiltroRelacaoLancamentoDebito {


    private Long codigoProcessoInicial;
    private Long codigoProcessoFinal;
    private TipoProcessoDebito tipoProcessoDebito;
    private TipoCadastroTributario tipoCadastroTributario;
    private String inscricaoInicial;
    private String inscricaoFinal;

    public FiltroRelacaoLancamentoProcessoDebito() {
        setSituacoes(new SituacaoParcela[]{SituacaoParcela.EM_ABERTO});
        setTipoRelatorio(TipoRelatorio.RESUMIDO);
        setSomenteTotalizador(Boolean.FALSE);
    }

    public Long getCodigoProcessoInicial() {
        return codigoProcessoInicial;
    }

    public void setCodigoProcessoInicial(Long codigoProcessoInicial) {
        this.codigoProcessoInicial = codigoProcessoInicial;
    }

    public Long getCodigoProcessoFinal() {
        return codigoProcessoFinal;
    }

    public void setCodigoProcessoFinal(Long codigoProcessoFinal) {
        this.codigoProcessoFinal = codigoProcessoFinal;
    }

    public TipoProcessoDebito getTipoProcessoDebito() {
        return tipoProcessoDebito;
    }

    public void setTipoProcessoDebito(TipoProcessoDebito tipoProcessoDebito) {
        this.tipoProcessoDebito = tipoProcessoDebito;
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
