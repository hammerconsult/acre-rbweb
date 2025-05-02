package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class RelatorioCertidaoTempoServicoSubReport2 {

    private BigDecimal totalDias;
    private String extenso;
    private String anoMesDiaExtenso;
    private BigDecimal anos;
    private BigDecimal meses;
    private BigDecimal dias;

    public static List<RelatorioCertidaoTempoServicoSubReport2> preencherDados(List<Object[]> objs) {
        List<RelatorioCertidaoTempoServicoSubReport2> retorno = Lists.newArrayList();
        for (Object[] obj : objs) {
            RelatorioCertidaoTempoServicoSubReport2 rel = new RelatorioCertidaoTempoServicoSubReport2();
            rel.setTotalDias((BigDecimal) obj[0]);
            rel.setExtenso((String) obj[1]);
            rel.setAnos((BigDecimal) obj[2]);
            rel.setMeses((BigDecimal) obj[3]);
            rel.setDias((BigDecimal) obj[4]);
            rel.setAnoMesDiaExtenso(DataUtil.totalDeDiasEmAnosMesesDias(Integer.parseInt(obj[0].toString())));
            retorno.add(rel);
        }
        return retorno;
    }

    public BigDecimal getTotalDias() {
        return totalDias;
    }

    public void setTotalDias(BigDecimal totalDias) {
        this.totalDias = totalDias;
    }

    public String getExtenso() {
        return extenso;
    }

    public void setExtenso(String extenso) {
        this.extenso = extenso;
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
