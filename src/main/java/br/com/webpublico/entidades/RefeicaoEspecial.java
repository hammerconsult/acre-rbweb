package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoRefeicaoEspecial;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Etiqueta("Refeição Especial")
public class RefeicaoEspecial extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Refeição Especial")
    private TipoRefeicaoEspecial tipoRefeicaoEspecial;

    @Etiqueta("Refeição")
    @ManyToOne
    private Refeicao refeicao;

    public RefeicaoEspecial() {
    }

    public RefeicaoEspecial(TipoRefeicaoEspecial tipoRefeicaoEspecial) {
        this.tipoRefeicaoEspecial = tipoRefeicaoEspecial;
    }

    public RefeicaoEspecial(TipoRefeicaoEspecial tipoRefeicaoEspecial, Refeicao refeicao) {
        this.tipoRefeicaoEspecial = tipoRefeicaoEspecial;
        this.refeicao = refeicao;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoRefeicaoEspecial getTipoRefeicaoEspecial() {
        return tipoRefeicaoEspecial;
    }

    public void setTipoRefeicaoEspecial(TipoRefeicaoEspecial tipoRefeicaoEspecial) {
        this.tipoRefeicaoEspecial = tipoRefeicaoEspecial;
    }

    public Refeicao getRefeicao() {
        return refeicao;
    }

    public void setRefeicao(Refeicao refeicao) {
        this.refeicao = refeicao;
    }
}
