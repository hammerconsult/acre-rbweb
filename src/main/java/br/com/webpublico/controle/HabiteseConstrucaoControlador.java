package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.*;
import br.com.webpublico.enums.tributario.regularizacaoconstrucao.TipoConstrucao;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.HabiteseConstrucaoFacade;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
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
import javax.faces.model.SelectItem;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoHabiteseConstrucao",
        pattern = "/habitese-construcao/novo/",
        viewId = "/faces/tributario/habiteseconstrucao/edita.xhtml"),
    @URLMapping(id = "editarHabiteseConstrucao",
        pattern = "/habitese-construcao/editar/#{habiteseConstrucaoControlador.id}/",
        viewId = "/faces/tributario/habiteseconstrucao/edita.xhtml"),
    @URLMapping(id = "listarHabiteseConstrucao",
        pattern = "/habitese-construcao/listar/",
        viewId = "/faces/tributario/habiteseconstrucao/lista.xhtml"),
    @URLMapping(id = "verHabiteseConstrucao",
        pattern = "/habitese-construcao/ver/#{habiteseConstrucaoControlador.id}/",
        viewId = "/faces/tributario/habiteseconstrucao/visualizar.xhtml"),
})
public class HabiteseConstrucaoControlador extends PrettyControlador<Habitese> implements CRUD {

    @EJB
    private HabiteseConstrucaoFacade habiteseConstrucaoFacade;
    private List<ResultadoParcela> resultadoConsulta;
    private ProcRegularizaConstrucao procRegularizaConstrucao;
    private ServicosAlvaraConstrucao servicoAtual;
    private String emails;
    private String mensagemEmail;

    private String enderecoCompleto;
    private String descricaoProprietarios;
    private String inscricaoCadastral;

    public HabiteseConstrucaoControlador() {
        super(Habitese.class);
    }

    private void buscarInformacoesAlvara() {
        if (selecionado != null && selecionado.getAlvaraConstrucao() != null && selecionado.getAlvaraConstrucao().getProcRegularizaConstrucao() != null) {
            if (enderecoCompleto == null) {
                enderecoCompleto = selecionado.getAlvaraConstrucao().getProcRegularizaConstrucao().getCadastroImobiliario().getEnderecoCompleto();
            }
            if (descricaoProprietarios == null) {
                descricaoProprietarios = selecionado.getAlvaraConstrucao().getProcRegularizaConstrucao().getCadastroImobiliario().getDescricaoProprietarios();
            }
            if (inscricaoCadastral == null) {
                inscricaoCadastral = selecionado.getAlvaraConstrucao().getProcRegularizaConstrucao().getCadastroImobiliario().getInscricaoCadastral();
            }
        }
    }

    public void enviarPorEmail() {
        try {
            String[] emailsSeparados = validarAndSepararEmails();
            habiteseConstrucaoFacade.enviarEmail(selecionado, mensagemEmail, emailsSeparados);
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

    public void calcular() {
        try {
            selecionado = habiteseConstrucaoFacade.gerarCalculoHabitese(selecionado, false);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void efetivarCalculo() {
        try {
            habiteseConstrucaoFacade.gerarCalculoHabitese(selecionado, true);
            FacesUtil.addOperacaoRealizada("Calculo efetivado com sucesso!");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void emitirDam() {
        try {
            habiteseConstrucaoFacade.emitirDAM(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void emitirTermo() {
        try {
            habiteseConstrucaoFacade.emitirTermo(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarSelecionarProcesso() {
        ValidacaoException ve = new ValidacaoException();
        if (procRegularizaConstrucao == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi possível recuperar o dados do processo de regularização!");
        } else {
            procRegularizaConstrucao = habiteseConstrucaoFacade.getAlvaraConstrucaoFacade().getProcRegularizaConstrucaoFacade().recarregar(procRegularizaConstrucao);
            if (procRegularizaConstrucao.getUltimoAlvara() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado o alvará de construção desse processo de regularização!");
            } else {
                if (procRegularizaConstrucao.getUltimoAlvara().isTodosPavimentosComHabitesePago() && procRegularizaConstrucao.getUltimoAlvara().getConstrucaoAlvara().getQuantidadePavimentos() > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Todos os pavimentos do alvará desse processo já possuem habite-se pagos");
                }
            }
        }
        ve.lancarException();
    }

    public void recuperarProcRegularizacao() {
        try {
            validarSelecionarProcesso();
            selecionado.setAlvaraConstrucao(procRegularizaConstrucao.getUltimoAlvara());
            popularCaracteristicas();
        } catch (ValidacaoException ve) {
            procRegularizaConstrucao = null;
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void abrirDialogAdicionarServico() {
        servicoAtual = new ServicosAlvaraConstrucao();
        servicoAtual.setCaractConstruHabitese(selecionado.getCaracteristica());
        FacesUtil.executaJavaScript("adicionarServico.show()");
    }

    private void validarAdicionarServicoAtual() {
        ValidacaoException ve = new ValidacaoException();
        if (servicoAtual.getServicoConstrucao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Serviço deve ser informado.");
        }
        if (servicoAtual.getItemServicoConstrucao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Item de Serviço deve ser informado.");
        }
        if (servicoAtual.getArea() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Área deve ser informado.");
        }
        ve.lancarException();
    }

    public void adicionarServicoAtual() {
        try {
            validarAdicionarServicoAtual();
            selecionado.getCaracteristica().getServicos().add(servicoAtual);
            servicoAtual = null;
            FacesUtil.executaJavaScript("adicionarServico.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public DAM recuperarDAM(Long parcela) {
        return habiteseConstrucaoFacade.recuperarDAM(parcela);
    }

    public List<ResultadoParcela> getParcelas() {
        if (resultadoConsulta == null || resultadoConsulta.isEmpty()) {
            resultadoConsulta = habiteseConstrucaoFacade.buscarParcelas(selecionado.getProcessoCalcAlvaConstHabi());
        }
        return resultadoConsulta;
    }

    public List<HabiteseClassesConstrucao> completarClasses(String parte) {
        return habiteseConstrucaoFacade.getHabiteseClassesConstrucaoFacade().listarFiltrando(parte.trim());
    }

    public List<ProcRegularizaConstrucao> completar(String parte) {
        return habiteseConstrucaoFacade.getAlvaraConstrucaoFacade().getProcRegularizaConstrucaoFacade().listarPorSituacao(parte, ProcRegularizaConstrucao.Situacao.TAXA_VISTORIA, ProcRegularizaConstrucao.Situacao.AGUARDANDO_HABITESE);
    }

    public List<SelectItem> buscarServicosConstrucao() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        List<SelectItem> collect = habiteseConstrucaoFacade.getAlvaraConstrucaoFacade().getServicoConstrucaoFacade()
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
        List<SelectItem> collect = habiteseConstrucaoFacade.getAlvaraConstrucaoFacade().getItemServicoConstrucaoFacade()
            .buscarItemServicoPorServico(servicoAtual.getServicoConstrucao(), "")
            .stream()
            .map((item) -> new SelectItem(item, item.toString()))
            .collect(Collectors.toList());
        if (!collect.isEmpty()) toReturn.addAll(collect);
        return toReturn;
    }
    private void popularCaracteristicas() {
        selecionado.setCaracteristica(new CaracteristicaConstrucaoHabitese());
        selecionado.getCaracteristica().setHabitese(selecionado);
        selecionado.getCaracteristica().setBaseDeCalculo(BigDecimal.ZERO);
        selecionado.getCaracteristica().setTipoHabitese(CaracteristicaConstrucaoHabitese.TipoHabitese.TOTAL);
        selecionado.getCaracteristica().setTipoConstrucao(TipoConstrucao.CONSTRUCAO);
        selecionado.getCaracteristica().setQuantidadeDePavimentos(selecionado.getAlvaraConstrucao().getQuantidadePavimentosComHabiteseNaoPago());
        selecionado.getCaracteristica().setAreaConstruida(selecionado.getAlvaraConstrucao().getConstrucaoAlvara().getAreaConstruida());
        selecionado.getCaracteristica().setTempoConstrucao(1);
        for (ServicosAlvaraConstrucao servico : selecionado.getAlvaraConstrucao().getServicos()) {
            if (servico.getServicoConstrucao().getGeraHabitese()) {
                ServicosAlvaraConstrucao servicosAlvaraConstrucao = new ServicosAlvaraConstrucao();
                servicosAlvaraConstrucao.setCaractConstruHabitese(selecionado.getCaracteristica());
                servicosAlvaraConstrucao.setServicoConstrucao(servico.getServicoConstrucao());
                servicosAlvaraConstrucao.setItemServicoConstrucao(servico.getItemServicoConstrucao());
                servicosAlvaraConstrucao.setHabiteseClassesConstrucao(servico.getHabiteseClassesConstrucao());
                servicosAlvaraConstrucao.setArea(servico.getArea());
                selecionado.getCaracteristica().getServicos().add(servicosAlvaraConstrucao);
            }
        }
        if (selecionado.getCaracteristica().getServicos().isEmpty()) {
            FacesUtil.addAtencao("Nenhum serviço que gera habite-se foi encontrado para o alvará desse processo.");
        }
    }

    public BigDecimal getTotalDeducoes() {
        BigDecimal total = BigDecimal.ZERO;
        for (DeducaoHabitese deducaoHabitese : selecionado.getDeducoes()) {
            total = total.add(deducaoHabitese.getBaseDeCalculo());
        }
        return total;
    }

    public BigDecimal getTotalAreaServicos() {
        BigDecimal total = BigDecimal.ZERO;
        for (ServicosAlvaraConstrucao servico : selecionado.getCaracteristica().getServicos()) {
            total = total.add(servico.getArea());
        }
        return total;
    }

    public ProcRegularizaConstrucao getProcRegularizaConstrucao() {
        return procRegularizaConstrucao;
    }

    public void setProcRegularizaConstrucao(ProcRegularizaConstrucao procRegularizaConstrucao) {
        this.procRegularizaConstrucao = procRegularizaConstrucao;
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

    public ServicosAlvaraConstrucao getServicoAtual() {
        return servicoAtual;
    }

    public void setServicoAtual(ServicosAlvaraConstrucao servicoAtual) {
        this.servicoAtual = servicoAtual;
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

    @Override
    public boolean validaRegrasEspecificas() {
        ValidacaoException ve = new ValidacaoException();
        if ((!Strings.isNullOrEmpty(selecionado.getNumeroProtocolo()) && Strings.isNullOrEmpty(selecionado.getAnoProtocolo())) || (!Strings.isNullOrEmpty(selecionado.getAnoProtocolo()) && Strings.isNullOrEmpty(selecionado.getNumeroProtocolo()))) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o Número e Ano do Protocolo.");
        }
        if (selecionado.getCaracteristica().getServicos().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos um serviço.");
        }
        if (ve.temMensagens()) {
            ve.lancarException();
            return false;
        }
        return true;
    }

    @URLAction(mappingId = "novoHabiteseConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(Util.getSistemaControlador().getExercicioCorrente());
        selecionado.setUsuarioSistema(habiteseConstrucaoFacade.getAlvaraConstrucaoFacade().getSistemaFacade().getUsuarioCorrente());
        selecionado.setDataLancamento(habiteseConstrucaoFacade.getAlvaraConstrucaoFacade().getSistemaFacade().getDataOperacao());
        selecionado.setDataVistoria(new Date());
        buscarInformacoesAlvara();
        ProcRegularizaConstrucao proc = (ProcRegularizaConstrucao) Web.pegaDaSessao(ProcRegularizaConstrucao.class);
        if (proc != null) {
            procRegularizaConstrucao = proc;
            recuperarProcRegularizacao();
        }
    }

    @URLAction(mappingId = "editarHabiteseConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        procRegularizaConstrucao = selecionado.getAlvaraConstrucao().getProcRegularizaConstrucao();
        buscarInformacoesAlvara();
    }

    @URLAction(mappingId = "verHabiteseConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        buscarInformacoesAlvara();
    }

    @Override
    public void salvar() {
        super.salvar(Redirecionar.VER);
    }

    @Override
    public AbstractFacade getFacede() {
        return habiteseConstrucaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/habitese-construcao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
