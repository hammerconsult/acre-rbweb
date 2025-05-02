package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.OrdenaResultadoParcelaPorVencimento;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ProcessoAlteracaoVencimentoParcelaFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Wellington on 08/12/2015.
 */

@ManagedBean(name = "processoAlteracaoVencimentoParcelaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listaProcessoAlteracaoVencimento", pattern = "/tributario/conta-corrente/processo-de-alteracao-de-vencimento-da-parcela/listar/",
        viewId = "/faces/tributario/contacorrente/processoalteracaovencimentoparcela/lista.xhtml"),
    @URLMapping(id = "novoProcessoAlteracaoVencimento", pattern = "/tributario/conta-corrente/processo-de-alteracao-de-vencimento-da-parcela/novo/",
        viewId = "/faces/tributario/contacorrente/processoalteracaovencimentoparcela/edita.xhtml"),
    @URLMapping(id = "verProcessoAlteracaoVencimento", pattern = "/tributario/conta-corrente/processo-de-alteracao-de-vencimento-da-parcela/ver/#{processoAlteracaoVencimentoParcelaControlador.id}/",
        viewId = "/faces/tributario/contacorrente/processoalteracaovencimentoparcela/visualizar.xhtml"),
    @URLMapping(id = "editarProcessoAlteracaoVencimento", pattern = "/tributario/conta-corrente/processo-de-alteracao-de-vencimento-da-parcela/editar/#{processoAlteracaoVencimentoParcelaControlador.id}/",
        viewId = "/faces/tributario/contacorrente/processoalteracaovencimentoparcela/edita.xhtml")})
public class ProcessoAlteracaoVencimentoParcelaControlador extends PrettyControlador<ProcessoAlteracaoVencimentoParcela> implements Serializable, CRUD {

    @EJB
    private ProcessoAlteracaoVencimentoParcelaFacade processoAlteracaoVencimentoParcelaFacade;
    private FiltroDebito filtroDebito;
    private List<ResultadoParcela> resultadoConsulta;
    private ResultadoParcela[] resultados;

    public ProcessoAlteracaoVencimentoParcelaControlador() {
        super(ProcessoAlteracaoVencimentoParcela.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return processoAlteracaoVencimentoParcelaFacade;
    }

    @URLAction(mappingId = "novoProcessoAlteracaoVencimento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        limparFiltrosDeDebitos();
        atribuirInformacoesIniciaisAoProcesso();
    }

    private void atribuirInformacoesIniciaisAoProcesso() {
        selecionado.setRealizadoEm(processoAlteracaoVencimentoParcelaFacade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(processoAlteracaoVencimentoParcelaFacade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setSituacao(SituacaoProcessoDebito.EM_ABERTO);
        selecionado.setExercicio(processoAlteracaoVencimentoParcelaFacade.getSistemaFacade().getExercicioCorrente());
        selecionado.setCodigo(processoAlteracaoVencimentoParcelaFacade.recuperarProximoCodigoPorExercicio(selecionado.getExercicio()));
    }

    @URLAction(mappingId = "verProcessoAlteracaoVencimento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        inicializarEstorno();
    }

    private void inicializarEstorno() {
        selecionado.setDataEstorno(processoAlteracaoVencimentoParcelaFacade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioEstorno(processoAlteracaoVencimentoParcelaFacade.getSistemaFacade().getUsuarioCorrente());
    }

    @URLAction(mappingId = "editarProcessoAlteracaoVencimento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        limparFiltrosDeDebitos();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/conta-corrente/processo-de-alteracao-de-vencimento-da-parcela/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<ResultadoParcela> getResultadoConsulta() {
        return resultadoConsulta;
    }

    public String buscarInscricaoDoCadastro(ParcelaValorDivida parcela) {
        return processoAlteracaoVencimentoParcelaFacade.getConsultaDebitoFacade().buscarNumeroTipoCadastroPorParcela(parcela);
    }

    public String buscarEstadoDaParcela(ParcelaValorDivida parcela) {
        return processoAlteracaoVencimentoParcelaFacade.getConsultaDebitoFacade().buscarEstadoDaParcela(parcela);
    }

    public String buscarDescricaoDaSituacaoDaParcela(ParcelaValorDivida parcela) {
        SituacaoParcelaValorDivida ultimaSituacao = processoAlteracaoVencimentoParcelaFacade.getConsultaDebitoFacade().getUltimaSituacao(parcela);
        return ultimaSituacao.getSituacaoParcela().getDescricao();
    }

    public String buscarReferenciaDaParcela(ParcelaValorDivida parcela) {
        SituacaoParcelaValorDivida ultimaSituacao = processoAlteracaoVencimentoParcelaFacade.getConsultaDebitoFacade().getUltimaSituacao(parcela);
        return ultimaSituacao.getReferencia();
    }

    public BigDecimal buscarValorTotalDaParcela(ParcelaValorDivida parcela) {
        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, parcela.getId());
        consulta.executaConsulta();
        List<ResultadoParcela> resultadoParcelas = consulta.getResultados();
        return resultadoParcelas.get(0).getValorTotal();
    }

    public void removerItemProcessoAlteracaoVencimentoParcela(ItemProcessoAlteracaoVencimentoParcela itemProcessoAlteracaoVencimentoParcela) {
        selecionado.getItensProcessoAlteracaoVencParc().remove(itemProcessoAlteracaoVencimentoParcela);
    }

    public FiltroDebito getFiltroDebito() {
        return filtroDebito;
    }

    public void setFiltroDebito(FiltroDebito filtroDebito) {
        this.filtroDebito = filtroDebito;
    }

    public void limparFiltrosDeDebitos() {
        if (filtroDebito == null) {
            filtroDebito = new FiltroDebito();
        }
        resultadoConsulta = Lists.newArrayList();
    }

    public void abrirConsultaDebitos() {
        limparFiltrosDeDebitos();
        if (filtroDebito.getCadastro() != null || filtroDebito.getPessoa() != null) {
            FacesUtil.executaJavaScript("dialogo.show()");
        } else {
            FacesUtil.addCampoObrigatorio("Informe o cadastro!");
        }
    }

    public void definirFiltros(ConsultaParcela consulta) {
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);

        if (filtroDebito.getExercicioInicial() != null && filtroDebito.getExercicioFinal() != null) {
            consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MAIOR_IGUAL, filtroDebito.getExercicioInicial().getAno());
            consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MENOR_IGUAL, filtroDebito.getExercicioFinal().getAno());
        }
        if (filtroDebito.getCadastro() != null && filtroDebito.getCadastro().getId() != null) {
            consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, filtroDebito.getCadastro().getId());
        }
        if (filtroDebito.getTipoCadastroTributario().equals(TipoCadastroTributario.PESSOA)) {
            consulta.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, filtroDebito.getPessoa().getId());
        }
        if (filtroDebito.getDivida() != null) {
            consulta.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IGUAL, filtroDebito.getDivida().getId());
        }

        if (filtroDebito.isDoExercicio() && !filtroDebito.isDividaAtiva() && !filtroDebito.isDividaAtivaAjuizada()) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 0);
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 0);
        } else if (filtroDebito.isDoExercicio() && filtroDebito.isDividaAtiva() && !filtroDebito.isDividaAtivaAjuizada()) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 0);
        } else if (filtroDebito.isDoExercicio() && !filtroDebito.isDividaAtiva() && filtroDebito.isDividaAtivaAjuizada()) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 0);
        } else if (!filtroDebito.isDoExercicio() && filtroDebito.isDividaAtiva() && filtroDebito.isDividaAtivaAjuizada()) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 1);
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 1);
        } else if (!filtroDebito.isDoExercicio() && filtroDebito.isDividaAtiva() && !filtroDebito.isDividaAtivaAjuizada()) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 1);
        } else if (!filtroDebito.isDoExercicio() && !filtroDebito.isDividaAtiva() && filtroDebito.isDividaAtivaAjuizada()) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 1);
        }

        if (filtroDebito.getVencimentoInicial() != null && filtroDebito.getVencimentoFinal() != null) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR_IGUAL, filtroDebito.getVencimentoFinal());
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MAIOR_IGUAL, filtroDebito.getVencimentoInicial());
        }

        consulta.addOrdem(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Ordem.TipoOrdem.ASC).addOrdem(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Ordem.TipoOrdem.ASC);
    }

    public void pesquisarDebitos() {
        try {
            filtroDebito.validarFiltrosNecessarios();
            resultadoConsulta.clear();
            ConsultaParcela consulta = new ConsultaParcela();
            definirFiltros(consulta);
            consulta.executaConsulta();
            resultadoConsulta.addAll(consulta.getResultados());

            Collections.sort(resultadoConsulta, new OrdenaResultadoParcelaPorVencimento());
            if (resultadoConsulta.isEmpty()) {
                FacesUtil.addError("A pesquisa não encontrou nenhum Débito!", "");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public boolean isPossivelAdicionarDebito(ResultadoParcela resultadoParcela) {
        if (selecionado.getItensProcessoAlteracaoVencParc() != null) {
            for (ItemProcessoAlteracaoVencimentoParcela item : selecionado.getItensProcessoAlteracaoVencParc()) {
                if (item.getParcelaValorDivida().getId().equals(resultadoParcela.getId())) {
                    return false;
                }
            }
        }
        return true;
    }

    public ItemProcessoAlteracaoVencimentoParcela criarItemProcessoAlteracaoVencimentoParcela(ResultadoParcela resultadoParcela) {
        ItemProcessoAlteracaoVencimentoParcela item = new ItemProcessoAlteracaoVencimentoParcela();
        item.setProcAlteracaoVencimentoParc(selecionado);
        item.setParcelaValorDivida(processoAlteracaoVencimentoParcelaFacade.getConsultaDebitoFacade().recuperaParcela(resultadoParcela.getId()));
        item.setVencimentoAnterior(item.getParcelaValorDivida().getVencimento());
        item.setVencimentoModificado(null);
        return item;
    }

    public void adicionarDebito() {
        for (ResultadoParcela resultadoParcela : resultados) {
            if (selecionado.getItensProcessoAlteracaoVencParc() == null) {
                selecionado.setItensProcessoAlteracaoVencParc(Lists.<ItemProcessoAlteracaoVencimentoParcela>newArrayList());
            }

            if (isPossivelAdicionarDebito(resultadoParcela)) {
                selecionado.getItensProcessoAlteracaoVencParc().add(criarItemProcessoAlteracaoVencimentoParcela(resultadoParcela));
            } else {
                FacesUtil.addError("Não foi possível continuar!", "A parcela selecionada: "+resultadoParcela.getReferencia()+ " - "+
                        resultadoParcela.getDivida()+" - "+resultadoParcela.getParcela()+" já foi adicionada ao processo!");
            }
        }
        FacesUtil.addOperacaoRealizada("Parcelas adicionadas no Processo de Débitos!");
    }

    @Override
    public boolean validaRegrasEspecificas() {
        boolean toReturn = super.validaRegrasEspecificas();
        if (toReturn) {
            if (selecionado.getItensProcessoAlteracaoVencParc() == null || selecionado.getItensProcessoAlteracaoVencParc().size() == 0) {
                toReturn = false;
            } else {
                for (ItemProcessoAlteracaoVencimentoParcela item : selecionado.getItensProcessoAlteracaoVencParc()) {
                    if (item.getVencimentoModificado() == null) {
                        toReturn = false;
                        FacesUtil.addOperacaoNaoRealizada("Não foi informado o novo vencimento para a parcela: Ref. " + buscarReferenciaDaParcela(item.getParcelaValorDivida()) + " Seq. " +
                            item.getParcelaValorDivida().getSequenciaParcela() + " Venc. " + Util.formatterDiaMesAno.format(item.getParcelaValorDivida().getVencimento()));
                    }
                }
            }

        }
        return toReturn;
    }

    public void estornarProcesso() {
        if (selecionado.getMotivoEstorno() != null && !selecionado.getMotivoEstorno().trim().isEmpty()) {
            processoAlteracaoVencimentoParcelaFacade.estornarProcesso(selecionado);
            FacesUtil.addOperacaoRealizada("Estorno realizado com sucesso.");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } else {
            FacesUtil.addCampoObrigatorio("Informe o motivo para o estorno.");
        }
    }

    public ResultadoParcela[] getResultados() {
        return resultados;
    }

    public void setResultados(ResultadoParcela[] resultados) {
        this.resultados = resultados;
    }

    public class FiltroDebito {
        private TipoCadastroTributario tipoCadastroTributario;
        private Cadastro cadastro;
        private Pessoa pessoa;
        private Divida divida;
        private boolean doExercicio;
        private boolean dividaAtiva;
        private boolean dividaAtivaAjuizada;
        private Exercicio exercicioInicial;
        private Exercicio exercicioFinal;
        private Date vencimentoInicial;
        private Date vencimentoFinal;


        public FiltroDebito() {
        }

        public TipoCadastroTributario getTipoCadastroTributario() {
            return tipoCadastroTributario;
        }

        public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
            this.tipoCadastroTributario = tipoCadastroTributario;
        }

        public Cadastro getCadastro() {
            return cadastro;
        }

        public void setCadastro(Cadastro cadastro) {
            this.cadastro = cadastro;
        }

        public Pessoa getPessoa() {
            return pessoa;
        }

        public void setPessoa(Pessoa pessoa) {
            this.pessoa = pessoa;
        }

        public Divida getDivida() {
            return divida;
        }

        public void setDivida(Divida divida) {
            this.divida = divida;
        }

        public boolean isDoExercicio() {
            return doExercicio;
        }

        public void setDoExercicio(boolean doExercicio) {
            this.doExercicio = doExercicio;
        }

        public boolean isDividaAtiva() {
            return dividaAtiva;
        }

        public void setDividaAtiva(boolean dividaAtiva) {
            this.dividaAtiva = dividaAtiva;
        }

        public boolean isDividaAtivaAjuizada() {
            return dividaAtivaAjuizada;
        }

        public void setDividaAtivaAjuizada(boolean dividaAtivaAjuizada) {
            this.dividaAtivaAjuizada = dividaAtivaAjuizada;
        }

        public Exercicio getExercicioInicial() {
            return exercicioInicial;
        }

        public void setExercicioInicial(Exercicio exercicioInicial) {
            this.exercicioInicial = exercicioInicial;
        }

        public Exercicio getExercicioFinal() {
            return exercicioFinal;
        }

        public void setExercicioFinal(Exercicio exercicioFinal) {
            this.exercicioFinal = exercicioFinal;
        }

        public Date getVencimentoInicial() {
            return vencimentoInicial;
        }

        public void setVencimentoInicial(Date vencimentoInicial) {
            this.vencimentoInicial = vencimentoInicial;
        }

        public Date getVencimentoFinal() {
            return vencimentoFinal;
        }

        public void setVencimentoFinal(Date vencimentoFinal) {
            this.vencimentoFinal = vencimentoFinal;
        }

        public void processarSelecaoTipoCadastro() {
            cadastro = null;
            pessoa = null;
        }

        private void validarFiltrosNecessarios() {
            ValidacaoException ve = new ValidacaoException();
            if ((pessoa == null || pessoa.getId() == null)
                && (tipoCadastroTributario == null)
                && (cadastro == null || cadastro.getId() != null)
                && (divida == null || divida.getId() == null)
                && (exercicioInicial == null || exercicioInicial.getId() == null)
                && (exercicioFinal == null || exercicioFinal.getId() == null)
                && !dividaAtiva
                && !dividaAtivaAjuizada
                && !doExercicio) {
                ve.adicionarMensagemDeCampoObrigatorio("Por favor informe ao menos um filtro.");
            } else if (tipoCadastroTributario == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o tipo de cadastro.");
            } else if ((cadastro == null && pessoa == null)) {
                ve.adicionarMensagemDeCampoObrigatorio("Quando informado o tipo de cadastro, é necessário que seja informado também o cadastro.");
            } else if (exercicioInicial != null && exercicioFinal != null &&
                exercicioFinal.compareTo(exercicioInicial) < 0) {
                ve.adicionarMensagemDeCampoObrigatorio("O Exercício inicial não pode ser posterior ao exercício final.");
            } else if (exercicioInicial != null && exercicioFinal == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O Exercício final deve ser informado.");
            } else if (exercicioInicial == null && exercicioFinal != null) {
                ve.adicionarMensagemDeCampoObrigatorio("O Exercício inicial deve ser informado.");
            } else if (vencimentoInicial != null && vencimentoFinal != null && vencimentoInicial.compareTo(vencimentoFinal) > 0) {
                ve.adicionarMensagemDeCampoObrigatorio("A Data de Vencimento Final não pode ser maior que a Data de Vencimento Inicial.");
            }
            if (ve.temMensagens()) {
                throw ve;
            }
        }

    }
}
