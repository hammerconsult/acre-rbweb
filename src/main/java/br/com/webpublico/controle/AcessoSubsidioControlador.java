/*
 * Codigo gerado automaticamente em Thu Oct 06 11:04:36 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.FiltroBaseFP;
import br.com.webpublico.enums.PropositoAtoLegal;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoPCS;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.hibernate.LazyInitializationException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ManagedBean(name = "acessoSubsidioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoAcessoSubsidio", pattern = "/acesso-subsidio/novo/", viewId = "/faces/rh/administracaodepagamento/acessosubsidio/edita.xhtml"),
    @URLMapping(id = "editarAcessoSubsidio", pattern = "/acesso-subsidio/editar/#{acessoSubsidioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/acessosubsidio/edita.xhtml"),
    @URLMapping(id = "listarAcessoSubsidio", pattern = "/acesso-subsidio/listar/", viewId = "/faces/rh/administracaodepagamento/acessosubsidio/lista.xhtml"),
    @URLMapping(id = "verAcessoSubsidio", pattern = "/acesso-subsidio/ver/#{acessoSubsidioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/acessosubsidio/visualizar.xhtml")
})
public class AcessoSubsidioControlador extends PrettyControlador<AcessoSubsidio> implements Serializable, CRUD {

    @EJB
    private AcessoSubsidioFacade acessoSubsidioFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private OpcaoSalarialCCFacade opcaoSalarialCCFacade;
    @EJB
    private AtoDePessoalFacade atoDePessoalFacade;
    @EJB
    private PlanoCargosSalariosFacade planoCargosSalariosFacade;
    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;
    @EJB
    private ProgressaoPCSFacade progressaoPCSFacade;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private CargoFacade cargoFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private BaseFPFacade baseFPFacade;

    private EnquadramentoCC enquadramentoCC;
    private List<PlanoCargosSalarios> planoCargosSalarios;
    private PlanoCargosSalarios planoCargoSalario;
    private CategoriaPCS categoriaPCS;
    private ProgressaoPCS progressaoPCS;
    private List<SelectItem> categoriasPCS;
    private EnquadramentoPCS enquadramentoPCS;
    private ItemBaseFP itemBaseFP;

    public AcessoSubsidioControlador() {
        super(AcessoSubsidio.class);
    }

    public List<SelectItem> getCategoriasPCS() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        if (planoCargoSalario != null && selecionado.getCargo() != null && enquadramentoCC.getInicioVigencia() != null) {
            for (CategoriaPCS object : categoriaPCSFacade.getFolhasPorPlanoCargo(planoCargoSalario, selecionado.getCargo(), enquadramentoCC.getInicioVigencia())) {
                toReturn.add(new SelectItem(object, object.toString()));
            }
        }
        return toReturn;
    }

    public void setCategoriasPCS(List<SelectItem> categoriasPCS) {
        this.categoriasPCS = categoriasPCS;
    }

    @Override
    public AbstractFacade getFacede() {
        return acessoSubsidioFacade;
    }

    public EnquadramentoPCS getEnquadramentoPCS() {
        return enquadramentoPCS;
    }

    public void setEnquadramentoPCS(EnquadramentoPCS enquadramentoPCS) {
        this.enquadramentoPCS = enquadramentoPCS;
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        return contratoFPFacade.recuperaContratoMatriculaSql(parte.trim());
    }

    public ItemBaseFP getItemBaseFP() {
        return itemBaseFP;
    }

    public void setItemBaseFP(ItemBaseFP itemBaseFP) {
        this.itemBaseFP = itemBaseFP;
    }

    public boolean validaContrato() {
        if ((selecionado).getContratoFP() != null) {
            if (!contratoFPFacade.verificaContratoEnquadramentoFuncional(selecionado.getContratoFP()) && !TipoRegime.ESTATUTARIO.equals(selecionado.getContratoFP().getTipoRegime().getCodigo())) {
                FacesContext.getCurrentInstance().addMessage("Formulario:contratoFP", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Problema", "O Acesso a Subsídio só pode ser feito para servidores concursados e do tipo de regime estatutário."));
                cancelaContratoFP();
                return false;
            }
            if (servidorEstaEmFuncaoGratificadaVigente()) {
                FacesUtil.addOperacaoNaoPermitida("Provimento não permitido para o servidor em acesso a função gratificada para o cadastro do provimento de acesso ao cargo e comissão se faz necessário o retorno ao cargo e carreira.");
                cancelaContratoFP();
                return false;
            }

            if (!acessoSubsidioFacade.buscarAcessoSubsidioPorContratoFP(selecionado.getContratoFP()).isEmpty()) {
                FacesUtil.addOperacaoNaoPermitida("O servidor informado já está vigente em um  Acesso a Subsídio. Para continuar, realize o retorno ao cargo e carreira do servidor informado.");
                cancelaContratoFP();
                return false;
            }
        }

        return true;

    }

    private void cancelaContratoFP() {
        selecionado.setContratoFP(null);
    }

    private boolean servidorEstaEmFuncaoGratificadaVigente() {
        return acessoSubsidioFacade.getFuncaoGratificadaFacade().recuperaFuncaoGratificada(selecionado.getContratoFP()) != null;
    }

    public ConverterAutoComplete getConverterContratoFP() {
        return new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
    }

    public List<SelectItem> getOpcaoSalarialCC() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (OpcaoSalarialCC object : opcaoSalarialCCFacade.listaAtivos()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterAutoComplete getConverterOpcaoSalarialCC() {
        return new ConverterAutoComplete(OpcaoSalarialCC.class, opcaoSalarialCCFacade);
    }

    public List<AtoLegal> completaAtoDePessoal(String parte) {
        return atoLegalFacade.listaFiltrandoAtoLegalPorPropositoAtoLegal(parte.trim(), PropositoAtoLegal.ATO_DE_PESSOAL);
    }

    public Converter getConverterAtoDePessoal() {
        return new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
    }

    public List<Cargo> completarCargos(String parte) {
        return cargoFacade.listaFiltrando(TipoPCS.CARGO_EM_COMISSAO, parte.trim());
    }

    public ConverterAutoComplete getConverterCargo() {
        return new ConverterAutoComplete(Cargo.class, cargoFacade);
    }

    @URLAction(mappingId = "novoAcessoSubsidio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        planoCargosSalarios = planoCargosSalariosFacade.getPlanoEmFuncaoGratificadaVigente(TipoPCS.ACESSO_SUBSIDIO);
        itemBaseFP = new ItemBaseFP();
    }

    @URLAction(mappingId = "verAcessoSubsidio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        selecionar();
    }

    @URLAction(mappingId = "editarAcessoSubsidio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionar();
        itemBaseFP = new ItemBaseFP();
    }

    public void selecionar() {
        progressaoPCS = null;
        enquadramentoPCS = null;
        planoCargosSalarios = planoCargosSalariosFacade.getPlanoEmFuncaoGratificadaVigente(TipoPCS.CARGO_EM_COMISSAO);
    }

    @Override
    public void salvar() {

        try {
            if (isOperacaoNovo()) {
                if (!validaContrato()) {
                    return;
                }
                contratoFPFacade.verificarParametrosControleCargoComissaoAndValores(selecionado.getContratoFP());
                acessoSubsidioFacade.validarValoresEntidade(selecionado.getContratoFP());
            }

            if (!validaEnquadramentoCC()) {
                return;
            }
            validarCampos();
            ProvimentoFP provimentoFP;
            if (selecionado.getProvimentoFP() == null) {
                TipoProvimento tipoProvimento = tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.PROVIMENTO_ACESSO_CARGO_COMISSAO);
                if (tipoProvimento == null) {
                    FacesUtil.addWarn("Atenção!", "Tipo de provimento para acesso a cargo em comissão não encontrado!");
                    return;
                }

                provimentoFP = new ProvimentoFP();
                provimentoFP.setTipoProvimento(tipoProvimento);
                provimentoFP.setSequencia(provimentoFPFacade.geraSequenciaPorVinculoFP(selecionado.getContratoFP()));
            } else {
                provimentoFP = provimentoFPFacade.recuperar(selecionado.getProvimentoFP().getId());
            }

            provimentoFP.setVinculoFP(selecionado.getContratoFP());
            provimentoFP.setDataProvimento(selecionado.getInicioVigencia());
            provimentoFP.setAtoLegal(selecionado.getAtoDePessoal());
            provimentoFP.setObservacao(selecionado.getDescricao());


            if (selecionado.getBaseFP() != null) {
                selecionado.setBaseFP(baseFPFacade.salvarRetornando(selecionado.getBaseFP()));
            }

            acessoSubsidioFacade.salvarAcessoSubsidio(selecionado, provimentoFP);
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    @Override
    public boolean validaRegrasParaExcluir() {
        if (acessoSubsidioFacade.getFichaFinanceiraFPFacade().existeFolhaProcessadaPorVinculoFPAndPeriodoDeVigencia(selecionado.getContratoFP(), selecionado.getInicioVigencia(), selecionado.getFinalVigencia())) {
            FacesUtil.addOperacaoNaoPermitida("Foi encontrado folha processada para este acesso a cargo de comissão no período de vigência. É impossível excluir este registro!");
            return false;
        }
        return true;
    }

    private boolean validaEnquadramentoCC() {
        if (selecionado.getEnquadramentoCCs().isEmpty()) {
            FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "Não é possível realizar o acesso de cargo de comissão sem adicionar um enquadramento de cargo comissão");
            return false;
        }
        return true;
    }

    public List<PlanoCargosSalarios> getPlanoCargosSalarios() {
        return planoCargosSalarios;
    }

    public void setPlanoCargosSalarios(List<PlanoCargosSalarios> planoCargosSalarios) {
        this.planoCargosSalarios = planoCargosSalarios;
    }

    public PlanoCargosSalarios getPlanoCargoSalario() {
        return planoCargoSalario;
    }

    public void setPlanoCargoSalario(PlanoCargosSalarios planoCargoSalario) {
        this.planoCargoSalario = planoCargoSalario;
    }

    public EnquadramentoCC getEnquadramentoCC() {
        return enquadramentoCC;
    }

    public void setEnquadramentoCC(EnquadramentoCC enquadramentoCC) {
        this.enquadramentoCC = enquadramentoCC;
    }

    public void limparDadosAtuaisDoEnquadramento() {
        enquadramentoCC = null;
    }

    public EnquadramentoPCS getEnquadramento() {
        if (enquadramentoCC != null) {
            if (enquadramentoCC.getCategoriaPCS() != null && enquadramentoCC.getProgressaoPCS() != null) {
                enquadramentoPCS = enquadramentoPCSFacade.recuperaValor(enquadramentoCC.getCategoriaPCS(), enquadramentoCC.getProgressaoPCS(), enquadramentoCC.getInicioVigencia());
            }
        }
        if (enquadramentoPCS == null) {
            enquadramentoPCS = new EnquadramentoPCS();
            enquadramentoPCS.setVencimentoBase(BigDecimal.ZERO);
        }
        return enquadramentoPCS;
    }

    public void setEnquadramento(EnquadramentoPCS enquadramentoPCS) {
        this.enquadramentoPCS = enquadramentoPCS;
    }

    public ProgressaoPCS getProgressaoPCS() {
        return progressaoPCS;
    }

    public void setProgressaoPCS(ProgressaoPCS progressaoPCS) {
        this.progressaoPCS = progressaoPCS;
    }

    public void cancelarEnquadramentoCC() {
        this.enquadramentoCC = null;
    }

    public void confirmarEnquadramentoCC() {
        try {
            validarCamposObrigatoriosEnquadramentoCC();
            if (!DataUtil.isVigenciaValida(enquadramentoCC, selecionado.getEnquadramentoCCs())) {
                return;
            }
            enquadramentoCC.setAcessoSubsidio(selecionado);
            enquadramentoCC.setDataCadastro(UtilRH.getDataOperacao());
            enquadramentoCC.setDataRegistro(UtilRH.getDataOperacao());
            enquadramentoPCS.setVencimentoBase(enquadramentoPCS.getVencimentoBase());
            Util.adicionarObjetoEmLista(selecionado.getEnquadramentoCCs(), enquadramentoCC);

            cancelarEnquadramentoCC();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCamposObrigatoriosEnquadramentoCC() {
        ValidacaoException ve = new ValidacaoException();
        if (enquadramentoCC.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de início de vigência deve ser informado.");
        }

        if (planoCargoSalario == null || planoCargoSalario.getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Plano de Cargos e Salários deve ser informado.");
        }

        if (enquadramentoCC.getCategoriaPCS() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo categoriaPCS deve ser informado.");
        }
        if (enquadramentoPCS == null || enquadramentoPCS.getVencimentoBase() == null || enquadramentoPCS.getVencimentoBase().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O valor de referência deve ser maior que 0(zero).");
        }
        ve.lancarException();
    }

    public List<SelectItem> getPlanosDeCargosSalarios() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado.getCargo() != null && enquadramentoCC != null && enquadramentoCC.getInicioVigencia() != null) {
            for (PlanoCargosSalarios planoCargosSalarios : categoriaPCSFacade.buscarPlanoCargosSalariosNoEnquadramentoFuncionalPorCargoAndDataVigencia(selecionado.getCargo(), enquadramentoCC.getInicioVigencia())) {
                toReturn.add(new SelectItem(planoCargosSalarios, planoCargosSalarios.toString()));
            }
        }
        if (toReturn.size() == 1) {
            planoCargoSalario = (PlanoCargosSalarios) toReturn.get(0).getValue();
        }
        return toReturn;
    }

    public String getValor() {

        if (categoriaPCS != null && progressaoPCS != null) {
            EnquadramentoPCS epcs = enquadramentoPCSFacade.recuperaValor(categoriaPCS, progressaoPCS, selecionado.getInicioVigencia());
            if (epcs != null) {

                return epcs.getVencimentoBase() + "";
            }
        }
        return "";
    }

    public ConverterAutoComplete getConverterPlanosDeCargosSalarios() {
        return new ConverterAutoComplete(PlanoCargosSalarios.class, planoCargosSalariosFacade);
    }

    public ConverterAutoComplete getConverterCategoriaPCS() {
        return new ConverterAutoComplete(CategoriaPCS.class, categoriaPCSFacade);
    }

    public void validarCampos() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getFinalVigencia() != null) {
            if (selecionado.getFinalVigencia().before(selecionado.getInicioVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data final de vigência não pode ser menor que a data inicial!");
            }
        }
        for (EnquadramentoCC enquadramento : selecionado.getEnquadramentoCCs()) {
            if (enquadramento.getFinalVigencia() == null || DataUtil.isVigente(acessoSubsidioFacade.getSistemaFacade().getDataOperacao(), enquadramento.getFinalVigencia())) {
                boolean isValido = false;
                for (PlanoCargosSalarios planoCargosSalarios : categoriaPCSFacade.buscarPlanoCargosSalariosNoEnquadramentoFuncionalPorCargoAndDataVigencia(selecionado.getCargo(), enquadramento.getInicioVigencia())) {
                    if (enquadramento.getCategoriaPCS().getPlanoCargosSalarios().equals(planoCargosSalarios)) {
                        isValido = true;
                        break;
                    }
                }
                if (!isValido) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Existe enquadramento vigente não pertencente ao cargo selecionado.");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    public BigDecimal valorVencimentoBase(EnquadramentoCC enquadramentoCC) {
        if (enquadramentoCC.getCategoriaPCS() != null && enquadramentoCC.getProgressaoPCS() != null) {
            EnquadramentoPCS epcs = enquadramentoPCSFacade.recuperaValor(enquadramentoCC.getCategoriaPCS(), enquadramentoCC.getProgressaoPCS(), enquadramentoCC.getInicioVigencia());
            if (epcs != null) {
                return epcs.getVencimentoBase();
            }
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/acesso-subsidio/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public boolean isCargoComissaoPreenchido() {
        if (selecionado.getCargo() == null) {
            FacesUtil.addOperacaoNaoPermitida("É necessário informar o campo Cargo Comissão na aba Dados Gerais.");
            return false;
        }

        return true;
    }

    public void novoEnquadramentoCC() {
        if (!isCargoComissaoPreenchido()) {
            enquadramentoCC = null;
            return;
        }

        enquadramentoCC = new EnquadramentoCC();
        enquadramentoPCS = null;
        categoriaPCS = null;
    }

    private void limparCategoriaAndEnquadramentoAtual() {
        enquadramentoCC.setCategoriaPCS(null);
        categoriasPCS = null;
        enquadramentoPCS = null;
    }

    public void carregarEnquadramentoPCSAndCategoriasPCCR() {
        limparCategoriaAndEnquadramentoAtual();
        carregarEnquadramentoPCS();
    }

    public void carregarEnquadramentoPCS() {
        if (enquadramentoCC.getCategoriaPCS() == null || enquadramentoCC.getProgressaoPCS() == null) {
            enquadramentoPCS = new EnquadramentoPCS();
            enquadramentoPCS.setVencimentoBase(BigDecimal.ZERO);
            return;
        }

        enquadramentoPCS = enquadramentoPCSFacade.recuperaUltimoValor(enquadramentoCC.getCategoriaPCS(), enquadramentoCC.getProgressaoPCS(), null, null);
        enquadramentoCC.setVencimentoBase(enquadramentoPCS.getVencimentoBase());
    }

    public void carregarProgressoesPCS() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));

        for (ProgressaoPCS progressao : progressaoPCSFacade.buscaProgressoesNoEnquadramentoPCS(enquadramentoCC.getCategoriaPCS(), enquadramentoCC.getInicioVigencia())) {
            enquadramentoCC.setProgressaoPCS(progressao);
            carregarEnquadramentoPCS();
            return;
        }

        enquadramentoPCS.setVencimentoBase(BigDecimal.ZERO);
    }

    public void selecionarEnquadramentoCC(EnquadramentoCC enquadramentoCC) {
        this.enquadramentoCC = (EnquadramentoCC) Util.clonarObjeto(enquadramentoCC);
        carregarEnquadramentoPCS();

        planoCargoSalario = enquadramentoCC.getCategoriaPCS().getPlanoCargosSalarios();
    }

    public void removerEnquadramentoCC(EnquadramentoCC enquadramentoCC) {
        selecionado.getEnquadramentoCCs().remove(enquadramentoCC);
    }

    public List<BaseFP> completarBaseFP(String parte) {
        return baseFPFacade.buscarBasePFsPorCodigoOrDescricao(parte);
    }

    public void confirmarBaseFP() {
        try {
            validarBaseFP();
            FacesUtil.executaJavaScript("dialogBaseFolhaPagamento.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarBaseFP() {
        if (selecionado.getBaseFP() != null) {
            if (Strings.isNullOrEmpty(selecionado.getBaseFP().getCodigo())
                || Strings.isNullOrEmpty(selecionado.getBaseFP().getDescricao())
                || selecionado.getBaseFP().getItensBasesFPs().isEmpty()) {
                selecionado.setBaseFP(null);
            }
        }
    }

    private void validarBaseFP() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getBaseFP() != null) {
            if (Strings.isNullOrEmpty(selecionado.getBaseFP().getCodigo())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo código é obrigatório.");
            }
            if (Strings.isNullOrEmpty(selecionado.getBaseFP().getDescricao())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo descrição é obrigatório.");
            }
            if (selecionado.getBaseFP().getItensBasesFPs() == null || selecionado.getBaseFP().getItensBasesFPs().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Adicione ao menos um Item da Base Folha de Pagamento.");
            }
        }
        ve.lancarException();
    }

    private BaseFP getBaseFPLancamento() {
        BaseFP b = new BaseFP();
        b.setFiltroBaseFP(FiltroBaseFP.CARGO_COMISSAO);
        return b;
    }

    private void buscarUltimoCodigo() {
        if (selecionado.getBaseFP() != null) {
            String b = baseFPFacade.getCodigoBaseFP();
            if (b != null) {
                selecionado.getBaseFP().setCodigo(b);
            }
        }
    }

    public void removeItemBaseFP(ItemBaseFP e) {
        try {
            validarRemoverItemBaseFP();
            selecionado.getBaseFP().getItensBasesFPs().remove(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarRemoverItemBaseFP() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getBaseFP().getId() != null && !FiltroBaseFP.CARGO_COMISSAO.equals(selecionado.getBaseFP().getFiltroBaseFP())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Essa Base FP já existe no sistema, dessa forma os itens só podem ser removidos na tela de edita Base FP");
        }
        ve.lancarException();
    }

    public void instanciaBaseFP() {
        if (selecionado.getBaseFP() == null) {
            selecionado.setBaseFP(getBaseFPLancamento());
            buscarUltimoCodigo();
        } else {
            try {
                selecionado.getBaseFP().getItensBasesFPs().size();
            } catch (LazyInitializationException lz) {
                selecionado.setBaseFP(baseFPFacade.recuperar(selecionado.getBaseFP().getId()));
            }
        }
    }


    public void removerBaseFP() {
        selecionado.setBaseFP(null);
    }


    public void adicionarItemBaseFP() {
        try {
            validarBase();
            for (ItemBaseFP item : selecionado.getBaseFP().getItensBasesFPs()) {
                if ((item.getEventoFP().equals(itemBaseFP.getEventoFP()))
                    && (item.getOperacaoFormula().equals(itemBaseFP.getOperacaoFormula()))) {
                    FacesUtil.addMessageWarn("Atenção", "Já existe um item semelhante ao qual está sendo cadastrado!");
                    return;
                }
            }
            BaseFP bfp = selecionado.getBaseFP();
            itemBaseFP.setBaseFP(bfp);
            bfp.getItensBasesFPs().add(itemBaseFP);
            Collections.sort(bfp.getItensBasesFPs());
            itemBaseFP = new ItemBaseFP();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    private void validarBase() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getBaseFP() != null) {
            if (Strings.isNullOrEmpty(selecionado.getBaseFP().getCodigo())) {
                ve.adicionarMensagemDeCampoObrigatorio("Preencha o codigo da base.");
            }
            if (Strings.isNullOrEmpty(selecionado.getBaseFP().getDescricao())) {
                ve.adicionarMensagemDeCampoObrigatorio("Preencha a descrição da base.");
            }
            if (selecionado.getBaseFP().getId() != null && !FiltroBaseFP.CARGO_COMISSAO.equals(selecionado.getBaseFP().getFiltroBaseFP())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Essa Base FP já existe no sistema, dessa forma os itens só podem ser adicionados na tela de edita Base FP");
            }
        }
        ve.lancarException();
    }


    public void buscarBaseFP() {

        if (selecionado.getBaseFP() != null && !selecionado.getBaseFP().getCodigo().equals("")) {
            BaseFP b = baseFPFacade.getBaseByCodigo(selecionado.getBaseFP().getCodigo().trim());
            if (b != null) {
                selecionado.setBaseFP(b);
            } else {
                if (selecionado.getBaseFP() != null && !isOperacaoEditar())
                    if (baseFPFacade.existeCodigo(selecionado.getBaseFP().getCodigo())) {
                        FacesUtil.addWarn("Atenção", "Este código não pode ser utilizado");
                        selecionado.getBaseFP().setCodigo("");
                    }
            }
        }
    }

}
