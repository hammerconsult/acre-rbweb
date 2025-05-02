package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.SecretariaFiscalizacao;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 21/07/15
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */
public class FiltroRelacaoLancamentoFiscalizacaoSecretaria extends AbstractFiltroRelacaoLancamentoDebito {

    private TipoCadastroTributario tipoCadastroTributario;
    private String inscricaoInicial;
    private String inscricaoFinal;
    private String numeroAutoInfracaoInicial;
    private String numeroAutoInfracaoFinal;
    private SecretariaFiscalizacao secretariaFiscalizacao;
    private List<SecretariaFiscalizacao> secretarias;

    public FiltroRelacaoLancamentoFiscalizacaoSecretaria() {
        setSituacoes(new SituacaoParcela[]{SituacaoParcela.EM_ABERTO});
        setTipoRelatorio(TipoRelatorio.RESUMIDO);
        setSomenteTotalizador(Boolean.FALSE);
        secretarias = Lists.newArrayList();
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

    public String getNumeroAutoInfracaoInicial() {
        return numeroAutoInfracaoInicial;
    }

    public void setNumeroAutoInfracaoInicial(String numeroAutoInfracaoInicial) {
        this.numeroAutoInfracaoInicial = numeroAutoInfracaoInicial;
    }

    public String getNumeroAutoInfracaoFinal() {
        return numeroAutoInfracaoFinal;
    }

    public void setNumeroAutoInfracaoFinal(String numeroAutoInfracaoFinal) {
        this.numeroAutoInfracaoFinal = numeroAutoInfracaoFinal;
    }

    public SecretariaFiscalizacao getSecretariaFiscalizacao() {
        return secretariaFiscalizacao;
    }

    public void setSecretariaFiscalizacao(SecretariaFiscalizacao secretariaFiscalizacao) {
        this.secretariaFiscalizacao = secretariaFiscalizacao;
    }

    public List<SecretariaFiscalizacao> getSecretarias() {
        return secretarias;
    }

    public void setSecretarias(List<SecretariaFiscalizacao> secretarias) {
        this.secretarias = secretarias;
    }

    public void processaMudancaTipoCadastro() {
        inscricaoInicial = "";
        inscricaoFinal = "";
        setPessoa(null);
        setContribuintes(Lists.<Pessoa>newArrayList());
    }

    public void adicionarSecretaria() {
        if (secretarias == null) {
            FacesUtil.addCampoObrigatorio("Por favor selecione uma secretaria!");
            return;
        }
        if (secretarias != null && secretarias.contains(this.getSecretariaFiscalizacao())) {
            FacesUtil.addCampoObrigatorio("Secretaria já foi adicionada a pesquisa!");
            return;
        }
        if (secretarias == null) {
            secretarias = Lists.newArrayList();
        }
        this.secretarias.add(secretariaFiscalizacao);
        this.secretariaFiscalizacao = null;
    }

    public void removerSecretaria(SecretariaFiscalizacao secretaria) {
        secretarias.remove(secretaria);
    }
}

