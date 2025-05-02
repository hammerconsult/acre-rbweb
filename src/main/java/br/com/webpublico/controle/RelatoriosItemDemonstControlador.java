/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.FormulaItemDemonstrativoVO;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.enums.TipoDespesaORC;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.RelatoriosItemDemonstFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.LogUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author juggernaut
 */
@ViewScoped
@ManagedBean(name = "relatoriosItemDemonstControlador")
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-itemdemonstrativo", pattern = "/relatorio-item-demonstrativo/novo/", viewId = "/faces/financeiro/planodecontas/relatorioitemdemonst/edita.xhtml"),
    @URLMapping(id = "editar-relatorio-itemdemonstrativo", pattern = "/relatorio-item-demonstrativo/editar/#{relatoriosItemDemonstControlador.id}/", viewId = "/faces/financeiro/planodecontas/relatorioitemdemonst/edita.xhtml"),
    @URLMapping(id = "config-relatorio", pattern = "/relatorio-item-demonstrativo/relatorio/#{relatoriosItemDemonstControlador.id}/configuracao/", viewId = "/faces/financeiro/planodecontas/relatorioitemdemonst/edita-config.xhtml"),
    @URLMapping(id = "ver-relatorio-itemdemonstrativo", pattern = "/relatorio-item-demonstrativo/ver/#{relatoriosItemDemonstControlador.id}/", viewId = "/faces/financeiro/planodecontas/relatorioitemdemonst/visualizar.xhtml"),
    @URLMapping(id = "listar-relatorio-itemdemonstrativo", pattern = "/relatorio-item-demonstrativo/listar/", viewId = "/faces/financeiro/planodecontas/relatorioitemdemonst/lista.xhtml"),
    @URLMapping(id = "exportar-importar-relatorio-itemdemonstrativo", pattern = "/relatorio-item-demonstrativo/exportar-importar/", viewId = "/faces/financeiro/planodecontas/relatorioitemdemonst/exportar-importar.xhtml")
})
public class RelatoriosItemDemonstControlador extends PrettyControlador<RelatoriosItemDemonst> implements Serializable, CRUD {

    private static final String MENSAGEM_ERRO_AO_REMOVER_REGISTRO = "Erro ao remover registro";

    private final Logger logger = LoggerFactory.getLogger(RelatoriosItemDemonstControlador.class);

    @EJB
    private RelatoriosItemDemonstFacade facade;
    private ItemDemonstrativo itemDemonstrativo;
    private List<ItemDemonstrativo> itensDemonstrativos;
    private Integer colunaFormula;
    private Boolean editar;
    private Integer grupo;
    private Integer ordem;
    private Integer coluna;
    private Integer espaco;
    private List<String> listaErrosTransporte;
    private Exercicio exercicioCopia;
    private ConverterAutoComplete converterItemDemonstrativo;
    private ConverterAutoComplete converterSubacaoPPA;
    private FormulaItemDemonstrativoVO formulaAdicao;
    private FormulaItemDemonstrativoVO formulaSubtracao;
    private HashMap<String, FormulaItemDemonstrativo> mapFormulaPorOperacaoEColuna;
    private ItemDemonstrativo itemDemonstrativoExAnterior;
    private Exercicio exercicioExportar;
    private Exercicio exercicioImportar;
    private StreamedContent file;
    private String logErrosImportar;
    private RelatoriosItemDemonst relatoriosItemDemonst;

    public RelatoriosItemDemonstControlador() {
        super(RelatoriosItemDemonst.class);
    }

    public List<SelectItem> getListaTipoRelatorioItemDemonstrativo() {
        return Util.getListSelectItem(TipoRelatorioItemDemonstrativo.values(), false);
    }

    public List<Exercicio> recuperarExerciciosSemRelatorio(String parte) {
        return facade.getExercicioFacade().buscarExerciciosFuturosSemRelatorio(parte.trim(), selecionado, selecionado.getExercicio());
    }

    public void transportarRelatorio() {
        try {
            listaErrosTransporte = new ArrayList<>();
            if (exercicioCopia == null) {
                FacesUtil.addCampoObrigatorio(" O campo Exercício deve ser informado.");
            } else {
                RelatoriosItemDemonst novoRelatorio = new RelatoriosItemDemonst();
                novoRelatorio = facade.duplicarRelatorioNovoExercicio(selecionado, novoRelatorio, exercicioCopia);
                facade.salvar(corrigirItens(novoRelatorio));
                RequestContext.getCurrentInstance().execute("dialogTransportar.hide()");
                FacesUtil.addOperacaoRealizada("Relatório " + selecionado.getDescricao() + " foi duplicado com sucesso para o Exercício de <b> " + exercicioCopia.getAno() + "</b>.");
                verificarExistenciaDeErrosDoTransporte();
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void verificarExistenciaDeErrosDoTransporte() {
        if (!listaErrosTransporte.isEmpty()) {
            for (int i = 0; i < listaErrosTransporte.size(); i++) {
                FacesUtil.addAtencao(listaErrosTransporte.get(i));
            }
        }
    }

    private RelatoriosItemDemonst corrigirItens(RelatoriosItemDemonst novoRelatorio) {
        List<ItemDemonstrativo> itensDemonstrativos = novoRelatorio.getItensDemonstrativos();
        for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
            itemDemonstrativo = facade.getItemDemonstrativoFacade().recuperar(itemDemonstrativo.getId());
            percorrerFormulasDoItem(itemDemonstrativo, novoRelatorio);
            Util.adicionarObjetoEmLista(novoRelatorio.getItensDemonstrativos(), itemDemonstrativo);
        }
        return novoRelatorio;
    }

    private void percorrerFormulasDoItem(ItemDemonstrativo item, RelatoriosItemDemonst novoRelatorio) {
        for (FormulaItemDemonstrativo formula : item.getFormulas()) {
            formula = facade.getFormulaItemDemonstrativoFacade().recuperar(formula.getId());
            List<ComponenteFormula> componentesParaRemover = Lists.newArrayList();
            List<ComponenteFormula> componentesParaAdicionar = Lists.newArrayList();
            for (ComponenteFormula cf : formula.getComponenteFormula()) {
                verificarComponenteFormula(cf, novoRelatorio, componentesParaRemover, componentesParaAdicionar);
            }
            for (ComponenteFormula cfr : componentesParaRemover) {
                formula.getComponenteFormula().remove(cfr);
            }
            for (ComponenteFormula cfa : componentesParaAdicionar) {
                formula.getComponenteFormula().add(cfa);
            }
            facade.getFormulaItemDemonstrativoFacade().salvar(formula);
        }
    }

    private void verificarComponenteFormula(ComponenteFormula cf, RelatoriosItemDemonst novoRelatorio, List<ComponenteFormula> componentesParaRemover, List<ComponenteFormula> componentesParaAdicionar) {
        if (cf instanceof ComponenteFormulaAcao) {
            ((ComponenteFormulaAcao) cf).setAcaoPPA(recuperarAcaoPPAPorExercicio(((ComponenteFormulaAcao) cf).getAcaoPPA(), exercicioCopia));
            if (((ComponenteFormulaAcao) cf).getAcaoPPA() == null) {
                componentesParaRemover.add(cf);
            }
        } else if (cf instanceof ComponenteFormulaConta) {
            List<Conta> contas = recuperarContaPorExercicio(((ComponenteFormulaConta) cf).getConta(), exercicioCopia);
            if (!contas.isEmpty()) {
                if (contas.size() == 1) {
                    ((ComponenteFormulaConta) cf).setConta(recuperarContaPorExercicio(((ComponenteFormulaConta) cf).getConta(), exercicioCopia).get(0));
                } else {
                    percorrerContas(cf, componentesParaAdicionar, contas);
                }
            } else {
                componentesParaRemover.add(cf);
            }
        } else if (cf instanceof ComponenteFormulaFonteRecurso) {
            ((ComponenteFormulaFonteRecurso) cf).setFonteDeRecursos(recuperarFonteDeRecursoPorExercicio(((ComponenteFormulaFonteRecurso) cf).getFonteDeRecursos(), exercicioCopia));
            if (((ComponenteFormulaFonteRecurso) cf).getFonteDeRecursos() == null) {
                componentesParaRemover.add(cf);
            }
        } else if (cf instanceof ComponenteFormulaItem) {
            ((ComponenteFormulaItem) cf).setItemDemonstrativo(recuperarItemDemonstrativo(novoRelatorio, ((ComponenteFormulaItem) cf).getItemDemonstrativo()));
            if (((ComponenteFormulaItem) cf).getItemDemonstrativo() == null) {
                componentesParaRemover.add(cf);
            }
        } else if (cf instanceof ComponenteFormulaPrograma) {
            ((ComponenteFormulaPrograma) cf).setProgramaPPA(recuperarProgramaPorExercicio(((ComponenteFormulaPrograma) cf).getProgramaPPA(), exercicioCopia));
            if (((ComponenteFormulaPrograma) cf).getProgramaPPA() == null) {
                componentesParaRemover.add(cf);
            }
        }
    }

    private void percorrerContas(ComponenteFormula cf, List<ComponenteFormula> componentesParaAdicionar, List<Conta> contas) {
        for (Conta conta : contas) {
            if (contas.indexOf(conta) == 0) {
                ((ComponenteFormulaConta) cf).setConta(conta);
            } else {
                duplicarComponenteFormulaConta(cf, componentesParaAdicionar, conta);
            }
        }
    }

    private void duplicarComponenteFormulaConta(ComponenteFormula cf, List<ComponenteFormula> componentesParaAdicionar, Conta conta) {
        ComponenteFormulaConta componenteFormulaConta = (ComponenteFormulaConta) Util.clonarObjeto(cf);
        componenteFormulaConta.setId(null);
        componenteFormulaConta.setConta(conta);
        componentesParaAdicionar.add(componenteFormulaConta);
    }

    private AcaoPPA recuperarAcaoPPAPorExercicio(AcaoPPA acaoPPA, Exercicio exercicio) {
        AcaoPPA acaoRetorno = facade.getFormulaItemDemonstrativoFacade().getProjetoAtividadeFacade().buscarAcaoPPAPeloCodigoEDescricaoEExercicio(acaoPPA.getCodigo(), acaoPPA.getDescricao(), exercicio);
        if (acaoRetorno == null) {
            listaErrosTransporte.add("A Ação " + acaoPPA.getCodigo() + " - " + acaoPPA.getDescricao() + " não foi encontrada no exercício " + exercicio.getAno());
        }
        return acaoRetorno;
    }

    private ItemDemonstrativo recuperarItemDemonstrativo(RelatoriosItemDemonst relatoriosItemDemonst, ItemDemonstrativo itemDemonstrativo) {
        ItemDemonstrativo itemRetorno = null;
        for (ItemDemonstrativo item : relatoriosItemDemonst.getItensDemonstrativos()) {
            if (item.getNome().equals(itemDemonstrativo.getNome()) && item.getDescricao().equals(itemDemonstrativo.getDescricao())) {
                itemRetorno = item;
                break;
            }
        }
        if (itemRetorno == null) {
            listaErrosTransporte.add("O Item Demonstrativo " + itemDemonstrativo.getDescricao() + " não foi encontrado");
        }
        return itemRetorno;
    }

    private FonteDeRecursos recuperarFonteDeRecursoPorExercicio(FonteDeRecursos fonteDeRecursos, Exercicio exercicio) {
        FonteDeRecursos fonteRetorno = facade.getFormulaItemDemonstrativoFacade().getFonteDeRecursosFacade().recuperaPorCodigoExercicio(fonteDeRecursos, exercicio);
        if (fonteRetorno == null) {
            listaErrosTransporte.add("A Fonte de Recurso " + fonteDeRecursos.getCodigo() + " - " + fonteDeRecursos.getDescricao() + " não foi encontrada no exercício " + exercicio.getAno());
        }
        return fonteRetorno;
    }

    private List<Conta> recuperarContaPorExercicio(Conta conta, Exercicio exercicio) {
        List<Conta> contaRetorno = facade.getFormulaItemDemonstrativoFacade().getContaFacade().buscarContasEquivalentesPorIdOrigemAndExercicioAndDType(conta, exercicio);
        if (contaRetorno == null || contaRetorno.isEmpty()) {
            listaErrosTransporte.add("A Conta " + conta.getCodigo() + " - " + conta.getDescricao() + " não foi encontrada no exercício " + exercicio.getAno());
        }
        return contaRetorno;
    }

    private ProgramaPPA recuperarProgramaPorExercicio(ProgramaPPA programaPPA, Exercicio exercicio) {
        ProgramaPPA programaRetorno = facade.getFormulaItemDemonstrativoFacade().getProgramaPPAFacade().buscarProgramasPorExercicio(programaPPA.getCodigo(), programaPPA.getDenominacao(), exercicio);
        if (programaRetorno == null) {
            listaErrosTransporte.add("O Programa " + programaPPA.getCodigo() + " - " + programaPPA.getDenominacao() + " não foi encontrado no exercício " + exercicio.getAno());
        }
        return programaRetorno;
    }

    public List<ItemDemonstrativo> buscarConfiguracoesPorDescricaoOrNome(String descricao) {
        return facade.getItemDemonstrativoFacade().buscarItensDemonstrativosPorDescricaoOrNome(selecionado, descricao);
    }

    public void cancelarTransporte() {
        RequestContext.getCurrentInstance().execute("dialogTransportar.hide()");
    }

    public void abrirDialogTransporte() {
        RequestContext.getCurrentInstance().execute("dialogTransportar.show()");
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/relatorio-item-demonstrativo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    private void validarItemDemonstrativo() {
        ValidacaoException ve = new ValidacaoException();
        if (itemDemonstrativo.getDescricao().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Descrição deve ser informado.");
        }
        if (itemDemonstrativo.getNome().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Nome deve ser informado.");
        }
        if (ordem == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ordem deve ser informado.");
        } else if (ordem <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Ordem deve ser maior que zero(0).");
        }
        if (grupo == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Grupo deve ser informado.");
        } else if (grupo <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Grupo deve ser maior que zero(0).");
        } else if (selecionado.getGrupos() < grupo) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Grupo informado excede a quantidade de grupos definido para este relatório, composto por " + selecionado.getGrupos() + " grupo(s).");
        }
        if (coluna == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Coluna deve ser informado.");
        } else if (coluna < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Coluna deve ser maior ou igual a zero(0).");
        }
        if (espaco == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Espaço deve ser informado.");
        } else if (espaco < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Espaço deve ser maior ou igual a zero(0).");
        }
        ve.lancarException();
        if (facade.getItemDemonstrativoFacade().hasItemDemonstrativoPorRelatorioOrdemGrupo(selecionado, ordem, grupo, itemDemonstrativo)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um Item Demonstrativo com esta Ordem e Grupo.");
        }
        if (facade.getItemDemonstrativoFacade().hasItemPorRelatorioAndDescricao(selecionado, itemDemonstrativo)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um Item Demonstrativo com esta descrição.");
        }
        ve.lancarException();
    }

    private boolean hasItemNaOrdemAndGrupo(ItemDemonstrativo it) {
        if (facade.getItemDemonstrativoFacade().hasItemDemonstrativoPorRelatorioOrdemGrupo(selecionado, it.getOrdem(), it.getGrupo(), it)) {
            FacesUtil.addOperacaoNaoPermitida("Já existe um Item Demonstrativo com esta Ordem e Grupo.");
            return true;
        }
        return false;
    }

    private void popularItemDemonstrativo() {
        itemDemonstrativo.setRelatoriosItemDemonst(selecionado);
        itemDemonstrativo.setGrupo(grupo);
        itemDemonstrativo.setOrdem(ordem);
        itemDemonstrativo.setColuna(coluna);
        itemDemonstrativo.setEspaco(espaco);
    }

    public void adicionarItemDemonstrativo() {
        try {
            validarItemDemonstrativo();
            popularItemDemonstrativo();
            if (itemDemonstrativoExAnterior != null) {
                itemDemonstrativo.setItemExercicioAnterior(itemDemonstrativoExAnterior);
            } else {
                itemDemonstrativo.setItemExercicioAnterior(null);
            }
            if (!editar) {
                ordem = ordem + 1;
            } else {
                ordem = null;
                grupo = null;
            }
            itemDemonstrativo = facade.getItemDemonstrativoFacade().salvarRetornando(itemDemonstrativo);
            Util.adicionarObjetoEmLista(itensDemonstrativos, itemDemonstrativo);
            itemDemonstrativo = new ItemDemonstrativo();
            coluna = 0;
            espaco = 0;
            editar = false;
            itemDemonstrativoExAnterior = null;
            ordenarItens();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Detalhes do erro: " + e.getMessage());
        }

    }

    private void ordenarItens() {
        Collections.sort(itensDemonstrativos, new Comparator() {
            public int compare(Object o1, Object o2) {
                ItemDemonstrativo s1 = (ItemDemonstrativo) o1;
                ItemDemonstrativo s2 = (ItemDemonstrativo) o2;
                if (s1.getOrdem() != null && s2.getOrdem() != null) {
                    return s1.getOrdem().compareTo(s2.getOrdem());
                } else {
                    return 0;
                }
            }
        });
        Collections.sort(itensDemonstrativos, new Comparator() {
            public int compare(Object o1, Object o2) {
                ItemDemonstrativo s1 = (ItemDemonstrativo) o1;
                ItemDemonstrativo s2 = (ItemDemonstrativo) o2;

                if (s1.getGrupo() != null && s2.getGrupo() != null) {
                    return s1.getGrupo().compareTo(s2.getGrupo());
                } else {
                    return 0;
                }
            }
        });
    }

    public void editarItem(ItemDemonstrativo item) {
        itemDemonstrativo = (ItemDemonstrativo) Util.clonarObjeto(item);
        ordem = item.getOrdem();
        grupo = item.getGrupo();
        coluna = item.getColuna();
        espaco = item.getEspaco();
        editar = true;
        if (itemDemonstrativo.getItemExercicioAnterior() != null) {
            itemDemonstrativoExAnterior = facade.getItemDemonstrativoFacade().recuperar(itemDemonstrativo.getItemExercicioAnterior().getId());
        } else {
            itemDemonstrativoExAnterior = null;
        }
    }

    public void removerItem(ItemDemonstrativo item) {
        try {
            facade.removerComponentesFormulaComItemDemonstrativo(item);
            facade.getItemDemonstrativoFacade().remover(item);
            itensDemonstrativos.remove(item);
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoPermitida("Não foi possível remover, pois existem configurações vinculadas a este item. " + ex.getMessage());
        }
    }

    public void descerOrdem(ItemDemonstrativo item) {
        for (ItemDemonstrativo it : itensDemonstrativos) {
            if (it.equals(item)) {
                if ((it.getOrdem() - 1) > 0) {
                    it.setOrdem(it.getOrdem() - 1);
                    if (!hasItemNaOrdemAndGrupo(it)) {
                        facade.getItemDemonstrativoFacade().salvar(it);
                    } else {
                        it.setOrdem(it.getOrdem() + 1);
                    }
                } else {
                    FacesUtil.addOperacaoNaoPermitida("O campo Grupo deve ser maior que zero(0).");
                }
            }
        }
    }

    public void subirOrdem(ItemDemonstrativo item) {
        for (ItemDemonstrativo it : itensDemonstrativos) {
            if (it.equals(item)) {
                aumentarOrdemProximosItens(it.getOrdem() + 1, it.getGrupo());
                it.setOrdem(it.getOrdem() + 1);
                if (!hasItemNaOrdemAndGrupo(it)) {
                    facade.getItemDemonstrativoFacade().salvar(it);
                } else {
                    it.setOrdem(it.getOrdem() - 1);
                }
            }
        }
    }

    private void aumentarOrdemProximosItens(Integer numeroOrdem, Integer grupo) {
        for (ItemDemonstrativo item : itensDemonstrativos) {
            if (item.getOrdem() >= numeroOrdem && grupo.equals(item.getGrupo())) {
                item.setOrdem(item.getOrdem() + 1);
                facade.getItemDemonstrativoFacade().salvar(item);
            }
        }
    }

    @URLAction(mappingId = "config-relatorio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void recuperaConfig() {
        selecionado = facade.recuperar(super.getId());
        itemDemonstrativo = new ItemDemonstrativo();
        coluna = 0;
        espaco = 0;
        itensDemonstrativos = facade.getItemDemonstrativoFacade().buscarItensDemonstrativosPorRelatorio(selecionado);
        editar = false;
        ordenarItens();
    }

    public void redirecionarConfigItens() {
        try {
            Util.validarCampos(selecionado);
            if (isOperacaoNovo()) {
                this.getFacede().salvarNovo(selecionado);
            } else {
                this.getFacede().salvar(selecionado);
            }
            if (selecionado.getId() != null) {
                String url = this.getCaminhoPadrao() + "relatorio/" + selecionado.getId() + "/configuracao/";
                FacesUtil.redirecionamentoInterno(url);
            } else {
                throw new ValidacaoException("O Relatório precisar ser salvo para poder editar as configurações dos itens.");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarColunaFormula() {
        ValidacaoException ve = new ValidacaoException();
        if (colunaFormula != null && colunaFormula <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Coluna deve ser maior que 0(zero).");
        }
        ve.lancarException();
    }

    @URLAction(mappingId = "novo-relatorio-itemdemonstrativo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        itemDemonstrativo = new ItemDemonstrativo();
        itensDemonstrativos = Lists.newArrayList();
        editar = false;
    }

    @URLAction(mappingId = "ver-relatorio-itemdemonstrativo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "editar-relatorio-itemdemonstrativo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "exportar-importar-relatorio-itemdemonstrativo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void exportarImportar() {
        exercicioExportar = facade.getSistemaFacade().getExercicioCorrente();
        exercicioImportar = facade.getSistemaFacade().getExercicioCorrente();
        logErrosImportar = "";
        limparRelatoriosItemDemonst();
    }

    public void limparRelatoriosItemDemonst() {
        relatoriosItemDemonst = null;
    }

    public void voltarRedirecionarRelatorio() {
        if (selecionado.getId() == null) {
            Web.poeNaSessao(selecionado);
            FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "novo/");
        } else {
            FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
        }
    }

    private void recuperaEditarVer() {
        itemDemonstrativo = new ItemDemonstrativo();
        itensDemonstrativos = facade.getItemDemonstrativoFacade().buscarItensDemonstrativosPorRelatorio(selecionado);
    }

    public ItemDemonstrativo getItemDemonstrativo() {
        return itemDemonstrativo;
    }

    public void setItemDemonstrativo(ItemDemonstrativo itemDemonstrativo) {
        this.itemDemonstrativo = itemDemonstrativo;
    }

    public Integer getGrupo() {
        return grupo;
    }

    public void setGrupo(Integer grupo) {
        this.grupo = grupo;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public ConverterAutoComplete getConverterSubacaoPPA() {
        if (converterSubacaoPPA == null) {
            converterSubacaoPPA = new ConverterAutoComplete(SubAcaoPPA.class, facade.getFormulaItemDemonstrativoFacade().getSubProjetoAtividadeFacade());
        }
        return converterSubacaoPPA;
    }

    public List<SelectItem> getTipoDeDespesa() {
        return Util.getListSelectItem(TipoDespesaORC.values(), false);
    }

    public void salvarFormulas() {
        try {
            for (Map.Entry<String, FormulaItemDemonstrativo> map : mapFormulaPorOperacaoEColuna.entrySet()) {
                if (map.getValue().getComponenteFormula() != null && !map.getValue().getComponenteFormula().isEmpty()) {
                    if (map.getValue().getId() == null) {
                        facade.getFormulaItemDemonstrativoFacade().salvarNovo(map.getValue());
                    } else {
                        facade.getFormulaItemDemonstrativoFacade().salvar(map.getValue());
                    }
                } else if (map.getValue().getId() != null) {
                    facade.getFormulaItemDemonstrativoFacade().remover(map.getValue());
                }
            }
            limparFormulasVOs();
            itemDemonstrativo = new ItemDemonstrativo();
            RequestContext.getCurrentInstance().execute("dialogFormula.hide()");
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
        }
    }

    public void adicionarItem(FormulaItemDemonstrativoVO formulaVO) {
        try {
            validarColunaFormula();
            validarAdicionarItemDemonstrativo(formulaVO);
            adicionarNovaFormulaMapPorOperacaoEColuna(formulaVO);
            ComponenteFormulaItem componenteFormulaItem = new ComponenteFormulaItem();
            componenteFormulaItem.setFormulaItemDemonstrativo(mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()));
            componenteFormulaItem.setItemDemonstrativo(formulaVO.getItemDemonst());
            componenteFormulaItem.setDataRegistro(facade.getSistemaFacade().getDataOperacao());
            componenteFormulaItem.setColuna(getColunaFormula());
            mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()).getComponenteFormula().add(componenteFormulaItem);
            formulaVO.getComponentesItemDemonstrativo().add(componenteFormulaItem);
            formulaVO.setItemDemonst(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarAdicionarItemDemonstrativo(FormulaItemDemonstrativoVO formulaVO) {
        ValidacaoException ve = new ValidacaoException();
        if (formulaVO.getItemDemonst() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Item Demonstrativo deve ser informado.");
        }
        ve.lancarException();
        for (ComponenteFormulaItem cfi : formulaVO.getComponentesItemDemonstrativo()) {
            if (formulaVO.getItemDemonst().equals(cfi.getItemDemonstrativo())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Item Demonstrativo selecionado já está adicionada!");
            }
        }
        ve.lancarException();
    }

    public void adicionarPrograma(FormulaItemDemonstrativoVO formulaVO) {
        try {
            validarColunaFormula();
            validarAdicionarPrograma(formulaVO);
            adicionarNovaFormulaMapPorOperacaoEColuna(formulaVO);
            ComponenteFormulaPrograma componenteFormulaPrograma = new ComponenteFormulaPrograma();
            componenteFormulaPrograma.setFormulaItemDemonstrativo(mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()));
            componenteFormulaPrograma.setProgramaPPA(formulaVO.getProgramaPPA());
            componenteFormulaPrograma.setDataRegistro(facade.getSistemaFacade().getDataOperacao());
            componenteFormulaPrograma.setColuna(getColunaFormula());
            mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()).getComponenteFormula().add(componenteFormulaPrograma);
            formulaVO.getComponentesPrograma().add(componenteFormulaPrograma);
            formulaVO.setProgramaPPA(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarAdicionarPrograma(FormulaItemDemonstrativoVO formulaVO) {
        ValidacaoException ve = new ValidacaoException();
        if (formulaVO.getProgramaPPA() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Programa deve ser informado.");
        }
        ve.lancarException();
        for (ComponenteFormulaPrograma cfp : formulaVO.getComponentesPrograma()) {
            if (formulaVO.getProgramaPPA().equals(cfp.getProgramaPPA())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Programa selecionado já está adicionado!");
            }
        }
        ve.lancarException();
    }

    public void adicionarAcao(FormulaItemDemonstrativoVO formulaVO) {
        try {
            validarColunaFormula();
            validarAdicionarAcao(formulaVO);
            adicionarNovaFormulaMapPorOperacaoEColuna(formulaVO);
            ComponenteFormulaAcao componenteFormulaAcao = new ComponenteFormulaAcao();
            componenteFormulaAcao.setFormulaItemDemonstrativo(mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()));
            componenteFormulaAcao.setAcaoPPA(formulaVO.getAcaoPPA());
            componenteFormulaAcao.setDataRegistro(facade.getSistemaFacade().getDataOperacao());
            componenteFormulaAcao.setColuna(getColunaFormula());
            mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()).getComponenteFormula().add(componenteFormulaAcao);
            formulaVO.getComponentesAcao().add(componenteFormulaAcao);
            formulaVO.setAcaoPPA(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarAdicionarAcao(FormulaItemDemonstrativoVO formulaVO) {
        ValidacaoException ve = new ValidacaoException();
        if (formulaVO.getAcaoPPA() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ação deve ser informado.");
        }
        ve.lancarException();
        for (ComponenteFormulaAcao cfa : formulaVO.getComponentesAcao()) {
            if (formulaVO.getAcaoPPA().equals(cfa.getAcaoPPA())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Ação selecionada já está adicionada!");
            }
        }
        ve.lancarException();
    }

    public void adicionarSubacao(FormulaItemDemonstrativoVO formulaVO) {
        try {
            validarColunaFormula();
            validarSubacao(formulaVO);
            adicionarNovaFormulaMapPorOperacaoEColuna(formulaVO);
            ComponenteFormulaSubacao componenteFormulaSubacao = new ComponenteFormulaSubacao();
            componenteFormulaSubacao.setFormulaItemDemonstrativo(mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()));
            componenteFormulaSubacao.setSubAcaoPPA(formulaVO.getSubAcaoPPA());
            componenteFormulaSubacao.setDataRegistro(facade.getSistemaFacade().getDataOperacao());
            componenteFormulaSubacao.setColuna(getColunaFormula());
            mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()).getComponenteFormula().add(componenteFormulaSubacao);
            formulaVO.getComponentesSubacao().add(componenteFormulaSubacao);
            formulaVO.setSubAcaoPPA(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarSubacao(FormulaItemDemonstrativoVO formulaVO) {
        ValidacaoException ve = new ValidacaoException();
        if (formulaVO.getSubAcaoPPA() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Subação deve ser informado.");
        }
        ve.lancarException();
        for (ComponenteFormulaSubacao cfa : formulaVO.getComponentesSubacao()) {
            if (formulaVO.getSubAcaoPPA().equals(cfa.getSubAcaoPPA())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Subação selecionada já está adicionada!");
            }
        }
        ve.lancarException();
    }

    public void adicionarFuncao(FormulaItemDemonstrativoVO formulaVO) {
        try {
            validarColunaFormula();
            validarAdicionarFuncao(formulaVO);
            adicionarNovaFormulaMapPorOperacaoEColuna(formulaVO);
            ComponenteFormulaFuncao componenteFormulaFuncao = new ComponenteFormulaFuncao();
            componenteFormulaFuncao.setFormulaItemDemonstrativo(mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()));
            componenteFormulaFuncao.setFuncao(formulaVO.getFuncao());
            componenteFormulaFuncao.setDataRegistro(facade.getSistemaFacade().getDataOperacao());
            componenteFormulaFuncao.setColuna(getColunaFormula());
            mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()).getComponenteFormula().add(componenteFormulaFuncao);
            formulaVO.getComponentesFuncao().add(componenteFormulaFuncao);
            formulaVO.setFuncao(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarAdicionarFuncao(FormulaItemDemonstrativoVO formulaVO) {
        ValidacaoException ve = new ValidacaoException();
        if (formulaVO.getFuncao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Função deve ser informado.");
        }
        ve.lancarException();
        for (ComponenteFormulaFuncao cff : formulaVO.getComponentesFuncao()) {
            if (formulaVO.getFuncao().equals(cff.getFuncao())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Função selecionada já está adicionada!");
            }
        }
        ve.lancarException();
    }

    public void adicionarFonteDeRecurso(FormulaItemDemonstrativoVO formulaVO) {
        try {
            validarColunaFormula();
            validarAdicionarFonteDeRecurso(formulaVO);
            adicionarNovaFormulaMapPorOperacaoEColuna(formulaVO);
            ComponenteFormulaFonteRecurso componenteFormulaFonteRecurso = new ComponenteFormulaFonteRecurso();
            componenteFormulaFonteRecurso.setFormulaItemDemonstrativo(mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()));
            componenteFormulaFonteRecurso.setFonteDeRecursos(formulaVO.getFonteDeRecursos());
            componenteFormulaFonteRecurso.setDataRegistro(facade.getSistemaFacade().getDataOperacao());
            componenteFormulaFonteRecurso.setColuna(getColunaFormula());
            mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()).getComponenteFormula().add(componenteFormulaFonteRecurso);
            formulaVO.getComponentesFonteRecurso().add(componenteFormulaFonteRecurso);
            formulaVO.setFonteDeRecursos(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarAdicionarFonteDeRecurso(FormulaItemDemonstrativoVO formulaVO) {
        ValidacaoException ve = new ValidacaoException();
        if (formulaVO.getFonteDeRecursos() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Fonte de Recurso deve ser informado.");
        }
        ve.lancarException();
        for (ComponenteFormulaFonteRecurso cffr : formulaVO.getComponentesFonteRecurso()) {
            if (formulaVO.getFonteDeRecursos().equals(cffr.getFonteDeRecursos())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Fonte de Recurso selecionada já está adicionada!");
            }
        }
        ve.lancarException();
    }

    public void adicionarUnidadeOrganizacional(FormulaItemDemonstrativoVO formulaVO) {
        try {
            validarColunaFormula();
            validarAdicionarUnidadeOrganizacional(formulaVO);
            adicionarNovaFormulaMapPorOperacaoEColuna(formulaVO);
            ComponenteFormulaUnidadeOrganizacional componenteFormulaUnidadeOrganizacional = new ComponenteFormulaUnidadeOrganizacional();
            componenteFormulaUnidadeOrganizacional.setFormulaItemDemonstrativo(mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()));
            componenteFormulaUnidadeOrganizacional.setUnidadeOrganizacional(formulaVO.getHierarquiaOrganizacional().getSubordinada());
            componenteFormulaUnidadeOrganizacional.setDataRegistro(facade.getSistemaFacade().getDataOperacao());
            componenteFormulaUnidadeOrganizacional.setColuna(getColunaFormula());
            mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()).getComponenteFormula().add(componenteFormulaUnidadeOrganizacional);
            formulaVO.getComponentesUnidadeOrganizacional().add(componenteFormulaUnidadeOrganizacional);
            formulaVO.setHierarquiaOrganizacional(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarAdicionarUnidadeOrganizacional(FormulaItemDemonstrativoVO vo) {
        ValidacaoException ve = new ValidacaoException();
        if (vo.getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Organizacional deve ser informado.");
        }
        ve.lancarException();
        for (ComponenteFormulaUnidadeOrganizacional cfuo : vo.getComponentesUnidadeOrganizacional()) {
            if (vo.getHierarquiaOrganizacional().getSubordinada().equals(cfuo.getUnidadeOrganizacional())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Unidade Organizacional selecionada já está adicionada!");
            }
        }
        ve.lancarException();
    }

    public void adicionarSubFuncao(FormulaItemDemonstrativoVO formulaVO) {
        try {
            validarColunaFormula();
            validarAdicionarSubFuncao(formulaVO);
            adicionarNovaFormulaMapPorOperacaoEColuna(formulaVO);
            ComponenteFormulaSubFuncao componenteFormulaSubFuncao = new ComponenteFormulaSubFuncao();
            componenteFormulaSubFuncao.setFormulaItemDemonstrativo(mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()));
            componenteFormulaSubFuncao.setSubFuncao(formulaVO.getSubFuncao());
            componenteFormulaSubFuncao.setDataRegistro(facade.getSistemaFacade().getDataOperacao());
            componenteFormulaSubFuncao.setColuna(getColunaFormula());
            mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()).getComponenteFormula().add(componenteFormulaSubFuncao);
            formulaVO.getComponentesSubFuncao().add(componenteFormulaSubFuncao);
            formulaVO.setSubFuncao(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarAdicionarSubFuncao(FormulaItemDemonstrativoVO formulaVO) {
        ValidacaoException ve = new ValidacaoException();
        if (formulaVO.getSubFuncao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Subfunção deve ser informado.");
        }
        ve.lancarException();
        for (ComponenteFormulaSubFuncao cfsf : formulaVO.getComponentesSubFuncao()) {
            if (formulaVO.getSubFuncao().equals(cfsf.getSubFuncao())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Subfunção selecionada já está adicionada!");
            }
        }
        ve.lancarException();
    }

    public void adicionarSubConta(FormulaItemDemonstrativoVO formulaVO) {
        try {
            validarColunaFormula();
            validarSubConta(formulaVO);
            adicionarNovaFormulaMapPorOperacaoEColuna(formulaVO);
            ComponenteFormulaSubConta componenteFormulaSubConta = new ComponenteFormulaSubConta();
            componenteFormulaSubConta.setFormulaItemDemonstrativo(mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()));
            componenteFormulaSubConta.setSubConta(formulaVO.getSubConta());
            componenteFormulaSubConta.setDataRegistro(facade.getSistemaFacade().getDataOperacao());
            componenteFormulaSubConta.setColuna(getColunaFormula());
            mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()).getComponenteFormula().add(componenteFormulaSubConta);
            formulaVO.getComponentesSubConta().add(componenteFormulaSubConta);
            formulaVO.setSubConta(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarSubConta(FormulaItemDemonstrativoVO formulaVO) {
        ValidacaoException ve = new ValidacaoException();
        if (formulaVO.getSubConta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta Financeira deve ser informado.");
        }
        ve.lancarException();
        for (ComponenteFormulaSubConta compSubConta : formulaVO.getComponentesSubConta()) {
            if (formulaVO.getSubConta().equals(compSubConta.getSubConta())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Conta Financeira selecionada já está adicionada!");
            }
        }
        ve.lancarException();
    }

    public void adicionarTipoDespesa(FormulaItemDemonstrativoVO formulaVO) {
        try {
            validarColunaFormula();
            validarAdicionarTipoDespesa(formulaVO);
            adicionarNovaFormulaMapPorOperacaoEColuna(formulaVO);
            ComponenteFormulaTipoDesp componenteFormulaTipoDesp = new ComponenteFormulaTipoDesp();
            componenteFormulaTipoDesp.setFormulaItemDemonstrativo(mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()));
            componenteFormulaTipoDesp.setTipoDespesaORC(formulaVO.getTipoDespesaORC());
            componenteFormulaTipoDesp.setDataRegistro(facade.getSistemaFacade().getDataOperacao());
            componenteFormulaTipoDesp.setColuna(getColunaFormula());
            mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()).getComponenteFormula().add(componenteFormulaTipoDesp);
            formulaVO.getComponentesTipoDespesa().add(componenteFormulaTipoDesp);
            formulaVO.setTipoDespesaORC(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarAdicionarTipoDespesa(FormulaItemDemonstrativoVO formulaVO) {
        ValidacaoException ve = new ValidacaoException();
        if (formulaVO.getSubConta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Despesa deve ser informado.");
        }
        ve.lancarException();
        for (ComponenteFormulaTipoDesp cftd : formulaVO.getComponentesTipoDespesa()) {
            if (formulaVO.getTipoDespesaORC().equals(cftd.getTipoDespesaORC())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Tipo de Despesa selecionado já está adicionado!");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getPlanos() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (PlanoDeContas object : facade.getFormulaItemDemonstrativoFacade().getPlanoDeContasFacade().recuperaPlanosPorExercicio(selecionado.getExercicio())) {
            retorno.add(new SelectItem(object, object.getDescricao()));
        }
        return retorno;
    }

    public void adicionarConta(FormulaItemDemonstrativoVO formulaVO) {
        try {
            validarColunaFormula();
            validarAdicionarConta(formulaVO);
            adicionarNovaFormulaMapPorOperacaoEColuna(formulaVO);
            mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()).setPlanoDeContas(formulaVO.getPlanoDeContas());
            ComponenteFormulaConta componenteFormulaConta = new ComponenteFormulaConta();
            componenteFormulaConta.setFormulaItemDemonstrativo(mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()));
            componenteFormulaConta.setConta(formulaVO.getConta());
            componenteFormulaConta.setDataRegistro(facade.getSistemaFacade().getDataOperacao());
            componenteFormulaConta.setColuna(getColunaFormula());
            mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()).getComponenteFormula().add(componenteFormulaConta);
            formulaVO.getComponentesConta().add(componenteFormulaConta);
            formulaVO.setConta(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarAdicionarConta(FormulaItemDemonstrativoVO formulaVO) {
        ValidacaoException ve = new ValidacaoException();
        if (formulaVO.getPlanoDeContas() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Plano de Contas deve ser informado.");
        }
        if (formulaVO.getConta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta deve ser informado.");
        }
        ve.lancarException();
        for (ComponenteFormulaConta c : formulaVO.getComponentesConta()) {
            if (formulaVO.getConta().equals(c.getConta())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Conta selecionada já está adicionada!");
            }
        }
        ve.lancarException();
    }

    public void atribuirNullConta(FormulaItemDemonstrativoVO vo) {
        vo.setConta(null);
    }

    public void adicionarValorPadrao(FormulaItemDemonstrativoVO formulaVO) {
        try {
            validarColunaFormula();
            validarAdicionarValorPadrao(formulaVO);
            adicionarNovaFormulaMapPorOperacaoEColuna(formulaVO);
            ComponenteFormulaValorPadrao componenteFormulaValorPadrao = new ComponenteFormulaValorPadrao();
            componenteFormulaValorPadrao.setFormulaItemDemonstrativo(mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()));
            componenteFormulaValorPadrao.setMes(formulaVO.getMes());
            componenteFormulaValorPadrao.setValorPadrao(formulaVO.getValorPadrao());
            componenteFormulaValorPadrao.setDataRegistro(facade.getSistemaFacade().getDataOperacao());
            componenteFormulaValorPadrao.setColuna(getColunaFormula());
            mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + getColunaFormulaAsString()).getComponenteFormula().add(componenteFormulaValorPadrao);
            formulaVO.getComponentesValorPadrao().add(componenteFormulaValorPadrao);
            formulaVO.setValorPadrao(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarAdicionarValorPadrao(FormulaItemDemonstrativoVO formulaVO) {
        ValidacaoException ve = new ValidacaoException();
        if (formulaVO.getValorPadrao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Valor Padrão deve ser informado.");
        }
        ve.lancarException();
        for (ComponenteFormulaValorPadrao c : formulaVO.getComponentesValorPadrao()) {
            if (formulaVO.getMes() == null && c.getMes() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Previsão Atualizada já foi adicionada.");
            }
            if (formulaVO.getMes() != null && formulaVO.getMes().equals(c.getMes())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Mês selecionado já foi adicionado.");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getMesesEPrevisao() {
        List<SelectItem> retorno = Lists.newArrayList(new SelectItem(null, "Previsão Atualizada"));
        retorno.addAll(Util.getListSelectItemSemCampoVazio(Mes.values(), false));
        return retorno;
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String parte) {
        return facade.getFormulaItemDemonstrativoFacade().getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte.trim(), selecionado.getExercicio());
    }

    public void setaNullItem(FormulaItemDemonstrativoVO formula) {
        formula.setItemDemonst(null);
    }

    public List<ItemDemonstrativo> completarItensAdicao(String parte) {
        if (formulaAdicao != null && formulaAdicao.getRelatoriosItemDemonst() != null) {
            return facade.getFormulaItemDemonstrativoFacade().getItemDemonstrativoFacade().buscarItensPorRelatorio(parte.trim(), formulaAdicao.getRelatoriosItemDemonst());
        }
        return facade.getFormulaItemDemonstrativoFacade().getItemDemonstrativoFacade().buscarItensPorRelatorio(parte.trim(), selecionado);
    }

    public List<ItemDemonstrativo> completarItensSubtracao(String parte) {
        if (formulaSubtracao != null && formulaSubtracao.getRelatoriosItemDemonst() != null) {
            return facade.getFormulaItemDemonstrativoFacade().getItemDemonstrativoFacade().buscarItensPorRelatorio(parte.trim(), formulaSubtracao.getRelatoriosItemDemonst());
        }
        return facade.getFormulaItemDemonstrativoFacade().getItemDemonstrativoFacade().buscarItensPorRelatorio(parte.trim(), selecionado);
    }

    public List<ProgramaPPA> completaPrograma(String parte) {
        return facade.getFormulaItemDemonstrativoFacade().getProgramaPPAFacade().listaFiltrandoProgramasPorExercicio(parte.trim(), selecionado.getExercicio());
    }

    public List<AcaoPPA> completarAcoes(String parte) {
        return facade.getFormulaItemDemonstrativoFacade().getProjetoAtividadeFacade().buscarAcoesPPAPorExercicio(parte.trim(), selecionado.getExercicio());
    }

    public List<SubAcaoPPA> completarSubacoesAdicao(String parte) {
        List<SubAcaoPPA> retorno = Lists.newArrayList();
        if (!formulaAdicao.getComponentesAcao().isEmpty()) {
            for (ComponenteFormulaAcao componenteFormulaAcao : formulaAdicao.getComponentesAcao()) {
                retorno.addAll(facade.getFormulaItemDemonstrativoFacade().getSubProjetoAtividadeFacade().buscarSubPorProjetoAtividades(parte.trim(), componenteFormulaAcao.getAcaoPPA()));
            }
        } else {
            FacesUtil.addAtencao("Selecione uma ou mais Ações para buscar as Subações.");
        }
        return retorno;
    }

    public List<SubAcaoPPA> completarSubacoesSubtracao(String parte) {
        List<SubAcaoPPA> retorno = Lists.newArrayList();
        if (!formulaSubtracao.getComponentesAcao().isEmpty()) {
            for (ComponenteFormulaAcao componenteFormulaAcao : formulaSubtracao.getComponentesAcao()) {
                retorno.addAll(facade.getFormulaItemDemonstrativoFacade().getSubProjetoAtividadeFacade().buscarSubPorProjetoAtividades(parte.trim(), componenteFormulaAcao.getAcaoPPA()));
            }
        } else {
            FacesUtil.addAtencao("Selecione uma ou mais Ações para buscar as Subações.");
        }
        return retorno;
    }

    public List<Conta> completarContasAdicao(String parte) {
        try {
            return facade.getFormulaItemDemonstrativoFacade().getContaFacade().listaPorPlanoDeContas(formulaAdicao.getPlanoDeContas(), parte.trim());
        } catch (Exception e) {
            FacesUtil.addOperacaoRealizada(e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<Conta> completarContasSubtracao(String parte) {
        try {
            return facade.getFormulaItemDemonstrativoFacade().getContaFacade().listaPorPlanoDeContas(formulaSubtracao.getPlanoDeContas(), parte.trim());
        } catch (Exception e) {
            FacesUtil.addOperacaoRealizada(e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<Funcao> completarFuncoes(String parte) {
        return facade.getFormulaItemDemonstrativoFacade().getFuncaoFacade().listaFiltrandoFuncao(parte.trim());
    }

    public List<SubFuncao> completarSubfuncoes(String parte) {
        return facade.getFormulaItemDemonstrativoFacade().getSubFuncaoFacade().listaFiltrandoSubFuncao(parte.trim());
    }

    public List<HierarquiaOrganizacional> completarUnidadesOrganizacionais(String parte) {
        return facade.getFormulaItemDemonstrativoFacade().getUnidadeOrganizacionalFacade().listaHierarquiasVigentes(facade.getSistemaFacade().getDataOperacao(), parte.trim());
    }

    public List<RelatoriosItemDemonst> completarRelatorios(String parte) {
        return facade.getFormulaItemDemonstrativoFacade().getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().listaExercicio(selecionado.getExercicio(), parte.trim());
    }

    public List<SubConta> completarSubContas(String filtro) {
        return facade.getSubContaFacade().buscarContasFinanceirasPorBancariaEntidadeOuTodosPorUnidadeAndContaDeDestinacao(filtro.trim(), null, null, selecionado.getExercicio(), null, null, null, true);
    }

    public void removeComponente(ComponenteFormula compForm, FormulaItemDemonstrativoVO formulaVO) {
        try {
            mapFormulaPorOperacaoEColuna.get(formulaVO.getOperacaoFormula().name() + (compForm.getColuna() != null ?
                compForm.getColuna().toString() : "")).getComponenteFormula().remove(compForm);

            if (compForm instanceof ComponenteFormulaItem) {
                formulaVO.getComponentesItemDemonstrativo().remove(compForm);
            } else if (compForm instanceof ComponenteFormulaUnidadeOrganizacional) {
                formulaVO.getComponentesUnidadeOrganizacional().remove(compForm);
            } else if (compForm instanceof ComponenteFormulaFuncao) {
                formulaVO.getComponentesFuncao().remove(compForm);
            } else if (compForm instanceof ComponenteFormulaSubFuncao) {
                formulaVO.getComponentesSubFuncao().remove(compForm);
            } else if (compForm instanceof ComponenteFormulaAcao) {
                formulaVO.getComponentesAcao().remove(compForm);
            } else if (compForm instanceof ComponenteFormulaSubacao) {
                formulaVO.getComponentesSubacao().remove(compForm);
            } else if (compForm instanceof ComponenteFormulaConta) {
                formulaVO.getComponentesConta().remove(compForm);
            }  else if (compForm instanceof ComponenteFormulaFonteRecurso) {
                formulaVO.getComponentesFonteRecurso().remove(compForm);
            }  else if (compForm instanceof ComponenteFormulaSubConta) {
                formulaVO.getComponentesSubConta().remove(compForm);
            } else if (compForm instanceof ComponenteFormulaPrograma) {
                formulaVO.getComponentesPrograma().remove(compForm);
            } else if (compForm instanceof ComponenteFormulaTipoDesp) {
                formulaVO.getComponentesTipoDespesa().remove(compForm);
            } else if (compForm instanceof ComponenteFormulaValorPadrao) {
                formulaVO.getComponentesValorPadrao().remove(compForm);
            }
        } catch (Exception ex) {
            LogUtil.registrarExcecao(logger, MENSAGEM_ERRO_AO_REMOVER_REGISTRO, ex);
            FacesUtil.addErrorGenerico(ex);
        }
    }

    public void abrirDialogFormula(ItemDemonstrativo item) {
        limparFormulasVOs();
        itemDemonstrativo = facade.getItemDemonstrativoFacade().recuperar(item.getId());
        if (itemDemonstrativo.getFormulas() != null && !itemDemonstrativo.getFormulas().isEmpty()) {
            for (FormulaItemDemonstrativo fid : itemDemonstrativo.getFormulas()) {
                mapFormulaPorOperacaoEColuna.put(fid.getOperacaoFormula().name() + (fid.getColuna() != null ? fid.getColuna().toString() : ""), fid);
                if (OperacaoFormula.ADICAO.equals(fid.getOperacaoFormula())) {
                    if (formulaAdicao == null) {
                        formulaAdicao = new FormulaItemDemonstrativoVO();
                        formulaAdicao.setOperacaoFormula(fid.getOperacaoFormula());
                    }
                    atualizarComponentesFormulaVO(formulaAdicao, fid);
                }
                if (OperacaoFormula.SUBTRACAO.equals(fid.getOperacaoFormula())) {
                    if (formulaSubtracao == null) {
                        formulaSubtracao = new FormulaItemDemonstrativoVO();
                        formulaSubtracao.setOperacaoFormula(fid.getOperacaoFormula());
                    }
                    atualizarComponentesFormulaVO(formulaSubtracao, fid);
                }
            }
        }
        if (formulaAdicao == null) {
            formulaAdicao = new FormulaItemDemonstrativoVO();
            formulaAdicao.setOperacaoFormula(OperacaoFormula.ADICAO);
        }
        if (formulaSubtracao == null) {
            formulaSubtracao = new FormulaItemDemonstrativoVO();
            formulaSubtracao.setOperacaoFormula(OperacaoFormula.SUBTRACAO);
        }
        RequestContext.getCurrentInstance().execute("dialogFormula.show()");
    }

    private void atualizarComponentesFormulaVO(FormulaItemDemonstrativoVO vo, FormulaItemDemonstrativo fid) {
        fid.getComponenteFormula().forEach(comp -> {
            comp.setColuna(fid.getColuna());
            if (comp instanceof ComponenteFormulaAcao) {
                Util.adicionarObjetoEmLista(vo.getComponentesAcao(), (ComponenteFormulaAcao) comp);
            } else if (comp instanceof ComponenteFormulaSubacao) {
                Util.adicionarObjetoEmLista(vo.getComponentesSubacao(), (ComponenteFormulaSubacao) comp);
            } else if (comp instanceof ComponenteFormulaConta) {
                Util.adicionarObjetoEmLista(vo.getComponentesConta(), (ComponenteFormulaConta) comp);
            } else if (comp instanceof ComponenteFormulaFonteRecurso) {
                Util.adicionarObjetoEmLista(vo.getComponentesFonteRecurso(), (ComponenteFormulaFonteRecurso) comp);
            } else if (comp instanceof ComponenteFormulaItem) {
                Util.adicionarObjetoEmLista(vo.getComponentesItemDemonstrativo(), (ComponenteFormulaItem) comp);
            } else if (comp instanceof ComponenteFormulaFuncao) {
                Util.adicionarObjetoEmLista(vo.getComponentesFuncao(), (ComponenteFormulaFuncao) comp);
            } else if (comp instanceof ComponenteFormulaPrograma) {
                Util.adicionarObjetoEmLista(vo.getComponentesPrograma(), (ComponenteFormulaPrograma) comp);
            } else if (comp instanceof ComponenteFormulaSubFuncao) {
                Util.adicionarObjetoEmLista(vo.getComponentesSubFuncao(), (ComponenteFormulaSubFuncao) comp);
            } else if (comp instanceof ComponenteFormulaUnidadeOrganizacional) {
                Util.adicionarObjetoEmLista(vo.getComponentesUnidadeOrganizacional(), (ComponenteFormulaUnidadeOrganizacional) comp);
            } else if (comp instanceof ComponenteFormulaTipoDesp) {
                Util.adicionarObjetoEmLista(vo.getComponentesTipoDespesa(), (ComponenteFormulaTipoDesp) comp);
            } else if (comp instanceof ComponenteFormulaSubConta) {
                Util.adicionarObjetoEmLista(vo.getComponentesSubConta(), (ComponenteFormulaSubConta) comp);
            } else if (comp instanceof ComponenteFormulaValorPadrao) {
                Util.adicionarObjetoEmLista(vo.getComponentesValorPadrao(), (ComponenteFormulaValorPadrao) comp);
            }
        });
    }

    public void limparFormulasVOs() {
        colunaFormula = null;
        formulaAdicao = null;
        formulaSubtracao = null;
        mapFormulaPorOperacaoEColuna = new HashMap<>();
    }

    private void adicionarNovaFormulaMapPorOperacaoEColuna(FormulaItemDemonstrativoVO vo) {
        if (!mapFormulaPorOperacaoEColuna.containsKey(vo.getOperacaoFormula().name() + getColunaFormulaAsString())) {
            FormulaItemDemonstrativo formula = new FormulaItemDemonstrativo();
            formula.setColuna(colunaFormula);
            formula.setItemDemonstrativo(itemDemonstrativo);
            formula.setExercicio(selecionado.getExercicio());
            formula.setOperacaoFormula(vo.getOperacaoFormula());
            mapFormulaPorOperacaoEColuna.put(vo.getOperacaoFormula().name() + getColunaFormulaAsString(), formula);
        }
    }

    public void cancelaFormula() {
        itemDemonstrativo = new ItemDemonstrativo();
        RequestContext.getCurrentInstance().execute("dialogFormula.hide()");
    }

    public ConverterAutoComplete getConverterItemDemonstrativo() {
        if (converterItemDemonstrativo == null) {
            converterItemDemonstrativo = new ConverterAutoComplete(ItemDemonstrativo.class, facade.getFormulaItemDemonstrativoFacade().getItemDemonstrativoFacade());
        }
        return converterItemDemonstrativo;
    }

    public Boolean getEditar() {
        return editar;
    }

    public void setEditar(Boolean editar) {
        this.editar = editar;
    }

    public Integer getColuna() {
        return coluna;
    }

    public void setColuna(Integer coluna) {
        this.coluna = coluna;
    }

    public Integer getEspaco() {
        return espaco;
    }

    public void setEspaco(Integer espaco) {
        this.espaco = espaco;
    }

    public Exercicio getExercicioCopia() {
        return exercicioCopia;
    }

    public void setExercicioCopia(Exercicio exercicioCopia) {
        this.exercicioCopia = exercicioCopia;
    }

    public List<String> getListaErrosTransporte() {
        return listaErrosTransporte;
    }

    public void setListaErrosTransporte(List<String> listaErrosTransporte) {
        this.listaErrosTransporte = listaErrosTransporte;
    }

    public String getColunaFormulaAsString() {
        return colunaFormula != null ? colunaFormula.toString() : "";
    }

    public Integer getColunaFormula() {
        return colunaFormula;
    }

    public void setColunaFormula(Integer colunaFormula) {
        this.colunaFormula = colunaFormula;
    }

    public UsuarioSistema getUsuarioCorrente() {
        return facade.getSistemaFacade().getUsuarioCorrente();
    }

    public ItemDemonstrativo getItemDemonstrativoExAnterior() {
        return itemDemonstrativoExAnterior;
    }

    public void setItemDemonstrativoExAnterior(ItemDemonstrativo itemDemonstrativoExAnterior) {
        this.itemDemonstrativoExAnterior = itemDemonstrativoExAnterior;
    }

    public List<ItemDemonstrativo> getItensDemonstrativos() {
        return itensDemonstrativos;
    }

    public void setItensDemonstrativos(List<ItemDemonstrativo> itensDemonstrativos) {
        this.itensDemonstrativos = itensDemonstrativos;
    }

    public FormulaItemDemonstrativoVO getFormulaAdicao() {
        return formulaAdicao;
    }

    public void setFormulaAdicao(FormulaItemDemonstrativoVO formulaAdicao) {
        this.formulaAdicao = formulaAdicao;
    }

    public FormulaItemDemonstrativoVO getFormulaSubtracao() {
        return formulaSubtracao;
    }

    public void setFormulaSubtracao(FormulaItemDemonstrativoVO formulaSubtracao) {
        this.formulaSubtracao = formulaSubtracao;
    }

    public Exercicio getExercicioExportar() {
        return exercicioExportar;
    }

    public void setExercicioExportar(Exercicio exercicioExportar) {
        this.exercicioExportar = exercicioExportar;
    }

    public Exercicio getExercicioImportar() {
        return exercicioImportar;
    }

    public void setExercicioImportar(Exercicio exercicioImportar) {
        this.exercicioImportar = exercicioImportar;
    }

    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public String getLogErrosImportar() {
        return logErrosImportar;
    }

    public void setLogErrosImportar(String logErrosImportar) {
        this.logErrosImportar = logErrosImportar;
    }

    public RelatoriosItemDemonst getRelatoriosItemDemonst() {
        return relatoriosItemDemonst;
    }

    public void setRelatoriosItemDemonst(RelatoriosItemDemonst relatoriosItemDemonst) {
        this.relatoriosItemDemonst = relatoriosItemDemonst;
    }

    public List<RelatoriosItemDemonst> buscarRelatorios(String parte) {
        if (exercicioExportar != null) {
            return facade.listaExercicio(exercicioExportar, parte.trim());
        }
        return facade.listaExercicio(facade.getSistemaFacade().getExercicioCorrente(), parte.trim());
    }

    public void gerarArquivoTXTRelatorio() {
        try {
            validarExercicioArquivo(true);
            file = facade.gerarArquivoRelatoriosItemDemonst(relatoriosItemDemonst, exercicioExportar);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
        }
    }

    private void validarExercicioArquivo(boolean isExportar) {
        ValidacaoException ve = new ValidacaoException();
        if (isExportar && exercicioExportar == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        if (!isExportar && exercicioImportar == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        ve.lancarException();
    }

    public void uploadArquivo(FileUploadEvent event) {
        try {
            validarExercicioArquivo(false);
            InputStreamReader in = new InputStreamReader(event.getFile().getInputstream(), StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(in);
            logErrosImportar = facade.montarItemDemonstrativoComArquivoImportado(bufferedReader, exercicioImportar);
            FacesUtil.addOperacaoRealizada("Os relatórios foram criados com sucesso, confira o Log para mais detalhes.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception ex) {
            FacesUtil.addErrorPadrao(ex);
        }
    }

    public void gerarPdfLog() {
        try {
            facade.gerarPdfLog(logErrosImportar);
        } catch (Exception ex) {
            FacesUtil.addErrorPadrao(ex);
        }
    }
}
