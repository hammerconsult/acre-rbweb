package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by William on 24/05/2018.
 */
@Entity
@Audited
@Table(name = "INCIDENCIATRIBPREVIDENCIA")
@Etiqueta("Incidência Tributária para Previdência")
public class IncidenciaTributariaPrevidencia extends SuperEntidade {

    private static final String AUXILIO_DOENCA_MENSAL = "23";
    private static final String AUXILIO_DOENCA_MENSAL_13 = "24";
    private static final String COMPLEMENTO_SALARIO_MINIMO = "61";

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

    public boolean eventoIncidenciaTributariaPrevidenciaInvalidoS1200() {
        return AUXILIO_DOENCA_MENSAL.equals(this.codigo) || AUXILIO_DOENCA_MENSAL_13.equals(this.codigo) || COMPLEMENTO_SALARIO_MINIMO.equals(this.codigo);
    }
}
