package br.com.webpublico.consultaentidade;

import br.com.webpublico.DateUtils;
import br.com.webpublico.enums.Operador;
import br.com.webpublico.enums.OperadorLogico;
import br.com.webpublico.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.Date;

public class FiltroConsultaEntidade {

    private FieldConsultaEntidade field;
    private Operador operacao;
    private OperadorLogico operadorLogico;
    private Object valor;

    public FiltroConsultaEntidade() {
        operadorLogico = OperadorLogico.AND;
    }

    public FiltroConsultaEntidade(FieldConsultaEntidade field) {
        this.field = field;
    }

    public FieldConsultaEntidade getField() {
        return field;
    }

    public void setField(FieldConsultaEntidade field) {
        this.field = field;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    public Operador getOperacao() {
        return operacao;
    }

    public void setOperacao(Operador operacao) {
        this.operacao = operacao;
    }

    public void mudarOperadorLogico() {
        operadorLogico = operadorLogico == null || operadorLogico.equals(OperadorLogico.OR) ? OperadorLogico.AND : OperadorLogico.OR;
    }

    public OperadorLogico getOperadorLogico() {
        return operadorLogico;
    }

    public void setOperadorLogico(OperadorLogico operadorLogico) {
        this.operadorLogico = operadorLogico;
    }

    @JsonIgnore
    public String getCampoParaQuery() {
        if (field == null) {
            return null;
        }
        if (valor != null && !StringUtils.isBlank(valor.toString())) {
            if (field.getTipo().equals(TipoCampo.STRING)) {
                return "trim(lower(translate(replace(replace(replace(" + field.getValor() + ",'.',''),'-',''), '/', ''),'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')))";
            }
            if (field.getTipo().equals(TipoCampo.DATE)) {
                return "trunc(" + field.getValor() + ")";
            }
        }
        return field.getValor();
    }

    @JsonIgnore
    public Object getValorParaQuery() {
        if (operadorIsNull()) {
            return null;
        }
        if (TipoCampo.STRING.equals(field.getTipo())) {
            if (Operador.LIKE.equals(operacao) || Operador.NOT_LIKE.equals(operacao)) {
                return "%" + getStringValue(valor.toString()) + "%";
            }
            return getStringValue(valor.toString());
        }
        if (TipoCampo.BOOLEAN.equals(field.getTipo())) {
            if (Operador.IS_NOT_NULL.equals(operacao) || Operador.IS_NULL.equals(operacao)) {
                return null;
            }
            return valor.equals("true") ? 1 : 0;
        }
        if (TipoCampo.INTEGER.equals(field.getTipo())) {
            return Integer.valueOf(valor.toString());
        }
        if (TipoCampo.DATE.equals(field.getTipo())) {
            return DateUtils.dataSemHorario((Date) valor);
        }
        return valor;
    }

    private String getStringValue(String valor) {
        return (StringUtil.removerApenasAcentuacao(valor)).toLowerCase().trim().replace(".", "").replace("-", "").replace("/", "").toLowerCase();
    }

    public boolean operadorIsNull() {
        return (valor == null || StringUtils.isEmpty(valor.toString()));
    }

}
