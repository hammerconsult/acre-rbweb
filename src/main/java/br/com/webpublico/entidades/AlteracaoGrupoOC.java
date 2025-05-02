package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Alteração Grupo Objeto de Compra")
public class AlteracaoGrupoOC extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Etiqueta("Data da Alteração")
    private Date dataAlteracao;

    @Pesquisavel
    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Objeto Compra")
    private TipoObjetoCompra tipoObjetoCompra;

    @Etiqueta("Objetos de Compra")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "alteracaoGrupoOC")
    private List<ItemAlteracaoGrupoOC> itensAlteracaoGrupoOCS;

    public AlteracaoGrupoOC() {
    }

    public AlteracaoGrupoOC(Date dataAlteracao, UsuarioSistema usuarioSistema, List<ItemAlteracaoGrupoOC> itensAlteracaoGrupoOCS) {
        this.dataAlteracao = dataAlteracao;
        this.usuarioSistema = usuarioSistema;
        this.itensAlteracaoGrupoOCS = itensAlteracaoGrupoOCS;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }

    public List<ItemAlteracaoGrupoOC> getItensAlteracaoGrupoOCS() {
        return itensAlteracaoGrupoOCS;
    }

    public void setItensAlteracaoGrupoOCS(List<ItemAlteracaoGrupoOC> itensAlteracaoGrupoOCS) {
        this.itensAlteracaoGrupoOCS = itensAlteracaoGrupoOCS;
    }

    @Override
    public String toString() {
        return usuarioSistema.toString() + dataAlteracao.toString();
    }
}
