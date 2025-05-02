package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.GrupoRecursoFPFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.RecursoFPFacade;
import br.com.webpublico.util.*;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "grupoRecursoFPControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoRecursoFPAgrupamento", pattern = "/agrupamento-recursofp/novo/", viewId = "/faces/rh/administracaodepagamento/agrupamentorecursofp/edita.xhtml"),
        @URLMapping(id = "editarRecursoFPAgrupamento", pattern = "/agrupamento-recursofp/editar/#{grupoRecursoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/agrupamentorecursofp/edita.xhtml"),
        @URLMapping(id = "listarRecursoFPAgrupamento", pattern = "/agrupamento-recursofp/listar/", viewId = "/faces/rh/administracaodepagamento/agrupamentorecursofp/lista.xhtml"),
        @URLMapping(id = "verRecursoFPAgrupamento", pattern = "/agrupamento-recursofp/ver/#{grupoRecursoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/agrupamentorecursofp/visualizar.xhtml")
})
public class GrupoRecursoFPControlador extends PrettyControlador<GrupoRecursoFP> implements Serializable, CRUD {

    @EJB
    private GrupoRecursoFPFacade grupoRecursoFPFacade;
    @EJB
    private RecursoFPFacade recursoFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    protected ConverterAutoComplete converterRecursoFP;
    protected ConverterAutoComplete converterHierarquiaOrganizacional;
    private List<RecursoFP> listaRecursosFP;
    private RecursoFP[] recursosFPSelecionados;

    public GrupoRecursoFPControlador() {
        super(GrupoRecursoFP.class);
    }

    public RecursoFP[] getRecursosFPSelecionados() {
        return recursosFPSelecionados;
    }

    public void setRecursosFPSelecionados(RecursoFP[] recursosFPSelecionados) {
        this.recursosFPSelecionados = recursosFPSelecionados;
    }

    public List<RecursoFP> getListaRecursosFP() {
        return listaRecursosFP;
    }


    @URLAction(mappingId = "novoRecursoFPAgrupamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setAgrupamentoRecursoFP(new ArrayList<AgrupamentoRecursoFP>());
        listaRecursosFP = new ArrayList<RecursoFP>();
    }

    @URLAction(mappingId = "verRecursoFPAgrupamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        selecionar();
    }

    @URLAction(mappingId = "editarRecursoFPAgrupamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionar();
    }

    public void selecionar() {
        carregaListaRecursosFP();
        recursosFPSelecionados = new RecursoFP[selecionado.getAgrupamentoRecursoFP().size()];
        for (int i = 0; i < selecionado.getAgrupamentoRecursoFP().size(); i++) {
            recursosFPSelecionados[i] = selecionado.getAgrupamentoRecursoFP().get(i).getRecursoFP();
        }
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            adicionaRecursosSelecionados();
            super.salvar();
        }
    }

    private void adicionaRecursosSelecionados() {
        if (operacao.equals(Operacoes.EDITAR)) {
            selecionado.getAgrupamentoRecursoFP().clear();
        }
        for (RecursoFP r : recursosFPSelecionados) {
            AgrupamentoRecursoFP a = new AgrupamentoRecursoFP();
            a.setRecursoFP(r);
            a.setGrupoRecursoFP(selecionado);
            selecionado.getAgrupamentoRecursoFP().add(a);
        }
    }

    public void limparRecursosFP() {
        this.recursosFPSelecionados = null;
        this.listaRecursosFP = null;
    }

    private boolean validaCampos() {
        boolean valido = Util.validaCampos(selecionado);
        if (valido) {
            if (recursosFPSelecionados.length == 0) {
                FacesUtil.addWarn("Atenção!", "Selecione pelo menos um Recurso FP");
                valido = false;
            }
            for (RecursoFP recursosFP : recursosFPSelecionados) {
                GrupoRecursoFP grupoRecursoFP = grupoRecursoFPFacade.buscarGrupoRecursoVigentePorRecursoAndDataOperacao(recursosFP);
                if (grupoRecursoFP != null && (Operacoes.NOVO.equals(operacao) || !selecionado.getId().equals(grupoRecursoFP.getId()))) {
                    String link = "<a href=" + FacesUtil.getRequestContextPath() + "/agrupamento-recursofp/editar/" + grupoRecursoFP.getId() + " target='blank' style='font-weight: bold !important; color: #497692'>" + grupoRecursoFP.getNome() + "</a>";
                    FacesUtil.addOperacaoNaoPermitida("Recurso FP " + recursosFP.toString() + " já associado à Hierarquia " + grupoRecursoFP.getHierarquiaOrganizacional().toString() + ". - " +
                        link);
                    valido = false;
                }
            }
        }
        return valido;
    }

    public Converter getConverterRecursoFP() {
        if (converterRecursoFP == null) {
            converterRecursoFP = new ConverterAutoComplete(RecursoFP.class, recursoFPFacade);
        }
        return converterRecursoFP;
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquiaOrganizacional;
    }

    public void carregaListaRecursosFP() {
        listaRecursosFP = new ArrayList<RecursoFP>();
        if (selecionado.getHierarquiaOrganizacional() != null && selecionado.getHierarquiaOrganizacional().getId() != null) {
            String orgao[] = selecionado.getHierarquiaOrganizacional().getCodigo().split("\\.");
            //orgao[1];
            if (!orgao[1].equals("00")) {
                listaRecursosFP.addAll(recursoFPFacade.recuperaRecursosPorNumeroOrgao(orgao[1]));
            }
        } else {
            FacesUtil.addWarn("Atenção!", "Informe o Orgão antes de utilizar o Filtro.");
        }
    }


//    public List<SelectItem> getBanco() {
//        List<SelectItem> toReturn = new ArrayList<SelectItem>();
//        for (Banco object : bancoFacade.lista()) {
//            toReturn.add(new SelectItem(object, object.getDescricao()));
//        }
//        return toReturn;
//    }


    @Override
    public String getCaminhoPadrao() {
        return "/agrupamento-recursofp/";
    }

    @Override
    public AbstractFacade getFacede() {
        return grupoRecursoFPFacade;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacade.filtraPorNivel(parte.trim(), "2", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
