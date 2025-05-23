package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ItemServicoConstrucao;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ParametroRegularizacao;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ServicoConstrucao;
import br.com.webpublico.enums.ClasseDoAtributo;
import br.com.webpublico.enums.tributario.regularizacaoconstrucao.TipoConstrucao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.ValidacaoExceptionComCodigo;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AlvaraConstrucaoFacade;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoAlvaraConstrucao",
        pattern = "/alvara-construcao/novo/",
        viewId = "/faces/tributario/alvaraconstrucao/edita.xhtml"),
    @URLMapping(id = "editarAlvaraConstrucao",
        pattern = "/alvara-construcao/editar/#{alvaraConstrucaoControlador.id}/",
        viewId = "/faces/tributario/alvaraconstrucao/edita.xhtml"),
    @URLMapping(id = "listarAlvaraConstrucao",
        pattern = "/alvara-construcao/listar/",
        viewId = "/faces/tributario/alvaraconstrucao/lista.xhtml"),
    @URLMapping(id = "verAlvaraConstrucao",
        pattern = "/alvara-construcao/ver/#{alvaraConstrucaoControlador.id}/",
        viewId = "/faces/tributario/alvaraconstrucao/visualizar.xhtml"),
})
public class AlvaraConstrucaoControlador extends PrettyControlador<AlvaraConstrucao> implements CRUD {

    @EJB
    private AlvaraConstrucaoFacade alvaraConstrucaoFacade;
    private ServicosAlvaraConstrucao servicoAtual;
    private List<ResultadoParcela> resultadoConsulta;
    private String emails;
    private String mensagemEmail;
    private Construcao construcao;
    private ConverterAutoComplete converterConstrucao;

    private String enderecoCompleto;
    private String descricaoProprietarios;
    private String inscricaoCadastral;
    private ItemServicoConstrucao itemServicoConstrucao;
    private List<Habitese> habitesesDisponiveis;

    public AlvaraConstrucaoControlador() {
        super(AlvaraConstrucao.class);
    }

    private void buscarInformacoesAlvara() {
        if (selecionado != null && selecionado.getProcRegularizaConstrucao() != null) {
            if (enderecoCompleto == null) {
                enderecoCompleto = selecionado.getProcRegularizaConstrucao().getCadastroImobiliario().getEnderecoCompleto();
            }
            if (descricaoProprietarios == null) {
                descricaoProprietarios = selecionado.getProcRegularizaConstrucao().getCadastroImobiliario().getDescricaoProprietarios();
            }
            if (inscricaoCadastral == null) {
                inscricaoCadastral = selecionado.getProcRegularizaConstrucao().getCadastroImobiliario().getInscricaoCadastral();
            }
        }
    }

    public void calcular() {
        try {
            validarVencido();
            validarServicoParametro();
            selecionado = alvaraConstrucaoFacade.gerarCalculo(selecionado, false);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void efetivarCalculo() {
        try {
            validarVencido();
            selecionado.setSituacao(AlvaraConstrucao.Situacao.FINALIZADO);
            selecionado = alvaraConstrucaoFacade.salvarRetornando(selecionado);
            selecionado.getProcRegularizaConstrucao().setSituacao(ProcRegularizaConstrucao.Situacao.ALVARA_CONSTRUCAO);
            alvaraConstrucaoFacade.getProcRegularizaConstrucaoFacade().salvar(selecionado.getProcRegularizaConstrucao());
            FacesUtil.addOperacaoRealizada("Calculo efetivado com sucesso!");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void emitirDam() {
        try {
            validarVencido();
            alvaraConstrucaoFacade.emitirDAM(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void emitirAlvara() {
        try {
            validarVencido();
            alvaraConstrucaoFacade.emitirAlvara(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void enviarPorEmail() {
        try {
            validarVencido();
            String[] emailsSeparados = validarAndSepararEmails();
            alvaraConstrucaoFacade.enviarEmail(selecionado, mensagemEmail, emailsSeparados);
            FacesUtil.executaJavaScript("enviarPorEmail.hide()");
            FacesUtil.addOperacaoRealizada("E-mail enviado com sucesso!");
            emails = null;
            mensagemEmail = null;
        } catch (AddressException e) {
            FacesUtil.addOperacaoNaoRealizada("O e-mail informado é invalido!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public String[] validarAndSepararEmails() throws AddressException {
        ValidacaoException ve = new ValidacaoException();
        String emailsQuebrados[] = null;
        if (emails == null || emails.trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo E-mail é obrigatório.");
        } else {
            emailsQuebrados = emails.split(Pattern.quote(","));
            for (String emailsQuebrado : emailsQuebrados) {
                InternetAddress emailAddr = new InternetAddress(emailsQuebrado);
                emailAddr.validate();
            }
        }
        if (mensagemEmail == null || mensagemEmail.trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mensagem é obrigatório.");
        }
        ve.lancarException();
        return emailsQuebrados;
    }

    public void validarVencido() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.isVencido()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O alvará de construção está vencido.");
        }
        ve.lancarException();
    }

    private void validarServicoParametro() {
        ValidacaoException ve = new ValidacaoException();
        ParametroRegularizacao parametroRegularizacao = alvaraConstrucaoFacade.getParametroRegularizacaoFacade().buscarParametroRegularizacaoPorExercicio(selecionado.getExercicio());

        if (parametroRegularizacao != null && parametroRegularizacao.getServicoConstrucao() != null) {
            for (ServicosAlvaraConstrucao servico : selecionado.getServicos()) {
                if (servico.getServicoConstrucao().equals(parametroRegularizacao.getServicoConstrucao())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Serviço: " + servico.getServicoConstrucao().getCodigo() + " - " + servico.getServicoConstrucao().getDescricao() +
                        " já foi adicionado como Serviço de Construção da Taxa de Vistoria na tela de Parâmetros.");
                }
            }
        }
        ve.lancarException();
    }

    public DAM recuperarDAM(Long parcela) {
        return alvaraConstrucaoFacade.getDamFacade().recuperarDAM(parcela);
    }

    public List<ResultadoParcela> getParcelas() {
        if (resultadoConsulta == null || resultadoConsulta.isEmpty()) {
            resultadoConsulta = alvaraConstrucaoFacade.buscarParcelas(selecionado);
        }
        return resultadoConsulta;
    }

    public void abrirDialogAdicionarServico() {
        servicoAtual = new ServicosAlvaraConstrucao();
        FacesUtil.executaJavaScript("adicionarServico.show()");
    }

    private void validarAdicionarServicoAtual() {
        ValidacaoException ve = new ValidacaoException();
        if (servicoAtual.getServicoConstrucao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Serviço deve ser informado.");
        }
        if (servicoAtual.getArea() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Área deve ser informado.");
        }
        if (servicoAtual.getItemServicoConstrucao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Item de Serviço deve ser informado.");
        }
        if (servicoAtual != null && servicoAtual.getServicoConstrucao() != null) {
            ParametroRegularizacao parametroRegularizacao = alvaraConstrucaoFacade.getParametroRegularizacaoFacade().buscarParametroRegularizacaoPorExercicio(selecionado.getExercicio());
            if (parametroRegularizacao == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe parâmetro cadastrado para o processo de regularização de construção.");
            } else {
                if (servicoAtual.getServicoConstrucao().equals(parametroRegularizacao.getServicoConstrucao())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Serviço: " + servicoAtual.getServicoConstrucao().getCodigo() + " - " + servicoAtual.getServicoConstrucao().getDescricao() +
                        " já foi adicionado como Serviço de Construção da Taxa de Vistoria na tela de Parâmetros.");
                }
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> buscarServicosConstrucao() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        List<SelectItem> collect = alvaraConstrucaoFacade.getServicoConstrucaoFacade()
            .listarFiltrando("")
            .stream()
            .map((servico) -> new SelectItem(servico, servico.toString()))
            .collect(Collectors.toList());
        if (!collect.isEmpty()) toReturn.addAll(collect);
        return toReturn;
    }

    public List<SelectItem> buscarItensServico() {
        if (servicoAtual == null || servicoAtual.getServicoConstrucao() == null) {
            return Lists.newArrayList();
        }
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        List<SelectItem> collect = alvaraConstrucaoFacade.getItemServicoConstrucaoFacade()
            .buscarItemServicoPorServico(servicoAtual.getServicoConstrucao(), "")
            .stream()
            .map((item) -> new SelectItem(item, item.toString()))
            .collect(Collectors.toList());
        if (!collect.isEmpty()) toReturn.addAll(collect);
        return toReturn;
    }

    public List<Construcao> completaConstrucao(String parte) {
        List<Construcao> construcoes = Lists.newArrayList();
        if (selecionado.getProcRegularizaConstrucao() != null) {
            for (Construcao construcoe : selecionado.getProcRegularizaConstrucao().getCadastroImobiliario().getConstrucoes()) {
                if (construcoe.toString().toLowerCase().trim().contains(parte.toLowerCase().trim())) {
                    construcoes.add(construcoe);
                }
            }
        }
        return construcoes;
    }

    public void adicionarServicoAtual() {
        try {
            validarVencido();
            validarAdicionarServicoAtual();
            servicoAtual.setAlvaraConstrucao(selecionado);
            selecionado.getServicos().add(servicoAtual);
            servicoAtual = null;
            FacesUtil.executaJavaScript("adicionarServico.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void atribuirConstrucaoSePossuir() {
        if (selecionado.getProcRegularizaConstrucao().getCadastroImobiliario().getConstrucoesAtivas().size() == 1) {
            construcao = selecionado.getProcRegularizaConstrucao().getCadastroImobiliario().getConstrucoesAtivas().get(0);
            selecionado.setConstrucaoAlvara(new ConstrucaoAlvara(construcao));
        } else if (selecionado.getProcRegularizaConstrucao().getCadastroImobiliario().getConstrucoesAtivas().isEmpty()) {
            ConstrucaoAlvara construcaoAlvara = new ConstrucaoAlvara();
            for (Atributo atributo : getAtributos()) {
                ValorAtributo valorAtributo = new ValorAtributo();
                valorAtributo.setAtributo(atributo);
                construcaoAlvara.getAtributos().put(atributo, valorAtributo);
            }
            selecionado.setConstrucaoAlvara(alvaraConstrucaoFacade.getAtributoFacade().inicializarCaracteristicasDaConstrucaoDoAlvara(construcaoAlvara));
        }
    }

    public void recuperarProcRegularizacao(SelectEvent evento) {
        selecionado.setProcRegularizaConstrucao(alvaraConstrucaoFacade.getProcRegularizaConstrucaoFacade().recuperar(((ProcRegularizaConstrucao) evento.getObject()).getId()));
        selecionado.setResponsavelServico(selecionado.getProcRegularizaConstrucao().getResponsavelProjeto());
        atribuirConstrucaoSePossuir();
    }

    public void construcaoSelecionada(SelectEvent event) {
        selecionado.setConstrucaoAlvara(new ConstrucaoAlvara(construcao));
    }

    public List<Atributo> getAtributos() {
        List<Atributo> listaAtributos = Lists.newArrayList();
        try {
            for (Atributo atributo : alvaraConstrucaoFacade.getAtributoFacade().listaAtributosPorClasse(ClasseDoAtributo.CONSTRUCAO)) {
                if ("tipo_imovel".equals(atributo.getIdentificacao()) || "utilizacao_imovel".equals(atributo.getIdentificacao()) || "padrao_residencial".equals(atributo.getIdentificacao())) {
                    listaAtributos.add(atributo);
                }
            }
            return listaAtributos;
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao inicializar os atributos da construção!" + e.getMessage());
            logger.error("Erro: ", e);
        }
        return listaAtributos;
    }

    public BigDecimal getAreaTotalConstrucao() {
        BigDecimal total = BigDecimal.ZERO;
        for (ServicosAlvaraConstrucao servico : selecionado.getServicos()) {
            if (TipoConstrucao.CONSTRUCAO.equals(servico.getServicoConstrucao().getTipoConstrucao())) {
                total = total.add(servico.getArea());
            }
        }
        return total;
    }

    public BigDecimal getAreaTotalDemolicao() {
        BigDecimal total = BigDecimal.ZERO;
        for (ServicosAlvaraConstrucao servico : selecionado.getServicos()) {
            if (TipoConstrucao.DEMOLICAO.equals(servico.getServicoConstrucao().getTipoConstrucao())) {
                total = total.add(servico.getArea());
            }
        }
        return total;
    }

    public BigDecimal getAreaTotalReforma() {
        BigDecimal total = BigDecimal.ZERO;
        for (ServicosAlvaraConstrucao servico : selecionado.getServicos()) {
            if (TipoConstrucao.REFORMA.equals(servico.getServicoConstrucao().getTipoConstrucao())) {
                total = total.add(servico.getArea());
            }
        }
        return total;
    }

    public BigDecimal getAreaTotalOutros() {
        BigDecimal total = BigDecimal.ZERO;
        for (ServicosAlvaraConstrucao servico : selecionado.getServicos()) {
            if (TipoConstrucao.OUTRO.equals(servico.getServicoConstrucao().getTipoConstrucao())) {
                total = total.add(servico.getArea());
            }
        }
        return total;
    }

    @Override
    public boolean validaRegrasEspecificas() {
        validarVencido();
        ValidacaoException ve = new ValidacaoException();
        if ((!Strings.isNullOrEmpty(selecionado.getNumeroProtocolo()) && Strings.isNullOrEmpty(selecionado.getAnoProtocolo())) || (!Strings.isNullOrEmpty(selecionado.getAnoProtocolo()) && Strings.isNullOrEmpty(selecionado.getNumeroProtocolo()))) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o Número e Ano do Protocolo");
        }
        if (selecionado.getServicos().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor inserir ao menos um serviço");
        }
        if (selecionado.getConstrucaoAlvara().getAreaConstruida() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar a área da construção");
        }
        if (selecionado.getConstrucaoAlvara().getQuantidadePavimentos() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar a quantidade de pavimentos da construção");
        }
        if (isOperacaoNovo()) {
            AlvaraConstrucao alvaraConstrucao = alvaraConstrucaoFacade.buscarUltimoAlvaraParaProcesso(false, selecionado.getProcRegularizaConstrucao());
            if (alvaraConstrucao != null && !alvaraConstrucao.isVencido()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível lançar o alvará de construção pois já existe outro alvará " + alvaraConstrucao.getSituacao().getDescricao() + " não vencido com o código <b>" + alvaraConstrucao.getCodigo() + "</b> para o processo selecionado");
            }
        }
        if (ve.temMensagens()) {
            ve.lancarException();
            return false;
        }
        return true;
    }

    @URLAction(mappingId = "novoAlvaraConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(Util.getSistemaControlador().getExercicioCorrente());
        selecionado.setUsuarioIncluiu(Util.getSistemaControlador().getUsuarioCorrente());
        selecionado.setDataExpedicao(Util.getSistemaControlador().getDataOperacao());
        Object processoSessao = Web.pegaDaSessao("processoRegularizacaoConstrucao");
        if (processoSessao != null) {
            selecionado.setProcRegularizaConstrucao((ProcRegularizaConstrucao) processoSessao);
            selecionado.setResponsavelServico(selecionado.getProcRegularizaConstrucao().getResponsavelProjeto());
            atribuirConstrucaoSePossuir();
        }
        buscarInformacoesAlvara();
    }

    @URLAction(mappingId = "editarAlvaraConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        buscarInformacoesAlvara();
    }

    @URLAction(mappingId = "verAlvaraConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        buscarInformacoesAlvara();
    }

    @Override
    public void salvar() {
        for (Atributo atributo : selecionado.getConstrucaoAlvara().getAtributos().keySet()) {
            for (CaracteristicasAlvaraConstrucao caracteristica : selecionado.getConstrucaoAlvara().getCaracteristicas()) {
                if (atributo.equals(caracteristica.getAtributo())) {
                    caracteristica.setValorAtributo(selecionado.getConstrucaoAlvara().getAtributos().get(atributo));
                }
            }
        }
        selecionado = alvaraConstrucaoFacade.salvarRetornando(selecionado);
        redireciona(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    @Override
    public void excluir() {
        Web.navegacao(getUrlAtual(), "/processo-regularizacao-construcao/ver/" + selecionado.getProcRegularizaConstrucao().getId() + "/");
        Web.limpaNavegacao();
        super.excluir();
    }

    @Override
    public AbstractFacade getFacede() {
        return alvaraConstrucaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/alvara-construcao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public ServicosAlvaraConstrucao getServicoAtual() {
        return servicoAtual;
    }

    public void setServicoAtual(ServicosAlvaraConstrucao servicoAtual) {
        this.servicoAtual = servicoAtual;
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public String getMensagemEmail() {
        return mensagemEmail;
    }

    public void setMensagemEmail(String mensagemEmail) {
        this.mensagemEmail = mensagemEmail;
    }

    public Construcao getConstrucao() {
        return construcao;
    }

    public void setConstrucao(Construcao construcao) {
        this.construcao = construcao;
    }

    public String getEnderecoCompleto() {
        return enderecoCompleto;
    }

    public void setEnderecoCompleto(String enderecoCompleto) {
        this.enderecoCompleto = enderecoCompleto;
    }

    public String getDescricaoProprietarios() {
        return descricaoProprietarios;
    }

    public void setDescricaoProprietarios(String descricaoProprietarios) {
        this.descricaoProprietarios = descricaoProprietarios;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public ConverterAutoComplete getConverterConstrucao() {
        if (converterConstrucao == null) {
            converterConstrucao = new ConverterAutoComplete(Construcao.class, alvaraConstrucaoFacade.getConstrucaoFacade());
        }
        return converterConstrucao;
    }

    public void setConverterConstrucao(ConverterAutoComplete converterConstrucao) {
        this.converterConstrucao = converterConstrucao;
    }

    public ItemServicoConstrucao getItemServicoConstrucao() {
        return itemServicoConstrucao;
    }

    public void setItemServicoConstrucao(ItemServicoConstrucao itemServicoConstrucao) {
        this.itemServicoConstrucao = itemServicoConstrucao;
    }

    public boolean canEmitirTaxaVistoria() {
        return selecionado.getProcRegularizaConstrucao() != null &&
            (ProcRegularizaConstrucao.Situacao.ALVARA_CONSTRUCAO.equals(selecionado.getProcRegularizaConstrucao().getSituacao()) ||
                ProcRegularizaConstrucao.Situacao.AGUARDANDO_TAXA_VISTORIA.equals(selecionado.getProcRegularizaConstrucao().getSituacao()));
    }

    public void emitirTaxaVistoria() {
        try {
            alvaraConstrucaoFacade.emitirDAM(selecionado);
            selecionado = alvaraConstrucaoFacade.recarregar(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            e = Util.getRootCauseEJBException(e);
            if (e instanceof ValidacaoException) {
                FacesUtil.printAllFacesMessages(((ValidacaoException) e).getMensagens());
                logger.error("Erro ao emitirTaxaVistoria {} ", e);
            }
        }
    }

    public List<Habitese> getHabitesesDisponiveis() {
        return habitesesDisponiveis;
    }

    public void setHabitesesDisponiveis(List<Habitese> habitesesDisponiveis) {
        this.habitesesDisponiveis = habitesesDisponiveis;
    }

    public boolean canIrParaHabitese() {
        if (selecionado.getProcRegularizaConstrucao() != null &&
            (ProcRegularizaConstrucao.Situacao.TAXA_VISTORIA.equals(selecionado.getProcRegularizaConstrucao().getSituacao())
                || ProcRegularizaConstrucao.Situacao.AGUARDANDO_HABITESE.equals(selecionado.getProcRegularizaConstrucao().getSituacao())
                || ProcRegularizaConstrucao.Situacao.HABITESE.equals(selecionado.getProcRegularizaConstrucao().getSituacao())
                || ProcRegularizaConstrucao.Situacao.FINALIZADO.equals(selecionado.getProcRegularizaConstrucao().getSituacao()))) {
            return true;
        }
        return false;
    }

    public void redirecionarHabitese() {
        habitesesDisponiveis = selecionado.getHabiteses();
        if (selecionado.getHabiteses() == null || selecionado.getHabiteses().isEmpty()) {
            redirecionarNovoHabitese();
        } else {
            FacesUtil.atualizarComponente("dialogEscolherHabitese");
            FacesUtil.executaJavaScript("dlgEscolherHabitese.show();");
        }
    }

    public void redirecionarHabitese(Habitese habitese) {
        Web.navegacao(getUrlAtual(), "/habitese-construcao/ver/" + habitese.getId() + "/");
    }

    public void redirecionarNovoHabitese() {
        Web.navegacao(getUrlAtual(), "/habitese-construcao/novo/", selecionado.getProcRegularizaConstrucao());
    }
}
