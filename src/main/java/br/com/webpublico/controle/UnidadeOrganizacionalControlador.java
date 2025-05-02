/*
 * Codigo gerado automaticamente em Fri May 13 14:50:18 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoAtoLegal;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.UnidadeOrganizacionalFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-unidade-organizacional", pattern = "/unidade-organizacional/novo/", viewId = "/faces/tributario/cadastromunicipal/unidadeorganizacional/edita.xhtml"),
    @URLMapping(id = "editar-unidade-organizacional", pattern = "/unidade-organizacional/editar/#{unidadeOrganizacionalControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/unidadeorganizacional/edita.xhtml"),
    @URLMapping(id = "ver-unidade-organizacional", pattern = "/unidade-organizacional/ver/#{unidadeOrganizacionalControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/unidadeorganizacional/visualizar.xhtml"),
    @URLMapping(id = "listar-unidade-organizacional", pattern = "/unidade-organizacional/listar/", viewId = "/faces/tributario/cadastromunicipal/unidadeorganizacional/lista.xhtml")
})
public class UnidadeOrganizacionalControlador extends PrettyControlador<UnidadeOrganizacional> implements Serializable, CRUD {

    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    private ConverterGenerico converterEsferaGoverno;
    private ConverterAutoComplete converterCBO;
    private ConverterAutoComplete converterCNAE;
    private ConverterAutoComplete converterPessoaJuridica, converterUnidade;
    private Entidade entidadeSelecionada;
    private boolean habilitaEntidade;
    private FileUploadEvent fileUploadEvent;
    private Arquivo arquivo;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterPagamentoDaGPS;
    private ConverterAutoComplete converterNaturezaJuridica;
    private Boolean listarEntidades = Boolean.FALSE;
    private ResponsavelUnidade responsavelUnidade;
    private ConverterAutoComplete converterAtoLegal;
    private UnidadeOrganizacionalAnexo anexo;
    private List<ResponsavelUnidade> responsaveis;
    private UnidadeOrganizacionalAtoNormativo atoNormativo;

    public UnidadeOrganizacionalControlador() {
        super(UnidadeOrganizacional.class);
    }

    public void trataAssociacao() {

        UnidadeOrganizacional uo = (UnidadeOrganizacional) selecionado;
//        uo.setEndereco(enderecoSelecionado);
        entidadeSelecionada.setAtiva(habilitaEntidade);
        if (entidadeSelecionada.isAtiva()) {
            uo.setEntidade(entidadeSelecionada);
        }
    }

    public List<PessoaFisica> listaPessoaFisica(String parte) {
        return unidadeOrganizacionalFacade.getPessoaFacade().listaPessoaComVinculoVigente(parte.trim());
    }

    public List<Pessoa> completaPessoa(String parte) {
        return unidadeOrganizacionalFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    @Override
    public AbstractFacade getFacede() {
        return unidadeOrganizacionalFacade;
    }

    @URLAction(mappingId = "novo-unidade-organizacional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        arquivo = new Arquivo();
        fileUploadEvent = null;
        entidadeSelecionada = new Entidade();
        habilitaEntidade = false;
        atoNormativo = null;
        super.novo();
    }

    @URLAction(mappingId = "editar-unidade-organizacional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionar();
        responsaveis = unidadeOrganizacionalFacade.buscaResponsaveisPorUnidade(selecionado);
    }

    @URLAction(mappingId = "ver-unidade-organizacional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        selecionar();
    }

    public void selecionar() {
        arquivo = new Arquivo();
        fileUploadEvent = null;
        operacao = Operacoes.EDITAR;
//        UnidadeOrganizacional uo = (UnidadeOrganizacional) evento.getComponent().getAttributes().get("objeto");
        selecionado = unidadeOrganizacionalFacade.recuperar(selecionado.getId());
//        selecionado = uo;
        entidadeSelecionada = selecionado.getEntidade();
//        enderecoSelecionado = uo.getEndereco();
        if (entidadeSelecionada != null) {
            habilitaEntidade = entidadeSelecionada.isAtiva();
            if (entidadeSelecionada.getArquivo() != null) {
                arquivo = entidadeSelecionada.getArquivo();
            }
        } else {
            entidadeSelecionada = new Entidade();
            habilitaEntidade = false;
        }
        atoNormativo = null;
    }

    public Boolean validaCampos() {

        boolean retorno = Util.validaCampos(selecionado);

        if (retorno && habilitaEntidade) {
            if (entidadeSelecionada.getNome() == null || entidadeSelecionada.getNome().trim().isEmpty()) {
                FacesUtil.addError("Impossível continuar!", "O campo Nome é obrigatório.");
                retorno = false;
            }
            if (entidadeSelecionada.getCnae() == null || entidadeSelecionada.getCnae().getId() == null) {
                FacesUtil.addError("Impossível continuar!", "O campo CNAE é obrigatório.");
                retorno = false;
            }
            if (entidadeSelecionada.getPessoaJuridica() == null || entidadeSelecionada.getPessoaJuridica().getId() == null) {
                FacesUtil.addError("Impossível continuar!", "O campo Pessoa Jurídica é obrigatório.");
                retorno = false;
            }
            if (entidadeSelecionada.getNaturezaJuridicaEntidade() == null || entidadeSelecionada.getNaturezaJuridicaEntidade().getId() == null) {
                FacesUtil.addError("Impossível continuar!", "O campo Natureza Jurídica é obrigatório.");
                retorno = false;
            }
        }

        return retorno;
    }

    public void uploadImagen(FileUploadEvent event) {
        fileUploadEvent = event;
    }

    public void desmarcarUnidadeEscolar() {
        selecionado.setCodigoInep(null);
    }

    private void validarCodigoInep() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getEscola() && Strings.isNullOrEmpty(selecionado.getCodigoInep())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O preenchimento do campo Código INEP é obrigatório para unidades escolares.");
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validarCodigoInep();
            UnidadeOrganizacional uo = (UnidadeOrganizacional) selecionado;
            if (Operacoes.EDITAR.equals(operacao)) {
                if (validaCampos()) {
                    if (controle(uo)) {
                        trataAssociacao();
                        unidadeOrganizacionalFacade.salvarUnidade(uo, arquivo, fileUploadEvent);
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Alterado com Sucesso!", "A " + metadata.getNomeEntidade() + " " + selecionado.toString() + " foi salvo com sucesso!"));
                        redireciona();
                    }
                }
            } else {
                if (validaCampos()) {
                    trataAssociacao();
                    unidadeOrganizacionalFacade.salvarNovaUnidade(uo, fileUploadEvent, arquivo);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Salvo com Sucesso!", "A " + metadata.getNomeEntidade() + " " + selecionado.toString() + " foi salvo com sucesso!"));
                    redireciona();
                }
            }
            unidadeOrganizacionalFacade.getHierarquiaOrganizacionalFacade().atualizaViewHierarquiaOrcamentaria();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public Boolean controle(UnidadeOrganizacional unidade) {
        Boolean controle = null;
        if (unidade.getInativo()) {
            controle = unidadeOrganizacionalFacade.verificaInativo(unidade);
            if (controle) {
                return controle;
            } else {
                controle = false;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, " Esta Unidade Organizacional não poder ser INATIVADA!", "Desmarque a opção Inativar. Obrigado."));
                return controle;
            }
        } else {
            return true;
        }


    }

    public void apagaArquivo() {
        //System.out.println("passou aqui");
        entidadeSelecionada.setArquivo(null);
    }

    @Override
    public Operacoes getOperacao() {
        return operacao;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public Boolean getListarEntidades() {
        return listarEntidades;
    }

    public void setListarEntidades(Boolean listarEntidades) {
        this.listarEntidades = listarEntidades;
    }

    public Converter getConverterUnidadeOrganizacional() {
        if (converterPessoaJuridica == null) {
            converterPessoaJuridica = new ConverterAutoComplete(PessoaJuridica.class, unidadeOrganizacionalFacade.getPessoaJuridicaFacade());
        }
        return converterPessoaJuridica;
    }

    public Converter getConverterUnidade() {
        if (converterUnidade == null) {
            converterUnidade = new ConverterAutoComplete(UnidadeOrganizacional.class, unidadeOrganizacionalFacade);
        }
        return converterUnidade;
    }

    public List<PessoaJuridica> completaUnidadeOrganizacional(String parte) {
        return unidadeOrganizacionalFacade.getPessoaJuridicaFacade().listaFiltrando(parte.trim(), "razaoSocial");
    }

    public List<UnidadeOrganizacional> completaUnidade(String parte) {
        return unidadeOrganizacionalFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public List<SelectItem> getEsferaGoverno() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (EsferaGoverno object : unidadeOrganizacionalFacade.getEsferaGovernoFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

//    public ConverterGenerico getConverterEsferaGoverno() {
//        if (converterEsferaGoverno == null) {
//            converterEsferaGoverno = new ConverterGenerico(EsferaGoverno.class, unidadeOrganizacionalFacade.getEsferaGovernoFacade());
//        }
//        return converterEsferaGoverno;
//    }
//
//    public List<SelectItem> getCBO() {
//        List<SelectItem> toReturn = new ArrayList<SelectItem>();
//        for (CBO object : CBOFacade.lista()) {
//            toReturn.add(new SelectItem(object, object.getDescricao()));
//        }
//        return toReturn;
//    }
//
//    public Converter getConverterCBO() {
//        if (converterCBO == null) {
//            converterCBO = new ConverterAutoComplete(CBO.class, CBOFacade);
//        }
//        return converterCBO;
//    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, unidadeOrganizacionalFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

//    public List<CBO> completaCBO(String parte) {
//        return CBOFacade.listaFiltrando(parte.trim(), "descricao", "codigo");
//    }
//
//    public List<CNAE> completaCNAE(String parte) {
//        return CNAEFacade.listaFiltrando(parte.trim(), "descricaoDetalhada", "codigoCnae");
//    }
//
//    public Converter getConverterCNAE() {
//        if (converterCNAE == null) {
//            converterCNAE = new ConverterAutoComplete(CNAE.class, CNAEFacade);
//        }
//        return converterCNAE;
//    }
//
//    public List<SelectItem> getTipoEntidade() {
//        List<SelectItem> toReturn = new ArrayList<SelectItem>();
//        for (TipoEntidade object : TipoEntidade.values()) {
//            toReturn.add(new SelectItem(object, object.toString()));
//        }
//        return toReturn;
//    }
//
//    public void validacaoCnpj(FacesContext context, UIComponent component, Object value) throws ValidatorException {
//        if (unidadeOrganizacionalFacade.valida_CpfCnpj((String) value) == false) {
//            ((UIInput) component).setValid(false);
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "CNPJ inválida!", "Por favor, verificar CNPJ."));
//        }
//    }
//
//    public Entidade getEntidadeSelecionada() {
//        return entidadeSelecionada;
//    }
//
//    public void setEntidadeSelecionada(Entidade entidadeSelecionada) {
//        this.entidadeSelecionada = entidadeSelecionada;
//    }
//
//    public boolean isHabilitaEntidade() {
//        return habilitaEntidade;
//    }
//
//    public void setHabilitaEntidade(boolean habilitaEntidade) {
//        this.habilitaEntidade = habilitaEntidade;
//    }

    public void excluir(ActionEvent evento) {
        try {
            UnidadeOrganizacional uo = (UnidadeOrganizacional) evento.getComponent().getAttributes().get("objeto");
            selecionado = uo;
            if (unidadeOrganizacionalFacade.isDependenciaUnidadeOrganizacional((UnidadeOrganizacional) selecionado)) {
                unidadeOrganizacionalFacade.remover(uo);
                unidadeOrganizacionalFacade.getHierarquiaOrganizacionalFacade().atualizaViewHierarquiaOrcamentaria();
            } else {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " A Unidade Organizacional não pode ser removida, pois possui denpendências no sistema.");
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage()));
        }
    }

    public void excluirSelecionado() {
        try {
            if (unidadeOrganizacionalFacade.isDependenciaUnidadeOrganizacional((UnidadeOrganizacional) selecionado)) {
                getFacede().remover(selecionado);
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Ops", "Não foi possível excluir a Unidade Organizacional"));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage()));
        }

    }

    /**
     * @author peixe Método usado para ligar a descrição da unidade para o nome
     * da entidade, quando se habilita uma entidade
     */
    public void mesmoNomeParaEntidade() {
        UnidadeOrganizacional uo = (UnidadeOrganizacional) selecionado;
        if (uo.getDescricao() != null) {
            entidadeSelecionada.setNome(uo.getDescricao());
        }
    }

    public List<SelectItem> getTipoAtoLegal() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        for (TipoAtoLegal tal : TipoAtoLegal.values()) {
            retorno.add(new SelectItem(tal, tal.getDescricao()));
        }
        return retorno;
    }

//    public List<PagamentoDaGPS> completaPagamentoDaGPS(String parte) {
//        return pagamentoDaGPSFacade.listaFiltrandoCodigoDescricao(parte);
//    }
//
//    public Converter getConverterPagamentoDaGPS() {
//        if (converterPagamentoDaGPS == null) {
//            converterPagamentoDaGPS = new ConverterAutoComplete(PagamentoDaGPS.class, pagamentoDaGPSFacade);
//        }
//        return converterPagamentoDaGPS;
//    }
//
//    public List<SelectItem> getSimples() {
//        List<SelectItem> retorno = new ArrayList<SelectItem>();
//        for (Simples object : Simples.values()) {
//            retorno.add(new SelectItem(object, object.toString()));
//        }
//        return retorno;
//    }


    //    public List listaFiltrandoX() {
//        getLista().clear();
//        getLista().addAll(unidadeOrganizacionalFacade.listaFiltrandoX(getFiltro(), getInicioBuscaTabela(), getMaximoRegistrosTabela(), getListarEntidades()));
//        return getLista();

//    public List<NaturezaJuridicaEntidade> completaNaturezaJuridica(String parte) {
//        return naturezaJuridicaEntidadeFacade.listaFiltrando(parte.trim(), "descricao");
//    }
//
//    public Converter getConverterNaturezaJuridica() {
//        if (converterNaturezaJuridica == null) {
//            converterNaturezaJuridica = new ConverterAutoComplete(NaturezaJuridicaEntidade.class, naturezaJuridicaEntidadeFacade);
//        }
//        return converterNaturezaJuridica;
//    }

    @Override
    public String getCaminhoPadrao() {
        return "/unidade-organizacional/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

//    public ResponsavelUnidade getResponsavelUnidade() {
//        return responsavelUnidade;
//    }
//
//    public void setResponsavelUnidade(ResponsavelUnidade responsavelUnidade) {
//        this.responsavelUnidade = responsavelUnidade;
//    }
//
//    public void novoResponsavelUnidade() {
//        this.responsavelUnidade = new ResponsavelUnidade();
//    }
//
//    public List<SelectItem> getFuncoesGestorOrdenador() {
//        List<SelectItem> toReturn = new ArrayList<SelectItem>();
//        for (FuncaoResponsavelUnidade object : FuncaoResponsavelUnidade.values()) {
//            toReturn.add(new SelectItem(object, object.getDescricao()));
//        }
//        return toReturn;
//    }
//
//    public void cancelarResponsavel() {
//        this.responsavelUnidade = null;
//    }
//
//    public void removerGestorOrdenador(ActionEvent ev) {
//        ResponsavelUnidade goe = (ResponsavelUnidade) ev.getComponent().getAttributes().get("item");
//        selecionado.getResponsaveisUnidades().remove(selecionado.getResponsaveisUnidades().indexOf(goe));
//    }
//
//    public void selecionarGestorOrdenador(ActionEvent ev) {
//        ResponsavelUnidade ru = (ResponsavelUnidade) ev.getComponent().getAttributes().get("item");
//        responsavelUnidade = (ResponsavelUnidade) Util.clonarObjeto(ru);
//    }
//
//    public void confirmarResponsavel() {
//        try {
//            responsavelUnidade.validarAntesDeConfirmar();
//            selecionado.addGestorOrdenadorEntidade(this.responsavelUnidade);
//            responsavelUnidade = null;
//        } catch (Exception e) {
//            FacesUtil.addError("Operação não realizada!", e.getMessage());
//        }
//    }
//
//    public List<AtoLegal> completaAtoLeal(String parte) {
//        return atoLegalFacade.listaFiltrandoAtoLegal(parte.trim());
//    }

    public ConverterAutoComplete getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, unidadeOrganizacionalFacade.getAtoLegalFacade());
        }
        return converterAtoLegal;
    }

    public UnidadeOrganizacionalAnexo getAnexo() {
        return anexo;
    }

    public void setAnexo(UnidadeOrganizacionalAnexo anexo) {
        this.anexo = anexo;
    }

    public void novoAnexo() {
        this.anexo = new UnidadeOrganizacionalAnexo();
    }

    public void atribuirNullAnexo() {
        this.anexo = null;
    }

    public void adicionarAnexo() {
        try {
            Util.validarCampos(this.anexo);
            if (anexo.getDetentorArquivoComposicao() == null) {
                throw new ValidacaoException("É obrigatório anexo pelo menos um arquivo");
            }
            this.anexo.setUnidadeOrganizacional(selecionado);
            this.selecionado.setAnexos(Util.adicionarObjetoEmLista(this.selecionado.getAnexos(), this.anexo));
            atribuirNullAnexo();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void editarAnexo(UnidadeOrganizacionalAnexo anexo) {
        this.anexo = anexo;
    }

    public void removerAnexo(UnidadeOrganizacionalAnexo anexo) {
        this.selecionado.getAnexos().remove(anexo);
    }

    public List<ResponsavelUnidade> getResponsaveis() {
        return responsaveis;
    }

    public void setResponsaveis(List<ResponsavelUnidade> responsaveis) {
        this.responsaveis = responsaveis;
    }

    public UnidadeOrganizacionalAtoNormativo getAtoNormativo() {
        return atoNormativo;
    }

    public void setAtoNormativo(UnidadeOrganizacionalAtoNormativo atoNormativo) {
        this.atoNormativo = atoNormativo;
    }

    public void editarAtoNormativo(UnidadeOrganizacionalAtoNormativo atoNormativo) {
        this.atoNormativo = (UnidadeOrganizacionalAtoNormativo) Util.clonarObjeto(atoNormativo);
    }

    public void cancelarAtoNormativo() {
        this.atoNormativo = null;
    }

    public void adicionarAtoNormativo() {
        try {
            Util.validarCampos(this.atoNormativo);
            Util.adicionarObjetoEmLista(selecionado.getAtosNormativos(), this.atoNormativo);
            cancelarAtoNormativo();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorPadrao(ex);
        }
    }

    public void removerAtoNormativo(UnidadeOrganizacionalAtoNormativo atoNormativo) {
        selecionado.getAtosNormativos().remove(atoNormativo);
    }

    public void novoAtoNormativo() {
        this.atoNormativo = new UnidadeOrganizacionalAtoNormativo();
        this.atoNormativo.setUnidadeOrganizacional(selecionado);
    }
}
