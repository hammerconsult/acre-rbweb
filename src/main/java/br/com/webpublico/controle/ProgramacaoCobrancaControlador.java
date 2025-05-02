package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteProgramacaoCobranca;
import br.com.webpublico.entidadesauxiliares.DataTableConsultaDebito;
import br.com.webpublico.entidadesauxiliares.ProgramacaoCobrancaVO;
import br.com.webpublico.entidadesauxiliares.VOProgramacaoCobranca;
import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoSituacaoProgramacaoCobranca;
import br.com.webpublico.enums.tributario.TipoDamEmitido;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CadastroImobiliarioFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ProgramacaoCobrancaFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcConsultaDebitoIntegralDAO;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;

import static br.com.webpublico.controle.Web.limpaNavegacao;

@ManagedBean(name = "programacaoCobrancaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaProgramacao", pattern = "/programacaocobranca/novo/", viewId = "/faces/tributario/contacorrente/programacaodecobranca/edita.xhtml"),
    @URLMapping(id = "editarProgramacao", pattern = "/programacaocobranca/editar/#{programacaoCobrancaControlador.id}/", viewId = "/faces/tributario/contacorrente/programacaodecobranca/edita.xhtml"),
    @URLMapping(id = "listarProgramacao", pattern = "/programacaocobranca/listar/", viewId = "/faces/tributario/contacorrente/programacaodecobranca/lista.xhtml"),
    @URLMapping(id = "verProgramacao", pattern = "/programacaocobranca/ver/#{programacaoCobrancaControlador.id}/", viewId = "/faces/tributario/contacorrente/programacaodecobranca/visualizar.xhtml"),
})
public class ProgramacaoCobrancaControlador extends PrettyControlador<ProgramacaoCobranca> implements Serializable, CRUD {

    @EJB
    private ProgramacaoCobrancaFacade programacaoCobrancaFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    private JdbcConsultaDebitoIntegralDAO consultaDebitoIntegralDAO;
    private DataTableConsultaDebito[] parcelas;
    private Map<String, BigDecimal> totaisSituacao;
    private TotalTabelaParcelas totalTabelaParcelas;
    private List<ResultadoParcela> debitoSelecionados;
    private List<ResultadoParcela> resultadosConsulta;
    private CNAE cnae;
    private Divida divida;
    private Bairro bairro;
    private Logradouro logradouro;
    private ClassificacaoAtividade atividade;
    private Pessoa contribuinte;
    private Servico servico;
    private List<Pessoa> pessoas;
    private List<ProgramacaoCobrancaVO> cobrancaVOS;
    private Future<AssistenteProgramacaoCobranca> futureProgramacao;
    private AssistenteProgramacaoCobranca assistenteProgramacao;

    public ProgramacaoCobrancaControlador() {
        super(ProgramacaoCobranca.class);
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        this.consultaDebitoIntegralDAO = (JdbcConsultaDebitoIntegralDAO) ap.getBean("consultaDebitoIntegralDAO");
    }

    @URLAction(mappingId = "novaProgramacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        selecionado = (ProgramacaoCobranca) Web.pegaDaSessao(ProgramacaoCobranca.class);
        if (selecionado == null) {
            super.novo();
            totalTabelaParcelas = new TotalTabelaParcelas();
            totaisSituacao = Maps.newHashMap();
            selecionado.setUsuarioSistema(programacaoCobrancaFacade.getSistemaFacade().getUsuarioCorrente());
            selecionado.setExercicio(programacaoCobrancaFacade.getSistemaFacade().getExercicioCorrente());
            selecionado.setNumero(null);
            selecionado.getListaSituacaoProgramacaoCobrancas().add(new SituacaoProgramacaoCobranca(selecionado, TipoSituacaoProgramacaoCobranca.SIMULACAO, programacaoCobrancaFacade.getSistemaFacade().getDataOperacao()));
            limparCampos();
        } else {
            debitoSelecionados = selecionado.getDebitoSelecionados();
        }
    }

    @URLAction(mappingId = "verProgramacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarProgramacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarParcelasItemProgramacaoCobranca();
    }

    @Override
    public void salvar() {
        try {
            validarProgramacao();
            selecionado.getListaItemProgramacaoCobrancas().clear();
            programacaoCobrancaFacade.gerarItemProgramacaoCobranca(selecionado, debitoSelecionados);
            limpaNavegacao();
            selecionado = programacaoCobrancaFacade.salvarProgramacao(selecionado);
            redirecionarVer();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void redirecionarVer() {
        FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    public ProgramacaoCobrancaFacade getFacade() {
        return programacaoCobrancaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/programacaocobranca/";
    }

    @Override
    public AbstractFacade getFacede() {
        return programacaoCobrancaFacade;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    public CNAE getCnae() {
        return cnae;
    }

    public void setCnae(CNAE cnae) {
        this.cnae = cnae;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public ClassificacaoAtividade getAtividade() {
        return atividade;
    }

    public void setAtividade(ClassificacaoAtividade atividade) {
        this.atividade = atividade;
    }

    public Pessoa getContribuinte() {
        return contribuinte;
    }

    public void setContribuinte(Pessoa contribuinte) {
        this.contribuinte = contribuinte;
    }

    public DataTableConsultaDebito[] getParcelas() {
        return parcelas;
    }

    public void setParcelas(DataTableConsultaDebito[] parcelas) {
        this.parcelas = parcelas;
    }

    public TotalTabelaParcelas getTotalTabelaParcelas() {
        return totalTabelaParcelas;
    }

    public void setTotalTabelaParcelas(TotalTabelaParcelas totalTabelaParcelas) {
        this.totalTabelaParcelas = totalTabelaParcelas;
    }

    public List<ResultadoParcela> getResultadosConsulta() {
        return resultadosConsulta;
    }

    public List<SelectItem> getClassificacaoAtividade() {
        return Util.getListSelectItem(ClassificacaoAtividade.values());
    }

    public List<SelectItem> getDividas() {
        return Util.getListSelectItem(programacaoCobrancaFacade.getDividaFacade().listaDividasPorTipoCadastro(selecionado.getTipoCadastro()));
    }

    private void validarDivida() {
        ValidacaoException ve = new ValidacaoException();
        if (divida == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione uma dívida para adicionar.");
        }
        if (selecionado.getDividas() != null && !selecionado.getDividas().isEmpty()) {
            for (ProgramacaoCobrancaDivida pDivida : selecionado.getDividas()) {
                if (pDivida.getDivida().equals(divida)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Essa dívida já foi adicionada.");
                }
            }
        }
        ve.lancarException();
    }

    private void validarBairro() {
        ValidacaoException ve = new ValidacaoException();
        if (bairro == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um bairro para adicionar.");
        }
        if (selecionado.getBairros() != null && !selecionado.getBairros().isEmpty()) {
            for (ProgramacaoCobrancaBairro pBairro : selecionado.getBairros()) {
                if (pBairro.getBairro().equals(bairro)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Esse bairro já foi adicionado.");
                }
            }
        }
        ve.lancarException();
    }

    private void validarLogradouro() {
        ValidacaoException ve = new ValidacaoException();
        if (logradouro == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um logradouro para adicionar.");
        }
        if (selecionado.getLogradouros() != null && !selecionado.getLogradouros().isEmpty()) {
            for (ProgramacaoCobrancaLogradouro pLogradouro : selecionado.getLogradouros()) {
                if (pLogradouro.getLogradouro().equals(logradouro)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Esse logradouro já foi adicionado.");
                }
            }
        }
        ve.lancarException();
    }

    private void validarClassificacaoDaAtividade() {
        ValidacaoException ve = new ValidacaoException();
        if (atividade == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione uma classificacao de atividade para adicionar.");
        }
        if (selecionado.getAtividades() != null && !selecionado.getAtividades().isEmpty()) {
            for (ProgramacaoCobrancaClassificacaoAtividade pAtividade : selecionado.getAtividades()) {
                if (pAtividade.getClassificacaoAtividade().equals(atividade)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Essa classificacao de atividade já foi adicionada.");
                }
            }
        }
        ve.lancarException();
    }

    private void validarCnae() {
        ValidacaoException ve = new ValidacaoException();
        if (cnae == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um CNAE para adicionar.");
        }
        if (selecionado.getCnaes() != null && !selecionado.getCnaes().isEmpty()) {
            for (ProgramacaoCobrancaCNAE pCnae : selecionado.getCnaes()) {
                if (pCnae.getCnae().equals(cnae)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O CNAE já foi adicionado na lista.");
                }
            }
        }
        ve.lancarException();
    }

    private void validarServico() {
        ValidacaoException ve = new ValidacaoException();
        if (servico == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um servico para adicionar.");
        }
        if (selecionado.getServicos() != null && !selecionado.getServicos().isEmpty()) {
            for (ProgramacaoCobrancaServico pServico : selecionado.getServicos()) {
                if (pServico.getServico().equals(servico)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Serviço já foi adicionado na lista.");
                }
            }
        }
        ve.lancarException();
    }

    private void validarPessoa() {
        ValidacaoException ve = new ValidacaoException();
        if (contribuinte == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um Contribuinte para adicionar.");
        }
        if (selecionado.getContribuintes() != null && !selecionado.getContribuintes().isEmpty()) {
            for (ProgramacaoCobrancaPessoa pContribuinte : selecionado.getContribuintes()) {
                if (pContribuinte.getContribuinte().equals(contribuinte)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Contribuinte já foi adiiconado na lista.");
                }
            }
        }
        ve.lancarException();
    }

    public void adicionarDivida() {
        try {
            validarDivida();
            ProgramacaoCobrancaDivida pDivida = new ProgramacaoCobrancaDivida();
            pDivida.setDivida(divida);
            pDivida.setProgramacaoCobranca(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getDividas(), pDivida);
            divida = new Divida();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void adicionarBairro() {
        try {
            validarBairro();
            ProgramacaoCobrancaBairro pBairro = new ProgramacaoCobrancaBairro();
            pBairro.setBairro(bairro);
            pBairro.setProgramacaoCobranca(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getBairros(), pBairro);
            bairro = new Bairro();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarLogradouro() {
        try {
            validarLogradouro();
            ProgramacaoCobrancaLogradouro pLogradouro = new ProgramacaoCobrancaLogradouro();
            pLogradouro.setLogradouro(logradouro);
            pLogradouro.setProgramacaoCobranca(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getLogradouros(), pLogradouro);
            logradouro = new Logradouro();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarClassificacaoAtividade() {
        try {
            validarClassificacaoDaAtividade();
            ProgramacaoCobrancaClassificacaoAtividade pAtividade = new ProgramacaoCobrancaClassificacaoAtividade();
            pAtividade.setClassificacaoAtividade(atividade);
            pAtividade.setProgramacaoCobranca(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getAtividades(), pAtividade);
            atividade = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarCNAE() {
        try {
            validarCnae();
            ProgramacaoCobrancaCNAE pCnae = new ProgramacaoCobrancaCNAE();
            pCnae.setCnae(cnae);
            pCnae.setProgramacaoCobranca(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getCnaes(), pCnae);
            cnae = new CNAE();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarServico() {
        try {
            validarServico();
            ProgramacaoCobrancaServico pServico = new ProgramacaoCobrancaServico();
            pServico.setServico(servico);
            pServico.setProgramacaoCobranca(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getServicos(), pServico);
            servico = new Servico();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarContribuinte() {
        try {
            validarPessoa();
            ProgramacaoCobrancaPessoa pContribuinte = new ProgramacaoCobrancaPessoa();
            pContribuinte.setContribuinte(contribuinte);
            pContribuinte.setProgramacaoCobranca(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getContribuintes(), pContribuinte);
            if (contribuinte instanceof PessoaFisica) {
                contribuinte = new PessoaFisica();
            } else {
                contribuinte = new PessoaJuridica();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerCNAE(ProgramacaoCobrancaCNAE cnae) {
        selecionado.getCnaes().remove(cnae);
    }

    public void removerServico(ProgramacaoCobrancaServico servico) {
        selecionado.getServicos().remove(servico);
    }

    public void removerDivida(ProgramacaoCobrancaDivida divida) {
        selecionado.getDividas().remove(divida);
    }

    public void removerBairro(ProgramacaoCobrancaBairro bairro) {
        selecionado.getBairros().remove(bairro);
    }

    public void removerLogradouro(ProgramacaoCobrancaLogradouro logradouro) {
        selecionado.getLogradouros().remove(logradouro);
    }

    public void removerClassificacaoAtividade(ProgramacaoCobrancaClassificacaoAtividade classificacaoAtividade) {
        selecionado.getAtividades().remove(classificacaoAtividade);
    }

    public void removerContribuinte(ProgramacaoCobrancaPessoa contribuinte) {
        selecionado.getContribuintes().remove(contribuinte);
    }

    public List<SelectItem> getTiposDeCadastroTributario() {
        return Util.getListSelectItem(TipoCadastroTributario.getTodosSemRural());
    }

    public void gerarRelatorio() {
        try {
            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            String arquivoJasper = "DemonstrativoDebitosSelecionados.jasper";
            HashMap parameters = Maps.newHashMap();
            abstractReport.setGeraNoDialog(true);
            parameters.put("MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO");
            parameters.put("BRASAO", abstractReport.getCaminhoImagem());
            parameters.put("MODULO", "Secretaria Municipal de Finanças");
            parameters.put("NOMERELATORIO", "Relatório Demonstrativo de Débitos");
            parameters.put("USER", selecionado.getUsuarioSistema().getNome());
            parameters.put("FILTROS", selecionado.getFiltros());
            parameters.put("NUMEROPROGRAMACAO", selecionado.getNumero());
            parameters.put("DATAPROGRAMACAO", selecionado.getDataProgramacaoCobranca());
            parameters.put("PROTOCOLO", selecionado.getNumeroAnoProtocolo());
            JRBeanCollectionDataSource jbs = new JRBeanCollectionDataSource(buscarDadosRelatorio());
            abstractReport.gerarRelatorioComDadosEmCollection(arquivoJasper, parameters, jbs);
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio("Não foi possível gerar o relatório " + e.getMessage());
        }
    }

    private List<VOProgramacaoCobranca> buscarDadosRelatorio() {
        List<VOProgramacaoCobranca> retorno = Lists.newArrayList();
        for (ResultadoParcela item : getDebitoSelecionados()) {
            VOProgramacaoCobranca prog = new VOProgramacaoCobranca();
            prog.setCadastro(item.getCadastro());
            prog.setContribuinte(pessoasDoCalculoDoValorDivida(item).replace("<br/>", ""));
            prog.setDivida(item.getDivida());
            prog.setExercicio(item.getExercicio().toString());
            prog.setTipoDeDebito(item.getTipoDeDebito());
            prog.setParcela(item.getParcela());
            prog.setSd(item.getSd().toString());
            prog.setVencimento(item.getVencimentoToString());
            prog.setValor(item.getValorOriginal());
            prog.setImposto(item.getValorImposto());
            prog.setTaxa(item.getValorTaxa());
            prog.setDesconto(item.getValorDesconto());
            prog.setJuros(item.getValorJuros());
            prog.setMulta(item.getValorMulta());
            prog.setCorrecao(item.getValorCorrecao());
            prog.setHonorarios(item.getValorHonorarios());
            prog.setTotal(item.getValorTotal());
            prog.setSituacao(item.getSituacaoDescricaoEnum());
            retorno.add(prog);
        }
        return retorno;
    }

    private String pessoasDoCalculoDoValorDivida(Long id) {
        return consultaDebitoIntegralDAO.pessoasDoCalculoDoValorDivida(id);
    }

    public void limparCadastro() {
        limparCampos();
        if (selecionado.getTipoCadastro() != null) {
            selecionado.setCadastroInicial("1");
            if (selecionado.getTipoCadastro().equals(TipoCadastroTributario.ECONOMICO)) {
                selecionado.setCadastroFinal("9999999");
            } else {
                selecionado.setCadastroFinal("999999999999999");
            }
        }
    }

    public void copiarCadastroInicialParaCadastroFinal() {
        selecionado.setCadastroFinal(selecionado.getCadastroInicial());
    }

    private void limparCampos() {
        inicializarListas();
        totalTabelaParcelas = new TotalTabelaParcelas();
        logradouro = new Logradouro();
        bairro = new Bairro();
        cnae = new CNAE();
        atividade = null;
        Util.limparCampos(selecionado);
    }

    private void inicializarListas() {
        selecionado.setCnaes(Lists.<ProgramacaoCobrancaCNAE>newArrayList());
        selecionado.setServicos(Lists.<ProgramacaoCobrancaServico>newArrayList());
        selecionado.setBairros(Lists.<ProgramacaoCobrancaBairro>newArrayList());
        selecionado.setLogradouros(Lists.<ProgramacaoCobrancaLogradouro>newArrayList());
        selecionado.setAtividades(Lists.<ProgramacaoCobrancaClassificacaoAtividade>newArrayList());
        selecionado.setDividas(Lists.<ProgramacaoCobrancaDivida>newArrayList());
        selecionado.setContribuintes(Lists.<ProgramacaoCobrancaPessoa>newArrayList());
        resultadosConsulta = Lists.newArrayList();
        debitoSelecionados = Lists.newArrayList();
        cobrancaVOS = Lists.newArrayList();
    }

    public void pesquisar() {
        try {
            retornarCadastrosTipoTributario();
            validarCampos();
            AssistenteProgramacaoCobranca assistente = new AssistenteProgramacaoCobranca(selecionado);
            assistente.setResultadosParcela(resultadosConsulta);
            assistente.setCobrancaVOS(cobrancaVOS);
            assistente.setPessoas(pessoas);
            assistente.setDataOperacao(programacaoCobrancaFacade.getSistemaFacade().getDataOperacao());
            futureProgramacao = programacaoCobrancaFacade.buscarDebitosProgramacaoCobranca(assistente, totaisSituacao, totalTabelaParcelas);
            FacesUtil.executaJavaScript("statusDialog.show()");
            FacesUtil.executaJavaScript("acompanharPesquisar()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void retornarCadastrosTipoTributario() {

        resultadosConsulta = Lists.newArrayList();
        debitoSelecionados = Lists.newArrayList();
        totalTabelaParcelas = new TotalTabelaParcelas();
        pessoas = Lists.newArrayList();
        cobrancaVOS = Lists.newArrayList();

        if (TipoCadastroTributario.IMOBILIARIO.equals(selecionado.getTipoCadastro())) {
            cobrancaVOS.addAll(programacaoCobrancaFacade.recuperaCadastroImobiliarioFaixadeLote(selecionado));
        }
        if (TipoCadastroTributario.ECONOMICO.equals(selecionado.getTipoCadastro())) {
            cobrancaVOS.addAll(programacaoCobrancaFacade.recuperaCadastroEconomico(selecionado));
        }
        if (TipoCadastroTributario.RURAL.equals(selecionado.getTipoCadastro())) {
            cobrancaVOS.addAll(programacaoCobrancaFacade.recuperaCadastroRural(selecionado));
        }
        if (TipoCadastroTributario.PESSOA.equals(selecionado.getTipoCadastro())) {
            if (selecionado.getContribuintes() != null && !selecionado.getContribuintes().isEmpty()) {
                for (ProgramacaoCobrancaPessoa contribuinte : selecionado.getContribuintes()) {
                    pessoas.add(contribuinte.getContribuinte());
                }
            }
        }
    }

    public void consultarFuturePesquisar() {
        if (futureProgramacao != null && futureProgramacao.isDone()) {
            try {
                AssistenteProgramacaoCobranca assistente = futureProgramacao.get();
                assistenteProgramacao = assistente;
                selecionado = assistenteProgramacao.getSelecionado();
                resultadosConsulta = assistente.getResultadosParcela();
                FacesUtil.executaJavaScript("finalizarPesquisar()");
            } catch (Exception ex) {
                descobrirETratarException(ex);
            }
        }
    }

    public void finalizarFuturePesquisar() {
        futureProgramacao = null;
        if (resultadosConsulta != null && resultadosConsulta.isEmpty()) {
            FacesUtil.addAtencao( "Nenhuma registro de débito encontrado!");
        }
        FacesUtil.executaJavaScript("statusDialog.hide()");
    }

    private void adicionarValores(List<ResultadoParcela> resultadosParcela) {
        for (ResultadoParcela resultado : resultadosParcela) {
            BigDecimal total = resultado.getValorTotal();
            String situacaoParaProcessamento = resultado.getSituacaoParaProcessamento(programacaoCobrancaFacade.getSistemaFacade().getDataOperacao());
            if (totaisSituacao.containsKey(situacaoParaProcessamento)) {
                total = totaisSituacao.get(situacaoParaProcessamento);
                total = total.add(resultado.getValorTotal());
            }
            totaisSituacao.put(situacaoParaProcessamento, total);
            totalTabelaParcelas.soma(resultado);
        }
    }

    private void recuperarParcelasItemProgramacaoCobranca() {
        totaisSituacao = Maps.newHashMap();
        try {
            debitoSelecionados = Lists.newArrayList();
            novaConsultaParcela(getIdsParcelaDaProgramacao());
            debitoSelecionados.addAll(resultadosConsulta);
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", e.getMessage()));
        }

        logger.debug("Tamanho da lista: " + debitoSelecionados.size());

        totalTabelaParcelas = new TotalTabelaParcelas();
        if (debitoSelecionados != null && !debitoSelecionados.isEmpty()) {
            adicionarValores(debitoSelecionados);
        }
        Collections.sort(debitoSelecionados, ResultadoParcela.getComparatorByValuePadrao());
    }

    private void novaConsultaParcela(List<Long> ids) {
        if (resultadosConsulta == null) {
            resultadosConsulta = Lists.newArrayList();
        }
        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IN, ids);
        consulta.executaConsulta();
        resultadosConsulta.addAll(consulta.getResultados());
    }

    public Date getDataOperacao() {
        return programacaoCobrancaFacade.getSistemaFacade().getDataOperacao();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (hasCamposVazios()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos um filtro dos campos Exercício, Vencimento ou Valor para realizar a consulta!");
        }
        if (!hasCamposVazios()) {
            if (!hasCadastrosOuPessoasAtravesDosfitrosdaPesquisa()) {
                ve.adicionarMensagemDeOperacaoNaoRealizada("Nenhum registro encontrado através dos filtros selecionados.");
            }
        }
        ve.lancarException();
    }

    private boolean hasCamposVazios() {
        return (selecionado.getExercicioInicial() == null) && (selecionado.getExercicioFinal() == null)
            && (selecionado.getVencimentoInicial() == null) && (selecionado.getVencimentoFinal() == null)
            && (selecionado.getValorInicial() == null) && (selecionado.getValorFinal() == null)
            && (selecionado.getTipoDamEmitido() == null);
    }

    public void removerItem(ItemProgramacaoCobranca item) {
        selecionado.getListaItemProgramacaoCobrancas().remove(item);
    }

    private List<Long> getIdsParcelaDaProgramacao() {
        List<Long> ids = Lists.newArrayList();
        for (ItemProgramacaoCobranca item : selecionado.getListaItemProgramacaoCobrancas()) {
            if (!ids.contains(item.getParcelaValorDivida().getId())) {
                ids.add(item.getParcelaValorDivida().getId());
            }
        }
        return ids;
    }

    public void efetivarProgramacao() {
        try {
            validarProgramacao();
            if (selecionado.getListaItemProgramacaoCobrancas().isEmpty() && !debitoSelecionados.isEmpty()) {
                programacaoCobrancaFacade.gerarItemProgramacaoCobranca(selecionado, debitoSelecionados);
            }
            selecionado.getListaSituacaoProgramacaoCobrancas().add(new SituacaoProgramacaoCobranca(selecionado, TipoSituacaoProgramacaoCobranca.PROGRAMADO, programacaoCobrancaFacade.getSistemaFacade().getDataOperacao()));
            selecionado.setUnidadeOrganizacional(programacaoCobrancaFacade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
            programacaoCobrancaFacade.salvarProgramacao(selecionado);
            limpaNavegacao();
            redireciona();
            FacesUtil.addInfo("Operação Realizada!", "Efetivação realizada com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarProgramacao() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(selecionado.getDescricao())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe uma descrição para a programação de cobrança.");
        }

        if (debitoSelecionados == null || debitoSelecionados.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A programação não possui débitos selecionados ! Realize uma consulta para selecionar os débitos.");
        }
        ve.lancarException();
    }

    @Override
    public void cancelar() {
        if (!isSessao()) {
            limpaNavegacao();
        }
        super.cancelar();
    }

    public void selecionarTodasParcelas() {
        if (resultadosConsulta != null && !resultadosConsulta.isEmpty()) {
            debitoSelecionados = resultadosConsulta;
        } else {
            FacesUtil.addError("Atenção!", "Realize uma consulta para selecionar todos os débitos");
        }
    }

    private boolean hasCadastrosOuPessoasAtravesDosfitrosdaPesquisa() {
        return !cobrancaVOS.isEmpty() || !pessoas.isEmpty();
    }

    public List<CNAE> completarCNAE(String parte) {
        return programacaoCobrancaFacade.getCnaeFacade().listaCnaesAtivos(parte.trim());
    }

    public List<Servico> completarServico(String parte) {
        return programacaoCobrancaFacade.getServicoFacade().completaServico(parte.trim());
    }

    public List<Bairro> completarBairro(String parte) {
        return programacaoCobrancaFacade.getBairroFacade().completaBairro(parte.trim());
    }

    public List<Logradouro> completarLogradouro(String parte) {
        return programacaoCobrancaFacade.getLogradouroFacade().listaLogradourosAtivos(parte.trim());
    }

    public List<Setor> completarSetor(String parte) {
        return programacaoCobrancaFacade.getSetorFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<Lote> completarLote(String parte) {
        return programacaoCobrancaFacade.getLoteFacade().listaFiltrando(parte.trim(), "codigoLote");
    }

    public List<Quadra> completarQuadra(String parte) {
        return programacaoCobrancaFacade.getQuadraFacade().listaFiltrando(parte.trim(), "codigo");
    }

    public boolean mostrarBotaoSelecionarTodos() {
        return debitoSelecionados.size() != resultadosConsulta.size();
    }

    public void desmarcarTodos() {
        debitoSelecionados.clear();
    }

    public void selecionarTodos() {
        desmarcarTodos();
        debitoSelecionados.addAll(resultadosConsulta);
    }

    public boolean mostrarBotaoSelecionarObjeto(ResultadoParcela obj) {
        return !debitoSelecionados.contains(obj);
    }

    public void desmarcarObjeto(ResultadoParcela obj) {
        debitoSelecionados.remove(obj);
    }

    public void selecionarObjeto(ResultadoParcela obj) {
        debitoSelecionados.add(obj);
    }

    public List<ResultadoParcela> getDebitoSelecionados() {
        return debitoSelecionados;
    }

    public void setDebitoSelecionados(List<ResultadoParcela> debitoSelecionados) {
        this.debitoSelecionados = debitoSelecionados;
    }

    public class TotalTabelaParcelas implements Serializable {
        private BigDecimal totalValor, totalImposto, totalTaxa, totalDesconto, totalJuros, totalMulta, totalCorrecao, totalGeral, totalHonorarios;

        public TotalTabelaParcelas() {
            totalValor = BigDecimal.ZERO;
            totalImposto = BigDecimal.ZERO;
            totalTaxa = BigDecimal.ZERO;
            totalDesconto = BigDecimal.ZERO;
            totalJuros = BigDecimal.ZERO;
            totalCorrecao = BigDecimal.ZERO;
            totalMulta = BigDecimal.ZERO;
            totalHonorarios = BigDecimal.ZERO;
            totalGeral = BigDecimal.ZERO;
        }

        public void soma(ResultadoParcela resultado) {
            totalValor = totalValor.add(resultado.getValorOriginal());
            totalImposto = totalImposto.add(resultado.getValorImposto());
            totalTaxa = totalTaxa.add(resultado.getValorTaxa());
            totalCorrecao = totalCorrecao.add(resultado.getValorCorrecao());
            totalDesconto = totalDesconto.add(resultado.getValorDesconto());
            totalJuros = totalJuros.add(resultado.getValorJuros());
            totalMulta = totalMulta.add(resultado.getValorMulta());
            totalHonorarios = totalHonorarios.add(resultado.getValorHonorarios());
            totalGeral = totalGeral.add(resultado.getValorTotal());
        }

        public BigDecimal getTotalValor() {
            return totalValor;
        }

        public BigDecimal getTotalImposto() {
            return totalImposto;
        }

        public BigDecimal getTotalTaxa() {
            return totalTaxa;
        }

        public BigDecimal getTotalDesconto() {
            return totalDesconto;
        }

        public BigDecimal getTotalJuros() {
            return totalJuros;
        }

        public BigDecimal getTotalHonorarios() {
            return totalHonorarios;
        }

        public BigDecimal getTotalMulta() {
            return totalMulta;
        }

        public BigDecimal getTotalGeral() {
            return totalGeral;
        }

        public BigDecimal getTotalCorrecao() {
            return totalCorrecao;
        }
    }

    public List<SelectItem> getTiposDamsEmitidos() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        for (TipoDamEmitido tipo : TipoDamEmitido.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public String pessoasDoCalculoDoValorDivida(ResultadoParcela resultadoParcela) {
        if (TipoCadastroTributario.IMOBILIARIO.name().equals(resultadoParcela.getTipoCadastroTributarioEnumValue().name())) {
            return cadastroImobiliarioFacade.recuperarProprietarioPrincipal(resultadoParcela.getIdCadastro()).getPessoa().getNomeCpfCnpj();
        }
        return pessoasDoCalculoDoValorDivida(resultadoParcela.getId());
    }
}
