/*
 * Codigo gerado automaticamente em Fri Nov 25 14:51:04 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.PeriodoAquisitivoFL;
import br.com.webpublico.entidades.rh.administracaodepagamento.LancamentoTercoFeriasAut;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.rh.SemBasePeriodoAquisitivoException;
import br.com.webpublico.exception.rh.SemConfiguracaoDeFaltaInjustificada;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.administracaodepagamento.LancamentoTercoFeriasAutFacade;
import br.com.webpublico.negocios.rh.executores.PeriodoAquisitivoFLExecutor;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean(name = "periodoAquisitivoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "editaPeriodoAquisitivo", pattern = "/periodo-aquisitivo/consultar/", viewId = "/faces/rh/administracaodepagamento/periodoaquisitivo/edita.xhtml")
})
public class PeriodoAquisitivoControlador extends PrettyControlador {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ContratoFP contratoFP;
    private TipoPeriodoAquisitivo tipoPeriodoAquisitivoSelecionado;
    private List<PeriodoAquisitivoFL> periodosAquisitivos;
    @EJB
    private PeriodoAquisitivoFLFacade periodoAquisitivoFLFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LancamentoTercoFeriasAutFacade lancamentoTercoFeriasAutFacade;
    private TipoFiltro tipoFiltro = TipoFiltro.VINCULO;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Future<AssistenteBarraProgresso> future;

    public PeriodoAquisitivoControlador() {
        super(PeriodoAquisitivoFL.class);
    }

    public TipoPeriodoAquisitivo getTipoPeriodoAquisitivoSelecionado() {
        return tipoPeriodoAquisitivoSelecionado;
    }

    public void setTipoPeriodoAquisitivoSelecionado(TipoPeriodoAquisitivo tipoPeriodoAquisitivoSelecionado) {
        this.tipoPeriodoAquisitivoSelecionado = tipoPeriodoAquisitivoSelecionado;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public List<PeriodoAquisitivoFL> getPeriodosAquisitivos() {
        return periodosAquisitivos;
    }

    public void setPeriodosAquisitivos(List<PeriodoAquisitivoFL> periodosAquisitivos) {
        this.periodosAquisitivos = periodosAquisitivos;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Converter getConverterContratoFP() {
        return new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        return contratoFPFacade.recuperaContratoMatricula(parte.trim());
    }

    public List<HierarquiaOrganizacional> completarHierarquia(String parte) {
        return hierarquiaOrganizacionalFacade.filtraPorNivel(parte.trim(), "2", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), sistemaFacade.getDataOperacao());
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public List<SelectItem> getTiposPeriodoAquisitivo() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Todos"));
        for (TipoPeriodoAquisitivo object : TipoPeriodoAquisitivo.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    private boolean validarCamposObrigatorios() {
        if (contratoFP == null && hierarquiaOrganizacional == null) {
            FacesUtil.addCampoObrigatorio("O campo Servidor ou Orgão deve ser informado.");
            return false;
        }

        return true;
    }

    @Override
    public AbstractFacade getFacede() {
        return periodoAquisitivoFLFacade;
    }

    @Override
    @URLAction(mappingId = "editaPeriodoAquisitivo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        hierarquiaOrganizacional = null;
        periodosAquisitivos = Lists.newLinkedList();
        if (!FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().isEmpty()) {
            consultarPeriodoAquisitivoParametrosUrl();
        } else {
            contratoFP = null;
        }
    }

    private void consultarPeriodoAquisitivoParametrosUrl() {
        if (buscarParametrosURL("contrato") != null) {
            contratoFP = contratoFPFacade.recuperar(Long.parseLong(buscarParametrosURL("contrato")));
            consultarPeriodoAquisitivo();
        }
    }

    private String buscarParametrosURL(String texto) {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(texto);
    }

    public void consultarPeriodoAquisitivo() {
        if (!validarCamposObrigatorios()) {
            return;
        }

        if (tipoPeriodoAquisitivoSelecionado == null)
            periodosAquisitivos = periodoAquisitivoFLFacade.recuperaPeriodosPorContrato(contratoFP);

        if (tipoPeriodoAquisitivoSelecionado != null)
            periodosAquisitivos = periodoAquisitivoFLFacade.recuperarPeriodosAquisitivos(contratoFP, tipoPeriodoAquisitivoSelecionado);

    }

    public void atualizarPeriodoAquisitivo() {
        if (!validarCamposObrigatorios()) {
            return;
        }

        try {
            if (contratoFP != null) {
                contratoFP = periodoAquisitivoFLFacade.gerarPeriodosAquisitivos(contratoFP, sistemaFacade.getDataOperacao(), tipoPeriodoAquisitivoSelecionado);
                consultarPeriodoAquisitivo();
                FacesUtil.addOperacaoRealizada("Periodos aquisitivos gerados/atualizados com sucesso.");
            } else {
                gerarPeriodoAquisitivoPorHierarquia(hierarquiaOrganizacional);
            }

        } catch (SemBasePeriodoAquisitivoException semBaseEx) {
            logger.error("erro", semBaseEx);
            String url = "<b><a href='" + FacesUtil.getRequestContextPath() + "/cargo/" + (contratoFP != null ? "editar/" + contratoFP.getCargo().getId() : "listar/") + "' target='_blank'>Cadastro de Cargos</a></b>";
            FacesUtil.addOperacaoNaoRealizada((contratoFP != null ? "O cargo <b>" + contratoFP.getCargo() : "Há Cargos que ") + "</b> não possui bases de período aquisitivo vigentes em <b>" + Util.formatterDiaMesAno.format(UtilRH.getDataOperacao()) + "</b>.<br />Verifique as configurações no " + url);
            return;
        } catch (SemConfiguracaoDeFaltaInjustificada semConfEx) {
            logger.error("erro", semConfEx);
            String url = "<b><a href='" + FacesUtil.getRequestContextPath() + "/configuracao/rh/listar/' target='_blank'>Configurações Gerais do RH</a></b>";
            FacesUtil.addOperacaoNaoRealizada(semConfEx.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Verifique as informações em: " + url);
            return;
        } catch (ExcecaoNegocioGenerica ex) {
            logger.error("erro", ex);
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            return;
        } catch (Exception e) {
            logger.error("erro", e);
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            return;
        }
        FacesUtil.atualizarComponente("atualizar-componente");
    }

    private void gerarPeriodoAquisitivoPorHierarquia(HierarquiaOrganizacional hierarquiaOrganizacional) {
        List<ContratoFP> contratoFPS = contratoFPFacade.recuperaContratoVigenteMatriculaPorLocalDeTrabalhoRecursivaPelaView(hierarquiaOrganizacional, sistemaFacade.getDataOperacao());
        Collections.sort(contratoFPS);
        assistenteBarraProgresso = new AssistenteBarraProgresso();
        assistenteBarraProgresso.setDescricaoProcesso("Gerando/Atualizando Períodos Aquisitivos...");
        assistenteBarraProgresso.zerarContadoresProcesso();
        assistenteBarraProgresso.setTotal(contratoFPS.size());
        future = new PeriodoAquisitivoFLExecutor(periodoAquisitivoFLFacade).execute(contratoFPS, assistenteBarraProgresso, sistemaFacade.getDataOperacao(), tipoPeriodoAquisitivoSelecionado);
        FacesUtil.executaJavaScript("gerarAtualizarPeriodosAquisitivos()");
    }

    public void verificarTermino() {
        if (future != null && future.isDone()) {
            try {
                AssistenteBarraProgresso assistente = future.get();
                if (assistente != null) {
                    List<String> erros = (List<String>) assistente.getSelecionado();
                    if (erros != null && !erros.isEmpty()) {
                        for (String erro : erros) {
                            FacesUtil.addWarn("Atenção", erro);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("erro", e);
            }
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.executaJavaScript("termina()");
            future = null;
            FacesUtil.addOperacaoRealizada("Periodos aquisitivos gerados/atualizados com sucesso.");
        }
    }

    public void encerrarFuture() {
        FacesUtil.executaJavaScript("terminaProcesso()");
    }

    public void removerPeriodoAquisitivoFL(PeriodoAquisitivoFL periodo) {
        periodo = periodoAquisitivoFLFacade.recuperar(periodo.getId());
        try {
            validarExclusao(periodo);
            if (TipoPeriodoAquisitivo.LICENCA.equals(periodo.getBaseCargo().getBasePeriodoAquisitivo().getTipoPeriodoAquisitivo())) {
                validarForeinKeysComRegistro(periodo);
            }
            periodoAquisitivoFLFacade.remover(periodo);
            consultarPeriodoAquisitivo();
            FacesUtil.addOperacaoRealizada("Período Aquisitivo Excluído com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao excluir o período aquisitivo {}", e);
        }
    }

    private void validarExclusao(PeriodoAquisitivoFL periodo) {
        ValidacaoException ve = new ValidacaoException();
        if (periodoAquisitivoFLFacade.getConcessaoFeriasLicencaFacade().existeConcessaoFeriasLicencaPorPeriodoAquisitivoFL(periodo)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Foi encontrado Concessão de " + periodo.getBaseCargo().getBasePeriodoAquisitivo().getTipoPeriodoAquisitivo().getDescricao() +
                " para este Período Aquisitivo. Não pode ser removido!");
        }
        if (periodo.temSugestaoFeriasAprovada()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Foi encontrado Programação de Férias 'Aprovada' para este Período Aquisitivo. Não pode ser removido!");
        }
        LancamentoTercoFeriasAut lancamento = lancamentoTercoFeriasAutFacade.recuperaLancamentoTercoFeriasAutPorPeriodoAquisitivo(periodo);
        if (lancamento != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Existe registro de lançamento de 1/3 de férias automático vinculado ao " +
                "período aquisitivo. Para efetuar a exclusão do período aquisitivo, por favor, proceda a exclusão do registro de lançamento de 1/3 de férias automático. </br>" +
                "<b>AVISO:</b> a exclusão do registro de lançamento de 1/3 de férias automático não exclui possível cálculo de verbas correspondentes em ficha financeira. " +
                "A exclusão poderá deixar tais verbas sem lastro, com possibilidade de pagamento duplicado em competência posterior.");
        }
        ve.lancarException();
    }

    public TipoFiltro getTipoFiltro() {
        return tipoFiltro;
    }

    public void setTipoFiltro(TipoFiltro tipoFiltro) {
        this.tipoFiltro = tipoFiltro;
    }

    public void limparPeriodosAquisitivos(ActionEvent ev) {
        periodosAquisitivos = null;
    }

    public enum TipoFiltro {
        VINCULO, ORGAO;
    }
}
