package br.com.webpublico.controle;

import br.com.webpublico.entidades.AnulacaoORC;
import br.com.webpublico.entidades.CotaOrcamentaria;
import br.com.webpublico.entidades.GrupoOrcamentario;
import br.com.webpublico.entidades.SuplementacaoORC;
import br.com.webpublico.negocios.ReprocessamentoCotaOrcamentariaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edi on 15/04/2015.
 */
@ManagedBean(name = "reprocessamentoCotaOrcamentariaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-reprocessamento-cota-orcamentaria", pattern = "/reprocessamento-cota-orcamentaria/novo/", viewId = "/faces/financeiro/orcamentario/reprocessamento-cota-orcamentaria/edita.xhtml")
})
public class ReprocessamentoCotaOrcamentariaControlador implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private ReprocessamentoCotaOrcamentariaFacade reprocessamentoCotaOrcamentariaFacade;
    private GrupoOrcamentario grupoOrcamentario;
    private ConverterAutoComplete converterGrupoOrcamentario;
    private List<CotaOrcamentaria> cotaOrcamentariasSelecionadas;
    private List<CotaOrcamentaria> cotasCotaOrcamentarias;
    private static final Logger logger = LoggerFactory.getLogger(ReprocessamentoCotaOrcamentariaControlador.class);

    public ReprocessamentoCotaOrcamentariaControlador() {
    }

    @URLAction(mappingId = "novo-reprocessamento-cota-orcamentaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        cotasCotaOrcamentarias = new ArrayList<>();
        cotaOrcamentariasSelecionadas = new ArrayList<>();
    }

    public List<GrupoOrcamentario> completaGrupoOrcamentario(String parte) {
        try {
            return reprocessamentoCotaOrcamentariaFacade.getGrupoOrcamentarioFacade().listaGrupoPorExercicio(sistemaControlador.getExercicioCorrente(), parte.trim());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<CotaOrcamentaria> recuperarCotas() {
        try {
            if (grupoOrcamentario != null) {
                cotasCotaOrcamentarias = reprocessamentoCotaOrcamentariaFacade.getCotaOrcamentariaFacade().recuperarCotaPorGrupoOrcamentario(grupoOrcamentario);
                calculaSaldo();
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
        return new ArrayList<>();
    }

    public Boolean validaCampo() {
        if (grupoOrcamentario == null) {
            FacesUtil.addCampoObrigatorio("O campo Grupo Orçamentário deve ser informado.");
            return false;
        }
        if (cotaOrcamentariasSelecionadas.isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("É obrigatório selecionar ao menos uma cota para reprocessar.");
            return false;
        }
        return true;
    }

    public void reprocessar() {
        try {
            if (validaCampo()) {
                for (CotaOrcamentaria cota : cotaOrcamentariasSelecionadas) {
                    distribuirValorOrcamentoInicialNoProgramado(cota, getOrcamentoInicial());
                    adicinarValorColunaUtilizado(cota);
                    creditaValorCotaOrc(cota);
                    cota = reprocessamentoCotaOrcamentariaFacade.getCotaOrcamentariaFacade().salvarRetornando(cota);
                    debitaValorCotaOrc(cota);
                }
                recalcularPorcentagemGrupoOrc();

                cotaOrcamentariasSelecionadas.clear();
                cotasCotaOrcamentarias.clear();
                grupoOrcamentario = null;
                FacesUtil.addOperacaoRealizada("Reprocessamento realizado com sucesso.");
                FacesUtil.executaJavaScript("setaFoco('Formulario:grupoOrcamentario_input')");
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao reprocessar " + ex.getMessage());
        }
    }

    private void debitaValorCotaOrc(CotaOrcamentaria cota) {
        for (AnulacaoORC anul : getAnulacaoORCs(cota)) {
            debitaValorCotaOrcamentaria(cota, anul.getValor());
        }
    }

    private void creditaValorCotaOrc(CotaOrcamentaria cota) {
        for (SuplementacaoORC sup : getSuplementacaoORCs(cota)) {
            creditaValorCotaOrcamentaria(cota, sup.getValor());
        }
    }

    private BigDecimal getOrcamentoAtualizado() {
        BigDecimal valor = BigDecimal.ZERO;
        if (grupoOrcamentario != null) {
            valor = reprocessamentoCotaOrcamentariaFacade.getFonteDespesaORCFacade().recuperarSaldoAtualPorGrupoOrc(grupoOrcamentario);
        }
        return valor;
    }

    private BigDecimal getOrcamentoInicial() {
        BigDecimal valor = BigDecimal.ZERO;
        if (grupoOrcamentario != null) {
            valor = reprocessamentoCotaOrcamentariaFacade.recuperarValorOrcamentoInicial(grupoOrcamentario);
        }
        return valor;
    }

    private List<SuplementacaoORC> getSuplementacaoORCs(CotaOrcamentaria cota) {
        try {
            return reprocessamentoCotaOrcamentariaFacade.recuperarSuplementacoes(cota);
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
            return new ArrayList<>();
        }
    }

    private List<AnulacaoORC> getAnulacaoORCs(CotaOrcamentaria cota) {
        try {
            return reprocessamentoCotaOrcamentariaFacade.recuperarValorAnulacao(cota);
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
            return new ArrayList<>();
        }
    }

    private BigDecimal getValorAtualizado(CotaOrcamentaria cota) {
        BigDecimal valor = BigDecimal.ZERO;
        valor = reprocessamentoCotaOrcamentariaFacade.recuperarValorUtilizadoNaCota(cota, sistemaControlador.getExercicioCorrente());
        return valor;
    }

    public void recalcularPorcentagemGrupoOrc() {
        BigDecimal saldoGrupoOrc = reprocessamentoCotaOrcamentariaFacade.getFonteDespesaORCFacade().recuperarSaldoAtualPorGrupoOrc(grupoOrcamentario);
        for (CotaOrcamentaria cota : reprocessamentoCotaOrcamentariaFacade.getCotaOrcamentariaFacade().recuperarCotaPorGrupoOrcamentario(grupoOrcamentario)) {
            BigDecimal valorProgramado = cota.getValorProgramado().multiply(new BigDecimal(100));
            if (saldoGrupoOrc.compareTo(BigDecimal.ZERO) > 0) {
                cota.setPercentual(valorProgramado.divide(saldoGrupoOrc, 4, RoundingMode.HALF_UP));
                reprocessamentoCotaOrcamentariaFacade.getCotaOrcamentariaFacade().salvar(cota);
            }
        }
    }

    public void distribuirValorOrcamentoInicialNoProgramado(CotaOrcamentaria cota, BigDecimal orcamentoInicial) {
        cota.setValorProgramado(BigDecimal.ZERO);
        if (cota.getPercentual() != null) {
            if (cota.getPercentual().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal valorCalculado = (orcamentoInicial.multiply(cota.getPercentual())).divide(new BigDecimal(100));
                cota.setValorProgramado(valorCalculado);
            }
        }
    }

    private void creditaValorCotaOrcamentaria(CotaOrcamentaria cota, BigDecimal valorSuplementacao) {
        cota.setValorProgramado(cota.getValorProgramado().add(valorSuplementacao).setScale(2, RoundingMode.HALF_UP));
    }

    private void adicinarValorColunaUtilizado(CotaOrcamentaria cota) {
        cota.setValorUtilizado(cota.getValorUtilizado().add(getValorAtualizado(cota)).setScale(2, RoundingMode.HALF_UP));
    }


    private CotaOrcamentaria debitaValorCotaOrcamentaria(CotaOrcamentaria cota, BigDecimal valorAnulacao) {
        if (valorAnulacao.compareTo(BigDecimal.ZERO) > 0) {
            for (int x = cota.getIndice(); x > 0; x--) {
                CotaOrcamentaria cotaDoMes = reprocessamentoCotaOrcamentariaFacade.getCotaOrcamentariaFacade().recuperarPorGrupoOrcEIndice(grupoOrcamentario, x);
                if (valorAnulacao.compareTo(cotaDoMes.getValorProgramado()) <= 0) {
                    cotaDoMes.setValorProgramado(cotaDoMes.getValorProgramado().subtract(valorAnulacao).setScale(2, RoundingMode.HALF_UP));
                    cotaDoMes = reprocessamentoCotaOrcamentariaFacade.getCotaOrcamentariaFacade().salvarRetornando(cotaDoMes);
                    return cotaDoMes;
                }
            }
        }
        return cota;
    }

    public void reprocessarTodosOsGrupo() {
        try {
            List<GrupoOrcamentario> grupoOrcamentarios = reprocessamentoCotaOrcamentariaFacade.getGrupoOrcamentarioFacade().listaExercicio(sistemaControlador.getExercicioCorrente());
            for (GrupoOrcamentario grupo : grupoOrcamentarios) {
                grupoOrcamentario = grupo;
                List<CotaOrcamentaria> cotaOrcamentarias = reprocessamentoCotaOrcamentariaFacade.getCotaOrcamentariaFacade().recuperarCotaPorGrupoOrcamentario(grupo);
                for (CotaOrcamentaria cota : cotaOrcamentarias) {
                    distribuirValorOrcamentoInicialNoProgramado(cota, getOrcamentoInicial());
                    adicinarValorColunaUtilizado(cota);
                    creditaValorCotaOrc(cota);
                    cota = reprocessamentoCotaOrcamentariaFacade.getCotaOrcamentariaFacade().salvarRetornando(cota);
                    debitaValorCotaOrc(cota);
                }
                recalcularPorcentagemGrupoOrc();
            }
            FacesUtil.addOperacaoRealizada("Reprocessamento de " + grupoOrcamentarios.size() + " grupos realizado(s) com sucesso.");
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Erro ao reprocessar " + ex.getMessage());
        }
    }

    public String icone(CotaOrcamentaria cotaOrcamentaria) {
        if (cotaOrcamentariasSelecionadas.contains(cotaOrcamentaria)) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public String title(CotaOrcamentaria cotaOrcamentaria) {
        if (cotaOrcamentariasSelecionadas.contains(cotaOrcamentaria)) {
            return "Clique para deselecionar esta cota orçamentária.";
        }
        return "Clique para selecionar esta cota orçamentária.";
    }

    public String iconeTodos() {
        if (cotaOrcamentariasSelecionadas.size() == cotasCotaOrcamentarias.size()) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public String titleTodos() {
        if (cotaOrcamentariasSelecionadas.size() == cotasCotaOrcamentarias.size()) {
            return "Clique para deselecionar todas cotas orçamentárias.";
        }
        return "Clique para selecionar todas cotas orçamentárias.";
    }

    public void selecionarCota(CotaOrcamentaria cotaOrcamentaria) {
        if (cotaOrcamentariasSelecionadas.contains(cotaOrcamentaria)) {
            cotaOrcamentariasSelecionadas.remove(cotaOrcamentaria);
        } else {
            cotaOrcamentariasSelecionadas.add(cotaOrcamentaria);
        }
    }

    public void selecionarTodasCotas() {
        if (cotaOrcamentariasSelecionadas.size() == cotasCotaOrcamentarias.size()) {
            cotaOrcamentariasSelecionadas.removeAll(cotasCotaOrcamentarias);
        } else {
            for (CotaOrcamentaria cotaOrcamentaria : cotasCotaOrcamentarias) {
                selecionarCota(cotaOrcamentaria);
            }
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

    public BigDecimal somaTotaisUtilizados() {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (CotaOrcamentaria co : cotasCotaOrcamentarias) {
            soma = soma.add(co.getValorUtilizado());
        }
        return soma;
    }

    public BigDecimal calculaSaldo() {
        BigDecimal saldo = new BigDecimal(BigInteger.ZERO);
        for (CotaOrcamentaria co : cotasCotaOrcamentarias) {
            co.setSaldo((co.getValorProgramado().subtract(co.getValorUtilizado())).add(saldo));
            saldo = co.getSaldo();
        }
        return saldo;
    }

    public ConverterAutoComplete getConverterGrupoOrcamentario() {
        if (converterGrupoOrcamentario == null) {
            converterGrupoOrcamentario = new ConverterAutoComplete(GrupoOrcamentario.class, reprocessamentoCotaOrcamentariaFacade.getGrupoOrcamentarioFacade());
        }
        return converterGrupoOrcamentario;
    }

    public String getExercicio() {
        return sistemaControlador.getExercicio().toString();
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public GrupoOrcamentario getGrupoOrcamentario() {
        return grupoOrcamentario;
    }

    public void setGrupoOrcamentario(GrupoOrcamentario grupoOrcamentario) {
        this.grupoOrcamentario = grupoOrcamentario;
    }

    public List<CotaOrcamentaria> getCotaOrcamentariasSelecionadas() {
        return cotaOrcamentariasSelecionadas;
    }

    public void setCotaOrcamentariasSelecionadas(List<CotaOrcamentaria> cotaOrcamentariasSelecionadas) {
        this.cotaOrcamentariasSelecionadas = cotaOrcamentariasSelecionadas;
    }

    public List<CotaOrcamentaria> getCotasCotaOrcamentarias() {
        return cotasCotaOrcamentarias;
    }

    public void setCotasCotaOrcamentarias(List<CotaOrcamentaria> cotasCotaOrcamentarias) {
        this.cotasCotaOrcamentarias = cotasCotaOrcamentarias;
    }
}
