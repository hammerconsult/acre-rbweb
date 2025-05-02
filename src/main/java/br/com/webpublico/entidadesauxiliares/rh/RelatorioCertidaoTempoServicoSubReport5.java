package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class RelatorioCertidaoTempoServicoSubReport5 {

    private BigDecimal totalExercicio;
    private BigDecimal totalAverbado;
    private BigDecimal totalGeral;
    private String totalGeralExtenso;
    private BigDecimal anos;
    private BigDecimal meses;
    private BigDecimal dias;
    private String anoMesDiaExtenso;

    public static List<RelatorioCertidaoTempoServicoSubReport5> preencherDados(List<Object[]> objs) {
        List<RelatorioCertidaoTempoServicoSubReport5> retorno = Lists.newArrayList();
        for (Object[] obj : objs) {
            RelatorioCertidaoTempoServicoSubReport5 rel = new RelatorioCertidaoTempoServicoSubReport5();
            rel.setTotalExercicio((BigDecimal) obj[0]);
            rel.setTotalAverbado((BigDecimal) obj[1]);
            rel.setTotalGeral((BigDecimal) obj[2]);
            rel.setTotalGeralExtenso((String) obj[3]);
            rel.setAnos((BigDecimal) obj[4]);
            rel.setMeses((BigDecimal) obj[5]);
            rel.setDias((BigDecimal) obj[6]);
            rel.setAnoMesDiaExtenso(DataUtil.totalDeDiasEmAnosMesesDias(Integer.parseInt(obj[2].toString())));
            retorno.add(rel);
        }
        return retorno;
    }

    public BigDecimal getTotalExercicio() {
        return totalExercicio;
    }

    public void setTotalExercicio(BigDecimal totalExercicio) {
        this.totalExercicio = totalExercicio;
    }

    public BigDecimal getTotalAverbado() {
        return totalAverbado;
    }

    public void setTotalAverbado(BigDecimal totalAverbado) {
        this.totalAverbado = totalAverbado;
    }

    public BigDecimal getTotalGeral() {
        return totalGeral;
    }

    public void setTotalGeral(BigDecimal totalGeral) {
        this.totalGeral = totalGeral;
    }

    public String getTotalGeralExtenso() {
        return totalGeralExtenso;
    }

    public void setTotalGeralExtenso(String totalGeralExtenso) {
        this.totalGeralExtenso = totalGeralExtenso;
    }

    public BigDecimal getAnos() {
        return anos;
    }

    public void setAnos(BigDecimal anos) {
        this.anos = anos;
    }

    public BigDecimal getMeses() {
        return meses;
    }

    public void setMeses(BigDecimal meses) {
        this.meses = meses;
    }

    public BigDecimal getDias() {
        return dias;
    }

    public void setDias(BigDecimal dias) {
        this.dias = dias;
    }

    public String getAnoMesDiaExtenso() {
        return anoMesDiaExtenso;
    }

    public void setAnoMesDiaExtenso(String anoMesDiaExtenso) {
        this.anoMesDiaExtenso = anoMesDiaExtenso;
    }
}
