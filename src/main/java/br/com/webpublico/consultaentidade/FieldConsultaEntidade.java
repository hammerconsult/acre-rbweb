package br.com.webpublico.consultaentidade;

import br.com.webpublico.enums.Operador;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;

public class FieldConsultaEntidade {

    private static final String FORMATO_DEFAULT = "###,###,##0.00";

    private String descricao;
    private Integer ordem;
    private String valor;
    private String tipoEnum;
    private String select;
    private TipoCampo tipo;
    private TipoOrdenacao tipoOrdenacao;
    private TipoAlinhamento tipoAlinhamento;
    private Operador operacaoPadrao;
    private Object valorPadrao;
    private Boolean obrigatorio = false;
    private Boolean totalizar = false;
    private Boolean dinamico = false;
    private String valorOrdenacao;
    private String formato;

    private final Long criadoEm;

    public FieldConsultaEntidade() {
        this.criadoEm = System.nanoTime();
        this.formato = FORMATO_DEFAULT;
    }

    public FieldConsultaEntidade(Integer ordem) {
        this();
        this.ordem = ordem;
    }

    public FieldConsultaEntidade(String descricao, String valor, TipoCampo tipo) {
        this();
        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo;
    }

    public FieldConsultaEntidade(String descricao, String valor, String tipoEnum, TipoCampo tipo, TipoAlinhamento tipoAlinhamento) {
        this();
        this.descricao = descricao;
        this.valor = valor;
        this.tipoEnum = tipoEnum;
        this.tipo = tipo;
        this.tipoAlinhamento = tipoAlinhamento;
    }

    public FieldConsultaEntidade(String descricao, String valor, String tipoEnum, TipoCampo tipo, TipoAlinhamento tipoAlinhamento, String select) {
        this();
        this.descricao = descricao;
        this.valor = valor;
        this.tipoEnum = tipoEnum;
        this.tipo = tipo;
        this.tipoAlinhamento = tipoAlinhamento;
        this.select = select;
    }

    public Boolean getTotalizar() {
        return totalizar != null && totalizar;
    }

    public void setTotalizar(Boolean totalizar) {
        this.totalizar = totalizar;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public TipoAlinhamento getTipoAlinhamento() {
        return tipoAlinhamento == null ? TipoAlinhamento.ESQUERDA : tipoAlinhamento;
    }

    public void setTipoAlinhamento(TipoAlinhamento tipoAlinhamento) {
        this.tipoAlinhamento = tipoAlinhamento;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public TipoCampo getTipo() {
        return tipo;
    }

    public void setTipo(TipoCampo tipo) {
        this.tipo = tipo;
    }

    public TipoOrdenacao getTipoOrdenacao() {
        return tipoOrdenacao;
    }

    public void setTipoOrdenacao(TipoOrdenacao tipoOrdenacao) {
        this.tipoOrdenacao = tipoOrdenacao;
    }

    public Object getValorPadrao() {
        return TipoCampo.DATE.equals(tipo) ? (valorPadrao == null || Strings.isNullOrEmpty(valorPadrao.toString()) ? null : valorPadrao) : valorPadrao;
    }

    public void setValorPadrao(Object valorPadrao) {
        this.valorPadrao = valorPadrao;
    }

    public Boolean getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(Boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }

    public Operador getOperacaoPadrao() {
        return operacaoPadrao;
    }

    public void setOperacaoPadrao(Operador operacaoPadrao) {
        this.operacaoPadrao = operacaoPadrao;
    }

    public void modarOrdem() {
        if (tipoOrdenacao == null) {
            tipoOrdenacao = TipoOrdenacao.ASC;
        } else if (TipoOrdenacao.ASC.equals(tipoOrdenacao)) {
            tipoOrdenacao = TipoOrdenacao.DESC;
        } else {
            tipoOrdenacao = null;
        }
    }

    public String getTipoEnum() {
        return tipoEnum;
    }

    public void setTipoEnum(String tipoEnum) {
        this.tipoEnum = tipoEnum;
    }

    public Boolean getDinamico() {
        return dinamico;
    }

    public void setDinamico(Boolean dinamico) {
        this.dinamico = dinamico;
    }

    public String getValorOrdenacao() {
        return valorOrdenacao;
    }

    public void setValorOrdenacao(String valorOrdenacao) {
        this.valorOrdenacao = valorOrdenacao;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public String getFormato() {
        return !Strings.isNullOrEmpty(formato) ? formato : FORMATO_DEFAULT;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public FieldConsultaEntidade clonar() {
        FieldConsultaEntidade fieldConsultaEntidade = new FieldConsultaEntidade(this.descricao, this.valor, this.tipo);
        fieldConsultaEntidade.setTipoAlinhamento(this.tipoAlinhamento);
        fieldConsultaEntidade.setTipoOrdenacao(this.tipoOrdenacao);
        fieldConsultaEntidade.setValorOrdenacao(this.valor);
        fieldConsultaEntidade.setTipoEnum(this.tipoEnum);
        return fieldConsultaEntidade;
    }

    @JsonIgnore
    public boolean isDefault() {
        return getObrigatorio() || getOperacaoPadrao() != null;
    }
}
