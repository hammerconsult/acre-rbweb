package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: julio
 * Date: 15/08/13
 * Time: 16:42
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
@Etiqueta("Desmembramento")
public class Desmembramento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta("Originário")
    @Tabelavel
    @Pesquisavel
    private CadastroImobiliario original;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data")
    @Tabelavel
    @Pesquisavel
    private Date dataDesmembramento;
    @ManyToOne
    @Etiqueta("Responsável")
    @Pesquisavel
    private UsuarioSistema usuarioResponsavel;

    @Invisivel
    @Transient
    private Long criadoEm;
    @OneToMany(mappedBy = "desmembramento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemDesmembramento> itens;

    public Desmembramento() {
        this.criadoEm = System.nanoTime();
        itens = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CadastroImobiliario getOriginal() {
        return original;
    }

    public void setOriginal(CadastroImobiliario bciOriginario) {
        this.original = bciOriginario;
    }

    public Date getDataDesmembramento() {
        return dataDesmembramento;
    }

    public void setDataDesmembramento(Date dataDesmembramento) {
        this.dataDesmembramento = dataDesmembramento;
    }

    public UsuarioSistema getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public void setUsuarioResponsavel(UsuarioSistema usuarioResponsavel) {
        this.usuarioResponsavel = usuarioResponsavel;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<ItemDesmembramento> getItens() {
        return itens;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return "Desmembramento";
    }
}
