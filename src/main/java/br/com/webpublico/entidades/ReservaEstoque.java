package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 03/04/14
 * Time: 08:44
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Reserva de Estoque")
public class ReservaEstoque extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Tabelavel
    @Etiqueta("Item Aprovação Material")
    @OneToOne
    private ItemAprovacaoMaterial itemAprovacaoMaterial;

    @OneToOne
    @Etiqueta("Item Entrada Material")
    private ItemEntradaMaterial itemEntradaMaterial;

    @Obrigatorio
    @Tabelavel
    @Etiqueta("Local de Estoque")
    @ManyToOne
    private LocalEstoque localEstoque;

    @Obrigatorio
    @Tabelavel
    @Etiqueta("Status da Reserva de Estoque")
    @Enumerated(EnumType.STRING)
    private StatusReservaEstoque statusReservaEstoque;

    public ReservaEstoque() {
        super();
    }

    public ReservaEstoque(LocalEstoque local, ItemAprovacaoMaterial item) {
        super();
        this.localEstoque = local;
        this.itemAprovacaoMaterial = item;
        this.statusReservaEstoque = StatusReservaEstoque.AGUARDANDO;
    }

    public ReservaEstoque(LocalEstoque local, ItemEntradaMaterial item) {
        super();
        this.localEstoque = local;
        this.itemEntradaMaterial = item;
        this.statusReservaEstoque = StatusReservaEstoque.AGUARDANDO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemAprovacaoMaterial getItemAprovacaoMaterial() {
        return itemAprovacaoMaterial;
    }

    public void setItemAprovacaoMaterial(ItemAprovacaoMaterial itemAprovacaoMaterial) {
        this.itemAprovacaoMaterial = itemAprovacaoMaterial;
    }

    public ItemEntradaMaterial getItemEntradaMaterial() {
        return itemEntradaMaterial;
    }

    public void setItemEntradaMaterial(ItemEntradaMaterial itemEntradaMaterial) {
        this.itemEntradaMaterial = itemEntradaMaterial;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public StatusReservaEstoque getStatusReservaEstoque() {
        return statusReservaEstoque;
    }

    public void setStatusReservaEstoque(StatusReservaEstoque statusReservaEstoque) {
        this.statusReservaEstoque = statusReservaEstoque;
    }

    public BigDecimal getQuantidadeReservada() {
        try {
            if (isReservaEstornoEntradaPorCompra()) {
                return itemEntradaMaterial.getQuantidade();
            }
            if (itemAprovacaoMaterial.getItemRequisicaoMaterial().getRequisicaoMaterial().isRequisicaoConsumo()) {
                return itemAprovacaoMaterial.getQuantidadeAprovada().subtract(itemAprovacaoMaterial.getItemRequisicaoMaterial().getQuantidadeAtendida());
            }
            return itemAprovacaoMaterial.getQuantidadeAprovada().subtract(itemAprovacaoMaterial.getItemRequisicaoMaterial().getQuantidadeEmTransito());
        } catch (NullPointerException e) {
            return BigDecimal.ZERO;
        }
    }

    public String getQuantidadeReservadaFormatada() {
        TipoMascaraUnidadeMedida mascaraQtde = isReservaRequisicaoMaterial()
            ? itemAprovacaoMaterial.getMaterial().getMascaraQuantidade()
            : itemEntradaMaterial.getMaterial().getMascaraQuantidade();
        return Util.formataQuandoDecimal(getQuantidadeReservada(), mascaraQtde);
    }

    public Long getNumeroOrigemReserva() {
        if (isReservaRequisicaoMaterial()) {
            return itemAprovacaoMaterial.getAprovacaoMaterial().getNumero();
        }
        return itemEntradaMaterial.getEntradaMaterial().getNumero();
    }

    public Long getIdOrigemReserva() {
        if (isReservaRequisicaoMaterial()) {
            return itemAprovacaoMaterial.getAprovacaoMaterial().getId();
        }
        return itemEntradaMaterial.getEntradaMaterial().getId();
    }

    public Date getDataReserva() {
        if (isReservaRequisicaoMaterial()) {
            return itemAprovacaoMaterial.getAprovacaoMaterial().getDataDaAprovacao();
        }
        return itemEntradaMaterial.getEntradaMaterial().getDataEntrada();
    }

    public UnidadeOrganizacional getUnidadeReserva() {
        if (isReservaRequisicaoMaterial()) {
            return itemAprovacaoMaterial.getAprovacaoMaterial().getRequisicaoMaterial().getUnidadeRequerente();
        }
        return itemEntradaMaterial.getEntradaMaterial().getUnidadeOrganizacional();
    }

    public String getOrigemReserva(){
        if (isReservaRequisicaoMaterial()) {
            return "Avaliação de Requisição Material";
        }
        return "Solicitação de Estorno Entrada por Compra";
    }

    public boolean isReservaRequisicaoMaterial() {
        return itemAprovacaoMaterial != null;
    }

    public boolean isReservaEstornoEntradaPorCompra() {
        return itemEntradaMaterial != null;
    }

    public enum StatusReservaEstoque {
        AGUARDANDO("Aguardando"),
        FINALIZADA("Finalizada");

        private String descricao;

        StatusReservaEstoque(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
