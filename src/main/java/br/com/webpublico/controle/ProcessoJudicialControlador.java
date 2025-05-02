package br.com.webpublico.controle;

import br.com.webpublico.entidades.CertidaoDividaAtiva;
import br.com.webpublico.entidades.ProcessoJudicial;
import br.com.webpublico.entidades.ProcessoJudicialCDA;
import br.com.webpublico.entidades.ProcessoJudicialExtincao;
import br.com.webpublico.entidades.tributario.procuradoria.*;
import br.com.webpublico.entidadesauxiliares.AssistenteDetentorArquivoComposicao;
import br.com.webpublico.entidadesauxiliares.datajud.RegistroDatajud;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.tributario.procuradoria.PapelProcesso;
import br.com.webpublico.enums.tributario.procuradoria.TipoAcaoProcessoJudicial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ComunicaSofPlanFacade;
import br.com.webpublico.negocios.ProcessoJudicialFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.ws.procuradoria.IntegraSoftplanFacade;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "processoJudicialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarProcessoJudicial",
        pattern = "/processo-judicial/listar/",
        viewId = "/faces/tributario/dividaativa/processojudicial/lista.xhtml"),
    @URLMapping(id = "novoProcessoJudicial",
        pattern = "/processo-judicial/novo/",
        viewId = "/faces/tributario/dividaativa/processojudicial/edita.xhtml"),
    @URLMapping(id = "verProcessoJudicial",
        pattern = "/processo-judicial/ver/#{processoJudicialControlador.id}/",
        viewId = "/faces/tributario/dividaativa/processojudicial/visualizar.xhtml"),
    @URLMapping(id = "editaProcessoJudicial",
        pattern = "/processo-judicial/editar/#{processoJudicialControlador.id}/",
        viewId = "/faces/tributario/dividaativa/processojudicial/edita.xhtml")
})
public class ProcessoJudicialControlador extends PrettyControlador<ProcessoJudicialCDA> implements Serializable, CRUD {

    @EJB
    private ProcessoJudicialFacade processoJudicialFacade;
    private ParametroProcuradoria parametroProcuradoria;
    private List<CertidaoDividaAtiva> certidoesDoProcesso;
    private ConverterAutoComplete converterVara;
    private ConverterGenerico converterSituacaoTramiteJudicial;
    private ConverterAutoComplete converterCertidaoDividaAtiva;
    private TramiteProcessoJudicial tramite;
    private ProcessoJudicial processoJudicial;
    private AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao;
    private CertidaoDividaAtiva certidao;
    private PessoaProcesso pessoaProcesso;
    private ProcessoJudicialExtincao processoJudicialExtincao;
    private List<RegistroDatajud> registrosDatajud;

    public ProcessoJudicialControlador() {
        super(ProcessoJudicialCDA.class);
        tramite = new TramiteProcessoJudicial();
        pessoaProcesso = new PessoaProcesso();
    }

    public List<RegistroDatajud> getRegistrosDatajud() {
        return registrosDatajud;
    }

    public void setRegistrosDatajud(List<RegistroDatajud> registrosDatajud) {
        this.registrosDatajud = registrosDatajud;
    }

    public ParametroProcuradoria getParametroProcuradoria() {
        return parametroProcuradoria;
    }

    public void setParametroProcuradoria(ParametroProcuradoria parametroProcuradoria) {
        this.parametroProcuradoria = parametroProcuradoria;
    }

    public TramiteProcessoJudicial getTramite() {
        return tramite;
    }

    public void setTramite(TramiteProcessoJudicial tramite) {
        this.tramite = tramite;
    }

    public Converter getConverterVara() {
        if (converterVara == null) {
            converterVara = new ConverterAutoComplete(Vara.class, processoJudicialFacade.getVaraFacade());
        }
        return converterVara;
    }

    public Converter getConverterSituacaoTramiteJudicial() {
        if (converterSituacaoTramiteJudicial == null) {
            converterSituacaoTramiteJudicial = new ConverterGenerico(SituacaoTramiteJudicial.class, processoJudicialFacade.getSituacaoTramiteJudicialFacade());
        }
        return converterSituacaoTramiteJudicial;
    }

    public PessoaProcesso getPessoaProcesso() {
        return pessoaProcesso;
    }

    public void setPessoaProcesso(PessoaProcesso pessoaProcesso) {
        this.pessoaProcesso = pessoaProcesso;
    }

    public ConverterAutoComplete getConverterCertidaoDividaAtiva() {
        if (converterCertidaoDividaAtiva == null) {
            converterCertidaoDividaAtiva = new ConverterAutoComplete(CertidaoDividaAtiva.class, processoJudicialFacade.getCertidaoDividaAtivaFacade());
        }
        return converterCertidaoDividaAtiva;
    }

    public CertidaoDividaAtiva getCertidao() {
        return certidao;
    }

    public void setCertidao(CertidaoDividaAtiva certidao) {
        this.certidao = certidao;
    }

    public AssistenteDetentorArquivoComposicao getAssistenteDetentorArquivoComposicao() {
        return assistenteDetentorArquivoComposicao;
    }

    public void setAssistenteDetentorArquivoComposicao(AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao) {
        this.assistenteDetentorArquivoComposicao = assistenteDetentorArquivoComposicao;
    }

    public ProcessoJudicial getProcessoJudicial() {
        return processoJudicial;
    }

    public void setProcessoJudicial(ProcessoJudicial processoJudicial) {
        this.processoJudicial = processoJudicial;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/processo-judicial/";
    }

    @Override
    public AbstractFacade getFacede() {
        return processoJudicialFacade;
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "verProcessoJudicial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        buscarExtincao();
    }

    @URLAction(mappingId = "editaProcessoJudicial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        selecionado = processoJudicialFacade.recuperarProcessoJudicialCDA(getId());
        processoJudicial = selecionado.getProcessoJudicial();
        certidoesDoProcesso = Lists.newArrayList();
        assistenteDetentorArquivoComposicao = new AssistenteDetentorArquivoComposicao(selecionado.getProcessoJudicial(), new Date());
    }

    @URLAction(mappingId = "novoProcessoJudicial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        processoJudicial = new ProcessoJudicial();
        certidoesDoProcesso = Lists.newArrayList();
        parametroProcuradoria = processoJudicialFacade.getProcuradoriaParametroFacade().recuperarParametroVigente(new Date());
        assistenteDetentorArquivoComposicao = new AssistenteDetentorArquivoComposicao(processoJudicial, new Date());
    }

    public BigDecimal valorOriginalDaCertidao() {
        return processoJudicialFacade.getCertidaoDividaAtivaFacade().valorDaCertidao(selecionado.getCertidaoDividaAtiva());
    }

    public List<CertidaoDividaAtiva> getCertidoesDoProcesso() {
        return certidoesDoProcesso;
    }


    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    private void validaCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (processoJudicial.getDataAjuizamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Data de Ajuizamento!");
        }

        if (processoJudicial.getNumero() == null || processoJudicial.getNumero().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o número do Processo Judicial!");
        }

        if (processoJudicial.getNumeroProcessoForum() == null || processoJudicial.getNumeroProcessoForum().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o número do Processo no Fórum!");
        }

        if (processoJudicial.getProcessos().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Certidão Dívida Ativa do processo!");
        }
        for (ProcessoJudicialCDA processo : processoJudicial.getProcessos()) {
            CertidaoDividaAtiva cda = processo.getCertidaoDividaAtiva();
            try {
                IntegraSoftplanFacade.CDAAnoNumero cdaAnoNumero = processoJudicialFacade.getIntegraSoftplanFacade().extrairAnoNumeroCDA(cda.getNumero());
                ProcessoJudicialCDA procCda = processoJudicialFacade.getIntegraSoftplanFacade().findProcessoJudicialCDAByNumero(cdaAnoNumero.ano, cdaAnoNumero.numero);
                if (procCda != null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("CDA [" + cdaAnoNumero.numero + "] do ano [" + cdaAnoNumero.ano + "] já associada ao processo [" + procCda.getProcessoJudicial().getNumero() + "].");
                }
            } catch (Exception e) {
                logger.error("Exercício da CDA inválido: {}", cda.getExercicio());
            }
        }
        processoJudicialFacade.verificarSeCdaTemParcelas(processoJudicial, ve);
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validaCampos();
            processoJudicialFacade.atualizarSituacoes(processoJudicial);
            processoJudicialFacade.salvarProcesso(processoJudicial);
            comunicaSofPlan();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void comunicaSofPlan() {
        processoJudicialFacade.getComunicaSofPlanFacade().enviarCDA(certidoesDoProcesso);
    }

    public List<DocumentoProcuradoria> getDocumentos() {
        List<DocumentoProcuradoria> retorno = Lists.newArrayList();
        if (parametroProcuradoria != null) {
            for (DocumentoProcuradoria doc : parametroProcuradoria.getDocumentosNecessarios()) {
                retorno.add(doc);
            }
        }
        return retorno;
    }

    public List<SelectItem> getSituacaoTramite() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (SituacaoTramiteJudicial situacao : processoJudicialFacade.getSituacaoTramiteJudicialFacade().getSituacaoTramiteJudicial()) {
            retorno.add(new SelectItem(situacao, situacao.getDescricao()));
        }
        return retorno;
    }

    public List<Vara> completaVara(String parte) {
        return processoJudicialFacade.getVaraFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public void removerTramite(TramiteProcessoJudicial tramite) {
        processoJudicial.getListaTramites().remove(tramite);
    }

    public void adicionarTramite() {
        if (podeAdicionarTramite()) {
            if (tramite.getValor() == null) {
                tramite.setValor(BigDecimal.ZERO);
            }
            tramite.setProcessoJudicial(processoJudicial);
            processoJudicial.getListaTramites().add(tramite);
            tramite = new TramiteProcessoJudicial();
        }
    }

    public boolean podeAdicionarTramite() {
        boolean retorno = true;
        if (tramite.getJuizResponsavel() == null) {
            FacesUtil.addOperacaoNaoPermitida("Informe o Juiz Responsável do Trâmite");
            retorno = false;
        }
        if (tramite.getVara() == null) {
            FacesUtil.addOperacaoNaoPermitida("Informe a Vara do Trâmite");
            retorno = false;
        }
        if (tramite.getSituacaoTramiteJudicial() == null) {
            FacesUtil.addOperacaoNaoPermitida("Informe a Situação do trâmite");
            retorno = false;
        }
        return retorno;
    }

    public List<SituacaoCadastralPessoa> getSituacoesDisponiveis() {
        return Lists.newArrayList(SituacaoCadastralPessoa.ATIVO);
    }

    public void removerCDA(ProcessoJudicialCDA processoJudicialCDA) {
        certidoesDoProcesso.remove(processoJudicialCDA.getCertidaoDividaAtiva());
        processoJudicial.getProcessos().remove(processoJudicialCDA);
    }


    public void adicionaCDA() {
        if (podeAdicionarCDA()) {
            selecionado = new ProcessoJudicialCDA();
            certidao.setDataCertidao(new Date());
            certidoesDoProcesso.add(certidao); // lista de certidoes para a softplan
            selecionado.setCertidaoDividaAtiva(certidao);
            selecionado.setProcessoJudicial(processoJudicial);
            processoJudicial.getProcessos().add(selecionado);
            certidao = null;
        }
    }

    public boolean podeAdicionarCDA() {
        for (CertidaoDividaAtiva c : certidoesDoProcesso) {
            if (c.getId().equals(certidao.getId())) {
                FacesUtil.addOperacaoNaoPermitida("Certidão de Dívda Ativa já adicionada no processo!");
                return false;
            }
        }
        return true;
    }

    public List<CertidaoDividaAtiva> completaCertidaoDividaAtiva(String parte) {
        return processoJudicialFacade.getCertidaoDividaAtivaFacade().recuperarCDAQueNaoEstaoEMProcessoJudicial(parte);
    }

    public List<SelectItem> getPapelProcesso() {
        return Util.getListSelectItem(Arrays.asList(PapelProcesso.values()));
    }

    public void removerPessoasEnvolvidas(PessoaProcesso pessoa) {
        processoJudicial.getEnvolvidos().remove(pessoa);
    }

    public void adicionarPessoasEnvolvidas() {
        if (podeAdicionarPessoaEnvolvida()) {
            pessoaProcesso.setProcessoJudicial(processoJudicial);
            processoJudicial.getEnvolvidos().add(pessoaProcesso);
            pessoaProcesso = new PessoaProcesso();
        }
    }

    public boolean podeAdicionarPessoaEnvolvida() {
        boolean retorno = true;
        if (pessoaProcesso.getPessoa() == null) {
            FacesUtil.addOperacaoNaoPermitida("Informe a Pessoa Envolvida");
            retorno = false;
        }
        if (pessoaProcesso.getPapelProcesso() == null) {
            FacesUtil.addOperacaoNaoPermitida("Informe o papel que a pessoa exerce no processo");
            retorno = false;
        }
        for (PessoaProcesso p : processoJudicial.getEnvolvidos()) {
            if (pessoaProcesso.getPessoa() != null) {
                if (p.getPessoa().getId().equals(pessoaProcesso.getPessoa().getId())) {
                    FacesUtil.addOperacaoNaoPermitida("Pessoa já adicionada no processo");
                    return false;
                }
            }
        }
        return retorno;
    }

    public BigDecimal getValorAtualizadoDaCertidao(CertidaoDividaAtiva cda) {
        return processoJudicialFacade.getCertidaoDividaAtivaFacade().valorAtualizadoDaCertidao(cda).getValorTotal();
    }

    public BigDecimal getValorDeSucumbencia() {
        BigDecimal total = BigDecimal.ZERO;
        if (!processoJudicial.getProcessos().isEmpty()) {
            for (ProcessoJudicialCDA processoJudicialCDA : processoJudicial.getProcessos()) {
                total = total.add(processoJudicialFacade.getCertidaoDividaAtivaFacade().valorAtualizadoDaCertidao(processoJudicialCDA.getCertidaoDividaAtiva()).getValorTotal());
            }
        }
        if (!processoJudicial.getListaTramites().isEmpty()) {
            for (TramiteProcessoJudicial tramite : processoJudicial.getListaTramites()) {
                total = total.add(tramite.getValor());
            }
        }
        return total;
    }

    public List<SelectItem> getTipoDeAcao() {
        return Util.getListSelectItem(TipoAcaoProcessoJudicial.values());
    }

    public void buscarExtincao() {
        if (ProcessoJudicial.Situacao.INATIVO.equals(selecionado.getProcessoJudicial().getSituacao())) {
            processoJudicialExtincao = processoJudicialFacade.buscarExtincaoPorNumero(selecionado.getProcessoJudicial().getNumeroProcessoForum());
        }
    }

    public ProcessoJudicialExtincao getProcessoJudicialExtincao() {
        return processoJudicialExtincao;
    }

    public void setProcessoJudicialExtincao(ProcessoJudicialExtincao processoJudicialExtincao) {
        this.processoJudicialExtincao = processoJudicialExtincao;
    }

    @Override
    public void excluir() {
        try {
            processoJudicialFacade.removerProcessoCDA(selecionado);
            redireciona();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoExcluir()));
        } catch (Exception e) {
            logger.error("Erro ao excluir ProcessoJudicialCDA: ", e);
            try {
                validarForeinKeysComRegistro(selecionado);
                descobrirETratarException(e);
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        }
    }

    public void consultarProcessoDatajud() {
        try {
            registrosDatajud = processoJudicialFacade.buscarDadosDatajud(selecionado.getProcessoJudicial().getNumeroProcessoForum());
            if (registrosDatajud == null || registrosDatajud.isEmpty()) {
                FacesUtil.addAtencao("Processo judicial não encontrado no Datajud.");
            } else {
                FacesUtil.atualizarComponente("formConsultaDatajud");
                FacesUtil.executaJavaScript("openDialog(dlgConsultaDatajud)");
            }
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }
}
