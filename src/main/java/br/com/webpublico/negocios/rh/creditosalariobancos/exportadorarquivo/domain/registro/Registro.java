package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.domain.registro;


import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.annotation.Campo;
import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exception.CampoNaoEncontradoException;
import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exception.WPArquivoException;
import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.validador.ValidadorFactory;
import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.validador.decorator.Validador;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Daniel Franco
 * @since 16/05/2016 14:48
 */
public abstract class Registro implements Serializable {

    //private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final Logger log = LoggerFactory.getLogger(Registro.class);

    public Object get(String identificador) {
        return getValue(getFieldComIdentificador(identificador));
    }

    public Object get(int posicao) {
        return getValue(getFieldComPosicao(posicao));
    }

    public void set(String identificador, Object valor) {
        validarESetar(getFieldComIdentificador(identificador), valor);
    }

    public void set(int posicao, Object valor) {
        validarESetar(getFieldComPosicao(posicao), valor);
    }

    private void validarESetar(Field field, Object valor) {
        try {
            validarValor(field, valor);
            setarValor(field, valor);
        } catch (Exception ex) {
            throw new WPArquivoException("Erro Setando Valor [" + valor + "] no Campo [" + field.getName() + "]", ex);
        }
    }

    private Object getValue(Field field) {
        if (field == null) {
            return null;
        }
        try {
            field.setAccessible(true);
            return field.get(this);
        } catch (Exception ex) {
            throw new WPArquivoException("Erro Recuperando Valor do Campo [" + field + "]", ex);
        }
    }

    private void validarValor(Field field, Object valor) {
        if (field == null) {
            throw new IllegalArgumentException("Campo não pode ser Null");
        }
        Campo campo = getCampoAnnotationFromField(field);
        if (campo == null) {
            throw new IllegalArgumentException("Campo informado não possui annotation @Campo");
        }
        Validador validador = ValidadorFactory.paraOCampo(campo);
        validador.validar(valor);
    }

    private void setarValor(Field field, Object valor) {
        try {
            field.setAccessible(true);
            if (valor.getClass().isAssignableFrom(String.class)) {
                if (field.getType().isAssignableFrom(Integer.class)) {
                    field.set(this, Integer.parseInt(valor.toString()));
                } else if (field.getType().isAssignableFrom(Date.class)) {
                    field.set(this, new SimpleDateFormat(getFormato(field)).parse(valor.toString()));
                } else if (field.getType().isAssignableFrom(Date.class)) {
                    field.set(this, getFormatter(field).parse(valor.toString()));
                } else if (field.getType().isAssignableFrom(BigDecimal.class)) {
                    field.set(this, new BigDecimal(valor.toString()));
                } else {
                    field.set(this, valor);
                }
            } else if (field.getType().isAssignableFrom(Date.class)) {
                field.set(this, new SimpleDateFormat(getFormato(field)));
            } else {
                field.set(this, valor);
            }
        } catch (Exception ex) {
            throw new WPArquivoException("Erro Setando Valor " + (valor == null ? "[null]" : "[" + valor + "] - (" + valor.getClass().getCanonicalName() + ")") + " no Campo [" + field + "]", ex);
        }
    }

    private SimpleDateFormat getFormatter(Field field) {
        String formato = getFormato(field);
        try {
            if (formato == null || formato.trim().isEmpty()) {
                return simpleDateFormat;
            }
            return new SimpleDateFormat(formato);
        } catch (IllegalArgumentException il) {
            log.warn("Formato inválido [{}]", formato);
            return simpleDateFormat;
        }

    }

    private Field getFieldComIdentificador(String identificador) {
        if (identificador == null || identificador.isEmpty()) {
            throw new IllegalArgumentException("O Identificador Não pode ser Null/Vazio");
        }
        for (Field field : getClass().getDeclaredFields()) {
            if (identificador.equalsIgnoreCase(getIdentificador(field))) {
                return field;
            }
        }
        throw new CampoNaoEncontradoException("Nenhum Campo Encontrado com Identificador [" + identificador + "] na Classe [" + this.getClass().getCanonicalName() + "]");
    }

    private Campo getCampoAnnotationFromField(Field field) {
        if (field == null || !field.isAnnotationPresent(Campo.class)) {
            return null;
        }
        return getAnnotationsByType(field, Campo.class)[0];
    }

    private String getIdentificador(Field field) {
        Campo campo = getCampoAnnotationFromField(field);
        return campo.identificador().isEmpty() ? field.getName() : campo.identificador();
    }

    private String getFormato(Field field) {
        return getCampoAnnotationFromField(field).formato();
    }


    private Field getFieldComPosicao(int posicao) {
        if (posicao <= 0) {
            throw new IllegalArgumentException("Posição Deve ser Maior que Zero");
        }
        Iterable<Field> fieldsUpTo = getFieldsUpTo(getClass(), Object.class);
        for (Field field : fieldsUpTo) {
            if (field.isAnnotationPresent(Campo.class) && getAnnotationsByType(field, Campo.class)[0].posicao() == posicao) {
                return field;
            }
        }
        throw new CampoNaoEncontradoException("Nenhum Campo Encontrado com Posição [" + posicao + "] na Classe [" + this.getClass().getCanonicalName() + "]");
    }

    public int getMaiorPosicao() {
        int retorno = 0;
        Iterable<Field> fieldsUpTo = getFieldsUpTo(getClass(), Object.class);
        for (Field field : fieldsUpTo) {
            if (field.isAnnotationPresent(Campo.class) && getAnnotationsByType(field, Campo.class)[0].posicao() > retorno) {
                retorno = getAnnotationsByType(field, Campo.class)[0].posicao();
            }
        }
        return retorno;
    }

    public Campo[] getAnnotationsByType(Field field, Class annotationClass) {
        List<Annotation> annotations = Lists.newArrayList();

        for (Annotation annotation : field.getAnnotations()) {
            if (annotation.annotationType().equals(annotationClass)) {
                annotations.add(annotation);
            }
        }
        Campo[] annotationsArray = new Campo[annotations.size()]; //TODO
        annotations.toArray(annotationsArray);
        return annotationsArray;

    }


    private static Iterable<Field> getFieldsUpTo(Class<?> startClass,
                                                 Class<?> exclusiveParent) {

        List<Field> currentClassFields = Lists.newArrayList(startClass.getDeclaredFields());
        Class<?> parentClass = startClass.getSuperclass();

        if (parentClass != null &&
            (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
            List<Field> parentClassFields =
                (List<Field>) getFieldsUpTo(parentClass, exclusiveParent);
            currentClassFields.addAll(parentClassFields);
        }

        return currentClassFields;
    }

    private String getAsString(Object valor) {
        if (valor != null) {
            return valor.toString();
        }
        return "";
    }

    public String getAsString(String identificador) {
        return getAsString(get(identificador));
    }

    public String getAsString(int posicao) {
        return getAsString(get(posicao));
    }
}
