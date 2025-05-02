/*
 * Codigo gerado automaticamente em Thu Aug 04 08:07:43 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.PropositoAtoLegal;
import br.com.webpublico.enums.TipoParentescoRPPS;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "dependenteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoDependente", pattern = "/dependente/novo/", viewId = "/faces/rh/administracaodepagamento/dependente/edita.xhtml"),
    @URLMapping(id = "editarDependente", pattern = "/dependente/editar/#{dependenteControlador.id}/", viewId = "/faces/rh/administracaodepagamento/dependente/edita.xhtml"),
    @URLMapping(id = "listarDependente", pattern = "/dependente/listar/", viewId = "/faces/rh/administracaodepagamento/dependente/lista.xhtml"),
    @URLMapping(id = "verDependente", pattern = "/dependente/ver/#{dependenteControlador.id}/", viewId = "/faces/rh/administracaodepagamento/dependente/visualizar.xhtml")
})
public class DependenteControlador extends PrettyControlador<Dependente> implements Serializable, CRUD {

    @EJB
    private DependenteFacade dependenteFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    private ConverterAutoComplete converterResponsavel;
    private ConverterAutoComplete converterAtoLegal;
    private ConverterAutoComplete converterDependente;
    @EJB
    private GrauDeParentescoFacade grauDeParentescoFacade;
    private ConverterGenerico converterGrauDeParentesco;
    private DependenteVinculoFP dependenteVinculoFP;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    private ConverterGenerico converterVinculoFP;
    private List<DependenteVinculoFP> listaDependenteVinculoFP;
    @ManagedProperty(name = "prestadorServicosControlador", value = "#{prestadorServicosControlador}")
    private PrestadorServicosControlador prestadorServicosControlador;
    private PessoaFisica auxiliarResponsavel;
    @EJB
    private DependenteVinculoFPFacade dependenteVinculoFPFacade;
    @ManagedProperty(name = "contratoFPControlador", value = "#{contratoFPControlador}")
    private ContratoFPControlador contratoFPControlador;
    private int inicio = 0;
    private ConverterGenerico converterTipoDependente;
    @EJB
    private TipoDependenteFacade tipoDependenteFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;

    public DependenteControlador() {
        super(Dependente.class);
        auxiliarResponsavel = null;
    }

    public DependenteFacade getFacade() {
        return dependenteFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return dependenteFacade;
    }

    public PrestadorServicosControlador getPrestadorServicosControlador() {
        return prestadorServicosControlador;
    }

    public void setPrestadorServicosControlador(PrestadorServicosControlador prestadorServicosControlador) {
        this.prestadorServicosControlador = prestadorServicosControlador;
    }

    public ContratoFPControlador getContratoFPControlador() {
        return contratoFPControlador;
    }

    public void setContratoFPControlador(ContratoFPControlador contratoFPControlador) {
        this.contratoFPControlador = contratoFPControlador;
    }

    public PessoaFisica getAuxiliarResponsavel() {
        return auxiliarResponsavel;
    }

    public void setPrestadorResponsavel(PessoaFisica auxiliarResponsavel) {
        this.auxiliarResponsavel = auxiliarResponsavel;
    }

    @URLAction(mappingId = "novoDependente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        Dependente dpFP = selecionado;
        listaDependenteVinculoFP = new ArrayList<>();
        dpFP.setDependentesVinculosFPs(listaDependenteVinculoFP);
        dependenteVinculoFP = new DependenteVinculoFP();
        auxiliarResponsavel = null;
        getParametros();
    }

    @URLAction(mappingId = "verDependente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarDependente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        dependenteVinculoFP = new DependenteVinculoFP();
        super.editar();
    }

    public List<SelectItem> getResponsavel() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (PessoaFisica object : pessoaFisicaFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public Converter getConverterResponsavel() {
        if (converterResponsavel == null) {
            converterResponsavel = new ConverterAutoComplete(PessoaFisica.class, pessoaFisicaFacade);
        }
        return converterResponsavel;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public Converter getConverterDependentes() {
        if (converterTipoDependente == null) {
            converterTipoDependente = new ConverterGenerico(TipoDependente.class, tipoDependenteFacade);
        }
        return converterTipoDependente;
    }

    public List<SelectItem> getDependente() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (PessoaFisica object : pessoaFisicaFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoDependentes() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoDependente object : tipoDependenteFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public Converter getConverterDependente() {
        if (converterDependente == null) {
            converterDependente = new ConverterAutoComplete(PessoaFisica.class, pessoaFisicaFacade);
        }
        return converterDependente;
    }

    public List<SelectItem> getGrauDeParentesco() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (GrauDeParentesco object : grauDeParentescoFacade.buscarGrauDeParentescoAtivo()) {
            toReturn.add(new SelectItem(object, object.getCodigoEsocial() + " - " + object.getDescricao()));
        }
        return toReturn;
    }

    public GrauDeParentesco getGrauDeParentescoRecuperado() {
        if (selecionado != null) {
            GrauDeParentesco grau = selecionado.getGrauDeParentesco();
            if (grau != null) {
                grau = grauDeParentescoFacade.recuperar(grau.getId());
                return grau;
            }
        }

        return null;
    }

    public ConverterGenerico getConverterGrauDeParentesco() {
        if (converterGrauDeParentesco == null) {
            converterGrauDeParentesco = new ConverterGenerico(GrauDeParentesco.class, grauDeParentescoFacade);
        }
        return converterGrauDeParentesco;
    }

    @Override
    public void salvar() {
        try {
            validarInformacoesDependentes();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }


    public void selecionarAtoLegal(ActionEvent event) {
        selecionado.setAtoLegal((AtoLegal) event.getComponent().getAttributes().get("objeto"));
    }

    public List<AtoLegal> completaAtoLegalCargo(String s) {
        return atoLegalFacade.listaFiltrandoParteEPropositoAtoLegal(s.trim(), PropositoAtoLegal.ATO_DE_PESSOAL, "numero", "nome");
    }

    public List<PessoaFisica> completaResponsavel(String parte) {
        return pessoaFisicaFacade.listaPessoaPorMatriculaFP(parte.trim());
    }

    public List<PessoaFisica> completaDependente(String parte) {
        return pessoaFisicaFacade.listaFiltrandoTodasPessoasByNomeAndCpf(parte.trim());
    }

    public void adicionarDependenteVinculoFP() {
        try {
            validarDependenteVinculo();
            verificarSeExisteVigenciaEntreDatas();
            dependenteVinculoFP.setDependente(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getDependentesVinculosFPs(), dependenteVinculoFP);
            dependenteVinculoFP = new DependenteVinculoFP();
            dependenteVinculoFP.setDataRegistro(new Date());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ve) {
            logger.error("Registro ja vigente");
        }
    }

    public void verificarSeExisteVigenciaEntreDatas() throws ExcecaoNegocioGenerica {
        if (dependenteVinculoFP.getTipoDependente() != null) {
            if (!DataUtil.isVigenciaValida(dependenteVinculoFP, selecionado.getDependentesVinculosFPsPorTipoDependente(dependenteVinculoFP.getTipoDependente()))) {
                throw new ExcecaoNegocioGenerica("");
            }
        }
    }

    public void removerDependenteVinculoFP(DependenteVinculoFP dependenteVinculo) {
        selecionado.getDependentesVinculosFPs().remove(dependenteVinculo);
    }

    public void editarDependenteVinculoFP(DependenteVinculoFP dependenteVinculo) {
        dependenteVinculoFP = (DependenteVinculoFP) Util.clonarObjeto(dependenteVinculo);
    }

    public List<SelectItem> getVinculoFP() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (selecionado != null) {
            if (selecionado.getResponsavel() != null) {
                for (VinculoFP object : vinculoFPFacade.listaTodosVinculosPorPessoa(selecionado.getResponsavel())) {
                    toReturn.add(new SelectItem(object, object.getMatriculaFP().getMatricula() + "/" + object.getNumero() + " - " + object.getUnidadeOrganizacional()));
                }
            }
        }
        return toReturn;
    }

    public ConverterGenerico getConverterVinculoFP() {
        if (converterVinculoFP == null) {
            converterVinculoFP = new ConverterGenerico(VinculoFP.class, vinculoFPFacade);
        }
        return converterVinculoFP;
    }

    public DependenteVinculoFP getDependenteVinculoFP() {
        return dependenteVinculoFP;
    }

    public void setDependenteVinculoFP(DependenteVinculoFP dependenteVinculoFP) {
        this.dependenteVinculoFP = dependenteVinculoFP;
    }

    public List<DependenteVinculoFP> getDependentesVinculosFPs() {
        if (selecionado != null) {
            return selecionado.getDependentesVinculosFPs();
        }
        return new ArrayList<>();
    }


    @Override
    public void cancelar() {
        auxiliarResponsavel = null;
        super.cancelar();
    }

    public void validarInformacoesDependentes() {
        ValidacaoException ve = new ValidacaoException();

        if (selecionado.getDependente() != null && selecionado.getResponsavel() != null) {
            if (selecionado.getDependente().getId().equals(selecionado.getResponsavel().getId())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Dependente não pode ser a mesma pessoa que o responsável!");
            }

            DependenteVinculoFP dependenteVinculoFPRecuperado = dependenteVinculoFPFacade.recuperaDependenteVigentePorDependente(selecionado.getDependente());
            if (dependenteVinculoFPRecuperado != null) {
                if (!dependenteVinculoFPRecuperado.getDependente().equals(selecionado)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Dependente selecionado já é vigente em um outro contrato!");
                }
            }

            if (selecionado.getGrauDeParentesco() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O Grau de Parentesco deve ser informado.");
            }

            if (selecionado.getDependentesVinculosFPs() == null || selecionado.getDependentesVinculosFPs().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("Deve ser informado ao menos um Dependente Vínculo Folha de Pagamento.");
            }

            DependenteVinculoFP dpVinculoFP = dependenteVinculoFPFacade.recuperaDependenteVigentePorResponsavel(selecionado);

            if (selecionado.getGrauDeParentesco() != null) {
                if (dpVinculoFP != null && (!TipoParentescoRPPS.CONJUGE.equals(selecionado.getGrauDeParentesco().getTipoParentesco()) &&
                    !TipoParentescoRPPS.UNIAO_ESTAVEL.equals(selecionado.getGrauDeParentesco().getTipoParentesco()))) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Responsável selecionado já é um dependente no vínculo "
                        + dpVinculoFP.getDependente().getResponsavel() + " que está vigente no cadastro de dependente " + dpVinculoFP.getDependente().getDependente() + " !");
                }
            }
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private void validarDependenteVinculo() {
        ValidacaoException ve = new ValidacaoException();

        if (dependenteVinculoFP.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Início de Vigência!");
        }

        if (dependenteVinculoFP.getInicioVigencia() != null && dependenteVinculoFP.getFinalVigencia() != null) {
            if (dependenteVinculoFP.getInicioVigencia().after(dependenteVinculoFP.getFinalVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data inicial é maior que a data final");
            }
        }

        if (dependenteVinculoFP.getTipoDependente() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Tipo de Dependente!");
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/dependente/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void ativouAba(ActionEvent ev) {
        RequestContext.getCurrentInstance().update("Formulario:grauDeParentesco");
        RequestContext.getCurrentInstance().update("Formulario:responsavel");
    }

    private void getParametros() {
        String pessoaId = PrettyContext.getCurrentInstance().getRequestQueryString().getParameter("responsavel");
        if (pessoaId != null && !pessoaId.trim().isEmpty()) {
            selecionado.setResponsavel(pessoaFisicaFacade.recuperar(Long.parseLong(pessoaId)));
        }
    }

    public List<TipoParentescoRPPS> tiposParentescoRPPS() {
        return Arrays.asList(TipoParentescoRPPS.values());
    }

}
