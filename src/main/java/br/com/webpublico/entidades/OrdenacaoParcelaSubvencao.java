package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoOrdenacaoSubvencao;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by william on 14/08/17.
 */
@Entity
@Audited
public class OrdenacaoParcelaSubvencao extends SuperEntidade {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;
    private Integer ordem;
    @Enumerated(EnumType.STRING)
    private TipoOrdenacaoSubvencao tipoOrdenacaoSubvencao;
    @ManyToOne
    private SubvencaoParametro subvencaoParametro;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public TipoOrdenacaoSubvencao getTipoOrdenacaoSubvencao() {
        return tipoOrdenacaoSubvencao;
    }

    public void setTipoOrdenacaoSubvencao(TipoOrdenacaoSubvencao tipoOrdenacaoSubvencao) {
        this.tipoOrdenacaoSubvencao = tipoOrdenacaoSubvencao;
    }

    public SubvencaoParametro getSubvencaoParametro() {
        return subvencaoParametro;
    }

    public void setSubvencaoParametro(SubvencaoParametro subvencaoParametro) {
        this.subvencaoParametro = subvencaoParametro;
    }
}
