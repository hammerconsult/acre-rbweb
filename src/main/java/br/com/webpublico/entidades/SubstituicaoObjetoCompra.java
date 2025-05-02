package br.com.webpublico.entidades;

import br.com.webpublico.enums.OrigemSubstituicaoObjetoCompra;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Etiqueta("Substituição Objeto de Compra")
public class SubstituicaoObjetoCompra extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número")
    private Long numero;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data da Substituição ")
    private Date dataSubstituicao;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Origem da Substituição")
    private OrigemSubstituicaoObjetoCompra origemSubstituicao;

    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    private String historico;
    private Long idMovimento;

    @OneToMany(mappedBy = "substituicaoObjetoCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubstituicaoObjetoCompraItem> itens;

    public SubstituicaoObjetoCompra() {
        itens = Lists.newArrayList();
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public OrigemSubstituicaoObjetoCompra getOrigemSubstituicao() {
        return origemSubstituicao;
    }

    public void setOrigemSubstituicao(OrigemSubstituicaoObjetoCompra tipoSubstituicao) {
        this.origemSubstituicao = tipoSubstituicao;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataSubstituicao() {
        return dataSubstituicao;
    }

    public void setDataSubstituicao(Date dataSubstituicao) {
        this.dataSubstituicao = dataSubstituicao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public List<SubstituicaoObjetoCompraItem> getItens() {
        return itens;
    }

    public void setItens(List<SubstituicaoObjetoCompraItem> itens) {
        this.itens = itens;
    }

    public Long getIdMovimento() {
        return idMovimento;
    }

    public void setIdMovimento(Long idMovimento) {
        this.idMovimento = idMovimento;
    }
}
