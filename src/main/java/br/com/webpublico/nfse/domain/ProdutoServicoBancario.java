package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.enums.GrupoProdutoServicoBancario;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class ProdutoServicoBancario extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private GrupoProdutoServicoBancario grupo;
    private Integer codigo;
    private String descricao;
    private Boolean obrigaDescricaoComplementar;

    public ProdutoServicoBancario() {
        super();
        obrigaDescricaoComplementar = Boolean.FALSE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GrupoProdutoServicoBancario getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoProdutoServicoBancario grupo) {
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

    public Boolean getObrigaDescricaoComplementar() {
        return obrigaDescricaoComplementar;
    }

    public void setObrigaDescricaoComplementar(Boolean obrigaDescricaoComplementar) {
        this.obrigaDescricaoComplementar = obrigaDescricaoComplementar;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
