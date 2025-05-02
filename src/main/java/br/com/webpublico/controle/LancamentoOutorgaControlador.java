/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.StatusLancamentoOutorga;
import br.com.webpublico.enums.TipoMesVencimento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.tributario.consultadebitos.enums.SituacaoParcelaDTO;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author cheles
 */
@ManagedBean(name = "lancamentoOutorgaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoLancamentoOutorga", pattern = "/lancamento-de-outorga/novo/",
        viewId = "/faces/tributario/rbtrans/lancamentodeoutorga/edita.xhtml"),
    @URLMapping(id = "listarLancamentoOutorga", pattern = "/lancamento-de-outorga/listar/",
        viewId = "/faces/tributario/rbtrans/lancamentodeoutorga/lista.xhtml"),
    @URLMapping(id = "verLancamentoOutorga", pattern = "/lancamento-de-outorga/ver/#{lancamentoOutorgaControlador.id}/",
        viewId = "/faces/tributario/rbtrans/lancamentodeoutorga/visualizar.xhtml")
})
public class LancamentoOutorgaControlador extends PrettyControlador<LancamentoOutorga> implements Serializable, CRUD {

    @EJB
    private LancamentoOutorgaFacade lancamentoOutorgaFacade;
    @EJB
    private ParametroOutorgaFacade parametroOutorgaFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private PermissaoTransporteFacade permissaoTransporteFacade;
    @EJB
    private ProcessoDebitoFacade processoDebitoFacade;
    @EJB
    private DAMFacade dAMFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContribuinteDebitoOutorgaFacade contribuinteDebitoOutorgaFacade;
    private ConverterAutoComplete converterExercicio;
    private ConverterAutoComplete converterCmc;
    private boolean damVencido;
    private boolean podeImprimirDam = true;
    private List<ResultadoParcela> parcelas;
    private BigDecimal valorTotal;
    private Boolean outorgaLancado = false;
    private UploadedFile file;
    private Arquivo arquivoSelecionado;
    @EJB
    private DAMFacade dam;
    private Boolean mostraDataSugerida = false;
    private Boolean criaNovaDataVencimento;
    private Boolean teste = false;
    private List<OutorgaIPO> outorgaIPOs;
    private OutorgaIPO outorgaIPO;

    public LancamentoOutorgaControlador() {
        super(LancamentoOutorga.class);
    }


    @Override
    public void setSelecionado(LancamentoOutorga selecionado) {
        this.selecionado = selecionado;
    }

    public Boolean getMostraDataSugerida() {
        return mostraDataSugerida;
    }

    public void setMostraDataSugerida(Boolean mostraDataSugerida) {
        this.mostraDataSugerida = mostraDataSugerida;
    }

    public Boolean getOutorgaLancado() {
        return outorgaLancado;
    }

    public void setOutorgaLancado(Boolean outorgaLancado) {
        this.outorgaLancado = outorgaLancado;
    }

    public Boolean getCriaNovaDataVencimento() {
        return criaNovaDataVencimento;
    }

    public void setCriaNovaDataVencimento(Boolean criaNovaDataVencimento) {
        this.criaNovaDataVencimento = criaNovaDataVencimento;
    }

    public List<OutorgaIPO> getOutorgaIPOs() {
        return outorgaIPOs;
    }

    public void setOutorgaIPOs(List<OutorgaIPO> outorgaIPOs) {
        this.outorgaIPOs = outorgaIPOs;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/lancamento-de-outorga/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public boolean isPodeImprimirDam() {
        return podeImprimirDam;
    }

    public void setPodeImprimirDam(boolean podeImprimirDam) {
        this.podeImprimirDam = podeImprimirDam;
    }

    @URLAction(mappingId = "novoLancamentoOutorga", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        if (parametroOutorgaFacade.getParametroOutorgaRBTransVigente() == null) {
            FacesUtil.addOperacaoNaoPermitida("Verifique as Taxas de Parâmetro de Outorga");
            redireciona();
        }
        damVencido = false;
        selecionado.setDataLancamento(SistemaFacade.getDataCorrente());
        selecionado.setUsuarioLancamento(getSistemaControlador().getUsuarioCorrente());
        arquivoSelecionado = new Arquivo();
        selecionado.setStatusLancamentoOutorga(StatusLancamentoOutorga.LANCADO);
        criaNovaDataVencimento = false;
        selecionado.setDiaVencimentoParaAparecerNoCalender(null);
    }

    public void validaSeANovaDataFoiInformada() {
        atribuirNovaDataVencimento();
        if (selecionado.getDiaVencimentoParaAparecerNoCalender() != null) {
            salvar();
            FacesUtil.executaJavaScript("vencimento.hide()");
            FacesUtil.atualizarComponente("Formulario");
        } else {
            FacesUtil.addError("Não foi possível continuar!", "Informe a data de vencimento da parcela.");
        }
    }

    public void atribuirNovaDataVencimento() {
        if (selecionado.getDiaVencimentoParaAparecerNoCalender() != null) {
            Calendar dataEscolhida = Calendar.getInstance();
            dataEscolhida.setTime(selecionado.getDiaVencimentoParaAparecerNoCalender());
            selecionado.setDiaVencimentoParcelaSugerido(dataEscolhida.get(Calendar.DAY_OF_MONTH));
        }
        atribuirParametroTipoMesVencimento();
    }

    @Override
    public void salvar() {
        if (outorgaIPO != null) {
            salvar(outorgaIPO);
        }
    }

    private void validarRegrasDeNegocio() {
        ValidacaoException ve = new ValidacaoException();

        if (selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Exercício.");
        }

        if (selecionado.getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Mês.");
        }

        if (selecionado.getCmc() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o C.M.C.");
        }

        if (selecionado.getTarifaOutorga() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Tarifa.");
        }

        if (selecionado.getPassageiroTranspEquiv() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Passageiro Equivamente Transportado.");
        } else if (selecionado.getPassageiroTranspEquiv().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Passageiro Equivalente Transportado não pode ser menor que zero!");
        }

        ve.lancarException();
    }

    public void atribuirParametroTipoMesVencimento() {

        Integer anoReferencia = selecionado.getExercicio().getAno();
        Integer mesReferencia = selecionado.getMes().getNumeroMes();

        if (TipoMesVencimento.MES_REFERENCIA.equals(parametroOutorgaFacade.getParametroOutorgaRBTransVigente().getTipoMesVencimento())) {
            selecionado.setMesVencimento(selecionado.getMes());
            selecionado.setAnoVencimento(anoReferencia);

        } else if (TipoMesVencimento.MES_PRECEDENTE_REFERENCIA.equals(parametroOutorgaFacade.getParametroOutorgaRBTransVigente().getTipoMesVencimento())) {
            if (mesReferencia == 1) {
                mesReferencia = 12;
                selecionado.setMesVencimento(Mes.getMesToInt(mesReferencia));

                anoReferencia = selecionado.getExercicio().getAno() - 1;
                selecionado.setAnoVencimento(anoReferencia);

            }else{
                mesReferencia = selecionado.getMes().getNumeroMes() -1;
                selecionado.setMesVencimento(Mes.getMesToInt(mesReferencia));
                selecionado.setAnoVencimento(selecionado.getExercicio().getAno());
            }
        } else {
            if (mesReferencia == 12) {
                mesReferencia = 1;
                selecionado.setMesVencimento(Mes.getMesToInt(mesReferencia));

                anoReferencia = selecionado.getExercicio().getAno() + 1;
                selecionado.setAnoVencimento(anoReferencia);

            }else{
                mesReferencia = selecionado.getMes().getNumeroMes() + 1;
                selecionado.setMesVencimento(Mes.getMesToInt(mesReferencia));
                selecionado.setAnoVencimento(selecionado.getExercicio().getAno());
            }
        }
    }


    public void salvar(OutorgaIPO outorgaIPO) {
        atribuirNovaDataVencimento();
        try {
            Date vencimentoDefinido = selecionado.getDiaVencimentoParaAparecerNoCalender();
            selecionado = lancamentoOutorgaFacade.salvarLancamento(selecionado);
            ProcessoCalculoLancamentoOutorga processo = null;
            if (Operacoes.NOVO.equals(operacao)) {
                processo = lancamentoOutorgaFacade.salvarNovo(selecionado, outorgaIPO);
                if (processo != null) {
                    selecionado = processo.getLancamentoOutorga();
                    atribuirParametroTipoMesVencimento();
                    if (vencimentoDefinido != null) {
                        selecionado.setDiaVencimentoParaAparecerNoCalender(vencimentoDefinido);
                    }
                    lancamentoOutorgaFacade.gerarDebito(processo);
                    FacesUtil.addOperacaoRealizada("Lançamento realizado com sucesso!");
                    redirecionaParaVer();
                }
            } else {
                FacesUtil.addOperacaoRealizada("Lançamento salvo com sucesso!");
                redirecionaParaVer();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro ao salvar o Lançamento! " + e.getMessage());
            logger.error("Erro ao salvar o Lançamento de Outorga: {}", e);
        }
    }

    public void redirecionaParaVer() {
        if (selecionado.getId() != null) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        }
    }

    public void lancarOutorga() {
        try {
            validarRegrasDeNegocio();
            validarLancamento();

            outorgaIPOs = contribuinteDebitoOutorgaFacade.buscarLancamentoOutorgaIPO(selecionado.getMes(), selecionado.getExercicio(), selecionado.getCmc());

            if (outorgaIPOs == null || outorgaIPOs.isEmpty()) {
                FacesUtil.addError("Não é possível continuar!", "Não foi encontrado configuração de IPO para o mês " + selecionado.getMes() + "/" + selecionado.getExercicio() + " do C.M.C. " + selecionado.getCmc());
            } else {
                if (outorgaIPOs.size() > 1) {
                    FacesUtil.atualizarComponente("idOutorgaIpo");
                    FacesUtil.executaJavaScript("outorgaIPO.show()");
                } else {
                    outorgaIPO = outorgaIPOs.get(0);
                    if (parametroOutorgaFacade.getParametroOutorgaRBTransVigente().getDiaVencimentoPrimeiraParcela() < Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                        FacesUtil.executaJavaScript("vencimento.show()");
                    } else {
                        salvar(outorgaIPO);
                    }
                }
            }
        } catch (ValidacaoException ve) {

            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void atribuirParametroOutorgaIPO(OutorgaIPO ipo) {
        outorgaIPO = ipo;
        if (parametroOutorgaFacade.getParametroOutorgaRBTransVigente().getDiaVencimentoPrimeiraParcela() < Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
            FacesUtil.executaJavaScript("vencimento.show()");
        } else {
            salvar(ipo);
        }

    }

    private void validarLancamento() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um Exercicios para o lançamento!");
        }
        if (selecionado.getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um Mês para o lançamento!");
        }
        if (selecionado.getCmc() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um C.M.C. para o lançamento!");
        }
        if (selecionado.getTarifaOutorga() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe uma Tarifa para o lançamento!");
        }
        if (parametroOutorgaFacade.getParametroOutorgaRBTransVigente().getDiaVencimentoPrimeiraParcela() < Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            && parametroOutorgaFacade.getParametroOutorgaRBTransVigente().getDiaVencimentoSegundaParcela() != null && parametroOutorgaFacade.getParametroOutorgaRBTransVigente().getDiaVencimentoSegundaParcela() < Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data da segunda parcela é menor do que a data de hoje, por favor verifique as datas do parâmetro!");
        }
        ve.lancarException();
    }


    @Override
    public void editar() {
        super.editar();
        selecionado = lancamentoOutorgaFacade.recuperar(selecionado);
        damVencido = damEstaVencido();
        arquivoSelecionado = new Arquivo();
        selecionado.setDiaVencimentoParaAparecerNoCalender(null);
    }


    @URLAction(mappingId = "verLancamentoOutorga", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        try {
            super.ver();
            calculaValorTotal();
        } catch (Exception ex) {
            FacesUtil.addError("", "Não foi possível encontrar as parcelas desse lançamento!");
        }
    }

    private void calculaValorTotal() {
        CalculoLancamentoOutorga calculo = lancamentoOutorgaFacade.recuperaValorDoLancamento(selecionado);
        valorTotal = calculo.getValorEfetivo();
        recuperaParcelasDaOutorga(calculo);
    }

    @Override
    public AbstractFacade getFacede() {
        return lancamentoOutorgaFacade;
    }

    public ConverterAutoComplete getConverterCMC() {
        if (converterCmc == null) {
            converterCmc = new ConverterAutoComplete(CadastroEconomico.class, cadastroEconomicoFacade);
        }
        return converterCmc;
    }

    public ConverterAutoComplete getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, lancamentoOutorgaFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public boolean isDamVencido() {
        return damVencido;
    }

    public void setDamVencido(boolean damVencido) {
        this.damVencido = damVencido;
    }

    public List<CadastroEconomico> completaCMC(String parte) {
        HashSet<CadastroEconomico> set = Sets.newHashSet(lancamentoOutorgaFacade.contribuintesCadastrados(parte.trim().toLowerCase()));
        return Lists.newArrayList(set);
    }

    public List<Exercicio> completaExercicios(String parte) {
        return lancamentoOutorgaFacade.getExercicioFacade().listaFiltrandoEspecial(parte.trim());
    }

    public void dialogEstorno() {
        selecionado.setDataEstorno(SistemaFacade.getDataCorrente());
        selecionado.setUsuarioEstorno(getSistemaControlador().getUsuarioCorrente());
        FacesUtil.executaJavaScript("dialogEstorno.show()");
    }

    public void estornarLancamento() {
        try {
            if (podeEstornar()) return;
            UsuarioSistema usuarioSistema = sistemaFacade.getUsuarioCorrente();
            gerarEstorno(getParcelas(), usuarioSistema);
            limparMensagensDoContexto();
            selecionado = lancamentoOutorgaFacade.salvarLancamento(selecionado);
            for (ResultadoParcela parcela : getParcelas()) {
                ParcelaValorDivida pvd = lancamentoOutorgaFacade.recuperaParcelaValorDivida(parcela.getIdParcela());
                dam.cancelarDamsDaParcela(pvd, usuarioSistema);
                ParcelaValorDivida pvdRecuperado = lancamentoOutorgaFacade.recuperaParcelaValorDividaEstorno(pvd.getId());
                lancamentoOutorgaFacade.pagarParcela(pvdRecuperado, parcela.getValorTotal());
            }
            FacesUtil.addInfo("Estorno realizado com sucesso!", null);

        } catch (Exception ex) {
            FacesUtil.addError("Não é possível estornar o lançamento!", ex.getMessage());
        }
        FacesUtil.executaJavaScript("dialogEstorno.hide()");
    }

    private boolean podeEstornar() {
        for (ResultadoParcela parcela : getParcelas()) {
            if (!SituacaoParcelaDTO.EM_ABERTO.equals(parcela.getSituacaoEnumValue()) && !SituacaoParcelaDTO.CANCELAMENTO.equals(parcela.getSituacaoEnumValue())) {
                FacesUtil.addError("Não foi possível estornar o lançamento!", "Existe(m) parcela(s) com situação diferente de 'Em Aberto' e/ou 'Canceladas'.");
                return true;
            }
        }
        if (selecionado.getMotivoEstorno().trim().equals("")) {
            FacesUtil.addError("Não foi possível estornar o lançamento!", "Informe o motivo do estorno.");
            return true;
        }
        return false;
    }

    public List<SelectItem> getMes() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (Mes mes : Mes.values()) {
            retorno.add(new SelectItem(mes, mes.getDescricao()));
        }
        return retorno;

    }

    private void limparMensagensDoContexto() {
        Iterator<FacesMessage> msgIterator = FacesContext.getCurrentInstance().getMessages();
        while (msgIterator.hasNext()) {
            msgIterator.next();
            msgIterator.remove();
        }
    }

    private void gerarEstorno(List<ResultadoParcela> parcelas, UsuarioSistema usuario) {
        selecionado.setStatusLancamentoOutorga(StatusLancamentoOutorga.ESTORNADO);
        FacesUtil.printAllMessages(processoDebitoFacade.gerarEstornoDeLancamentoOutorga(parcelas, usuario).getMensagens());
    }

    public Boolean podeLancarDamComNovoVencimento() {
        if (selecionado.getDataVencimentoDam() != null) {
            return false;
        }
        return true;
    }

    private boolean damEstaVencido() {
        try {
            List<DAM> damsRecuperado = dAMFacade.recuperaDAMsPeloCalculo(selecionado.obterUnicoCalculoDaLista());
            for (DAM dams : damsRecuperado) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                if (sdf.parse(sdf.format(getSistemaControlador().getDataOperacao())).after(sdf.parse(sdf.format(dams.getVencimento())))) {
                    return true;
                }
            }
            return false;
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível continuar!", ex.getMessage()));
            return true;
        }
    }


    public void handleFileUpload(FileUploadEvent event) {
        file = event.getFile();
        arquivoSelecionado.setDescricao(file.getFileName());
        adicionarArquivo();
    }

    public void adicionarArquivo() {
        ArquivoLancamentoOutorga arquivoLancamentoOutorga = new ArquivoLancamentoOutorga();
        arquivoLancamentoOutorga.setArquivo(arquivoSelecionado);
        arquivoLancamentoOutorga.setLancamentoOutorga(selecionado);
        arquivoLancamentoOutorga.setFile((Object) file);
        origemDoUpload(arquivoLancamentoOutorga);
        arquivoSelecionado.setMimeType(file.getContentType());
        arquivoSelecionado.setNome(file.getFileName());
        selecionado.getArquivos().add(arquivoLancamentoOutorga);
        arquivoSelecionado = new Arquivo();
    }

    private void origemDoUpload(ArquivoLancamentoOutorga arquivoLancamentoOutorga) {
        if (selecionado.getId() != null) {
            arquivoLancamentoOutorga.setOrigem("Estornado");
        } else {
            arquivoLancamentoOutorga.setOrigem("Lançado");
        }
    }

    public List<ArquivoLancamentoOutorga> getArquivos() {
        List<ArquivoLancamentoOutorga> lista = new ArrayList<>();
        for (ArquivoLancamentoOutorga a : selecionado.getArquivos()) {
            if (!a.getExcluido()) {
                lista.add(a);
            }
        }

        return lista;
    }

    public void removeArquivo(ArquivoLancamentoOutorga arquivoLancamentoOutorga) {
        if (arquivoLancamentoOutorga.getId() != null) {
            arquivoLancamentoOutorga.setExcluido(true);
        } else {
            selecionado.getArquivos().remove(arquivoLancamentoOutorga);
        }
    }

    public StreamedContent getArquivoStream(ArquivoLancamentoOutorga arquivoLancamentoOutorga) throws IOException {
        UploadedFile download = (UploadedFile) arquivoLancamentoOutorga.getFile();
        return new DefaultStreamedContent(download.getInputstream(), download.getContentType(), download.getFileName());
    }

    public void gerarDam() {
        try {
            imprimeDamDeOutorga(selecionado.obterUnicoCalculoDaLista());
        } catch (Exception ex) {
            FacesUtil.addError(ex.getMessage(), null);
        }
    }

    public void gerarDamVencido() {
        try {
            verificaSeODamEstaVencido(selecionado.obterUnicoCalculoDaLista());
        } catch (Exception ex) {
            FacesUtil.addError(ex.getMessage(), null);
        }
    }

    private void imprimeDamDeOutorga(Calculo calculo) throws JRException, IOException {
        try {
            ValorDivida valorDivida = permissaoTransporteFacade.recuperaValorDividaDoCalculo(calculo);
            if (gerarDamComNovoVencimento(calculo, valorDivida)) return;
            List<DAM> damsRecuperado = dAMFacade.recuperaDAMsPeloCalculo(calculo);
            if (podeImprimirDam(damsRecuperado)) {
                if (!damsRecuperado.isEmpty()) {
                    ImprimeDAM imprimeDAM = new ImprimeDAM();
                    imprimeDAM.setGeraNoDialog(true);
                    imprimeDAM.imprimirDamUnicoViaApi(damsRecuperado);
                } else {
                    List<DAM> dam = dAMFacade.geraDAMs(valorDivida.getParcelaValorDividas());
                    ImprimeDAM imprimeDAM = new ImprimeDAM();
                    imprimeDAM.setGeraNoDialog(true);
                    imprimeDAM.imprimirDamUnicoViaApi(dam);
                }

            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível continuar!", ex.getMessage()));
        }
    }

    private boolean gerarDamComNovoVencimento(Calculo calculo, ValorDivida valorDivida) throws Exception {
        if (selecionado.getDataVencimentoDam() != null) {
            List<DAM> damNovoVencimento = new ArrayList<>();
            for (ParcelaValorDivida pvd : valorDivida.getParcelaValorDividas()) {
                DAM dam = dAMFacade.geraDAM(pvd, selecionado.getDataVencimentoDam(), sistemaFacade.getExercicioCorrente(), sistemaFacade.getUsuarioCorrente());
                damNovoVencimento.add(dam);
            }
            new ImprimeDAM().imprimirDamUnicoViaApi(damNovoVencimento);
            return true;
        }
        return false;
    }

    public void verificaSeODamEstaVencido(Calculo calculo) throws IOException, JRException {
        FacesUtil.executaJavaScript("vencimentoDam.show()");
    }


    private Date vencimentoDam() {
        return new Date();
    }


    private Boolean podeImprimirDam(List<DAM> damsRecuperado) {
        boolean retorno = true;
        for (DAM dams : damsRecuperado) {
            if (!dams.getSituacao().equals(DAM.Situacao.ABERTO)) {
                FacesUtil.addError("Não foi possível gerar o DAM!", "Este DAM não se encontra com a situação Em Aberto.");
                retorno = false;
                break;
            }
        }
        return retorno;
    }

    public void teste() {
    }

    public void recuperaParcelasDaOutorga(CalculoLancamentoOutorga calc) {
        parcelas = new ConsultaParcela().addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calc.getId()).executaConsulta().getResultados();
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public List<ResultadoParcela> getParcelas() {
        return parcelas;
    }

    public Date ultimoDiaUtilDoMes() {
        Calendar dataSugerida = Calendar.getInstance();
        dataSugerida.setTime(DataUtil.ultimoDiaUtil(DataUtil.ultimoDiaMes(new Date()), feriadoFacade).getTime());
        dataSugerida.set(Calendar.MONTH, selecionado.getMesVencimento() != null ? selecionado.getMesVencimento().getNumeroMes() : selecionado.getMes().getNumeroMes());
        dataSugerida.set(Calendar.YEAR, selecionado.getAnoVencimento() != null ? selecionado.getAnoVencimento() : selecionado.getExercicio().getAno());
        return dataSugerida.getTime();
    }


    public void mostrarDataSugerida() {
        atribuirNovaDataVencimento();
        mostraDataSugerida = true;
        criaNovaDataVencimento = true;
    }

    public void verificaSeODamEstaVencidoParaAtualizarAPagina() {
        damVencido = false;
        FacesUtil.atualizarComponente("Formulario");
    }

    public Boolean getTeste() {
        return teste;
    }

    public void setTeste(Boolean teste) {
        this.teste = teste;
    }

    public void habilitarBotoesDeTipoDeLancamento() {
        mostraDataSugerida = false;
        criaNovaDataVencimento = false;
        FacesUtil.atualizarComponente("idVencimento");
    }
}
