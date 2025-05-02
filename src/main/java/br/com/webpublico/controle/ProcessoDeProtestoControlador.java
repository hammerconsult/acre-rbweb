
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ObjetoCampoValor;
import br.com.webpublico.entidadesauxiliares.PesquisaProtestoCDA;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ProcessoDeProtestoFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.tributario.consultadebitos.enums.TipoCalculoDTO;
import br.com.webpublico.tributario.consultadebitos.enums.TipoDeDebitoDTO;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-processo-protesto",
        pattern = "/tributario/divida-ativa/processo-protesto/novo/",
        viewId = "/faces/tributario/dividaativa/processodeprotesto/edita.xhtml"),
    @URLMapping(id = "editar-processo-protesto",
        pattern = "/tributario/divida-ativa/processo-protesto/editar/#{processoDeProtestoControlador.id}/",
        viewId = "/faces/tributario/dividaativa/processodeprotesto/edita.xhtml"),
    @URLMapping(id = "ver-processo-protesto",
        pattern = "/tributario/divida-ativa/processo-protesto/ver/#{processoDeProtestoControlador.id}/",
        viewId = "/faces/tributario/dividaativa/processodeprotesto/visualizar.xhtml"),
    @URLMapping(id = "listar-processo-protesto",
        pattern = "/tributario/divida-ativa/processo-protesto/listar/",
        viewId = "/faces/tributario/dividaativa/processodeprotesto/lista.xhtml")
})
public class ProcessoDeProtestoControlador extends PrettyControlador<ProcessoDeProtesto> implements Serializable, CRUD {

    @EJB
    private ProcessoDeProtestoFacade processoDeProtestoFacade;

    private Pessoa filtroContribuinte;
    private Cadastro cadastro;
    private TipoCadastroTributario tipoCadastroTributario;
    private Divida filtroDivida;
    private Exercicio filtroExercicioInicio;
    private Exercicio filtroExercicioFinal;
    private boolean dividaDoExercicio;
    private boolean dividaAtiva;
    private boolean dividaAtivaAzuijada;

    private List<ResultadoParcela> parcelasEncontradas;
    private List<ResultadoParcela> parcelasSelecionadas;
    private List<PesquisaProtestoCDA> cdasEncontradas;
    private List<PesquisaProtestoCDA> cdasSelecionadas;
    private SituacaoParcela situacaoParcela;
    private Date vencimentoInicial;
    private Date vencimentoFinal;
    private String numeroCdaInicial;
    private String numeroCdaFinal;
    private List<ItemProcessoDeProtesto> novosItens = Lists.newArrayList();
    private List<ItemProcessoDeProtesto> itensRemovidos = Lists.newArrayList();
    private List<ObjetoCampoValor> informacoesAdicionais;
    private Map<Long, String> mapParcelaCda;

    private LazyDataModel<ItemProcessoDeProtesto> itensDoProcesso;

    public ProcessoDeProtestoControlador() {
        super(ProcessoDeProtesto.class);
        this.parcelasEncontradas = Lists.newArrayList();
        this.cdasEncontradas = Lists.newArrayList();
        this.informacoesAdicionais = Lists.newArrayList();
        this.mapParcelaCda = Maps.newHashMap();

    }

    @Override
    public AbstractFacade getFacede() {
        return processoDeProtestoFacade;
    }


    @Override
    public void salvar() {
        try {
            validarProcesso();
            selecionado = processoDeProtestoFacade.salvarRetornando(selecionado);
            for (ItemProcessoDeProtesto novo : novosItens) {
                novo.setProcessoDeProtesto(selecionado);
            }
            processoDeProtestoFacade.salvarItens(novosItens);
            processoDeProtestoFacade.deletarItens(itensRemovidos);
            redirecionarParaVisualizar();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro ao salvar: " + e.getMessage());
        }
    }

    private void redirecionarParaVisualizar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    private void validarProcesso() {
        ValidacaoException ve = new ValidacaoException();
        if (StringUtils.isBlank(selecionado.getMotivo())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo ou Fundamentação Legal deve ser informado.");
        }
        if (selecionado.getAtoLegal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ato Legal deve ser informado.");
        }
        if (selecionado.getCdas() == null || selecionado.getCdas().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O processo deve conter ao menos uma CDA!");
        }
        ve.lancarException();
    }

    @URLAction(mappingId = "novo-processo-protesto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarCamposNovo();
    }

    @URLAction(mappingId = "editar-processo-protesto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        criarDataModelDosItens();
    }

    @URLAction(mappingId = "ver-processo-protesto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        criarDataModelDosItens();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/divida-ativa/processo-protesto/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void inicializarCamposNovo() {
        operacao = Operacoes.NOVO;
        novosItens = Lists.newArrayList();
        itensRemovidos = Lists.newArrayList();
        try {
            selecionado.setUsuarioIncluiu(processoDeProtestoFacade.getUsuarioCorrente());
            selecionado.setSituacao(SituacaoProcessoDebito.EM_ABERTO);
            selecionado.setLancamento(processoDeProtestoFacade.getDataOperacao());
            selecionado.setExercicio(processoDeProtestoFacade.getExercicioCorrente());
            selecionado.setCodigo(processoDeProtestoFacade.recuperarProximoCodigoPorExercicio(selecionado.getExercicio()));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro inesperado durante o processo!", " Log do servidor: " + ex.getMessage()));
        }
    }

    public List<AtoLegal> completarAtoLegal(String parte) {
        return processoDeProtestoFacade.completarAtoLegal(parte);
    }

    public List<SelectItem> montarTiposCadastro() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricaoLonga()));
        }
        return toReturn;
    }

    public void uploadArquivo(FileUploadEvent file) {
        try {
            Arquivo arq = new Arquivo();
            arq.setNome(file.getFile().getFileName());
            arq.setMimeType(processoDeProtestoFacade.getMimeType(file.getFile().getInputstream()));
            arq.setDescricao(new Date().toString());
            arq.setTamanho(file.getFile().getSize());

            ProcessoDeProtestoArquivo proArq = new ProcessoDeProtestoArquivo();
            proArq.setArquivo(processoDeProtestoFacade.novoArquivoMemoria(arq, file.getFile().getInputstream()));
            proArq.setProcessoDeProtesto(selecionado);

            selecionado.getArquivos().add(proArq);
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    public boolean habilitarBotaoSalvar() {
        inicializarFiltros();
        return selecionado.getSituacao() == null || selecionado.getSituacao().equals(SituacaoProcessoDebito.EM_ABERTO);
    }

    public void inicializarFiltros() {
        parcelasEncontradas = Lists.newArrayList();
        cdasEncontradas = Lists.newArrayList();
        filtroDivida = null;
        filtroExercicioFinal = null;
        filtroExercicioInicio = null;
        dividaAtiva = false;
        dividaAtivaAzuijada = false;
        dividaDoExercicio = false;
        numeroCdaInicial = null;
        numeroCdaFinal = null;
    }

    public void removerArquivo(ProcessoDeProtestoArquivo arquivo) {
        selecionado.getArquivos().remove(arquivo);
    }

    public List<Pessoa> recuperarPessoasCadastro() {
        List<Pessoa> pessoas = Lists.newArrayList();
        Cadastro cadastro = selecionado.getCadastroProtesto();
        if (cadastro instanceof CadastroImobiliario) {
            CadastroImobiliario cadastroIm = processoDeProtestoFacade.recuperarCadastroImobiliario(selecionado.getCadastroProtesto().getId());
            for (Propriedade p : cadastroIm.getPropriedade()) {
                pessoas.add(p.getPessoa());
            }
        }
        if (cadastro instanceof CadastroRural) {
            CadastroRural cadastroRu = processoDeProtestoFacade.recuperarCadastroRural(cadastro.getId());
            for (PropriedadeRural p : cadastroRu.getPropriedade()) {
                pessoas.add(p.getPessoa());
            }
        }
        if (cadastro instanceof CadastroEconomico) {
            CadastroEconomico cadastroEco = processoDeProtestoFacade.recuperarCadastroEconomico(cadastro.getId());
            for (SociedadeCadastroEconomico sociedadeCadastroEconomico : cadastroEco.getSociedadeCadastroEconomico()) {
                pessoas.add(sociedadeCadastroEconomico.getSocio());
            }
        }
        return pessoas;
    }

    public SituacaoCadastroEconomico getSituacaoCadastroEconomico() {
        if (selecionado.getCadastroProtesto() != null) {
            return processoDeProtestoFacade.recuperarUltimaSituacaoDoCadastroEconomico((CadastroEconomico) selecionado.getCadastroProtesto());
        } else {
            return new SituacaoCadastroEconomico();
        }
    }

    public Testada getTestadaPrincipal() {
        if (selecionado.getCadastroProtesto() != null) {
            return processoDeProtestoFacade.recuperarTestadaPrincipal(((CadastroImobiliario) selecionado.getCadastroProtesto()).getLote());
        } else {
            return new Testada();
        }
    }

    public void abrirDialogConsultaCda() {
        if (selecionado.getCadastroProtesto() != null || selecionado.getPessoaProtesto() != null) {
            cdasEncontradas = Lists.newArrayList();
            cdasSelecionadas = Lists.newArrayList();
            cadastro = selecionado.getCadastroProtesto();
            filtroContribuinte = selecionado.getPessoaProtesto();
            tipoCadastroTributario = selecionado.getTipoCadastroTributario();
            FacesUtil.executaJavaScript("dlgCdas.show()");
            FacesUtil.atualizarComponente("formConsultaCda");
        } else {
            FacesUtil.addCampoObrigatorio("Informe o cadastro!");
        }
    }

    public void abrirDialogConsultaDebitos() {
        if (selecionado.getCadastroProtesto() != null || selecionado.getPessoaProtesto() != null) {
            parcelasEncontradas = Lists.newArrayList();
            parcelasSelecionadas = Lists.newArrayList();
            cadastro = selecionado.getCadastroProtesto();
            filtroContribuinte = selecionado.getPessoaProtesto();
            tipoCadastroTributario = selecionado.getTipoCadastroTributario();
            FacesUtil.executaJavaScript("dlgParcelas.show()");
            FacesUtil.atualizarComponente("formConsulta");
        } else {
            FacesUtil.addCampoObrigatorio("Informe o cadastro!");
        }
    }

    public void atribuirInscricaoCadastro() {
        selecionado.setCadastroProtesto(null);
        selecionado.setPessoaProtesto(null);
    }

    public List<Divida> completarDividas(String parte) {
        return processoDeProtestoFacade.listaFiltrandoDividas(parte.trim());
    }

    public void pesquisarCda() {
        try {
            validarCamposPreenchidos();
            cdasEncontradas.clear();
            List<CertidaoDividaAtiva> cdas = processoDeProtestoFacade.buscarCdasPorFiltros(cadastro,
                selecionado.getPessoaProtesto(), filtroExercicioInicio, filtroExercicioFinal,
                numeroCdaInicial, numeroCdaFinal);
            for (CertidaoDividaAtiva cda : cdas) {
                PesquisaProtestoCDA pesquisaProtestoCDA = new PesquisaProtestoCDA();
                pesquisaProtestoCDA.setCertidaoDividaAtiva(cda);
                pesquisaProtestoCDA.setParcelas(processoDeProtestoFacade.buscarParcelasDaCda(cda));
                pesquisaProtestoCDA.setDataPrescricao(getMenorDataPrescricao(pesquisaProtestoCDA.getParcelas()));
                cdasEncontradas.add(pesquisaProtestoCDA);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        if (cdasEncontradas.isEmpty()) {
            FacesUtil.addError("A pesquisa não encontrou nenhuma CDA!", "");
        }
    }

    private Date getMenorDataPrescricao(List<ResultadoParcela> parcelas) {
        return parcelas.stream()
            .map(ResultadoParcela::getPrescricao)
            .filter(Objects::nonNull)
            .min(Date::compareTo)
            .orElse(null);
    }

    public void pesquisar() {
        try {
            validarCamposPreenchidos();
            parcelasEncontradas.clear();
            if (selecionado.getTipoCadastroTributario().equals(TipoCadastroTributario.PESSOA)) {
                ConsultaParcela consulta = new ConsultaParcela();
                List<BigDecimal> cadastros = processoDeProtestoFacade.buscarIdsCadastrosAtivosDaPessoa(selecionado.getPessoaProtesto(), false);
                if (!cadastros.isEmpty()) {
                    adicionarParametro(consulta);
                    consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IN, cadastros);
                    consulta.executaConsulta();
                    parcelasEncontradas.addAll(consulta.getResultados());
                }
                consulta = new ConsultaParcela();
                adicionarParametro(consulta);
                consulta.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, selecionado.getPessoaProtesto().getId());
                consulta.addComplementoDoWhere(" and vw.cadastro_id is null");
                consulta.executaConsulta();
                parcelasEncontradas.addAll(consulta.getResultados());
                Collections.sort(parcelasEncontradas, ResultadoParcela.getComparatorByValuePadrao());

            } else {
                ConsultaParcela consulta = new ConsultaParcela();
                adicionarParametro(consulta);
                consulta.executaConsulta();
                parcelasEncontradas.addAll(consulta.getResultados());
                Collections.sort(parcelasEncontradas, ResultadoParcela.getComparatorByValuePadraoSemCadastro());
            }
            for (ResultadoParcela parcela : parcelasEncontradas) {
                if (parcela.getTipoCalculoEnumValue().isCancelamentoParcelamento()) {
                    Long idParcela = processoDeProtestoFacade.buscarIdParcelaOriginalDaParcelaDoCancelamento(parcela.getIdParcela());
                    if (idParcela != null) {
                        List<CertidaoDividaAtiva> cdas = processoDeProtestoFacade.buscarCertidoesDividaAtivaDaParcela(idParcela);
                        if (!cdas.isEmpty()) {
                            mapParcelaCda.put(parcela.getIdParcela(), cdas.get(0).getNumeroCdaComExercicio());
                        }
                    }
                } else {
                    String cda = processoDeProtestoFacade.buscarNumeroCDA(parcela.getIdParcela());
                    mapParcelaCda.put(parcela.getIdParcela(), cda);
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        try {
            if (parcelasEncontradas.isEmpty()) {
                FacesUtil.addError("A pesquisa não encontrou nenhum Débito!", "");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro durante a pesquisa: " + e.getMessage());
        }
    }

    private void validarCamposPreenchidos() {
        ValidacaoException ve = new ValidacaoException();
        if ((selecionado.getCadastroProtesto() == null && selecionado.getPessoaProtesto() == null) && selecionado.getTipoCadastroTributario() != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Quando informado o tipo de cadastro, é necessário que seja informado também o cadastro.");
        } else if (filtroExercicioInicio != null && filtroExercicioFinal != null) {
            if (filtroExercicioFinal.getAno().compareTo(filtroExercicioInicio.getAno()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Exercício final deve ser inferior ao exercício inicial.");
            }
        } else if (filtroExercicioInicio != null && filtroExercicioFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Exercício final deve ser informado.");
        } else if (filtroExercicioInicio == null && filtroExercicioFinal != null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Exercício inicial deve ser informado.");
        } else if (vencimentoInicial != null && vencimentoFinal != null) {
            if (vencimentoInicial.compareTo(vencimentoFinal) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Vencimento Final deve ser menor ou igual à Data de Vencimento Inicial.");
            }
        }
        ve.lancarException();
    }

    public ConsultaParcela adicionarParametro(ConsultaParcela consulta) {
        if (filtroExercicioInicio != null && filtroExercicioFinal != null) {
            consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MAIOR_IGUAL, filtroExercicioInicio.getAno());
            consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MENOR_IGUAL, filtroExercicioFinal.getAno());
        }
        if (selecionado.getCadastroProtesto() != null && selecionado.getCadastroProtesto().getId() != null) {
            consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, selecionado.getCadastroProtesto().getId());
        }

        if (filtroDivida != null) {
            consulta.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IGUAL, filtroDivida.getId());
        }

        if (dividaDoExercicio && !dividaAtiva && !dividaAtivaAzuijada) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 0);
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 0);
        } else if (dividaDoExercicio && dividaAtiva && !dividaAtivaAzuijada) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 0);
        } else if (dividaDoExercicio && !dividaAtiva && dividaAtivaAzuijada) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 0);
        } else if (!dividaDoExercicio && dividaAtiva && dividaAtivaAzuijada) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 1);
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 1);
        } else if (!dividaDoExercicio && dividaAtiva && !dividaAtivaAzuijada) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 1);
        } else if (!dividaDoExercicio && !dividaAtiva && dividaAtivaAzuijada) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 1);
        }

        if (vencimentoInicial != null && vencimentoFinal != null) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR_IGUAL, vencimentoFinal);
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MAIOR_IGUAL, vencimentoInicial);
        }
        consulta.addOrdem(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Ordem.TipoOrdem.ASC).addOrdem(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Ordem.TipoOrdem.ASC);

        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);

        return consulta;
    }

    public void adicionarCdas() {
        if (!cdasSelecionadas.isEmpty()) {
            try {
                validarCdaParaAdicionar();
                for (PesquisaProtestoCDA pesquisaProtestoCDA : cdasSelecionadas) {
                    processoDeProtestoFacade.adicionarCda(pesquisaProtestoCDA.getCertidaoDividaAtiva(),
                        selecionado, selecionado.getCdas(), selecionado.getItens(),
                        cdasEncontradas.stream().map(PesquisaProtestoCDA::getCertidaoDividaAtiva).collect(Collectors.toList()));
                }
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        } else {
            FacesUtil.addOperacaoNaoRealizada("Nenhuma CDA foi selecionada!");
        }
    }

    public void adicionarParcelas() {
        if (!parcelasSelecionadas.isEmpty()) {
            try {
                validarParcelaParaAdicionar();
                for (ResultadoParcela resultadoParcela : parcelasSelecionadas) {
                    processoDeProtestoFacade.adicionarItem(resultadoParcela, selecionado, novosItens, parcelasEncontradas);
                }
                criarDataModelDosItens();
                FacesUtil.addOperacaoRealizada("Parcelas adicionadas no Processo de Protesto!");
                FacesUtil.atualizarComponente("Formulario");
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        } else {
            FacesUtil.addOperacaoNaoRealizada("Nenhuma parcela foi selecionada!");
        }
    }

    private void validarParcelaParaAdicionar() {
        ValidacaoException ve = new ValidacaoException();
        for (ResultadoParcela resultado : parcelasSelecionadas) {
            if (processoDeProtestoFacade.hasProcessoDeProtestoAtivoParaParcela(resultado.getIdParcela())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido adicionar parcela que ja esteja adicionada em um processo de protesto ativo.");
            }
        }
        ve.lancarException();
    }

    private void validarCdaParaAdicionar() {
        ValidacaoException ve = new ValidacaoException();
        for (PesquisaProtestoCDA pesquisaProtestoCDA : cdasSelecionadas) {
            if (pesquisaProtestoCDA.getParcelas().stream().anyMatch(p -> p.getEnumTipoDeDebito().equals(TipoDeDebitoDTO.DAP) ||
                p.getEnumTipoDeDebito().equals(TipoDeDebitoDTO.AJZP))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido adicionar a CDA "+
                    pesquisaProtestoCDA.getCertidaoDividaAtiva().getNumeroCdaComExercicio() +
                    ", a mesma contém débito(s) protestado(s)!");
            }
            for (ResultadoParcela parcela : pesquisaProtestoCDA.getParcelas()) {
                if ((parcela.isParcelado() && TipoCalculoDTO.INSCRICAO_DA.equals(parcela.getTipoCalculoEnumValue())) ||
                    (parcela.isCancelado() && TipoCalculoDTO.PARCELAMENTO.equals(parcela.getTipoCalculoEnumValue()))) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido adicionar a CDA " +
                        pesquisaProtestoCDA.getCertidaoDividaAtiva().getNumeroCdaComExercicio() +
                        ", existem parcelas em parcelamento ativo!");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    private void criarDataModelDosItens() {
        itensDoProcesso = new LazyDataModel<ItemProcessoDeProtesto>() {
            @Override
            public List<ItemProcessoDeProtesto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                setRowCount(processoDeProtestoFacade.contarItens(selecionado, itensRemovidos) + novosItens.size());
                if (novosItens != null && novosItens.size() > first) {
                    List<ItemProcessoDeProtesto> itens = novosItens.subList(first, first + (Math.min(novosItens.size(), pageSize)));
                    if (itens.size() < pageSize) {
                        itens.addAll(processoDeProtestoFacade.buscarItemProcesso(0, pageSize - itens.size(), selecionado, itensRemovidos));
                    }
                    buscarCdaDasParcelas(itens);
                    return itens;
                }
                List<ItemProcessoDeProtesto> itens = processoDeProtestoFacade.buscarItemProcesso(first, pageSize, selecionado, itensRemovidos);
                buscarCdaDasParcelas(itens);
                return itens;
            }

            private void buscarCdaDasParcelas(List<ItemProcessoDeProtesto> itens) {
                for (ItemProcessoDeProtesto item : itens) {
                    if (item.getResultadoParcela().getTipoCalculoEnumValue().isCancelamentoParcelamento()) {
                        Long idParcela = processoDeProtestoFacade.buscarIdParcelaOriginalDaParcelaDoCancelamento(item.getParcela().getId());
                        if (idParcela != null) {
                            List<CertidaoDividaAtiva> cdas = processoDeProtestoFacade.buscarCertidoesDividaAtivaDaParcela(idParcela);
                            if (!cdas.isEmpty()) {
                                mapParcelaCda.put(item.getParcela().getId(), cdas.get(0).getNumeroCdaComExercicio());
                            }
                        }
                    } else {
                        String cda = processoDeProtestoFacade.buscarNumeroCDA(item.getParcela().getId());
                        mapParcelaCda.put(item.getParcela().getId(), cda);
                    }
                }
            }
        };
    }

    public void removerItem(ItemProcessoDeProtesto item) {
        if (item != null) {
            itensRemovidos.add(item);
            novosItens.remove(item);
        }
    }

    public void removerCda(CdaProcessoDeProtesto cda) {
        if (cda != null) {
            processoDeProtestoFacade.removerParcelasDaCda(cda.getCda(), selecionado, selecionado.getItens());
            selecionado.getCdas().remove(cda);
        }
    }

    public void processarProtesto() {
        try {
            selecionado = processoDeProtestoFacade.processarProtesto(selecionado);
            FacesUtil.addOperacaoRealizada("Processo processado com sucesso!");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao processar processo de protesto. Detalhes: " + e.getMessage());
            logger.error("Erro ao processar processo de protesto. ", e);
        }
    }

    public void cancelarProtesto() {
        try {
            selecionado = processoDeProtestoFacade.cancelarProtesto(selecionado, "Cancelamento manual.");
            FacesUtil.addOperacaoRealizada("Processo cancelado com sucesso!");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao cancelar processo de protesto. Detalhes: " + e.getMessage());
            logger.error("Erro ao cancelar processo de protesto. ", e);
        }
    }

    public void excluirProtesto() {
        try {
            validarExclusaoProtesto();
            super.excluir();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao excluir processo de protesto. Detalhes: " + e.getMessage());
            logger.error("Erro ao excluir processo de protesto. ", e);
        }
    }

    private void validarExclusaoProtesto() {
        ValidacaoException ve = new ValidacaoException();
        if (SituacaoProcessoDebito.FINALIZADO.equals(selecionado.getSituacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Processo de Protesto precisa se Cancelado antes de ser Excluído.");
        }
        ve.lancarException();
    }

    public void imprimirDocumento() {
        try {
            processoDeProtestoFacade.gerarDocumento(selecionado);
        } catch (UFMException | AtributosNulosException e) {
            logger.error("Erro ao gerar o documento", e);
        }
    }

    public String recuperarCDA(Long idParcela) {
        return mapParcelaCda.get(idParcela);
    }

    public void atribuirResultadoDetalhamento(ResultadoParcela parcela) {
        informacoesAdicionais = processoDeProtestoFacade.atribuirResultadoDetalhamento(parcela);
    }

    public void adicionarParcela(ResultadoParcela parcela) {
        adicionarParcela(parcela, true);
    }

    public void adicionarCda(PesquisaProtestoCDA pesquisaProtestoCDA) {
        Util.adicionarObjetoEmLista(cdasSelecionadas, pesquisaProtestoCDA);
    }

    public void adicionarParcela(ResultadoParcela parcela, boolean validar) {
        if (validar) {
            String numeroCDA = mapParcelaCda.get(parcela.getIdParcela());
            if (StringUtils.isBlank(numeroCDA)) {
                FacesUtil.addOperacaoNaoPermitida("A parcela não pode ser selecionada pois não possui CDA.");
            } else {
                Util.adicionarObjetoEmLista(parcelasSelecionadas, parcela);
            }
        } else {
            Util.adicionarObjetoEmLista(parcelasSelecionadas, parcela);
        }
    }

    public void removerParcela(ResultadoParcela parcela) {
        parcelasSelecionadas.remove(parcela);
    }

    public void removerCda(PesquisaProtestoCDA pesquisaProtestoCDA) {
        cdasSelecionadas.remove(pesquisaProtestoCDA);
    }

    public void adicionarTodasParcelas() {
        parcelasSelecionadas.clear();
        boolean exibirValidacao = false;
        for (ResultadoParcela parcela : parcelasEncontradas) {
            String numeroCDA = mapParcelaCda.get(parcela.getIdParcela());
            if (StringUtils.isBlank(numeroCDA) && !exibirValidacao) {
                exibirValidacao = true;
            } else if (!StringUtils.isBlank(numeroCDA)) {
                adicionarParcela(parcela, false);
            }
        }
        if (exibirValidacao) {
            FacesUtil.addOperacaoNaoPermitida("Algumas parcelas não foram selecionadas pois não possuem CDA.");
        }
    }

    public void adicionarTodasCdas() {
        cdasSelecionadas.clear();
        for (PesquisaProtestoCDA pesquisaProtestoCDA : cdasEncontradas) {
            adicionarCda(pesquisaProtestoCDA);
        }
    }

    public void removerTodasParcelas() {
        parcelasSelecionadas.clear();
    }

    public void removerTodasCdas() {
        cdasSelecionadas.clear();
    }

    public boolean hasParcelaAdicionada(ResultadoParcela parcela) {
        return parcelasSelecionadas.contains(parcela);
    }

    public boolean hasCdaAdicionada(PesquisaProtestoCDA pesquisaProtestoCDA) {
        return cdasSelecionadas.contains(pesquisaProtestoCDA);
    }

    public boolean hasTodasParcelasAdicionadas() {
        return parcelasSelecionadas.containsAll(parcelasEncontradas);
    }

    public boolean hasTodasCdasAdicionadas() {
        return cdasSelecionadas.containsAll(cdasEncontradas);
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public Pessoa getFiltroContribuinte() {
        return filtroContribuinte;
    }

    public void setFiltroContribuinte(Pessoa filtroContribuinte) {
        this.filtroContribuinte = filtroContribuinte;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public Divida getFiltroDivida() {
        return filtroDivida;
    }

    public void setFiltroDivida(Divida filtroDivida) {
        this.filtroDivida = filtroDivida;
    }

    public Exercicio getFiltroExercicioInicio() {
        return filtroExercicioInicio;
    }

    public void setFiltroExercicioInicio(Exercicio filtroExercicioInicio) {
        this.filtroExercicioInicio = filtroExercicioInicio;
    }

    public Exercicio getFiltroExercicioFinal() {
        return filtroExercicioFinal;
    }

    public void setFiltroExercicioFinal(Exercicio filtroExercicioFinal) {
        this.filtroExercicioFinal = filtroExercicioFinal;
    }

    public boolean isDividaDoExercicio() {
        return dividaDoExercicio;
    }

    public void setDividaDoExercicio(boolean dividaDoExercicio) {
        this.dividaDoExercicio = dividaDoExercicio;
    }

    public boolean isDividaAtiva() {
        return dividaAtiva;
    }

    public void setDividaAtiva(boolean dividaAtiva) {
        this.dividaAtiva = dividaAtiva;
    }

    public boolean isDividaAtivaAzuijada() {
        return dividaAtivaAzuijada;
    }

    public void setDividaAtivaAzuijada(boolean dividaAtivaAzuijada) {
        this.dividaAtivaAzuijada = dividaAtivaAzuijada;
    }

    public List<PesquisaProtestoCDA> getCdasEncontradas() {
        return cdasEncontradas;
    }

    public void setCdasEncontradas(List<PesquisaProtestoCDA> cdasEncontradas) {
        this.cdasEncontradas = cdasEncontradas;
    }

    public List<PesquisaProtestoCDA> getCdasSelecionadas() {
        return cdasSelecionadas;
    }

    public void setCdasSelecionadas(List<PesquisaProtestoCDA> cdasSelecionadas) {
        this.cdasSelecionadas = cdasSelecionadas;
    }

    public List<ResultadoParcela> getParcelasEncontradas() {
        return parcelasEncontradas;
    }

    public void setParcelasEncontradas(List<ResultadoParcela> parcelasEncontradas) {
        this.parcelasEncontradas = parcelasEncontradas;
    }

    public SituacaoParcela getSituacaoParcela() {
        return situacaoParcela;
    }

    public void setSituacaoParcela(SituacaoParcela situacaoParcela) {
        this.situacaoParcela = situacaoParcela;
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

    public String getNumeroCdaInicial() {
        return numeroCdaInicial;
    }

    public void setNumeroCdaInicial(String numeroCdaInicial) {
        this.numeroCdaInicial = numeroCdaInicial;
    }

    public String getNumeroCdaFinal() {
        return numeroCdaFinal;
    }

    public void setNumeroCdaFinal(String numeroCdaFinal) {
        this.numeroCdaFinal = numeroCdaFinal;
    }

    public List<ItemProcessoDeProtesto> getNovosItens() {
        return novosItens;
    }

    public void setNovosItens(List<ItemProcessoDeProtesto> novosItens) {
        this.novosItens = novosItens;
    }

    public LazyDataModel<ItemProcessoDeProtesto> getItensDoProcesso() {
        return itensDoProcesso;
    }

    public void setItensDoProcesso(LazyDataModel<ItemProcessoDeProtesto> itensDoProcesso) {
        this.itensDoProcesso = itensDoProcesso;
    }

    public List<ObjetoCampoValor> getInformacoesAdicionais() {
        return informacoesAdicionais;
    }

    public void setInformacoesAdicionais(List<ObjetoCampoValor> informacoesAdicionais) {
        this.informacoesAdicionais = informacoesAdicionais;
    }
}
