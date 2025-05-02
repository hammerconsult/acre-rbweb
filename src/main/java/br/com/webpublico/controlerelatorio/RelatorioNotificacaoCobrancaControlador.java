package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteRelatorioNotificacaoCobranca;
import br.com.webpublico.entidadesauxiliares.FiltroMalaDiretaGeral;
import br.com.webpublico.entidadesauxiliares.NotificacaoCobrancaMalaDireta;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.relatoriofacade.RelatorioNotificacaoCobrancaFacade;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by mga on 30/06/2017.
 */
@ViewScoped
@ManagedBean(name = "relatorioNotificacaoCobrancaControlador")
@URLMappings(mappings = {
    @URLMapping(id = "nova-notificao-cobranca",
        pattern = "/tributario/mala-direta/notificacao-e-cobranca/",
        viewId = "/faces/tributario/maladiretageral/relatorio/notificacao-cobranca.xhtml")
})
public class RelatorioNotificacaoCobrancaControlador {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(RelatorioNotificacaoCobrancaControlador.class);
    @EJB
    private RelatorioNotificacaoCobrancaFacade facade;
    private FiltroMalaDiretaGeral filtro;
    private List<Future<AssistenteRelatorioNotificacaoCobranca>> futures;
    private Future<ByteArrayOutputStream> futureBytesRelatorio;
    private List<NotificacaoCobrancaMalaDireta> notificacoesCobrancasMalaDireta;
    private AssistenteBarraProgresso assistenteBarraProgresso;

    @URLAction(mappingId = "nova-notificao-cobranca", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroMalaDiretaGeral();
        notificacoesCobrancasMalaDireta = Lists.newArrayList();
        assistenteBarraProgresso = new AssistenteBarraProgresso();
        iniciarAssistente();
        filtro.setVencimentoInicial(new DataUtil().montaData(01, 00, facade.getSistemaFacade().getExercicioCorrente().getAno()).getTime());
        filtro.setVencimentoFinal(new DataUtil().montaData(31, 11, facade.getSistemaFacade().getExercicioCorrente().getAno()).getTime());
        filtro.setTipoRelatorio(DetalhadoResumido.DETALHADO);
    }

    public void limparTexto() {
        filtro.setTexto("");
        filtro.setFiltro("");
    }

    private void iniciarFuture() {
        futures = Lists.newArrayList();
        futureBytesRelatorio = null;
    }

    private void iniciarAssistente() {
        assistenteBarraProgresso = new AssistenteBarraProgresso("Notificação e Cobrança", 0);
    }

    public String getCaminhoRelatorio() {
        return "/tributario/mala-direta/notificacao-e-cobranca/";
    }

    public List<SelectItem> getDividas() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        List<Divida> dividas = Lists.newArrayList();
        if (isPessoa()) {
            dividas = facade.getDividaFacade().listaDividasOrdenadoPorDescricao();

        } else if (isImobiliario()) {
            dividas = facade.getDividaFacade().listaDividasPorTipoCadastro(TipoCadastroTributario.IMOBILIARIO);

        } else if (isEconomico()) {
            dividas = facade.getDividaFacade().listaDividasPorTipoCadastro(TipoCadastroTributario.ECONOMICO);

        } else if (isRural()) {
            dividas = facade.getDividaFacade().listaDividasPorTipoCadastro(TipoCadastroTributario.RURAL);
        } else {
            toReturn.add(new SelectItem(null, "Selecione um tipo de cadastro para filtrar suas dívidas"));
        }
        for (Divida t : dividas) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    private void validarCampo() {
        ValidacaoException ve = new ValidacaoException();
        if (filtro.getVencimentoInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de vencimento inicial deve ser informado.");
        } else {
            filtro.setVencimentoInicial(Util.getDataHoraMinutoSegundoZerado(filtro.getVencimentoInicial()));
        }
        if (filtro.getVencimentoFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de vencimento deve ser informado.");
        } else {
            filtro.setVencimentoFinal(Util.getDataHoraMinutoSegundoZerado(filtro.getVencimentoFinal()));
        }
        if (filtro.getTipoCadastroTributario() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de cadastro deve ser informado.");
        }
        ve.lancarException();
        if (filtro.getVencimentoInicial().after(filtro.getVencimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de vencimento inicial deve ser inferior ou igual a data de vencimento final.");
        }
        if (filtro.getLancamentoInicial() != null && filtro.getLancamentoFinal() != null) {
            if (filtro.getLancamentoInicial().after(filtro.getLancamentoFinal())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de lançamento inicial deve ser inferior ou igual a data de lançamento final.");
            }
        }
        if (filtro.getMovimentoInicial() != null && filtro.getMovimentoFinal() != null) {
            if (filtro.getMovimentoInicial().after(filtro.getMovimentoFinal())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de movimento inicial deve ser inferior ou igual a data de movimento final.");
            }
        }
        if (filtro.getNotificacaoInicial() != null && filtro.getNotificacaoFinal() != null) {
            if (filtro.getNotificacaoInicial().after(filtro.getNotificacaoFinal())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de notificação inicial deve ser inferior ou igual a data de notificação final.");
            }
        }
        if (filtro.getPagamentoInicial() != null && filtro.getPagamentoFinal() != null) {
            if (filtro.getPagamentoInicial().after(filtro.getPagamentoFinal())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de pagamento inicial deve ser inferior ou igual a data de pagamento final.");
            }
        }
        ve.lancarException();
    }

    public void executarConsulta() {
        if (isPessoa()) {
            futures.add(facade.buscarNotificacaoCobrancaPorContribuinte(filtro, montarCondicaoCadastroContribuinte().toString()));
        }
        if (isImobiliario()) {
            futures.add(facade.buscarNotificacaoCobrancaIPorCadastroImobiliario(filtro, montarCondicaoCadastroImobiliario().toString()));
        }
        if (isEconomico()) {
            futures.add(facade.buscarNotificacaoCobrancaPorCadastroEconomico(filtro, montarCondicaoCadastroEconomico().toString()));
        }
        if (isRural()) {
            futures.add(facade.buscarNotificacaoCobrancaPorCadastroRural(filtro, montarCondicaoCadastroRural().toString()));
        }
    }


    public boolean isFutureAtualizacaoParcelaConcluida() {
        if (futures == null) {
            return false;
        }
        for (Future<AssistenteRelatorioNotificacaoCobranca> future : futures) {
            if (!future.isDone()) {
                return false;
            }
        }
        return true;
    }

    private boolean isFutureRelatorioConcluida() {
        return futureBytesRelatorio != null && futureBytesRelatorio.isDone();
    }

    public void consultarAndamentoQuery() {
        if (futures != null) {
            for (Future<AssistenteRelatorioNotificacaoCobranca> future : futures) {
                if (future == null || !future.isDone()) {
                    return;
                }
            }
            buscarValorParecela();
            filtro.setTexto("Atualizando Valores");
            FacesUtil.atualizarComponente("myAguardeForm");
            FacesUtil.executaJavaScript("acompanhaGeracaoParcela()");
        }
    }

    public void consultarAndamentoGeracaoParcela() throws ExecutionException, InterruptedException {
        if (isFutureAtualizacaoParcelaConcluida()) {
            iniciarGeracaoRelatorio();
            FacesUtil.atualizarComponente("myAguardeForm");
            filtro.setTexto("Gerando Relatório");
            FacesUtil.executaJavaScript("acompanhaGeracaoRelatorio()");
        }
    }

    public void consultarAndamentoGeracaoRelatorio() throws ExecutionException, InterruptedException {
        if (isFutureRelatorioConcluida()) {
            FacesUtil.executaJavaScript("terminaRelatorio()");
            try {
                if (futureBytesRelatorio != null && futureBytesRelatorio.get() != null) {
                    futureBytesRelatorio.get().close();
                }
            } catch (IOException e) {
                log.debug("Future bytes " + e.getMessage());
            }
        }
    }

    public void iniciarConsulta() {
        try {
            validarCampo();
            limparTexto();
            iniciarAssistente();
            iniciarFuture();
            filtro.setTexto("Executando Consulta");
            FacesUtil.atualizarComponente("myAguardeForm");
            FacesUtil.executaJavaScript("myArguarde.show()");
            executarConsulta();
            FacesUtil.executaJavaScript("acompanhaConsulta()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            FacesUtil.executaJavaScript("myArguarde.hide()");
        }
    }

    private void buscarValorParecela() {
        try {
            notificacoesCobrancasMalaDireta = getNotificacoes();
            iniciarFuture();
            FacesUtil.atualizarComponente("myAguardeForm");
            assistenteBarraProgresso = new AssistenteBarraProgresso("Notificação e Cobrança", notificacoesCobrancasMalaDireta.size());
            futures.add(facade.atualizarValores(filtro, assistenteBarraProgresso, notificacoesCobrancasMalaDireta));
        } catch (Exception e) {
            log.error("Não foi possível atualizar os valores para o relatório {} ", e);
            FacesUtil.addError("Atenção!", "Não foi possível atualizar os valores para o relatório! (ERROR:" + e.getMessage() + ")");
        }
        if (futures.isEmpty()) {
            FacesUtil.executaJavaScript("myArguarde.hide()");
        }
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    private void iniciarGeracaoRelatorio() {
        try {
            notificacoesCobrancasMalaDireta = getNotificacoes();
            iniciarFuture();
            FacesUtil.atualizarComponente("myAguardeForm");
            criarParametroRelatorio();
            futureBytesRelatorio = facade.gerarRelatorio(filtro.getAssistenteGeracaoRelatorio(), notificacoesCobrancasMalaDireta, getSistemaControlador().getUnidadeOrganizacionalAdministrativaCorrente());
        } catch (Exception e) {
            FacesUtil.executaJavaScript("myAguarde.hide()");
        }
    }

    private void criarParametroRelatorio() {
        filtro.getAssistenteGeracaoRelatorio().setCaminhoReport(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/WEB-INF/report/") + "/");
        filtro.getAssistenteGeracaoRelatorio().setCaminhoImagem(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/img/") + "/");
        filtro.getAssistenteGeracaoRelatorio().setUsuario(getNomeUsuarioLogado());
        filtro.getAssistenteGeracaoRelatorio().setArquivoJasper(getArquivoJasper());
        filtro.getAssistenteGeracaoRelatorio().setNomeRelatorio(getNomeRelatorio());
        filtro.getAssistenteGeracaoRelatorio().setTipoCadastroTributario(filtro.getTipoCadastroTributario());
        filtro.getAssistenteGeracaoRelatorio().setLabeInscricao(getLabelInscricao());
        filtro.getAssistenteGeracaoRelatorio().setFiltros(filtro.getFiltro().substring(0, filtro.getFiltro().length() - 2));
    }

    public String getNomeUsuarioLogado() {
        if (facade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica() != null) {
            return facade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica().getNome();
        } else {
            return facade.getSistemaFacade().getUsuarioCorrente().getUsername();
        }
    }

    private String getArquivoJasper() {
        if (DetalhadoResumido.DETALHADO.equals(filtro.getTipoRelatorio())) {
            return "NotificacaoCobrancaMalaDiretaGeralDetalhado.jasper";
        }
        return "NotificacaoCobrancaMalaDiretaGeralResumido.jasper";
    }

    private String getNomeRelatorio() {
        if (DetalhadoResumido.DETALHADO.equals(filtro.getTipoRelatorio())) {
            return "NOTIFICAÇÃO E COBRANÇA";
        }
        return "NOTIFICAÇÃO E COBRANÇA - RESUMIDO";
    }

    private String getLabelInscricao() {
        if (isPessoa()) {
            return "Cpf/Cnpj: ";
        }
        if (isImobiliario()) {
            return "Inscrição Cadastral: ";
        }
        if (isEconomico()) {
            return "C.M.C.: ";
        }
        if (isRural()) {
            return "Código Rural: ";
        }
        return "";
    }

    public void baixarPDF() {
        try {
            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            response.addHeader("Content-Disposition:", "attachment; filename=AuditoriaCadastro.pdf");
            response.setContentType("application/pdf");
            byte[] bytes = futureBytesRelatorio.get().toByteArray();
            response.setContentLength(bytes.length);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            outputStream.close();
            faces.responseComplete();
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private List<NotificacaoCobrancaMalaDireta> getNotificacoes() throws InterruptedException, ExecutionException {
        HashSet<NotificacaoCobrancaMalaDireta> setNotificacoes = Sets.newHashSet();
        for (Future<AssistenteRelatorioNotificacaoCobranca> future : futures) {
            setNotificacoes.addAll(future.get().getResultado());
        }
        return Lists.newLinkedList(setNotificacoes);
    }

    public List<Logradouro> completarLogradouro(String parte) {
        Bairro bairro = null;
        if (filtro.getTipoCadastroTributario().isEconomico() && filtro.getFiltroEconomico().getBairro() != null) {
            bairro = filtro.getFiltroEconomico().getBairro();
        }
        if (filtro.getTipoCadastroTributario().isImobiliario() && filtro.getFiltroImovel().getBairro() != null) {
            bairro = filtro.getFiltroImovel().getBairro();
        }
        if (bairro != null) {
            return facade.getLogradouroFacade().completaLogradourosPorBairro(parte.trim(), bairro);
        }
        return facade.getLogradouroFacade().listaFiltrando(parte.trim(), "nome", "nomeUsual", "nomeAntigo");
    }

    public List<Bairro> completarBairro(String parte) {
        return facade.getBairroFacade().completaBairro(parte.trim());
    }

    public List<TipoAutonomo> completarTipoAutonomo(String parte) {
        return facade.getTipoAutonomoFacade().completaTipoAutonomo(parte.trim());
    }

    public List<SelectItem> getClassificacoesAtividades() {
        return Util.getListSelectItem(ClassificacaoAtividade.values());
    }

    public List<SelectItem> getGrausDeRisco() {
        return Util.getListSelectItem(GrauDeRiscoDTO.values());
    }

    public List<SelectItem> getNaturezasJuridica() {
        List<SelectItem> toReturn = new ArrayList();
        toReturn.add(new SelectItem(null, " "));
        for (NaturezaJuridica natureza : facade.getNaturezaJuridicaFacade().buscarNaturezasJuridicasAtivas()) {
            toReturn.add(new SelectItem(natureza, natureza.getCodigo() + " - " + natureza.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricaoLonga()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposRelatorio() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (DetalhadoResumido tipo : DetalhadoResumido.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposImoveis() {
        return Util.getListSelectItem(TipoImovel.values());
    }

    private StringBuilder montarCondicaoCadastroContribuinte() {
        StringBuilder condicao = new StringBuilder();
        StringBuilder filtros = new StringBuilder();

        condicao.append(montarCondicaoGeral());

        if (!filtro.getFiltroPessoa().getPessoas().isEmpty()) {
            condicao.append(" and p.id in (").append(Util.getIdsDaListaAsString(filtro.getFiltroPessoa().getPessoas())).append(")");
            String nome = "Contribuinte: ";
            for (Pessoa pessoa : filtro.getFiltroPessoa().getPessoas()) {
                nome += pessoa.getNome() + ", ";
            }
            filtros.append(nome);
        }
        filtro.setFiltro(filtro.getFiltro() + filtros);
        return condicao;
    }

    private StringBuilder montarCondicaoCadastroImobiliario() {
        StringBuilder condicao = new StringBuilder();
        StringBuilder filtros = new StringBuilder();
        condicao.append(montarCondicaoGeral());

        if (filtro.getFiltroImovel().getInscricaoIncial() != null && !filtro.getFiltroImovel().getInscricaoIncial().isEmpty()) {
            condicao.append(" and imo.inscricaocadastral >= ").append("'" + filtro.getFiltroImovel().getInscricaoIncial().trim() + "'");
            filtros.append("Inscrição inicial: ").append(filtro.getFiltroImovel().getInscricaoIncial()).append(", ");
        }

        if (filtro.getFiltroImovel().getInscricaoFinal() != null && !filtro.getFiltroImovel().getInscricaoFinal().isEmpty()) {
            condicao.append(" and imo.inscricaocadastral <= ").append("'" + filtro.getFiltroImovel().getInscricaoFinal().trim() + "'");
            filtros.append("Inscrição final: ").append(filtro.getFiltroImovel().getInscricaoFinal()).append(", ");
        }

        if (filtro.getFiltroImovel().getTipoImovel() != null && TipoImovel.PREDIAL.equals(filtro.getFiltroImovel().getTipoImovel())) {
            condicao.append(" and exists (select 1 from construcao c where imovel_id = imo.id and coalesce(c.cancelada, 0) = 0 and rownum = 1) ");
            filtros.append("Tipo de imóvel: ").append(filtro.getFiltroImovel().getTipoImovel().getDescricao()).append(", ");
        }

        if (filtro.getFiltroImovel().getTipoImovel() != null && TipoImovel.TERRITORIAL.equals(filtro.getFiltroImovel().getTipoImovel())) {
            condicao.append(" and not exists (select 1 from construcao c where imovel_id = imo.id and coalesce(c.cancelada, 0) = 0 and rownum = 1) ");
            filtros.append("Tipo de imóvel: ").append(filtro.getFiltroImovel().getTipoImovel().getDescricao()).append(", ");
        }

        if (filtro.getFiltroImovel().getBairro() != null) {
            condicao.append(" and vwend.id_bairro = ").append(filtro.getFiltroImovel().getBairro().getId());
            filtros.append("Bairro: ").append(filtro.getFiltroImovel().getBairro()).append(", ");
        }

        if (filtro.getFiltroImovel().getLogradouro() != null) {
            condicao.append(" and vwend.id_logradouro = ").append(filtro.getFiltroImovel().getLogradouro().getId());
            filtros.append("Logradouro: ").append(filtro.getFiltroImovel().getLogradouro()).append(", ");
        }

        if (filtro.getFiltroImovel().getSetorInicial() != null && !filtro.getFiltroImovel().getSetorInicial().isEmpty()) {
            condicao.append(" and vwend.setor >= '" + filtro.getFiltroImovel().getSetorInicial().trim() + "'");
            filtros.append("Setor inicial: ").append(filtro.getFiltroImovel().getSetorInicial()).append(", ");
        }

        if (filtro.getFiltroImovel().getSetorFinal() != null && !filtro.getFiltroImovel().getSetorFinal().isEmpty()) {
            condicao.append(" and vwend.setor <= '" + filtro.getFiltroImovel().getSetorFinal().trim() + "'");
            filtros.append("Setor inicial: ").append(filtro.getFiltroImovel().getSetorFinal()).append(", ");
        }

        if (filtro.getFiltroImovel().getQuadraInicial() != null && !filtro.getFiltroImovel().getQuadraInicial().isEmpty()) {
            condicao.append(" and vwend.quadra >= ").append("'" + filtro.getFiltroImovel().getQuadraInicial().trim() + "'");
            filtros.append("Quadra inicial: ").append(filtro.getFiltroImovel().getQuadraInicial()).append(", ");
        }

        if (filtro.getFiltroImovel().getQuadraFinal() != null && !filtro.getFiltroImovel().getQuadraFinal().isEmpty()) {
            condicao.append(" and vwend.quadra <= ").append("'" + filtro.getFiltroImovel().getQuadraFinal().trim() + "'");
            filtros.append("Quadra inicial: ").append(filtro.getFiltroImovel().getQuadraFinal()).append(", ");
        }

        if (filtro.getFiltroImovel().getLoteInicial() != null && !filtro.getFiltroImovel().getLoteInicial().isEmpty()) {
            condicao.append(" and vwend.lote >= ").append("'" + filtro.getFiltroImovel().getLoteInicial().trim() + "'");
            filtros.append("Lote inicial: ").append(filtro.getFiltroImovel().getLoteInicial()).append(", ");
        }

        if (filtro.getFiltroImovel().getLoteFinal() != null && !filtro.getFiltroImovel().getLoteFinal().isEmpty()) {
            condicao.append(" and vwend.lote <= ").append("'" + filtro.getFiltroImovel().getLoteFinal().trim() + "'");
            filtros.append("Lote inicial: ").append(filtro.getFiltroImovel().getLoteFinal()).append(", ");
        }
        filtro.setFiltro(filtro.getFiltro() + filtros);
        return condicao;
    }

    private StringBuilder montarCondicaoCadastroEconomico() {
        StringBuilder condicao = new StringBuilder();
        StringBuilder filtros = new StringBuilder();
        condicao.append(montarCondicaoGeral());

        if (filtro.getFiltroEconomico().getInscricaoIncial() != null && !filtro.getFiltroEconomico().getInscricaoIncial().isEmpty()) {
            condicao.append(" and cmc.inscricaocadastral >= ").append("'" + filtro.getFiltroEconomico().getInscricaoIncial().trim() + "'");
            filtros.append("C.M.C. inicial: ").append(filtro.getFiltroEconomico().getInscricaoIncial()).append(", ");
        }

        if (filtro.getFiltroEconomico().getInscricaoFinal() != null && !filtro.getFiltroEconomico().getInscricaoFinal().isEmpty()) {
            condicao.append(" and cmc.inscricaocadastral <= ").append("'" + filtro.getFiltroEconomico().getInscricaoFinal().trim() + "'");
            filtros.append("C.M.C. final: ").append(filtro.getFiltroEconomico().getInscricaoFinal()).append(", ");
        }

        if (filtro.getFiltroEconomico().getClassificacao() != null) {
            condicao.append(" and cmc.classificacaoAtividade = ").append("'" + filtro.getFiltroEconomico().getClassificacao().name() + "'");
            filtros.append("Classificação atividade: ").append(filtro.getFiltroEconomico().getClassificacao().getDescricao()).append(", ");
        }

        if (filtro.getFiltroEconomico().getNaturezaJuridica() != null) {
            condicao.append(" and cmc.naturezajuridica_id = ").append(filtro.getFiltroEconomico().getNaturezaJuridica().getId());
            filtros.append("Natureza jurídica: ").append(filtro.getFiltroEconomico().getNaturezaJuridica().getDescricao()).append(", ");
        }

        if (filtro.getFiltroEconomico().getTipoAutonomo() != null) {
            condicao.append(" and cmc.tipoAutonomo_id = ").append(filtro.getFiltroEconomico().getTipoAutonomo().getId());
            filtros.append("Tipo autonomo: ").append(filtro.getFiltroEconomico().getTipoAutonomo()).append(", ");
        }

        if (filtro.getFiltroEconomico().getCnae() != null) {
            condicao.append("  and  exists (select 1 from economicocnae eco where eco.cadastroeconomico_id = cmc.id ")
                .append(" inner join cnae cnae on cnae.id = eco.cnae_id ")
                .append(" where cnae.grauDeRisco = '").append(filtro.getFiltroEconomico().getGrauDeRisco().name()).append("') ");
            filtros.append("Grau de Risco: ").append(filtro.getFiltroEconomico().getGrauDeRisco()).append(", ");
        }

        if (filtro.getFiltroEconomico().getLogradouro() != null) {
            condicao.append(" and exists(select 1 from vwenderecopessoa vw where lower(trim(coalesce(vw.logradouro, ''))) = ")
                .append(filtro.getFiltroEconomico().getLogradouro().getNome().trim().toLowerCase()).append(")");
            filtros.append("Logradouro: ").append(filtro.getFiltroEconomico().getLogradouro()).append(", ");
        }

        if (filtro.getFiltroEconomico().getBairro() != null) {
            condicao.append(" and exists(select 1 from vwenderecopessoa vw where lower(trim(coalesce(vw.bairro, ''))) = ")
                .append(filtro.getFiltroEconomico().getBairro().getDescricao().trim().toLowerCase()).append(")");
            filtros.append("Bairro: ").append(filtro.getFiltroEconomico().getBairro()).append(", ");
        }

        filtro.setFiltro(filtro.getFiltro() + filtros);
        return condicao;
    }

    private StringBuilder montarCondicaoCadastroRural() {
        StringBuilder condicao = new StringBuilder();
        StringBuilder filtros = new StringBuilder();
        condicao.append(montarCondicaoGeral());

        if (Strings.isNullOrEmpty(filtro.getFiltroRural().getInscricaoIncial())) {
            condicao.append(" and rural.codigo >= ").append("'" + filtro.getFiltroRural().getInscricaoIncial().trim() + "'");
            filtros.append("Inscrição inicial: ").append(filtro.getFiltroRural().getInscricaoIncial() + ", ");
        }
        if (Strings.isNullOrEmpty(filtro.getFiltroRural().getInscricaoFinal())) {
            condicao.append(" and rural.codigo <= ").append("'" + filtro.getFiltroRural().getInscricaoFinal().trim() + "'");
            filtros.append("Inscrição final: ").append(filtro.getFiltroRural().getInscricaoFinal() + ", ");
        }
        filtro.setFiltro(filtro.getFiltro() + filtros);
        return condicao;
    }

    private StringBuilder montarCondicaoGeral() {
        StringBuilder condicao = new StringBuilder();
        StringBuilder filtros = new StringBuilder();

        filtros.append("Tipo de Cadastro: ").append(filtro.getTipoCadastroTributario().getDescricao()).append(", ");

        if (filtro.getVencimentoInicial() != null && filtro.getVencimentoFinal() != null) {
            condicao.append(" where pvd.vencimento between ")
                .append(" to_date('" + DataUtil.getDataFormatada(filtro.getVencimentoInicial()) + "', 'dd/MM/yyyy') and ")
                .append(" to_date('" + DataUtil.getDataFormatada(filtro.getVencimentoFinal()) + "', 'dd/MM/yyyy')");

            filtros.append("Vencimento inicial: ").append(DataUtil.getDataFormatada(filtro.getVencimentoInicial())).append(", ");
            filtros.append("Vencimento final: ").append(DataUtil.getDataFormatada(filtro.getVencimentoFinal())).append(", ");
        }

        if (filtro.getLancamentoInicial() != null && filtro.getLancamentoFinal() != null) {
            condicao.append(" and calc.dataCalculo between ")
                .append(" to_date('" + DataUtil.getDataFormatada(filtro.getLancamentoInicial()) + "', 'dd/MM/yyyy') and ")
                .append(" to_date('" + DataUtil.getDataFormatada(filtro.getLancamentoFinal()) + "', 'dd/MM/yyyy')");

            filtros.append("Lançamento inicial: ").append(DataUtil.getDataFormatada(filtro.getLancamentoInicial())).append(", ");
            filtros.append("Lançamento final: ").append(DataUtil.getDataFormatada(filtro.getLancamentoFinal())).append(", ");
        }

        if (filtro.getPagamentoInicial() != null && filtro.getPagamentoFinal() != null) {
            condicao.append(" and lote.datapagamento between ")
                .append(" to_date('" + DataUtil.getDataFormatada(filtro.getPagamentoInicial()) + "', 'dd/MM/yyyy') and ")
                .append(" to_date('" + DataUtil.getDataFormatada(filtro.getPagamentoInicial()) + "', 'dd/MM/yyyy')");

            filtros.append("Pagamento Inicial: ").append(DataUtil.getDataFormatada(filtro.getPagamentoInicial())).append(", ");
            filtros.append("Pagamento Final: ").append(DataUtil.getDataFormatada(filtro.getPagamentoInicial())).append(", ");
        }

        if (filtro.getMovimentoInicial() != null && filtro.getMovimentoFinal() != null) {
            condicao.append(" and lote.datafinanciamento between ")
                .append(" to_date('" + DataUtil.getDataFormatada(filtro.getMovimentoInicial()) + "', 'dd/MM/yyyy') and ")
                .append(" to_date('" + DataUtil.getDataFormatada(filtro.getMovimentoFinal()) + "', 'dd/MM/yyyy')");

            filtros.append("Movimento Inicial: ").append(DataUtil.getDataFormatada(filtro.getMovimentoInicial())).append(", ");
            filtros.append("Movimento Final: ").append(DataUtil.getDataFormatada(filtro.getMovimentoFinal())).append(", ");
        }

        if (filtro.getNotificacaoInicial() != null && filtro.getNotificacaoFinal() != null) {
            condicao.append(" and trunc(mala.geradoem) between ")
                .append(" to_date('" + DataUtil.getDataFormatada(filtro.getNotificacaoInicial()) + "', 'dd/MM/yyyy') and ")
                .append(" to_date('" + DataUtil.getDataFormatada(filtro.getNotificacaoFinal()) + "', 'dd/MM/yyyy')");

            filtros.append("Notificação Inicial: ").append(DataUtil.getDataFormatada(filtro.getNotificacaoInicial())).append(", ");
            filtros.append("Notificação Final: ").append(DataUtil.getDataFormatada(filtro.getNotificacaoFinal())).append(", ");
        }

        if (filtro.getExercicioInicial() != null) {
            condicao.append(" and vd.exercicio_id = ").append(filtro.getExercicioInicial().getId());
            filtros.append("Exercício do débito = ").append(filtro.getExercicioInicial().getAno());
        }

        if (filtro.getExercicioFinal() != null) {
            condicao.append(" and vd.exercicio_id = ").append(filtro.getExercicioFinal().getId());
            filtros.append("Exercício do débito = ").append(filtro.getExercicioFinal().getAno());
        }

        if (!filtro.getDividas().isEmpty()) {
            String descricao = "Dívida: ";
            condicao.append(" and vd.divida_id in (").append(Util.getIdsDaListaAsString(filtro.getDividas())).append(")");
            for (Divida divida : filtro.getDividas()) {
                descricao += divida.getDescricao() + ", ";
            }
            filtros.append(descricao);
        }

        if (filtro.getSituacoes() != null && filtro.getSituacoes().length > 0) {
            if (filtro.getSituacoes().length == SituacaoParcela.getValues().size()) {
                filtros.append("Situação(ões) de Pagamento: Todas");
            } else {
                String juncao = "";
                String ids = "";
                String situacoes = "";
                for (SituacaoParcela situacaoParcela : filtro.getSituacoes()) {
                    ids += juncao + "'" + situacaoParcela.name() + "'";
                    situacoes += juncao + situacaoParcela.getDescricao();
                    juncao = ", ";
                }
                condicao.append(" and spvd.situacaoparcela in (").append(ids).append(")");
                filtros.append("Situação(ões) de Pagamento: ").append(situacoes).append("; ");
            }
        }

        filtro.setFiltro(filtro.getFiltro() + filtros);
        return condicao;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public FiltroMalaDiretaGeral getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroMalaDiretaGeral filtro) {
        this.filtro = filtro;
    }

    public boolean isPessoa() {
        return filtro != null && TipoCadastroTributario.PESSOA.equals(filtro.getTipoCadastroTributario());
    }

    public boolean isRural() {
        return filtro != null && TipoCadastroTributario.RURAL.equals(filtro.getTipoCadastroTributario());
    }

    public boolean isImobiliario() {
        return filtro != null && TipoCadastroTributario.IMOBILIARIO.equals(filtro.getTipoCadastroTributario());
    }

    public boolean isEconomico() {
        return filtro != null && TipoCadastroTributario.ECONOMICO.equals(filtro.getTipoCadastroTributario());
    }
}
