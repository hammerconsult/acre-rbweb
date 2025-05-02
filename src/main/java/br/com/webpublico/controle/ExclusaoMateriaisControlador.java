package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteExclusaoMateriais;
import br.com.webpublico.entidadesauxiliares.ExclusaoMateriaisItensVO;
import br.com.webpublico.entidadesauxiliares.ExclusaoMateriaisVO;
import br.com.webpublico.enums.TipoExclusaoMaterial;
import br.com.webpublico.enums.TipoRequisicaoMaterial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ExclusaoMateriaisFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaExclusaoMateriais", pattern = "/exclusao-materiais/novo/", viewId = "/faces/administrativo/materiais/exclusao-materiais/edita.xhtml"),
    @URLMapping(id = "listarExclusaoMateriais", pattern = "/exclusao-materiais/listar/", viewId = "/faces/administrativo/materiais/exclusao-materiais/lista.xhtml"),
    @URLMapping(id = "verExclusaoMateriais", pattern = "/exclusao-materiais/ver/#{exclusaoMateriaisControlador.id}/", viewId = "/faces/administrativo/materiais/exclusao-materiais/visualizar.xhtml")
})
public class ExclusaoMateriaisControlador extends PrettyControlador<ExclusaoMateriais> implements Serializable, CRUD {

    @EJB
    private ExclusaoMateriaisFacade facade;
    private AssistenteExclusaoMateriais assistente;

    public ExclusaoMateriaisControlador() {
        super(ExclusaoMateriais.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/exclusao-materiais/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novaExclusaoMateriais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataExclusao(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        assistente = new AssistenteExclusaoMateriais();
    }

    @URLAction(mappingId = "verExclusaoMateriais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void salvar() {
        try {
            validarCamposObrigatorio();
            validarPeriodoFase();
            verificarRelacionamentosDependentes();
            if (!assistente.getRelacionamentosDependentes().isEmpty()) {
                FacesUtil.executaJavaScript("dlgRelacionamentos.show()");
                FacesUtil.atualizarComponente("formdlgRelacionamentos");
                return;
            }
            ordenarRelacionamentos();
            assistente.setSelecionado(selecionado);
            selecionado = facade.salvarRetornando(assistente);
            if (selecionado.getTipo().isEntradaCompra()) {
                redirecionarParaReprocessamento();
            } else {
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
                FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public boolean hasMensagemAlerta() {
        return !Strings.isNullOrEmpty(assistente.getMensagemAlerta());
    }

    private void definirAtributosAssistente() {
        assistente.setMensagemAlerta("");
        switch (selecionado.getTipo()) {
            case SAIDA_CONSUMO:
                assistente.setUrlMovimento("/administrativo/materiais/saidaMaterial/consumo/edita.xhtml");
                assistente.setMensagemAlerta("A saída por consumo possui movimentos relacionamos, caso deseje excluir esses movimentos, selecione os mesmo na lista acima.");
                assistente.setClasseAlerta("alert alert-info");
                assistente.setMovimento(assistente.getSaidaMaterial().toString());
                break;
            case ENTRADA_COMPRA:
                assistente.setUrlMovimento("/administrativo/materiais/entradamaterial/compra/edita.xhtml");
                for (ExclusaoMateriaisVO mov : assistente.getMovimentos()) {
                    if (mov.getTipoRelacionamento().isLiquidacao()) {
                        assistente.setMensagemAlerta("Não é permitido excluir uma entrada por compra que já teve seu(s) documento(s) fiscal(is) liquidado(s).");
                        assistente.setClasseAlerta("alert alert-danger");
                        break;
                    }
                    if (assistente.getItens().stream().anyMatch(ExclusaoMateriaisItensVO::hasEstoqueNegativo)) {
                        assistente.setMensagemAlerta("Não é permitido excluir uma entrada por compra onde o valor total do item é maior que estoque atual.");
                        assistente.setClasseAlerta("alert alert-danger");
                        break;
                    }
                    assistente.setMensagemAlerta("Exclusão permitida");
                    assistente.setClasseAlerta("alert alert-success");
                }
                assistente.setMovimento(assistente.getEntradaMaterial().toString());
                break;
            case REQUISICAO_MATERIAL_CONSUMO:
                assistente.setUrlMovimento("/administrativo/materiais/requisicaomaterial/edita.xhtml");
                if (assistente.getMovimentos().size() > 1) {
                    assistente.setMensagemAlerta("A requisição por consumo possui movimentos relacionamos, para excluir a mesma, selecione todos os movimentos acima.");
                    assistente.setClasseAlerta("alert alert-danger");
                }
                assistente.setMovimento(assistente.getRequisicaoMaterial().toString());
                break;
            case AVALIACAO_MATERIAL:
                assistente.setUrlMovimento("/administrativo/materiais/aprovacaomaterial/edita.xhtml");
                for (ExclusaoMateriaisVO mov : assistente.getMovimentos()) {
                    if (mov.getTipoRelacionamento().isSaidaMaterial()) {
                        assistente.setMensagemAlerta("A avaliação de material possui saída de material, para excluir a mesma selecione todos os movimentos acima.");
                        assistente.setClasseAlerta("alert alert-danger");
                    }
                }
                assistente.setMovimento(assistente.getAprovacaoMaterial().toString());
                break;
        }
    }

    public void redirecionarParaReprocessamento() {
        List<Long> ids = assistente.getItens().stream().map(ExclusaoMateriaisItensVO::getIdMaterial).collect(Collectors.toList());
        Web.poeNaSessao("IDS_MATERIAIS", ids);
        Web.poeNaSessao("ID_OBJETO", selecionado.getId());
        FacesUtil.redirecionamentoInterno("/reprocessamento-estoque/");
    }

    public void validarPeriodoFase() {
        ValidacaoException ve = new ValidacaoException();
        if (facade.getFaseFacade().temBloqueioFaseParaRecurso(
            assistente.getUrlMovimento(),
            selecionado.getDataExclusao(),
            facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente(),
            facade.getSistemaFacade().getExercicioCorrente())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O movimento " + selecionado.getTipo().getDescricao() + " está bloqueado por período da fase.");
        }
        for (ExclusaoMateriaisVO mov : assistente.getMovimentos()) {
            if (mov.getTipoRelacionamento().isBensEstoqueSaida() && mov.getSelecionado()) {
                BensEstoque bensEstoque = mov.getSaidaMaterialContabil().getBensEstoque();
                if (facade.getFaseFacade().temBloqueioFaseParaRecurso(
                    "/financeiro/orcamentario/bens/bensestoque/edita.xhtml",
                    bensEstoque.getDataBem(),
                    bensEstoque.getUnidadeOrganizacional(),
                    bensEstoque.getExercicio())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe fase aberta para a exclusão do movimento contábil nº " + bensEstoque.getNumero() + ".");
                }
            }
        }
        ve.lancarException();
    }

    private void validarCamposObrigatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo deve ser informado.");
        }
        ve.lancarException();
        validarMovimento();
    }

    private void validarRelacionamentos() {
        ValidacaoException ve = new ValidacaoException();
        ve.lancarException();
    }

    public void validarMovimento() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipo().isSaidaConsumo() && assistente.getSaidaMaterial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo saída material deve ser informado.");
        }
        if (selecionado.getTipo().isEntradaCompra()) {
            if (assistente.getEntradaMaterial() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo entrada material deve ser informado.");
            }
            ve.lancarException();
            for (ExclusaoMateriaisVO rel : assistente.getMovimentos()) {
                if (rel.getTipoRelacionamento().isLiquidacao()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido excluir uma entrada por compra que já teve seu(s) documento(s) fiscal(is) liquidado(s).");
                    break;
                }
            }
            if (assistente.getItens().stream().anyMatch(ExclusaoMateriaisItensVO::hasEstoqueNegativo)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido excluir uma entrada por compra onde o valor total do item é maior que estoque atual.");
            }
        }
        if (selecionado.getTipo().isRequisicaoConsumo() && assistente.getRequisicaoMaterial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo requisição de material deve ser informado.");
        }
        if (selecionado.getTipo().isAvaliacaoMaterial() && assistente.getAprovacaoMaterial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo aprovação material deve ser informado.");
        }
        ve.lancarException();
    }

    private void verificarRelacionamentosDependentes() {
        assistente.setRelacionamentosDependentes(Lists.<ExclusaoMateriaisVO>newArrayList());
        if (!selecionado.getTipo().isEntradaCompra()) {
            for (ExclusaoMateriaisVO rel : assistente.getMovimentos()) {
                if (!rel.getId().equals(selecionado.getIdMovimento()) && !rel.getSelecionado()) {
                    if (selecionado.getTipo().isSaidaConsumo()) {
                        if (rel.getTipoRelacionamento().isBensEstoqueSaida()) {
                            assistente.getRelacionamentosDependentes().add(rel);
                        }
                    } else if (selecionado.getTipo().isAvaliacaoMaterial()) {
                        if (rel.getTipoRelacionamento().isSaidaMaterial()) {
                            assistente.getRelacionamentosDependentes().add(rel);
                            assistente.setAvaliacaoPossuiSaida(true);
                        }
                    } else {
                        assistente.getRelacionamentosDependentes().add(rel);
                    }
                }
            }
        }
        if (selecionado.getTipo().isAvaliacaoMaterial() && assistente.getAvaliacaoPossuiSaida()) {
            for (ExclusaoMateriaisVO mov : assistente.getMovimentos()) {
                if (mov.getTipoRelacionamento().isBensEstoqueSaida() && !mov.getSelecionado()) {
                    assistente.getRelacionamentosDependentes().add(mov);
                }
                if (mov.getTipoRelacionamento().isRequisicaoMaterial() && !mov.getSelecionado()) {
                    assistente.getRelacionamentosDependentes().add(mov);
                }
            }
        }
    }

    public void atribuirRelacionamentosDependentesParaExclusao() {
        for (ExclusaoMateriaisVO rel : assistente.getMovimentos()) {
            for (ExclusaoMateriaisVO relDep : assistente.getRelacionamentosDependentes()) {
                if (rel.getId().equals(relDep.getId())) {
                    rel.setSelecionado(true);
                }
            }
        }
        ordenarRelacionamentos();
        cancelarRelacionamentosDependentes();
    }

    public void ordenarRelacionamentos() {
        Collections.sort(assistente.getMovimentos(), new Comparator<ExclusaoMateriaisVO>() {
            @Override
            public int compare(ExclusaoMateriaisVO o1, ExclusaoMateriaisVO o2) {
                Integer i1 = o1.getTipoRelacionamento().getOrderBy();
                Integer i2 = o2.getTipoRelacionamento().getOrderBy();
                return i1.compareTo(i2);
            }
        });
    }

    public void cancelarRelacionamentosDependentes() {
        assistente.setRelacionamentosDependentes(Lists.<ExclusaoMateriaisVO>newArrayList());
        FacesUtil.executaJavaScript("dlgRelacionamentos.hide()");
    }

    public List<EntradaCompraMaterial> completarEntradaMaterialPorCompra(String parte) {
        return facade.getEntradaMaterialFacade().buscarEntradaMaterialPorCompra(parte.trim());
    }

    public List<SaidaMaterial> completarSaidaMaterialPorConsumo(String parte) {
        return facade.getSaidaMaterialFacade().buscarSaidaMaterialPorTipoRequisicao(parte.trim(), TipoRequisicaoMaterial.CONSUMO);
    }

    public List<RequisicaoMaterial> completarRequisicaoMaterialPorConsumo(String parte) {
        return facade.getRequisicaoMaterialFacade().buscarRequisicaoMaterialPorTipo(parte.trim(), TipoRequisicaoMaterial.CONSUMO);
    }

    public List<AprovacaoMaterial> completarAprovacaoMaterial(String parte) {
        return facade.getAprovacaoMaterialFacade().buscarAprovacaoMaterial(parte.trim());
    }

    public void buscarRelacionamentos() {
        if (assistente.getSaidaMaterial() != null) {
            selecionado.setIdMovimento(assistente.getSaidaMaterial().getId());
        }
        if (assistente.getEntradaMaterial() != null) {
            selecionado.setIdMovimento(assistente.getEntradaMaterial().getId());
            assistente.setItens(facade.buscarItensEntrada(assistente.getEntradaMaterial().getId()));
        }
        if (assistente.getRequisicaoMaterial() != null) {
            selecionado.setIdMovimento(assistente.getRequisicaoMaterial().getId());
        }
        if (assistente.getAprovacaoMaterial() != null) {
            selecionado.setIdMovimento(assistente.getAprovacaoMaterial().getId());
        }
        assistente.setMovimentos(facade.buscarMovimentos(selecionado));
        selecionarMovimentoOrigem();
        definirAtributosAssistente();
        ordenarRelacionamentos();
        gerarHistorico();
    }

    private void selecionarMovimentoOrigem() {
        for (ExclusaoMateriaisVO rel : assistente.getMovimentos()) {
            if (rel.getId().equals(selecionado.getIdMovimento())) {
                rel.setSelecionado(true);
            }
        }
    }

    public void gerarHistorico() {
        StringBuilder historico = new StringBuilder();
        for (ExclusaoMateriaisVO rel : assistente.getMovimentos()) {
            if (rel.getSelecionado()) {
                historico.append("<b>").append(rel.getTipoRelacionamento().getDescricao().toUpperCase()).append(":</b> ").append(rel.getMovimento()).append(", <b>ID: </b> ").append(rel.getId()).append("<br/>");
            }
        }
        selecionado.setHistorico(historico.toString());
    }

    public boolean hasRelacionamentos() {
        return assistente.getMovimentos() != null && !assistente.getMovimentos().isEmpty();
    }

    public void limparDados() {
        selecionado.setHistorico(null);
        assistente.getMovimentos().clear();
        assistente.setSaidaMaterial(null);
        assistente.setEntradaMaterial(null);
        assistente.setRequisicaoMaterial(null);
        assistente.setAprovacaoMaterial(null);
        assistente.setMensagemAlerta("");
        assistente.setItens(Lists.newArrayList());
    }

    public List<SelectItem> getTiposExclusao() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(TipoExclusaoMaterial.SAIDA_CONSUMO, TipoExclusaoMaterial.SAIDA_CONSUMO.getDescricao()));
        toReturn.add(new SelectItem(TipoExclusaoMaterial.ENTRADA_COMPRA, TipoExclusaoMaterial.ENTRADA_COMPRA.getDescricao()));
        toReturn.add(new SelectItem(TipoExclusaoMaterial.REQUISICAO_MATERIAL_CONSUMO, TipoExclusaoMaterial.REQUISICAO_MATERIAL_CONSUMO.getDescricao()));
        toReturn.add(new SelectItem(TipoExclusaoMaterial.AVALIACAO_MATERIAL, TipoExclusaoMaterial.AVALIACAO_MATERIAL.getDescricao()));
        return toReturn;
    }

    public void exibirRevisao() {
        Long idRevtype = 0L;
        String classe = "";
        switch (selecionado.getTipo()) {
            case SAIDA_CONSUMO:
                idRevtype = facade.getSaidaMaterialFacade().recuperarIdRevisaoAuditoria(selecionado.getIdMovimento());
                classe = SaidaMaterial.class.getSimpleName();
                break;
            case ENTRADA_COMPRA:
                idRevtype = facade.getEntradaMaterialFacade().recuperarIdRevisaoAuditoria(selecionado.getIdMovimento());
                classe = EntradaMaterial.class.getSimpleName();
                break;
            case REQUISICAO_MATERIAL_CONSUMO:
                idRevtype = facade.getRequisicaoMaterialFacade().recuperarIdRevisaoAuditoria(selecionado.getIdMovimento());
                classe = RequisicaoMaterial.class.getSimpleName();
                break;
            case AVALIACAO_MATERIAL:
                idRevtype = facade.getAprovacaoMaterialFacade().recuperarIdRevisaoAuditoria(selecionado.getIdMovimento());
                classe = AprovacaoMaterial.class.getSimpleName();
                break;
            default:
                break;
        }
        Web.getSessionMap().put("pagina-anterior-auditoria-listar", getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        FacesUtil.redirecionamentoInterno("/auditoria/ver/" + idRevtype + "/" + selecionado.getIdMovimento() + "/" + classe);
    }

    public AssistenteExclusaoMateriais getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteExclusaoMateriais assistente) {
        this.assistente = assistente;
    }
}
