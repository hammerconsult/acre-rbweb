/*
 * Codigo gerado automaticamente em Wed May 18 15:16:08 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.PoliticaDeEstoque;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.PoliticaDeEstoqueFacade;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "politicaDeEstoqueControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaPoliticaDeEstoque", pattern = "/politica-de-estoque/novo/", viewId = "/faces/administrativo/materiais/politicadeestoque/edita.xhtml"),
    @URLMapping(id = "editarPoliticaDeEstoque", pattern = "/politica-de-estoque/editar/#{politicaDeEstoqueControlador.id}/", viewId = "/faces/administrativo/materiais/politicadeestoque/edita.xhtml"),
    @URLMapping(id = "listarPoliticaDeEstoque", pattern = "/politica-de-estoque/listar/", viewId = "/faces/administrativo/materiais/politicadeestoque/lista.xhtml"),
    @URLMapping(id = "verPoliticaDeEstoque", pattern = "/politica-de-estoque/ver/#{politicaDeEstoqueControlador.id}/", viewId = "/faces/administrativo/materiais/politicadeestoque/visualizar.xhtml")
})
public class PoliticaDeEstoqueControlador extends PrettyControlador<PoliticaDeEstoque> implements Serializable, CRUD {

    @EJB
    private PoliticaDeEstoqueFacade politicaDeEstoqueFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;

    public PoliticaDeEstoqueControlador() {
        metadata = new EntidadeMetaData(PoliticaDeEstoque.class);
    }

    @Override
    public void salvar() {
        atribuirUnidadeOrganizacionalViewToSelecionado();

        try {
            validarCamposPositivos();

            if (validarCampos()) {
                validarRegrasNegocioPoliticaDeEstoque();
                super.salvar();
            }

        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }


    }

    public void validarCamposPositivos() throws ValidacaoException {
        ValidacaoException ex = new ValidacaoException();

        if ((selecionado.getEstoqueMinimo() != null) && selecionado.getEstoqueMinimo().compareTo(BigDecimal.ZERO) < 0)
            ex.adicionarMensagemDeOperacaoNaoPermitida("O campo  Estoque Mínimo não pode ser menor que zero(0).");

        if ((selecionado.getPontoDeReposicao() != null) && selecionado.getPontoDeReposicao().compareTo(BigDecimal.ZERO) < 0)
            ex.adicionarMensagemDeOperacaoNaoPermitida("O campo  Ponto de Reposição não pode ser menor que zero(0).");

        if ((selecionado.getEstoqueMaximo() != null) && selecionado.getEstoqueMaximo().compareTo(BigDecimal.ZERO) < 0)
            ex.adicionarMensagemDeOperacaoNaoPermitida("O campo  Estoque Máximo não pode ser menor que zero(0).");

        if ((selecionado.getLoteEconomico() != null) && selecionado.getLoteEconomico().compareTo(BigDecimal.ZERO) < 0)
            ex.adicionarMensagemDeOperacaoNaoPermitida("O campo  Lote Econômico não pode ser menor que zero(0).");

        if ((selecionado.getSaidaMaxima() != null) && selecionado.getSaidaMaxima().compareTo(BigDecimal.ZERO) < 0)
            ex.adicionarMensagemDeOperacaoNaoPermitida("O campo  Saída Maxima  não pode ser menor que zero(0).");


        if (ex.temMensagens())
            throw ex;
    }

    private void atribuirUnidadeOrganizacionalViewToSelecionado() {
        if (hierarquiaOrganizacionalSelecionada != null) {
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacionalSelecionada.getSubordinada());
        } else {
            selecionado.setUnidadeOrganizacional(null);
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/politica-de-estoque/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novaPoliticaDeEstoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        hierarquiaOrganizacionalSelecionada = (HierarquiaOrganizacional) Web.pegaDaSessao(HierarquiaOrganizacional.class);

        if (hierarquiaOrganizacionalSelecionada == null) {
            hierarquiaOrganizacionalSelecionada = new HierarquiaOrganizacional();
        }
    }

    @Override
    @URLAction(mappingId = "editarPoliticaDeEstoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalFacade.recuperaHierarquiaOrganizacionalPelaUnidade(selecionado.getUnidadeOrganizacional().getId());
    }

    @Override
    @URLAction(mappingId = "verPoliticaDeEstoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    public AbstractFacade getFacede() {
        return politicaDeEstoqueFacade;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public void navegarMaterial() {
        Web.navegacao(getUrlAtual(), "/material/novo/", selecionado, hierarquiaOrganizacionalSelecionada);
    }

    private void validarRegrasNegocioPoliticaDeEstoque() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getEstoqueMaximo() != null && selecionado.getEstoqueMinimo() != null) {
            // Estoque máximo deve ser maior que estoque mínimo
            if (selecionado.getEstoqueMaximo().compareTo(selecionado.getEstoqueMinimo()) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O estoque máximo deve ser superior ao estoque mínimo.");
            }
        }

        // Estoque máximo deve ser maior que ponto de reposição
        if (selecionado.getEstoqueMaximo() != null && selecionado.getPontoDeReposicao() != null) {
            if (selecionado.getEstoqueMaximo().compareTo(selecionado.getPontoDeReposicao()) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O estoque máximo deve ser superior ao ponto de reposição.");
            }
        }

        // O ponto de reposição deve ser superior ao estoque mínimo
        if ((selecionado.getPontoDeReposicao() != null && selecionado.getEstoqueMinimo() != null) && selecionado.getPontoDeReposicao().compareTo(selecionado.getEstoqueMinimo()) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O ponto de reposição deve ser superior ao estoque mínimo.");
        }

        if (politicaDeEstoqueFacade.jaExistePoliticaCadastradaMaterialUnidade(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma política de estoque cadastrada para este material nesta unidade.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public boolean validarCampos() {
        boolean validou = Util.validaCampos(selecionado);

        if (selecionado.getEstoqueMinimo() == null
            && selecionado.getEstoqueMaximo() == null
            && selecionado.getLoteEconomico() == null
            && selecionado.getPontoDeReposicao() == null
            && selecionado.getSaidaMaxima() == null
            && selecionado.getNivelDeNotificacao() == null) {
            FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "Além de Unidade Organizacional e Material pelo menos um campo não obrigatório deve ser preenchido.");
            validou = false;
        } else {
            if (selecionado.getNivelDeNotificacao() != null && PoliticaDeEstoque.TipoNivelNotificacao.ESTOQUE_MINIMO.equals(selecionado.getNivelDeNotificacao()) && selecionado.getEstoqueMinimo() == null) {
                FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "O campo Estoque Mínimo deve ser preenchido!");
                validou = false;
            }

            if (selecionado.getNivelDeNotificacao() != null && PoliticaDeEstoque.TipoNivelNotificacao.PONTO_REPOSICAO.equals(selecionado.getNivelDeNotificacao()) && selecionado.getPontoDeReposicao() == null) {
                FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "O campo Ponto de Reposição deve ser preenchido!");
                validou = false;
            }

            if (selecionado.getNivelDeNotificacao() == null && (selecionado.getEstoqueMinimo() != null || selecionado.getPontoDeReposicao() != null)) {
                FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "O campo nível de notificação deve ser preenchido com a opção 'Estoque Mínimo' ou 'Ponto de Reposição'!");
                validou = false;
            }
        }

        return validou;
    }

    public List<SelectItem> getListSelectItemNivelNotificacao() {
        return Util.getListSelectItem(Arrays.asList(PoliticaDeEstoque.TipoNivelNotificacao.values()));
    }
}
