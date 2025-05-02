/*
 * Codigo gerado automaticamente em Thu May 19 14:54:48 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CotaMaterialCentroDeCusto;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CotaMaterialCentroDeCustoFacade;
import br.com.webpublico.negocios.PoliticaDeEstoqueFacade;
import br.com.webpublico.negocios.RequisicaoMaterialFacade;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@ManagedBean(name = "cotaMaterialCentroDeCustoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novaCotaMaterialCentroDeCusto", pattern = "/cota-de-material-centro-de-custo/novo/", viewId = "/faces/administrativo/materiais/cotamaterialcentrodecusto/edita.xhtml"),
        @URLMapping(id = "editarCotaMaterialCentroDeCusto", pattern = "/cota-de-material-centro-de-custo/editar/#{cotaMaterialCentroDeCustoControlador.id}/", viewId = "/faces/administrativo/materiais/cotamaterialcentrodecusto/edita.xhtml"),
        @URLMapping(id = "listarCotaMaterialCentroDeCusto", pattern = "/cota-de-material-centro-de-custo/listar/", viewId = "/faces/administrativo/materiais/cotamaterialcentrodecusto/lista.xhtml"),
        @URLMapping(id = "verCotaMaterialCentroDeCusto", pattern = "/cota-de-material-centro-de-custo/ver/#{cotaMaterialCentroDeCustoControlador.id}/", viewId = "/faces/administrativo/materiais/cotamaterialcentrodecusto/visualizar.xhtml")
})
public class CotaMaterialCentroDeCustoControlador extends PrettyControlador<CotaMaterialCentroDeCusto> implements Serializable, CRUD {

    @EJB
    private CotaMaterialCentroDeCustoFacade cotaMaterialCentroDeCustoFacade;
    @EJB
    private RequisicaoMaterialFacade requisicaoMaterialFacade;
    @EJB
    private PoliticaDeEstoqueFacade politicaEstoqueFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;

    public CotaMaterialCentroDeCustoControlador() {
        metadata = new EntidadeMetaData(CotaMaterialCentroDeCusto.class);
    }

    public CotaMaterialCentroDeCustoFacade getFacade() {
        return cotaMaterialCentroDeCustoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return cotaMaterialCentroDeCustoFacade;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    private void atribuirUnidadeOrganizacionalViewToSelecionado() {
        try {
            selecionado.setCentroDeCusto(hierarquiaOrganizacionalSelecionada.getSubordinada());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private Boolean validarRegrasNegocio() {
        Boolean validou = Boolean.TRUE;

        if (cotaMaterialCentroDeCustoFacade.jaExisteMaterialCadastradoParametros(selecionado)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cota já cadastrada.", "Já existe uma cota cadastrada para este material no período e centro de custo informado."));
            validou = Boolean.FALSE;
        }

        Calendar calendario = Calendar.getInstance();
        calendario.set(selecionado.getAno(), selecionado.getMes() - 1, 01);
        BigDecimal totalJaUtilizado = requisicaoMaterialFacade.totalAtendidoDoItemRequisicaoNaUnidade(calendario.getTime(), selecionado.getMaterial(), selecionado.getCentroDeCusto());

        if (totalJaUtilizado.compareTo(selecionado.getQuantidade()) > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Quantidade Inválida.", "A cota informada é inferior a quantidade já atendida deste material."));
            validou = Boolean.FALSE;
        }

        BigDecimal totalEstoqueMaximo = politicaEstoqueFacade.recuperarEstoqueMaximo(selecionado.getMaterial(), selecionado.getCentroDeCusto());
        // Caso não haja estoque máximo o totalEstoqueMaximo será null, e a validação não será necessária.
        if (totalEstoqueMaximo != null) {
            if (selecionado.getQuantidade().compareTo(totalEstoqueMaximo) > 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Quantidade Inválida.", "A cota informada é superior ao estoque máximo cadastrado para esta unidade."));
                validou = Boolean.FALSE;
            }
        }
        return validou;
    }

    public boolean permiteExcluirCota() {
        boolean retorno = true;
        Date dataAtual = new Date();
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(dataAtual);

        int mesAtual = calendario.get(Calendar.MONTH) + 1;
        int anoAtual = calendario.get(Calendar.YEAR);

        if (anoAtual < selecionado.getAno()) {
            retorno = false;
        }

        if (anoAtual == selecionado.getAno() && selecionado.getMes() <= mesAtual) {
            retorno = false;
        }

        return retorno;
    }

    public boolean permiteAlterarCota() {
        boolean retorno = true;
        Date dataAtual = new Date();
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(dataAtual);

        int mesAtual = calendario.get(Calendar.MONTH) + 1;
        int anoAtual = calendario.get(Calendar.YEAR);

        if (anoAtual < selecionado.getAno()) {
            retorno = false;
        }

        if (anoAtual == selecionado.getAno() && selecionado.getMes() < mesAtual) {
            retorno = false;
        }

        return retorno;
    }

    private void criarParametrosIniciaisSelecionar() {
        hierarquiaOrganizacionalSelecionada = new HierarquiaOrganizacional();
        hierarquiaOrganizacionalSelecionada.setSubordinada(selecionado.getCentroDeCusto());
        tratarExibicaoBotoesVisualizar();
    }

    private void tratarExibicaoBotoesVisualizar() {
        if (!permiteAlterarCota()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Período de validade da cota.", "Não será possível ALTERAR esta cota pois seu período de validade já expirou."));
        }
        if (!permiteExcluirCota()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Período de validade da cota.", "Não será possível EXCLUIR esta cota pois seu período de validade já expirou ou é o corrente."));
        }
    }

    @Override
    public void salvar() {
        atribuirUnidadeOrganizacionalViewToSelecionado();

        if (Util.validaCampos(selecionado) && validarRegrasNegocio()) {
            super.salvar();
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cota-de-material-centro-de-custo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novaCotaMaterialCentroDeCusto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        hierarquiaOrganizacionalSelecionada = null;
    }

    @Override
    @URLAction(mappingId = "editarCotaMaterialCentroDeCusto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        criarParametrosIniciaisSelecionar();
    }

    @Override
    @URLAction(mappingId = "verCotaMaterialCentroDeCusto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }
}
