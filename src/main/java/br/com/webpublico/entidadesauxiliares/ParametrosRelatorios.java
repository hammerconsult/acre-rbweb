package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.webreportdto.dto.comum.ParametrosRelatoriosDTO;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 29/05/14
 * Time: 17:33
 * To change this template use File | Settings | File Templates.
 */
public class ParametrosRelatorios {

    private String nomeAtributo;
    private String campo1;
    private String campo2;
    private OperacaoRelatorio operacao;
    private Object valor1;
    private Object valor2;
    private Integer local;
    private Boolean ehData;
    private Class classe;
    private String subQuery;

    public ParametrosRelatorios() {
        this.ehData = false;
    }

    public ParametrosRelatorios(String nomeAtributo, String campo1, String campo2, OperacaoRelatorio operacao, Object valor1, Object valor2, Integer local, Boolean ehData) {
        this.nomeAtributo = nomeAtributo;
        this.campo1 = campo1;
        this.campo2 = campo2;
        this.operacao = operacao;
        this.valor1 = valor1;
        this.valor2 = valor2;
        this.local = local;
        this.ehData = ehData;
    }

    public ParametrosRelatorios(String nomeAtributo, String campo1, String campo2, OperacaoRelatorio operacao, Object valor1, Object valor2, Integer local, Boolean ehData, Class classe) {
        this.nomeAtributo = nomeAtributo;
        this.campo1 = campo1;
        this.campo2 = campo2;
        this.operacao = operacao;
        this.valor1 = valor1;
        this.valor2 = valor2;
        this.local = local;
        this.ehData = ehData;
        this.classe = classe;
    }

    public ParametrosRelatorios(String subQuery, Integer local) {
        this.subQuery = subQuery;
        this.local = local;
        this.ehData = Boolean.FALSE;
    }

    public static List<ParametrosRelatoriosDTO> parametrosToDto(List<ParametrosRelatorios> parametrosRelatorios) {
        List<ParametrosRelatoriosDTO> toReturn = Lists.newArrayList();
        for (ParametrosRelatorios parametrosRelatorio : parametrosRelatorios) {
            toReturn.add(parametroToDto(parametrosRelatorio));
        }
        return toReturn;
    }

    public static ParametrosRelatoriosDTO parametroToDto(ParametrosRelatorios parametrosRelatorio) {
        ParametrosRelatoriosDTO parametro = new ParametrosRelatoriosDTO();
        parametro.setCampo1(parametrosRelatorio.getCampo1SemFormatacao());
        parametro.setCampo2(parametrosRelatorio.getCampo2SemFormatacao());
        parametro.setLocal(parametrosRelatorio.getLocal());
        parametro.setData(parametrosRelatorio.getEhData());
        parametro.setNomeAtributo(parametrosRelatorio.getNomeAtributo());
        parametro.setOperacao(parametrosRelatorio.getOperacao() != null ? parametrosRelatorio.getOperacao().getToDto() : null);
        parametro.setValor1(parametrosRelatorio.getValor1());
        parametro.setValor2(parametrosRelatorio.getValor2());
        parametro.setClasse(parametrosRelatorio.getClasse());
        parametro.setSubQuery(parametrosRelatorio.getSubQuery());
        return parametro;
    }


    public static List<ParametrosRelatorios> dtoToParametros(List<ParametrosRelatoriosDTO> parametrosRelatorios) {
        List<ParametrosRelatorios> toReturn = Lists.newArrayList();
        for (ParametrosRelatoriosDTO parametrosRelatorio : parametrosRelatorios) {
            toReturn.add(dtoToParametro(parametrosRelatorio));
        }
        return toReturn;
    }

    private static ParametrosRelatorios dtoToParametro(ParametrosRelatoriosDTO parametrosRelatorio) {
        ParametrosRelatorios parametrosRelatorios = new ParametrosRelatorios();
        parametrosRelatorios.setCampo1(parametrosRelatorio.getCampo1());
        parametrosRelatorios.setCampo2(parametrosRelatorio.getCampo2());
        parametrosRelatorios.setLocal(parametrosRelatorio.getLocal());
        parametrosRelatorios.setEhData(parametrosRelatorio.isData());
        parametrosRelatorios.setNomeAtributo(parametrosRelatorio.getNomeAtributo());
        parametrosRelatorios.setOperacao(parametrosRelatorio.getOperacao() != null ? OperacaoRelatorio.valueOf(parametrosRelatorio.getOperacao().name()) : null);
        parametrosRelatorios.setValor1(parametrosRelatorio.getValor1());
        parametrosRelatorios.setValor2(parametrosRelatorio.getValor2());
        parametrosRelatorios.setClasse(parametrosRelatorio.getClasse());
        return parametrosRelatorios;
    }

    public String getNomeAtributo() {
        return nomeAtributo;
    }

    public void setNomeAtributo(String nomeAtributo1) {
        this.nomeAtributo = nomeAtributo1;
    }

    public String getCampo1SemDoisPontos() {
        return campo1.replace(":", "");
    }

    public String getCampo1() {
        try {
            if (campo1 != null) {
                if (ehData) {
                    return "to_date(" + campo1 + ",'dd/mm/yyyy')";
                } else if (operacao.equals(OperacaoRelatorio.IN)) {
                    return "(" + campo1 + ")";
                }
            }
            return campo1;
        } catch (Exception ex) {
            return campo1;
        }
    }

    public String getCampo1SemFormatacao() {
        return campo1;
    }

    public void setCampo1(String campo1) {
        this.campo1 = campo1;
    }

    public String getCampo2SemDoisPontos() {
        return campo2.replace(":", "");
    }

    public String getCampo2() {
        try {
            if (campo2 != null) {
                if (ehData) {
                    return "to_date(" + campo2 + ",'dd/mm/yyyy')";
                } else if (operacao.equals(OperacaoRelatorio.IN)) {
                    return "(" + campo2 + ")";
                }
            }
            return campo2;
        } catch (Exception ex) {
            return campo2;
        }
    }

    public String getCampo2SemFormatacao() {
        return campo2;
    }

    public void setCampo2(String campo2) {
        this.campo2 = campo2;
    }

    public Object getValor1() {
        return valor1;
    }

    public void setValor1(Object valor1) {
        this.valor1 = valor1;
    }

    public Object getValor2() {
        return valor2;
    }

    public void setValor2(Object valor2) {
        this.valor2 = valor2;
    }

    public OperacaoRelatorio getOperacao() {
        return operacao;
    }

    public void setOperacao(OperacaoRelatorio operacao) {
        this.operacao = operacao;
    }

    public Integer getLocal() {
        return local;
    }

    public void setLocal(Integer local) {
        this.local = local;
    }

    public Boolean getEhData() {
        return ehData;
    }

    public void setEhData(Boolean ehData) {
        this.ehData = ehData;
    }

    public Class getClasse() {
        return classe;
    }

    public void setClasse(Class classe) {
        this.classe = classe;
    }

    public String getSubQuery() {
        return subQuery;
    }

    public void setSubQuery(String subQuery) {
        this.subQuery = subQuery;
    }

    @Override
    public String toString() {
        return "ParametrosRelatorios{" +
            "nomeAtributo='" + nomeAtributo + '\'' +
            ", campo1='" + campo1 + '\'' +
            ", campo2='" + campo2 + '\'' +
            ", operacao=" + operacao +
            ", valor1=" + valor1 +
            ", valor2=" + valor2 +
            ", local=" + local +
            ", ehData=" + ehData +
            '}';
    }
}
