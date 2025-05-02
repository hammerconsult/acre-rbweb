package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by William on 24/05/2018.
 */
@Entity
@Audited
@Etiqueta("Incidência Tributária IRRF")
public class IncidenciaTributariaIRRF extends SuperEntidade {

    private static final String REMUNERACAO_MENSAL = "31";
    private static final String DECIMO_TERCEIRO_SALARIO = "32";
    private static final String FERIAS = "33";
    private static final String PLR = "34";
    private static final String RRA = "35";
    private static final String PENSAO_ALIMENTICIA = "51";
    private static final String PENSAO_ALIMENTICIA_13 = "52";
    private static final String PENSAO_ALIMENTICIA_FERIAS = "53";
    private static final String PENSAO_ALIMENTICIA_PLR = "54";
    private static final String PENSAO_ALIMENTICIA_RRA = "55";
    private static final String DEPOSITO_JUDICIAL = "81";
    private static final String COMPENSACAO_JUDICIAL_ANOS_ANTERIORES = "83";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    @Obrigatorio
    private String codigo;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    @Obrigatorio
    private String descricao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }

    public boolean eventoIncidenciaTributariaIRRFInvalidoS1200() {
        return REMUNERACAO_MENSAL.equals(this.codigo) || DECIMO_TERCEIRO_SALARIO.equals(this.codigo) || FERIAS.equals(this.codigo) ||
            PLR.equals(this.codigo) || RRA.equals(this.codigo) || PENSAO_ALIMENTICIA.equals(this.codigo) || PENSAO_ALIMENTICIA_13.equals(this.codigo) ||
            PENSAO_ALIMENTICIA_FERIAS.equals(this.codigo) || PENSAO_ALIMENTICIA_PLR.equals(this.codigo) || PENSAO_ALIMENTICIA_RRA.equals(this.codigo) ||
            DEPOSITO_JUDICIAL.equals(this.codigo) || COMPENSACAO_JUDICIAL_ANOS_ANTERIORES.equals(this.codigo);
    }
}
