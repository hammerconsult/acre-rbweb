package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EmpenhoVinculoFPFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-empenho-fichas-financeiras", pattern = "/empenho-fichas-financeiras/novo/", viewId = "/faces/rh/configuracao/empenho-fichas-financeiras/edita.xhtml"),
    @URLMapping(id = "editar-empenho-fichas-financeiras", pattern = "/empenho-fichas-financeiras/editar/#{empenhoVinculoFPControlador.id}/", viewId = "/faces/rh/configuracao/empenho-fichas-financeiras/edita.xhtml"),
    @URLMapping(id = "ver-empenho-fichas-financeiras", pattern = "/empenho-fichas-financeiras/ver/#{empenhoVinculoFPControlador.id}/", viewId = "/faces/rh/configuracao/empenho-fichas-financeiras/visualizar.xhtml"),
    @URLMapping(id = "listar-empenho-fichas-financeiras", pattern = "/empenho-fichas-financeiras/listar/", viewId = "/faces/rh/configuracao/empenho-fichas-financeiras/lista.xhtml"),
})
public class EmpenhoVinculoFPControlador extends PrettyControlador<EmpenhoVinculoFP> implements Serializable, CRUD {

    @EJB
    private EmpenhoVinculoFPFacade facade;
    private ConverterAutoComplete converterVinculoFP;
    private FolhaDePagamento folhaDePagamento;
    private VinculoFP vinculoFP;
    private CompetenciaFP competenciaFP;
    private List<FichaFinanceiraFP> fichasFinanceiras;
    private FichaFinanceiraFP[] fichasSelecionadas;

    public EmpenhoVinculoFPControlador() {
        super(EmpenhoVinculoFP.class);
    }

    @URLAction(mappingId = "nova-empenho-fichas-financeiras", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataCadastro(UtilRH.getDataOperacao());
        inicializarCampos();
    }

    private void inicializarCampos() {
        fichasFinanceiras = Lists.newArrayList();
        fichasSelecionadas = null;
        vinculoFP = null;
        folhaDePagamento = null;
        competenciaFP = null;
    }

    @URLAction(mappingId = "editar-empenho-fichas-financeiras", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        inicializarCampos();
    }

    @URLAction(mappingId = "ver-empenho-fichas-financeiras", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            selecionado.realizarValidacoes();
            validarCampos();
            if (isOperacaoNovo()) {
                facade.salvarNovo(selecionado);
            } else {
                facade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getFichasFinanceiras().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhuma ficha financeira selecionada.");
        }
        ve.lancarException();
    }

    private void validarFichasFinanceiras() {
        ValidacaoException ve = new ValidacaoException();
        if (fichasSelecionadas.length == 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhuma ficha financeira selecionada.");
        }
        for (FichaFinanceiraFP ficha : fichasSelecionadas) {
            for (EmpenhoFichaFinanceiraFP empenhoFichaFinanceiraFP : selecionado.getFichasFinanceiras()) {
                if (empenhoFichaFinanceiraFP.getFichaFinanceiraFP().equals(ficha)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A ficha financeira " + ficha.getVinculoFP().getDescricao() + " já está adicionada.");
                    break;
                }
            }
            if (facade.hasVinculoComFichaCadastrado(ficha)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A ficha financeira " + ficha.getVinculoFP().getDescricao() + " já está vinculada em outro cadastro.");
                break;
            }
        }
        ve.lancarException();
    }

    public void popularFichas() {
        try {
            validarFichasFinanceiras();
            for (FichaFinanceiraFP ficha : fichasSelecionadas) {
                EmpenhoFichaFinanceiraFP empenhoFichaFinanceiraFP = new EmpenhoFichaFinanceiraFP();
                empenhoFichaFinanceiraFP.setEmpenhoVinculoFP(selecionado);
                empenhoFichaFinanceiraFP.setFichaFinanceiraFP(ficha);
                Util.adicionarObjetoEmLista(selecionado.getFichasFinanceiras(), empenhoFichaFinanceiraFP);
            }
            inicializarCampos();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    public void removerFicha(EmpenhoFichaFinanceiraFP empenhoFichaFinanceiraFP) {
        selecionado.getFichasFinanceiras().remove(empenhoFichaFinanceiraFP);
    }

    public List<Empenho> completarEmpenhos(String filtro) {
        return facade.buscarEmpenhos(filtro);
    }

    public List<VinculoFP> buscarServidores(String filtro) {
        return facade.buscarServidores(filtro);
    }

    public List<SelectItem> getCompetenciasFP() {
        List<SelectItem> toReturn = Lists.newArrayList();
        List<CompetenciaFP> competencias = facade.buscarCompetencias();
        toReturn.add(new SelectItem(null, ""));
        if (!competencias.isEmpty()) {
            for (CompetenciaFP obj : competencias) {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getFolhasDePagamento() {
        List<SelectItem> toReturn = Lists.newArrayList();
        if (competenciaFP != null) {
            List<FolhaDePagamento> folhasDePagamento = facade.buscarFolhasDePagamento(competenciaFP);
            toReturn.add(new SelectItem(null, ""));
            if (!folhasDePagamento.isEmpty()) {
                for (FolhaDePagamento obj : folhasDePagamento) {
                    toReturn.add(new SelectItem(obj, obj.toString()));
                }
            }
        }
        return toReturn;
    }

    public ConverterAutoComplete getConverterVinculoFP() {
        if (converterVinculoFP == null) {
            converterVinculoFP = new ConverterAutoComplete(VinculoFP.class, facade.getVinculoFPFacade());
        }
        return converterVinculoFP;
    }

    public void buscarFichasFinanceiras() {
        try {
            validarFiltros();
            fichasFinanceiras = facade.buscarFichasFinanceirasPorCompetenciaOrServidorOrFolha(montarFiltros());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao buscar as fichas financeiras: ", ex);
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro durante a pesquisa. Detalhes do erro: " + ex.getMessage());
        }
    }

    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (competenciaFP == null && folhaDePagamento == null && vinculoFP == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Por favor, selecione pelo menos um dos campos para consultar as fichas financeiras.");
        }
        ve.lancarException();

    }

    private String montarFiltros() {
        String clausula = " where ";
        String retorno = "";
        if (competenciaFP != null) {
            retorno += clausula + " folha.COMPETENCIAFP_ID = " + competenciaFP.getId();
            clausula = " and ";
        }
        if (folhaDePagamento != null) {
            retorno += clausula + " folha.id = " + folhaDePagamento.getId();
            clausula = " and ";
        }
        if (vinculoFP != null) {
            retorno += clausula + " ficha.VINCULOFP_ID = " + vinculoFP.getId();
        }
        return retorno;
    }

    public FolhaDePagamento getFolhaDePagamento() {
        return folhaDePagamento;
    }

    public void setFolhaDePagamento(FolhaDePagamento folhaDePagamento) {
        this.folhaDePagamento = folhaDePagamento;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/empenho-fichas-financeiras/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<FichaFinanceiraFP> getFichasFinanceiras() {
        return fichasFinanceiras;
    }

    public void setFichasFinanceiras(List<FichaFinanceiraFP> fichasFinanceiras) {
        this.fichasFinanceiras = fichasFinanceiras;
    }

    public FichaFinanceiraFP[] getFichasSelecionadas() {
        return fichasSelecionadas;
    }

    public void setFichasSelecionadas(FichaFinanceiraFP[] fichasSelecionadas) {
        this.fichasSelecionadas = fichasSelecionadas;
    }

    public CompetenciaFP getCompetenciaFP() {
        return competenciaFP;
    }

    public void setCompetenciaFP(CompetenciaFP competenciaFP) {
        this.competenciaFP = competenciaFP;
    }
}
