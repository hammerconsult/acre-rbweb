package br.com.webpublico.util;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Munif
 */
public class AtributoMetadata implements Comparable<AtributoMetadata>, Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(AtributoMetadata.class);
    private String etiqueta;
    private String alinhamento;
    private transient Field atributo;
    private Integer ordem;
    private int max = 15;

    public AtributoMetadata(Field f) {
        atributo = f;
        ordem = 1;
        if (f.isAnnotationPresent(Tabelavel.class)) {
            Tabelavel a = f.getAnnotation(Tabelavel.class);
            alinhamento = a.ALINHAMENTO().getValor();
        }
        if (f.isAnnotationPresent(Etiqueta.class)) {
            Etiqueta e = f.getAnnotation(Etiqueta.class);
            etiqueta = e.value();
        } else {
            String nome = f.getName();
            etiqueta = nome.substring(0, 1).toUpperCase() + nome.substring(1);
        }
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getAlinhamento() {
        return alinhamento;
    }

    public void setAlinhamento(String alinhamento) {
        this.alinhamento = alinhamento;
    }

    public Field getAtributo() {
        return atributo;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getString(Object objeto) {
        if (objeto == null) {
            return "";
        }
        atributo.setAccessible(true);
        try {
            Object valor = atributo.get(objeto);
            if (valor == null) {
                return "";
            }
            if (atributo.isAnnotationPresent(Tabelavel.class)) {
                Tabelavel annotation = atributo.getAnnotation(Tabelavel.class);
                if (annotation.TIPOCAMPO().equals(Tabelavel.TIPOCAMPO.DATA)) {
                    return Util.formatterDiaMesAno.format(valor);
                }
                if (annotation.TIPOCAMPO().equals(Tabelavel.TIPOCAMPO.NUMERO)) {
                    return new DecimalFormat("#,##0.00").format(valor);
                }
                if (annotation.TIPOCAMPO().equals(Tabelavel.TIPOCAMPO.DECIMAL_QUATRODIGITOS)) {
                    return new DecimalFormat("#,####0.0000").format(valor);
                }

            }
            if (atributo.isAnnotationPresent(br.com.webpublico.util.anotacoes.Monetario.class)) {
                NumberFormat nf = NumberFormat.getCurrencyInstance();
                nf.setMinimumFractionDigits(2);
                nf.setMaximumFractionDigits(3);
                return nf.format(new BigDecimal(valor.toString()));
            }
            if (atributo.isAnnotationPresent(br.com.webpublico.util.anotacoes.UFM.class)) {
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMinimumFractionDigits(2);
                nf.setMaximumFractionDigits(4);
                return nf.format(new BigDecimal(valor.toString()));
            }
            if (atributo.isAnnotationPresent(br.com.webpublico.util.anotacoes.Porcentagem.class)) {
                NumberFormat nf = NumberFormat.getPercentInstance();
                nf.setMinimumFractionDigits(2);
                nf.setMaximumFractionDigits(4);
                return nf.format(new BigDecimal(valor.toString()).divide(new BigDecimal("100")));
            }
            if (atributo.getType().equals(Date.class)) {
                Temporal t = atributo.getAnnotation(Temporal.class);
                if (t.value() == TemporalType.TIME) {
                    return Util.formatterHoraMinuto.format(valor);
                }
                if (t.value() == TemporalType.DATE) {
                    return Util.formatterDiaMesAno.format(valor);
                } else {
                    return Util.formatterDataHora.format(valor);
                }

            }
            if (atributo.getType().equals(Boolean.class)) {
                if (valor.equals(true)) {
                    return "Sim";
                } else {
                    return "Não";
                }
            }
            if ((atributo.getType().equals(List.class)) || (atributo.getType().equals(Set.class))) {
                String lista = valor.toString().replaceAll("\\[", "<table class='tabelaInterna'><tr><td>");
                lista = lista.replaceAll(",", "</td></tr><tr><td>");
                lista = lista.replaceAll("]", "</td></tr></table>");
                return lista;
            }
            if ((atributo.getType().equals(Map.class))) {
                String lista = valor.toString().replaceAll("\\{", "<table><tr><td>");
                lista = lista.replaceAll(",", "</td></tr><tr><td>");
                lista = lista.replaceAll("}", "</td></tr></table>");
                return lista;
            }
            return valor.toString();
        } catch (Exception ex) {
            logger.debug("Problemas na conversão de atributos", ex);
        }
        return "?" + objeto.toString();
    }

    public int getMax() {
        max = 15;
        Size s = atributo.getAnnotation(Size.class);
        if (s != null) {
            max = s.max();
        }
        if (max > 70) {
            max = 70;
        }
        return max;
    }

    @Override
    public int compareTo(AtributoMetadata o) {
        try {
            return this.getOrdem().compareTo(o.getOrdem());
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }
}
