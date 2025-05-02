/*
 * Codigo gerado automaticamente em Mon Jan 23 15:16:50 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.GrupoCotaORCFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MoneyConverter;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ManagedBean(name = "grupoCotaORCControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-cota-orcamentaria", pattern = "/cota-orcamentaria/novo/", viewId = "/faces/financeiro/orcamentario/cotaorcamentaria/edita.xhtml"),
    @URLMapping(id = "editar-cota-orcamentaria", pattern = "/cota-orcamentaria/editar/#{grupoCotaORCControlador.id}/", viewId = "/faces/financeiro/orcamentario/cotaorcamentaria/edita.xhtml"),
    @URLMapping(id = "ver-cota-orcamentaria", pattern = "/cota-orcamentaria/ver/#{grupoCotaORCControlador.id}/", viewId = "/faces/financeiro/orcamentario/cotaorcamentaria/visualizar.xhtml"),
    @URLMapping(id = "listar-cota-orcamentaria", pattern = "/cota-orcamentaria/listar/", viewId = "/faces/financeiro/orcamentario/cotaorcamentaria/lista.xhtml")
})
public class GrupoCotaORCControlador extends PrettyControlador<GrupoCotaORC> implements Serializable, CRUD {

    @EJB
    private GrupoCotaORCFacade grupoCotaORCFacade;
    private ConverterAutoComplete converterAtoLegal;
    private ConverterAutoComplete converterGrupoOrcamentario;
    private MoneyConverter moneyConverter;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private BigDecimal saldoGrupoOrcamentario;

    public GrupoCotaORCControlador() {
        super(GrupoCotaORC.class);
    }

    public GrupoCotaORCFacade getFacade() {
        return grupoCotaORCFacade;
    }

    @URLAction(mappingId = "novo-cota-orcamentaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-cota-orcamentaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        cotasOrcamentarias();
    }

    @URLAction(mappingId = "editar-cota-orcamentaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        cotasOrcamentarias();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cota-orcamentaria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return grupoCotaORCFacade;
    }

    @Override
    public void salvar() {
        try {
            if (Util.validaCampos(selecionado)
                && validarCemPorcento()) {
                if (operacao.equals(Operacoes.NOVO)) {
                    grupoCotaORCFacade.salvarNovo(selecionado);
                    FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "Registro salvo com sucesso.");
                } else {
                    grupoCotaORCFacade.salvar(selecionado);
                    FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "Registro alterado com sucesso.");
                }
                redireciona();
            }
        } catch (Exception ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
        }
    }

    public void setSaldoGrupoOrcamentario(BigDecimal saldoGrupoOrcamentario) {
        this.saldoGrupoOrcamentario = saldoGrupoOrcamentario;
    }

    public Boolean validaLancamentosDeCotas() {
        if (calculaDiferenca().equals(new BigDecimal(BigInteger.ZERO))) {
            return true;
        }
        return false;
    }

    public void calcularSaldoCumulativo() {
        BigDecimal saldoAnteriorCota = BigDecimal.ZERO;
        for (CotaOrcamentaria co : selecionado.getCotaOrcamentaria()) {
            calcularPercentualPorProgramado(co);
            co.setValorUtilizado(grupoCotaORCFacade.getReprocessamentoCotaOrcamentariaFacade().recuperarValorUtilizadoNaCota(co, sistemaControlador.getExercicioCorrente()));
            co.setSaldo((co.getValorProgramado().subtract(co.getValorUtilizado())).add(saldoAnteriorCota));
            saldoAnteriorCota = co.getSaldo();
        }
    }

    public BigDecimal calculaDiferenca() {
        BigDecimal dif = new BigDecimal(BigInteger.ZERO);
        dif = getSaldoGrupoOrcamentario().subtract(somaTotaisProgramados());
        return dif;
    }

    public void calculaPorPercentual(CotaOrcamentaria cota) {
        CotaOrcamentaria co = cota;
        if (!validarCemPorcento()) return;
        try {
            BigDecimal valor = (getSaldoGrupoOrc().multiply(co.getPercentual())).divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_EVEN);
            co.setValorProgramado(valor);
        } catch (Exception e) {
            co.setValorProgramado(BigDecimal.ZERO);
        }
    }

    public boolean validarCemPorcento() {
        if (somaTotaisPercentuais().compareTo(BigDecimal.valueOf(100)) > 0) {
            FacesUtil.addAtencao("O percentual total excedeu 100%");
            return false;
        }
        return true;
    }

    public void calcularPercentualPorProgramado(CotaOrcamentaria cota) {
        CotaOrcamentaria co = cota;
        if (validarValorProgramado(cota)) {
            if (!validarCemPorcento()) return;
            try {
                BigDecimal valor = new BigDecimal(BigInteger.ZERO);
                BigDecimal prog = co.getValorProgramado();
                BigDecimal saldo = getSaldoGrupoOrc();
                valor = (prog.multiply(new BigDecimal(100))).divide(saldo, 2, RoundingMode.HALF_UP);
                co.setPercentual(valor);
            } catch (Exception e) {
                co.setValorProgramado(BigDecimal.ZERO);
            }
        }
    }

    private boolean validarValorProgramado(CotaOrcamentaria cotaOrcamentaria) {
        if (cotaOrcamentaria.getValorProgramado().compareTo(getSaldoGrupoOrc()) > 0) {
            FacesUtil.addAtencao("O valor programado excedeu o valor total para esse grupo orçamentário.");
            cotaOrcamentaria.setValorProgramado(BigDecimal.ZERO);
            return false;
        }
        return true;
    }

    public BigDecimal somaSaldosTotais() {
        GrupoCotaORC gco = ((GrupoCotaORC) selecionado);
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (CotaOrcamentaria co : gco.getCotaOrcamentaria()) {
            try {
                soma = soma.add(co.getValorProgramado().subtract(co.getValorUtilizado()));
            } catch (Exception e) {

            }
        }
        return soma;
    }

    public BigDecimal somaTotaisPercentuais() {
        GrupoCotaORC gco = ((GrupoCotaORC) selecionado);
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (CotaOrcamentaria co : gco.getCotaOrcamentaria()) {
            try {
                soma = soma.add(co.getPercentual());
            } catch (Exception e) {

            }
        }
        return soma;
    }

    public BigDecimal somaTotaisUtilizados() {
        GrupoCotaORC gco = ((GrupoCotaORC) selecionado);
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (CotaOrcamentaria co : gco.getCotaOrcamentaria()) {
            soma = soma.add(co.getValorUtilizado());
        }
        return soma;
    }

    public BigDecimal getSaldoRestante() {
        GrupoCotaORC gco = ((GrupoCotaORC) selecionado);
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (CotaOrcamentaria co : gco.getCotaOrcamentaria()) {
            soma = co.getSaldo();
        }
        return soma;
    }

    public BigDecimal somaTotaisProgramados() {
        GrupoCotaORC gco = ((GrupoCotaORC) selecionado);
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (CotaOrcamentaria co : gco.getCotaOrcamentaria()) {
            try {
                soma = soma.add(co.getValorProgramado());
            } catch (Exception e) {

            }
        }
        return soma;
    }

    public Boolean getLiberaDistribuicao() {
        GrupoCotaORC gco = ((GrupoCotaORC) selecionado);
        if (gco.getGrupoOrcamentario() != null) {
            return true;
        }
        return false;
    }

    public BigDecimal getSaldoGrupoOrcamentario() {
        GrupoCotaORC gco = ((GrupoCotaORC) selecionado);
        if (gco.getGrupoOrcamentario() != null) {
            if (saldoGrupoOrcamentario == null) {
                if (somaTotaisUtilizados().compareTo(BigDecimal.ZERO) == 0) {
                    saldoGrupoOrcamentario = grupoCotaORCFacade.getFonteDespesaORCFacade().recuperarSaldoCalculadoGrupoOrc(gco.getGrupoOrcamentario(), sistemaControlador.getDataOperacao());
                } else {
                    saldoGrupoOrcamentario = somaTotaisProgramados().subtract(somaTotaisUtilizados());
                }
            }
            return saldoGrupoOrcamentario;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getSaldoGrupoOrc() {
        GrupoCotaORC gco = ((GrupoCotaORC) selecionado);
        if (gco.getGrupoOrcamentario() != null) {
            saldoGrupoOrcamentario = grupoCotaORCFacade.getFonteDespesaORCFacade().recuperarSaldoAtualPorGrupoOrc(gco.getGrupoOrcamentario());
            return saldoGrupoOrcamentario;
        }
        return BigDecimal.ZERO;
    }

    public void cotasOrcamentarias() {
        if (isOperacaoNovo()) {
            selecionado.getCotaOrcamentaria().clear();
            Integer num = 1;
            for (Mes m : Mes.values()) {
                CotaOrcamentaria co = new CotaOrcamentaria();
                co.setMes(m);
                co.setGrupoCotaORC(selecionado);
                co.setIndice(num);
                selecionado.getCotaOrcamentaria().add(co);
                num = num + 1;
            }
        }
        ordenarCotasOrcamentarias();
        calcularSaldoCumulativo();
    }

    private void ordenarCotasOrcamentarias() {
        Collections.sort(selecionado.getCotaOrcamentaria(), new Comparator() {
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
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return grupoCotaORCFacade.getAtoLegalFacade().listaFiltrando(parte.trim(), "nome");
    }

    public ConverterAutoComplete getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, grupoCotaORCFacade.getAtoLegalFacade());
        }
        return converterAtoLegal;
    }

    public List<GrupoOrcamentario> completaGrupoOrcamentario(String parte) {
        return grupoCotaORCFacade.getGrupoOrcamentarioFacade().listaSemCotasPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente(), "codigo", "descricao");
    }

    public ConverterAutoComplete getConverterGrupoOrcamentario() {
        if (converterGrupoOrcamentario == null) {
            converterGrupoOrcamentario = new ConverterAutoComplete(GrupoOrcamentario.class, grupoCotaORCFacade.getCotaOrcamentariaFacade());
        }
        return converterGrupoOrcamentario;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public boolean possuiValorUtilizado() {
        try {
            if (somaTotaisUtilizados().compareTo(BigDecimal.ZERO) > 0) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

    @Override
    public RevisaoAuditoria getUltimaRevisao() {
        if (ultimaRevisao == null) {
            ultimaRevisao = buscarUltimaAuditoria();

            if (!selecionado.getCotaOrcamentaria().isEmpty()) {
                for (CotaOrcamentaria cotaOrcamentaria : selecionado.getCotaOrcamentaria()) {
                    RevisaoAuditoria revisaoAuditoria = buscarUltimaAuditoria(CotaOrcamentaria.class, cotaOrcamentaria.getId());
                    if (ultimaRevisao == null || (revisaoAuditoria != null && revisaoAuditoria.getDataHora().after(ultimaRevisao.getDataHora()))) {
                        ultimaRevisao = revisaoAuditoria;
                    }
                }
            }
        }
        return ultimaRevisao;
    }
}
