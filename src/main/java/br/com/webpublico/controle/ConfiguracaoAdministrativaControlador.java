/*
 * Codigo gerado automaticamente em Fri Feb 10 11:17:13 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoAdministrativa;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoAdministrativaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ManagedBean
@SessionScoped
public class ConfiguracaoAdministrativaControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private ConfiguracaoAdministrativaFacade configuracaoAdministrativaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private ConfiguracaoAdministrativa confAdmAntiga;
    private List<ConfiguracaoAdministrativa> historicoConfiguracoesAdministrativa;

    public ConfiguracaoAdministrativaControlador() {
        metadata = new EntidadeMetaData(ConfiguracaoAdministrativa.class);
    }

    public List<ConfiguracaoAdministrativa> getHistoricoConfiguracoesAdministrativa() {
        return historicoConfiguracoesAdministrativa;
    }

    public void setHistoricoConfiguracoesAdministrativa(List<ConfiguracaoAdministrativa> historicoConfiguracoesAdministrativa) {
        this.historicoConfiguracoesAdministrativa = historicoConfiguracoesAdministrativa;
    }

    public ConfiguracaoAdministrativaFacade getFacade() {
        return configuracaoAdministrativaFacade;
    }

    public ConfiguracaoAdministrativa getConfAdmAntiga() {
        return confAdmAntiga;
    }

    public void setConfAdmAntiga(ConfiguracaoAdministrativa confAdmAntiga) {
        this.confAdmAntiga = confAdmAntiga;
    }

    @Override
    public AbstractFacade getFacede() {
        return configuracaoAdministrativaFacade;
    }

    public ConfiguracaoAdministrativa esteSelecionado() {
        return (ConfiguracaoAdministrativa) selecionado;
    }

    @Override
    public void novo() {
        super.novo();
        criarParametrosNovaConfiguracaoAdministrativa();
    }

    private void criarParametrosNovaConfiguracaoAdministrativa() {
        esteSelecionado().setDesde(new Date());
        esteSelecionado().setAlteradoPor(sistemaFacade.getUsuarioCorrente());
        confAdmAntiga = null;
    }

    public String criarParametroFilho(ConfiguracaoAdministrativa configAdm) {
        // Altera o parâmetro anterior;
        confAdmAntiga = configAdm;
        confAdmAntiga.setValidoAte(new Date());

        // Copia as novas informações para o novo parâmetro
        ConfiguracaoAdministrativa configuracaoAdministrativaClonada = (ConfiguracaoAdministrativa) Util.clonarObjeto(configAdm);
        if (configuracaoAdministrativaClonada == null) {
            return caminho();
        }
        configuracaoAdministrativaClonada.setId(null);
        configuracaoAdministrativaClonada.setDesde(new Date());
        configuracaoAdministrativaClonada.setValidoAte(null);
        configuracaoAdministrativaClonada.setAlteradoPor(sistemaFacade.getUsuarioCorrente());
        setSelecionado(configuracaoAdministrativaClonada);
        return "edita.xhtml";
    }

    @Override
    public String salvar() {
        if (verificaSePermitirAlterar(confAdmAntiga)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao salvar parâmetro.", "Já existem locais de estoque cadastrados com o parâmetro informado '" + esteSelecionado().getParametro() + "' e com a Máscara " + confAdmAntiga.getValor() + "."));
            return "edita.xhtml";
        }
        if (!salvarConfAdmAntiga()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao salvar parâmetro pai.", "Ocorreu um erro ao salvar o parâmetro pai. Por favor, tente novamente!"));
            return "edita.xhtml";
        }
        if (confAdmAntiga == null) {
            if (configuracaoAdministrativaFacade.verificarParametroJaExistente(esteSelecionado()) != null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao salvar parâmetro.", "Já existe uma configuração cadastrada com o parâmetro informado '" + esteSelecionado().getParametro() + "'. Por favor, duplique-o!"));
                return "edita.xhtml";
            }
        }
        confAdmAntiga = null;
        return super.salvar();
    }

    private Boolean salvarConfAdmAntiga() {
        if (confAdmAntiga == null) {
            return Boolean.TRUE;
        }

        ConfiguracaoAdministrativa confAdmDaVez = esteSelecionado();
        setSelecionado(confAdmAntiga);
        try {
            getFacade().salvar(confAdmAntiga);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Parâmetro Antigo.", "A data de validade do parâmetro antigo foi alterada."));
        } catch (Exception e) {
            return Boolean.FALSE;
        }

        setSelecionado(confAdmDaVez);
        return Boolean.TRUE;
    }

    @Override
    public List getLista() {
        if (lista == null) {
            lista = configuracaoAdministrativaFacade.lista();
        }
        return lista;
    }

    public String recuperarHistoricoAlteracoes(ConfiguracaoAdministrativa caParametro) {
        try {
            historicoConfiguracoesAdministrativa = configuracaoAdministrativaFacade.recuperarHistoricoAlteracoes(caParametro);
            return "visualizar_historico.xhtml";
        } catch (Exception e) {
            return "";
        }
    }

    public Boolean verificaSePermitirAlterar(ConfiguracaoAdministrativa conf) {
        if (conf.getParametro().equals("MASCARA_CODIGO_LOCAL_ESTOQUE")) {
            if (configuracaoAdministrativaFacade.validarSeParametroPossuiVinculoComLocalEstoque()) {
                return true;
            }
        }
        return false;
    }
}
