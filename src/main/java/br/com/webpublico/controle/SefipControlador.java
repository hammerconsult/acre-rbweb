/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.DependenciasDirf;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SefipFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@ManagedBean
@SessionScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoSefip", pattern = "/sefip/novo/", viewId = "/faces/rh/administracaodepagamento/arquivosefip/edita.xhtml"),
    @URLMapping(id = "listaSefip", pattern = "/sefip/listar/", viewId = "/faces/rh/administracaodepagamento/arquivosefip/lista.xhtml"),
    @URLMapping(id = "verSefip", pattern = "/sefip/ver/#{sefipControlador.id}/", viewId = "/faces/rh/administracaodepagamento/arquivosefip/visualizar.xhtml"),
    @URLMapping(id = "logSefip", pattern = "/sefip/acompanhamento/", viewId = "/faces/rh/administracaodepagamento/arquivosefip/log.xhtml")
})
public class SefipControlador extends PrettyControlador<Sefip> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(SefipControlador.class);
    @EJB
    private SefipFacade facade;
    private File arquivoSefip;
    private StreamedContent fileDownload;
    private DependenciasDirf dependenciasDirf;
    private List<FolhaDePagamento> folhasDePagamento;
    private ItemEntidadeDPContas itemEntidadeDPContas;
    private ConverterGenerico converterRecolhimentoSEFIP;
    private ConverterAutoComplete converterPessoaFisica;

    public SefipControlador() {
        super(Sefip.class);
    }

    public List<FolhaDePagamento> getFolhasDePagamento() {
        return folhasDePagamento;
    }

    public void setFolhasDePagamento(List<FolhaDePagamento> folhasDePagamento) {
        this.folhasDePagamento = folhasDePagamento;
    }

    @Override
    @URLAction(mappingId = "verSefip", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        selecionado.setArquivo(facade.getArquivoFacade().recuperaDependencias(selecionado.getArquivo().getId()));
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public StreamedContent getFileDownload() throws FileNotFoundException, IOException {
        InputStream stream = new FileInputStream(arquivoSefip);
        fileDownload = new DefaultStreamedContent(stream, "text/plain", "SEFIP.RE");
        return fileDownload;
    }

    public void escolherFolhasDePagamento() {
        try {
            validarFiltros();
            getFolhasDePagamentoEfetivadasDoMesDoSefip();
            FacesUtil.executaJavaScript("dialogFolhasDePagamento.show()");
            FacesUtil.atualizarComponente("form-folhas-de-pagamento");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void transferirFolhasDePagamento() {
        selecionado.setSefipFolhasDePagamento(new ArrayList<SefipFolhaDePagamento>());
        for (FolhaDePagamento fp : folhasDePagamento) {
            if (fp.getSelecionado() != null && fp.getSelecionado()) {
                SefipFolhaDePagamento sfp = new SefipFolhaDePagamento();
                sfp.setSefip(selecionado);
                sfp.setFolhaDePagamento(fp);
                selecionado.setSefipFolhasDePagamento(Util.adicionarObjetoEmLista(selecionado.getSefipFolhasDePagamento(), sfp));
            }
        }
    }

    private boolean aoMenosUmaFolhaSelecionada() {
        for (FolhaDePagamento fp : folhasDePagamento) {
            if (fp.getSelecionado() != null && fp.getSelecionado()) {
                return true;
            }
        }
        return false;
    }

    public void confirmarFolhasDePagamento() {
        if (!aoMenosUmaFolhaSelecionada()) {
            FacesUtil.addOperacaoNaoPermitida("Selecione ao menos uma folha de pagamento para poder prosseguir com a operação.");
            FacesUtil.executaJavaScript("aguarde.hide()");
            return;
        }

        transferirFolhasDePagamento();

        try {
            facade.getSefipAcompanhamentoFacade().jaExisteSefipGeradoMesmosParametros(selecionado);
        } catch (RuntimeException re) {
            FacesUtil.executaJavaScript("dialogArquivoJaExistente.show()");
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.atualizarComponente("form-arquivo-existente");
            return;
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
            return;
        }

        gerarSefip();
    }

    public StreamedContent montarRelatorioConferenciaSefipParaDownload() {
        return facade.getArquivoFacade().montarArquivoParaDownloadPorArquivo(selecionado.getRelatorioDeConferencia());
    }

    public boolean hasArquivo() {
        return selecionado.getRelatorioDeConferencia() != null;
    }


    public void gerarSefip() {
        Web.poeNaSessao("SEFIP", selecionado);
        FacesUtil.redirecionamentoInterno("/sefip/acompanhamento/");
    }

    public void selecionar(ActionEvent evento) {
        selecionado = (Sefip) evento.getComponent().getAttributes().get("objeto");
        try {
            selecionado.setArquivo(facade.getArquivoFacade().recuperaDependencias(selecionado.getArquivo().getId()));
        } catch (Exception e) {
            logger.error("Erro ao selecionar evento sefip {}", e);
        }
    }

    @URLAction(mappingId = "logSefip", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void log() {
        try {
            Sefip s = (Sefip) Web.pegaDaSessao("SEFIP");
            if (s != null) {
                dependenciasDirf = new DependenciasDirf();
                dependenciasDirf.iniciarProcesso();
                selecionado = s;
                selecionado.setProcessadoEm(new Date());
                selecionado.setDataOperacao(UtilRH.getDataOperacao());
                facade.gerarSefip(selecionado, dependenciasDirf, facade.getSistemaFacade().getDataOperacao(), facade.getSistemaFacade().getUsuarioCorrente());

            } else if (dependenciasDirf.getParado()) {
                FacesUtil.redirecionamentoInterno("/sefip/listar/");
            }
        } catch (RuntimeException re) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), re.getMessage());
        } catch (Exception e) {
            logger.error("Falha ao gerar arquivo SEFIP. ",e);
            FacesUtil.addErrorGenerico(e);
        }
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Mes m : Mes.values()) {
            toReturn.add(new SelectItem(m.getNumeroMes(), m.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getMesesCalendarioPagamento() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (MesCalendarioPagamento object : MesCalendarioPagamento.values()) {
            toReturn.add(new SelectItem(object.getNumeroDoMes(), object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getCodigosRecolhimentos() {
        List<SelectItem> toReturn = new ArrayList<>();
        String s;
        for (RecolhimentoSEFIP object : facade.getRecolhimentoSEFIPFacade().lista()) {
            s = object.toString();

            if (s.length() > 80) {
                s = s.substring(0, 80);
            }

            toReturn.add(new SelectItem(object, s));
        }
        return toReturn;
    }

    public List<SelectItem> buscarEntidadesParaDeclaracao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Entidade e : facade.getEntidadeFacade().buscarEntidadesParaDeclaracoes(CategoriaDeclaracaoPrestacaoContas.SEFIP, selecionado.getPrimeiroDiaDoMes(), selecionado.getUltimoDiaDoMes())) {
            toReturn.add(new SelectItem(e, e.getNome() + " - " + e.getSigla()));
        }
        return toReturn;
    }

    public Converter getConverterRecolhimentoSEFIP() {
        if (converterRecolhimentoSEFIP == null) {
            converterRecolhimentoSEFIP = new ConverterGenerico(RecolhimentoSEFIP.class, facade.getRecolhimentoSEFIPFacade());
        }
        return converterRecolhimentoSEFIP;
    }

    public List<SelectItem> getValoresFGTS() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (SefipFGTS object : SefipFGTS.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getValoresPrevidenciaSocial() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (SefipPrevidenciaSocial object : SefipPrevidenciaSocial.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getSefipModalidadesArquivos() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (SefipModalidadeArquivo object : SefipModalidadeArquivo.values()) {
            toReturn.add(new SelectItem(object, object.getCodigo() + " - " + object.getDescricao()));
        }
        return toReturn;
    }

    public List<PessoaFisica> completaPessoaFisica(String parte) {
        return facade.getPessoaFisicaFacade().listaFiltrando(parte.trim(), "nome", "cpf");
    }

    public ConverterAutoComplete getConverterPessoaFisica() {
        if (converterPessoaFisica == null) {
            converterPessoaFisica = new ConverterAutoComplete(PessoaFisica.class, facade.getPessoaFisicaFacade());
        }
        return converterPessoaFisica;
    }

    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();

        if (selecionado.getAno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Ano' é obrigatório.");
        } else {
            if (selecionado.getAno().toString().length() != 4) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo 'Ano' deve conter 4 dígitos.");
            }
        }

        if (selecionado.getEntidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Estabelecimento' é obrigatório.");
        } else {
            if (selecionado.getEntidade().getPessoaJuridica() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O estabelecimento selecionado não possui uma Pessoa Jurídica associada. Por favor, faça esta relação no cadastro de entidades.");
            }

            if (selecionado.getEntidade().getPagamentoDaGPS() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O estabelecimento selecionado não possui um Pagamento da GPS associado. Por favor, faça esta relação no cadastro de entidades.");
            }

            if (selecionado.getEntidade().getCodigoFpas() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O estabelecimento selecionado não possui o código PAS informado. Por favor, informe o código no cadastro de entidades.");
            }
        }
        if (selecionado.getResponsavel() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Responsável' é obrigatório.");
        }
        if (selecionado.getAnoInicioCompensacao() != null) {
            if (selecionado.getAnoInicioCompensacao().toString().length() != 4) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo 'Ano Início' da compensação deve conter 4 dígitos.");
            }
        }

        if (selecionado.getAnoFimCompensacao() != null) {
            if (selecionado.getAnoFimCompensacao().toString().length() != 4) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O campo 'Ano Final' da compensação deve conter 4 dígitos.");
            }
        }
        UnidadeGestora ug = facade.getUnidadeGestoraFacade().getUnidadeGestoraDo(CategoriaDeclaracaoPrestacaoContas.SEFIP);
        if (ug == null) {
            DeclaracaoPrestacaoContas dpc = facade.getDeclaracaoPrestacaoContasFacade().recuperarDeclaracaoParaFinalidade(CategoriaDeclaracaoPrestacaoContas.SEFIP);
            ve.adicionarMensagemDeOperacaoNaoPermitida("As Declarações/Prestações de Contas do SEFIP não está relacionada a nenhuma Unidade Gestora. Por favor, associe uma Unidade Gestora clicando '<b>" + Util.link("/declaracao-prestacao-contas/editar/" + dpc.getId() + "/", "AQUI") + "</b>'.");
        }
        ve.lancarException();
    }

    private void definirMesSefip() {
        selecionado.setMes(DataUtil.getMes(UtilRH.getDataOperacao()));
    }

    private void definirAnoSefip() {
        selecionado.setAno(facade.getSistemaFacade().getExercicioCorrente().getAno());
    }

    @Override
    public String getCaminhoPadrao() {
        return "/sefip/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoSefip", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        definirMesSefip();
        definirAnoSefip();
        arquivoSefip = null;
        DeclaracaoPrestacaoContas dpc = facade.getDeclaracaoPrestacaoContasFacade().recuperarDeclaracaoParaFinalidade(CategoriaDeclaracaoPrestacaoContas.SEFIP);
        if (dpc == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Não existem Declarações/Prestações de Contas cadastradas para o SEFIP. Por favor, cadastre clicando '<b>" + Util.link("/declaracao-prestacao-contas/novo/", "AQUI") + "</b>'.");
        }
    }

    public boolean orgaoEstaEmEstabelecimentoSelecionado(HierarquiaOrganizacional ho) {
        return facade.getEntidadeFacade().entidadePossuiUnidadeParaDeclaracao(ho, selecionado.getEntidade(), CategoriaDeclaracaoPrestacaoContas.SEFIP, selecionado.getPrimeiroDiaDoMes(), selecionado.getUltimoDiaDoMes());
    }

    public List<SelectItem> recuperarAnos() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Exercicio e : facade.getExercicioFacade().getExerciciosAtualPassados()) {
            toReturn.add(new SelectItem(e.getAno(), "" + e.getAno()));
        }
        return toReturn;
    }

    public List<HierarquiaOrganizacional> orgaosVigentesNaDataDoSefip() {
        return facade.getHierarquiaOrganizacionalFacade().getHierarquiasVigentesNoPeriodoNoNivel(selecionado.getPrimeiroDiaDoMes(), selecionado.getUltimoDiaDoMes(), TipoHierarquiaOrganizacional.ADMINISTRATIVA, 2);
    }

    public DependenciasDirf getDependenciasDirf() {
        return dependenciasDirf;
    }

    public void setDependenciasDirf(DependenciasDirf dependenciasDirf) {
        this.dependenciasDirf = dependenciasDirf;
    }

    public StreamedContent recuperarArquivoParaDownload() {
        StreamedContent s = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (ArquivoParte a : selecionado.getArquivo().getPartes()) {
            try {
                buffer.write(a.getDados());
            } catch (IOException ex) {
                logger.error("Erro: ", ex);
            }
        }
        InputStream is = new ByteArrayInputStream(buffer.toByteArray());
        s = new DefaultStreamedContent(is, selecionado.getArquivo().getMimeType(), selecionado.getArquivo().getNome());
        return s;
    }

    public void geraTxt() throws FileNotFoundException, IOException {
        String conteudo = "<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>"
            + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
            + " <html>"
            + " <head>"
            + " <style type=\"text/css\">@page{size: A4 portrait;}</style>"
            + " <title>"
            + " < META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=iso-8859-1\">"
            + " </title>"
            + " </head>"
            + " <body>"
            + "<p style='font-size : 15px;'><b><u> Ref. " + selecionado + "<u></b></p>"
            + "<p style='font-size : 15px;'><b><u>Data Processamento: " + Util.formatterDataHora.format(selecionado.getProcessadoEm()) + "<u></b></p>"
            + "<p style='font-size : 15px;'><b><u>USUÁRIO RESPONSÁVEL: " + facade.getSistemaFacade().getUsuarioCorrente().getLogin() + "<u></b></p>"
            + "<p style='font-size : 10px;'>"
            + dependenciasDirf.getSomenteStringDoLog()
            + "</p>"
            + " </body>"
            + " </html>";
        String nome = "Log geração arquivo SEFIP.RE - " + selecionado;
        nome = nome.replace(" ", "_");
        Util.downloadPDF(nome, conteudo, FacesContext.getCurrentInstance());
    }

    private void getFolhasDePagamentoEfetivadasDoMesDoSefip() {
        folhasDePagamento = Lists.newLinkedList();
        if (selecionado.getMes() == 13) {
            folhasDePagamento = facade.getFolhaDePagamentoFacade().getFolhasDePagamentoEfetivadasDoMesDecimoTerceiro(Mes.getMesToInt(selecionado.getMes() - 1), selecionado.getAno());
        } else if (itemEntidadeDPContas != null && itemEntidadeDPContas.getGerarSefip13Dezembro()) {
            folhasDePagamento = facade.getFolhaDePagamentoFacade().getFolhasDePagamentoEfetivadasDoMes(Mes.getMesToInt(selecionado.getMes()), selecionado.getAno());
        } else {
            folhasDePagamento = facade.getFolhaDePagamentoFacade().getFolhasDePagamentoEfetivadasDoMesSemDecimoTerceiro(Mes.getMesToInt(selecionado.getMes()), selecionado.getAno());
        }
    }

    public boolean todasAsFolhasMarcadas() {
        try {
            for (FolhaDePagamento fp : folhasDePagamento) {
                if (fp.getSelecionado() == null || !fp.getSelecionado()) {
                    return false;
                }
            }
            return true;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public void marcarTodasAsFolhas() {
        for (FolhaDePagamento fp : folhasDePagamento) {
            marcarFolhaDePagamento(fp);
        }
    }

    public void desmarcarTodasAsFolhas() {
        for (FolhaDePagamento fp : folhasDePagamento) {
            desmarcarFolhaDePagamento(fp);
        }
    }

    public void marcarFolhaDePagamento(FolhaDePagamento fp) {
        fp.setSelecionado(Boolean.TRUE);
    }

    public void desmarcarFolhaDePagamento(FolhaDePagamento fp) {
        fp.setSelecionado(Boolean.FALSE);
    }

    public String mensagemArquivoExistente() {
        String retorno = "Já existe um arquivo gerado para a entidade: <b>" + selecionado.getEntidade() + "</b> em <b>" + selecionado.getMes() + "/" + selecionado.getAno() + "</b> e para as folhas de pagamento:";
        retorno += "<br/><br/>";
        for (SefipFolhaDePagamento sfp : selecionado.getSefipFolhasDePagamento()) {
            retorno += sfp.getFolhaDePagamento() + "<br/>";
        }
        return retorno;
    }

    public void atribuirItemEntidadeDPContas() {
        if (selecionado.getEntidade() != null) {
            itemEntidadeDPContas = facade.getItemEntidadeDPContas(selecionado.getEntidade(), facade.getSistemaFacade().getDataOperacao());
        }
    }
}
