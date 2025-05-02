/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

/**
 * @author peixe
 */
/*
 * Codigo gerado automaticamente em Sat Jul 02 10:03:07 BRT 2011
 * Gerador de Controlador
 */

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.PropositoAtoLegal;
import br.com.webpublico.enums.TipoPromocao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author peixe
 */
@ManagedBean(name = "promocaoController")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoPromocao", pattern = "/promocao/novo/", viewId = "/faces/rh/administracaodepagamento/promocao/edita.xhtml"),
    @URLMapping(id = "editarPromocao", pattern = "/promocao/editar/#{promocaoController.id}/", viewId = "/faces/rh/administracaodepagamento/promocao/edita.xhtml"),
    @URLMapping(id = "listarPromocao", pattern = "/promocao/listar/", viewId = "/faces/rh/administracaodepagamento/promocao/lista.xhtml"),
    @URLMapping(id = "verPromocao", pattern = "/promocao/ver/#{promocaoController.id}/", viewId = "/faces/rh/administracaodepagamento/promocao/visualizar.xhtml")
})
public class PromocaoController extends PrettyControlador<Promocao> implements Serializable, CRUD {

    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterCategoria, converterProgressao;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;
    private ConverterAutoComplete converterContratoServidor;
    private EnquadramentoFuncional enquadramentoFuncionalVigente;
    private MoneyConverter moneyConverter;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    private AtoLegal atoLegal;
    private ConverterAutoComplete converterAtoLegal;
    private List<CategoriaPCS> proximasCategorias;
    private List<ProgressaoPCS> proximasProgressoes;
    private CategoriaPCS categoriaPCSAtual;
    @EJB
    private PromocaoFacade promocaoFacade;
    @EJB
    private ProgressaoPCSFacade progressaoPCSFacade;
    @EJB
    private FaltasFacade faltasFacade;
    @EJB
    private PenalidadeFPFacade penalidadeFPFacade;
    @EJB
    private AfastamentoFacade afastamentoFacade;

    public PromocaoController() {
        super(Promocao.class);
    }

    public CategoriaPCS getCategoriaPCSAtual() {
        return categoriaPCSAtual;
    }

    @Override
    @URLAction(mappingId = "novoPromocao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        enquadramentoFuncionalVigente = null;
        atoLegal = null;
        proximasProgressoes = new ArrayList<ProgressaoPCS>();
        selecionado.setDataPromocao(UtilRH.getDataOperacao());
        atoLegal = null;
    }

    public ConverterAutoComplete getConverterCategoria() {
        if (converterCategoria == null) {
            converterCategoria = new ConverterAutoComplete(CategoriaPCS.class, categoriaPCSFacade);
        }
        return converterCategoria;
    }

    public ConverterAutoComplete getConverterProgressao() {
        if (converterProgressao == null) {
            converterProgressao = new ConverterAutoComplete(ProgressaoPCS.class, progressaoPCSFacade);
        }
        return converterProgressao;
    }

    @Override
    @URLAction(mappingId = "verPromocao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        selecionar();
    }

    @Override
    @URLAction(mappingId = "editarPromocao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        selecionar();
    }

    public void selecionar() {
        proximasProgressoes = new ArrayList<ProgressaoPCS>();
        selecionado = promocaoFacade.recuperar(this.getId());
        selecionado.setContratoFP(((Promocao) selecionado).getEnquadramentoAnterior().getContratoServidor());
        this.enquadramentoFuncionalVigente = ((Promocao) selecionado).getEnquadramentoAnterior();
        this.atoLegal = ((Promocao) selecionado).getEnquadramentoNovo().getProvimentoFP().getAtoLegal();
    }

    @Override
    public void salvar() {
        try {
            validarPromocao();
            validarAfastamento();
            if (isOperacaoNovo()) {
                promocaoFacade.salvarNovo(selecionado, atoLegal, enquadramentoFuncionalVigente, selecionado.getEnquadramentoNovo());
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ex) {
            if (ex.getMessage() != null) {
                FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            } else {
                FacesUtil.printAllFacesMessages(ex.getMensagens());
            }
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    @Override
    public AbstractFacade getFacede() {
        return promocaoFacade;
    }

    public void validarPromocao() {
        ValidacaoException ve = new ValidacaoException();
        EnquadramentoFuncional efNovo = getEnquadramentoFuncionalComListasRecuperadas();

        validarCampoObrigatorioServidor(ve);
        validarCampoObrigatorioAtoLegal(ve);
        validarCampoObrigatorioInicioVigenciaNovoEnquadramentoFuncional(ve, efNovo);
        validarCampoObrigatorioTipoPromocaoNovoEnquadramentoFuncional(ve, efNovo);
        validarInicioVigenciaNovoEnquadramentoFuncionalReferenteAInicioVigenciaEnquadramentoAtual(ve, efNovo);
        validarPeriodoVigenciaNovoEnquadramentoFuncional(ve, efNovo);
        validarCategoriaPCSNovoEnquadramentoFuncional(ve, efNovo);
        validarDireitoDoServidorParaPromocao(ve);
        validarCategoriaPCS(ve);
        lancarExcecao(ve);
    }

    public EnquadramentoFuncional getEnquadramentoFuncionalComListasRecuperadas() {
        if (selecionado.temEnquadramentoNovo()) {
            EnquadramentoFuncional efNovo = selecionado.getEnquadramentoNovo();
            efNovo.setCategoriaPCS(categoriaPCSFacade.recuperar(selecionado.getEnquadramentoNovo().getCategoriaPCS().getId()));
            return efNovo;
        }
        return null;
    }

    public void validarDireitoDoServidorParaPromocao(ValidacaoException ve) {
        if (penalidadeFPFacade.recuperaTotalDiasPenalidadePorContratoVigencia(selecionado.getContratoFP(), enquadramentoFuncionalVigente.getInicioVigencia(), DataUtil.adicionarMeses(enquadramentoFuncionalVigente.getInicioVigencia(), 48)) > 0) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O servidor não tem direito a promoção, pois teve punição disciplinar!");
            adiciona4AnosDataValidaPromocaoPorMotivoPunicao();
            enquadramentoFuncionalFacade.salvar(enquadramentoFuncionalVigente);
            ve.adicionarMensagemDeOperacaoNaoRealizada("O servidor só terá direito a requerer nova promoção a partir do dia " + DataUtil.getDataFormatada(enquadramentoFuncionalVigente.getDataValidaPromocao()));
            FacesUtil.redirecionamentoInterno("/promocao/listar/");
        }
    }

    public void validarCategoriaPCSNovoEnquadramentoFuncional(ValidacaoException ve, EnquadramentoFuncional efNovo) {
        if (efNovo.getCategoriaPCS().getId().compareTo(enquadramentoFuncionalVigente.getCategoriaPCS().getId()) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Escolha uma categoria pcs posterior à categoria atual!");
        }
    }

    public void validarPeriodoVigenciaNovoEnquadramentoFuncional(ValidacaoException ve, EnquadramentoFuncional efNovo) {
        if (efNovo.getFinalVigencia() != null) {
            if (efNovo.getFinalVigencia().before(efNovo.getInicioVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoRealizada("O fim da vigência deve ser posterior ao início da vigência!");
            }
        }
    }

    public void validarInicioVigenciaNovoEnquadramentoFuncionalReferenteAInicioVigenciaEnquadramentoAtual(ValidacaoException ve, EnquadramentoFuncional efNovo) {
        if (efNovo.getInicioVigencia().before(selecionado.getEnquadramentoAnterior().getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O início de vigência deve ser posterior ao início de vigência do enquadramento anterior!");
        }
    }

    public void validarCampoObrigatorioTipoPromocaoNovoEnquadramentoFuncional(ValidacaoException ve, EnquadramentoFuncional efNovo) {
        if (efNovo.getTipoPromocao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de promoção é obrigatório!");
        }
    }

    public void validarCampoObrigatorioInicioVigenciaNovoEnquadramentoFuncional(ValidacaoException ve, EnquadramentoFuncional efNovo) {
        if (efNovo.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo início de vigência é obrigatório!");
        }
    }

    public void validarCampoObrigatorioAtoLegal(ValidacaoException ve) {
        if (atoLegal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo ato legal é obrigatório!");
        }
    }

    public void validarCampoObrigatorioServidor(ValidacaoException ve) {
        if (selecionado.getContratoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo servidor é obrigatório!");
            throw ve;
        }
    }

    public void adiciona4AnosDataValidaPromocaoPorMotivoPunicao() {
        enquadramentoFuncionalVigente.setDataValidaPromocao(DataUtil.adicionarMeses(penalidadeFPFacade.recuperaFinalVigenciaPenalidadePorContratoVigencia(
            selecionado.getContratoFP(), enquadramentoFuncionalVigente.getInicioVigencia(),
            DataUtil.adicionarMeses(enquadramentoFuncionalVigente.getInicioVigencia(), 48)), 48));
    }

    public String getProximaCategoria() {
        if (((Promocao) selecionado).getEnquadramentoNovo().getCategoriaPCS() != null) {
            int indice = descobreIndice(getCategoriaPCS(), ((Promocao) selecionado).getEnquadramentoNovo().getCategoriaPCS());
            if (proximasCategorias.isEmpty()) {
                return "Não Disponível";
            }
            try {
                return proximasCategorias.get(indice + 1).getEstruturaCategoriaPCS();
            } catch (IndexOutOfBoundsException e) {
                return "O servidor já atingiu o limite máximo de promoção para este PCS";
            }

        }
        return "";
    }

    public int descobreIndice(List<SelectItem> lista, CategoriaPCS atual) {
        for (SelectItem c : lista) {
            if (c.getLabel().equals(atual.getEstruturaCategoriaPCS())) {
                return lista.indexOf(c);
            }
        }
        return 0;
    }

    public List<SelectItem> getCategoriaPCS() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        try {
            instanciarListaProximasCategorias();
            for (CategoriaPCS object : categoriaPCSFacade.getFilhosDe(selecionado.getEnquadramentoNovo().getCategoriaPCS().getSuperior())) {
                CategoriaPCS cat = new CategoriaPCS();
                String nome = "";
                cat = categoriaPCSFacade.recuperar(object.getId());
                nome = cat.getDescricao();
                //a verificação do equals é para quando acontece de um superior ser o proprio objeto.
                //ai cai em loop infinito
                if (cat.getFilhos().isEmpty()) {
                    while ((cat.getSuperior() != null) && (!cat.equals(cat.getSuperior()))) {
                        cat = cat.getSuperior();
                        nome = cat.getDescricao() + "/" + nome;
                    }
                    proximasCategorias.add(object);
                    toReturn.add(new SelectItem(object, nome));
                }
            }
        } catch (Exception ex) {
            logger.debug("Erro ao buscar categorias pcs: " + ex.getMessage());
        }
        return toReturn;
    }

    public void instanciarListaProximasCategorias() {
        proximasCategorias = new ArrayList<>();
    }

    public List<SelectItem> getPromocoes() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
//        toReturn.add(new SelectItem(null, ""));
        for (TipoPromocao object : TipoPromocao.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public Converter getConverterContratoServidor() {
        if (converterContratoServidor == null) {
            converterContratoServidor = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoServidor;
    }

    public List<ContratoFP> completaContratoServidor(String parte) {
        return contratoFPFacade.recuperaContratoEstatutarioComEnquadramento(parte.trim());
    }

    public void processarEnquadramentosPorServidor() {
        enquadramentoFuncionalVigente = enquadramentoFuncionalFacade.recuperaEnquadramentoFuncionalVigentePorContratoServidor(selecionado.getContratoFP(), null);
        EnquadramentoFuncional ef = (EnquadramentoFuncional) Util.clonarObjeto(enquadramentoFuncionalVigente);
        try {
            ef.setId(null);
            selecionado.setEnquadramentoNovo(ef);
            selecionado.getEnquadramentoNovo().setInicioVigencia(UtilRH.getDataOperacao());
            selecionado.getEnquadramentoNovo().setDataCadastro(UtilRH.getDataOperacao());
            selecionado.getEnquadramentoNovo().setProgressaoPCS(progressaoPCSFacade.recuperar(enquadramentoFuncionalVigente.getProgressaoPCS().getId()));
            selecionado.setEnquadramentoAnterior(enquadramentoFuncionalVigente);
            validarAfastamento();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (NullPointerException npe) {

        }
    }

    private void validarAfastamento() {
        ValidacaoException ve = new ValidacaoException();
        List<Afastamento> afastamentos = afastamentoFacade.buscarAfastamentoVigentePorContratoAndNaoPermitirPromocao(selecionado.getContratoFP(), selecionado.getEnquadramentoNovo().getInicioVigencia());
        if (!afastamentos.isEmpty()) {
            for (Afastamento afastamento : afastamentos) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O servidor selecionado tem afastamento '" + afastamento.getTipoAfastamento().getDescricao() + " (Início: " + DataUtil.getDataFormatada(afastamento.getInicio())
                    + ", Término: " + DataUtil.getDataFormatada(afastamento.getTermino()) + ", " + (afastamento.getRetornoInformado() ? "Com Retorno" : "Sem Retorno") + ")' que não permite Promoção.");
            }
        }
        ve.lancarException();
    }

    public EnquadramentoFuncional getEnquadramentoFuncionalVigente() {
        return enquadramentoFuncionalVigente;
    }

    public void setEnquadramentoFuncionalVigente(EnquadramentoFuncional enquadramentoFuncionalVigente) {
        this.enquadramentoFuncionalVigente = enquadramentoFuncionalVigente;
    }

    @Override
    public void cancelar() {
        super.cancelar();
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoParteEPropositoAtoLegal(parte, PropositoAtoLegal.ATO_DE_PESSOAL, "numero", "nome");
    }

    public ConverterAutoComplete getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public BigDecimal getValorVigente() {
        if (enquadramentoFuncionalVigente.getCategoriaPCS() != null) {
            EnquadramentoPCS valor = enquadramentoPCSFacade.recuperaUltimoValor(enquadramentoFuncionalVigente.getCategoriaPCS(), enquadramentoFuncionalVigente.getProgressaoPCS(), null, null);
            //EnquadramentoPCS valor = enquadramentoPCSFacade.filtraEnquadramentoVigente(selecionado.getEnquadramentoNovo().getProgressaoPCS(), selecionado.getEnquadramentoNovo().getCategoriaPCS());
            if (valor != null) {
                return valor.getVencimentoBase();
            }
        }
        return new BigDecimal(BigInteger.ZERO);
    }

    public BigDecimal getValorNovo() {
        if (((Promocao) selecionado).getEnquadramentoNovo().getCategoriaPCS() != null && ((Promocao) selecionado).getEnquadramentoNovo().getProgressaoPCS() != null) {
            //EnquadramentoPCS valor = enquadramentoPCSFacade.recuperaUltimoValor(((Promocao) selecionado).getEnquadramentoNovo().getCategoriaPCS(), ((Promocao) selecionado).getEnquadramentoNovo().getProgressaoPCS(), null, null);
            EnquadramentoPCS valor = enquadramentoPCSFacade.filtraEnquadramentoVigente(selecionado.getEnquadramentoNovo().getProgressaoPCS(), selecionado.getEnquadramentoNovo().getCategoriaPCS());
            if (valor != null) {
                return valor.getVencimentoBase();
            }
        }
        return new BigDecimal(BigInteger.ZERO);
    }

    public List<ProgressaoPCS> getProximasProgressoes() {
        return proximasProgressoes;
    }

    public List<SelectItem> getItensProximasProgressoes() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (ProgressaoPCS p : proximasProgressoes) {
            toReturn.add(new SelectItem(p, "" + p));
        }
        return toReturn;
    }

    public void progressoesSugeridas() {
        try {
            proximasProgressoes = new ArrayList<ProgressaoPCS>();
            proximasProgressoes = progressaoPCSFacade.getRaizPorPlanoECategoria(selecionado.getEnquadramentoNovo().getCategoriaPCS().getPlanoCargosSalarios(), selecionado.getEnquadramentoNovo().getCategoriaPCS());
            if (!selecionado.getEnquadramentoNovo().getCategoriaPCS().equals(selecionado.getEnquadramentoAnterior().getCategoriaPCS())) {
                if (proximasProgressoes != null && !proximasProgressoes.isEmpty()) {
                    selecionado.getEnquadramentoNovo().getProgressaoPCS().setSuperior(proximasProgressoes.get(0));
                    setaNovaProgressao();
                }
            } else {
                setaProgressaoAnterior();
            }
        } catch (Exception ex) {
            logger.debug("Erro no método progressoesSugeridas - PromocaoController " + ex.getMessage());
        } finally {
            getValorNovo();
        }
    }

    private void validarCategoriaPCS(ValidacaoException ve) {
        validarConfiguracaoDeMesesParaPromocaoPorPlanoCargosSalarios(selecionado.getEnquadramentoNovo().getCategoriaPCS().getPlanoCargosSalarios(), ve);
        lancarExcecao(ve);
        validarTempoPermitidoParaPromocao(ve);
        lancarExcecao(ve);
    }

    private void validarTempoPermitidoParaPromocao(ValidacaoException ve) {
        Date inicioVigenciaNovoEnquadramentoFuncional = selecionado.getEnquadramentoNovo().getInicioVigencia();
        MesesPromocao mesesPromocaoVigentePorPCS = promocaoFacade.getPlanoCargosSalariosFacade().buscarMesesParaPromocaoVigenteDoPlano(selecionado.getEnquadramentoNovo().getCategoriaPCS().getPlanoCargosSalarios(), inicioVigenciaNovoEnquadramentoFuncional);
        Integer mesesMinimoParaPromocao = mesesPromocaoVigentePorPCS.getMeses();
        Promocao promocaoPorContrato = promocaoFacade.buscarPromocaoPorContrato(selecionado.getContratoFP());
        Date dataMinimaConfiguradaParaPromocao;
        if (isPrimeiraPromocaoDoContratoFP(promocaoPorContrato)) {
            Date dataNomeacaoServidor = selecionado.getContratoFP().getDataNomeacao();
            dataMinimaConfiguradaParaPromocao = DataUtil.adicionarMeses(dataNomeacaoServidor, mesesMinimoParaPromocao);
        } else {
            dataMinimaConfiguradaParaPromocao = DataUtil.adicionarMeses(promocaoPorContrato.getDataPromocao(), mesesMinimoParaPromocao);
        }
        verificarDatasInicioVigenciaNovoEnquadramentoFuncionalAndDataMinimaConfiguradaParaPromocao(ve, inicioVigenciaNovoEnquadramentoFuncional, mesesPromocaoVigentePorPCS, dataMinimaConfiguradaParaPromocao);
    }

    private void verificarDatasInicioVigenciaNovoEnquadramentoFuncionalAndDataMinimaConfiguradaParaPromocao(ValidacaoException ve, Date inicioVigenciaNovoEnquadramentoFuncional, MesesPromocao mesesPromocaoVigentePorPCS, Date dataMinimaConfiguradaParaPromocao) {
        if (inicioVigenciaNovoEnquadramentoFuncional.before(dataMinimaConfiguradaParaPromocao)) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O servidor não atingiu o tempo mínimo de meses configurada no plano de cargos e salários " + selecionado.getEnquadramentoNovo().getCategoriaPCS().getPlanoCargosSalarios());
            ve.adicionarMensagemDeOperacaoNaoRealizada("Tempo mínimo para promoção: " + mesesPromocaoVigentePorPCS.toStringComAnos());
            ve.adicionarMensagemDeOperacaoNaoRealizada("Este servidor só poderá ter promoção a parir de " + DataUtil.getDataFormatada(dataMinimaConfiguradaParaPromocao));
            lancarExcecao(ve);
        }
    }

    private boolean isPrimeiraPromocaoDoContratoFP(Promocao promocao) {
        return promocao == null;
    }

    private void lancarExcecao(ValidacaoException ve) {
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private void validarConfiguracaoDeMesesParaPromocaoPorPlanoCargosSalarios(PlanoCargosSalarios planoCargosSalarios, ValidacaoException ve) {
        boolean pcsTemConfiguracaoDeMesesParaPromocao = planoCargosSalarios.temConfiguracaoDeMesesParaPromocao();
        if (!pcsTemConfiguracaoDeMesesParaPromocao) {
            String url = "<b><a href='" + FacesUtil.getRequestContextPath() + "/plano-cargos-salarios/editar/" + selecionado.getEnquadramentoNovo().getCategoriaPCS().getPlanoCargosSalarios().getId() + "' target='_blank'>Plano de Cargos e Salários</a></b>";
            ve.adicionarMensagemDeOperacaoNaoRealizada("Não foi localizado o parâmetro da quantidade de meses necessários para promoção.");
            ve.adicionarMensagemDeOperacaoNaoRealizada("Verifique as informações em: " + url);
        }
    }

    public void atualizarProgressao() {
        if (!selecionado.getEnquadramentoNovo().getCategoriaPCS().equals(selecionado.getEnquadramentoAnterior().getCategoriaPCS())) {
            setaNovaProgressao();
        } else {
            setaProgressaoAnterior();
        }
    }

    public void setaNovaProgressao() {
        ProgressaoPCS proximaProgressao = progressaoPCSFacade.recuperaProgressaoEquivalente(selecionado.getEnquadramentoNovo().getProgressaoPCS().getSuperior(), selecionado.getEnquadramentoNovo().getProgressaoPCS());
        if (proximaProgressao != null) {
            selecionado.getEnquadramentoNovo().setProgressaoPCS(proximaProgressao);
        }
    }

    public void setaProgressaoAnterior() {
        selecionado.getEnquadramentoNovo().getProgressaoPCS().setSuperior(selecionado.getEnquadramentoAnterior().getProgressaoPCS().getSuperior());
        selecionado.getEnquadramentoNovo().setProgressaoPCS(selecionado.getEnquadramentoAnterior().getProgressaoPCS());
    }

    public boolean bloquearComboProgressao() {
        boolean bloquear = false;
        if (proximasProgressoes.size() <= 1) {
            bloquear = true;
        }
        if (selecionado.getEnquadramentoNovo().getCategoriaPCS().equals(selecionado.getEnquadramentoAnterior().getCategoriaPCS())) {
            bloquear = true;
        }
        return bloquear;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/promocao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
