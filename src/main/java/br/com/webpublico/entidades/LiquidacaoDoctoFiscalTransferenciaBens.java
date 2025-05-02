package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Audited
@Entity
@Table(name = "LIQDOCTOFISCALTRANSFBENS")
public class LiquidacaoDoctoFiscalTransferenciaBens extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @ManyToOne
    private LiquidacaoDoctoFiscal liquidacaoDoctoFiscal;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoContaDespesa tipoContaDespesa;
    @Obrigatorio
    private Long idTransferencia;
    @Transient
    private TransfBensMoveis transfBensMoveis;
    @Transient
    private TransferenciaBensEstoque transferenciaBensEstoque;

    public LiquidacaoDoctoFiscalTransferenciaBens() {
    }

    public LiquidacaoDoctoFiscalTransferenciaBens(TransfBensMoveis transfBensMoveis) {
        this.tipoContaDespesa = TipoContaDespesa.BEM_MOVEL;
        this.transfBensMoveis = transfBensMoveis;
    }

    public LiquidacaoDoctoFiscalTransferenciaBens(TransferenciaBensEstoque transferenciaBensEstoque) {
        this.tipoContaDespesa = TipoContaDespesa.BEM_ESTOQUE;
        this.transferenciaBensEstoque = transferenciaBensEstoque;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LiquidacaoDoctoFiscal getLiquidacaoDoctoFiscal() {
        return liquidacaoDoctoFiscal;
    }

    public void setLiquidacaoDoctoFiscal(LiquidacaoDoctoFiscal liquidacaoDoctoFiscal) {
        this.liquidacaoDoctoFiscal = liquidacaoDoctoFiscal;
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return tipoContaDespesa;
    }

    public void setTipoContaDespesa(TipoContaDespesa tipoContaDespesa) {
        this.tipoContaDespesa = tipoContaDespesa;
    }

    public Long getIdTransferencia() {
        return idTransferencia;
    }

    public void setIdTransferencia(Long idTransferencia) {
        this.idTransferencia = idTransferencia;
    }

    public TransfBensMoveis getTransfBensMoveis() {
        return transfBensMoveis;
    }

    public void setTransfBensMoveis(TransfBensMoveis transfBensMoveis) {
        this.transfBensMoveis = transfBensMoveis;
    }

    public TransferenciaBensEstoque getTransferenciaBensEstoque() {
        return transferenciaBensEstoque;
    }

    public void setTransferenciaBensEstoque(TransferenciaBensEstoque transferenciaBensEstoque) {
        this.transferenciaBensEstoque = transferenciaBensEstoque;
    }

    public BigDecimal getValor() {
        if (transfBensMoveis != null || transferenciaBensEstoque != null) {
            switch (tipoContaDespesa) {
                case BEM_MOVEL:
                    return transfBensMoveis.getValor();

                case BEM_ESTOQUE:
                    return transferenciaBensEstoque.getValor();
            }
        }
        return BigDecimal.ZERO;
    }

    public void setValor(BigDecimal valor) {
        if (transfBensMoveis != null || transferenciaBensEstoque != null) {
            switch (tipoContaDespesa) {
                case BEM_MOVEL:
                    transfBensMoveis.setValor(valor);
                    break;

                case BEM_ESTOQUE:
                    transferenciaBensEstoque.setValor(valor);
                    break;
            }
        }
    }

    public String getDescricaoGrupoOrigem() {
        if (transfBensMoveis != null || transferenciaBensEstoque != null) {
            switch (tipoContaDespesa) {
                case BEM_MOVEL:
                    return transfBensMoveis.getGrupoBemOrigem().toString();

                case BEM_ESTOQUE:
                    return transferenciaBensEstoque.getGrupoMaterial().toString();
            }
        }
        return "";
    }

    public String getDescricaoGrupoDestino() {
        if (transfBensMoveis != null || transferenciaBensEstoque != null) {
            switch (tipoContaDespesa) {
                case BEM_MOVEL:
                    return transfBensMoveis.getGrupoBemDestino().toString();

                case BEM_ESTOQUE:
                    return transferenciaBensEstoque.getGrupoMaterialDestino().toString();
            }
        }
        return "";
    }

    public Long getIdGrupoDestino() {
        if (transfBensMoveis != null || transferenciaBensEstoque != null) {
            switch (tipoContaDespesa) {
                case BEM_MOVEL:
                    return transfBensMoveis.getGrupoBemDestino().getId();

                case BEM_ESTOQUE:
                    return transferenciaBensEstoque.getGrupoMaterialDestino().getId();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return tipoContaDespesa.name() + " - " + idTransferencia;
    }
}
