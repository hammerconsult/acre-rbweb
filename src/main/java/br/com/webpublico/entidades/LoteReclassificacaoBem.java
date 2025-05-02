package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 30/01/14
 * Time: 16:32
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta(value = "Reclassificação de Grupo Objeto de Compra", genero = "M")
public class LoteReclassificacaoBem extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Usuário")
    @ManyToOne
    private UsuarioSistema usuario;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo Bem")
    @Enumerated(EnumType.STRING)
    private TipoBem tipoBem;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Novo Grupo Objeto Compra")
    private GrupoObjetoCompra novoGrupoObjetoCompra;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data/Hora de Criação")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataHoraCriacao;

    @Transient
    private GrupoObjetoCompraGrupoBem grupoObjetoCompraGrupoBem;

    @OneToMany(mappedBy = "loteReclassificacaoBem", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ReclassificacaoBem> reclassificacoes;

    public LoteReclassificacaoBem() {
        super();
        this.reclassificacoes = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getDataHoraCriacao() {
        return dataHoraCriacao;
    }

    public void setDataHoraCriacao(Date dataHoraCriacao) {
        this.dataHoraCriacao = dataHoraCriacao;
    }

    public List<ReclassificacaoBem> getReclassificacoes() {
        return reclassificacoes;
    }

    public void setReclassificacoes(List<ReclassificacaoBem> reclassificacoes) {
        this.reclassificacoes = reclassificacoes;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    public GrupoObjetoCompra getNovoGrupoObjetoCompra() {
        return novoGrupoObjetoCompra;
    }

    public void setNovoGrupoObjetoCompra(GrupoObjetoCompra novoGrupoObjetoCompra) {
        this.novoGrupoObjetoCompra = novoGrupoObjetoCompra;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuarioSistema) {
        this.usuario = usuarioSistema;
    }

    public GrupoObjetoCompraGrupoBem getGrupoObjetoCompraGrupoBem() {
        return grupoObjetoCompraGrupoBem;
    }

    public void setGrupoObjetoCompraGrupoBem(GrupoObjetoCompraGrupoBem grupoObjetoCompraGrupoBem) {
        this.grupoObjetoCompraGrupoBem = grupoObjetoCompraGrupoBem;
    }
}
