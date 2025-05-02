package br.com.webpublico.controle;

import br.com.webpublico.entidades.CotaOrcamentaria;
import br.com.webpublico.entidades.GrupoOrcamentario;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.LiberacaoCotaOrcamentaria;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.LiberacaoCotaOrcamentariaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Edi on 15/04/2015.
 */
@ManagedBean(name = "liberacaoCotaOrcamentariaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-liberacao-cota-orcamentaria",   pattern = "/planejamento/liberacao-cota-orcamentaria/novo/",                                              viewId = "/faces/financeiro/orcamentario/liberacaocotaorcamentaria/edita.xhtml"),
        @URLMapping(id = "editar-liberacao-cota-orcamentaria", pattern = "/planejamento/liberacao-cota-orcamentaria/editar/#{liberacaoCotaOrcamentariaControlador.id}/", viewId = "/faces/financeiro/orcamentario/liberacaocotaorcamentaria/edita.xhtml"),
        @URLMapping(id = "ver-liberacao-cota-orcamentaria",    pattern = "/planejamento/liberacao-cota-orcamentaria/ver/#{liberacaoCotaOrcamentariaControlador.id}/",    viewId = "/faces/financeiro/orcamentario/liberacaocotaorcamentaria/visualizar.xhtml"),
        @URLMapping(id = "listar-liberacao-cota-orcamentaria", pattern = "/planejamento/liberacao-cota-orcamentaria/listar/",                                            viewId = "/faces/financeiro/orcamentario/liberacaocotaorcamentaria/lista.xhtml")
})
public class LiberacaoCotaOrcamentariaControlador extends PrettyControlador<LiberacaoCotaOrcamentaria> implements Serializable, CRUD {

    @EJB
    private LiberacaoCotaOrcamentariaFacade liberacaoCotaOrcamentariaFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ConverterAutoComplete converterGrupoOrcamentario;
    private ConverterAutoComplete converterUnidadeOrganizacional;
    private BigDecimal saldoGrupoOrcamentario;
    private List<CotaOrcamentaria> cotasCotaOrcamentarias;

    public LiberacaoCotaOrcamentariaControlador() {
        super(LiberacaoCotaOrcamentaria.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return liberacaoCotaOrcamentariaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/planejamento/liberacao-cota-orcamentaria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-liberacao-cota-orcamentaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        selecionado = new LiberacaoCotaOrcamentaria();
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        selecionado.setOperacao(OperacaoFormula.ADICAO);
        selecionado.setDataLiberacao(sistemaControlador.getDataOperacao());
        selecionado.setValor(BigDecimal.ZERO);
        saldoGrupoOrcamentario = BigDecimal.ZERO;
        cotasCotaOrcamentarias = new ArrayList<>();
    }

    @URLAction(mappingId = "editar-liberacao-cota-orcamentaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        recuperarCotas();
    }

    @URLAction(mappingId = "ver-liberacao-cota-orcamentaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        selecionado = liberacaoCotaOrcamentariaFacade.recuperar(super.getId());
        cotasCotaOrcamentarias = new ArrayList<>();
        saldoGrupoOrcamentario = BigDecimal.ZERO;
        recuperarCotas();
    }

    public void redirecionarParaLista() {
        FacesUtil.addOperacaoRealizada("Cota Orçamentária liberada com sucesso.");
        redireciona();
    }

    @Override
    public void salvar() {
        try {
            if (Util.validaCampos(selecionado)) {
                if (validaSaldoGrupoOrcamentario()) {
                    liberacaoCotaOrcamentariaFacade.salvarNovo(selecionado, cotasCotaOrcamentarias);
                }
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public List<GrupoOrcamentario> completaGrupoOrcamentario(String parte) {
        try {
            return liberacaoCotaOrcamentariaFacade.getGrupoOrcamentarioFacade().listaGrupoPorExercicio(sistemaControlador.getExercicioCorrente(), parte.trim());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<HierarquiaOrganizacional> completaUnidadeOrganizacional(String parte) {
        try {
            if (selecionado.getDataLiberacao() != null) {
                return liberacaoCotaOrcamentariaFacade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.trim(), "3", "ORCAMENTARIA", selecionado.getDataLiberacao());
            } else {
                FacesUtil.addOperacaoNaoPermitida("O campo data de liberação deve ser informado para filtrar a unidade orçamentária.");
            }
        } catch (Exception ex) {
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    public List<CotaOrcamentaria> recuperarCotas() {
        try {
            if (selecionado.getGrupoOrcamentario() != null) {
                cotasCotaOrcamentarias = liberacaoCotaOrcamentariaFacade.getCotaOrcamentariaFacade().recuperarCotaPorGrupoOrcamentario(selecionado.getGrupoOrcamentario());
                Collections.sort(cotasCotaOrcamentarias, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        CotaOrcamentaria s1 = (CotaOrcamentaria) o1;
                        CotaOrcamentaria s2 = (CotaOrcamentaria) o2;
                        if (s1.getIndice() != null && s2.getIndice() != null) {
                            return s1.getIndice().compareTo(s2.getIndice());
                        } else {
                            return 0;
                        }
                    }
                });
                calcularSaldoCumulativo();
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
        return new ArrayList<>();
    }

    public Boolean validaSaldoGrupoOrcamentario() {
        if (selecionado.getOperacao().equals(OperacaoFormula.ADICAO)) {
            if (selecionado.getValor().add(somaTotaisProgramados()).compareTo(getSaldoGrupoOrc()) > 0) {
                FacesUtil.addOperacaoNaoPermitida("O valor a ser liberado é maior que saldo disponível para o grupo orçamentário: " + selecionado.getGrupoOrcamentario().getCodigo() + ".");
                return false;
            }
        } else {
            for (CotaOrcamentaria cota : cotasCotaOrcamentarias) {
                Integer mes = (Integer) DataUtil.getMes(selecionado.getDataLiberacao());
                if (selecionado.getValor().compareTo(cota.getValorProgramado()) > 0) {
                    if (cota.getIndice().equals(mes)) {
                        FacesUtil.addOperacaoNaoPermitida("O valor a ser removido da cota é maior que o programado para o mês de " + cota.getMes().getDescricao() + " no grupo orçamentário: " + selecionado.getGrupoOrcamentario().getCodigo() + ".");
                        return false;
                    }
                }
            }
        }
        FacesUtil.executaJavaScript("dialogFinalizar.show()");
        return true;
    }

    public BigDecimal getSaldoGrupoOrc() {
        if (selecionado.getGrupoOrcamentario() != null) {
            saldoGrupoOrcamentario = liberacaoCotaOrcamentariaFacade.getFonteDespesaORCFacade().recuperarSaldoAtualPorGrupoOrc(selecionado.getGrupoOrcamentario());
            return saldoGrupoOrcamentario;
        }
        return BigDecimal.ZERO;
    }


    public List<SelectItem> getOperacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (OperacaoFormula op : OperacaoFormula.values()) {
            toReturn.add(new SelectItem(op, op.getDescricao()));
        }
        return toReturn;
    }

    public void setarUnidade() {
        if (hierarquiaOrganizacional != null) {
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        }
    }

    public BigDecimal somaTotaisProgramados() {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (CotaOrcamentaria co : cotasCotaOrcamentarias) {
            try {
                soma = soma.add(co.getValorProgramado());
            } catch (Exception e) {

            }
        }
        return soma;
    }

    public BigDecimal somaTotaisPercentuais() {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (CotaOrcamentaria co : cotasCotaOrcamentarias) {
            try {
                soma = soma.add(co.getPercentual());
            } catch (Exception e) {

            }
        }
        return soma;
    }

    public boolean validarCemPorcento() {
        if (somaTotaisPercentuais().compareTo(BigDecimal.valueOf(100)) > 0) {
            FacesUtil.addAtencao("O percentual total excedeu 100%");
            return false;
        }
        return true;
    }

    public BigDecimal somaTotaisUtilizados() {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (CotaOrcamentaria co : cotasCotaOrcamentarias) {
            soma = soma.add(co.getValorUtilizado());
        }
        return soma;
    }

    public BigDecimal getSaldoRestante() {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (CotaOrcamentaria co : cotasCotaOrcamentarias) {
            soma = co.getSaldo();
        }
        return soma;
    }

    public void calcularSaldoCumulativo() {
        BigDecimal saldoAnteriorCota = BigDecimal.ZERO;
        for (CotaOrcamentaria co : cotasCotaOrcamentarias) {
            calcularPercentualPorProgramado(co);
            co.setValorUtilizado(liberacaoCotaOrcamentariaFacade.getReprocessamentoCotaOrcamentariaFacade().recuperarValorUtilizadoNaCota(co, sistemaControlador.getExercicioCorrente()));
            co.setSaldo((co.getValorProgramado().subtract(co.getValorUtilizado())).add(saldoAnteriorCota));
            saldoAnteriorCota = co.getSaldo();
        }
    }

    public void calcularPercentualPorProgramado(CotaOrcamentaria cota) {
        CotaOrcamentaria co = cota;
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        BigDecimal prog = co.getValorProgramado();
        BigDecimal saldo = getSaldoGrupoOrc();
        valor = (prog.multiply(new BigDecimal(100))).divide(saldo, 2, RoundingMode.HALF_UP);
        co.setPercentual(valor);
    }

    public ConverterAutoComplete getConverterGrupoOrcamentario() {
        if (converterGrupoOrcamentario == null) {
            converterGrupoOrcamentario = new ConverterAutoComplete(GrupoOrcamentario.class, liberacaoCotaOrcamentariaFacade.getGrupoOrcamentarioFacade());
        }
        return converterGrupoOrcamentario;
    }

    public ConverterAutoComplete getConverterUnidadeOrganizacional() {
        if (converterUnidadeOrganizacional == null) {
            converterUnidadeOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, liberacaoCotaOrcamentariaFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterUnidadeOrganizacional;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<CotaOrcamentaria> getCotasCotaOrcamentarias() {
        return cotasCotaOrcamentarias;
    }

    public void setCotasCotaOrcamentarias(List<CotaOrcamentaria> cotasCotaOrcamentarias) {
        this.cotasCotaOrcamentarias = cotasCotaOrcamentarias;
    }
}
