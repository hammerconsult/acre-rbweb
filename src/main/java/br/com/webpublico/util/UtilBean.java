/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.interfaces.EntidadeContabilComValor;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Usuario
 */
@ManagedBean
@SessionScoped
public class UtilBean implements Serializable {

    private MoneyConverter moneyConverter;
    private ConverterBigDecimal2Casas converterBigDecimal;
    private PorcentagemConverter porcentagemConverter;

    /**
     * Creates a new instance of UtilBean
     */
    public UtilBean() {
    }

    public ConverterBigDecimal2Casas getConverterBigDecimal() {
        if (converterBigDecimal == null) {
            converterBigDecimal = new ConverterBigDecimal2Casas();
        }
        return converterBigDecimal;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public void setMoneyConverter(MoneyConverter moneyConverter) {
        this.moneyConverter = moneyConverter;
    }

    public PorcentagemConverter getPorcentagemConverter() {
        if (porcentagemConverter == null) {
            porcentagemConverter = new PorcentagemConverter();
        }
        return porcentagemConverter;
    }

    public void setPorcentagemConverter(PorcentagemConverter porcentagemConverter) {
        this.porcentagemConverter = porcentagemConverter;
    }

    public String converterBooleanSimOuNao(Boolean valor) {
        return Util.converterBooleanSimOuNao(valor);
    }

    public String localUsuarioAtualmente() {
        return FacesUtil.localUsuarioAtualmente();
    }

    // Ex: http://localhost:8080/webpublico/
    public String geraUrl() {
        return FacesUtil.geraUrlImagemDir();
    }

    public String dataAsString(Date data) {
        if (data == null) {
            return "";
        }
        return Util.dateToString(data);
    }

    public static String getTempoAsString(Long tempo) {
        long HOUR = TimeUnit.HOURS.toMillis(1);

        if (tempo < HOUR) {
            return String.format("%1$TM:%1$TS%n", tempo.longValue());
        } else {
            return String.format("%d:%2$TM:%2$TS%n", tempo.longValue() / HOUR, tempo.longValue() % HOUR);
        }
    }

    public String getMillisecondsAsString(Long milliSeconds) {
        if (milliSeconds == null) return "";
        return Util.dateHourToString(new Date(milliSeconds));
    }

    public String dataHoraAsString(Date data) {
        if (data == null) {
            return "";
        }
        return Util.dateHourToString(data);
    }

    public String getDescricaoMes(Integer indice) {
        return DataUtil.getDescricaoMes(indice);
    }

    public List<Field> getCamposDoSelecionado(Class classe) {
        List<Field> campos = new ArrayList<Field>();
        for (Field f : Persistencia.getAtributos(classe)) {
            f.setAccessible(true);
            if (f.isAnnotationPresent(Tabelavel.class)) {
                campos.add(f);
            }
        }
        return campos;
    }

    public Object obterLabelCampo(Field f) {
        f.setAccessible(true);
        return Persistencia.getNomeDoCampo(f);
    }

    public Boolean isCampoMonetario(Field f) {
        f.setAccessible(true);
        if (f.isAnnotationPresent(br.com.webpublico.util.anotacoes.Monetario.class)) {
            return true;
        }
        return false;
    }


    public Object obterConteudoCampo(Field f, Object selecionado) {
        f.setAccessible(true);
        return Persistencia.getAsStringAtributoValue(selecionado, f);
    }

    public BigDecimal getValorTotalPorCampo(Field f, List<Object> objetos) {
        BigDecimal valor = BigDecimal.ZERO;
        for (Object objeto : objetos) {
            try {
                if (f.getType().equals(BigDecimal.class)) {
                    valor = valor.add((BigDecimal) f.get(objeto));
                }
            } catch (Exception e) {

            }
        }
        return valor;
    }

    public BigDecimal getValorTotalPorEntidadeContabil(List<EntidadeContabilComValor> objetos) {
        BigDecimal valor = BigDecimal.ZERO;
        if (objetos != null && !objetos.isEmpty()) {
            for (EntidadeContabilComValor objeto : objetos) {
                valor = valor.add(objeto.getValor());
            }
        }
        return valor;
    }

    public void fazerNada() {

    }

    public List<SelectItem> getMeses() {
        List<SelectItem> selectItems = Lists.newArrayList();
        selectItems.add(new SelectItem(null, ""));
        for (Mes mes : Mes.values()) {
            selectItems.add(new SelectItem(mes, mes.getDescricao()));
        }
        return selectItems;
    }
}
