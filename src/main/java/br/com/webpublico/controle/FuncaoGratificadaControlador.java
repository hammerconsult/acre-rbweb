/*
 * Codigo gerado automaticamente em Thu Oct 06 17:09:26 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.PropositoAtoLegal;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoPCS;
import br.com.webpublico.enums.rh.TipoFuncaoGratificada;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "funcaoGratificadaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoFuncaoGratificada", pattern = "/funcao-gratificada/novo/", viewId = "/faces/rh/administracaodepagamento/funcaogratificada/edita.xhtml"),
    @URLMapping(id = "editarFuncaoGratificada", pattern = "/funcao-gratificada/editar/#{funcaoGratificadaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/funcaogratificada/edita.xhtml"),
    @URLMapping(id = "listarFuncaoGratificada", pattern = "/funcao-gratificada/listar/", viewId = "/faces/rh/administracaodepagamento/funcaogratificada/lista.xhtml"),
    @URLMapping(id = "verFuncaoGratificada", pattern = "/funcao-gratificada/ver/#{funcaoGratificadaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/funcaogratificada/visualizar.xhtml")
})
public class FuncaoGratificadaControlador extends PrettyControlador<FuncaoGratificada> implements Serializable, CRUD {

    @EJB
    private FuncaoGratificadaFacade funcaoGratificadaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterContratoFP;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    private ConverterAutoComplete converterAtoDePessoal;
    private List<PlanoCargosSalarios> planoCargosSalarios;
    private PlanoCargosSalarios planoCargoSalario;
    private EnquadramentoFG enquadramentoFG;
    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;
    @EJB
    private ProgressaoPCSFacade progressaoPCSFacade;
    @EJB
    private PlanoCargosSalariosFacade planoCargosSalariosFacade;
    private ConverterGenerico converterPlanosDeCargosSalarios;
    private ConverterGenerico converterProgressaoPCS;
    private ConverterGenerico converterCategoriaPCS;
    private EnquadramentoPCS enquadramentoPCS;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    private String limparValorEnquadramentPCS;
    private List<SelectItem> categoriasPCS;
    private Operacoes operacaoEnquadramentoFG;
    private int inicio = 0;

    public FuncaoGratificadaControlador() {
        super(FuncaoGratificada.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return funcaoGratificadaFacade;
    }

    public Operacoes getOperacaoEnquadramentoFG() {
        return operacaoEnquadramentoFG;
    }

    public void setOperacaoEnquadramentoFG(Operacoes operacaoEnquadramentoFG) {
        this.operacaoEnquadramentoFG = operacaoEnquadramentoFG;
    }

    public List<ContratoFP> contratoFPs(String parte) {
        return contratoFPFacade.recuperaContrato(parte.trim());
    }

    public List<SelectItem> contratoFPsReferencia() {
        return Util.getListSelectItem(contratoFPFacade.recuperaOutrosContratosMesmaMatriculaFP(selecionado.getContratoFP()));
    }

    public void validarContrato() {
        try {
            ValidacaoException ve = new ValidacaoException();
            if (selecionado.getContratoFP() != null && selecionado.getContratoFP().getId() != null) {
                if ((!TipoFuncaoGratificada.GESTOR_RECURSOS.equals(selecionado.getTipoFuncaoGratificada()) && !TipoFuncaoGratificada.CONTROLE_INTERNO.equals(selecionado.getTipoFuncaoGratificada()))
                    && !contratoFPFacade.verificaContratoEnquadramentoFuncional(selecionado.getContratoFP())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O contrato associado ao Enquadramento Funcional não é do Tipo PCS: QUADRO EFETIVO!");
                }

                if ((TipoFuncaoGratificada.GESTOR_RECURSOS.equals(selecionado.getTipoFuncaoGratificada()) || TipoFuncaoGratificada.CONTROLE_INTERNO.equals(selecionado.getTipoFuncaoGratificada())) && contratoFPFacade.verificaContratoEnquadramentoFuncional(selecionado.getContratoFP())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O contrato associado a Função Gratificada do Tipo Gestor de Recursos não pode possuir Enquadramento Funcional");
                }


                if (funcaoGratificadaFacade.isContratoComFPVigente(selecionado)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Esse servidor já possui acesso a F.G. nesse contrato!");
                }

                if (servidorEstaEmCargoComissaoVigente(selecionado.getContratoFP())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Provimento não permitido para o servidor em acesso ao cargo e comissão para o cadastro do provimento de acesso a função gratificada se faz necessário o retorno ao cargo e carreira!");
                }
            }
            if (ve.temMensagens()) {
                selecionado.setContratoFP(null);
                throw ve;
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarContratoReferencia() {
        try {
            ValidacaoException ve = new ValidacaoException();
            if (selecionado.getContratoFPRef() != null && selecionado.getContratoFPRef().getId() != null) {
                if (!contratoFPFacade.verificaContratoEnquadramentoFuncional(selecionado.getContratoFPRef())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O contrato associado ao Enquadramento Funcional não é do Tipo PCS: QUADRO EFETIVO!");
                }
            }
            if (ve.temMensagens()) {
                selecionado.setContratoFPRef(null);
                throw ve;
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private boolean servidorEstaEmCargoComissaoVigente(ContratoFP contratoFP) {
        return funcaoGratificadaFacade.getCargoConfiancaFacade().recuperaCargoConfiancaVigente(contratoFP) != null;
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public List<AtoLegal> completaAtoDePessoal(String parte) {
        return atoLegalFacade.listaFiltrandoAtoLegalPorPropositoAtoLegal(parte.trim(), PropositoAtoLegal.ATO_DE_PESSOAL);
    }

    public Converter getConverterAtoDePessoal() {
        if (converterAtoDePessoal == null) {
            converterAtoDePessoal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoDePessoal;
    }

    @URLAction(mappingId = "verFuncaoGratificada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        selecionar();
    }

    @URLAction(mappingId = "editarFuncaoGratificada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        planoCargosSalarios = Lists.newArrayList();
        selecionar();
        getPlanosDeCargosSalarios();
    }

    @URLAction(mappingId = "novoFuncaoGratificada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        enquadramentoPCS = new EnquadramentoPCS();
        enquadramentoPCS.setVencimentoBase(BigDecimal.ZERO);
        planoCargosSalarios = Lists.newArrayList();
    }

    public void selecionar() {
        for (EnquadramentoFG e : selecionado.getEnquadramentoFGs()) {
            EnquadramentoPCS ePCS = enquadramentoPCSFacade.recuperaUltimoValor(e.getCategoriaPCS(), e.getProgressaoPCS(), null, null);
            if (ePCS != null) {
                e.setVencimentoBase(ePCS.getVencimentoBase());
            }
        }
        enquadramentoPCS = new EnquadramentoPCS();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            ProvimentoFP provimentoFP;
            if (selecionado.getProvimentoFP() == null) {
                TipoProvimento tipoProvimento = tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.NOMEACAO_FUNCAO_GRATIFICADA);
                if (tipoProvimento == null) {
                    FacesUtil.addOperacaoNaoPermitida("Tipo de provimento para acesso a função gratificada não encontrado!");
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
            if (Operacoes.NOVO.equals(operacao)) {
                contratoFPFacade.verificarParametrosControleCargoFuncaoGratificadaAndValores(selecionado.getContratoFP());
            }
            funcaoGratificadaFacade.salvarFuncaoGratificada(selecionado, provimentoFP);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }


    }

    @Override
    public boolean validaRegrasParaExcluir() {
        try {
            if (funcaoGratificadaFacade.getFichaFinanceiraFPFacade().hasFichaCalculadaPorData(selecionado.getContratoFP(), selecionado.getDataRegistro())) {
                FacesUtil.addOperacaoNaoPermitida("Foi encontrato folha processada para esta função gratificada no período de vigência. É impossível excluir este registro!");
                return false;
            }
            return true;
        } catch (ValidacaoException e) {
            logger.error("Validação ao Excluir a Função Gratificada", e.getMessage());
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro", e);
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
        return false;
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

    public EnquadramentoFG getEnquadramentoFG() {
        return enquadramentoFG;
    }

    public void setEnquadramentoFG(EnquadramentoFG enquadramentoFG) {
        this.enquadramentoFG = enquadramentoFG;
    }

    public void novoEnquadramentoFG() {
        enquadramentoFG = new EnquadramentoFG();
        enquadramentoPCS = null;
        categoriasPCS = null;
    }


    public void cancelarEnquadramentoFG() {
        enquadramentoFG = null;
    }

    public boolean validarCamposObrigatoriosEnquadramentoFG() {
        boolean retorno = true;

        if (enquadramentoFG.getInicioVigencia() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo início da vigência deve ser informado.");
            retorno = false;
        }

        if (planoCargoSalario == null || planoCargoSalario.getId() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Plano de Cargos e Salários deve ser informado.");
            retorno = false;
        }

        if (enquadramentoFG.getCategoriaPCS() == null || enquadramentoFG.getCategoriaPCS().getId() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Categoria PCCR deve ser informado.");
            retorno = false;
        }

        return retorno;

    }

    public void confirmarEnquadramentoFG() {
        if (!validarCamposObrigatoriosEnquadramentoFG()) {
            return;
        }

        if (!DataUtil.isVigenciaValida(enquadramentoFG, selecionado.getEnquadramentoFGs())) {
            return;
        }

        enquadramentoFG.setFuncaoGratificada(selecionado);
        enquadramentoFG.setDataCadastro(UtilRH.getDataOperacao());
        enquadramentoFG.setDataRegistro(UtilRH.getDataOperacao());
        enquadramentoFG.setVencimentoBase(enquadramentoPCS.getVencimentoBase());
        selecionado.setEnquadramentoFGs(Util.adicionarObjetoEmLista(selecionado.getEnquadramentoFGs(), enquadramentoFG));

        enquadramentoPCS = null;
        cancelarEnquadramentoFG();
    }

    public void selecionarEnquadramentoFG(EnquadramentoFG enquadramentoFG) {
        this.enquadramentoFG = (EnquadramentoFG) Util.clonarObjeto(enquadramentoFG);
        if (this.enquadramentoFG != null) {
            this.planoCargoSalario = this.enquadramentoFG.getCategoriaPCS().getPlanoCargosSalarios();
        }
        carregarCategoriasPCS();
        carregarEnquadramentoPCS();
    }


    public void removerEnquadramentoFG(EnquadramentoFG enquadramentoFG) {
        if (enquadramentoFG.getFimVigencia() != null) {
            FacesUtil.addOperacaoNaoPermitida("O registro não pode ser removido pois a vigência já está fechada.");
            return;
        }

        selecionado.getEnquadramentoFGs().remove(enquadramentoFG);
    }

    public Date retornaDataMenosUmDia(Date dataAtual) {
        Calendar c = Calendar.getInstance();
        c.setTime(dataAtual);
        c.add(Calendar.DATE, -1);
        return c.getTime();
    }

    public List<SelectItem> getCategoriasPCS() {
        return categoriasPCS;
    }

    public void setCategoriasPCS(List<SelectItem> categoriasPCS) {
        this.categoriasPCS = categoriasPCS;
    }

    public void carregarProgressoesPCS() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));

        for (ProgressaoPCS progressao : progressaoPCSFacade.buscaProgressoesNoEnquadramentoPCS(enquadramentoFG.getCategoriaPCS(), enquadramentoFG.getInicioVigencia())) {
            enquadramentoFG.setProgressaoPCS(progressao);
            carregarEnquadramentoPCS();
            return;
        }

        enquadramentoPCS.setVencimentoBase(BigDecimal.ZERO);
    }

    public List<SelectItem> getPlanosDeCargosSalarios() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (selecionado.getTipoFuncaoGratificada() != null) {
            if (TipoFuncaoGratificada.NORMAL.equals(selecionado.getTipoFuncaoGratificada())) {
                planoCargosSalarios = planoCargosSalariosFacade.getPlanoEmFuncaoGratificadaVigente(TipoPCS.FUNCAO_GRATIFICADA);
            }
            if (TipoFuncaoGratificada.COORDENACAO.equals(selecionado.getTipoFuncaoGratificada())) {
                planoCargosSalarios = planoCargosSalariosFacade.getPlanoEmFuncaoGratificadaVigente(TipoPCS.FUNCAO_GRATIFICADA_COORDENACAO);
            }
        }

        for (PlanoCargosSalarios planoCargosSalario : planoCargosSalarios) {
            toReturn.add(new SelectItem(planoCargosSalario, planoCargosSalario.toString()));
        }

        return toReturn;
    }

    public ConverterGenerico getConverterPlanosDeCargosSalarios() {
        if (converterPlanosDeCargosSalarios == null) {
            converterPlanosDeCargosSalarios = new ConverterGenerico(PlanoCargosSalarios.class, planoCargosSalariosFacade);
        }
        return converterPlanosDeCargosSalarios;
    }

    public ConverterGenerico getConverterCategoriaPCS() {
        if (converterCategoriaPCS == null) {
            converterCategoriaPCS = new ConverterGenerico(CategoriaPCS.class, categoriaPCSFacade);
        }
        return converterCategoriaPCS;
    }

    public ConverterGenerico getConverterProgressaoPCS() {
        if (converterProgressaoPCS == null) {
            converterProgressaoPCS = new ConverterGenerico(ProgressaoPCS.class, progressaoPCSFacade);
        }
        return converterProgressaoPCS;
    }

    public EnquadramentoPCS getEnquadramentoPCS() {
        return enquadramentoPCS;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if ((!TipoFuncaoGratificada.GESTOR_RECURSOS.equals(selecionado.getTipoFuncaoGratificada()) && !TipoFuncaoGratificada.CONTROLE_INTERNO.equals(selecionado.getTipoFuncaoGratificada()))
            && selecionado.getEnquadramentoFGs().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("É necessário informar o enquadramento da função gratificada (Enquadramento FG)!");
        }
        if (selecionado.getContratoFP() == null || selecionado.getContratoFP().getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Servidor é obrigatório!");
        }
        if (selecionado.getTipoFuncaoGratificada() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Função Gratificada é obrigatório!");
        }
        if ((TipoFuncaoGratificada.GESTOR_RECURSOS.equals(selecionado.getTipoFuncaoGratificada()) || TipoFuncaoGratificada.CONTROLE_INTERNO.equals(selecionado.getTipoFuncaoGratificada()))
            && selecionado.getContratoFPRef() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Contrato de Referência é obrigatório!");
        }
        Util.validarCampos(selecionado);
        if (selecionado.getInicioVigencia() != null && selecionado.getFinalVigencia() != null) {
            if (selecionado.getInicioVigencia().after(selecionado.getFinalVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de início da vigência não pode ser superior a data final da vigência!");
            }
        }
        if (!DataUtil.isVigenciaValida(selecionado, funcaoGratificadaFacade.buscarFuncoesGratificadasPorContratoFP(selecionado))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O servidor já possuí um registro vigênte!");
        }
        if ((TipoFuncaoGratificada.GESTOR_RECURSOS.equals(selecionado.getTipoFuncaoGratificada()) || TipoFuncaoGratificada.CONTROLE_INTERNO.equals(selecionado.getTipoFuncaoGratificada())) && !selecionado.getEnquadramentoFGs().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido salvar Função Gratificada do tipo \"Gestor de Recursos\" contendo \"Enquadramentos\"");
        }
        ve.lancarException();
    }

    public void atualizaValoresEnquadramentoFG() {
        if (enquadramentoFG.getInicioVigencia() == null || planoCargoSalario == null || planoCargoSalario.getId() == null) {
            enquadramentoFG.setCategoriaPCS(null);
        }
        if (enquadramentoFG.getCategoriaPCS() == null || enquadramentoFG.getCategoriaPCS().getId() == null) {
            enquadramentoFG.setProgressaoPCS(null);
        }
        if (enquadramentoFG.getProgressaoPCS() == null || enquadramentoFG.getProgressaoPCS().getId() == null) {
            enquadramentoPCS.setVencimentoBase(BigDecimal.ZERO);
        }
    }

    public boolean validaCamposEnquadramentoFG() {
        boolean retorno = true;
        for (EnquadramentoFG p : selecionado.getEnquadramentoFGs()) {
            if (p.equals(enquadramentoFG)) {
                continue;
            }

            if (p.getFinalVigencia() == null) {
                FacesUtil.addOperacaoNaoPermitida("Já existe um registro vigente adicionado.");
                retorno = false;
                break;
            }

            if (p.getFinalVigencia().after(enquadramentoFG.getInicioVigencia())) {
                FacesUtil.addOperacaoNaoPermitida("O campo final da vigência anterior não pode ser posterior à data inicial da nova vigência.");
                retorno = false;
                break;
            }
        }

        return retorno;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/funcao-gratificada/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    public void carregarEnquadramentoPCS() {
        if (enquadramentoFG == null || enquadramentoFG.getCategoriaPCS() == null || enquadramentoFG.getProgressaoPCS() == null) {
            enquadramentoPCS = new EnquadramentoPCS();
            enquadramentoPCS.setVencimentoBase(BigDecimal.ZERO);
            return;
        }

        enquadramentoPCS = enquadramentoPCSFacade.recuperaUltimoValor(enquadramentoFG.getCategoriaPCS(), enquadramentoFG.getProgressaoPCS(), null, null);
        enquadramentoFG.setVencimentoBase(enquadramentoPCS.getVencimentoBase());
    }

    public void carregarCategoriasPCS() {
        categoriasPCS = new ArrayList<>();
        categoriasPCS.add(new SelectItem(null, ""));

        if (planoCargoSalario == null || enquadramentoFG.getInicioVigencia() == null) {
            enquadramentoFG.setProgressaoPCS(null);
            enquadramentoPCS = null;
            return;
        }

        for (CategoriaPCS object : categoriaPCSFacade.recuperaCategoriasNoEnquadramentoFuncionalVigente(planoCargoSalario, enquadramentoFG.getInicioVigencia())) {
            CategoriaPCS cat = categoriaPCSFacade.recuperar(object.getId());
            categoriasPCS.add(new SelectItem(object, cat.getEstruturaCategoriaPCS()));
        }
    }

    private void limparCategoriaAndEnquadramentoAtual() {
        categoriasPCS = null;
        enquadramentoPCS = null;
        enquadramentoFG.setCategoriaPCS(null);
    }

    public void carregarEnquadramentoPCSAndCategoriasPCCR() {
        limparCategoriaAndEnquadramentoAtual();
        carregarCategoriasPCS();
        carregarEnquadramentoPCS();
    }

    public List<SelectItem> getTipoFuncaoGratificada() {
        return Util.getListSelectItem(TipoFuncaoGratificada.values());
    }

    public boolean isGestorOuControleInterno() {
        TipoFuncaoGratificada tipo = selecionado.getTipoFuncaoGratificada();
        return TipoFuncaoGratificada.GESTOR_RECURSOS.equals(tipo) || TipoFuncaoGratificada.CONTROLE_INTERNO.equals(tipo);
    }

}
