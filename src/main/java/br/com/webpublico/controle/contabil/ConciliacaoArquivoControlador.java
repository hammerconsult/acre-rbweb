package br.com.webpublico.controle.contabil;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SubConta;
import br.com.webpublico.entidades.TipoConciliacao;
import br.com.webpublico.entidades.contabil.ConciliacaoArquivo;
import br.com.webpublico.entidades.contabil.ConciliacaoArquivoMovimentoArquivo;
import br.com.webpublico.entidades.contabil.ConciliacaoArquivoMovimentoSistema;
import br.com.webpublico.entidadesauxiliares.BarraProgressoItens;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoOperacaoConcilicaoBancaria;
import br.com.webpublico.enums.contabil.TipoFiltroConciliacaoArquivo;
import br.com.webpublico.enums.contabil.TipoMovimentoConciliacaoArquivo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.contabil.execucao.ConciliacaoArquivoFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoConciliacaoArquivo", pattern = "/contabil/conciliacao-arquivo/novo/", viewId = "/faces/financeiro/conciliacao-arquivo/edita.xhtml"),
    @URLMapping(id = "editarConciliacaoArquivo", pattern = "/contabil/conciliacao-arquivo/editar/#{conciliacaoArquivoControlador.id}/", viewId = "/faces/financeiro/conciliacao-arquivo/edita.xhtml"),
    @URLMapping(id = "listarConciliacaoArquivo", pattern = "/contabil/conciliacao-arquivo/listar/", viewId = "/faces/financeiro/conciliacao-arquivo/lista.xhtml"),
    @URLMapping(id = "verConciliacaoArquivo", pattern = "/contabil/conciliacao-arquivo/ver/#{conciliacaoArquivoControlador.id}/", viewId = "/faces/financeiro/conciliacao-arquivo/visualizar.xhtml")
})
public class ConciliacaoArquivoControlador extends PrettyControlador<ConciliacaoArquivo> implements Serializable, CRUD {
    @EJB
    private ConciliacaoArquivoFacade facade;
    private List<String> tiposFiltrosSelecionados;
    private List<String> tiposMovimentosSelecionados;
    private Integer quantidadeSucesso;
    private Integer quantidadeAlerta;
    private Integer quantidadeErro;
    private BarraProgressoItens barraProgressoItens;
    private Future<BarraProgressoItens> future;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private SubConta subConta;
    private TipoOperacaoConcilicaoBancaria tipoOperacaoConcilicaoBancaria;
    private TipoConciliacao tipoConciliacao;
    private Boolean mostrarSomenteNaoInformadas;
    private Boolean selecionarTodos;
    private List<ConciliacaoArquivoMovimentoArquivo> futurasPendencias;
    private ConciliacaoArquivoMovimentoArquivo movimentoArquivo;
    private List<ConciliacaoArquivoMovimentoArquivo> movimentosFiltrados;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public ConciliacaoArquivoControlador() {
        super(ConciliacaoArquivo.class);
    }

    @URLAction(mappingId = "novoConciliacaoArquivo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        inicializarQuantidades();
        limparVariaveisDialogPendencias();
        limparMovimentoArquivo();

        tiposFiltrosSelecionados = Lists.newArrayList(
            TipoFiltroConciliacaoArquivo.DATA.name(),
            TipoFiltroConciliacaoArquivo.VALOR.name());

        tiposMovimentosSelecionados = Lists.newArrayList(
            TipoMovimentoConciliacaoArquivo.TRANSFERENCIA_FINANCEIRA.name(),
            TipoMovimentoConciliacaoArquivo.CREDITO.name(),
            TipoMovimentoConciliacaoArquivo.DEBITO.name(),
            TipoMovimentoConciliacaoArquivo.DEPOSITO.name());
    }

    @URLAction(mappingId = "editarConciliacaoArquivo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        atualizarQuantidadesComMovimentos();
        limparVariaveisDialogPendencias();
        limparMovimentoArquivo();
    }

    @URLAction(mappingId = "verConciliacaoArquivo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        atualizarQuantidadesComMovimentos();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/contabil/conciliacao-arquivo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    private void inicializarQuantidades() {
        quantidadeSucesso = 0;
        quantidadeAlerta = 0;
        quantidadeErro = 0;
    }

    public List<String> getTiposFiltrosSelecionados() {
        return tiposFiltrosSelecionados;
    }

    public void setTiposFiltrosSelecionados(List<String> tiposFiltrosSelecionados) {
        this.tiposFiltrosSelecionados = tiposFiltrosSelecionados;
    }

    public List<String> getTiposMovimentosSelecionados() {
        return tiposMovimentosSelecionados;
    }

    public void setTiposMovimentosSelecionados(List<String> tiposMovimentosSelecionados) {
        this.tiposMovimentosSelecionados = tiposMovimentosSelecionados;
    }

    public Integer getQuantidadeSucesso() {
        return quantidadeSucesso;
    }

    public void setQuantidadeSucesso(Integer quantidadeSucesso) {
        this.quantidadeSucesso = quantidadeSucesso;
    }

    public Integer getQuantidadeAlerta() {
        return quantidadeAlerta;
    }

    public void setQuantidadeAlerta(Integer quantidadeAlerta) {
        this.quantidadeAlerta = quantidadeAlerta;
    }

    public Integer getQuantidadeErro() {
        return quantidadeErro;
    }

    public void setQuantidadeErro(Integer quantidadeErro) {
        this.quantidadeErro = quantidadeErro;
    }

    public Future<BarraProgressoItens> getFuture() {
        return future;
    }

    public void setFuture(Future<BarraProgressoItens> future) {
        this.future = future;
    }

    public BarraProgressoItens getBarraProgressoItens() {
        return barraProgressoItens;
    }

    public void setBarraProgressoItens(BarraProgressoItens barraProgressoItens) {
        this.barraProgressoItens = barraProgressoItens;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public TipoOperacaoConcilicaoBancaria getTipoOperacaoConcilicaoBancaria() {
        return tipoOperacaoConcilicaoBancaria;
    }

    public void setTipoOperacaoConcilicaoBancaria(TipoOperacaoConcilicaoBancaria tipoOperacaoConcilicaoBancaria) {
        this.tipoOperacaoConcilicaoBancaria = tipoOperacaoConcilicaoBancaria;
    }

    public TipoConciliacao getTipoConciliacao() {
        return tipoConciliacao;
    }

    public void setTipoConciliacao(TipoConciliacao tipoConciliacao) {
        this.tipoConciliacao = tipoConciliacao;
    }

    public Boolean getMostrarSomenteNaoInformadas() {
        return mostrarSomenteNaoInformadas == null ? Boolean.FALSE : mostrarSomenteNaoInformadas;
    }

    public void setMostrarSomenteNaoInformadas(Boolean mostrarSomenteNaoInformadas) {
        this.mostrarSomenteNaoInformadas = mostrarSomenteNaoInformadas;
    }


    public Boolean getSelecionarTodos() {
        return selecionarTodos == null ? Boolean.FALSE : selecionarTodos;
    }

    public void setSelecionarTodos(Boolean selecionarTodos) {
        this.selecionarTodos = selecionarTodos;
    }

    public List<ConciliacaoArquivoMovimentoArquivo> getFuturasPendencias() {
        return futurasPendencias;
    }

    public void setFuturasPendencias(List<ConciliacaoArquivoMovimentoArquivo> futurasPendencias) {
        this.futurasPendencias = futurasPendencias;
    }

    public List<HierarquiaOrganizacional> completarHierarquiasOrcamentarias(String parte) {
        return facade.buscarHierarquiasOrganizacionais(parte);
    }

    public List<SubConta> completarContasFinanceiras(String parte) {
        return facade.buscarContasFinanceiras(parte);
    }

    public List<TipoConciliacao> completarTiposConciliacoes(String parte) {
        return facade.buscarTiposConciliacoes(parte);
    }

    public List<SelectItem> getOperacoesConciliacoes() {
        return Util.getListSelectItem(TipoOperacaoConcilicaoBancaria.values());
    }

    public List<TipoFiltroConciliacaoArquivo> getTiposFiltros() {
        return Arrays.asList(TipoFiltroConciliacaoArquivo.values());
    }

    public List<TipoMovimentoConciliacaoArquivo> getTiposMovimentos() {
        return Arrays.asList(TipoMovimentoConciliacaoArquivo.values());
    }

    public void importar(FileUploadEvent evento) {
        try {
            UploadedFile file = evento.getFile();
            selecionado.setArquivo(facade.criarArquivo(file));
            InputStreamReader in = new InputStreamReader(file.getInputstream(), StandardCharsets.ISO_8859_1);
            BufferedReader bufferedReader = new BufferedReader(in);
            String line;
            inicializarQuantidades();
            selecionado.getMovimentos().clear();
            ConciliacaoArquivoMovimentoArquivo mov = null;
            String conta = null;
            String digitoVerificador = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("ACCTID")) {
                    String bancoAgencia = line.replace("<ACCTID>", "").replaceAll("\\t", "").replaceAll(" ", "");
                    if (bancoAgencia.length() > 8) {
                        conta = bancoAgencia.substring(4, 8);
                    } else {
                        conta = bancoAgencia;
                    }
                    if (conta.contains("-")) {
                        digitoVerificador = conta.substring(conta.indexOf("-")).replace("-", "");
                        conta = conta.substring(0, conta.indexOf("-"));
                    }
                }
                mov = criarConciliacaoArquivoMovimento(line, mov, selecionado);
            }
            selecionado.setContaBancariaEntidade(facade.buscarContaBancaria(conta, digitoVerificador));
            facade.buscarMovimentos(selecionado, tiposFiltrosSelecionados);
            atualizarQuantidadesComMovimentos();
        } catch (Exception e) {
            logger.error("Erro ao importar arquivo de conciliação bancara.", e);
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Ocorreu um erro durante a importação do arquivo: " + e.getMessage());
        }
    }

    private ConciliacaoArquivoMovimentoArquivo criarConciliacaoArquivoMovimento(String line, ConciliacaoArquivoMovimentoArquivo mov, ConciliacaoArquivo selecionado) {
        if (line.contains("<STMTTRN>")) {
            mov = new ConciliacaoArquivoMovimentoArquivo();
            mov.setConciliacaoArquivo(selecionado);
        }
        if (mov != null) {
            if (line.contains("TRNTYPE")) {
                for (TipoMovimentoConciliacaoArquivo tipoMovimento : TipoMovimentoConciliacaoArquivo.values()) {
                    if (line.contains(tipoMovimento.getIdentificadorArquivo())) {
                        mov.setTipo(tipoMovimento);
                        break;
                    }
                }
            }
            if (line.contains("<TRNAMT>")) {
                String valor = line.replace("<TRNAMT>", "").replaceAll("\\t", "").replaceAll(" ", "");
                mov.setValor(Util.converterValor(valor.replace("-", "")));
            }
            if (line.contains("<MEMO>")) {
                mov.setHistorico(line.replace("<MEMO>", "").replaceAll("\\t", "").trim());
            }
            if (line.contains("<DTPOSTED>")) {
                String data = line.replace("<DTPOSTED>", "").replaceAll("\\t", "").replaceAll(" ", "");
                mov.setDataMovimento(DataUtil.getDateParse(data, "yyyyMMdd"));
            }
            if (line.contains("<FITID>")) {
                String identificador = line.replace("<FITID>", "").replaceAll("\\t", "").replaceAll(" ", "");
                mov.setIdentificador(identificador);
            }
            if (line.contains("<CHECKNUM>")) {
                String numeroDocumento = line.replace("<CHECKNUM>", "").replaceAll("\\t", "").replaceAll(" ", "");
                mov.setNumeroDocumento(numeroDocumento);
            }
            if (line.contains("</STMTTRN>")) {
                if (mov.getTipo() != null &&
                    tiposMovimentosSelecionados.contains(mov.getTipo().name()) &&
                    mov.getDataMovimento() != null &&
                    mov.getIdentificador() != null &&
                    mov.getValor() != null) {
                    selecionado.getMovimentos().add(mov);
                }
                mov = null;
            }
        }
        return mov;
    }

    private void atualizarQuantidadesComMovimentos() {
        inicializarQuantidades();
        selecionado.getMovimentos().forEach(mov -> {
            if (!mov.getMovimentosSistemas().isEmpty()) {
                if (mov.getMovimentosSistemas().size() > 1) {
                    quantidadeAlerta++;
                } else {
                    quantidadeSucesso++;
                }
            } else {
                quantidadeErro++;
            }
        });
    }

    public void removerArquivo() {
        selecionado.getMovimentos().clear();
        selecionado.setArquivo(null);
        inicializarQuantidades();
    }

    public void removerMovimentoArquivo(ConciliacaoArquivoMovimentoArquivo movimentoArquivo) {
        selecionado.getMovimentos().remove(movimentoArquivo);
        atualizarQuantidadesComMovimentos();
        if (movimentosFiltrados != null) {
            movimentosFiltrados.remove(movimentoArquivo);
        }
    }

    public void removerMovimentoArquivoSistema(ConciliacaoArquivoMovimentoArquivo movimentoArquivo, ConciliacaoArquivoMovimentoSistema movimentoSistema) {
        movimentoArquivo.getMovimentosSistemas().remove(movimentoSistema);
        movimentoArquivo.atualizarStyles();
        if (movimentoArquivo.getMovimentosSistemas().size() == 1) {
            movimentoArquivo.getMovimentosSistemas().get(0).setCorrespondente(Boolean.TRUE);
            movimentoArquivo.getMovimentosSistemas().get(0).atualizarStyles();
        }
        atualizarQuantidadesComMovimentos();
    }

    public void conciliar() {
        try {
            Date dataConciliacao = new Date();
            validarConciliar(dataConciliacao);
            selecionado.setDataConciliacao(dataConciliacao);
            barraProgressoItens = new BarraProgressoItens();
            future = facade.conciliar(barraProgressoItens, selecionado);
            FacesUtil.executaJavaScript("iniciarConciliacao()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("erro ao conciliar arquivo: ", ex);
            FacesUtil.addErrorGenerico(ex);
        }
    }

    private void validarConciliar(Date dataConciliacao) {
        ValidacaoException ve = new ValidacaoException();
        for (ConciliacaoArquivoMovimentoArquivo movimentoArquivo : selecionado.getMovimentos()) {
            if (!movimentoArquivo.getMovimentosSistemasNaoConciliados().isEmpty() &&
                movimentoArquivo.getMovimentosSistemasNaoConciliados().stream().noneMatch(ConciliacaoArquivoMovimentoSistema::getCorrespondente)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O movimento do arquivo <b>" + movimentoArquivo.toString() + "</b> deve ter um correspondente selecionado para poder conciliar.");
            }
        }
        ve.lancarException();
        facade.validarMesContabil(dataConciliacao);
    }

    public void finalizarConciliacao() {
        if (future != null && future.isDone()) {
            try {
                FacesUtil.executaJavaScript("terminarConciliacao()");
                FacesUtil.atualizarComponente("Formulario");
            } catch (Exception ex) {
                logger.error("Error ao conciliar arquivo: ", ex);
                FacesUtil.addErrorPadrao(ex);
            }

        }
    }

    public void abortarConciliacao() {
        if (future != null) {
            future.cancel(true);
        }
    }

    public void selecionarOuDesselecionarTodasFuturasPendencias() {
        for (ConciliacaoArquivoMovimentoArquivo fp : futurasPendencias) {
            fp.setSelecionado(selecionarTodos);
        }
    }

    public void buscarFuturasPendencias() {
        futurasPendencias.clear();
        for (ConciliacaoArquivoMovimentoArquivo movimentoArquivo : selecionado.getMovimentos()) {
            if (movimentoArquivo.getMovimentosSistemas().isEmpty()) {
                if (mostrarSomenteNaoInformadas) {
                    if (movimentoArquivo.getUnidadeOrganizacional() == null) {
                        futurasPendencias.add(movimentoArquivo);
                    }
                } else {
                    futurasPendencias.add(movimentoArquivo);
                }
            }
        }
    }

    public void atualizarFuturasPendencias() {
        try {
            validarFuturasPendencias();
            for (ConciliacaoArquivoMovimentoArquivo fp : futurasPendencias) {
                if (fp.getSelecionado()) {
                    fp.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
                    fp.setSubConta(subConta);
                    fp.setTipoOperacaoConcilicaoBancaria(tipoOperacaoConcilicaoBancaria);
                    fp.setTipoConciliacao(tipoConciliacao);
                    fp.setSelecionado(Boolean.FALSE);
                    Util.adicionarObjetoEmLista(selecionado.getMovimentos(), fp);
                }
            }
            limparVariaveisDialogPendencias();
            FacesUtil.executaJavaScript("dialogPendencias.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao atualizar futuras pendências: ", ex);
            FacesUtil.addErrorPadrao(ex);
        }
    }

    private void validarFuturasPendencias() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Orçamentária deve ser informado.");
        }
        if (subConta == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta Financeira deve ser informado.");
        }
        if (tipoOperacaoConcilicaoBancaria == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Operação da Conciliação deve ser informado.");
        }
        if (tipoConciliacao == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Conciliação deve ser informado.");
        }
        ve.lancarException();
    }

    public void limparVariaveisDialogPendencias() {
        mostrarSomenteNaoInformadas = Boolean.FALSE;
        selecionarTodos = Boolean.FALSE;
        futurasPendencias = Lists.newArrayList();
        hierarquiaOrganizacional = null;
        subConta = null;
        tipoOperacaoConcilicaoBancaria = null;
        tipoConciliacao = null;
    }

    public boolean isArquivoConciliado() {
        return selecionado.getDataConciliacao() != null;
    }

    public void atualizarMovimentoArquivo() {
        try {
            validarFuturasPendencias();
            movimentoArquivo.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
            movimentoArquivo.setSubConta(subConta);
            movimentoArquivo.setTipoOperacaoConcilicaoBancaria(tipoOperacaoConcilicaoBancaria);
            movimentoArquivo.setTipoConciliacao(tipoConciliacao);
            Util.adicionarObjetoEmLista(selecionado.getMovimentos(), movimentoArquivo);
            limparMovimentoArquivo();
            FacesUtil.executaJavaScript("dialogPendenciaUnica.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao atualizar futuras pendências: ", ex);
            FacesUtil.addErrorPadrao(ex);
        }
    }

    public void editarMovimentoArquivo(ConciliacaoArquivoMovimentoArquivo mov) {
        movimentoArquivo = (ConciliacaoArquivoMovimentoArquivo) Util.clonarObjeto(mov);
        hierarquiaOrganizacional = facade.getHierarquiaDaUnidade(selecionado, movimentoArquivo.getUnidadeOrganizacional());
        subConta = movimentoArquivo.getSubConta();
        tipoOperacaoConcilicaoBancaria = movimentoArquivo.getTipoOperacaoConcilicaoBancaria();
        tipoConciliacao = movimentoArquivo.getTipoConciliacao();
    }

    public void limparMovimentoArquivo() {
        movimentoArquivo = null;
        hierarquiaOrganizacional = null;
        subConta = null;
        tipoOperacaoConcilicaoBancaria = null;
        tipoConciliacao = null;
    }

    public List<ConciliacaoArquivoMovimentoArquivo> getMovimentosFiltrados() {
        return movimentosFiltrados;
    }

    public void setMovimentosFiltrados(List<ConciliacaoArquivoMovimentoArquivo> movimentosFiltrados) {
        this.movimentosFiltrados = movimentosFiltrados;
    }
}
