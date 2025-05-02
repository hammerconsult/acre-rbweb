package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.EntidadeFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public abstract class RelatorioConvenioSuperControlador implements Serializable {
    @EJB
    protected SistemaFacade sistemaFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    protected Date vigenciaInicialDe;
    protected Date vigenciaInicialAte;
    protected Date vigenciaFinalDe;
    protected Date vigenciaFinalAte;
    protected Entidade entidade;

    public void limparCamposGeral() {
        vigenciaInicialDe = DataUtil.montaData(1, 1, sistemaFacade.getExercicioCorrente().getAno()).getTime();
        vigenciaInicialAte = sistemaFacade.getDataOperacao();
        vigenciaFinalDe = null;
        vigenciaFinalAte = null;
        entidade = null;
    }

    protected void validarGerarRelatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (vigenciaInicialDe == null || vigenciaInicialAte == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Os campos de Vigência Inicial devem ser informados.");
        }
        ve.lancarException();
    }

    public List<Entidade> completarEntidades(String parte) {
        return entidadeFacade.listaEntidadesAtivas(parte.trim());
    }

    public Date getVigenciaInicialDe() {
        return vigenciaInicialDe;
    }

    public void setVigenciaInicialDe(Date vigenciaInicialDe) {
        this.vigenciaInicialDe = vigenciaInicialDe;
    }

    public Date getVigenciaInicialAte() {
        return vigenciaInicialAte;
    }

    public void setVigenciaInicialAte(Date vigenciaInicialAte) {
        this.vigenciaInicialAte = vigenciaInicialAte;
    }

    public Date getVigenciaFinalDe() {
        return vigenciaFinalDe;
    }

    public void setVigenciaFinalDe(Date vigenciaFinalDe) {
        this.vigenciaFinalDe = vigenciaFinalDe;
    }

    public Date getVigenciaFinalAte() {
        return vigenciaFinalAte;
    }

    public void setVigenciaFinalAte(Date vigenciaFinalAte) {
        this.vigenciaFinalAte = vigenciaFinalAte;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }
}
