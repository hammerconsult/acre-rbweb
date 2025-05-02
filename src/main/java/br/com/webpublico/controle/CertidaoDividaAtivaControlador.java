/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@ManagedBean(name = "certidaoDividaAtivaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoGeracaoEmissaoCDA",
        pattern = "/geracao-de-certidao-de-divida-ativa/",
        viewId = "/faces/tributario/dividaativa/certidaodividaativa/geraremitircda.xhtml"),
    @URLMapping(id = "verCertidaoInclusaoParcelamento",
        pattern = "/certida-divida-ativa/inclusao-parcelamento/#{certidaoDividaAtivaControlador.id}/",
        viewId = "/faces/tributario/dividaativa/certidaodividaativa/visualiza.xhtml"),
    @URLMapping(id = "verCertidaoInclusao",
        pattern = "/certida-divida-ativa/inclusao/#{certidaoDividaAtivaControlador.id}/",
        viewId = "/faces/tributario/dividaativa/certidaodividaativa/visualiza.xhtml"),
    @URLMapping(id = "retificaCertidao",
        pattern = "/certida-divida-ativa/retificacao/#{certidaoDividaAtivaControlador.pessoaId}/",
        viewId = "/faces/tributario/dividaativa/certidaodividaativa/retificacda.xhtml"),
    @URLMapping(id = "redirecionamentoPorInscricaoDividaAtiva",
        pattern = "/geracao-de-certidao-de-divida-ativa-inscricao/#{certidaoDividaAtivaControlador.idInscricaoDividaAtiva}/",
        viewId = "/faces/tributario/dividaativa/certidaodividaativa/geraremitircdaviainscricao.xhtml")
})
public class CertidaoDividaAtivaControlador extends SuperControladorCRUD {

    private static final Logger logger = LoggerFactory.getLogger(CertidaoDividaAtivaControlador.class);
    @EJB
    SistemaFacade sistemaFacade;
    Future futureRetificacao;
    CompletableFuture<AssistenteCDA> future;
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    @EJB
    private ParametrosDividaAtivaFacade parametrosDividaAtivaFacade;
    private ConverterGenerico converterDivida;
    private String exercicioInicial;
    private String exercicioFinal;
    private TipoCadastroTributario tipoCadastroTributario;
    private String cadastroInicial;
    private String cadastroFinal;
    private List<ItemInscricaoDividaAtiva> itensInscricaoDividaAtivas;
    private Pessoa pessoa;
    private Divida divida;
    private Long id;
    private Long idInscricaoDividaAtiva;
    private CertidaoDividaAtiva certidao;
    private ComunicaSofPlanFacade.ServiceId serviceId;
    private AssistenteCDA assistenteCDA;
    private Long pessoaId;
    private Pessoa pessoaRetificacao;
    private List<CertidaoDividaAtiva> certidoesDividaAtivaRetificacao;
    private EnviarCDAControlador.SituacaoEnvio situacaoEnvio;
    private List<Divida> dividasSelecionadas;
    private Map<Divida, AgrupadorCDA> agrupadoresCadastrados;
    private Map<ItemInscricaoDividaAtiva, UltimoLinhaDaPaginaDoLivroDividaAtiva> mapaLinhaLivroDividaAtiva;
    private AssistenteBarraProgresso assistente;
    private CertidaoDividaAtiva certidaoSelecionada;

    public CertidaoDividaAtivaControlador() {
        metadata = new EntidadeMetaData(CertidaoDividaAtiva.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return certidaoDividaAtivaFacade;
    }

    @URLAction(mappingId = "verCertidaoInclusaoParcelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verInclusaoParcelamento() {
        recuperarCDA();
        serviceId = ComunicaSofPlanFacade.ServiceId.INCLUSAO_PARCELAMENTO;
    }

    @URLAction(mappingId = "verCertidaoInclusao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verInclusao() {
        recuperarCDA();
        serviceId = ComunicaSofPlanFacade.ServiceId.INCLUSAO;
    }

    private void recuperarCDA() {
        certidao = certidaoDividaAtivaFacade.recuperar(id);
    }

    public void recuperarCDA(CertidaoDividaAtiva cda) {
        certidao = certidaoDividaAtivaFacade.recuperar(cda.getId());
    }

    @URLAction(mappingId = "novoGeracaoEmissaoCDA", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        exercicioInicial = null;
        exercicioFinal = null;
        tipoCadastroTributario = null;
        cadastroInicial = "1";
        cadastroFinal = "999999999999999999";
        itensInscricaoDividaAtivas = Lists.newArrayList();
        pessoa = null;
        divida = null;
        assistenteCDA = new AssistenteCDA(getExercicioCorrente());
        dividasSelecionadas = new ArrayList<>();
        agrupadoresCadastrados = Maps.newHashMap();
    }

    @URLAction(mappingId = "retificaCertidao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void retificaCertidao() {
        situacaoEnvio = EnviarCDAControlador.SituacaoEnvio.AGUARDANDO;
        pessoaRetificacao = certidaoDividaAtivaFacade.getPessoaFacade().recuperar(pessoaId);
        if (pessoaRetificacao != null) {
            certidoesDividaAtivaRetificacao = certidaoDividaAtivaFacade.listaCertidaoDividaAtivaPorPessoa(pessoaRetificacao);
        }
    }

    @URLAction(mappingId = "redirecionamentoPorInscricaoDividaAtiva", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaGeracaoPorInscricaoDividaAtiva() {
        super.novo();
        InscricaoDividaAtiva filtros = certidaoDividaAtivaFacade.getInscricaoDividaAtivaFacade().recuperar(idInscricaoDividaAtiva);
        exercicioInicial = filtros.getExercicio().toString();
        exercicioFinal = filtros.getExercicioFinal().toString();
        tipoCadastroTributario = filtros.getTipoCadastroTributario();
        cadastroInicial = filtros.getCadastroInicial();
        cadastroFinal = filtros.getCadastroFinal();
        itensInscricaoDividaAtivas = Lists.newArrayList();
        pessoa = filtros.getContribuinte();
        dividasSelecionadas = filtros.getDividas();
        assistenteCDA = new AssistenteCDA(getExercicioCorrente());
        agrupadoresCadastrados = Maps.newHashMap();
    }

    public Converter converterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterGenerico(Divida.class, certidaoDividaAtivaFacade.getDividaFacade());
        }
        return converterDivida;
    }

    public List<SelectItem> getTiposDeCadastroTributario() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public String getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(String exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public String getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(String exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public String getCadastroFinal() {
        return cadastroFinal;
    }

    public void setCadastroFinal(String cadastroFinal) {
        this.cadastroFinal = cadastroFinal;
    }

    public String getCadastroInicial() {
        return cadastroInicial;
    }

    public void setCadastroInicial(String cadastroInicial) {
        this.cadastroInicial = cadastroInicial;
    }

    public List<ItemInscricaoDividaAtiva> getItensInscricaoDividaAtivas() {
        return itensInscricaoDividaAtivas;
    }

    public void setItensInscricaoDividaAtivas(List<ItemInscricaoDividaAtiva> itensInscricaoDividaAtivas) {
        this.itensInscricaoDividaAtivas = itensInscricaoDividaAtivas;
    }

    public Integer getNaoGerados() {
        return assistenteCDA.naoGerados;
    }

    public Boolean getGerando() {
        return assistenteCDA.getGerando();
    }

    public Boolean getGerado() {
        return assistenteCDA.getGerado();
    }

    public List<CertidaoDividaAtiva> getCertidoesDividaAtivaRetificacao() {
        return certidoesDividaAtivaRetificacao;
    }

    public void setCertidoesDividaAtivaRetificacao(List<CertidaoDividaAtiva> certidoesDividaAtivaRetificacao) {
        this.certidoesDividaAtivaRetificacao = certidoesDividaAtivaRetificacao;
    }

    public Long getPessoaId() {
        return pessoaId;
    }

    public void setPessoaId(Long pessoaId) {
        this.pessoaId = pessoaId;
    }

    public Pessoa getPessoaRetificacao() {
        return pessoaRetificacao;
    }

    public void setPessoaRetificacao(Pessoa pessoaRetificacao) {
        this.pessoaRetificacao = pessoaRetificacao;
    }

    public AssistenteCDA getAssistenteCDA() {
        return assistenteCDA;
    }

    public void setAssistenteCDA(AssistenteCDA assistenteCDA) {
        this.assistenteCDA = assistenteCDA;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CertidaoDividaAtiva getCertidao() {
        return certidao;
    }

    public void setCertidao(CertidaoDividaAtiva certidao) {
        this.certidao = certidao;
    }

    public AssistenteBarraProgresso getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteBarraProgresso assistente) {
        this.assistente = assistente;
    }

    public List<AgrupadorCDA> getAgrupadoresCadastrados() {
        List<AgrupadorCDA> retorno = Lists.newArrayList();
        if (agrupadoresCadastrados != null && agrupadoresCadastrados.values() != null) {
            for (AgrupadorCDA agrupador : agrupadoresCadastrados.values()) {
                if (!retorno.contains(agrupador))
                    retorno.add(agrupador);
            }
        }
        return retorno;
    }

    public void pesquisar() {
        try {
            validarCamposPreenchidos();
            agrupadoresCadastrados.clear();
            for (Divida dividaAgrupada : dividasSelecionadas) {
                identificarAgrupador(dividaAgrupada);
            }
            assistenteCDA.gerados = 0;
            assistenteCDA.naoGerados = 0;
            assistenteCDA.situacao = Situacao.CONSULTANDO;
            if (!tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA)) {
                pessoa = null;
            }
            itensInscricaoDividaAtivas = Lists.newArrayList();
            assistente = new AssistenteBarraProgresso();
            assistente.setDescricaoProcesso("Pesquisando Inscrição em D.A para geração de C.D.A");
            assistente.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
            future = AsyncExecutor.getInstance().execute(assistente, () -> {
                try {
                    return certidaoDividaAtivaFacade.recuperarItensInscricaoDividaAtiva(this);
                } catch (com.itextpdf.io.IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (ValidacaoException ex) {
            FacesUtil.executaJavaScript("encerraTimer()");
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    public void executarJavaScriptAposConcluirPesquisa() {
        if (future != null && future.isDone()) {
            List<ItemInscricaoDividaAtiva> itensPesquisados = getItensInscricaoDividaAtivas();
            if (itensPesquisados != null && !itensPesquisados.isEmpty()) {
                FacesUtil.executaJavaScript("terminaPesquisaComRegistros();");
            } else {
                FacesUtil.executaJavaScript("terminaPesquisaSemRegistros();");
            }
        }

    }

    public void enquantoGera() {
        if (future != null && future.isDone()) {
            FacesUtil.executaJavaScript("encerraTimer();");
            FacesUtil.executaJavaScript("novaPesquisa();");
            FacesUtil.executaJavaScript("atualizaFormFinaliza();");
        }
    }

    public Boolean getPesquisando() {
        return assistenteCDA.getPesquisando();
    }

    public Boolean getPesquisado() {
        return assistenteCDA.getPesquisado();
    }

    public void gerarCertidao() {
        Map<AssistenteAgrupadorCDA, List<ItemInscricaoDividaAtiva>> mapaAgrupador = Maps.newHashMap();

        for (ItemInscricaoDividaAtiva item : getItensInscricaoDividaAtivas()) {
            AssistenteAgrupadorCDA agrupador;
            if (agrupadoresCadastrados.containsKey(item.getDivida())) {
                agrupador = new AssistenteAgrupadorCDA(agrupadoresCadastrados.get(item.getDivida()), item);
            } else {
                agrupador = new AssistenteAgrupadorCDA(item);
            }
            if (!mapaAgrupador.containsKey(agrupador)) {
                mapaAgrupador.put(agrupador, new ArrayList<ItemInscricaoDividaAtiva>());
            }
            mapaAgrupador.get(agrupador).add(item);
        }
        assistenteCDA.situacao = Situacao.GERANDO;
        assistenteCDA.gerados = 0;
        assistenteCDA.setItens(mapaAgrupador);

        assistente = new AssistenteBarraProgresso();
        assistente.setDescricaoProcesso("Gerando C.D.A");
        assistente.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        future = AsyncExecutor.getInstance().execute(assistente, () -> {
            try {
                return certidaoDividaAtivaFacade.gerarCDA(assistente, assistenteCDA);
            } catch (com.itextpdf.io.IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void emitirCertidao(CertidaoDividaAtiva certidao, boolean novo) {
        if (certidao != null) {
            try {
                certidaoDividaAtivaFacade.geraDocumento(certidao, (SistemaControlador) Util.getControladorPeloNome("sistemaControlador"), novo);
                FacesUtil.executaJavaScript("gerarNovoDocumento.hide()");
            } catch (Exception e) {
                FacesUtil.addError("Erro ao emitir a CDA!", e.getMessage());
                logger.error(e.getMessage());
            }
        } else {
            FacesUtil.addError("Operação não permitida", "Nenhum paramentro de dívida ativa foi encontrado para o exercício de " + certidao.getExercicio());
        }
    }

    private void validarCamposPreenchidos() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicioInicial == null || exercicioInicial.trim().length() <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o exercício inicial.");
        }
        if (exercicioFinal == null || exercicioFinal.trim().length() <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o exercício final.");
        }

        if (tipoCadastroTributario == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o tipo de cadastro.");
        } else {
            if (!tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA)) {
                if (cadastroInicial == null && cadastroFinal == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("Informe o cadastro inicial e o cadastro final.");
                }
            }
        }

        adicionarDivida(false);
        if (dividasSelecionadas.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a dívida.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public void copiaExercicio() {
        this.exercicioFinal = this.exercicioInicial;
    }

    public void copiarCadastroInicialParaCadastroFinal() {
        this.cadastroFinal = this.cadastroInicial;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    List<Divida> recuperaDividas() {
        if (tipoCadastroTributario != null) {
            return certidaoDividaAtivaFacade.getInscricaoDividaAtivaFacade().buscarDividasPorTipoCadastroTributario(tipoCadastroTributario);
        }
        return new ArrayList<>();
    }

    public List<SelectItem> getDividasTipoCadastro() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, " "));
        for (Divida d : recuperaDividas()) {
            lista.add(new SelectItem(d, d.getDescricao()));
        }
        return lista;
    }

    public UltimoLinhaDaPaginaDoLivroDividaAtiva linhaSequenciaNumeroDoLivro(ItemInscricaoDividaAtiva item) {
        if (mapaLinhaLivroDividaAtiva == null) {
            mapaLinhaLivroDividaAtiva = Maps.newHashMap();
        }
        if (!mapaLinhaLivroDividaAtiva.containsKey(item)) {
            mapaLinhaLivroDividaAtiva.put(item, certidaoDividaAtivaFacade.buscarLinhaSequenciaNumeroDoLivroDoItemInscricao(item));
        }
        return mapaLinhaLivroDividaAtiva.get(item);
    }

    public String valorDacertidao(CertidaoDividaAtiva certidao) {
        return Util.formataValor(certidaoDividaAtivaFacade.valorAtualizadoDaCertidao(certidao).getValorTotal());
    }

    public Double getPorcentagemDoCalculo() {
        Double retorno;
        if (assistenteCDA.gerados == 0 || itensInscricaoDividaAtivas == null || itensInscricaoDividaAtivas.isEmpty()) {
            retorno = 0D;
        } else {
            retorno = (assistenteCDA.gerados.doubleValue() / itensInscricaoDividaAtivas.size()) * 100;
        }
        return retorno;
    }

    public void imprimirPessoasInconsistentes() {
        try {
            new ImprimirRelatorio().emitir();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void imprimirRelatorioPessoasInconsistentesDividaAtiva() {
        try {
            new ImprimirRelatorio().gerarRelatorio(tipoCadastroTributario, exercicioInicial, exercicioFinal, divida, pessoa, cadastroInicial, cadastroFinal);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void tentarNovamente() {
        switch (serviceId) {
            case INCLUSAO_PARCELAMENTO:
                incluiParcelamento();
                break;
            case INCLUSAO:
                inclui();
                break;
            case ALTERACAO:
                altera();
                break;
        }

    }

    private void altera() {
        certidaoDividaAtivaFacade.getProcessoParcelamentoFacade().getComunicaSofPlanFacade().alterarCDA(Lists.newArrayList(certidao));

        FacesUtil.addInfo("Operação realizada", "A comunicação com o sistema de procuradoria é feita em segundo plano, caso aconteça algo inesperado os usuários resposáveis serão notificados");
        FacesUtil.redirecionamentoInterno(serviceId.link + certidao.getId() + "/");
    }

    private void inclui() {
        certidaoDividaAtivaFacade.getProcessoParcelamentoFacade().getComunicaSofPlanFacade().enviarCDA(Lists.newArrayList(certidao));

        FacesUtil.addInfo("Operação realizada", "A comunicação com o sistema de procuradoria é feita em segundo plano, caso aconteça algo inesperado os usuários resposáveis serão notificados");
    }

    private void incluiParcelamento() {
        certidaoDividaAtivaFacade.getProcessoParcelamentoFacade().pagarParcelamento(certidao);

        FacesUtil.addInfo("Operação realizada", "A comunicação com o sistema de procuradoria é feita em segundo plano, caso aconteça algo inesperado os usuários resposáveis serão notificados");
    }

    public void editaPessoa() {
        String origem = serviceId.link + certidao.getId() + "/";
        if (certidao.getPessoa() instanceof PessoaJuridica) {
            Web.navegacao(origem, "/tributario/configuracoes/pessoa/editarpessoajuridica/" + certidao.getPessoa().getId() + "/");
        } else {
            Web.navegacao(origem, "/tributario/configuracoes/pessoa/editarpessoafisica/" + certidao.getPessoa().getId() + "/");
        }
    }

    private void retifica(List<CertidaoDividaAtiva> certidoes) {
        situacaoEnvio = EnviarCDAControlador.SituacaoEnvio.ENVIADO;
        futureRetificacao = certidaoDividaAtivaFacade.getProcessoParcelamentoFacade().getComunicaSofPlanFacade().enviarCDA(Lists.newArrayList(certidao));
    }

    public String retornarInscricaoDoCadastro(Cadastro cadastro) {
        return certidaoDividaAtivaFacade.retornaInscricaoDoCadastro(cadastro);
    }

    public Integer getSizeCertidoesDividaAtivaRetificacao() {
        if (certidoesDividaAtivaRetificacao != null) {
            return certidoesDividaAtivaRetificacao.size();
        }
        return 0;
    }

    public void enviarCertidoesRetificacao() {
        if (certidoesDividaAtivaRetificacao != null && !certidoesDividaAtivaRetificacao.isEmpty()) {
            RequestContext.getCurrentInstance().execute("dialogEnviar.show()");
        } else {
            FacesUtil.addError("Atenção", "Nenhum Certidão encontrada para o envio!");
        }
    }

    public void atribuirEnviado() {
        if (situacaoEnvio.equals(EnviarCDAControlador.SituacaoEnvio.ENVIADO) && futureRetificacao != null && futureRetificacao.isDone()) {
            situacaoEnvio = EnviarCDAControlador.SituacaoEnvio.ENVIADO;
        }
    }

    public EnviarCDAControlador.SituacaoEnvio getSituacaoEnvio() {
        return situacaoEnvio;
    }

    public void enviarRetificacao() {
        retifica(certidoesDividaAtivaRetificacao);
    }

    public List<Divida> getDividasSelecionadas() {
        return dividasSelecionadas;
    }

    public void setDividasSelecionadas(List<Divida> dividasSelecionadas) {
        this.dividasSelecionadas = dividasSelecionadas;
    }

    public void removerDivida(Divida divida) {
        if (dividasSelecionadas.contains(divida)) {
            dividasSelecionadas.remove(divida);
        }
    }

    public void adicionarDivida(boolean mostrarMensagem) {
        if (divida != null) {
            dividasSelecionadas.add(divida);
            divida = null;
        } else {
            if (mostrarMensagem) {
                FacesUtil.addCampoObrigatorio("Selecione uma dívida e adicione na lista.");
            }
        }
    }

    private void identificarAgrupador(Divida divida) {
        AgrupadorCDA agrupador = parametrosDividaAtivaFacade.buscarAgrupadoresDaDivida(divida);
        if (agrupador != null) {
            for (AgrupadorCDADivida agrupadorCDADivida : agrupador.getDividas()) {
                agrupadoresCadastrados.put(agrupadorCDADivida.getDivida(), agrupador);
            }
        }
    }

    public List<Long> recuperarIDsDividas() {
        List<Long> retorno = Lists.newArrayList();
        for (Divida d : dividasSelecionadas) {
            retorno.add(d.getId());
        }
        for (Divida d : agrupadoresCadastrados.keySet()) {
            retorno.add(d.getId());
        }

        return retorno;
    }

    public Long getIdInscricaoDividaAtiva() {
        return idInscricaoDividaAtiva;
    }

    public void setIdInscricaoDividaAtiva(Long idInscricaoDividaAtiva) {
        this.idInscricaoDividaAtiva = idInscricaoDividaAtiva;
    }

    public void redirecionarParaConsultaAndReemissao() {
        FacesUtil.redirecionamentoInterno("/consulta-e-reemissao-de-certidao-de-divida-ativa/");
    }

    public String getDataCertidaoSelecionada() {
        if (getCertidaoSelecionada() != null && getCertidaoSelecionada().getDocumentoOficial() != null) {
            return DataUtil.getDataFormatada(getCertidaoSelecionada().getDocumentoOficial().getDataEmissao());
        }
        return "";
    }

    public enum Situacao {
        AGUARDANDO,
        CONSULTANDO,
        CONSULTADO,
        GERANDO,
        GERADO;
    }

    public static class AssistenteCDA {
        private Situacao situacao;
        private Integer gerados;
        private Integer naoGerados;
        private Exercicio exercicio;
        private Map<AssistenteAgrupadorCDA, List<ItemInscricaoDividaAtiva>> itens;

        public AssistenteCDA(Exercicio exercicio) {
            gerados = 0;
            naoGerados = 0;
            situacao = Situacao.AGUARDANDO;
            this.exercicio = exercicio;
        }

        public Situacao getSituacao() {
            return situacao;
        }

        public void setSituacao(Situacao situacao) {
            this.situacao = situacao;
        }

        private Integer getGerados() {
            return gerados;
        }

        private void setGerados(Integer gerados) {
            this.gerados = gerados;
        }

        private Integer getNaoGerados() {
            return naoGerados;
        }

        private void setNaoGerados(Integer naoGerados) {
            this.naoGerados = naoGerados;
        }

        public void conta() {
            gerados = gerados + 1;
        }

        public void contaNaoGerados() {
            naoGerados = naoGerados + 1;
        }

        public Boolean getGerando() {
            return situacao.equals(Situacao.GERANDO);
        }

        public Boolean getGerado() {
            return situacao.equals(Situacao.GERADO);
        }

        public Boolean getPesquisando() {
            return situacao.equals(Situacao.CONSULTANDO);
        }

        public Boolean getPesquisado() {
            return situacao.equals(Situacao.CONSULTADO);
        }

        public Map<AssistenteAgrupadorCDA, List<ItemInscricaoDividaAtiva>> getItens() {
            return itens;
        }

        public void setItens(Map<AssistenteAgrupadorCDA, List<ItemInscricaoDividaAtiva>> itens) {
            this.itens = itens;
        }

        public Exercicio getExercicio() {
            return exercicio;
        }
    }

    public static class AssistenteAgrupadorCDA {
        public Cadastro cadastro;
        public Pessoa pessoa;
        public Exercicio exercicio;
        public Divida divida;
        public AgrupadorCDA agrupadorCDA;

        public AssistenteAgrupadorCDA(ItemInscricaoDividaAtiva itemInscricaoDividaAtiva) {
            this.cadastro = itemInscricaoDividaAtiva.getCadastro();
            this.pessoa = itemInscricaoDividaAtiva.getPessoa();
            this.exercicio = itemInscricaoDividaAtiva.getProcessoCalculo().getExercicio();
            this.divida = itemInscricaoDividaAtiva.getDivida();
        }

        public AssistenteAgrupadorCDA(AgrupadorCDA agrupadorCDA, ItemInscricaoDividaAtiva itemInscricaoDividaAtiva) {
            this.cadastro = itemInscricaoDividaAtiva.getCadastro();
            this.pessoa = itemInscricaoDividaAtiva.getPessoa();
            this.exercicio = itemInscricaoDividaAtiva.getProcessoCalculo().getExercicio();
            this.agrupadorCDA = agrupadorCDA;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AssistenteAgrupadorCDA)) return false;

            AssistenteAgrupadorCDA that = (AssistenteAgrupadorCDA) o;

            if (cadastro != null ? !cadastro.equals(that.cadastro) : that.cadastro != null) return false;
            if (exercicio != null ? !exercicio.equals(that.exercicio) : that.exercicio != null) return false;
            if (pessoa != null ? !pessoa.equals(that.pessoa) : that.pessoa != null) return false;
            if (divida != null ? !divida.equals(that.divida) : that.divida != null) return false;
            if (agrupadorCDA != null ? !agrupadorCDA.equals(that.agrupadorCDA) : that.agrupadorCDA != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = cadastro != null ? cadastro.hashCode() : 0;
            result = 31 * result + (pessoa != null ? pessoa.hashCode() : 0);
            result = 31 * result + (exercicio != null ? exercicio.hashCode() : 0);
            result = 31 * result + (divida != null ? divida.hashCode() : 0);
            return result;
        }
    }

    public class ImprimirRelatorio extends AbstractReport {
        public void emitir() throws JRException, IOException {
            HashMap parameters = new HashMap();
            parameters.put("BRASAO", getCaminhoImagem());
            parameters.put("USUARIO", sistemaFacade.getUsuarioCorrente().getNome());
            parameters.put("WHERE", "");
            gerarRelatorio("PessoasInconsistentesDividaAtiva.jasper", parameters);
        }

        public void gerarRelatorio(TipoCadastroTributario tipo, String exercicioInicial, String exercicioFinal, Divida divida, Pessoa pessoa, String cadastroInicial, String cadastroFinal) throws JRException, IOException {
            StringBuilder where = new StringBuilder();
            if (TipoCadastroTributario.PESSOA.equals(tipo)) {
                where.append("and (pessoa.id = ").append(pessoa.getId()).append(") ");
            } else if (cadastroInicial != null || cadastroFinal != null) {
                where.append(" and (");
                if (cadastroInicial != null) {
                    where.append(" coalesce(ci.inscricaocadastral, ce.inscricaocadastral, to_char(cr.codigo), '-') >= '")
                        .append(cadastroInicial).append("' ");
                }
                if (cadastroFinal != null) {
                    if (cadastroInicial != null) where.append(" and ");
                    where.append(" coalesce(ci.inscricaocadastral, ce.inscricaocadastral, to_char(cr.codigo), '-') <= '")
                        .append(cadastroFinal).append("' ");
                }
                where.append(") ");
            }
            if (divida != null) {
                where.append("and (divida.id = ").append(divida.getId()).append(") ");
            }
            if (!exercicioInicial.isEmpty()) {
                where.append(" and (").append("exercicio.ano >= ").append(exercicioInicial).append(") ");
            }
            if (!exercicioFinal.isEmpty()) {
                where.append(" and (").append("exercicio.ano <= ").append(exercicioFinal).append(") ");
            }
            HashMap parameters = new HashMap();
            parameters.put("BRASAO", getCaminhoImagem());
            parameters.put("USUARIO", sistemaFacade.getUsuarioCorrente().getNome());
            parameters.put("WHERE", where.toString());
            gerarRelatorio("PessoasInconsistentesDividaAtiva.jasper", parameters);
        }
    }

    public CertidaoDividaAtiva getCertidaoSelecionada() {
        return certidaoSelecionada;
    }

    public void atribuirCertidaoSelecionada(CertidaoDividaAtiva certidao) {
        this.certidaoSelecionada = certidao;
    }

}
