/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.ValidarRegistro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Claudio
 */
public class RegistroRAISTipo9 implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(RegistroRAISTipo9.class);
    private String sequenciaRegistro;

    @Etiqueta("Inscrição CNPJ/CEI do último estabelecimento do arquivo")
    @ValidarRegistro(tamanhoDoCampo = 14, campoNumerico = true)
    private String inscricaoCNPJCEI;

    @Etiqueta("Prefixo do último estabelecimento do arquivo")
    @ValidarRegistro(tamanhoDoCampo = 2)
    private String prefixo;

    @Etiqueta("Tipo do registro")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true, valoresPossiveis = {"9"})
    private String tipoRegistro;

    @Etiqueta("Total de registro tipo 1 do arquivo")
    @ValidarRegistro(tamanhoDoCampo = 6, campoNumerico = true)
    private String totalRegistrosTipo1;

    @Etiqueta("Total de registro tipo 2 do arquivo")
    private String totalRegistrosTipo2;

    @Etiqueta("Espaços em branco")
    @ValidarRegistro(tamanhoDoCampo = 549)
    private String espacosBranco549;

    public String getSequenciaRegistro() {
        return sequenciaRegistro;
    }

    public void setSequenciaRegistro(String sequenciaRegistro) {
        this.sequenciaRegistro = StringUtil.cortaOuCompletaEsquerda(sequenciaRegistro, 6, "0");
    }

    public String getInscricaoCNPJCEI() {
        return inscricaoCNPJCEI;
    }

    public void setInscricaoCNPJCEI(String inscricaoCNPJCEI) {
        this.inscricaoCNPJCEI = inscricaoCNPJCEI;
    }

    public String getPrefixo() {
        return prefixo;
    }

    public void setPrefixo(String prefixo) {
        this.prefixo = prefixo;
    }

    public String getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(String tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }

    public String getTotalRegistrosTipo1() {
        return totalRegistrosTipo1;
    }

    public void setTotalRegistrosTipo1(String totalRegistrosTipo1) {
        this.totalRegistrosTipo1 = StringUtil.cortaOuCompletaEsquerda(totalRegistrosTipo1, 6, "0");
    }

    public String getTotalRegistrosTipo2() {
        return totalRegistrosTipo2;
    }

    public void setTotalRegistrosTipo2(String totalRegistrosTipo2) {
        this.totalRegistrosTipo2 = StringUtil.cortaOuCompletaEsquerda(totalRegistrosTipo2, 6, "0");
    }

    public String getEspacosBranco549() {
        return espacosBranco549;
    }

    public void setEspacosBranco549(String espacosBranco549) {
        this.espacosBranco549 = espacosBranco549;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getSequenciaRegistro());
        sb.append(this.getInscricaoCNPJCEI());
        sb.append(this.getPrefixo());
        sb.append(this.getTipoRegistro());
        sb.append(this.getTotalRegistrosTipo1());
        sb.append(this.getTotalRegistrosTipo2());
        sb.append(this.getEspacosBranco549());
//        sb.append(System.getProperty("line.separator"));
        return sb.toString();
    }

    public void validarLayout(AuxiliarAndamentoRais aar) {
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                ValidarRegistro validarRegistro = null;
                Etiqueta etiqueta = null;
                for (Annotation annotation : field.getAnnotations()) {
                    if (annotation instanceof Etiqueta) {
                        etiqueta = (Etiqueta) annotation;
                    }
                    if (annotation instanceof ValidarRegistro) {
                        validarRegistro = (ValidarRegistro) annotation;
                    }
                }
                if (validarRegistro == null) {
                    continue;
                }
                try {
                    String valorAtributo = field.get(this).toString();
                    if ("".equals(valorAtributo) || "null".equals(valorAtributo) || valorAtributo.equals(null)) {
                        aar.getLog().add(Util.dateHourToString(new Date()) + "<font style='color : red;'><i> - &nbsp;&bull; " + etiqueta.value() + " está vazio." + "</i></font><br/>");
                    } else {
                        int tamanhoCampo = validarRegistro.tamanhoDoCampo();
                        if (valorAtributo.length() > tamanhoCampo) {
                            aar.getLog().add(Util.dateHourToString(new Date()) + "<font style='color : red;'><i> - &nbsp;&bull; " + etiqueta.value() + " valor = " + valorAtributo + " quantidade de caracteres maior que" + tamanhoCampo + "</i></font><br/>");
                        }
                        if (valorAtributo.length() < tamanhoCampo) {
                            aar.getLog().add(Util.dateHourToString(new Date()) + "<font style='color : red;'><i> - &nbsp;&bull; " + etiqueta.value() + " valor = " + valorAtributo + " quantidade de caracteres menor que" + tamanhoCampo + "</i></font><br/>");
                        }
                        if (validarRegistro.campoNumerico()) {
                            try {
                                BigDecimal.valueOf(Long.parseLong(field.get(this).toString()));
                            } catch (NumberFormatException nu) {
                                aar.getLog().add(Util.dateHourToString(new Date()) + "<font style='color : red;'><i> - &nbsp;&bull; " + etiqueta.value() + " caracteres não numéricos. Valor " + valorAtributo + "</i></font><br/>");
                            }
                            boolean possivel = false;
                            for (String s : validarRegistro.valoresPossiveis()) {
                                if (s.equals("") || s.equals(valorAtributo)) {
                                    possivel = true;
                                }
                            }
                            if (!possivel) {
                                aar.getLog().add(Util.dateHourToString(new Date()) + "<font style='color : red;'><i> - &nbsp;&bull; " + etiqueta.value() + " o valor " + valorAtributo + " é inválido, dever estar entre os valores " + validarRegistro.valoresPossiveis() + " </i></font><br/>");
                            }
                        }
                    }
                } catch (NullPointerException ne) {
                    aar.getLog().add(Util.dateHourToString(new Date()) + "<font style='color : red;'><i> - &nbsp;&bull; " + etiqueta.value() + " está vazio." + "</i></font><br/>");
                }
            }
        } catch (IllegalAccessException e) {
            logger.debug(e.getMessage());
        }
    }
}
