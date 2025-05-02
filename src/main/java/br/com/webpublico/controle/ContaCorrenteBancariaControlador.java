/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModalidadeConta;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoConta;
import br.com.webpublico.enums.TipoPessoa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContaCorrenteBancariaFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author terminal1
 */
@ManagedBean(name = "contaCorrenteBancariaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-conta-corrente-bancaria", pattern = "/conta-corrente-bancaria/novo/", viewId = "/faces/tributario/cadastromunicipal/contacorrentebancaria/edita.xhtml"),
    @URLMapping(id = "editar-conta-corrente-bancaria", pattern = "/conta-corrente-bancaria/editar/#{contaCorrenteBancariaControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/contacorrentebancaria/edita.xhtml"),
    @URLMapping(id = "ver-conta-corrente-bancaria", pattern = "/conta-corrente-bancaria/ver/#{contaCorrenteBancariaControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/contacorrentebancaria/visualizar.xhtml"),
    @URLMapping(id = "listar-conta-corrente-bancaria", pattern = "/conta-corrente-bancaria/listar/", viewId = "/faces/tributario/cadastromunicipal/contacorrentebancaria/lista.xhtml")
})

public class ContaCorrenteBancariaControlador extends PrettyControlador<ContaCorrenteBancaria> implements Serializable, CRUD {

    @EJB
    private ContaCorrenteBancariaFacade contaCorrenteBancariaFacade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private FormContaCorrenteBancaria form;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterAgencia;
    private UsuarioSistema usuarioSistema;
    private String justificativa;

    public ContaCorrenteBancariaControlador() {
        super(ContaCorrenteBancaria.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return contaCorrenteBancariaFacade;
    }

    public ContaCorrenteBancariaFacade getFacade() {
        return contaCorrenteBancariaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/conta-corrente-bancaria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "ver-conta-corrente-bancaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditaVer();
    }

    @URLAction(mappingId = "editar-conta-corrente-bancaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditaVer();
    }


    @URLAction(mappingId = "nova-conta-corrente-bancaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        form = (FormContaCorrenteBancaria) Web.pegaDaSessao(FormContaCorrenteBancaria.class);
        if (form == null) {
            novoGeral();
        }
        paramentrosIniciais();
        if (isSessao()) {
            form.pessoa = (Pessoa) Web.pegaDaSessao(PessoaFisica.class);
            adicionaPessoaSessaoNaLista();
        }
    }

    private void novoGeral() {
        form = new FormContaCorrenteBancaria();
        form.setSelecionado(this.selecionado);
        form.selecionado = new ContaCorrenteBancaria();
        form.selecionado.setSituacao(SituacaoConta.ATIVO);
        form.selecionado.setModalidadeConta(ModalidadeConta.CONTA_CORRENTE);

    }

    public void paramentrosIniciais() {
        form.setTipoPessoa(TipoPessoa.FISICA);
        form.selecionado.setPessoas(new ArrayList<Pessoa>());
        form.selecionado.setSituacao(SituacaoConta.ATIVO);
        form.selecionado.setModalidadeConta(ModalidadeConta.CONTA_CORRENTE);
        form.selecionado.setContaCorrenteBancPessoas(new ArrayList<ContaCorrenteBancPessoa>());
        form.contaCorrenteBancPessoa = new ContaCorrenteBancPessoa();
        usuarioSistema = sistemaControlador.getUsuarioCorrente();
        form.selecionado.setContaConjunta(Boolean.FALSE);
    }

    public void recuperarEditaVer() {
        form = new FormContaCorrenteBancaria();
        form.selecionado = contaCorrenteBancariaFacade.recuperar(super.getId());
        if (!form.selecionado.getContaCorrenteBancPessoas().isEmpty()) {
            if (form.selecionado.getContaCorrenteBancPessoas().get(0).getPessoa() instanceof PessoaFisica) {
                form.setTipoPessoa(TipoPessoa.FISICA);
            } else {
                form.setTipoPessoa(TipoPessoa.JURIDICA);
            }
        }
        form.setBanco(form.selecionado.getAgencia().getBanco());
        form.contaCorrenteBancPessoa = new ContaCorrenteBancPessoa();
        usuarioSistema = sistemaControlador.getUsuarioCorrente();
    }

    public boolean getCarregaTipoPessoa() {
        if (operacao.equals(Operacoes.EDITAR)) {
            return true;
        } else {
            if (form.tipoPessoa == null) {
                return false;
            } else {
                return true;
            }
        }
    }

    public boolean isCorrentistaPF() {
        if (form != null) {
            return TipoPessoa.FISICA.equals(form.tipoPessoa);
        } else {
            return false;
        }
    }

    public boolean isBancoCaixa() {
        if (form != null && form.banco != null) {
            return "104".equals(form.banco.getNumeroBanco());
        } else {
            return false;
        }
    }

    public boolean isBancoBrasil() {
        if (form != null && form.banco != null) {
            return "001".equals(form.banco.getNumeroBanco());
        } else {
            return false;
        }
    }

    public boolean isCorrentistaPJ() {
        return TipoPessoa.JURIDICA.equals(form.tipoPessoa);
    }

    public void limparListaCorrentistas() {
        form.selecionado.setPessoas(new ArrayList<Pessoa>());
        form.selecionado.setContaCorrenteBancPessoas(new ArrayList<ContaCorrenteBancPessoa>());
        form.pessoa = new PessoaFisica();
        form.pessoa = new PessoaJuridica();
    }

    public void limparAgenciaAndTipoConta() {
        form.selecionado.setAgencia(null);
        form.selecionado.setModalidadeConta(null);
    }

    public void novoItem() {
        form.selecionado.getPessoas().add(form.pessoa);
        form.pessoa = null;
    }

    @Override
    public void salvar() {
        if (Util.validaCampos(form.selecionado)) {
            try {
                validarCorrentistasConta();
                form.selecionado.setDataRegistro(sistemaControlador.getDataOperacao());
                contaCorrenteBancariaFacade.salvar(form.selecionado);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso"));
                Web.poeNaSessao(form.selecionado);
                redireciona();

            } catch (ValidacaoException ex) {
                FacesUtil.printAllFacesMessages(ex.getMensagens());
            } catch (Exception ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Operação não Realizada! ", " Erro ao salvar: " + ex.getMessage()));
            }
        }
    }

    public void removeItem(ActionEvent evento) {
        Pessoa pe = (Pessoa) evento.getComponent().getAttributes().get("removeItem");
        form.selecionado.getPessoas().remove(pe);
    }

    public List<SelectItem> getSituacaoConta() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (SituacaoConta object : SituacaoConta.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoCorrentista() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoPessoa object : TipoPessoa.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getModalidades() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        toReturn.add(new SelectItem(ModalidadeConta.CONTA_CORRENTE, ModalidadeConta.CONTA_CORRENTE.getDescricao()));
        toReturn.add(new SelectItem(ModalidadeConta.CONTA_POUPANCA, ModalidadeConta.CONTA_POUPANCA.getDescricao()));
        if (isBancoCaixa() || isBancoBrasil()) {
            toReturn.add(new SelectItem(ModalidadeConta.CONTA_CAIXA_FACIL, ModalidadeConta.CONTA_CAIXA_FACIL.getDescricao()));
        }
        if (isCorrentistaPF()) {
            toReturn.add(new SelectItem(ModalidadeConta.CONTA_SALARIO, ModalidadeConta.CONTA_SALARIO.getDescricao()));
        } else {
            toReturn.add(new SelectItem(ModalidadeConta.ENTIDADES_PUBLICAS, ModalidadeConta.ENTIDADES_PUBLICAS.getDescricao()));
            toReturn.add(new SelectItem(ModalidadeConta.CONTA_POUPANCA_PESSOA_JURIDICA, ModalidadeConta.CONTA_POUPANCA_PESSOA_JURIDICA.getDescricao()));
        }
        return toReturn;
    }

    public Converter getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, contaCorrenteBancariaFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public List<Pessoa> completaPessoa(String parte) {
        if (isCorrentistaPF()) {
            return contaCorrenteBancariaFacade.getPessoaFacade().listaPessoasFisicas(parte.trim());
        } else {
            return contaCorrenteBancariaFacade.getPessoaFacade().listaPessoasJuridicas(parte.trim());
        }
    }

    public Converter getConverterAgencia() {
        if (converterAgencia == null) {
            converterAgencia = new ConverterAutoComplete(Agencia.class, contaCorrenteBancariaFacade.getAgenciaFacade());
        }
        return converterAgencia;
    }

    public List<Agencia> completarAgencias(String parte) {
        return contaCorrenteBancariaFacade.getAgenciaFacade().listaFiltrandoPorBanco(parte.trim(), form.getBanco());
    }

    public List<Banco> completarBancos(String filtro) {
        return contaCorrenteBancariaFacade.getBancoFacade().listaBancoPorCodigoOuNome(filtro.trim());
    }

    private void validarJustificativa() {
        ValidacaoException ve = new ValidacaoException();
        if (form.selecionado.getContaCorrenteBancPessoas().size() > 1) {
            if (Strings.isNullOrEmpty(getJustificativa())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A justificativa deve ser informada.");
                ve.lancarException();
            }
            if (getJustificativa().length() < 20) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A justificativa deve ser informada e deve conter ao menos 20 caracteres.");
            }
        }
        ve.lancarException();
    }

    public void validarAndAdicionarConta() {
        try {
            validarCorrentistasConta();
            if (form.selecionado.getContaCorrenteBancPessoas().size() > 1 && Strings.isNullOrEmpty(form.contaCorrenteBancPessoa.getJustificativa())) {
                FacesUtil.executaJavaScript("dialogJustificativa.show()");
            } else {
                adicionaContaCorrenteBancPessoa();
            }
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ", e);
        }
    }

    public void adicionaContaCorrenteBancPessoa() {
        try {
            validarJustificativa();
            if (validaAdicionarCorrentista(form.pessoa)) {
                form.contaCorrenteBancPessoa.setPessoa(form.pessoa);
                form.contaCorrenteBancPessoa.setContaCorrenteBancaria(form.selecionado);
                form.contaCorrenteBancPessoa.setDataRegistro(sistemaControlador.getDataOperacao());
                form.contaCorrenteBancPessoa.setJustificativa(getJustificativa());
                form.contaCorrenteBancPessoa.setResponsavel(sistemaControlador.getUsuarioCorrente());

                form.selecionado.getContaCorrenteBancPessoas().add(form.contaCorrenteBancPessoa);
                form.contaCorrenteBancPessoa = new ContaCorrenteBancPessoa();
                form.pessoa = new PessoaJuridica();
                form.pessoa = new PessoaFisica();
                setJustificativa(null);
                FacesUtil.executaJavaScript("dialogJustificativa.hide()");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCorrentistasConta() {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoRHFacade.recuperarConfiguracaoRHVigente().getConfiguracaoCreditoSalario().getPermMultCorrentContConjunta() && !form.selecionado.getContaConjunta() && form.selecionado.getContaCorrenteBancPessoas().size() >= 1) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitida a inclusão de mais de um correntista para contas que não são conjuntas.");
        }
        ve.lancarException();
    }

    private boolean validaAdicionarCorrentista(Pessoa pessoa) {
        if (form.pessoa == null && isCorrentistaPF()) {
            FacesUtil.addCampoObrigatorio(" O campo Pessoa Física é obrigatório para adicionar.");
            return false;
        }
        if (form.pessoa == null && isCorrentistaPJ()) {
            FacesUtil.addCampoObrigatorio(" O campo Pessoa Jurídica é obrigatório para adicionar.");
            return false;
        }
        for (ContaCorrenteBancPessoa conta : form.selecionado.getContaCorrenteBancPessoas()) {
            if (conta.getPessoa().getId().equals(pessoa.getId())) {
                FacesUtil.addOperacaoNaoPermitida(" A Pessoa: " + form.pessoa + ", já foi adicionada na lista.");
                return false;
            }
        }
        return true;
    }

    @Override
    public String getCaminhoOrigem() {
        if (form.selecionado.getId() == null) {
            return getCaminhoPadrao() + "novo/";
        }
        return getCaminhoPadrao() + "editar/" + form.selecionado.getId();
    }

    public void removeContaCorrenteBancPessoa(ActionEvent evento) {
        try {
            ContaCorrenteBancPessoa c = (ContaCorrenteBancPessoa) evento.getComponent().getAttributes().get("removeItem");
            validarExclusaoConta(c);
            form.selecionado.getContaCorrenteBancPessoas().remove(c);
            form.pessoa = new PessoaJuridica();
            form.pessoa = new PessoaFisica();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao remover correntista", e);
        }
    }

    public void validarExclusaoConta(ContaCorrenteBancPessoa conta) {
        ValidacaoException ve = new ValidacaoException();
        if (conta.getPrincipal()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível a exclusão da conta principal da pessoa.");
        }
        ve.lancarException();
    }

    public void contaCorrenteExistente() {
        if ((form.selecionado.getAgencia() != null)
            && ((form.selecionado.getAgencia().getId() != null)
            && ((form.selecionado.getNumeroConta() != null)
            && !((form.selecionado.getNumeroConta().trim().isEmpty()))))) {
            form.selecionado = contaCorrenteBancariaFacade.contaCorrenteExistente(form.selecionado);
            if (form.selecionado != null && form.selecionado.getId() != null) {
                List<Pessoa> pessoas = contaCorrenteBancariaFacade.getContaCorrenteBancPessoaFacade().listaPorContaCorrenteBancaria(form.selecionado);
                if (!pessoas.isEmpty()) {
                    form.pessoa = pessoas.get(0);
                    RequestContext.getCurrentInstance().execute("dialogContaExistente.show()");
                }
            }
        }
    }

    public void adicionaPessoaSessaoNaLista() {
        form.tipoPessoa = TipoPessoa.FISICA;
        adicionaContaCorrenteBancPessoa();
        this.form.pessoa = null;
    }

    public Boolean desabilitarContaConjunta() {
        return !ModalidadeConta.CONTA_CORRENTE.equals(form.selecionado.getModalidadeConta()) && !ModalidadeConta.CONTA_POUPANCA.equals(form.selecionado.getModalidadeConta());
    }

    public void verificarContaConjunta() {
        if (desabilitarContaConjunta()) {
            form.selecionado.setContaConjunta(Boolean.FALSE);
        }
    }

    public class FormContaCorrenteBancaria {

        private ContaCorrenteBancaria selecionado;
        private Pessoa pessoa;
        private TipoPessoa tipoPessoa;
        private ContaCorrenteBancPessoa contaCorrenteBancPessoa;
        private ContaCorrenteBancaria contaCorrenteBancaria;
        private Banco banco;

        public ContaCorrenteBancaria getSelecionado() {
            return selecionado;
        }

        public void setSelecionado(ContaCorrenteBancaria selecionado) {
            this.selecionado = selecionado;
        }

        public Pessoa getPessoa() {
            return pessoa;
        }

        public void setPessoa(Pessoa pessoa) {
            this.pessoa = pessoa;
        }

        public TipoPessoa getTipoPessoa() {
            return tipoPessoa;
        }

        public void setTipoPessoa(TipoPessoa tipoPessoa) {
            this.tipoPessoa = tipoPessoa;
        }

        public ContaCorrenteBancPessoa getContaCorrenteBancPessoa() {
            return contaCorrenteBancPessoa;
        }

        public void setContaCorrenteBancPessoa(ContaCorrenteBancPessoa contaCorrenteBancPessoa) {
            this.contaCorrenteBancPessoa = contaCorrenteBancPessoa;
        }

        public ContaCorrenteBancaria getContaCorrenteBancaria() {
            return contaCorrenteBancaria;
        }

        public void setContaCorrenteBancaria(ContaCorrenteBancaria contaCorrenteBancaria) {
            this.contaCorrenteBancaria = contaCorrenteBancaria;
        }

        public Banco getBanco() {
            return banco;
        }

        public void setBanco(Banco banco) {
            this.banco = banco;
        }
    }

    public FormContaCorrenteBancaria getForm() {
        return form;
    }

    public void setForm(FormContaCorrenteBancaria form) {
        this.form = form;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }
}
