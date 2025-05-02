package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.enums.GrupoTarifaBancaria;
import br.com.webpublico.nfse.enums.PeriodicidadeTarifaBancaria;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class TarifaBancaria extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private GrupoTarifaBancaria grupo;
    private Integer codigo;
    private String descricao;
    @Enumerated(EnumType.STRING)
    private PeriodicidadeTarifaBancaria periodicidade;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GrupoTarifaBancaria getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoTarifaBancaria grupo) {
        this.grupo = grupo;
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

    public PeriodicidadeTarifaBancaria getPeriodicidade() {
        return periodicidade;
    }

    public void setPeriodicidade(PeriodicidadeTarifaBancaria periodicidade) {
        this.periodicidade = periodicidade;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
