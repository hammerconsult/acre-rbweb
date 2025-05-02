package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.DateUtils;
import br.com.webpublico.StringUtils;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.RegimeTributario;
import br.com.webpublico.enums.TipoIssqn;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.nfse.enums.Exigibilidade;
import br.com.webpublico.nfse.enums.SituacaoNota;
import br.com.webpublico.util.Util;
import com.beust.jcommander.internal.Lists;
import org.apache.logging.log4j.util.Strings;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class FiltroReceitaContribuinte {

    private Mes mesInicial;
    private Exercicio exercicioInicial;
    private Mes mesFinal;
    private Exercicio exercicioFinal;
    private BigDecimal totalNotaInicial;
    private BigDecimal totalNotaFinal;
    private String cpfCnpjInicial;
    private String cpfCnpjFinal;
    private List<RegimeTributario> regimesTributario;
    private RegimeTributario regimeTributario;
    private Exigibilidade exigibilidade;
    private List<TipoIssqn> tiposIssqn;
    private TipoIssqn tipoIssqn;
    private List<Exigibilidade> exigibilidades;

    public FiltroReceitaContribuinte() {
        regimesTributario = Lists.newArrayList();
        exigibilidades = Lists.newArrayList();
        tiposIssqn = Lists.newArrayList();
    }

    public Mes getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(Mes mesInicial) {
        this.mesInicial = mesInicial;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Mes getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(Mes mesFinal) {
        this.mesFinal = mesFinal;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public String getCpfCnpjInicial() {
        return cpfCnpjInicial;
    }

    public void setCpfCnpjInicial(String cpfCnpjInicial) {
        this.cpfCnpjInicial = cpfCnpjInicial;
    }

    public String getCpfCnpjFinal() {
        return cpfCnpjFinal;
    }

    public void setCpfCnpjFinal(String cpfCnpjFinal) {
        this.cpfCnpjFinal = cpfCnpjFinal;
    }

    public List<RegimeTributario> getRegimesTributario() {
        return regimesTributario;
    }

    public void setRegimesTributario(List<RegimeTributario> regimesTributario) {
        this.regimesTributario = regimesTributario;
    }

    public RegimeTributario getRegimeTributario() {
        return regimeTributario;
    }

    public void setRegimeTributario(RegimeTributario regimeTributario) {
        this.regimeTributario = regimeTributario;
    }

    public Exigibilidade getExigibilidade() {
        return exigibilidade;
    }

    public void setExigibilidade(Exigibilidade exigibilidade) {
        this.exigibilidade = exigibilidade;
    }

    public BigDecimal getTotalNotaInicial() {
        return totalNotaInicial;
    }

    public void setTotalNotaInicial(BigDecimal totalNotaInicial) {
        this.totalNotaInicial = totalNotaInicial;
    }

    public BigDecimal getTotalNotaFinal() {
        return totalNotaFinal;
    }

    public void setTotalNotaFinal(BigDecimal totalNotaFinal) {
        this.totalNotaFinal = totalNotaFinal;
    }

    public TipoIssqn getTipoIssqn() {
        return tipoIssqn;
    }

    public void setTipoIssqn(TipoIssqn tipoIssqn) {
        this.tipoIssqn = tipoIssqn;
    }

    public List<TipoIssqn> getTiposIssqn() {
        return tiposIssqn;
    }

    public void setTiposIssqn(List<TipoIssqn> tiposIssqn) {
        this.tiposIssqn = tiposIssqn;
    }

    public List<Exigibilidade> getExigibilidades() {
        return exigibilidades;
    }

    public void setExigibilidades(List<Exigibilidade> exigibilidades) {
        this.exigibilidades = exigibilidades;
    }

    public void addExigibilidade() {
        add(exigibilidades, exigibilidade);
        exigibilidade = null;
    }

    public void addTipoIss() {
        add(tiposIssqn, tipoIssqn);
        tipoIssqn = null;
    }

    public void removeExigibilidade(Exigibilidade exigibilidade) {
        remove(exigibilidades, exigibilidade);
    }

    public void addRegimeTributario() {
        add(regimesTributario, regimeTributario);
        regimeTributario = null;
    }

    public void removeRegimeTributario(RegimeTributario regimeTributario) {
        remove(regimesTributario, regimeTributario);
    }

    public void removeRegimeTipoIssqn(TipoIssqn tipoIssqn) {
        remove(tiposIssqn, tipoIssqn);
    }

    public void add(List list, Object obj) {
        if (obj == null)
            return;
        if (list == null)
            list = Lists.newArrayList();
        list.add(obj);
    }

    public void remove(List list, Object obj) {
        list.remove(obj);
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();

        if (((exercicioInicial == null || mesInicial == null) || (exercicioFinal == null || mesFinal == null))) {
            ve.adicionarMensagemDeCampoObrigatorio("A competência é obrigatório.");
        }

        ve.lancarException();
    }


    public String getHaving() {
        String retorno = "";
        if (totalNotaInicial != null && totalNotaInicial.compareTo(BigDecimal.ZERO) != 0) {
            retorno += "having sum(dec.totalnota) >= " + totalNotaInicial + "";
        }
        if (totalNotaFinal != null && totalNotaFinal.compareTo(BigDecimal.ZERO) != 0) {
            retorno += (retorno.isEmpty() ? "having" : "and") + " sum(dec.totalnota) <= " + totalNotaFinal + "";
        }
        return retorno;
    }

    public String getWhere() {
        Date competenciaInicial = DateUtils.getData(1, mesInicial.getNumeroMes(), exercicioInicial.getAno());
        Date competenciaFinal = DateUtils.getData(1, mesFinal.getNumeroMes(), exercicioFinal.getAno());
        competenciaFinal = DateUtils.getUltimoDiaMes(competenciaFinal).getTime();


        String where = " where dec.situacao in ('" + SituacaoNota.EMITIDA.name() + "', '" + SituacaoNota.PAGA.name() + "') " +
            "  and trunc(dec.competencia) between to_date('" + DateUtils.getDataFormatada(competenciaInicial) + "', 'dd/MM/yyyy') " +
            "  and to_date('" + DateUtils.getDataFormatada(competenciaFinal) + "', 'dd/MM/yyyy') ";

        if (Strings.isNotEmpty(cpfCnpjInicial)) {
            where += " and coalesce(pf.cpf, pj.cnpj) >= '" + StringUtils.removerMascaraCpfCnpj(cpfCnpjInicial) + "' ";
        }

        if (Strings.isNotEmpty(cpfCnpjFinal)) {
            where += " and coalesce(pf.cpf, pj.cnpj) <= '" + StringUtils.removerMascaraCpfCnpj(cpfCnpjFinal) + "' ";
        }

        if (tiposIssqn != null && !tiposIssqn.isEmpty()) {
            List<String> in = Lists.newArrayList();
            for (TipoIssqn tipo : tiposIssqn) {
                in.add("'" + tipo.name() + "'");
            }
            where += " and enq.TIPOISSQN in (" + org.apache.commons.lang.StringUtils.join(in, ',') + ") ";
        }

        if (regimesTributario != null && !regimesTributario.isEmpty()) {
            List<String> in = Lists.newArrayList();
            for (RegimeTributario regime : regimesTributario) {
                in.add("'" + regime.name() + "'");
            }
            where += " and enq.regimetributario in (" + org.apache.commons.lang.StringUtils.join(in, ',') + ") ";
        }

        if (exigibilidades != null && !exigibilidades.isEmpty()) {
            List<String> in = Lists.newArrayList();
            for (Exigibilidade exigibilidade : exigibilidades) {
                in.add("'" + exigibilidade.name() + "'");
            }
            where += " and dec.naturezaoperacao in (" + org.apache.commons.lang.StringUtils.join(in, ',') + ") ";
        }

        return where;
    }

    public String getFiltros() {
        String filtros = " Competência Inicial: " + mesInicial.getNumeroMes() + "/" + exercicioInicial.getAno() + ";";
        filtros += " Competência Final: " + mesFinal.getNumeroMes() + "/" + exercicioFinal.getAno() + ";";

        if (Strings.isNotEmpty(cpfCnpjInicial)) {
            filtros += " CPF/CNPJ Inicial: " + cpfCnpjInicial.trim() + ";";
        }

        if (Strings.isNotEmpty(cpfCnpjFinal)) {
            filtros += " CPF/CNPJ Final: " + cpfCnpjInicial.trim() + ";";
        }

        if (tiposIssqn != null && !tiposIssqn.isEmpty()) {
            filtros += " Tipo(s) ISSQN: " + org.apache.commons.lang.StringUtils.join(Util.getListOfEnumDescricao(tiposIssqn), ',');
        }

        if (regimesTributario != null && !regimesTributario.isEmpty()) {
            filtros += " Regime(s) Tributário(s): " + org.apache.commons.lang.StringUtils.join(Util.getListOfEnumDescricao(regimesTributario), ',');
        }

        if (exigibilidades != null && !exigibilidades.isEmpty()) {
            filtros += " Natureza(s) Operação(ões): " + org.apache.commons.lang.StringUtils.join(Util.getListOfEnumDescricao(exigibilidades), ',');
        }

        return filtros;
    }
}
