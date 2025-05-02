package br.com.webpublico.controle.rh.administracaodepagamento;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.rh.administracaodepagamento.ReprocessamentoLancamentoTercoFeriasAutomatico;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidadesauxiliares.rh.LancamentoTercoFeriasAutVO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.rh.administracaodepagamento.ReprocessamentoLancamentoTercoFeriasAutomaticoFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "reprocessamentoLancamentoTercoFeriasAutomaticoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoReprocessamentoLancamentoTercoFerias", pattern = "/reprocessamento-lancamento-terco-ferias-automatico/novo/", viewId = "/faces/rh/administracaodepagamento/reprocessamentolancamentotercoferiasautomatico/edita.xhtml"),
    @URLMapping(id = "editarReprocessamentoLancamentoTercoFerias", pattern = "/reprocessamento-lancamento-terco-ferias-automatico/editar/#{reprocessamentoLancamentoTercoFeriasAutomaticoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/reprocessamentolancamentotercoferiasautomatico/edita.xhtml"),
    @URLMapping(id = "listarReprocessamentoLancamentoTercoFerias", pattern = "/reprocessamento-lancamento-terco-ferias-automatico/listar/", viewId = "/faces/rh/administracaodepagamento/reprocessamentolancamentotercoferiasautomatico/lista.xhtml"),
    @URLMapping(id = "verReprocessamentoLancamentoTercoFerias", pattern = "/reprocessamento-lancamento-terco-ferias-automatico/ver/#{reprocessamentoLancamentoTercoFeriasAutomaticoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/reprocessamentolancamentotercoferiasautomatico/visualizar.xhtml")
})
public class ReprocessamentoLancamentoTercoFeriasAutomaticoControlador extends PrettyControlador<ReprocessamentoLancamentoTercoFeriasAutomatico> implements Serializable, CRUD {

    @EJB
    private ReprocessamentoLancamentoTercoFeriasAutomaticoFacade facade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private List<LancamentoTercoFeriasAutVO> lancamentos;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ContratoFP contratoFP;
    private List<LancamentoTercoFeriasAutVO> filtroItem;
    private List<ContratoFP> contratoFPList;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public ReprocessamentoLancamentoTercoFeriasAutomaticoControlador() {
        super(ReprocessamentoLancamentoTercoFeriasAutomatico.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/reprocessamento-lancamento-terco-ferias-automatico/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoReprocessamentoLancamentoTercoFerias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        ConfiguracaoRH configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigente();
        if (configuracaoRH != null && configuracaoRH.getTipoTercoFeriasAutomatico() != null) {
            selecionado.setCriterioBusca(configuracaoRH.getTipoTercoFeriasAutomatico());
        }
        lancamentos = Lists.newArrayList();
        contratoFPList = Lists.newArrayList();
    }

    @Override
    @URLAction(mappingId = "editarReprocessamentoLancamentoTercoFerias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "verReprocessamentoLancamentoTercoFerias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public void limparOrgaoVazio() {
        if (this.hierarquiaOrganizacional == null || this.hierarquiaOrganizacional.getId() == null) {
            this.hierarquiaOrganizacional = null;
        }
    }

    public void limparContratoFPVazio() {
        if (this.contratoFP == null || this.contratoFP.getId() == null) {
            this.contratoFP = null;
        }
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            validarLancamentos();
            facade.salvar(selecionado, lancamentos);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao reprocessar lançamentos de 1/3 de férias automático {}", e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void buscarServidores() {
        try {
            lancamentos = Lists.newArrayList();
            filtroItem =  null;
            validarCampos();
            if (contratoFPList != null && !contratoFPList.isEmpty()){
                contratoFPList.forEach(contratoFP -> {
                    List<LancamentoTercoFeriasAutVO> resultado = facade.buscarServidoresParaLancamentoTerco(selecionado, contratoFP, hierarquiaOrganizacional);
                    if (resultado != null) {
                        lancamentos.addAll(resultado);
                    }
                } );
            } else {
                lancamentos = facade.buscarServidoresParaLancamentoTerco(selecionado, contratoFP, hierarquiaOrganizacional);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao buscar servidores {}", e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void validarCampos() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getAnoVerificacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano da Competência de Verificação deve ser informado.");
        }
        if (selecionado.getMesVerificacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês da Competência de Verificação deve ser informado.");
        } else if (selecionado.getMesVerificacao() < 1 || selecionado.getMesVerificacao() > 12) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Favor informar o Mês da Competência de Verificação entre 01 (Janeiro) e 12 (Dezembro).");
        }
        if (selecionado.getAnoLancamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano da Competência de Lançamento deve ser informado.");
        }
        if (selecionado.getMesLancamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês da Competência de Lançamento deve ser informado.");
        } else if (selecionado.getMesLancamento() < 1 || selecionado.getMesLancamento() > 12) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Favor informar o Mês da Competência de Lançamento entre 01 (Janeiro) e 12 (Dezembro).");
        }
        ve.lancarException();
    }

    public void validarLancamentos() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        boolean existeLancamentoASerSalvo = false;
        for (LancamentoTercoFeriasAutVO lancamento : lancamentos) {
            if (lancamento.getStatus().getSeraIncluido()) {
                existeLancamentoASerSalvo = true;
                break;
            }
        }
        if (!existeLancamentoASerSalvo) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não contém lançamentos de 1/3 de férias a serem incluídos");
        }
        ve.lancarException();
    }

    public void removerItemLancamento(LancamentoTercoFeriasAutVO item) {
        lancamentos.remove(item);
    }

    public List<LancamentoTercoFeriasAutVO> getLancamentos() {
        return lancamentos;
    }

    public void setLancamentos(List<LancamentoTercoFeriasAutVO> lancamentos) {
        this.lancamentos = lancamentos;
    }

    public List<ContratoFP> completaContratoFPs(String parte) {
        Date dataVerificacao = null;
        if(selecionado != null && selecionado.getMesVerificacao() != null && selecionado.getAnoVerificacao() != null) {
            dataVerificacao = DataUtil.getUltimoDiaMes(selecionado.getMesVerificacao(), selecionado.getAnoVerificacao());
        }
        return contratoFPFacade.recuperaContratosComCargoVincABasePAFerias(parte.trim(), dataVerificacao, hierarquiaOrganizacional);
    }

    public void setarHierarquiaOrganizacional(SelectEvent item) {
        if(item != null) {
            hierarquiaOrganizacional = (HierarquiaOrganizacional) item.getObject();
        }
    }

    public void setarContratoFP(SelectEvent item) {
        if(item != null) {
            contratoFP = (ContratoFP) item.getObject();
        }
    }

    public void adicionarContratoFP() {
        try {
            validarAdicionarContratoFP();
            contratoFPList.add(getContratoFP());
            setContratoFP(null);
            ordenarContratos();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarAdicionarContratoFP() {
        ValidacaoException ve = new ValidacaoException();
        if (contratoFP == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo verba deve ser infomado.");
        } else {
            for (ContratoFP contratofp : contratoFPList) {
                if (contratofp.equals(this.contratoFP)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O contrato " + contratoFP + " já foi adicionado na lista.");
                }
            }
        }
        ve.lancarException();
    }

    private void ordenarContratos() {
        contratoFPList.sort((o1, o2) -> {
            if (o1.getMatriculaFP().getMatricula() == null || o2.getMatriculaFP().getMatricula() == null) {
                return 0;
            }
            Long codigo1 = Long.valueOf(o1.getMatriculaFP().getMatricula());
            Long codigo2 = Long.valueOf(o2.getMatriculaFP().getMatricula());
            return codigo1.compareTo(codigo2);
        });
    }

    public void removerContratoFP(ContratoFP contratoFP) {
        getContratoFPList().remove(contratoFP);
    }

    public List<ContratoFP> getContratoFPList() {
        return contratoFPList;
    }

    public void setContratoFPList(List<ContratoFP> contratoFPList) {
        this.contratoFPList = contratoFPList;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public List<LancamentoTercoFeriasAutVO> getFiltroItem() {
        return filtroItem;
    }

    public void setFiltroItem(List<LancamentoTercoFeriasAutVO> filtroItem) {
        this.filtroItem = filtroItem;
    }

}
