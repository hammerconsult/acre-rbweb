package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.AutorizacaoTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.NovoCalculoIPTUFacade;
import br.com.webpublico.negocios.tributario.auxiliares.AssistenteCalculadorIPTU;
import br.com.webpublico.negocios.tributario.auxiliares.CalculadorIPTU;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcCalculoIptuDAO;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.ConverterExercicio;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean(name = "novoCalculoIPTUControlador")
@SessionScoped
@URLMappings(mappings = {
    @URLMapping(id = "calcularNovoIPTU", pattern = "/iptu/novo/", viewId = "/faces/tributario/iptu/calculo/edita.xhtml"),
    @URLMapping(id = "calcularNovoIPTUIsencao", pattern = "/iptu/novo-recalculo-isencao/#{novoCalculoIPTUControlador.idIsencao}", viewId = "/faces/tributario/iptu/calculo/edita.xhtml"),
    @URLMapping(id = "listarNovoIPTU", pattern = "/iptu/listar/", viewId = "/faces/tributario/iptu/calculo/lista.xhtml"),
    @URLMapping(id = "selecionarNovoIPTU", pattern = "/iptu/ver/#{novoCalculoIPTUControlador.id}/", viewId = "/faces/tributario/iptu/calculo/visualizar.xhtml"),
    @URLMapping(id = "calculandoNovoIPTU", pattern = "/iptu/calculando-novo-iptu/", viewId = "/faces/tributario/iptu/calculo/log.xhtml")
})
public class NovoCalculoIPTUControlador extends PrettyControlador<ProcessoCalculoIPTU> implements Serializable {

    @EJB
    private NovoCalculoIPTUFacade novoCalculoFacade;
    private List<ConfiguracaoEventoIPTU> configuracoes;
    private ConverterGenerico converterConfiguracao;
    private ConverterExercicio converterExercicio;
    private Long idIsencao;
    private String urlAtual;
    private AssistenteCalculadorIPTU assistente;
    private List<Future<AssistenteCalculadorIPTU>> futures;
    private CalculoIPTU calculoSelecionado;
    private IsencaoCadastroImobiliario isencaoCadastroImobiliarioSelecionado;
    private List<ItemCalculoIPTU> itensCalculoIptu;
    private List<ResultadoParcela> parcelasCalculoSelecionado;

    public CalculoIPTU getCalculoSelecionado() {
        return calculoSelecionado;
    }

    public void setCalculoSelecionado(CalculoIPTU calculoSelecionado) {
        this.calculoSelecionado = calculoSelecionado;
    }

    public List<ResultadoParcela> getParcelasCalculoSelecionado() {
        return parcelasCalculoSelecionado;
    }

    public void setParcelasCalculoSelecionado(List<ResultadoParcela> parcelasCalculoSelecionado) {
        this.parcelasCalculoSelecionado = parcelasCalculoSelecionado;
    }

    public List<ItemCalculoIPTU> getItensCalculoIptu() {
        return itensCalculoIptu;
    }

    public void setItensCalculoIptu(List<ItemCalculoIPTU> itensCalculoIptu) {
        this.itensCalculoIptu = itensCalculoIptu;
    }

    public IsencaoCadastroImobiliario getIsencaoCadastroImobiliarioSelecionado() {
        return isencaoCadastroImobiliarioSelecionado;
    }

    public void setIsencaoCadastroImobiliarioSelecionado(IsencaoCadastroImobiliario isencaoCadastroImobiliarioSelecionado) {
        this.isencaoCadastroImobiliarioSelecionado = isencaoCadastroImobiliarioSelecionado;
    }

    public Long getIdIsencao() {
        return idIsencao;
    }

    public void setIdIsencao(Long idIsencao) {
        this.idIsencao = idIsencao;
    }

    public void setSelecionado(ProcessoCalculoIPTU selecionado) {
        this.selecionado = selecionado;
    }

    public Converter getConverterConfiguracao() {

        if (converterConfiguracao == null) {
            converterConfiguracao = new ConverterGenerico(ConfiguracaoEventoIPTU.class, novoCalculoFacade.getConfiguracaoEventoFacade());
        }
        return converterConfiguracao;
    }

    public Converter getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(novoCalculoFacade.getConfiguracaoEventoFacade().getEventoCalculoFacade().getConsultaDebitoFacade().getExercicioFacade());
        }
        return converterExercicio;
    }

    @URLAction(mappingId = "selecionarNovoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void selecionar() {
        calculoSelecionado = novoCalculoFacade.recuperaIPTU(getId());
        urlAtual = "/iptu/ver/#{novoCalculoIPTUControlador.id}/";
        selecionado = calculoSelecionado.getProcessoCalculoIPTU();
        carregarParcelasOriginadas();
        isencaoCadastroImobiliarioSelecionado = novoCalculoFacade.getProcessoIsencaoIPTUFacade().buscarIsencaoPorIdCalculo(calculoSelecionado.getId());
        itensCalculoIptu = calculoSelecionado.getItensCalculo();
    }

    @Override
    public AbstractFacade getFacede() {
        return novoCalculoFacade;
    }

    private void carregarParcelasOriginadas() {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calculoSelecionado.getId());
        parcelasCalculoSelecionado = consultaParcela.executaConsulta().getResultados();
    }

    @URLAction(mappingId = "calcularNovoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        urlAtual = "/iptu/novo/";
        selecionado = (ProcessoCalculoIPTU) Web.pegaDaSessao(ProcessoCalculoIPTU.class);
        if (selecionado == null) {
            selecionado = new ProcessoCalculoIPTU();
            selecionado.setExercicio(novoCalculoFacade.getSistemaFacade().getExercicioCorrente());
            selecionado.setCadastroInical("1");
            selecionado.setCadastroFinal("999999999999999");
        }
        ;
        futures = Lists.newArrayList();
    }

    @URLAction(mappingId = "listarNovoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listar() {
        urlAtual = "/iptu/listar/";
    }

    @URLAction(mappingId = "calcularNovoIPTUIsencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoPorIsencao() {
        ProcessoIsencaoIPTU processo = novoCalculoFacade.getProcessoIsencaoIPTUFacade().recuperar(idIsencao);
        urlAtual = "/iptu/novo-recalculo-isencao/#{novoCalculoIPTUControlador.idIsencao}";
        selecionado = new ProcessoCalculoIPTU();
        selecionado.setExercicio(novoCalculoFacade.getSistemaFacade().getExercicioCorrente());
        selecionado.setCadastroInical(processo.getInscricaoInicial());
        selecionado.setCadastroFinal(processo.getInscricaoFinal());
        selecionado.setDescricao("Processo de recalculo por isenção (Lei " + processo.getCategoriaIsencaoIPTU().getNumeroLei() + ")");
        configuracoes = novoCalculoFacade.getConfiguracaoEventoFacade().lista();
        futures = Lists.newArrayList();
    }

    @URLAction(mappingId = "calculandoNovoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void calcularNovoIptu() {
        try {
            urlAtual = "/iptu/calculando-novo-iptu/";
            selecionado = (ProcessoCalculoIPTU) Web.pegaDaSessao(ProcessoCalculoIPTU.class);

            FacesUtil.executaJavaScript("acompanhaCalculoIPTU()");
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.addErrorPadrao(e);
        }

    }

    public List getConfiguracoes() {
        configuracoes = novoCalculoFacade.getConfiguracaoEventoFacade().listarAtivos();
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        for (ConfiguracaoEventoIPTU conf : configuracoes) {
            if (conf.getAtivo()) {
                retorno.add(new SelectItem(conf, conf.getDescricao()));
            }
        }
        return retorno;
    }

    public void preCalcular() {
        try {
            validarCampos();
            if (!avisarExistenciaParcelaPaga()) {
                calcular();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    private boolean avisarExistenciaParcelaPaga() {
        if (novoCalculoFacade.hasParcelaIPTUPaga(selecionado.getExercicio(),
            selecionado.getCadastroInicial(),
            selecionado.getCadastroFinal())) {
            UsuarioSistema usuarioCorrente = novoCalculoFacade.getSistemaFacade().getUsuarioCorrente();
            usuarioCorrente = novoCalculoFacade.getUsuarioSistemaFacade().recarregar(usuarioCorrente);
            if (usuarioCorrente.hasAutorizacaoTributario(AutorizacaoTributario.PERMITIR_LANCAMENTO_IPTU_PAGO)) {
                FacesUtil.executaJavaScript("openDialog(dlgConfirmacaoCalculoParcelaPaga)");
            } else {
                FacesUtil.addOperacaoNaoPermitida("O(s) cadastro(s) imobiliário(s) já possuem parcela(s) paga(s) no exercício selecionado.");
            }
            return true;
        }
        return false;
    }

    public void calcular() {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        JdbcCalculoIptuDAO calculoDAO = (JdbcCalculoIptuDAO) ap.getBean("jdbcCalculoIptuDAO");
        selecionado.setDivida(novoCalculoFacade.getConfiguracaoTributarioFacade().retornaUltimo().getDividaIPTU());
        selecionado.setConfiguracaoEventoIPTU(novoCalculoFacade.getConfiguracaoEventoFacade().recuperar(selecionado.getConfiguracaoEventoIPTU().getId()));
        selecionado.setUsuarioSistema(novoCalculoFacade.getSistemaFacade().getUsuarioCorrente());
        List<CadastroImobiliario> cadastros = calculoDAO.cadastroPorInicioFim(selecionado.getCadastroInicial(), selecionado.getCadastroFinal());
        if (!cadastros.isEmpty()) {
            List<List<CadastroImobiliario>> quebrado = Lists.newArrayList();
            if (cadastros.size() > 10) {
                quebrado = particionarEmCinco(cadastros);
            } else {
                quebrado.add(cadastros);
            }
            selecionado = calculoDAO.geraProcessoCalculo(selecionado);
            assistente = new AssistenteCalculadorIPTU(selecionado, cadastros.size());
            for (List<CadastroImobiliario> quebra : quebrado) {
                futures.add(novoCalculoFacade.calcularIPTU(quebra, assistente));
            }
            Web.poeNaSessao(selecionado);
            FacesUtil.redirecionamentoInterno("/iptu/calculando-novo-iptu/");
        } else {
            FacesUtil.addError("Operação não permitida", "Nenhum cadastro encontrado com os filtros informados");
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDescricao() == null || selecionado.getDescricao().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a descrição para o calculo. Ex: Calculo de I.P.T.U. para o exercício de " + novoCalculoFacade.getSistemaFacade().getExercicioCorrente());
        }
        if (selecionado.getCadastroInical() == null || selecionado.getCadastroInical().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o cadastro inicial. Ex: 100500370037001");
        }
        if (selecionado.getCadastroFinal() == null || selecionado.getCadastroFinal().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o cadastro final. Ex: 100500370037001");
        }
        if (selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o exercício para o calculo. Ex: " + novoCalculoFacade.getSistemaFacade().getExercicioCorrente());
        }
        if (selecionado.getConfiguracaoEventoIPTU() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe uma configuração de eventos para o calculo. ");
        } else {
            if (selecionado.getConfiguracaoEventoIPTU().getExercicioInicial() != null &&
                selecionado.getConfiguracaoEventoIPTU().getExercicioInicial().getAno().compareTo(selecionado.getExercicio().getAno()) > 0) {
                ve.adicionarMensagemDeCampoObrigatorio("O exercício informado não pode ser calculado com essa configuração!");
            } else {
                if (selecionado.getConfiguracaoEventoIPTU().getExercicioFinal() != null &&
                    selecionado.getConfiguracaoEventoIPTU().getExercicioFinal().getAno().compareTo(selecionado.getExercicio().getAno()) < 0) {
                    ve.adicionarMensagemDeCampoObrigatorio("O exercício informado não pode ser calculado com essa configuração!");
                }
            }
        }
        ve.lancarException();
    }

    private List<List<CadastroImobiliario>> particionarEmCinco(List<CadastroImobiliario> ids) {
        List<List<CadastroImobiliario>> retorno = new ArrayList<>();
        int parte = ids.size() / 5;
        retorno.add(ids.subList(0, parte));
        retorno.add(ids.subList(parte, parte * 2));
        retorno.add(ids.subList(parte * 2, parte * 3));
        retorno.add(ids.subList(parte * 3, parte * 4));
        retorno.add(ids.subList(parte * 4, ids.size()));
        return retorno;
    }

    public List<String> getMetodosDoCalculador() {
        return CalculadorIPTU.getMetodosDisponiveis();
    }

    public AssistenteCalculadorIPTU getAssistente() {
        return assistente;
    }

    public void abortar() {
        for (Future f : futures) {
            f.cancel(true);
        }
    }

    public void imprimeResumoDeCalculo() {
        FacesUtil.redirecionamentoInterno("/relatorios/relatorio-de-iptu/" + selecionado.getId());
    }

    public boolean isProcessoJaEfetivado() {
        novoCalculoFacade.verificarEfefetivacao(selecionado);
        return selecionado.getEfetivado() != null;
    }

    public String getUrlAtual() {
        return urlAtual;
    }

    public void consultarAndamentoCalculo() {
        if (futures != null && !futures.isEmpty()) {
            boolean terminou = true;
            for (Future<AssistenteCalculadorIPTU> futureDaVez : futures) {
                if (!futureDaVez.isDone()) {
                    terminou = false;
                }
            }
            if (terminou) {
                futures = null;
                FacesUtil.executaJavaScript("concluirCalculo()");
            }
        }

    }


    public void abrirConclusaoCalculo() {
        FacesUtil.executaJavaScript("conclusao.show()");
    }

}
