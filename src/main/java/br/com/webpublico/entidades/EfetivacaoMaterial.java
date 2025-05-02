package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusMaterial;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Length;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Criado por Mateus
 * Data: 27/01/2017.
 */
@Entity
@Audited
@Etiqueta("Efetivação de Material")
public class EfetivacaoMaterial extends SuperEntidade implements Comparable<EfetivacaoMaterial>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Data de Registro")
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date dataRegistro;
    @Etiqueta("Usuário")
    @Tabelavel
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Etiqueta("Observação")
    @Length(maximo = 255)
    private String observacao;
    @Etiqueta("Material")
    @ManyToOne
    private Material material;

    @Etiqueta("Unidade Administrativa")
    @ManyToOne
    private UnidadeOrganizacional unidadeAdministrativa;

    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private StatusMaterial situacao;

    public EfetivacaoMaterial() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public UnidadeOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(UnidadeOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public StatusMaterial getSituacao() {
        return situacao;
    }

    public void setSituacao(StatusMaterial situacao) {
        this.situacao = situacao;
    }

    public boolean hasSituacao() {
        return situacao != null;
    }

    @Override
    public int compareTo(EfetivacaoMaterial o) {
        if (getDataRegistro() != null && o.getDataRegistro() != null) {
            return ComparisonChain.start()
                .compare(getDataRegistro(), o.getDataRegistro())
                .result();
        }
        return 0;
    }

}
