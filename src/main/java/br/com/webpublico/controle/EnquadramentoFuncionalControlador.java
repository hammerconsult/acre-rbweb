/*
 * Codigo gerado automaticamente em Sat Jul 02 10:03:07 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.AutorizacaoUsuarioRH;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.rh.TipoAutorizacaoRH;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLActions;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@ManagedBean(name = "enquadramentoFuncionalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoEnquadramentoFuncional", pattern = "/enquadramento-funcional/novo/", viewId = "/faces/rh/administracaodepagamento/enquadramentofuncional/edita.xhtml"),
    @URLMapping(id = "enqudramentoFuncional-correcao", pattern = "/enquadramento-funcional/correcao/", viewId = "/faces/rh/administracaodepagamento/enquadramentofuncional/correcao.xhtml"),
    @URLMapping(id = "novoEnquadramentoFuncionalNovo", pattern = "/enquadramento-funcional-novo/novo/", viewId = "/faces/rh/administracaodepagamento/enquadramentofuncional/editaNovo.xhtml"),
    @URLMapping(id = "editarEnquadramentoFuncional", pattern = "/enquadramento-funcional/editar/#{enquadramentoFuncionalControlador.id}/", viewId = "/faces/rh/administracaodepagamento/enquadramentofuncional/edita.xhtml"),
    @URLMapping(id = "editarEnquadramentoFuncionalNovo", pattern = "/enquadramento-funcional-novo/editar/#{enquadramentoFuncionalControlador.id}/", viewId = "/faces/rh/administracaodepagamento/enquadramentofuncional/editaNovo.xhtml"),
    @URLMapping(id = "listarEnquadramentoFuncional", pattern = "/enquadramento-funcional/listar/", viewId = "/faces/rh/administracaodepagamento/enquadramentofuncional/lista.xhtml"),
    @URLMapping(id = "verEnquadramentoFuncional", pattern = "/enquadramento-funcional/ver/#{enquadramentoFuncionalControlador.id}/", viewId = "/faces/rh/administracaodepagamento/enquadramentofuncional/visualizar.xhtml")
})
public class EnquadramentoFuncionalControlador extends PrettyControlador<EnquadramentoFuncional> implements Serializable, CRUD {

    List<EnquadramentoFuncional> enquadramentos;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private PlanoCargosSalariosFacade planoCargosSalariosFacade;
    @EJB
    private ProgressaoPCSFacade progressaoPCSFacade;
    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private CargoConfiancaFacade cargoConfiancaFacade;
    @EJB
    private FuncaoGratificadaFacade funcaoGratificadaFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    private ConverterGenerico converterEnquadramentoPCS, converterPlano;
    private ConverterGenerico converterCategoria, converterProgressao;
    private EnquadramentoPCS enquadramento;
    private PlanoCargosSalarios planoCargosSalarios;
    private ConverterAutoComplete converterContratoServidor;
    private EnquadramentoFuncional enquadramentoFuncionalVigente;
    private Boolean existeEnquadramentoFuncionalVigente;
    private MoneyConverter moneyConverter;
    private ProgressaoPCS progressaoPCSPai;
    private CategoriaPCS categoriaPCSfilha;
    private boolean cadastroNovo = false;
    private ProvimentoFP provimentoFP;
    @EJB
    private AtoLegalFacade atoLegalFacade;

    private Date dataInicioVigencia;
    private Date dataFimVigencia;

    /**
     * Não utilizar!!!
     */
    private transient Date inicioVigencia;
    private transient Date dataCadastro;
    private Map<VinculoFP, Map<EnquadramentoFuncional, Boolean>> vinculoFPEnquadramentos;
    private int inicio = 0;
    private List<String> enquadramentosPcsDescricao;

    public EnquadramentoFuncionalControlador() {
        super(EnquadramentoFuncional.class);
    }

    public EnquadramentoFuncionalFacade getFacade() {
        return enquadramentoFuncionalFacade;
    }

    public PlanoCargosSalarios getPlanoCargosSalarios() {
        return planoCargosSalarios;
    }

    public void setPlanoCargosSalarios(PlanoCargosSalarios planoCargosSalarios) {
        this.planoCargosSalarios = planoCargosSalarios;
    }

    public EnquadramentoFuncional getEnquadramentoFuncionalVigente() {
        return enquadramentoFuncionalVigente;
    }

    public Boolean getExisteEnquadramentoFuncionalVigente() {
        return existeEnquadramentoFuncionalVigente;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    @URLActions(actions = {
        @URLAction(mappingId = "novoEnquadramentoFuncional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false),
        @URLAction(mappingId = "novoEnquadramentoFuncionalNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    })

    @Override
    public void novo() {
        super.novo();
        progressaoPCSPai = null;
        categoriaPCSfilha = null;
        enquadramento = new EnquadramentoPCS();
        planoCargosSalarios = new PlanoCargosSalarios();
        enquadramentoFuncionalVigente = null;
        existeEnquadramentoFuncionalVigente = false;
        ((EnquadramentoFuncional) selecionado).setDataCadastro(new Date());
        cadastroNovo = false;
        provimentoFP = new ProvimentoFP();
        selecionado.setConsideraParaProgAutomat(Boolean.TRUE);
    }

    public ConverterGenerico getConverterCategoria() {
        if (converterCategoria == null) {
            converterCategoria = new ConverterGenerico(CategoriaPCS.class, categoriaPCSFacade);
        }
        return converterCategoria;
    }

    public ConverterGenerico getConverterPlano() {
        if (converterPlano == null) {
            converterPlano = new ConverterGenerico(PlanoCargosSalarios.class, planoCargosSalariosFacade);
        }
        return converterPlano;
    }

    public ConverterGenerico getConverterProgressao() {
        if (converterProgressao == null) {
            converterProgressao = new ConverterGenerico(ProgressaoPCS.class, progressaoPCSFacade);
        }
        return converterProgressao;
    }

    private void ordenarEnquadramentosInicioVigenciaCrescente(List<EnquadramentoFuncional> enquadramentos) {
        Collections.sort(enquadramentos, new Comparator<EnquadramentoFuncional>() {
            @Override
            public int compare(EnquadramentoFuncional o1, EnquadramentoFuncional o2) {
                Date i1 = o1.getInicioVigencia();
                Date i2 = o2.getInicioVigencia();
                return i1.compareTo(i2);
            }
        });
    }

    @Override
    public void salvar() {
        definirProgressaoCategoria();
        if (validaCampos()) {
            try {
                List<EnquadramentoFuncional> enquadramentosDoContratoFP = enquadramentoFuncionalFacade.recuperarEnquadramentoContratoFP(selecionado.getContratoServidor());
                enquadramentos = prepararEnquadramentoParaValidacao(enquadramentosDoContratoFP);
                validarInicioAndFinalDeVigenciaEnquadramento(enquadramentos);

                EnquadramentoFuncional enquadramentoVigente = enquadramentoFuncionalFacade.recuperaEnquadramentoVigente(((EnquadramentoFuncional) selecionado).getContratoServidor());
                if (enquadramentoFuncionalVigente != null) {
                    if (provimentoFP.getTipoProvimento() != null && !Operacoes.NOVO.equals(operacao)) {
                        provimentoFP = enquadramentoFuncionalFacade.salvarProvimento(provimentoFP);
                        selecionado.setProvimentoFP(provimentoFP);
                    }
                    this.getFacade().salvar(enquadramentoFuncionalVigente);
                }

                if (operacao == Operacoes.NOVO) {
                    criarProvimentoFP();
                    enquadramentoFuncionalFacade.salvarNovo(selecionado, enquadramentoVigente, provimentoFP);
                } else {
                    if (provimentoFP.getTipoProvimento() != null) {
                        provimentoFP = enquadramentoFuncionalFacade.salvarProvimento(provimentoFP);
                        selecionado.setProvimentoFP(provimentoFP);
                    }
                    this.getFacede().salvar(selecionado);
                }
                enquadramentoFuncionalVigente = null;
                redireciona();
                FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());

            } catch (ValidacaoException val) {
                if (val.temMensagens()) {
                    FacesUtil.printAllFacesMessages(val.getMensagens());
                }
            } catch (Exception e) {
                logger.error("Erro: ", e);
                FacesUtil.addError("Exceção do sistema!", e.getMessage());
                return;
            }
        } else {
            return;
        }
    }

    private void definirProgressaoCategoria() {
        if (!cadastroNovo) {
            if (enquadramento != null) {
                getEnquadreamentoFuncional().setProgressaoPCS(enquadramento.getProgressaoPCS());
                getEnquadreamentoFuncional().setCategoriaPCS(enquadramento.getCategoriaPCS());
            } else {
                FacesUtil.addError("Enquadramento de Plano de Cargos e Trabalhos não selecionado!", "Por favor selecione.");
            }
        }
    }

    @Override
    public boolean validaRegrasEspecificas() {
        if (!DataUtil.isVigenciaValida(selecionado, enquadramentos)) {
            throw new ValidacaoException();
        }
        return false;
    }

    private void validarInicioAndFinalDeVigenciaEnquadramento(List<EnquadramentoFuncional> enquadramentos) {
        ValidacaoException ve = new ValidacaoException();
        if (enquadramentos != null && !enquadramentos.isEmpty()) {

            ordenarEnquadramentosInicioVigenciaCrescente(enquadramentos);
            validarSeExisteRegistroSemVigenciaCadastrado(enquadramentos, ve);

            for (EnquadramentoFuncional enquadramento : enquadramentos) {
                if (selecionado.getId() != null && (selecionado.getId().equals(enquadramento.getId()))) {
                    continue;
                }
                Date dataInicioVigencia = enquadramento.getInicioVigencia();
                Date dataFimVigencia = enquadramento.getFimVigencia();

                if (selecionado.getFinalVigencia() != null) {
                    if (((dataInicioVigencia.before(selecionado.getInicioVigencia())) && selecionado.getInicioVigencia().before(dataInicioVigencia)
                        && selecionado.getFinalVigencia().after(dataFimVigencia))) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe registro vigente no intervalo informado com início de vigência em "
                            + DataUtil.getDataFormatada(dataInicioVigencia) + ".");
                    }
                    ve.lancarException();
                }
            }
        }
    }

    private void validarSeExisteRegistroSemVigenciaCadastrado(List<EnquadramentoFuncional> enquadramentos, ValidacaoException ve) {
        for (EnquadramentoFuncional enquadramento : enquadramentos) {
            if (selecionado.getId() != null && (selecionado.getId().equals(enquadramento.getId()))) {
                continue;
            }
            if (selecionado.getFinalVigencia() == null && enquadramento.getFinalVigencia() == null &&
                selecionado.getContratoServidor().getId().equals(enquadramento.getContratoServidor().getId())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe enquadramento funcional com vigência em aberto.");
                ve.lancarException();
            }
        }
    }

    private String getMensagemValidacaoVIgenciaEnquadramento(EnquadramentoFuncional enquadramento) {
        String url = FacesUtil.getRequestContextPath() + "/enquadramento-funcional/editar/" + enquadramento.getId() + "/";
        String mensagem = "Existem enquadramentos funcionais anteriores com data final de vigência" +
            " menor que a data inicial.";
        mensagem += "<br/><b><a href='" + url + "' target='_blank' style='color:blue;'>Editar Enquadramento Funcional</a></b>";
        return mensagem;
    }

    private boolean hasEnquadramentoInconsistente() {
        return (dataInicioVigencia != null && dataFimVigencia != null) && dataFimVigencia.before(dataInicioVigencia);
    }

    private List<EnquadramentoFuncional> prepararEnquadramentoParaValidacao(List<EnquadramentoFuncional> enquadramentoFuncionals) {
        List<EnquadramentoFuncional> enquadramentos = Lists.newArrayList();
        for (EnquadramentoFuncional enquadramentoFuncional : enquadramentoFuncionals) {
            if (enquadramentoFuncionalVigente != null && enquadramentoFuncionalVigente.getId() != null && enquadramentoFuncional.getId().equals(enquadramentoFuncionalVigente.getId())) {
                enquadramentos.add(enquadramentoFuncionalVigente);
            } else {
                enquadramentos.add(enquadramentoFuncional);
            }
        }
        return enquadramentos;
    }

    private EnquadramentoFuncional getEnquadreamentoFuncional() {
        return (EnquadramentoFuncional) selecionado;
    }

    @URLAction(mappingId = "verEnquadramentoFuncional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        selecionar();
        provimentoFP = selecionado.getProvimentoFP();
        carregarEnquadramentosPcs();
    }

    @URLActions(actions = {
        @URLAction(mappingId = "editarEnquadramentoFuncional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false),
        @URLAction(mappingId = "editarEnquadramentoFuncionalNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)

    })
    @Override
    public void editar() {
        operacao = Operacoes.EDITAR;
        selecionar();
        if (selecionado.getProvimentoFP() != null) {
            provimentoFP = selecionado.getProvimentoFP();
        } else {
            provimentoFP = new ProvimentoFP();
            provimentoFP.setSequencia(enquadramentoFuncionalFacade.getProvimentoFPFacade().geraSequenciaPorVinculoFP(selecionado.getContratoServidor()));
            provimentoFP.setVinculoFP(selecionado.getContratoServidor());
            provimentoFP.setDataProvimento(new Date());
        }
        dataInicioVigencia = selecionado.getInicioVigencia();
        dataFimVigencia = selecionado.getFimVigencia();
        carregarEnquadramentosPcs();
    }

    @Override
    public void excluir() {
        if (enquadramentoFuncionalFacade.getEnquadramentoFuncionalLoteItem(selecionado) == null) {
            super.excluir();
        } else {
            FacesUtil.executaJavaScript("exclusaoEnquadramento.show()");
        }
    }

    public void excluirEnquadramentoFuncionalLoteItem() {
        if (validaRegrasParaExcluir()) {
            enquadramentoFuncionalFacade.removerEnquadramentoFuncionalLoteItem(enquadramentoFuncionalFacade.getEnquadramentoFuncionalLoteItem(selecionado));
            super.excluir();
        }

    }

    @Override
    public boolean validaRegrasParaExcluir() {
        int mesInicioVigencia = DataUtil.getMes(selecionado.getInicioVigencia());
        int mesFinalVigencia = DataUtil.getMes(selecionado.getFinalVigencia() != null ? selecionado.getFinalVigencia() : sistemaFacade.getDataOperacao());

        int anoInicioVigencia = DataUtil.getAno(selecionado.getInicioVigencia());
        int anoFinalVigencia = DataUtil.getAno(selecionado.getFinalVigencia() != null ? selecionado.getFinalVigencia() : sistemaFacade.getDataOperacao());

        UsuarioSistema usuario = usuarioSistemaFacade.recuperarAutorizacaoUsuarioRH(sistemaFacade.getUsuarioCorrente().getId());

        boolean validarFolhaDePagamento = true;

        for (AutorizacaoUsuarioRH autorizacaoUsuarioRH : usuario.getAutorizacaoUsuarioRH()) {
            if (TipoAutorizacaoRH.PERMITIR_EXCLUIR_ENQUADRAMENTO_FUNCIONAL_FOLHA_PROCESSADA.equals(autorizacaoUsuarioRH.getTipoAutorizacaoRH())) {
                validarFolhaDePagamento = false;
                break;
            }
        }

        if (validarFolhaDePagamento) {
            FichaFinanceiraFP ficha = fichaFinanceiraFPFacade.buscarFichaFinanceiraFPPorVinculoFPAndPeriodo(selecionado.getContratoServidor(), mesInicioVigencia, mesFinalVigencia, anoInicioVigencia, anoFinalVigencia);
            if (ficha != null) {
                FacesUtil.addOperacaoNaoPermitida("Foi encontrado folha processada para este enquadramento funcional no período de vigência <b>" + ficha.getFolhaDePagamento().getMes() + "/" + ficha.getFolhaDePagamento().getAno() + "</b>. Não é possível excluir este registro!");
                return false;
            }
        }

        return true;
    }

    public void selecionar() {
        selecionado = enquadramentoFuncionalFacade.recuperar(getId());
        EnquadramentoFuncional ef = selecionado;
        planoCargosSalarios = getEnquadreamentoFuncional().getCategoriaPCS().getPlanoCargosSalarios();
        progressaoPCSPai = ef.getProgressaoPCS().getSuperior();
        categoriaPCSfilha = ef.getCategoriaPCS();
        enquadramentoFuncionalVigente = null;
        existeEnquadramentoFuncionalVigente = false;
        cadastroNovo = false;
        provimentoFP = new ProvimentoFP();
    }

    @Override
    public AbstractFacade getFacede() {
        return enquadramentoFuncionalFacade;
    }

    public Boolean validaCampos() {
        Boolean retorno = Boolean.TRUE;
        if (!encerraEnquadramentoFuncionalVigente()) {
            return false;
        }
        if (!cadastroNovo) {
            if (selecionado.getContratoServidor() == null) {
                FacesUtil.addCampoObrigatorio("O campo Servidor é obrigatório.");
                retorno = false;
            }
            if (selecionado.getInicioVigencia() == null) {
                FacesUtil.addCampoObrigatorio("O campo Início de Vigência é obrigatório.");
                retorno = false;
            }
            if (planoCargosSalarios == null) {
                FacesUtil.addCampoObrigatorio("O campo Plano de Cargos e Salários é obrigatório.");
                retorno = false;
            }
            if (categoriaPCSfilha == null) {
                FacesUtil.addCampoObrigatorio("O campo Categoria de Plano de Cargos e Salários é obrigatório.");
                retorno = false;
            }
            if (progressaoPCSPai == null) {
                FacesUtil.addCampoObrigatorio("O campo Progressão de Plano de Cargos e Salários é obrigatório.");
                retorno = false;
            }

            if (selecionado.getProgressaoPCS() == null || selecionado.getProgressaoPCS().getId() == null) {
                FacesUtil.addCampoObrigatorio("O campo Referência é obrigatório.");
                retorno = false;
            }
            if (selecionado.getFinalVigencia() != null) {
                if (selecionado.getFinalVigencia().before(selecionado.getInicioVigencia())) {
                    FacesUtil.addOperacaoNaoPermitida("O Fim da Vigência não pode ser menor que Início da Vigência.");
                    retorno = false;
                }
            }
            if (enquadramento.getVencimentoBase().compareTo(BigDecimal.ZERO) <= 0) {
                FacesUtil.addOperacaoNaoPermitida("O valor da referência deve ser maior que 0 (zero).");
                retorno = false;
            }
        } else {
            if (selecionado.getContratoServidor() == null) {
                FacesUtil.addCampoObrigatorio("O campo Servidor é obrigatório.");
                return false;
            }
            if (selecionado.getInicioVigencia() == null) {
                FacesUtil.addCampoObrigatorio("Preencha o inicio da vigência corretamente.");
                return false;
            }
            if (selecionado.getCategoriaPCS() == null) {
                FacesUtil.addCampoObrigatorio("Selecione uma categoria PCCR.");
                return false;
            }
            if (selecionado.getProgressaoPCS() == null) {
                FacesUtil.addCampoObrigatorio("Selecione uma referência(Letra da Progressão)");
                return false;
            }
            if (selecionado.getFinalVigencia() != null) {
                if (selecionado.getFinalVigencia().before(selecionado.getInicioVigencia())) {
                    FacesUtil.addOperacaoNaoPermitida("O Fim da Vigência não pode ser menor que Início da Vigência.");
                    return false;
                }
            }
            if (getEnquadramentoAtual().compareTo(BigDecimal.ZERO) <= 0) {
                FacesUtil.addOperacaoNaoPermitida("O valor da referência deve ser maior que 0 (zero).");
                return false;
            }
            return true;
        }
        return retorno;
    }

    public List<SelectItem> getEnquadramentoPCS() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (EnquadramentoPCS object : enquadramentoPCSFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getCategoriaPCS() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado.getInicioVigencia() != null && planoCargosSalarios != null) {
            if (planoCargosSalarios.getId() != null) {
                for (CategoriaPCS object : categoriaPCSFacade.recuperaCategoriasNoEnquadramentoFuncionalVigente(planoCargosSalarios, selecionado.getInicioVigencia())) {

                    CategoriaPCS cat = new CategoriaPCS();
                    String nome = "";
                    cat = categoriaPCSFacade.recuperar(object.getId());
                    nome = cat.getDescricao();
                    if (cat.getFilhos().isEmpty()) {
                        while ((cat.getSuperior() != null) && (!cat.equals(cat.getSuperior()))) {
                            cat = cat.getSuperior();
                            nome = cat.getDescricao() + "/" + object.getCodigo() + " - " + nome;
                        }
                        toReturn.add(new SelectItem(object, nome));
                    }
                }
            }
        }

        return Util.ordenaSelectItem(toReturn);
    }

    public String getDescricaoCategoriaPCS() {
        CategoriaPCS cat = new CategoriaPCS();
        String nome = "";
        cat = categoriaPCSFacade.recuperar(selecionado.getCategoriaPCS().getId());
        nome = cat.getDescricao();
        if (cat.getFilhos().isEmpty()) {
            while ((cat.getSuperior() != null) && (!cat.equals(cat.getSuperior()))) {
                cat = cat.getSuperior();
                nome = cat.getDescricao() + "/" + selecionado.getCategoriaPCS().getCodigo() + " - " + nome;
            }
        }
        return nome;
    }

    public List<SelectItem> getProgrecaoPCSTodas() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (planoCargosSalarios != null) {
            if (planoCargosSalarios.getId() != null) {
                List<ProgressaoPCS> listaProgressao = progressaoPCSFacade.buscaProgressoesNoEnquadramentoPCS(planoCargosSalarios);
                UtilRH.ordenarProgressoes(listaProgressao);
                for (ProgressaoPCS object : listaProgressao) {
                    ProgressaoPCS prog = new ProgressaoPCS();
                    String nome = "";
                    prog = progressaoPCSFacade.recuperar(object.getId());
                    nome = prog.getDescricao();
                    if (prog.getFilhos().isEmpty()) {
                        while ((prog.getSuperior() != null) && (!prog.equals(prog.getSuperior()))) {
                            prog = prog.getSuperior();
                            nome = prog.getDescricao() + "/" + nome;
                        }
                        toReturn.add(new SelectItem(object, nome));
                    }
                }
            }
        }
        return toReturn;
    }

    public List<SelectItem> getProgressaoPCS() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado.getInicioVigencia() != null && planoCargosSalarios != null && categoriaPCSfilha != null) {
            if (planoCargosSalarios.getId() != null) {
                List<ProgressaoPCS> listaProgressao = progressaoPCSFacade.getRaizPorPlanoECategoriaVigenteNoEnquadramento(planoCargosSalarios, categoriaPCSfilha, selecionado.getInicioVigencia());
                if (listaProgressao != null && !listaProgressao.isEmpty()) {
                    UtilRH.ordenarProgressoes(listaProgressao);
                    for (ProgressaoPCS object : listaProgressao) {
                        ProgressaoPCS prog = progressaoPCSFacade.recuperar(object.getId());
                        toReturn.add(new SelectItem(prog, (prog.getCodigo() != null ? prog.getCodigo() + " - " : "") + prog.getDescricao()));
                    }
                }
            }
        }
        return toReturn;
    }

    public String getDescricaoProgressaoPCS() {
        String descricao = "";
        ProgressaoPCS prog = selecionado.getProgressaoPCS();
        descricao = (prog.getSuperior().getCodigo() != null ? prog.getSuperior().getCodigo() + " - " : "") + prog.getSuperior().getDescricao();
        return descricao;
    }

    public List<SelectItem> getProgrecaoPCSApenasFilhos() {

        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (progressaoPCSPai != null && planoCargosSalarios != null) {
            if (progressaoPCSPai.getId() != null && planoCargosSalarios.getId() != null) {
                List<ProgressaoPCS> listaProgressao = progressaoPCSFacade.getFilhosDe(progressaoPCSPai, planoCargosSalarios);
                UtilRH.ordenarProgressoes(listaProgressao);
                for (ProgressaoPCS object : listaProgressao) {
                    String nome = "";
                    ProgressaoPCS prog = progressaoPCSFacade.recuperar(object.getId());
                    nome = prog.getDescricao();
                    toReturn.add(new SelectItem(object, nome));
                }
            }
        }
        return toReturn;
    }

    public void limpa() {
        if (selecionado.getInicioVigencia() == null) {
            planoCargosSalarios = null;
        }
        if (planoCargosSalarios == null) {
            categoriaPCSfilha = null;
        }
        if (categoriaPCSfilha == null) {
            progressaoPCSPai = null;
        }
        if (progressaoPCSPai == null) {
            selecionado.setProgressaoPCS(null);
            selecionado.setCategoriaPCS(null);
        }
        if (selecionado.getProgressaoPCS() == null) {
            enquadramento.setVencimentoBase(BigDecimal.ZERO);
        }
    }

    public List<SelectItem> getPlanos() {

        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado.getInicioVigencia() != null) {
            for (PlanoCargosSalarios object : planoCargosSalariosFacade.getPlanosPorQuadro(selecionado.getInicioVigencia())) {
                toReturn.add(new SelectItem(object, object.toString()));
            }
        }
        return toReturn;
    }

    public ConverterGenerico getConverterEnquadramentoPCS() {
        if (converterEnquadramentoPCS == null) {
            converterEnquadramentoPCS = new ConverterGenerico(EnquadramentoPCS.class, enquadramentoPCSFacade);
        }
        return converterEnquadramentoPCS;
    }

    public Converter getConverterContratoServidor() {
        if (converterContratoServidor == null) {
            converterContratoServidor = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoServidor;
    }

    public EnquadramentoPCS getEnquadramento() {
        if (selecionado != null) {
            if (categoriaPCSfilha != null && getEnquadreamentoFuncional().getProgressaoPCS() != null) {
                Date dataParametro = selecionado.getInicioVigencia() == null ? new Date() : selecionado.getInicioVigencia();
                enquadramento = enquadramentoPCSFacade.recuperaValor(categoriaPCSfilha, getEnquadreamentoFuncional().getProgressaoPCS(), dataParametro);
                if (enquadramento != null && enquadramento.getId() != null) {
                    getEnquadreamentoFuncional().setVencimentoBase(enquadramento.getVencimentoBase());
                }
            }
        }
        if (selecionado.getProgressaoPCS() == null) {
            enquadramento.setVencimentoBase(BigDecimal.ZERO);
        }
        if (enquadramento == null || enquadramento.getId() == null) {
            enquadramento = new EnquadramentoPCS();
            enquadramento.setVencimentoBase(BigDecimal.ZERO);
        }
        return enquadramento;
    }

    public List<ContratoFP> completarContratoServidor(String parte) {
        return contratoFPFacade.recuperaContratoMatricula(parte.trim());
    }

    public void excluirSelecionado() {
        EnquadramentoFuncional enq = ((EnquadramentoFuncional) getSelecionado());
        if (folhaDePagamentoFacade.existeFolhaPorContratoData(enq.getContratoServidor(), enq.getInicioVigencia())) {
            FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_INFO, "Impossível excluir ! O Enquadramento Funcional já foi utilizado no cálculo da folha de pagamento !", ""));
        } else {
            excluir();
        }
    }

    public void buscaEnquadramentosPorServidor() {
        ContratoFP cfp = selecionado.getContratoServidor();
        EnquadramentoFuncional enquadramentoFuncionalRecuperado;
        if (operacao == Operacoes.NOVO) {
            enquadramentoFuncionalRecuperado = enquadramentoFuncionalFacade.recuperaEnquadramentoFuncionalVigentePorContratoServidor(cfp, null);
        } else {
            enquadramentoFuncionalRecuperado = enquadramentoFuncionalFacade.recuperaEnquadramentoFuncionalVigentePorContratoServidor(cfp, (EnquadramentoFuncional) selecionado);
        }

        if (enquadramentoFuncionalVigente != null) {
            if (!enquadramentoFuncionalVigente.equals(enquadramentoFuncionalRecuperado)) {
                enquadramentoFuncionalVigente = enquadramentoFuncionalRecuperado;
            }
        } else {
            enquadramentoFuncionalVigente = enquadramentoFuncionalRecuperado;
        }

        //para não acontecer de quando alterar um Enquadramento Funcional,
        //retornar ele mesmo para fechar a vigência
        if (enquadramentoFuncionalVigente != null) {
            if (enquadramentoFuncionalVigente.equals((EnquadramentoFuncional) selecionado)) {
                enquadramentoFuncionalVigente = null;
            }
        }

        existeEnquadramentoFuncionalVigente = (enquadramentoFuncionalVigente != null);

        if (existeEnquadramentoFuncionalVigente) {
            FacesUtil.atualizarComponente("form-enquadramento-vigente");
            FacesUtil.executaJavaScript("enquadramentoVigente.show()");
        }
    }

    public void confirmaFinalVigenciaDoEnquadramentoFuncionalVigente() {
        if (encerraEnquadramentoFuncionalVigente()) {
            FacesUtil.executaJavaScript("enquadramentoVigente.hide()");
        }
    }

    public Boolean encerraEnquadramentoFuncionalVigente() {
        if (podeEncerrarEnquadramentoFuncionalVigente()) {
            existeEnquadramentoFuncionalVigente = false;
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private boolean podeEncerrarEnquadramentoFuncionalVigente() {
        if (enquadramentoFuncionalVigente != null) {
            if (enquadramentoFuncionalVigente.getFinalVigencia() == null) {
                FacesUtil.addCampoObrigatorio("Informe a data final de vigência para encerrar o enquadramento funcional vigente.");
                return false;
            }
            if (enquadramentoFuncionalVigente.getFinalVigencia() != null && enquadramentoFuncionalVigente.getFinalVigencia().before(enquadramentoFuncionalVigente.getInicioVigencia())) {
                FacesUtil.addOperacaoNaoPermitida("A data final de vigência deve ser posterior a data inicial da vigência.");
                return false;
            }
        }
        return true;
    }

    @Override
    public void cancelar() {
        enquadramentoFuncionalVigente = null;
        existeEnquadramentoFuncionalVigente = false;
        redireciona();
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public void setContratoFP(ContratoFP c) {
        ((EnquadramentoFuncional) selecionado).setContratoServidor(c);
    }

    public ProgressaoPCS getProgressaoPCSPai() {
        return progressaoPCSPai;
    }

    public void setProgressaoPCSPai(ProgressaoPCS progressaoPCSPai) {
        this.progressaoPCSPai = progressaoPCSPai;
    }

    public CategoriaPCS getCategoriaPCSfilha() {
        return categoriaPCSfilha;
    }

    public void setCategoriaPCSfilha(CategoriaPCS categoriaPCSfilha) {
        this.categoriaPCSfilha = categoriaPCSfilha;
    }

    public List<String> getEnquadramentosPcsDescricao() {
        return enquadramentosPcsDescricao;
    }

    public void setEnquadramentosPcsDescricao(List<String> enquadramentosPcsDescricao) {
        this.enquadramentosPcsDescricao = enquadramentosPcsDescricao;
    }

    public List<CargoConfianca> getCargosComissoes() {
        if (getEnquadreamentoFuncional() != null && getEnquadreamentoFuncional().getContratoServidor() != null) {
            List<CargoConfianca> listaRetorno = new ArrayList<>();
            for (CargoConfianca c : cargoConfiancaFacade.recuperaCargoConfiancaContratoSemVigencia(getEnquadreamentoFuncional().getContratoServidor())) {
                c = cargoConfiancaFacade.recuperar(c.getId());
                EnquadramentoCC ecc = c.getEnquadramentoCCVigente();
                if (ecc != null) {
                    EnquadramentoPCS epcs = enquadramentoPCSFacade.recuperaUltimoValor(ecc.getCategoriaPCS(), ecc.getProgressaoPCS(), c.getInicioVigencia(), c.getFinalVigencia());
                    if (epcs != null) {
                        c.setValor(epcs.getVencimentoBase());
                    }
                }
                listaRetorno.add(c);
            }
            return listaRetorno;
        }
        return null;
    }

    public List<FuncaoGratificada> getFuncoesGratificadas() {
        if (getEnquadreamentoFuncional().getContratoServidor() != null) {
            List<FuncaoGratificada> listaRetorno = new ArrayList<>();
            for (FuncaoGratificada c : funcaoGratificadaFacade.recuperaFuncaoGratificadaContratoSemVigencia(getEnquadreamentoFuncional().getContratoServidor())) {
                c = funcaoGratificadaFacade.recuperar(c.getId());
                EnquadramentoFG ecc = c.getEnquadramentoCCVigente();
                if (ecc != null) {
                    EnquadramentoPCS epcs = enquadramentoPCSFacade.recuperaUltimoValor(ecc.getCategoriaPCS(), ecc.getProgressaoPCS(), c.getInicioVigencia(), c.getFinalVigencia());
                    if (epcs != null) {
                        c.setValor(epcs.getVencimentoBase());
                    }
                }
                listaRetorno.add(c);
            }
            return listaRetorno;

        }
        return null;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/enquadramento-funcional/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    public List<SelectItem> getCategoriaPCSNovo() {
        cadastroNovo = true;
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado.getInicioVigencia() != null && selecionado.getContratoServidor() != null) {
            for (CategoriaPCS object : categoriaPCSFacade.buscarCategoriasNoEnquadramentoFuncionalPorCargoAndDataVigencia(selecionado.getContratoServidor().getCargo(), selecionado.getInicioVigencia())) {
                CategoriaPCS cat = new CategoriaPCS();
                String nome = "";
                cat = categoriaPCSFacade.recuperar(object.getId());
                nome = cat.getDescricao();
                if (!cat.getFilhos().isEmpty()) {
                    for (CategoriaPCS categoriaPCS : cat.getFilhos()) {
                        nome = object.getDescricao() + "/" + categoriaPCS.getDescricao();
                        toReturn.add(new SelectItem(categoriaPCS, nome));
                    }
                }

            }
        }
        return toReturn;
    }

    public List<SelectItem> getProgressaoPCSApenasFilhosNovo() {
        cadastroNovo = true;
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado.getCategoriaPCS() != null && selecionado.getInicioVigencia() != null) {
            List<ProgressaoPCS> listaProgressao = progressaoPCSFacade.buscaProgressoesNoEnquadramentoPCSPorCategoriaSuperior(selecionado.getCategoriaPCS().getSuperior(), selecionado.getInicioVigencia());
            UtilRH.ordenarProgressoes(listaProgressao);
            for (ProgressaoPCS object : listaProgressao) {
                String nome = "";
                ProgressaoPCS prog = progressaoPCSFacade.recuperar(object.getId());
                nome = prog.getDescricao();
                toReturn.add(new SelectItem(object, nome));
            }
        }
        return toReturn;
    }

    public BigDecimal getEnquadramentoNaVigencia() {
        enquadramento = null;
        cadastroNovo = true;
        BigDecimal valorAtual = BigDecimal.ZERO;
        if (selecionado.getCategoriaPCS() != null && selecionado.getProgressaoPCS() != null && selecionado.getInicioVigencia() != null) {
            EnquadramentoPCS valor = enquadramentoPCSFacade.recuperaValor(selecionado.getCategoriaPCS(), selecionado.getProgressaoPCS(), selecionado.getInicioVigencia());
            if (valor != null) {
                valorAtual = valor.getVencimentoBase();
            }
        }
        return valorAtual;
    }

    public BigDecimal getEnquadramentoAtual() {
        BigDecimal valorAtual = BigDecimal.ZERO;
        if (selecionado.getCategoriaPCS() != null && selecionado.getProgressaoPCS() != null && selecionado.getInicioVigencia() != null) {
            EnquadramentoPCS valor = enquadramentoPCSFacade.recuperaValor(selecionado.getCategoriaPCS(), selecionado.getProgressaoPCS(), UtilRH.getDataOperacao());
            if (valor != null) {
                valorAtual = valor.getVencimentoBase();
            }
        }
        return valorAtual;
    }

    public void cancelaServidor() {
        selecionado.setContratoServidor(null);
    }

    public boolean isEditar() {
        return Operacoes.EDITAR.equals(operacao);
    }

    public boolean temCargoComissao() {
        try {
            return getCargosComissoes() != null && !getCargosComissoes().isEmpty();
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean temFuncaoGratificante() {
        try {
            return getFuncoesGratificadas() != null && !getFuncoesGratificadas().isEmpty();
        } catch (Exception ex) {
            return false;
        }
    }

    public Set<VinculoFP> getVinculoFPEnquadramentos() {
        return vinculoFPEnquadramentos.keySet();
    }

    @URLActions(actions = {
        @URLAction(mappingId = "enqudramentoFuncional-correcao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    })
    public void novoItemParaExclusao() {
        DateTime dataCadastro = new DateTime(2016, 2, 23, 0, 0);
        DateTime inicioVigencia = new DateTime(2016, 2, 22, 0, 0);
        this.inicioVigencia = inicioVigencia.toDate();
        this.dataCadastro = dataCadastro.toDate();
        vinculoFPEnquadramentos = Maps.newLinkedHashMap();
    }

    public void processarItensParaExlusao() {
        try {
            validaCamposObrigatorios();
            vinculoFPEnquadramentos = enquadramentoFuncionalFacade.processarItensParaExclusao(dataCadastro, inicioVigencia);
        } catch (ValidacaoException va) {
            FacesUtil.printAllFacesMessages(va.getMensagens());
        }

    }


    private void validaCamposObrigatorios() {
        ValidacaoException va = new ValidacaoException();
        if (inicioVigencia == null) {
            va.adicionarMensagemDeCampoObrigatorio("Inicio de Vigência é Obrigátório.");
        }
        if (dataCadastro == null) {
            va.adicionarMensagemDeCampoObrigatorio("Data de Cadastro é Obrigátório.");
        }
        if (va.temMensagens()) {
            throw va;
        }
    }

    public boolean getPodeRendedizar() {
        HashSet users = Sets.newHashSet();
        users.add("mailson");
        return users.contains(enquadramentoFuncionalFacade.getSistemaFacade().getUsuarioCorrente().getLogin());
    }

    public void deletarRegistros() {
        enquadramentoFuncionalFacade.deletarRegistros(vinculoFPEnquadramentos);
        FacesUtil.addInfo("Ok", "Ok");
    }

    public List<SelectItem> tiposProvimentos() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        List<TipoProvimento> progressaoAndPromocao = enquadramentoFuncionalFacade.getTipoProvimentoFacade().buscarProvimentosProgressaoAndPromocao();
        for (TipoProvimento tipoProvimento : progressaoAndPromocao) {
            toReturn.add(new SelectItem(tipoProvimento, tipoProvimento.getDescricao()));
        }
        return toReturn;
    }

    public void criarProvimentoFP() {
        if (provimentoFP.getTipoProvimento() != null) {
            provimentoFP.setContratoFP(selecionado.getContratoServidor());
            provimentoFP.setVinculoFP(selecionado.getContratoServidor());
            provimentoFP.setCargo(selecionado.getContratoServidor().getCargo());
            provimentoFP.setAtoLegal(null);
            provimentoFP.setDataProvimento(selecionado.getInicioVigencia());
            provimentoFP.setObservacao(null);
            provimentoFP.setSequencia(enquadramentoFuncionalFacade.getProvimentoFPFacade().geraSequenciaPorVinculoFP(selecionado.getContratoServidor()));
        }
    }

    public ProvimentoFP getProvimentoFP() {
        return provimentoFP;
    }

    public void setProvimentoFP(ProvimentoFP provimentoFP) {
        this.provimentoFP = provimentoFP;
    }

    public List<AtoLegal> completarAtosLegais(String parte) {
        return atoLegalFacade.listaFiltrandoAtoLegal(parte);
    }

    public void carregarEnquadramentosPcs() {
        enquadramentosPcsDescricao = Lists.newArrayList();
        selecionado.setEnquadramentoPCSList(enquadramentoPCSFacade.recuperaTodosEnquadramentos(selecionado.getCategoriaPCS(), selecionado.getProgressaoPCS()));
        Long idUltimoEnquadramento = selecionado.getEnquadramentoPCSList().get(selecionado.getEnquadramentoPCSList().size() - 1).getId();
        for (EnquadramentoPCS enq : selecionado.getEnquadramentoPCSList()) {
            boolean vigente = false;
            String descricao = DataUtil.getDataFormatada(enq.getInicioVigencia()) + " à ";
            if (enq.getFinalVigencia() == null) {
                descricao += "Hoje : ";
                vigente = true;
            } else {
                if (sistemaFacade.getDataOperacao().before(enq.getFinalVigencia()) || sistemaFacade.getDataOperacao().equals(enq.getFinalVigencia())) {
                    vigente = true;
                }
                descricao += DataUtil.getDataFormatada(enq.getFinalVigencia()) + " : ";
            }
            descricao += Util.formatarValor(enq.getVencimentoBase()) + " ";
            descricao += idUltimoEnquadramento.equals(enq.getId()) ? "Enquadramento " : "Reajuste ";
            descricao += vigente ? "(Atualmente vigente)" : "(Não vigente)";
            enquadramentosPcsDescricao.add(descricao);
        }
    }
}
