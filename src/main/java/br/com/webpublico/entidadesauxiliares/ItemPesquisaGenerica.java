package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

/**
 * @author Renato
 */
public class ItemPesquisaGenerica implements Serializable {

    private String condicao;
    private String label;
    private Object tipo;
    private String labelOrdenacao;
    private boolean eEnum;
    private boolean eEnumOrdinal;
    private boolean pertenceOutraClasse;
    private boolean stringExata;
    private String tipoOrdenacao;
    private String labelBooleanFalse;
    private String labelBooleanTrue;
    private String subCondicao;
    private Long criadoEm;
    private Boolean subSelectComExists;

    public ItemPesquisaGenerica() {
        this.pertenceOutraClasse = false;
        this.tipoOrdenacao = "asc";
        this.criadoEm = System.nanoTime();
    }

    public ItemPesquisaGenerica(String condicao, String label, Object tipo) {
        this();
        this.condicao = condicao;
        this.label = label;
        this.tipo = tipo;
        this.eEnum = false;
    }

    public ItemPesquisaGenerica(String condicao, String label, Object tipo, boolean eEnum) {
        this(condicao, label, tipo);
        this.eEnum = eEnum;
    }

    public ItemPesquisaGenerica(String condicao, String label, Object tipo, boolean eEnum, boolean pertenceOutraClasse) {
        this(condicao, label, tipo, eEnum);
        this.pertenceOutraClasse = pertenceOutraClasse;
    }

    public ItemPesquisaGenerica(String condicao, String label, Object tipo, boolean eEnum, boolean pertenceOutraClasse, boolean stringExata) {
        this(condicao, label, tipo, eEnum, pertenceOutraClasse);
        this.stringExata = stringExata;
    }

    public ItemPesquisaGenerica(String condicao, String label, Object tipo, boolean eEnum, Boolean pertenceOutraClasse, String labelBooleanFalse, String labelBooleanTrue) {
        this(condicao, label, tipo, eEnum, pertenceOutraClasse);
        this.labelBooleanFalse = labelBooleanFalse;
        this.labelBooleanTrue = labelBooleanTrue;
    }

    public boolean iseEnum() {
        return eEnum;
    }

    public void seteEnum(boolean eEnum) {
        this.eEnum = eEnum;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCondicao() {
        return condicao;
    }

    public void setCondicao(String condicao) {
        this.condicao = condicao;
    }

    public Object getTipo() {
        return tipo;
    }

    public void setTipo(Object tipo) {
        this.tipo = tipo;
    }

    public boolean isPertenceOutraClasse() {
        return pertenceOutraClasse;
    }

    public void setPertenceOutraClasse(boolean pertenceOutraClasse) {
        this.pertenceOutraClasse = pertenceOutraClasse;
    }

    public String getLabelOrdenacao() {
        return labelOrdenacao;
    }

    public void setLabelOrdenacao(String labelOrdenacao) {
        this.labelOrdenacao = labelOrdenacao;
    }

    public String getTipoOrdenacao() {
        return tipoOrdenacao;
    }

    public void setTipoOrdenacao(String tipoOrdenacao) {
        this.tipoOrdenacao = tipoOrdenacao;
    }

    public String getLabelBooleanFalse() {
        return labelBooleanFalse;
    }

    public void setLabelBooleanFalse(String labelBooleanFalse) {
        this.labelBooleanFalse = labelBooleanFalse;
    }

    public String getLabelBooleanTrue() {
        return labelBooleanTrue;
    }

    public void setLabelBooleanTrue(String labelBooleanTrue) {
        this.labelBooleanTrue = labelBooleanTrue;
    }

    public String getSubCondicao() {
        return subCondicao;
    }

    public void setSubCondicao(String subCondicao) {
        this.subCondicao = subCondicao;
    }

    public ItemPesquisaGenerica subCondicao(String subCondicao) {
        this.subCondicao = subCondicao;
        return this;
    }

    public boolean isStringExata() {
        return stringExata;
    }

    public void setStringExata(boolean stringExata) {
        this.stringExata = stringExata;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Boolean getSubSelectComExists() {
        return subSelectComExists;
    }

    public void setSubSelectComExists(Boolean subSelectComExists) {
        this.subSelectComExists = subSelectComExists;
    }

    public boolean iseEnumOrdinal() {
        return eEnumOrdinal;
    }

    public void seteEnumOrdinal(boolean eEnumOrdinal) {
        this.eEnumOrdinal = eEnumOrdinal;
    }

    @Override
    public String toString() {
        return label;
    }
}
