package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteNotificacaoCobranca;
import br.com.webpublico.enums.AgrupadorCobrancaAdm;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoAcaoCobranca;
import br.com.webpublico.enums.TipoSituacaoProgramacaoCobranca;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ImprimeDAM;
import br.com.webpublico.negocios.NotificacaoCobrancaAdministrativaFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcConsultaDebitoIntegralDAO;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: Julio Cesar
 * Date: 29/11/13
 * Time: 09:28
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "notificacaoCobrancaAdministrativaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaNotificacaoCobranca", pattern = "/notificacao-cobranca-administrativa/novo/", viewId = "/faces/tributario/contacorrente/notificacaocobrancaadministrativa/edita.xhtml"),
    @URLMapping(id = "listarNotificacaoCobranca", pattern = "/notificacao-cobranca-administrativa/listar/", viewId = "/faces/tributario/contacorrente/notificacaocobrancaadministrativa/lista.xhtml"),
    @URLMapping(id = "verNotificacaoCobranca", pattern = "/notificacao-cobranca-administrativa/ver/#{notificacaoCobrancaAdministrativaControlador.id}/", viewId = "/faces/tributario/contacorrente/notificacaocobrancaadministrativa/visualizar.xhtml")
})
public class NotificacaoCobrancaAdministrativaControlador extends PrettyControlador<NotificacaoCobrancaAdmin> implements Serializable, CRUD {

    @EJB
    private NotificacaoCobrancaAdministrativaFacade facade;
    private ProgramacaoCobranca programacao;
    private Boolean mostrarTabelaAgrupada = false;
    private Boolean mostrarTabelaSemAgrupamento = true;
    private List<ResultadoParcela> resultadoConsulta;
    private ItemNotificacao itemSelecionado;
    private AssistenteNotificacaoCobranca assistenteCobranca;
    private Future<AssistenteNotificacaoCobranca> futureProcesso;
    private List<ItemNotificacao> itensNotificacao;
    private AssistenteBarraProgresso assistente;
    private Date dataAceite;
    private String observacoes;
    private Aceite aceiteSelecionado;

    public NotificacaoCobrancaAdministrativaControlador() {
        super(NotificacaoCobrancaAdmin.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/notificacao-cobranca-administrativa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novaNotificacaoCobranca", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        itensNotificacao = new ArrayList<>();
    }

    @URLAction(mappingId = "verNotificacaoCobranca", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        selecionado = facade.recarregarNotificacao(this.getId());
    }

    public ProgramacaoCobranca getProgramacao() {
        return programacao;
    }

    public void setProgramacao(ProgramacaoCobranca programacaoCobranca) {
        programacao = programacaoCobranca;
    }

    public List<ProgramacaoCobranca> completaProgramacao(String parte) {
        return facade.getProgramacaoCobrancaFacade().buscarListaProgramacaoSituacao(parte.trim(), TipoSituacaoProgramacaoCobranca.PROGRAMADO);
    }

    public List<ResultadoParcela> getResultadoConsulta() {
        return this.resultadoConsulta;
    }

    public void setResultadoConsulta(List<ResultadoParcela> resultadoConsulta) {
        this.resultadoConsulta = resultadoConsulta;
    }

    public List<SelectItem> agrupados() {
        List<SelectItem> items = new ArrayList<>();
        items.add(new SelectItem("", ""));
        for (AgrupadorCobrancaAdm agrupador : AgrupadorCobrancaAdm.values()) {
            items.add(new SelectItem(agrupador, agrupador.getDescricao()));
        }
        return items;
    }

    public List<SelectItem> tipoAcaoCobranca() {
        List<SelectItem> items = new ArrayList<>();
        items.add(new SelectItem("", ""));
        for (TipoAcaoCobranca tipo : TipoAcaoCobranca.values()) {
            items.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return items;
    }

    public Boolean getMostrarTabelaSemAgrupamento() {
        return mostrarTabelaSemAgrupamento;
    }

    public void setMostrarTabelaSemAgrupamento(Boolean mostrarTabelaSemAgrupamento) {
        this.mostrarTabelaSemAgrupamento = mostrarTabelaSemAgrupamento;
    }

    public Boolean getMostrarTabelaAgrupada() {
        return mostrarTabelaAgrupada;
    }

    public void setMostrarTabelaAgrupada(Boolean mostrarTabelaAgrupada) {
        this.mostrarTabelaAgrupada = mostrarTabelaAgrupada;
    }

    public Aceite getAceiteSelecionado() {
        return aceiteSelecionado;
    }

    public void setAceiteSelecionado(Aceite aceiteSelecionado) {
        this.aceiteSelecionado = aceiteSelecionado;
    }

    public void agruparDebitos() {
        try {
            programacao = programacao == null ? null : facade.getProgramacaoCobrancaFacade().recuperarSimples(programacao.getId());
            validarProgramacao(programacao);
            programacao.setNotificacaoCobrancaAdmin(selecionado);
            validarAgrupamentoNotificacao();
            assistente = new AssistenteBarraProgresso();
            futureProcesso = facade.agruparDebitos(selecionado, programacao, assistente);
            FacesUtil.executaJavaScript("openDialog(dlgSalvar)");
            FacesUtil.executaJavaScript("acompanharSalvar()");

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public String retornaCadastro(Cadastro cadastro) {
        if (cadastro instanceof CadastroEconomico) {
            CadastroEconomico ce = (CadastroEconomico) cadastro;
            return ce.getInscricaoCadastral() + "  -  " + ce.getPessoa().getNome() + " -  " + ce.getPessoa().getCpf_Cnpj();
        }
        if (cadastro instanceof CadastroImobiliario) {
            CadastroImobiliario ci = (CadastroImobiliario) cadastro;
            return ci.getInscricaoCadastral();
        }
        if (cadastro instanceof CadastroRural) {
            CadastroRural cr = (CadastroRural) cadastro;
            return cr.getDescricao();
        }
        return null;
    }

    public String retornaContribuinte(Pessoa pessoa) {
        if (pessoa instanceof PessoaFisica) {
            PessoaFisica pf = (PessoaFisica) pessoa;
            return "CPF.: " + (pf.getCpf() != null ? pf.getCpf() : "  -  ") + " Nome.: " + (pf.getNome() != null ? pf.getNome() : "  -  ");
        }
        if (pessoa instanceof PessoaJuridica) {
            PessoaJuridica pj = (PessoaJuridica) pessoa;
            return "CNPJ.: " + (pj.getCnpj() != null ? pj.getCnpj() : "  -  ") + " Nome.: " + (pj.getNome() != null ? pj.getNome() : "  -  ") + " Nome Fantasia.: " + (pj.getNomeFantasia() != null ? pj.getNomeFantasia() : "  -  ");
        }
        return null;
    }

    public void gerarDAM(ItemNotificacao item) {
        if (item != null) {
            try {
                List<DAM> dams = facade.getDamFacade().geraDAMs(item.parcelas());
                selecionado.setVencimentoDam(dams.get(0).getVencimento());
                for (DAM dam : dams) {
                    DocumentoNotificacaoDAM documentoNotificacaoDAM = new DocumentoNotificacaoDAM();
                    documentoNotificacaoDAM.setDocumentoNotificacao(item.getDocumentoNotificacao());
                    documentoNotificacaoDAM.setDam(dam);
                    item.getDocumentoNotificacao().getListaDocumentoNotificacaoDAM().add(documentoNotificacaoDAM);
                }
            } catch (Exception ex) {
                FacesUtil.addError("Erro ao emitir o Dam Cobrança administrativa!", ex.getMessage());
                logger.error(ex.getMessage());
            }
        }
    }

    public void imprimirDAMs(ItemNotificacao item) {
        try {
            if (item != null) {
                ImprimeDAM imprimeDAM = new ImprimeDAM();
                imprimeDAM.setGeraNoDialog(true);
                imprimeDAM.imprimirDamUnicoViaApi(facade.recuperaListaDAM(item));
            }
        } catch (Exception ex) {
            FacesUtil.atualizarComponente("Formulario");
        }
    }

    public void imprimirDamAgrupado(ItemNotificacao item) {
        try {
            if (item != null) {
                DAM dam = facade.recuperaListaDAM(item).get(0);
                FacesContext facesContext = FacesContext.getCurrentInstance();
                String caminho = ((ServletContext) facesContext.getExternalContext().getContext()).getRealPath("/WEB-INF/report/");
                caminho += "/";
                HashMap<String, Object> parametros = new HashMap<>();
                parametros.put("SUBREPORT_DIR", caminho);
                ImprimeDAM imprimeDAM = new ImprimeDAM();
                imprimeDAM.setGeraNoDialog(true);
                List<ResultadoParcela> parcelas = recuperarResultadoParcelaItemNotificacao(item);

                Pessoa pessoa = null;
                if (parcelas != null && !parametros.isEmpty()) {
                    if (AgrupadorCobrancaAdm.CADASTRO.equals(selecionado.getAgrupado())) {
                        JdbcConsultaDebitoIntegralDAO consultaDebitoDAO = (JdbcConsultaDebitoIntegralDAO) Util.getSpringBeanPeloNome("consultaDebitoIntegralDAO");
                        List<Pessoa> pessoas = consultaDebitoDAO.listarPessoasDoCalculoDoValorDivida(parcelas.get(0).getIdParcela());
                        if (pessoas != null && !pessoas.isEmpty()) {
                            pessoa = pessoas.get(0);
                        }
                    } else {
                        pessoa = item.getContribuinte();
                    }
                }
                imprimeDAM.imprimirDamCompostoViaApi(dam, pessoa);
            }
        } catch (Exception ex) {
            FacesUtil.atualizarComponente("Formulario");
        }
    }

    public void imprimirDocNotificacao(ItemNotificacao item) {
        if (item != null) {
            try {
                novoAssistenteCobranca();
                assistenteCobranca.setEmitirNotificacao(true);
                facade.gerarNotificacao(assistenteCobranca, item, selecionado.getTipoAcaoCobranca());
            } catch (Exception e) {
                FacesUtil.addError("Erro ao emitir a notificação Cobrança administrativa!", e.getMessage());
                logger.error(e.getMessage());
            }
        }
    }

    private void validarProgramacao(ProgramacaoCobranca programacaoCobranca) {
        ValidacaoException ve = new ValidacaoException();
        if (programacaoCobranca == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione uma Programação de Cobrança.");
        }
        ve.lancarException();
        if (selecionado.getAgrupado() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione o tipo de Agrupamento.");
        }
        ve.lancarException();
        if (facade.jaPossuiNotificacao(programacaoCobranca)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma notificação de cobrança para esta programação!");
        }
        ve.lancarException();
        if (programacaoCobranca.getUltimaSituacao() != null && programacaoCobranca.getUltimaSituacao().equals(TipoSituacaoProgramacaoCobranca.SIMULACAO)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Esta Programação não foi efetivada!");
        }
        ve.lancarException();
    }

    private void validarAgrupamentoNotificacao() {
        ValidacaoException ve = new ValidacaoException();
        if (programacao == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione uma Programação de Cobrança!");
        }
        ve.lancarException();
        if (programacao != null && programacao.getListaItemProgramacaoCobrancas().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Programação de Cobrança não possui débitos!");
        }
        if (programacao != null && programacao.getListaItemProgramacaoCobrancas().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Programação de Cobrança não possui débitos!");
        }
        ve.lancarException();
    }

    private void validarNotificacaoCobranca() {
        ValidacaoException ve = new ValidacaoException();
        if (facade.getParametroCobrancaAdministrativaFacade().parametrosCobrancaAdministrativaPorExercicio(facade.getSistemaFacade().getExercicioCorrente()) == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe um Parâmetro de Cobrança Administrativa vigente!");
        }
        ve.lancarException();
        if (selecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione uma programação!");
        }
        ve.lancarException();
        if (selecionado.getItemNotificacaoLista().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Realize um tipo de agrupamento!");
        }
        ve.lancarException();
        if (selecionado.getTipoAcaoCobranca() == null || selecionado.getTipoAcaoCobranca().getDescricao().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Ação de Cobrança!");
        }
        if (selecionado.getDamAgrupado() != null && selecionado.getDamAgrupado().equals(Boolean.TRUE) && selecionado.getVencimentoDam() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O vencimento do DAM Agrupado é obrigatório!");
        }
        if (selecionado.getVencimentoDam() != null && selecionado.getVencimentoDam().before(facade.getSistemaFacade().getDataOperacao())) {
            ve.adicionarMensagemDeCampoObrigatorio("O DAM Agrupado não pode ser gerado com o vencimento anterior a data atual!");
        }
        ve.lancarException();
    }

    public void gerarAceiteParaTodosOsItens(boolean salvar) {
        try {
            boolean fezAceite = false;
            validarAceite();
            for (ItemNotificacao item : selecionado.getItemNotificacaoLista()) {
                if (item.getAceite() == null) {
                    setItemSelecionado(null);
                    gerarAceiteParaItem(item, false);
                    fezAceite = true;
                }
            }
            if (salvar && fezAceite) {
                selecionado = facade.salvarMerge(selecionado);
                FacesUtil.addOperacaoRealizada("Itens da Notificação de Cobrança aceitos com sucesso!");
                FacesUtil.atualizarComponente("Formulario:tabelaAgrupada");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public boolean itemNotificacaoAceito(ItemNotificacao item) {
        return item.getAceite() != null;
    }

    public boolean todosItensAceito() {
        for (ItemNotificacao item : selecionado.getItemNotificacaoLista()) {
            if (item.getAceite() == null) {
                return true;
            }
        }
        return false;
    }

    public void gerarAceiteParaItem(ItemNotificacao item, boolean salvar) {
        try {
            validarAceite();
            Aceite criarAceite = criarAceite();
            if (itemSelecionado != null) {
                item = itemSelecionado;
            }
            item.setAceite(criarAceite);
            if (salvar) {
                facade.salvarItemNotificacao(item);
                FacesUtil.addOperacaoRealizada("Item aceito com sucesso!");
                FacesUtil.atualizarComponente("Formulario:tabelaAgrupada");
                dataAceite = null;
                observacoes = null;
                itemSelecionado = null;
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void selecionarItemNotificacao(ItemNotificacao item) {
        if (item != null) {
            itemSelecionado = item;
        }
    }

    private void validarAceite() {
        ValidacaoException ve = new ValidacaoException();
        if (dataAceite == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Aceite deve ser informado.");
        }
        ve.lancarException();
    }

    private Aceite criarAceite() {
        Aceite aceite = new Aceite();
        aceite.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        aceite.setDataOperacao(DataUtil.juntarDataEHora(dataAceite, new Date()));
        if (!Strings.isNullOrEmpty(observacoes)) {
            aceite.setObservacoes(observacoes);
        }
        return aceite;
    }

    @Override
    public void salvar() {
        try {
            validarNotificacaoCobranca();
            operacao = Operacoes.EDITAR;
            novoAssistenteBarraProgresso();
            novoAssistenteCobranca();
            futureProcesso = facade.salvarNotificacaoAdm(selecionado, assistente, assistenteCobranca);
            FacesUtil.executaJavaScript("openDialog(dlgSalvar)");
            FacesUtil.executaJavaScript("acompanharSalvar()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void novoAssistenteBarraProgresso() {
        assistente = new AssistenteBarraProgresso();
        assistente.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        assistente.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        assistente.setDataAtual(facade.getSistemaFacade().getDataOperacao());
    }

    private void novoAssistenteCobranca() {
        assistenteCobranca = new AssistenteNotificacaoCobranca(selecionado);
        assistenteCobranca.setFinalizarProcesso(true);
        assistenteCobranca.setDataOperacao(facade.getSistemaFacade().getDataOperacao());
        assistenteCobranca.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        assistenteCobranca.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        assistenteCobranca.setIp(facade.getSistemaFacade().getIp());
    }

    public List<ResultadoParcela> recuperarResultadoParcelaItemNotificacao(ItemNotificacao item) {
        if (item != null) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IN, getIdsParcela(item.parcelas()));
            consultaParcela.executaConsulta();
            resultadoConsulta = consultaParcela.getResultados();
        }
        return resultadoConsulta;
    }

    public List<ParcelaValorDivida> parcelas(List<ItemProgramacaoCobranca> list) {
        List<ParcelaValorDivida> retorno = new ArrayList<>();
        for (ItemProgramacaoCobranca item : list) {
            retorno.add(item.getParcelaValorDivida());
        }
        return retorno;
    }

    public List<Long> getIdsParcela(List<ParcelaValorDivida> parcela) {
        List<Long> ids = Lists.newArrayList();
        for (ParcelaValorDivida pvd : parcela) {
            ids.add(pvd.getId());
        }
        return ids;
    }

    public List<Long> getIdsParcelaNaProgramacao() {
        List<Long> idsParcela = Lists.newArrayList();
        for (ItemProgramacaoCobranca item : selecionado.getProgramacaoCobranca().getListaItemProgramacaoCobrancas()) {
            idsParcela.add(item.getParcelaValorDivida().getId());
        }
        return idsParcela;
    }

    public void novaConsultaParcela(List<Long> idsParcela) {
        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IN, idsParcela);
        consulta.executaConsulta();
        resultadoConsulta.addAll(consulta.getResultados());
    }

    public void consultarFutureSalvar() {
        if (futureProcesso != null && futureProcesso.isDone()) {
            try {
                AssistenteNotificacaoCobranca assistente = futureProcesso.get();
                if (assistente != null) {
                    assistenteCobranca = assistente;
                    selecionado = assistente.getSelecionado();
                    resultadoConsulta = assistente.getResultadosParcela();
                    FacesUtil.executaJavaScript("finalizarSalvar()");
                }
            } catch (Exception ex) {
                descobrirETratarException(ex);
            }
        }
    }

    public void finalizarFutureSalvar() {
        if (assistenteCobranca.getFinalizarProcesso()) {
            selecionado = assistenteCobranca.getSelecionado();
            redireciona(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } else {
            mostrarTabelaAgrupada = true;
            mostrarTabelaSemAgrupamento = false;
            futureProcesso = null;
            FacesUtil.executaJavaScript("closeDialog(dlgSalvar)");
            FacesUtil.atualizarComponente("Formulario:tableItens");
        }
    }

    public ItemNotificacao getItemSelecionado() {
        return itemSelecionado;
    }

    public void setItemSelecionado(ItemNotificacao itemSelecionado) {
        this.itemSelecionado = itemSelecionado;
    }


    public List<ItemNotificacao> getItensNotificacao() {
        return itensNotificacao;
    }

    public void setItensNotificacao(List<ItemNotificacao> itensNotificacao) {
        this.itensNotificacao = itensNotificacao;
    }

    public AssistenteBarraProgresso getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteBarraProgresso assistente) {
        this.assistente = assistente;
    }

    public AssistenteNotificacaoCobranca getAssistenteCobranca() {
        return assistenteCobranca;
    }

    public void setAssistenteCobranca(AssistenteNotificacaoCobranca assistenteCobranca) {
        this.assistenteCobranca = assistenteCobranca;
    }

    public Date getDataAceite() {
        return dataAceite;
    }

    public void setDataAceite(Date dataAceite) {
        this.dataAceite = dataAceite;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public void selecionarAceite(Aceite aceite) {
        this.aceiteSelecionado = (Aceite) Util.clonarObjeto(aceite);
    }
}
