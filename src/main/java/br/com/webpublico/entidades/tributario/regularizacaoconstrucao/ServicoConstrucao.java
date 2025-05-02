package br.com.webpublico.entidades.tributario.regularizacaoconstrucao;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.enums.tributario.regularizacaoconstrucao.TipoConstrucao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributario")
@Etiqueta("Serviços de Construção")
public class ServicoConstrucao extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Integer codigo;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Gera habite-se")
    private Boolean geraHabitese;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo Construção")
    private TipoConstrucao tipoConstrucao;

    public ServicoConstrucao() {
    }

    public ServicoConstrucao(Integer codigo, String descricao, Boolean geraHabitese, TipoConstrucao tipoConstrucao) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.geraHabitese = geraHabitese;
        this.tipoConstrucao = tipoConstrucao;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getGeraHabitese() {
        return geraHabitese;
    }

    public void setGeraHabitese(Boolean geraHabitese) {
        this.geraHabitese = geraHabitese;
    }

    public TipoConstrucao getTipoConstrucao() {
        return tipoConstrucao;
    }

    public void setTipoConstrucao(TipoConstrucao tipoConstrucao) {
        this.tipoConstrucao = tipoConstrucao;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
