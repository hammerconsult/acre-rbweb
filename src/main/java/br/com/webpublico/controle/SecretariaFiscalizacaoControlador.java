/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SecretariaFiscalizacao;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SecretariaFiscalizacaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "secretariaFiscalizacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaSecretariaFiscalizacao", pattern = "/secretaria-de-fiscalizacao/novo/", viewId = "/faces/tributario/fiscalizacaosecretaria/secretaria/edita.xhtml"),
    @URLMapping(id = "editarSecretariaFiscalizacao", pattern = "/secretaria-de-fiscalizacao/editar/#{secretariaFiscalizacaoControlador.id}/", viewId = "/faces/tributario/fiscalizacaosecretaria/secretaria/edita.xhtml"),
    @URLMapping(id = "listarSecretariaFiscalizacao", pattern = "/secretaria-de-fiscalizacao/listar/", viewId = "/faces/tributario/fiscalizacaosecretaria/secretaria/lista.xhtml"),
    @URLMapping(id = "verSecretariaFiscalizacao", pattern = "/secretaria-de-fiscalizacao/ver/#{secretariaFiscalizacaoControlador.id}/", viewId = "/faces/tributario/fiscalizacaosecretaria/secretaria/visualizar.xhtml")
})
public class SecretariaFiscalizacaoControlador extends PrettyControlador<SecretariaFiscalizacao> implements Serializable, CRUD {

    @EJB
    private SecretariaFiscalizacaoFacade secretariaFiscalizacaoFacade;
    private ConverterAutoComplete converterSecretaria;
    private ConverterAutoComplete converterDepartamento;
    private ConverterAutoComplete converterDivida;

    public SecretariaFiscalizacaoControlador() {
        super(SecretariaFiscalizacao.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/secretaria-de-fiscalizacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return secretariaFiscalizacaoFacade;
    }

    @URLAction(mappingId = "novaSecretariaFiscalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        selecionado = new SecretariaFiscalizacao();
        selecionado.setCodigo(secretariaFiscalizacaoFacade.recuperaProximaCodigo());
    }

    private void recuperarSecretaria() {
        HierarquiaOrganizacional hierarquiaOrganizacional = secretariaFiscalizacaoFacade.getHierarquiaOrganizacionalFacade()
            .recuperarHierarquiaOrganizacionalPorUnidadeId(selecionado.getUnidadeOrganizacional().getId(),
                TipoHierarquiaOrganizacional.ADMINISTRATIVA,
                secretariaFiscalizacaoFacade.getSistemaFacade().getDataOperacao());
        selecionado.setHierarquiaOrganizacional(hierarquiaOrganizacional);
    }

    @URLAction(mappingId = "editarSecretariaFiscalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarSecretaria();
    }

    @URLAction(mappingId = "verSecretariaFiscalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarSecretaria();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    private boolean validaCampos() {
        boolean deuCerto = Util.validaCampos(selecionado);
        if (selecionado.getPrazoRecurso() != null && selecionado.getPrazoRecurso() <= 0) {
            deuCerto = false;
            FacesUtil.addOperacaoNaoPermitida("O  prazo para o Termo de Advertência (Dias) não pode ser menor que zero ");
        }
        if (selecionado.getPrazoAutoInfracao() != null && selecionado.getPrazoAutoInfracao() <= 0) {
            deuCerto = false;
            FacesUtil.addOperacaoNaoPermitida("O  prazo para o Auto de Infração (Dias) não pode ser menor que zero ");
        }
        if (selecionado.getPrazoPrimeiraInstancia() != null && selecionado.getPrazoPrimeiraInstancia() <= 0) {
            deuCerto = false;
            FacesUtil.addOperacaoNaoPermitida("O  prazo para 1ª Instância não pode ser menor que zero ");
        }
        if (selecionado.getPrazoSegundaInstancia() != null && selecionado.getPrazoSegundaInstancia() <= 0) {
            deuCerto = false;
            FacesContext.getCurrentInstance().addMessage("Formulario:segundaInstancia", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível continuar!", "O campo 2ª Instância não pode ser igual ou menor que zero."));

        }
        if (selecionado.getVencimentoDam() != null && selecionado.getVencimentoDam() <= 0) {
            deuCerto = false;
            FacesUtil.addOperacaoNaoPermitida("O  prazo para o vencimentod o DAM não pode ser menor ou igual a zero ");

        }
        if (selecionado.getPrazoReincidenciaEspecifica() != null && selecionado.getPrazoReincidenciaEspecifica() <= 0) {
            deuCerto = false;
            FacesUtil.addOperacaoNaoPermitida("O  prazo para Reincidência Específica não pode ser menor ou igual a zero ");
        }

        if (selecionado.getPrazoReincidenciaGenerica() != null && selecionado.getPrazoReincidenciaGenerica() <= 0) {
            deuCerto = false;
            FacesUtil.addOperacaoNaoPermitida("O  prazo para Reincidência Genérica não pode ser menor ou igual a zero ");

        }

        if (selecionado.getPrazoPrefeitura() != null && selecionado.getPrazoPrefeitura() <= 0) {
            deuCerto = false;
            FacesUtil.addOperacaoNaoPermitida("O  Prazo da Secretaria não pode ser menor ou igual a zero ");

        }
        return deuCerto;
    }

    public ConverterAutoComplete getConverterDepartamento() {
        if (converterDepartamento == null) {
            converterDepartamento = new ConverterAutoComplete(UnidadeOrganizacional.class, secretariaFiscalizacaoFacade.getUnidadeOrganizacionalFacade());
        }
        return converterDepartamento;
    }

    public ConverterAutoComplete getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterAutoComplete(Divida.class, secretariaFiscalizacaoFacade.getDividaFacade());
        }
        return converterDivida;
    }

    public ConverterAutoComplete getConverterSecretaria() {
        if (converterSecretaria == null) {
            converterSecretaria = new ConverterAutoComplete(UnidadeOrganizacional.class, secretariaFiscalizacaoFacade.getUnidadeOrganizacionalFacade());
        }
        return converterSecretaria;
    }

    public List<UnidadeOrganizacional> completaSecretaria(String parte) {
        return secretariaFiscalizacaoFacade.completaSecretaria(parte.trim());
    }

    public List<UnidadeOrganizacional> completaDepartamento(String parte) {
        return secretariaFiscalizacaoFacade.completaDepartamento(parte.trim(), selecionado.getUnidadeOrganizacional());
    }

    public List<Divida> completaDivida(String parte) {
        return secretariaFiscalizacaoFacade.completaDivida(parte.trim());
    }

    public void nullDepartamento() {
        selecionado.setDepartamento(null);
    }

    public List<SecretariaFiscalizacao> completarSecretariaFiscalizacao(String parte) {
        return secretariaFiscalizacaoFacade.completarSecretariaFiscalizacao(parte.trim());
    }
}
