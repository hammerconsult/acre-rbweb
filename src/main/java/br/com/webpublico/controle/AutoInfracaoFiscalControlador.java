/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.SituacaoAutoInfracao;
import br.com.webpublico.enums.TipoDoctoAcaoFiscal;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AutoInfracaoFiscalFacade;
import br.com.webpublico.negocios.ImprimeDAM;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author fabio
 */
@ManagedBean(name = "autoInfracaoFiscalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarAutoInfracaoFiscal",
        pattern = "/auto-de-infracao-fiscal/listar/",
        viewId = "/faces/tributario/fiscalizacao/autoinfracao/lista.xhtml"),
    @URLMapping(id = "verAutoInfracaoFiscal",
        pattern = "/auto-de-infracao-fiscal/ver/#{autoInfracaoFiscalControlador.id}/",
        viewId = "/faces/tributario/fiscalizacao/autoinfracao/visualizar.xhtml"),
    @URLMapping(id = "novoAutoInfracaoFiscal",
        pattern = "/auto-de-infracao-fiscal/novo/",
        viewId = "/faces/tributario/fiscalizacao/autoinfracao/edita.xhtml")
})
public class AutoInfracaoFiscalControlador extends PrettyControlador<AutoInfracaoFiscal> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(AutoInfracaoFiscalControlador.class);

    @EJB
    private AutoInfracaoFiscalFacade autoInfracaoFiscalFacade;
    private Map<Integer, Integer> diaVencimentoPorAno;
    private RegistroLancamentoContabil registroLancamentoContabil;
    private AcaoFiscal acaoFiscal;
    private LancamentoMultaFiscal lancamentoMultaFiscal;
    private List<LancamentoMultaFiscal> multasALancar;
    private List<LancamentoMultaFiscal> lancamentosMultasFiscais;
    private String fundamentacaoAutoInfracao;
    private String historicoAutoInfracao;
    private Exercicio exercicio;

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public AutoInfracaoFiscalControlador() {
        super(AutoInfracaoFiscal.class);
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @URLAction(mappingId = "verAutoInfracaoFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        if (selecionado.getSituacaoAutoInfracao() != null && !selecionado.getSituacaoAutoInfracao().equals(SituacaoAutoInfracao.CIENCIA)
            && !selecionado.getSituacaoAutoInfracao().equals(SituacaoAutoInfracao.REVELIA)
            && (!desabilitaCiencia() && !desabilitaRevelia())) {
            this.selecionado.setDataCienciaRevelia(new Date());
        }
        inicializar();
    }

    @URLAction(mappingId = "novoAutoInfracaoFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setAno(getSistemaControlador().getExercicioCorrenteAsInteger());
        selecionado.setNumero(autoInfracaoFiscalFacade.numeroAutoInfracaoMaisUm(selecionado.getAno()));
        selecionado.setSituacaoAutoInfracao(SituacaoAutoInfracao.GERADO);
        lancamentosMultasFiscais = Lists.newArrayList();
        lancamentoMultaFiscal = new LancamentoMultaFiscal();
        registroLancamentoContabil = new RegistroLancamentoContabil();
        registroLancamentoContabil.setNumeroRegistro(selecionado.getNumero().longValue());
        registroLancamentoContabil.setSituacao(RegistroLancamentoContabil.Situacao.AUTO_INFRACAO);
        selecionado.setRegistro(registroLancamentoContabil);
    }

    private void inicializar() {
        if (!isAutoInfracaoManual()) {
            acaoFiscal = autoInfracaoFiscalFacade.getAcaoFiscalFacade().recuperar(selecionado.getRegistro().getAcaoFiscal().getId());
        }
        registroLancamentoContabil = autoInfracaoFiscalFacade.recuperarRegistroLancamentoContabil(selecionado.getRegistro().getId());
        diaVencimentoPorAno = new HashMap<>();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            selecionado.setDoctoAcaoFiscal(autoInfracaoFiscalFacade.salvarDoctoAcaoFiscal(selecionado.getDoctoAcaoFiscal()));
            selecionado.setRegistro(autoInfracaoFiscalFacade.salvarRegistro(registroLancamentoContabil));
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCadastroEconomico() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Cadastro Econômico.");
        }
        if (selecionado.getRegistro().getMultas() == null || selecionado.getRegistro().getMultas().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe pelo menos uma multa.");
        }
        if (selecionado.getDoctoAcaoFiscal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("É necessário gerar o auto de infração.");
        }
        ve.lancarException();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/auto-de-infracao-fiscal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return autoInfracaoFiscalFacade;
    }

    public void ciencia() throws Exception {
        if (validaDataCienciaRevelia()) {
            if (validaParametros()) {
                try {
                    selecionado = autoInfracaoFiscalFacade.concluirAutoInfracao(selecionado, SituacaoAutoInfracao.CIENCIA);
                    FacesUtil.addOperacaoRealizada("Auto de Infração calculado com sucesso!");
                } catch (Exception e) {
                    FacesUtil.addError("Erro ao calcular o Auto de Infração!", e.getMessage());
                    logger.error("{}", e);
                }
            }
        } else {
            FacesUtil.addError("Atenção", "Não é possível efetuar o cálculo pois não foi configurado uma Dívida para o Auto de Infração na Configuração do Tributário!");
        }
    }

    public void revelia() {
        if (validaDataCienciaRevelia()) {
            if (selecionado.getObservacaoRevelia() != null && !selecionado.getObservacaoRevelia().trim().isEmpty()) {
                if (validaParametros()) {
                    try {
                        selecionado = autoInfracaoFiscalFacade.concluirAutoInfracao(selecionado, SituacaoAutoInfracao.REVELIA);
                        FacesUtil.executaJavaScript("dialogRevelia.hide()");
                        FacesUtil.addOperacaoRealizada("Auto de infração calculado com sucesso!");
                    } catch (Exception e) {
                        FacesUtil.addError("Erro ao calcular o Auto de Infração!", e.getMessage());
                        logger.error("{}", e);
                    }
                } else {
                    FacesUtil.addError("Atenção", "Não é possível efetuar o cálculo pois não foi configurado uma dívida para o Auto de infração na configuração do tributário!");
                }
            } else {
                FacesUtil.addCampoObrigatorio("Informe a observação da Revelia!");
            }
        }
    }

    public boolean temDebitosPagos() {
        return autoInfracaoFiscalFacade.getAcaoFiscalFacade().temDebitosPagos(selecionado.getDoctoAcaoFiscal().getAcaoFiscal());
    }

    public boolean desabilitaCiencia() {
        return ((!SituacaoAutoInfracao.GERADO.equals(selecionado.getSituacaoAutoInfracao()) && !SituacaoAutoInfracao.RETIFICADO.equals(selecionado.getSituacaoAutoInfracao())) ||
            !RegistroLancamentoContabil.Situacao.AUTO_INFRACAO.equals(selecionado.getRegistro().getSituacao()) ||
            temDebitosPagos());
    }

    private boolean desabilitaImpressaoDam() {
        return SituacaoAutoInfracao.CANCELADO.equals(selecionado.getSituacaoAutoInfracao())
            || SituacaoAutoInfracao.ESTORNADO.equals(selecionado.getSituacaoAutoInfracao())
            || SituacaoAutoInfracao.RETIFICADO.equals(selecionado.getSituacaoAutoInfracao());
    }

    public boolean desabilitaImpressaoDamAcaoFiscal() {
        boolean retorno = desabilitaImpressaoDam();
        if (!retorno) {
            retorno = (autoInfracaoFiscalFacade.getCalculoFiscalizacaoFacade().retornaCalculoDoAutoInfracao(selecionado) == null);
        }
        return retorno;
    }

    public boolean desabilitaImpressaoDamMulta() {
        boolean retorno = desabilitaImpressaoDam();
        if (!retorno) {
            retorno = (autoInfracaoFiscalFacade.getCalculoFiscalizacaoFacade().retornaCalculoMultaDoAutoInfracao(selecionado) == null);
        }
        return retorno;
    }

    public boolean desabilitaRevelia() {
        return ((!SituacaoAutoInfracao.GERADO.equals(selecionado.getSituacaoAutoInfracao()) && !SituacaoAutoInfracao.RETIFICADO.equals(selecionado.getSituacaoAutoInfracao())) ||
            !RegistroLancamentoContabil.Situacao.AUTO_INFRACAO.equals(selecionado.getRegistro().getSituacao()) ||
            temDebitosPagos());
    }

    public void imprimeDam() {
        try {
            CalculoFiscalizacao calculo = autoInfracaoFiscalFacade.getCalculoFiscalizacaoFacade().retornaCalculoDoAutoInfracao(selecionado);
            geraGuiaPagamento(calculo);
        } catch (Exception e) {
            FacesUtil.addError("Ocorreu um erro ao imprimir o DAM!", e.getMessage());
            logger.error("{}", e);
        }

    }

    public void imprimeDamMulta() {
        try {
            CalculoMultaFiscalizacao calculo = autoInfracaoFiscalFacade.getCalculoFiscalizacaoFacade().retornaCalculoMultaDoAutoInfracao(selecionado);
            geraGuiaPagamento(calculo);
        } catch (Exception e) {
            FacesUtil.addError("Ocorreu um erro ao imprimir o DAM!", e.getMessage());
            logger.error("{}", e);
        }

    }

    public void geraGuiaPagamento(Calculo calculo) throws JRException, IOException {
        ValorDivida valorDivida = autoInfracaoFiscalFacade.getAcaoFiscalFacade().retornaValorDividaDoCalculo(calculo);

        if (valorDivida != null) {
            Date vencimento = null;
            if (calculo instanceof CalculoMultaFiscalizacao) {
                vencimento = ((CalculoMultaFiscalizacao) calculo).getVencimento();
            } else if (calculo instanceof CalculoFiscalizacao) {
                vencimento = ((CalculoFiscalizacao) calculo).getVencimento();
            }
            try {
                DAM dam = autoInfracaoFiscalFacade.getDamFacade().geraDAM(valorDivida.getParcelaValorDividas().get(0), vencimento);
                new ImprimeDAM().imprimirDamUnicoViaApi(dam);
            } catch (Exception ex) {
                FacesUtil.addError("Ocorreu um erro ao gerar o DAM!", ex.getMessage());
                logger.error("{}", ex);
            }
        } else {
            FacesUtil.addError("Ocorreu um erro ao gerar o DAM!", "Não foi encontrado parcela com situação de 'EM ABERTO' para esse auto de infração!");
        }

    }

    private boolean validaParametros() {
        ConfiguracaoTributario config = autoInfracaoFiscalFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        return config.getDividaAutoInfracaoISS() != null;
    }

    public boolean validaDataCienciaRevelia() {
        if (selecionado.getDataCienciaRevelia() == null) {
            FacesUtil.addWarn("Atenção", "Informe a data da Ciência/Revelia.");
            return false;
        } else if (selecionado.getDataCienciaRevelia().compareTo(new Date()) > 0) {
            FacesUtil.addError("Atenção!", "A data da Ciência/Revelia não pode ser posterior a data atual.");
            return false;
        }
        return true;
    }

    public AcaoFiscal getAcaoFiscal() {
        return acaoFiscal;
    }

    public void setAcaoFiscal(AcaoFiscal acaoFiscal) {
        this.acaoFiscal = acaoFiscal;
    }

    public BigDecimal retornaValorTotalDeclaradoLancamento(Integer ano, RegistroLancamentoContabil reg) {
        return autoInfracaoFiscalFacade.getAcaoFiscalFacade().retornaValorTotalDeclaradoLancamento(ano, reg, acaoFiscal);
    }

    public BigDecimal retornaValorTotalApuradoLancamento(Integer ano, RegistroLancamentoContabil reg) {
        return autoInfracaoFiscalFacade.getAcaoFiscalFacade().retornaValorTotalApuradoLancamento(ano, reg, acaoFiscal);
    }

    public BigDecimal retornaValorTotalBaseCalculoLancamento(Integer ano, RegistroLancamentoContabil reg) {
        return autoInfracaoFiscalFacade.getAcaoFiscalFacade().retornaValorTotalBaseCalculoLancamento(ano, reg, acaoFiscal);
    }

    public BigDecimal retornaValorTotalIssPagoLancamento(Integer ano, RegistroLancamentoContabil reg) {
        return autoInfracaoFiscalFacade.getAcaoFiscalFacade().retornaValorTotalIssPagoLancamento(ano, reg, acaoFiscal);
    }

    public BigDecimal retornaValorTotalIssApuradoLancamento(Integer ano, RegistroLancamentoContabil reg) {
        return autoInfracaoFiscalFacade.getAcaoFiscalFacade().retornaValorTotalIssApuradoLancamento(ano, reg, acaoFiscal);
    }

    public BigDecimal retornaValorTotalIssDevidoLancamento(Integer ano, RegistroLancamentoContabil reg) {
        return autoInfracaoFiscalFacade.getAcaoFiscalFacade().retornaValorTotalIssDevidoLancamento(ano, reg, acaoFiscal);
    }

    public BigDecimal calculaCorrecaoLancamento(LancamentoContabilFiscal lcf) {
        return autoInfracaoFiscalFacade.getAcaoFiscalFacade().calcularCorrecaoLancamento(lcf, acaoFiscal);
    }

    public BigDecimal retornaValorTotalCorrecaoMonetariaLancamento(Integer ano, RegistroLancamentoContabil reg) {
        return autoInfracaoFiscalFacade.getAcaoFiscalFacade().retornarValorTotalCorrecaoMonetariaLancamento(ano, reg, acaoFiscal);
    }

    public BigDecimal calculaValorCorrigido(LancamentoContabilFiscal lcf) {
        return autoInfracaoFiscalFacade.getAcaoFiscalFacade().calcularValorCorrigido(lcf);
    }

    public BigDecimal retornaValorTotalCorrigidoLancamento(Integer ano, RegistroLancamentoContabil reg) {
        return autoInfracaoFiscalFacade.getAcaoFiscalFacade().retornarValorTotalCorrigidoLancamento(ano, reg, acaoFiscal);
    }

    public BigDecimal calculaJurosLancamento(LancamentoContabilFiscal lcf) {
        return autoInfracaoFiscalFacade.getAcaoFiscalFacade().calcularJurosLancamento(lcf, acaoFiscal, diaVencimentoPorAno);
    }

    public Calendar getDataVencimentoAcrescimo(LancamentoContabilFiscal lcf) {
        return autoInfracaoFiscalFacade.getAcaoFiscalFacade().getDataVencimentoAcrescimo(lcf, diaVencimentoPorAno);
    }

    public void carregaDiasPorAno(LancamentoContabilFiscal lcf) {
        autoInfracaoFiscalFacade.getAcaoFiscalFacade().carregaDiasPorAno(lcf, diaVencimentoPorAno);
    }

    public BigDecimal retornaValorTotalJurosMoraLancamento(Integer ano, RegistroLancamentoContabil reg) {
        return autoInfracaoFiscalFacade.getAcaoFiscalFacade().retornarValorTotalJurosMoraLancamento(ano, reg, acaoFiscal);
    }

    public BigDecimal calculaMultaLancamento(LancamentoContabilFiscal lcf) {
        return autoInfracaoFiscalFacade.getAcaoFiscalFacade().calcularMultaLancamento(lcf, acaoFiscal, diaVencimentoPorAno);
    }

    public BigDecimal retornaValorTotalMultaMoraLancamento(Integer ano, RegistroLancamentoContabil reg) {
        return autoInfracaoFiscalFacade.getAcaoFiscalFacade().retornarValorTotalMultaMoraLancamento(ano, reg, acaoFiscal);
    }

    public BigDecimal calculaTotalLancamento(LancamentoContabilFiscal lcf) {
        return autoInfracaoFiscalFacade.getAcaoFiscalFacade().calcularTotalLancamento(lcf);
    }

    public BigDecimal retornaValorTotalTotalLancamento(Integer ano, RegistroLancamentoContabil reg) {
        return autoInfracaoFiscalFacade.getAcaoFiscalFacade().retornarValorTotalTotalLancamento(ano, reg, acaoFiscal);
    }

    public RegistroLancamentoContabil getRegistroLancamentoContabil() {
        return registroLancamentoContabil;
    }

    public void setRegistroLancamentoContabil(RegistroLancamentoContabil registroLancamentoContabil) {
        this.registroLancamentoContabil = registroLancamentoContabil;
    }

    public BigDecimal retornaValorTotalMulta() {
        return autoInfracaoFiscalFacade.getAcaoFiscalFacade().retornarValorTotalMulta(registroLancamentoContabil);
    }

    public BigDecimal retornaValorTotalMultaIndice() {
        return autoInfracaoFiscalFacade.getAcaoFiscalFacade().retornarValorTotalMultaIndice(registroLancamentoContabil);
    }

    public String montarReferenciaMulta(LancamentoMultaFiscal lancamentoMultaFiscal) {
        return autoInfracaoFiscalFacade.getAcaoFiscalFacade().montarReferenciaMulta(lancamentoMultaFiscal);
    }

    public BigDecimal getValorTotalIss() {
        BigDecimal total = BigDecimal.ZERO;
        for (Integer ano : registroLancamentoContabil.getLancamentosPorAno().keySet()) {
            total = total.add(retornaValorTotalIssDevidoLancamento(ano, registroLancamentoContabil));
        }
        return total;
    }

    public void gerarAutoInfracao() {
        try {
            imprimirDocumento();
            selecionado.setDoctoAcaoFiscal(autoInfracaoFiscalFacade.salvarDoctoAcaoFiscal(selecionado.getDoctoAcaoFiscal()));
            RequestContext.getCurrentInstance().execute("imprimeAutoInfracao.hide()");
            FacesUtil.atualizarComponente("Formulario");
        } catch (UFMException e) {
            logger.error("Erro ao emitir o Auto de Infração! {}", e);
            FacesUtil.addError("Erro ao emitir o Auto de Infração!", e.getMessage());
        } catch (AtributosNulosException e) {
            logger.error("Erro ao emitir o Auto de Infração! {}", e);
            FacesUtil.addError("Erro ao emitir o Auto de Infração!", e.getMessage());
        }

    }

    public void imprimirDocumento() throws UFMException, AtributosNulosException {
        if (selecionado.getDoctoAcaoFiscal().getDocumentoOficial() == null) {
            TipoDoctoOficial tipo = autoInfracaoFiscalFacade.getParametroFiscalizacaoFacade().recuperarTipoDoctoAutoInfracaoPorExercicio(getSistemaControlador().getExercicioCorrente());

            CadastroEconomico cadastroEconomico = selecionado.getRegistro().getAcaoFiscal() != null ?
                selecionado.getRegistro().getAcaoFiscal().getCadastroEconomico() : selecionado.getCadastroEconomico();

            DocumentoOficial novoDocumentoOficial = autoInfracaoFiscalFacade.getDocumentoOficialFacade().geraDocumentoAutoInfracao(selecionado,
                selecionado.getDoctoAcaoFiscal().getDocumentoOficial(), cadastroEconomico,
                null, tipo, getSistemaControlador(),
                autoInfracaoFiscalFacade.getParametroFiscalizacaoFacade().recuperarParametroFiscalizacao(getSistemaControlador().getExercicioCorrente()));

            novoDocumentoOficial = autoInfracaoFiscalFacade.getDocumentoOficialFacade().salvarRetornando(novoDocumentoOficial);

            selecionado.getDoctoAcaoFiscal().setDocumentoOficial(novoDocumentoOficial);
        }
        autoInfracaoFiscalFacade.getDocumentoOficialFacade().emiteDocumentoOficial(selecionado.getDoctoAcaoFiscal().getDocumentoOficial());
    }

    public List<MultaFiscalizacao> completarMulta(String parte) {
        return autoInfracaoFiscalFacade.buscarMultasPorIncidenciaAndTipoMultaAndTipoCalculoAndFormaCalculo(parte.trim());
    }

    public LancamentoMultaFiscal getLancamentoMultaFiscal() {
        return lancamentoMultaFiscal;
    }

    public void setLancamentoMultaFiscal(LancamentoMultaFiscal lancamentoMultaFiscal) {
        this.lancamentoMultaFiscal = lancamentoMultaFiscal;
    }

    public List<LancamentoMultaFiscal> getMultasALancar() {
        return multasALancar;
    }

    public void setMultasALancar(List<LancamentoMultaFiscal> multasALancar) {
        this.multasALancar = multasALancar;
    }

    public void pegarDescricaoDaMulta(SelectEvent event) {
        MultaFiscalizacao m = (MultaFiscalizacao) event.getObject();
        selecionarMulta(m);
    }

    private void selecionarMulta(MultaFiscalizacao m) {
        multasALancar = Lists.newArrayList();
        lancamentosMultasFiscais = Lists.newArrayList();
        lancamentoMultaFiscal.setDescricao(m.getEmbasamento());
        lancamentoMultaFiscal.setQuantidade(1);
    }

    public void calcularMulta() {
        try {
            if(exercicio != null) {
                lancamentoMultaFiscal.setAno(exercicio.getAno());
            }
            validarCamposMulta();
            lancamentosMultasFiscais.clear();
            multasALancar.clear();
            lancarMulta();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void lancarMulta() {
        List<Mes> mesesNaoAdicionados = Lists.newArrayList();

        LancamentoMultaFiscal lancamento = new LancamentoMultaFiscal();
        BigDecimal baseCalculo = getBaseCalculoPorAno(lancamentoMultaFiscal.getAno(), lancamentoMultaFiscal.getMultaFiscalizacao().getBaseCalculo());
        BigDecimal valorUfm = lancamentoMultaFiscal.getMultaFiscalizacao().getValorMulta();
        BigDecimal valorMulta = converterIndiceParaReal(valorUfm, autoInfracaoFiscalFacade.buscarValorUFMParaAno(selecionado.getAno()));
        criarLancamentoDeMulta(lancamento, valorUfm, valorMulta, baseCalculo, mesesNaoAdicionados);

        try {
            validarMesesNaoAdicionados(mesesNaoAdicionados);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public BigDecimal converterIndiceParaReal(BigDecimal valor, BigDecimal indice) {
        BigDecimal retorno = BigDecimal.ZERO;
        if (valor != null && indice != null && indice != BigDecimal.ZERO) {
            retorno = valor.multiply(indice);
        }
        return retorno;
    }

    private void validarMesesNaoAdicionados(List<Mes> mesesNaoAdicionados) {
        ValidacaoException ve = new ValidacaoException();
        for (Mes mes : mesesNaoAdicionados) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Em " + (mes != null ? mes.getDescricao() + " de " : "") +
                lancamentoMultaFiscal.getAno() + " não foi encontrado valor de lançamento para o cálculo da multa.");
        }
        ve.lancarException();
    }

    private void criarLancamentoDeMulta(LancamentoMultaFiscal lancamento,
                                        BigDecimal valorUfm, BigDecimal valorMulta, BigDecimal baseCalculo, List<Mes> mesesNaoAdicionados) {
        if (multasALancar == null) {
            multasALancar = Lists.newArrayList();
        }

        if (valorMulta.compareTo(BigDecimal.ZERO) > 0) {
            lancamento.setValorMulta(valorMulta);
            lancamento.setValorMultaIndice(valorUfm);
            lancamento.setBaseCalculo(baseCalculo);
            lancamento.setAliquota(lancamentoMultaFiscal.getMultaFiscalizacao().getAliquotaMulta());
            lancamento.setAno(lancamentoMultaFiscal.getAno());
            lancamento.setDescricao(lancamentoMultaFiscal.getDescricao());
            lancamento.setQuantidade(lancamentoMultaFiscal.getQuantidade());
            lancamento.setObservacao(lancamentoMultaFiscal.getObservacao());
            lancamento.setMultaFiscalizacao(lancamentoMultaFiscal.getMultaFiscalizacao());
            lancamento.setRegistroLancamentoContabil(lancamentoMultaFiscal.getRegistroLancamentoContabil());
            lancamento.setNumeroLancamento(lancamentoMultaFiscal.getNumeroLancamento());
            lancamentosMultasFiscais.add(lancamento);
            multasALancar.add(lancamento);
        } else {
            mesesNaoAdicionados.add(lancamento.getMes());
        }
    }

    private BigDecimal getBaseCalculoPorAno(Integer ano, MultaFiscalizacao.BaseCalculo tipoBaseCalculo) {
        BigDecimal baseCalculo = BigDecimal.ZERO;
        for (LancamentoMultaFiscal multaFiscal : registroLancamentoContabil.getMultas()) {
            if (multaFiscal.getAno().equals(ano)) {
                baseCalculo = baseCalculo.add(multaFiscal.getValorMulta());
            }
        }
        return baseCalculo;
    }


    private void validarCamposMulta() {
        ValidacaoException ve = new ValidacaoException();

        if (lancamentoMultaFiscal.getMultaFiscalizacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Não é possível calcular, informe a multa.");
        } else if (lancamentoMultaFiscal.getMultaFiscalizacao().getTipoCalculoMultaFiscalizacao() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível calcular, pois não foi associado um tipo de cálculo a essa multa, verifique o cadastro desta multa.");
        }
        ve.lancarException();
    }

    public BigDecimal getValorMulta() {
        BigDecimal multa = BigDecimal.ZERO;
        for (LancamentoMultaFiscal lancamento : lancamentosMultasFiscais) {
            multa = multa.add(lancamento.getValorMulta());
        }
        return multa;
    }

    public BigDecimal getValorMultaUFM() {
        BigDecimal multa = BigDecimal.ZERO;
        for (LancamentoMultaFiscal lancamento : lancamentosMultasFiscais) {
            multa = multa.add(lancamento.getValorMultaIndice());
        }
        return multa;
    }

    public void podeLancarMulta() {
        try {
            validarAdicionarMulta();
            FacesUtil.executaJavaScript("confirmaMulta.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarAdicionarMulta() {
        ValidacaoException ve = new ValidacaoException();
        if (lancamentoMultaFiscal.getValorMulta() == null && lancamentoMultaFiscal.getValorMultaIndice() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível adicionar a multa pois não foi calculada. Clique em calcular antes de adicioná-la");
        } else if (getValorMulta().compareTo(BigDecimal.ZERO) <= 0 && getValorMultaUFM().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível adicionar multa que apresenta valores zerados ou abaixo de zero.");
        }
        ve.lancarException();
    }

    public void limparCamposMulta() {
        lancamentosMultasFiscais = Lists.newArrayList();
        multasALancar = Lists.newArrayList();
        lancamentoMultaFiscal = new LancamentoMultaFiscal();
        lancamentoMultaFiscal.setRegistroLancamentoContabil(registroLancamentoContabil);
    }

    public void adicionarMultaContinua() {
        try {
            validarAdicionarMulta();
            for (LancamentoMultaFiscal lancamento : lancamentosMultasFiscais) {
                if (lancamento != null) {
                    lancamento.setRegistroLancamentoContabil(registroLancamentoContabil);
                    registroLancamentoContabil.getMultas().add(lancamento);
                }
            }
            FacesUtil.addOperacaoRealizada("As Multas foram lançadas e associadas ao Registro " + registroLancamentoContabil.getNumeroAno());
            limparCamposMulta();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarMulta() {
        try {
            validarAdicionarMulta();
            for (LancamentoMultaFiscal lancamento : lancamentosMultasFiscais) {
                if (lancamento != null) {
                    lancamento.setRegistroLancamentoContabil(registroLancamentoContabil);
                    registroLancamentoContabil.getMultas().add(lancamento);
                }
            }
            FacesUtil.addOperacaoRealizada("As Multas foram lançadas e associadas ao Registro " + registroLancamentoContabil.getNumeroAno());
            limparCamposMulta();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void prepararImpressaoAuto() {
        try {
            validarImpressaoAutoInfracaoNovo();
            abrirDialogImpressaoAuto();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarImpressaoAutoInfracaoNovo() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCadastroEconomico() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Cadastro econômico");
        }
        if (registroLancamentoContabil.getMultas().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível gerar um novo auto de infração, verifique se possui multas lançadas!");
        }
        ve.lancarException();
    }

    public void abrirDialogImpressaoAuto() {
        if (selecionado != null) {
            fundamentacaoAutoInfracao = selecionado.getFundamentacao();
            historicoAutoInfracao = selecionado.getHistoricoFiscal();
        }
        FacesUtil.executaJavaScript("imprimeAutoInfracao.show()");
    }

    public void gerarAutoInfracaoNovo() {
        try {
            selecionado.setFundamentacao(fundamentacaoAutoInfracao);
            selecionado.setHistoricoFiscal(historicoAutoInfracao);

            DoctoAcaoFiscal docto = new DoctoAcaoFiscal();
            docto.setTipoDoctoAcaoFiscal(TipoDoctoAcaoFiscal.AUTOINFRACAO);
            docto.setAtivo(true);
            selecionado.setDoctoAcaoFiscal(docto);

            imprimirDocumento();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        } finally {
            RequestContext.getCurrentInstance().execute("imprimeAutoInfracao.hide()");
            FacesUtil.atualizarComponente("Formulario");
        }
    }

    public String getFundamentacaoAutoInfracao() {
        return fundamentacaoAutoInfracao;
    }

    public void setFundamentacaoAutoInfracao(String fundamentacaoAutoInfracao) {
        this.fundamentacaoAutoInfracao = fundamentacaoAutoInfracao;
    }

    public String getHistoricoAutoInfracao() {
        return historicoAutoInfracao;
    }

    public void setHistoricoAutoInfracao(String historicoAutoInfracao) {
        this.historicoAutoInfracao = historicoAutoInfracao;
    }

    public boolean isAutoInfracaoManual() {
        return selecionado.getRegistro().getAcaoFiscal() == null;
    }

    public void excluirMultaLancada(LancamentoMultaFiscal lancamentoMultaFiscal) {
        registroLancamentoContabil.getMultas().remove(lancamentoMultaFiscal);
    }
}
