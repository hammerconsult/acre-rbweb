package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Estorno de Requisição de Compra")
public class RequisicaoCompraEstorno extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private Long numero;

    @Tabelavel
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Etiqueta("Data de Lançamento")
    private Date dataLancamento;

    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Requisição de Compra")
    private RequisicaoDeCompra requisicaoDeCompra;

    @Pesquisavel
    @Obrigatorio
    @Length(maximo = 3000)
    @Etiqueta("Motivo")
    private String motivo;

    @OneToMany(mappedBy = "requisicaoCompraEstorno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemRequisicaoCompraEstorno> itensRequisicaoCompraEstorno;

    public RequisicaoCompraEstorno() {
        super();
        itensRequisicaoCompraEstorno = Lists.newArrayList();
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

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public RequisicaoDeCompra getRequisicaoDeCompra() {
        return requisicaoDeCompra;
    }

    public void setRequisicaoDeCompra(RequisicaoDeCompra requisicaoDeCompra) {
        this.requisicaoDeCompra = requisicaoDeCompra;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public List<ItemRequisicaoCompraEstorno> getItensRequisicaoCompraEstorno() {
        return itensRequisicaoCompraEstorno;
    }

    public void setItensRequisicaoCompraEstorno(List<ItemRequisicaoCompraEstorno> itensRequisicaoCompraEstorno) {
        this.itensRequisicaoCompraEstorno = itensRequisicaoCompraEstorno;
    }
}
