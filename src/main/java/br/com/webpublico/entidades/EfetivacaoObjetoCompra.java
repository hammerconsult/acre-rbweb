package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Length;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by HardRock on 30/03/2017.
 */
@Entity
@Etiqueta("Efetivação Objeto de Compra")
@Audited
@GrupoDiagrama(nome = "licitacao")
public class EfetivacaoObjetoCompra extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Registro")
    private Date dataRegistro;

    @Tabelavel
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    @Length(maximo = 255)
    @Tabelavel
    @Etiqueta("Observação")
    private String observacao;

    @OneToMany(orphanRemoval = false, mappedBy = "efetivacaoObjetoCompra")
    private List<ObjetoCompra> objetosCompra;

    public EfetivacaoObjetoCompra() {
        objetosCompra = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<ObjetoCompra> getObjetosCompra() {
        return objetosCompra;
    }

    public void setObjetosCompra(List<ObjetoCompra> objetosCompra) {
        this.objetosCompra = objetosCompra;
    }
}
