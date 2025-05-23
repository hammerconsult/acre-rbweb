/*
 * Codigo gerado automaticamente em Thu Feb 16 10:29:01 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "beneficiarioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoBeneficiario", pattern = "/beneficiario/novo/", viewId = "/faces/rh/administracaodepagamento/beneficiario/edita.xhtml"),
    @URLMapping(id = "editarBeneficiario", pattern = "/beneficiario/editar/#{beneficiarioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/beneficiario/edita.xhtml"),
    @URLMapping(id = "listarBeneficiario", pattern = "/beneficiario/listar/", viewId = "/faces/rh/administracaodepagamento/beneficiario/lista.xhtml"),
    @URLMapping(id = "verBeneficiario", pattern = "/beneficiario/ver/#{beneficiarioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/beneficiario/visualizar.xhtml")
})
public class BeneficiarioControlador extends PrettyControlador<Beneficiario> implements Serializable, CRUD {

    @EJB
    private BeneficiarioFacade beneficiarioFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    private ConverterGenerico converterUnidadeOrganizacional;
    @EJB
    private MatriculaFPFacade matriculaFPFacade;
    private ConverterAutoComplete converterMatriculaFP;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    private ConverterAutoComplete converterPessoaFisicaServidor;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterContratoFP;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    private ConverterAutoComplete converterAtoLegal;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private PessoaFisica pessoaFisicaSelecionada;
    private MatriculaFP matriculaSelecionado;
    private String numeroSelecionado;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    private ItemBeneficiario itemBeneficiario;
    @EJB
    private ParametroCalcIndenizacaoFacade parametroCalcIndenizacaoFacade;
    private ConverterAutoComplete converterParametroCalcIndenizacao;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private MoneyConverter moneyConverter;
    @EJB
    private ValorReferenciaFPFacade valorReferenciaFPFacade;
    @EJB
    private ContaCorrenteBancPessoaFacade contaCorrenteBancPessoaFacade;

    public BeneficiarioControlador() {
        metadata = new EntidadeMetaData(Beneficiario.class);
        novoItemBeneficiario();
    }

    public BeneficiarioFacade getFacade() {
        return beneficiarioFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return beneficiarioFacade;
    }

    public PessoaFisica getPessoaFisicaSelecionada() {
        return pessoaFisicaSelecionada;
    }

    public void setPessoaFisicaSelecionada(PessoaFisica pessoaFisicaSelecionada) {
        this.pessoaFisicaSelecionada = pessoaFisicaSelecionada;
    }

    public ItemBeneficiario getItemBeneficiario() {
        return itemBeneficiario;
    }

    public void setItemBeneficiario(ItemBeneficiario itemBeneficiario) {
        this.itemBeneficiario = itemBeneficiario;
    }

    public List<SelectItem> getUnidadeOrganizacional() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (UnidadeOrganizacional object : unidadeOrganizacionalFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterUnidadeOrganizacional() {
        if (converterUnidadeOrganizacional == null) {
            converterUnidadeOrganizacional = new ConverterGenerico(UnidadeOrganizacional.class, unidadeOrganizacionalFacade);
        }
        return converterUnidadeOrganizacional;
    }

    public Converter getConverterMatriculaFP() {
        if (converterMatriculaFP == null) {
            converterMatriculaFP = new ConverterAutoComplete(MatriculaFP.class, matriculaFPFacade);
        }
        return converterMatriculaFP;
    }

    public Converter getConverterPessoaFisicaServidor() {
        if (converterPessoaFisicaServidor == null) {
            converterPessoaFisicaServidor = new ConverterAutoComplete(PessoaFisica.class, pessoaFisicaFacade);
        }
        return converterPessoaFisicaServidor;
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public Converter getConverterParametroCalcIndenizacao() {
        if (converterParametroCalcIndenizacao == null) {
            converterParametroCalcIndenizacao = new ConverterAutoComplete(ParametroCalcIndenizacao.class, parametroCalcIndenizacaoFacade);
        }
        return converterParametroCalcIndenizacao;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        return contratoFPFacade.buscaContratoVigenteFiltrandoAtributosMatriculaFP(parte.trim());
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoAtoLegal(parte.trim());
    }

    public List<PessoaFisica> completaPessoaFisica(String parte) {
        return pessoaFisicaFacade.listaFiltrando(parte.trim(), "nome", "cpf");
    }

    public List<ParametroCalcIndenizacao> completaParametroCalcIndenizacao(String parte) {
        return parametroCalcIndenizacaoFacade.listaFiltrando(parte.trim(), "codigo", "descricao");
    }

    public void recuperarMatriculas(SelectEvent item) {
        pessoaFisicaSelecionada = (PessoaFisica) item.getObject();
    }

    public void selecionarMatricula(SelectEvent evt) {
        Beneficiario beneficiario = ((Beneficiario) selecionado);
        MatriculaFP matricula = (MatriculaFP) evt.getObject();
        beneficiario.setPessoaFisicaServidor(matricula.getPessoa());

        if ((beneficiario.getId() == null) && (matricula != null)) {
            beneficiario.setNumero(contratoFPFacade.retornaCodigo(matricula));
        } else if (!operacao.equals(Operacoes.NOVO)) {
            if (matriculaSelecionado.equals(beneficiario.getMatriculaFP())) {
                beneficiario.setNumero(numeroSelecionado);
            } else {
                beneficiario.setNumero(contratoFPFacade.retornaCodigo(matricula));
            }
        }

        selecionado = beneficiario;

    }

    public void setaHierarquiaOrganizacional(SelectEvent item) {
        setHierarquiaOrganizacionalSelecionada((HierarquiaOrganizacional) item.getObject());
    }

    @Override
    public String getCaminhoPadrao() {
        return "/beneficiario/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoBeneficiario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        definirSessao();
        carregarDaSessao();

        PessoaFisica pessoaFisica = (PessoaFisica) Web.pegaDaSessao(PessoaFisica.class);
        if (pessoaFisica != null) {
            selecionado.setPessoaFisicaServidor(pessoaFisica);
        } else {
            pessoaFisica = new PessoaFisica();
        }

        MatriculaFP matriculaFP = (MatriculaFP) Web.pegaDaSessao(MatriculaFP.class);
        if (matriculaFP != null) {
            selecionado.setMatriculaFP(matriculaFP);
        } else {
            matriculaFP = new MatriculaFP();
        }

        ContratoFP contratoFP = (ContratoFP) Web.pegaDaSessao(ContratoFP.class);
        if (contratoFP != null) {
            selecionado.setContratoFP(contratoFP);
        } else {
            contratoFP = new ContratoFP();
        }

        AtoLegal atoLegal = (AtoLegal) Web.pegaDaSessao(AtoLegal.class);
        if (atoLegal != null) {
            selecionado.setAtoLegal(atoLegal);
        } else {
            atoLegal = new AtoLegal();
        }

        ItemBeneficiario itemBeneficiario = (ItemBeneficiario) Web.pegaDaSessao(ItemBeneficiario.class);
        if (itemBeneficiario != null) {
            this.itemBeneficiario = itemBeneficiario;
            ParametroCalcIndenizacao parametroCalcIndenizacao = (ParametroCalcIndenizacao) Web.pegaDaSessao(ParametroCalcIndenizacao.class);
            if (parametroCalcIndenizacao != null) {
                this.itemBeneficiario.setParametroCalcIndenizacao(parametroCalcIndenizacao);
            }
        } else {
            this.itemBeneficiario = new ItemBeneficiario();
        }

        if (selecionado == null) {
            super.novo();
            Beneficiario b = (Beneficiario) selecionado;
            b.setItensBeneficiarios(new ArrayList<ItemBeneficiario>());
        }

    }

    @Override
    public boolean validaRegrasEspecificas() {
        boolean toReturn = true;
        if (selecionado.getMatriculaFP() == null || selecionado.getMatriculaFP().getId() == null) {
            FacesUtil.addCampoObrigatorio("O campo 'Matrícula' deve ser informado.");
            toReturn = false;
        }
        if (hierarquiaOrganizacionalSelecionada == null || hierarquiaOrganizacionalSelecionada.getId() == null) {
            FacesUtil.addCampoObrigatorio("O campo 'Hierarquia Organizacional' deve ser informado.");
            toReturn = false;
        }
        return toReturn;
    }

    @Override
    public void salvar() {
        if (hierarquiaOrganizacionalSelecionada != null) {
            selecionado.setUnidadeOrganizacional(getHierarquiaOrganizacionalSelecionada().getSubordinada());
        }
        try {
            validarCampos();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica eg) {
            FacesUtil.addError("Atenção", "Erro ao salvar o registro: " + eg);
        } catch (Exception ex) {
            FacesUtil.addError("Atenção", "Erro ao salvar o registro: " + ex);
        }
    }

    @Override
    @URLAction(mappingId = "editarBeneficiario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        selecionar();
        PessoaFisica pessoaFisica = (PessoaFisica) Web.pegaDaSessao(PessoaFisica.class);
        if (pessoaFisica != null) {
            selecionado.setPessoaFisicaServidor(pessoaFisica);
        } else {
            pessoaFisica = new PessoaFisica();
        }

        MatriculaFP matriculaFP = (MatriculaFP) Web.pegaDaSessao(MatriculaFP.class);
        if (matriculaFP != null) {
            selecionado.setMatriculaFP(matriculaFP);
        } else {
            matriculaFP = new MatriculaFP();
        }

        ContratoFP contratoFP = (ContratoFP) Web.pegaDaSessao(ContratoFP.class);
        if (contratoFP != null) {
            selecionado.setContratoFP(contratoFP);
        } else {
            contratoFP = new ContratoFP();
        }

        AtoLegal atoLegal = (AtoLegal) Web.pegaDaSessao(AtoLegal.class);
        if (atoLegal != null) {
            selecionado.setAtoLegal(atoLegal);
        } else {
            atoLegal = new AtoLegal();
        }

        ItemBeneficiario itemBeneficiario = (ItemBeneficiario) Web.pegaDaSessao(ItemBeneficiario.class);
        if (itemBeneficiario != null) {
            this.itemBeneficiario = itemBeneficiario;
            ParametroCalcIndenizacao parametroCalcIndenizacao = (ParametroCalcIndenizacao) Web.pegaDaSessao(ParametroCalcIndenizacao.class);
            if (parametroCalcIndenizacao != null) {
                this.itemBeneficiario.setParametroCalcIndenizacao(parametroCalcIndenizacao);
            }
        } else {
            this.itemBeneficiario = new ItemBeneficiario();
        }

    }

    @URLAction(mappingId = "verBeneficiario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void selecionar() {
        try {
            operacao = Operacoes.EDITAR;
            Beneficiario beneficiario = beneficiarioFacade.recuperar(getId());
            selecionado = beneficiario;
            itemBeneficiario = new ItemBeneficiario();

            Beneficiario b = (Beneficiario) selecionado;
            numeroSelecionado = b.getNumero();
            matriculaSelecionado = b.getMatriculaFP();
            pessoaFisicaSelecionada = b.getMatriculaFP().getPessoa();

            hierarquiaOrganizacionalSelecionada = new HierarquiaOrganizacional();
            hierarquiaOrganizacionalSelecionada.setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional.ADMINISTRATIVA);
            hierarquiaOrganizacionalSelecionada.setExercicio(sistemaControlador.getExercicioCorrente());

            HierarquiaOrganizacional h = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(b.getUnidadeOrganizacional(), hierarquiaOrganizacionalSelecionada, hierarquiaOrganizacionalSelecionada.getExercicio());
            hierarquiaOrganizacionalSelecionada = h;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();

        if (selecionado.getPessoaFisicaServidor() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Pessoa Fisica é obrigatório.");
        }

        if (selecionado.getMatriculaFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Matrícula é obrigatório.");
        }

        if (selecionado.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Início da Pensão é obrigatório.");
        }

        if (hierarquiaOrganizacionalSelecionada == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Hierarquia Organizacional é obrigatório.");
        }

        if (selecionado.getAtoLegal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ato Legal é obrigatório.");
        }

        if (selecionado.getItensBeneficiarios().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário adicionar ao menos uma Indenização.");
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void validarItemBeneficiario() {
        ValidacaoException ve = new ValidacaoException();

        if (itemBeneficiario.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Início de Vigência' deve ser informado.");
        }
        if (itemBeneficiario.getParametroCalcIndenizacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Parâmetro de cálculo da indenização' deve ser informado.");
        }

        for (ItemBeneficiario i : selecionado.getItensBeneficiarios()) {
            if (i.getFinalVigencia() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Há Itens com Vigência aberta na lista");
            }
            if (itemBeneficiario.getInicioVigencia() != null && itemBeneficiario.getFinalVigencia() != null) {
                if (itemBeneficiario.getInicioVigencia().before(i.getFinalVigencia())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A vigência que está sendo inserida conflita com as indenizações já inseridas !");
                }
            }
        }

        if (Util.validaPeriodo(itemBeneficiario.getInicioVigencia(), itemBeneficiario.getFinalVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Início de Vigência maior que o final de Vigência.");
        }

        if (itemBeneficiario.getValor() != null && itemBeneficiario.getValor().compareTo(BigDecimal.ZERO) == 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da indenização deve ser superior a 0,00(Zero) !");
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void addItemBeneficiario() {
        try {
            validarItemBeneficiario();

            itemBeneficiario.setBeneficiario(selecionado);
            itemBeneficiario.setDataRegistro(new Date());
            selecionado.getItensBeneficiarios().add(itemBeneficiario);

            novoItemBeneficiario();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    private void novoItemBeneficiario() {
        itemBeneficiario = new ItemBeneficiario();
    }

    public void navegaIndenizacao() {
        Web.navegacao(getUrlAtual(), "/rh/parametro-de-calculo-da-indenizacao/novo/", selecionado, itemBeneficiario);
    }

    public void navegaAtoLegal() {
        Web.navegacao(getUrlAtual(), "/atolegal/novo/", selecionado);
    }

    public void navegaMatricula() {
        Web.navegacao(getUrlAtual(), "/matriculafp/novo/", selecionado);
    }

    public void navegaPessoa() {
        Web.navegacao(getUrlAtual(), "/pessoa-pensionista/novo/", selecionado);
    }

    public void navegaContrato() {
        Web.navegacao(getUrlAtual(), "/contratofp/novo/", selecionado);
    }

    @Override
    public Beneficiario getSelecionado() {
        return (Beneficiario) super.getSelecionado();
    }

    public List<MatriculaFP> completaMatriculaFP(String parte) {
        return matriculaFPFacade.buscarMatriculaFpPorNomeOrCpf(parte.trim());
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public Boolean validaCampos() {
        Beneficiario b = getSelecionado();
        boolean valida = true;

        if (hierarquiaOrganizacionalSelecionada != null) {
            b.setUnidadeOrganizacional(hierarquiaOrganizacionalSelecionada.getSubordinada());
        } else {
            FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "A hierarquia organizacional deve ser informada!"));
            valida = false;
        }

        if (b.getItensBeneficiarios().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "É obrigatório informar ao menos uma indenização para o beneficiário !"));
            valida = false;
        }

        if (b.getMatriculaFP() == null) {
            FacesUtil.addMessageWarn("msgs", "Atenção!", "O Campo Matrícula é obrigatório !");
            valida = false;
        }

        return Util.validaCampos(selecionado) && valida;
    }

    public void setInicioVigenciaItem(DateSelectEvent e) {
        if (itemBeneficiario.getInicioVigencia() == null) {
            itemBeneficiario.setInicioVigencia(e.getDate());
        }
    }

    public void setaValorReferencia(SelectEvent item) {
        ParametroCalcIndenizacao parametro = (ParametroCalcIndenizacao) item.getObject();
        ValorReferenciaFP valorReferenciaFP = valorReferenciaFPFacade.recuperaValorReferenciaFPVigente(parametro.getReferenciaFP());
        itemBeneficiario.setValor(valorReferenciaFP.getValor());
    }

    public void removeValorBeneficio(ItemBeneficiario ib) {
        selecionado.getItensBeneficiarios().remove(ib);
        itemBeneficiario = new ItemBeneficiario();
        itemBeneficiario.setInicioVigencia(getSelecionado().getInicioVigencia());
    }

    public void editarItemBeneficiario(ItemBeneficiario itemBeneficiario) {
        removeValorBeneficio(itemBeneficiario);
        this.itemBeneficiario = itemBeneficiario;
    }

    public List<SelectItem> getContasCorrentesBancarias() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado != null && selecionado.getMatriculaFP() != null) {
            for (ContaCorrenteBancaria object : contaCorrenteBancPessoaFacade.listaContasPorPessoaFisica(selecionado.getMatriculaFP().getPessoa())) {
                toReturn.add(new SelectItem(object, object.toString()));
            }
        }
        return toReturn;
    }

    public void novaContaCorrente() {
        if (selecionado != null && selecionado.getMatriculaFP() != null) {
            Web.poeNaSessao(selecionado.getMatriculaFP().getPessoa());
            Web.navegacao(getUrlAtual(), "/conta-corrente-bancaria/novo/", selecionado);
        }
    }
}
