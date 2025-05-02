package br.com.webpublico.controle;

import br.com.webpublico.entidades.CalculoContrato;
import br.com.webpublico.entidades.Contrato;
import br.com.webpublico.entidades.DAM;
import br.com.webpublico.entidades.ProcessoCalculoContrato;
import br.com.webpublico.enums.SituacaoContrato;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ImprimeDAM;
import br.com.webpublico.negocios.LancamentoDebitoContratoFacade;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-lanc-debito-cont", pattern = "/lancamento-debito-contrato-concessao/novo/", viewId = "/faces/tributario/contrato-concessao/edita.xhtml"),
    @URLMapping(id = "ver-lanc-debito-cont", pattern = "/lancamento-debito-contrato-concessao/ver/#{lancamentoDebitoContratoControlador.id}/", viewId = "/faces/tributario/contrato-concessao/visualizar.xhtml"),
    @URLMapping(id = "listar-lanc-debito-cont", pattern = "/lancamento-debito-contrato-concessao/listar/", viewId = "/faces/tributario/contrato-concessao/lista.xhtml"),
})
public class LancamentoDebitoContratoControlador extends PrettyControlador<ProcessoCalculoContrato> implements CRUD {

    @EJB
    private LancamentoDebitoContratoFacade facade;
    private CalculoContrato calculoContrato;
    private BigDecimal valorLancamento;
    private List<ResultadoParcela> resultadoParcelas;
    private ConsultaDebitoControlador.TotalTabelaParcelas resultadoParcelaTotalizador;

    public LancamentoDebitoContratoControlador() {
        super(ProcessoCalculoContrato.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/lancamento-debito-contrato-concessao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-lanc-debito-cont", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setCalculos(Lists.<CalculoContrato>newArrayList());
        selecionado.setDataLancamento(new Date());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
    }

    @URLAction(mappingId = "ver-lanc-debito-cont", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        buscarParcelas();
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            selecionado = facade.gerarDebito(selecionado, valorLancamento);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Poblema ao salvar cálculo contrato concessão. ", e);
            FacesUtil.addOperacaoNaoRealizada("Poblema ao salvar cálculo contrato concessão. Detalhes: " + e.getMessage());
        }
    }

    public void gerarDam(ResultadoParcela parcela) {
        try {
            DAM dam = facade.getGeraValorDividaContrato().getDamFacade().buscarOuGerarDam(parcela);
            if (dam == null) {
                throw new ExcecaoNegocioGenerica("Não foi possível gerar o dam para a parcela referência: " + parcela.getReferencia() + ".");
            }
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.setGeraNoDialog(true);
            imprimeDAM.imprimirDamUnicoViaApi(dam);
        } catch (Exception e) {
            logger.error("Não foi possível gerar o dam para a parcela id {" + parcela.getIdParcela() + "}");
        }
    }


    public void buscarParcelas() {
        resultadoParcelas = facade.buscarParcelas(selecionado.getContrato());
        resultadoParcelaTotalizador = new ConsultaDebitoControlador.TotalTabelaParcelas();
        for (ResultadoParcela resultado : resultadoParcelas) {
            resultadoParcelaTotalizador.soma(resultado);
        }
    }

    public TipoCadastroTributario getTipoCadastroContribuinte() {
        return TipoCadastroTributario.PESSOA;
    }

    public void buscarDadosProcesso() {
        try {
            validarCamposObrigatorio();
            ProcessoCalculoContrato processoCalculoContrato = facade.buscarProcessoCalculoContratoPorExercicio(selecionado.getContrato(), selecionado.getExercicio());
            if (processoCalculoContrato != null) {
                selecionado = (ProcessoCalculoContrato) Util.clonarObjeto(processoCalculoContrato);
                buscarParcelas();
                FacesUtil.atualizarComponente("Formulario");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCamposObrigatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getContrato() == null)
            ve.adicionarMensagemDeCampoObrigatorio("O campo contrato deve ser informado.");
        if (selecionado.getExercicio() == null)
            ve.adicionarMensagemDeCampoObrigatorio("O campo exercício deve ser informado.");
        ve.lancarException();
    }

    private void validarSalvar() {
        ValidacaoException ve = new ValidacaoException();
        validarCamposObrigatorio();
        if (valorLancamento == null || valorLancamento.compareTo(BigDecimal.ZERO) == 0)
            ve.adicionarMensagemDeCampoObrigatorio("O campo do valor do laçamento deve ser informado com um valor maior que zero(0).");
        ve.lancarException();

        if (valorLancamento.compareTo(selecionado.getContrato().getSaldoAtualContrato()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo valor do lançamento deve menor ou igual ao saldo do contrato.");
        }
        validarDataVencimentoParcela(ve);
        ve.lancarException();
    }

    private void validarDataVencimentoParcela(ValidacaoException ve) {
        if (resultadoParcelas !=null && !resultadoParcelas.isEmpty()) {
            ResultadoParcela ultimaParcela = resultadoParcelas.get(resultadoParcelas.size() - 1);
            int mesVencimentoUltimaParcela = DataUtil.getMes(ultimaParcela.getVencimento());

            Date dataVencimentoNovaParcela = DataUtil.adicionaDias(selecionado.getDataLancamento(), 30);
            Date dataVenvimentoNovaParcelaDiaUtil = facade.getGeraValorDividaContrato().ajustaVencimento(dataVencimentoNovaParcela, Calendar.DAY_OF_MONTH, 1);
            int mesVencimentoNovaParcela = DataUtil.getMes(dataVenvimentoNovaParcelaDiaUtil);

            if (mesVencimentoNovaParcela == mesVencimentoUltimaParcela) {
                ve.adicionarMensagemDeCampoObrigatorio("Já foi gerado um débito com o vencimento para o mês de " + DataUtil.getDescricaoMes(mesVencimentoNovaParcela) + ".");
            }
        }
    }

    public Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    public List<Contrato> completarContrato(String parte) {
        return facade.getContratoFacade().buscarContratosConcessao(parte.trim(), SituacaoContrato.situacoesDiferenteEmElaboracao);
    }

    public CalculoContrato getCalculoContrato() {
        return calculoContrato;
    }

    public void setCalculoContrato(CalculoContrato calculoContrato) {
        this.calculoContrato = calculoContrato;
    }

    public BigDecimal getValorLancamento() {
        return valorLancamento;
    }

    public void setValorLancamento(BigDecimal valorLancamento) {
        this.valorLancamento = valorLancamento;
    }

    public List<ResultadoParcela> getResultadoParcelas() {
        return resultadoParcelas;
    }

    public void setResultadoParcelas(List<ResultadoParcela> resultadoParcelas) {
        this.resultadoParcelas = resultadoParcelas;
    }

    public ConsultaDebitoControlador.TotalTabelaParcelas getResultadoParcelaTotalizador() {
        return resultadoParcelaTotalizador;
    }

    public void setResultadoParcelaTotalizador(ConsultaDebitoControlador.TotalTabelaParcelas resultadoParcelaTotalizador) {
        this.resultadoParcelaTotalizador = resultadoParcelaTotalizador;
    }
}
