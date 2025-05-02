package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CalamidadePublicaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-calamidade-publica", pattern = "/calamidade-publica/novo/", viewId = "/faces/comum/calamidade-publica/edita.xhtml"),
    @URLMapping(id = "editar-calamidade-publica", pattern = "/calamidade-publica/editar/#{calamidadePublicaControlador.id}/", viewId = "/faces/comum/calamidade-publica/edita.xhtml"),
    @URLMapping(id = "visualizar-calamidade-publica", pattern = "/calamidade-publica/ver/#{calamidadePublicaControlador.id}/", viewId = "/faces/comum/calamidade-publica/visualizar.xhtml"),
    @URLMapping(id = "listar-calamidade-publica", pattern = "/calamidade-publica/listar/", viewId = "/faces/comum/calamidade-publica/lista.xhtml")
})
public class CalamidadePublicaControlador extends PrettyControlador<CalamidadePublica> implements Serializable, CRUD {

    @EJB
    private CalamidadePublicaFacade facade;
    private CalamidadePublicaContrato calamidadePublicaContrato;
    private CalamidadePublicaAtoLegal calamidadePublicaAtoLegal;
    private CalamidadePublicaRecurso calamidadePublicaRecurso;
    private CalamidadePublicaBemServico calamidadePublicaBemServico;
    private CalamidadePublicaBemDoado calamidadePublicaBemDoado;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    //Filtros
    private String filtroContrato;
    private String filtroAtoLegal;
    private List<Contrato> contratos;
    private List<AtoLegal> atosLegais;


    public CalamidadePublicaControlador() {
        super(CalamidadePublica.class);
    }

    @URLAction(mappingId = "novo-calamidade-publica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        cancelarAtoLegal();
        cancelarContrato();
        cancelarBemServico();
    }

    @URLAction(mappingId = "editar-calamidade-publica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        cancelarAtoLegal();
        cancelarContrato();
        cancelarBemServico();
        buscarHierarquiasDosBensServicosRecebidos();
    }

    @URLAction(mappingId = "visualizar-calamidade-publica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void cancelarRecurso() {
        calamidadePublicaRecurso = null;
    }

    public void cancelarContrato() {
        calamidadePublicaContrato = null;
    }

    public void instanciarContrato() {
        calamidadePublicaContrato = new CalamidadePublicaContrato();
        calamidadePublicaContrato.setCalamidadePublica(selecionado);
    }

    public void editarContrato(CalamidadePublicaContrato contrato) {
        calamidadePublicaContrato = (CalamidadePublicaContrato) Util.clonarObjeto(contrato);
    }

    public void removerContrato(CalamidadePublicaContrato contrato) {
        selecionado.getContratos().remove(contrato);
    }

    public void adicionarContrato() {
        try {
            validarContrato();
            Util.adicionarObjetoEmLista(selecionado.getContratos(), calamidadePublicaContrato);
            cancelarContrato();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarBemServico() {
        calamidadePublicaBemServico = null;
    }

    public void instanciarBemServico() {
        calamidadePublicaBemServico = new CalamidadePublicaBemServico();
        calamidadePublicaBemServico.setCalamidadePublica(selecionado);
    }

    public void editarBemServico(CalamidadePublicaBemServico bemServico) {
        calamidadePublicaBemServico = (CalamidadePublicaBemServico) Util.clonarObjeto(bemServico);
    }

    public void removerBemServico(CalamidadePublicaBemServico bemServico) {
        selecionado.getBensServicosRecebidos().remove(bemServico);
    }

    public void adicionarBemServico() {
        try {
            validarBemServico();
            Util.adicionarObjetoEmLista(selecionado.getBensServicosRecebidos(), calamidadePublicaBemServico);
            cancelarBemServico();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarBemServico() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(calamidadePublicaBemServico.getDescricao())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Descrição deve ser informado.");
        }
        ve.lancarException();
    }


    public void cancelarBemDoado() {
        calamidadePublicaBemDoado = null;
    }

    public void instanciarBemDoado() {
        calamidadePublicaBemDoado = new CalamidadePublicaBemDoado();
        calamidadePublicaBemDoado.setCalamidadePublica(selecionado);
    }

    public void editarBemDoado(CalamidadePublicaBemDoado bemDoado) {
        calamidadePublicaBemDoado = (CalamidadePublicaBemDoado) Util.clonarObjeto(bemDoado);
        if (calamidadePublicaBemDoado != null &&
            calamidadePublicaBemDoado.getHierarquiaOrganizacional() == null &&
            calamidadePublicaBemDoado.getUnidadeOrganizacional() != null) {
            calamidadePublicaBemDoado.setHierarquiaOrganizacional(facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(new Date(), calamidadePublicaBemDoado.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA));
        }
    }

    public void removerBemDoado(CalamidadePublicaBemDoado bemDoado) {
        selecionado.getBensDoados().remove(bemDoado);
    }

    public void adicionarBemDoado() {
        try {
            validarBemDoado();
            Util.adicionarObjetoEmLista(selecionado.getBensDoados(), calamidadePublicaBemDoado);
            cancelarBemDoado();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarBemDoado() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(calamidadePublicaBemDoado.getDescricao())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Descrição deve ser informado.");
        }
        ve.lancarException();
    }

    public List<HierarquiaOrganizacional> completarUnidadeOrganizacional(String parte) {
        return facade.completarUnidadeOrganizacional(parte);
    }

    public List<HierarquiaOrganizacional> completarUnidadeOrcamentaria(String parte) {
        return facade.completarUnidadeOrcamentaria(parte);
    }

    public List<UnidadeExterna> completarUnidadeExterna(String parte) {
        return facade.completarUnidadeExterna(parte.trim());
    }

    public List<FonteDeRecursos> completarFonteDeRecurso(String parte) {
        return facade.completarFonteDeRecurso(parte);
    }

    public List<UnidadeMedida> completarUnidadesMedida(String filtro) {
        return facade.buscarUnidadesMedida(filtro);
    }

    public List<SelectItem> getTipoUnidade() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAtoLegalUnidade object : TipoAtoLegalUnidade.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return Util.ordenaSelectItem(toReturn);
    }

    public void atribuirCamposAutoCompleteNull() {
        hierarquiaOrganizacional = null;
        calamidadePublicaRecurso.setUnidadeOrganizacional(null);
        calamidadePublicaRecurso.setUnidadeExterna(null);
    }

    public void instanciarRecurso() {
        calamidadePublicaRecurso = new CalamidadePublicaRecurso();
        calamidadePublicaRecurso.setCalamidadePublica(selecionado);
        calamidadePublicaRecurso.setTipo(TipoAtoLegalUnidade.EXTERNA);
        calamidadePublicaRecurso.setDataRecebimento(new Date());
    }

    public void editarRecurso(CalamidadePublicaRecurso recurso) {
        calamidadePublicaRecurso = (CalamidadePublicaRecurso) Util.clonarObjeto(recurso);
    }

    public void removerRecurso(CalamidadePublicaRecurso recurso) {
        selecionado.getRecursos().remove(recurso);
    }

    private void validarRecurso() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(calamidadePublicaRecurso, ve);
        ve.lancarException();
        if (calamidadePublicaRecurso.isInterna()) {
            if (calamidadePublicaRecurso.getUnidadeOrganizacional() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Organizacional deve ser informado.");
            }
        }
        if (!calamidadePublicaRecurso.isInterna()) {
            if (calamidadePublicaRecurso.getUnidadeExterna() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Externa deve ser informado.");
            }
        }
        ve.lancarException();
    }


    public void adicionarRecurso() {
        try {
            if (hierarquiaOrganizacional != null) {
                calamidadePublicaRecurso.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
            }
            validarRecurso();
            Util.adicionarObjetoEmLista(selecionado.getRecursos(), calamidadePublicaRecurso);
            cancelarRecurso();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerContratoFiltro(Contrato contrato) {
        contratos.remove(contrato);
    }

    public void adicionarContratoFiltro() {
        try {
            for (Contrato contrato : contratos) {
                calamidadePublicaContrato = new CalamidadePublicaContrato();
                calamidadePublicaContrato.setCalamidadePublica(selecionado);
                calamidadePublicaContrato.setContrato(contrato);
                validarContrato();
                Util.adicionarObjetoEmLista(selecionado.getContratos(), calamidadePublicaContrato);
            }
            cancelarContrato();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }


    private void validarContrato() {
        ValidacaoException ve = new ValidacaoException();
        if (calamidadePublicaContrato.getContrato() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Contrato deve ser informado.");
        }
        if (calamidadePublicaContrato.getSituacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Situação deve ser informado.");
        }
        ve.lancarException();
        for (CalamidadePublicaContrato contrato : selecionado.getContratos()) {
            if (!contrato.equals(calamidadePublicaContrato) && contrato.getContrato().equals(calamidadePublicaContrato.getContrato())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O contrato selecionado já está adicionado.");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getSituacoesContrato() {
        return Util.getListSelectItem(SituacaoContratoCalamidade.values());
    }

    public List<Contrato> completarContratos(String filtro) {
        return facade.buscarContratos(filtro);
    }

    public List<AtoLegal> completarAtosLegais(String filtro) {
        return facade.buscarAtosLegais(filtro);
    }

    public void buscarContratos() {
        contratos = facade.buscarContratos(filtroContrato);
    }

    public void buscarAtosLegais() {
        atosLegais = facade.buscarAtosLegais(filtroAtoLegal);
    }

    public void cancelarAtoLegal() {
        calamidadePublicaAtoLegal = null;
    }

    public void instanciarAtoLegal() {
        calamidadePublicaAtoLegal = new CalamidadePublicaAtoLegal();
        calamidadePublicaAtoLegal.setCalamidadePublica(selecionado);
    }


    public void editarAtoLegal(CalamidadePublicaAtoLegal atoLegal) {
        calamidadePublicaAtoLegal = (CalamidadePublicaAtoLegal) Util.clonarObjeto(atoLegal);
    }

    public void removerAtoLegal(CalamidadePublicaAtoLegal atoLegal) {
        selecionado.getAtosLegais().remove(atoLegal);
    }

    public void adicionarAtoLegal() {
        try {
            validarAtoLegal();
            Util.adicionarObjetoEmLista(selecionado.getAtosLegais(), calamidadePublicaAtoLegal);
            cancelarAtoLegal();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerAtoLegalFiltro(AtoLegal atoLegal) {
        atosLegais.remove(atoLegal);
    }

    public void adicionarAtoLegalFiltro() {
        try {
            for (AtoLegal atoLegal : atosLegais) {
                calamidadePublicaAtoLegal = new CalamidadePublicaAtoLegal();
                calamidadePublicaAtoLegal.setAtoLegal(atoLegal);
                calamidadePublicaAtoLegal.setCalamidadePublica(selecionado);
                validarAtoLegal();
                Util.adicionarObjetoEmLista(selecionado.getAtosLegais(), calamidadePublicaAtoLegal);
            }
            cancelarAtoLegal();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarAtoLegal() {
        ValidacaoException ve = new ValidacaoException();
        if (calamidadePublicaAtoLegal.getAtoLegal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ato Legal deve ser informado.");
        }
        ve.lancarException();
        for (CalamidadePublicaAtoLegal atoLegal : selecionado.getAtosLegais()) {
            if (!atoLegal.equals(calamidadePublicaAtoLegal) && atoLegal.getAtoLegal().equals(calamidadePublicaAtoLegal.getAtoLegal())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O ato legal selecionado já está adicionado.");
            }
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        super.salvar();
    }

    @Override
    protected boolean validaRegrasParaSalvar() {
        if (super.validaRegrasParaSalvar()) {
            return validarAbreviacao();
        }
        return Boolean.FALSE;
    }

    private Boolean validarAbreviacao() {
        if (selecionado.getAbreviacao() != null) {
            CalamidadePublica f = facade.buscarPorAbrevisacao(selecionado);
            if (f != null) {
                FacesUtil.addOperacaoNaoPermitida("Existe uma Calamidade Pública cadastrada com o tipo " + selecionado.getAbreviacao().getDescricao() + ".");
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public List<SelectItem> getTipos() {
        return Util.getListSelectItem(TipoCalamidadePublica.values());
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/calamidade-publica/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public CalamidadePublicaContrato getCalamidadePublicaContrato() {
        return calamidadePublicaContrato;
    }

    public void setCalamidadePublicaContrato(CalamidadePublicaContrato calamidadePublicaContrato) {
        this.calamidadePublicaContrato = calamidadePublicaContrato;
    }

    public CalamidadePublicaAtoLegal getCalamidadePublicaAtoLegal() {
        return calamidadePublicaAtoLegal;
    }

    public void setCalamidadePublicaAtoLegal(CalamidadePublicaAtoLegal calamidadePublicaAtoLegal) {
        this.calamidadePublicaAtoLegal = calamidadePublicaAtoLegal;
    }

    public String getFiltroContrato() {
        return filtroContrato;
    }

    public void setFiltroContrato(String filtroContrato) {
        this.filtroContrato = filtroContrato;
    }

    public String getFiltroAtoLegal() {
        return filtroAtoLegal;
    }

    public void setFiltroAtoLegal(String filtroAtoLegal) {
        this.filtroAtoLegal = filtroAtoLegal;
    }

    public List<Contrato> getContratos() {
        return contratos;
    }

    public void setContratos(List<Contrato> contratos) {
        this.contratos = contratos;
    }

    public List<AtoLegal> getAtosLegais() {
        return atosLegais;
    }

    public void setAtosLegais(List<AtoLegal> atosLegais) {
        this.atosLegais = atosLegais;
    }

    public CalamidadePublicaRecurso getCalamidadePublicaRecurso() {
        return calamidadePublicaRecurso;
    }

    public void setCalamidadePublicaRecurso(CalamidadePublicaRecurso calamidadePublicaRecurso) {
        this.calamidadePublicaRecurso = calamidadePublicaRecurso;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public CalamidadePublicaBemServico getCalamidadePublicaBemServico() {
        return calamidadePublicaBemServico;
    }

    public void setCalamidadePublicaBemServico(CalamidadePublicaBemServico calamidadePublicaBemServico) {
        this.calamidadePublicaBemServico = calamidadePublicaBemServico;
    }

    public List<Pessoa> completarPessoa(String parte) {
        return facade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public CalamidadePublicaBemDoado getCalamidadePublicaBemDoado() {
        return calamidadePublicaBemDoado;
    }

    public void setCalamidadePublicaBemDoado(CalamidadePublicaBemDoado calamidadePublicaBemDoado) {
        this.calamidadePublicaBemDoado = calamidadePublicaBemDoado;
    }

    public Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    private void buscarHierarquiasDosBensServicosRecebidos() {
        for (CalamidadePublicaBemServico item : selecionado.getBensServicosRecebidos()) {
            if (item.getUnidadeOrganizacional() != null) {
                item.setHierarquiaOrganizacional(facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(new Date(), item.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA));
            }
        }
    }
}
