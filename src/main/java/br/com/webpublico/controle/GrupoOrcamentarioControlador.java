/*
 * Codigo gerado automaticamente em Tue Jan 17 13:45:12 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.GrupoOrcamentarioFacade;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "grupoOrcamentarioControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-grupo-orcamentario", pattern = "/grupo-orcamentario/novo/", viewId = "/faces/financeiro/orcamentario/grupoorcamentario/edita.xhtml"),
        @URLMapping(id = "editar-grupo-orcamentario", pattern = "/grupo-orcamentario/editar/#{grupoOrcamentarioControlador.id}/", viewId = "/faces/financeiro/orcamentario/grupoorcamentario/edita.xhtml"),
        @URLMapping(id = "ver-grupo-orcamentario", pattern = "/grupo-orcamentario/ver/#{grupoOrcamentarioControlador.id}/", viewId = "/faces/financeiro/orcamentario/grupoorcamentario/visualizar.xhtml"),
        @URLMapping(id = "listar-grupo-orcamentario", pattern = "/grupo-orcamentario/listar/", viewId = "/faces/financeiro/orcamentario/grupoorcamentario/lista.xhtml")
})
public class GrupoOrcamentarioControlador extends PrettyControlador<GrupoOrcamentario> implements Serializable, CRUD {

    @EJB
    private GrupoOrcamentarioFacade grupoOrcamentarioFacade;
    private ConverterAutoComplete converterFuncao;
    private ConverterAutoComplete converterSubFuncao;
    private ConverterGenerico converterExercicio;
    private HierarquiaOrganizacional hieEntidade;
    private HierarquiaOrganizacional hieOrgao;
    private HierarquiaOrganizacional hieUnidade;
    private ConverterAutoComplete converterHirarquiaOrganizacional;
    private MoneyConverter moneyConverter;
    private ConverterAutoComplete converterProgramaPPA;
    private ConverterAutoComplete converterAcaoPPA;
    private ConverterAutoComplete converterSubAcaoPPA;
    private ConverterAutoComplete converterFonteDeRecursos;
    private ConverterAutoComplete converterNaturezaDespesa;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    public GrupoOrcamentarioControlador() {
        super(GrupoOrcamentario.class);
    }

    public GrupoOrcamentarioFacade getFacade() {
        return grupoOrcamentarioFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return grupoOrcamentarioFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/grupo-orcamentario/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-grupo-orcamentario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        hieEntidade = new HierarquiaOrganizacional();
        hieOrgao = new HierarquiaOrganizacional();
        hieUnidade = new HierarquiaOrganizacional();
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
    }

    @URLAction(mappingId = "ver-grupo-orcamentario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaHierarquias(selecionado);
    }

    @URLAction(mappingId = "editar-grupo-orcamentario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaHierarquias(selecionado);
    }

    @Override
    public void salvar() {
        try {
            if (Util.validaCampos(selecionado)
                && validaMesmoCodigo()) {
                if (operacao.equals(Operacoes.NOVO)) {
                    grupoOrcamentarioFacade.salvarNovo(selecionado);
                } else {
                    grupoOrcamentarioFacade.salvar(selecionado);
                }
                FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Registro salvo com sucesso.");
                redireciona();
            }
        } catch (Exception ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
        }
    }

    public void recuperaHierarquias(GrupoOrcamentario go) {
        hieEntidade = new HierarquiaOrganizacional();
        hieOrgao = new HierarquiaOrganizacional();
        hieUnidade = new HierarquiaOrganizacional();
        hieEntidade.setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional.ORCAMENTARIA);
        hieOrgao.setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional.ORCAMENTARIA);
        hieUnidade.setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional.ORCAMENTARIA);
        if (go.getEntidade() != null) {
            hieEntidade = grupoOrcamentarioFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(go.getEntidade(), hieEntidade, sistemaControlador.getExercicioCorrente());
        }
        if (go.getOrgao() != null) {
            hieOrgao = grupoOrcamentarioFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(go.getOrgao(), hieOrgao, sistemaControlador.getExercicioCorrente());
        }
        if (go.getUnidade() != null) {
            hieUnidade = grupoOrcamentarioFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(go.getUnidade(), hieUnidade, sistemaControlador.getExercicioCorrente());
        }
    }

    public void limpaCampos() {
        GrupoOrcamentario go = ((GrupoOrcamentario) selecionado);
        go.setEntidade(null);
        go.setOrgao(null);
        go.setUnidade(null);
        go.setFuncao(null);
        go.setSubFuncao(null);
        go.setProgramaPPA(null);
        go.setAcaoPPA(null);
        go.setSubAcaoPPA(null);
        go.setFonteDeRecursos(null);
        go.setNaturezaDespesa(null);
        hieEntidade = new HierarquiaOrganizacional();
        hieOrgao = new HierarquiaOrganizacional();
        hieUnidade = new HierarquiaOrganizacional();
    }

    public void removerTodos() {
        selecionado.setFonteDespesaOrc(new ArrayList<FonteDespesaORC>());
    }

    public void setaEntidade(SelectEvent evt) {
        HierarquiaOrganizacional hie = (HierarquiaOrganizacional) evt.getObject();
        GrupoOrcamentario go = ((GrupoOrcamentario) selecionado);
        if (hie != null) {
            go.setEntidade(hie.getSubordinada());
        }

    }

    public void setaEntidadeNula() {
        GrupoOrcamentario go = ((GrupoOrcamentario) selecionado);
        go.setEntidade(null);
    }

    public void setaOrgao(SelectEvent evt) {
        HierarquiaOrganizacional hie = (HierarquiaOrganizacional) evt.getObject();
        GrupoOrcamentario go = ((GrupoOrcamentario) selecionado);
        go.setOrgao(hie.getSubordinada());
    }

    public void setaUnidade(SelectEvent evt) {
        HierarquiaOrganizacional hie = (HierarquiaOrganizacional) evt.getObject();
        GrupoOrcamentario go = ((GrupoOrcamentario) selecionado);
        go.setUnidade(hie.getSubordinada());
    }

    public void removeFonte(ActionEvent evt) {
        FonteDespesaORC fon = (FonteDespesaORC) evt.getComponent().getAttributes().get("removeFonte");
        ((GrupoOrcamentario) selecionado).getFonteDespesaOrc().remove(fon);
    }

    public List<FonteDeRecursos> completaFonteDeRecursos(String parte) {
        return grupoOrcamentarioFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public ConverterAutoComplete getConverterFonteDeRecursos() {
        if (converterFonteDeRecursos == null) {
            converterFonteDeRecursos = new ConverterAutoComplete(FonteDeRecursos.class, grupoOrcamentarioFacade.getFonteDeRecursosFacade());
        }
        return converterFonteDeRecursos;
    }

    public List<Conta> completaNaturezaDespesa(String parte) throws ExcecaoNegocioGenerica {
        List<Conta> contasDespesa = new ArrayList<Conta>();
        try {
            contasDespesa = grupoOrcamentarioFacade.getContaFacade().listaFiltrandoContaDespesa(parte.trim(), sistemaControlador.getExercicioCorrente());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesContext.getCurrentInstance().addMessage("Formulario:natD", new FacesMessage(FacesMessage.SEVERITY_WARN, ex.getMessage(), " "));
        }
        return contasDespesa;
    }

    public ConverterAutoComplete getConverterNaturezaDespesa() {
        if (converterNaturezaDespesa == null) {
            converterNaturezaDespesa = new ConverterAutoComplete(Conta.class, grupoOrcamentarioFacade.getContaFacade());
        }
        return converterNaturezaDespesa;
    }

    public List<ProgramaPPA> completarProgramasPPA(String parte) {
        return grupoOrcamentarioFacade.getProgramaPPAFacade().buscarProgramasPorExercicio(parte, sistemaControlador.getExercicioCorrente());
    }

    public ConverterAutoComplete getConverterProgramaPPA() {
        if (converterProgramaPPA == null) {
            converterProgramaPPA = new ConverterAutoComplete(ProgramaPPA.class, grupoOrcamentarioFacade.getProgramaPPAFacade());
        }
        return converterProgramaPPA;
    }

    public List<AcaoPPA> completaAcaoPPA(String parte) {
        GrupoOrcamentario go = ((GrupoOrcamentario) selecionado);
        List<AcaoPPA> acoes = new ArrayList<AcaoPPA>();
        List<AcaoPPA> removidas = new ArrayList<AcaoPPA>();
        acoes = grupoOrcamentarioFacade.getProjetoAtividadeFacade().buscarAcoesPPAPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
        if (go.getProgramaPPA() != null) {
            for (AcaoPPA ac : acoes) {
                if (!ac.getPrograma().getId().equals(go.getProgramaPPA().getId())) {
                    removidas.add(ac);
                }
            }
            acoes.removeAll(removidas);
        }
        return acoes;
    }

    public ConverterAutoComplete getConverterAcaoPPA() {
        if (converterAcaoPPA == null) {
            converterAcaoPPA = new ConverterAutoComplete(AcaoPPA.class, grupoOrcamentarioFacade.getProjetoAtividadeFacade());
        }
        return converterAcaoPPA;
    }

    public List<SubAcaoPPA> completaSubAcaoPPA(String parte) {
        return grupoOrcamentarioFacade.getSubProjetoAtividadeFacade().listaFiltrando(parte.trim(), "codigo", "descricao");
    }

    public ConverterAutoComplete getConverterSubAcaoPPA() {
        if (converterSubAcaoPPA == null) {
            converterSubAcaoPPA = new ConverterAutoComplete(SubAcaoPPA.class, grupoOrcamentarioFacade.getSubProjetoAtividadeFacade());
        }
        return converterSubAcaoPPA;
    }

    public List<HierarquiaOrganizacional> completaEntidade(String parte) {
        return grupoOrcamentarioFacade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.trim(), "1", TipoHierarquiaOrganizacional.ORCAMENTARIA, sistemaControlador.getDataOperacao());
    }

    public List<HierarquiaOrganizacional> completaOrgao(String parte) {
        List<HierarquiaOrganizacional> listaOrgaos, removidos = new ArrayList<HierarquiaOrganizacional>();
        listaOrgaos = grupoOrcamentarioFacade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.trim(), "2", TipoHierarquiaOrganizacional.ORCAMENTARIA, sistemaControlador.getDataOperacao());
//        if (hieEntidade.getSubordinada() != null) {
//            for (HierarquiaOrganizacional hie : listaOrgaos) {
//                if (!hie.getSuperior().getId().equals(hieEntidade.getSubordinada().getId())) {
//                    removidos.add(hie);
//                }
//            }
//            listaOrgaos.removeAll(removidos);
//        }
        return listaOrgaos;
    }

    public List<HierarquiaOrganizacional> completaUnidade(String parte) {
        List<HierarquiaOrganizacional> listaUnidades, removidos = new ArrayList<HierarquiaOrganizacional>();
        listaUnidades = grupoOrcamentarioFacade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.trim(), "3", TipoHierarquiaOrganizacional.ORCAMENTARIA, sistemaControlador.getDataOperacao());
//        if (hieOrgao.getSubordinada() != null) {
//            for (HierarquiaOrganizacional hie : listaUnidades) {
//                if (!hie.getSuperior().getId().equals(hieOrgao.getSubordinada().getId())) {
//                    removidos.add(hie);
//                }
//            }
//            listaUnidades.removeAll(removidos);
//        }
        return listaUnidades;
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHirarquiaOrganizacional == null) {
            converterHirarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, grupoOrcamentarioFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterHirarquiaOrganizacional;
    }

    public void recuperaFontes() {
        List<FonteDespesaORC> lista = grupoOrcamentarioFacade.getFonteDespesaORCFacade().buscarFontesDespesaOrcPorGrupoOrcamentario(selecionado, sistemaControlador.getExercicioCorrente(), sistemaControlador.getDataOperacao());
        for (FonteDespesaORC f : lista) {
            if (!selecionado.getFonteDespesaOrc().contains(f)) {
                selecionado.getFonteDespesaOrc().add(f);
            }
        }
    }

    public List<Funcao> completaFuncao(String parte) {
        return grupoOrcamentarioFacade.getFuncaoFacade().listaFiltrando(parte.trim(), "codigo", "descricao");
    }

    public ConverterAutoComplete getConverterFuncao() {
        if (converterFuncao == null) {
            converterFuncao = new ConverterAutoComplete(Funcao.class, grupoOrcamentarioFacade.getFuncaoFacade());
        }
        return converterFuncao;
    }

    public List<SubFuncao> completaSubFuncao(String parte) {
        return grupoOrcamentarioFacade.getSubFuncaoFacade().listaFiltrando(parte.trim(), "codigo", "descricao");
    }

    public ConverterAutoComplete getConverterSubFuncao() {
        if (converterSubFuncao == null) {
            converterSubFuncao = new ConverterAutoComplete(SubFuncao.class, grupoOrcamentarioFacade.getSubFuncaoFacade());
        }
        return converterSubFuncao;
    }

    public List<SelectItem> getExercicio() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Exercicio object : grupoOrcamentarioFacade.getExercicioFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterGenerico(Exercicio.class, grupoOrcamentarioFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public HierarquiaOrganizacional getHieEntidade() {
        return hieEntidade;
    }

    public void setHieEntidade(HierarquiaOrganizacional hieEntidade) {
        this.hieEntidade = hieEntidade;
    }

    public HierarquiaOrganizacional getHieOrgao() {
        return hieOrgao;
    }

    public void setHieOrgao(HierarquiaOrganizacional hieOrgao) {
        this.hieOrgao = hieOrgao;
    }

    public HierarquiaOrganizacional getHieUnidade() {
        return hieUnidade;
    }

    public void setHieUnidade(HierarquiaOrganizacional hieUnidade) {
        this.hieUnidade = hieUnidade;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public boolean validaMesmoCodigo() {
        if (grupoOrcamentarioFacade.validaMesmoCodigo(selecionado)) {
            return true;
        }
        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Já existe um Grupo Orçamentário cadastrado com o código: <b>" + selecionado.getCodigo() + "</b>. Informe um código diferente para item atual.");
        return false;
    }

    public BigDecimal somaTotais() {

        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (FonteDespesaORC co : selecionado.getFonteDespesaOrc()) {
            try {
                soma = soma.add(co.getProvisaoPPAFonte().getValor());
            } catch (Exception e) {

            }
        }
        return soma;
    }
}
