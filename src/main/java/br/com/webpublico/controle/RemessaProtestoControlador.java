package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoMensagemValidacao;
import br.com.webpublico.enums.tributario.OrigemRemessaProtesto;
import br.com.webpublico.enums.tributario.SituacaoRemessaProtesto;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.CertidaoDividaAtivaFacade;
import br.com.webpublico.negocios.DividaFacade;
import br.com.webpublico.negocios.RemessaProtestoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.tributario.services.ServiceRemessaProtesto;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaRemessaProtesto;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.tributario.consultadebitos.enums.TipoCalculoDTO;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-remessa-protesto",
        pattern = "/tributario/divida-ativa/processo-protesto/remessa/novo/",
        viewId = "/faces/tributario/dividaativa/processodeprotesto/remessa/edita.xhtml"),
    @URLMapping(id = "ver-remessa-protesto",
        pattern = "/tributario/divida-ativa/processo-protesto/remessa/ver/#{remessaProtestoControlador.id}/",
        viewId = "/faces/tributario/dividaativa/processodeprotesto/remessa/visualizar.xhtml"),
    @URLMapping(id = "listar-remessa-protesto",
        pattern = "/tributario/divida-ativa/processo-protesto/remessa/listar/",
        viewId = "/faces/tributario/dividaativa/processodeprotesto/remessa/lista.xhtml"),

    @URLMapping(id = "listar-retorno-remessa-protesto",
        pattern = "/tributario/divida-ativa/processo-protesto/remessa/retorno/listar/",
        viewId = "/faces/tributario/dividaativa/processodeprotesto/remessa/retorno/lista.xhtml"),
    @URLMapping(id = "ver-retorno-remessa-protesto",
        pattern = "/tributario/divida-ativa/processo-protesto/remessa/retorno/ver/#{remessaProtestoControlador.id}/",
        viewId = "/faces/tributario/dividaativa/processodeprotesto/remessa/retorno/visualizar.xhtml"),
})
public class RemessaProtestoControlador extends PrettyControlador<RemessaProtesto> implements CRUD {

    @EJB
    private RemessaProtestoFacade remessaFacade;
    private RetornoRemessaProtesto retornoRemessa;
    private List<CdaRemessaProtesto> cdasEncontradas;
    private List<CdaRemessaProtesto> cdasSelecionadas;
    private List<CdaRemessaProtesto> cdasComDebitosEmAberto;
    private AssistenteBarraProgresso assistente;
    private CompletableFuture<AssistenteBarraProgresso> completableFutureRemessa;
    private List<Divida> dividasSelecionadas;
    private Divida divida;
    private List<Divida> dividasDeDividaAtiva;
    private ServiceRemessaProtesto serviceRemessaProtesto;
    private List<LogCdaRemessaProtesto> logsCDA;

    public RemessaProtestoControlador() {
        super(RemessaProtesto.class);
    }

    @PostConstruct
    public void init() {
        serviceRemessaProtesto = Util.recuperarSpringBean(ServiceRemessaProtesto.class);
    }

    public List<LogCdaRemessaProtesto> getLogsCDA() {
        return logsCDA;
    }

    public void setLogsCDA(List<LogCdaRemessaProtesto> logsCDA) {
        this.logsCDA = logsCDA;
    }

    @Override
    public RemessaProtestoFacade getFacede() {
        return remessaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/divida-ativa/processo-protesto/remessa/";
    }

    public String getCaminhoRetorno() {
        return "/tributario/divida-ativa/processo-protesto/remessa/retorno/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public RetornoRemessaProtesto getRetornoRemessa() {
        return retornoRemessa;
    }

    public void setRetornoRemessa(RetornoRemessaProtesto retornoRemessa) {
        this.retornoRemessa = retornoRemessa;
    }

    public List<CdaRemessaProtesto> getCdasEncontradas() {
        return cdasEncontradas;
    }

    public void setCdasEncontradas(List<CdaRemessaProtesto> cdasEncontradas) {
        this.cdasEncontradas = cdasEncontradas;
    }

    public List<CdaRemessaProtesto> getCdasComDebitosEmAberto() {
        if (cdasComDebitosEmAberto == null) {
            cdasComDebitosEmAberto = new ArrayList<>();
        }
        return cdasComDebitosEmAberto;
    }

    public void setCdasComDebitosEmAberto(List<CdaRemessaProtesto> cdasComDebitosEmAberto) {
        this.cdasComDebitosEmAberto = cdasComDebitosEmAberto;
    }

    public List<CdaRemessaProtesto> getCdasSelecionadas() {
        return cdasSelecionadas;
    }

    public void setCdasSelecionadas(List<CdaRemessaProtesto> cdasSelecionadas) {
        this.cdasSelecionadas = cdasSelecionadas;
    }

    public AssistenteBarraProgresso getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteBarraProgresso assistente) {
        this.assistente = assistente;
    }

    public List<Divida> getDividasSelecionadas() {
        return dividasSelecionadas;
    }

    public void setDividasSelecionadas(List<Divida> dividasSelecionadas) {
        this.dividasSelecionadas = dividasSelecionadas;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public void removerDivida(Divida divida) {
        if (dividasSelecionadas.contains(divida)) {
            dividasSelecionadas.remove(divida);
        }
    }

    public void adicionarDivida() {
        try {
            validarDivida();
            dividasSelecionadas.add(divida);
            divida = new Divida();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarDivida() {
        ValidacaoException ve = new ValidacaoException();
        if (divida == null || divida.getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione uma dívida para adicionar.");
        } else if (dividasSelecionadas.contains(divida)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Essa Dívida já foi selecionada!");
        }
        ve.lancarException();
    }

    public List<SelectItem> getDividas() {
        List<SelectItem> toReturn = new ArrayList<>();
        List<Divida> dividas = getDividasDeDividaAtiva();
        for (Divida divida : dividas) {
            toReturn.add(new SelectItem(divida, divida.getDescricao()));
        }
        return toReturn;
    }

    private List<Divida> getDividasDeDividaAtiva() {
        if (dividasDeDividaAtiva == null) {
            dividasDeDividaAtiva = remessaFacade.getDividaFacade().buscarDividasDeDividaAtiva("");
        }
        return dividasDeDividaAtiva;
    }

    @URLAction(mappingId = "nova-remessa-protesto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        this.cdasEncontradas = Lists.newArrayList();
        this.cdasSelecionadas = Lists.newArrayList();
        selecionado.setOrigemRemessa(OrigemRemessaProtesto.MANUAL);
        dividasSelecionadas = Lists.newArrayList();
    }

    @Override
    @URLAction(mappingId = "ver-remessa-protesto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        Collections.sort(selecionado.getCdas());
    }

    @URLAction(mappingId = "ver-retorno-remessa-protesto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verRetorno() {
        retornoRemessa = remessaFacade.recuperarRetornoRemessa(getId());
    }

    private Boolean cdaComDebitoEmAberto(CdaRemessaProtesto cdaRemessaProtesto) {
        for (ItemCertidaoDividaAtiva itemInscricaoDA : cdaRemessaProtesto.getCda().getItensCertidaoDividaAtiva()) {
            List<ResultadoParcela> resultadoParcelas = remessaFacade.getCertidaoDividaAtivaFacade().buscarParcelasDoCalculo(itemInscricaoDA.getItemInscricaoDividaAtiva().getId());
            if (resultadoParcelas != null && !resultadoParcelas.isEmpty()) {
                for (ResultadoParcela parcela : resultadoParcelas) {
                    if (!TipoCalculoDTO.PARCELAMENTO.equals(parcela.getTipoCalculoEnumValue())) {
                        if (SituacaoParcela.EM_ABERTO.equals(SituacaoParcela.valueOf(parcela.getSituacao()))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public Boolean cdaSoComDebitoAberto(CdaRemessaProtesto cdaRemessa) {
        return cdasComDebitosEmAberto.contains(cdaRemessa);
    }

    public void buscarCdasPorProcessoDeProtesto() {
        try {
            validarSingletonEnvioRemessa();
            validarEnvioRemessa();
            if (divida != null && divida.getId() != null) {
                adicionarDivida();
            }
            cdasEncontradas = remessaFacade.buscarCdasParaEnvioDeRemessa(selecionado.getDataInicial(), selecionado.getDataFinal(), dividasSelecionadas);
            validarParcelasRemessa(cdasEncontradas);
            Collections.sort(cdasEncontradas);

            cdasSelecionadas = Lists.newArrayList();
            cdasComDebitosEmAberto = Lists.newArrayList();
            for (CdaRemessaProtesto cda : cdasEncontradas) {
                if (cdaComDebitoEmAberto(cda)) {
                    cdasComDebitosEmAberto.add(cda);
                }
            }
            cdasSelecionadas.addAll(cdasComDebitosEmAberto);
            if (cdasEncontradas.size() != cdasComDebitosEmAberto.size()) {
                FacesUtil.addAtencao("As CDAs que estão em destaque possuem débitos que não estão com a situação \"Em Aberto\"!");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao enviar remessa de protesto. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao enviar remessa de protesto. Detalhes: " + e.getMessage());
        }
    }

    public void adicionarCda(CdaRemessaProtesto cda) {
        Util.adicionarObjetoEmLista(cdasSelecionadas, cda);
    }

    public void removerCda(CdaRemessaProtesto cda) {
        cdasSelecionadas.remove(cda);
    }

    public void adicionarTodasCdas() {
        cdasSelecionadas.clear();
        cdasSelecionadas.addAll(cdasComDebitosEmAberto);
    }

    public void removerTodasCdas() {
        cdasSelecionadas.clear();
    }

    public boolean hasCdaAdiciona(CdaRemessaProtesto cda) {
        return cdasSelecionadas.contains(cda);
    }

    public boolean hasTodasCdasAdicionadas() {
        return cdasSelecionadas.containsAll(getCdasComDebitosEmAberto());
    }

    public void enviarRemessaDeProtesto() {
        try {
            validarSingletonEnvioRemessa();
            validarEnvioRemessa();
            validarParcelasRemessa(cdasSelecionadas);
            adicionarParcelasProtestoARemessa();
            adicionarInformacoesLog();
            remessaFacade.getSingletonConcorrenciaRemessaProtesto().lock(selecionado.getResponsavelRemessa());
            assistente = criarAssistenteRemessa();
            completableFutureRemessa = AsyncExecutor.getInstance()
                .execute(assistente,
                    () -> serviceRemessaProtesto.enviarRemessa(assistente));
            FacesUtil.executaJavaScript("iniciarEnvio()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao eviar remessa de protesto. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao enviar remessa de protesto. Detalhes: " + e.getMessage());
        }
    }

    public void acompanharEnvio() {
        if (completableFutureRemessa != null && completableFutureRemessa.isDone()) {
            FacesUtil.executaJavaScript("finalizarEnvio()");
        }
    }

    public void finalizarEnvio() {
        try {
            assistente = completableFutureRemessa.get();
            if (assistente != null) {
                remessaFacade.getSingletonConcorrenciaRemessaProtesto().unLock();
                assistente.setExecutando(false);
                selecionado = (RemessaProtesto) assistente.getSelecionado();
                redirecionar();
            }
        } catch (Exception e) {
            logger.error("Erro ao eviar remessa de protesto. ", e);
            remessaFacade.getSingletonConcorrenciaRemessaProtesto().unLock();
            FacesUtil.addOperacaoNaoRealizada("Erro ao enviar remessa de protesto. Detalhes: " + e.getMessage());
        } finally {
            FacesUtil.executaJavaScript("closeDialog(dlgEnviar)");
        }
    }

    private AssistenteBarraProgresso criarAssistenteRemessa() {
        AssistenteBarraProgresso assistente = new AssistenteBarraProgresso();
        assistente.setSelecionado(selecionado);
        assistente.setExercicio(remessaFacade.recuperarExercicioCorrente());
        assistente.setUsuarioSistema(remessaFacade.recuperarUsuarioCorrente());
        assistente.setIp(Util.obterIpUsuario());
        assistente.setTotal(cdasSelecionadas.size() * 3);
        assistente.setExecutando(true);
        return assistente;
    }

    private void adicionarParcelasProtestoARemessa() {
        if (cdasSelecionadas != null) {
            selecionado.getCdas().clear();
            for (CdaRemessaProtesto cdaRemessa : cdasSelecionadas) {
                cdaRemessa.setRemessaProtesto(selecionado);
                selecionado.getCdas().add(cdaRemessa);
            }
        }
    }

    private void redirecionar() {
        if (selecionado.getId() != null) {
            if (SituacaoRemessaProtesto.ENVIADO.equals(selecionado.getSituacaoRemessa())) {
                FacesUtil.addOperacaoRealizada("Remessa enviada com sucesso.");
            } else {
                FacesUtil.addOperacaoNaoRealizada("Remessa não enviada. Consulte o Log de mensagens.");
            }
            redireciona(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } else {
            if (!assistente.getMensagens().isEmpty()) {
                List<MensagemValidacao> mensagensValidacao = Lists.newArrayList();
                for (String mensagem : assistente.getMensagens()) {
                    mensagensValidacao.add(new MensagemValidacao(TipoMensagemValidacao.ERRO, "", "", mensagem));
                }
                FacesUtil.printAllMessages(mensagensValidacao);
            }
        }
    }

    private void adicionarInformacoesLog() {
        selecionado.setEnvioRemessa(new Date());
        selecionado.setResponsavelRemessa(remessaFacade.recuperarUsuarioCorrente());
        selecionado.setSequencia(remessaFacade.gerarSequenciaRemessa());
    }

    private void validarEnvioRemessa() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (selecionado.getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        if (selecionado.getDataInicial() != null && selecionado.getDataFinal() != null &&
            DataUtil.dataSemHorario(selecionado.getDataInicial()).compareTo(DataUtil.dataSemHorario(selecionado.getDataFinal())) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Inicial deve ser menor ou igual a Data Final.");
        }
        ve.lancarException();
    }

    private void validarSingletonEnvioRemessa() {
        ValidacaoException ve = new ValidacaoException();
        if (remessaFacade.getSingletonConcorrenciaRemessaProtesto().isLocked()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma remessa sendo enviado pelo usuário " + remessaFacade.getSingletonConcorrenciaRemessaProtesto().getUsuarioSistema().getNome() + ". Aguarde!");
        }
        ve.lancarException();
    }

    private void validarParcelasRemessa(List<CdaRemessaProtesto> cdas) {
        ValidacaoException ve = new ValidacaoException();
        if (cdas == null || cdas.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A consulta para os filtros informados não encontrou nenhuma parcela.");
        }
        ve.lancarException();
    }

    public boolean canReenviarRemessa() {
        return !SituacaoRemessaProtesto.ENVIADO.equals(selecionado.getSituacaoRemessa());
    }

    public String formatarRespostaValidacao(String resposta) {
        if (!StringUtils.isBlank(resposta)) {
            return StringUtils.capitalize(resposta.toLowerCase());
        }
        return "";
    }

    public StreamedContent fazerDownloadArquivo(String xml) {
        InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.ISO_8859_1));
        return new DefaultStreamedContent(stream, "application/xml", selecionado.getNomeArquivo() + ".xml");
    }

    public StreamedContent fazerDownloadRetorno(String xml) {
        if (retornoRemessa != null && !StringUtils.isBlank(retornoRemessa.getNomeArquivo())) {
            InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.ISO_8859_1));
            return new DefaultStreamedContent(stream, "application/xml", retornoRemessa.getNomeArquivo() + ".xml");
        }
        FacesUtil.addOperacaoNaoRealizada("Erro ao fazer download do arquivo de retorno.");
        return null;
    }

    public void atualizarSituacoesParcela() {
        try {
            remessaFacade.atualizarSituacoes(selecionado.getId(), remessaFacade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            redireciona(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada("Situações atualizadas com sucesso.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void corrigirSituacaoParcelas() {
        try {
            if(!usuarioCorrenteIsAdmin()) return;
            remessaFacade.atualizarSituacoes(selecionado.getId(), remessaFacade.getSistemaFacade().getUsuarioCorrente().getNome(), true);
            redireciona(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada("Situações atualizadas com sucesso.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public boolean usuarioCorrenteIsAdmin() {
        return remessaFacade.getSistemaFacade().getUsuarioCorrente().hasRole(PerfilUsuario.Perfil.ROLE_ADMIN.name());
    }

    public void retornarParaListaRetornos() {
        FacesUtil.redirecionamentoInterno(getCaminhoRetorno() + "listar");
    }

    public void liberarSingleton() {
        remessaFacade.getSingletonConcorrenciaRemessaProtesto().unLock();
    }

    public void buscarLogsCDA(CdaRemessaProtesto cda) {
        logsCDA = remessaFacade.buscarLogsCDA(cda);
    }

}
