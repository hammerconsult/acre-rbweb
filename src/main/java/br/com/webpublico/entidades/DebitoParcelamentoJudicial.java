package br.com.webpublico.entidades;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class DebitoParcelamentoJudicial extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private ItemParcelamentoJudicial itemParcelamentoJudicial;
    @ManyToOne
    private CertidaoDividaAtiva certidaoDividaAtiva;
    @ManyToOne
    private ParcelaValorDivida parcelaValorDivida;
    @Transient
    private ResultadoParcela resultadoParcela;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public ItemParcelamentoJudicial getItemParcelamentoJudicial() {
        return itemParcelamentoJudicial;
    }

    public void setItemParcelamentoJudicial(ItemParcelamentoJudicial itemParcelamentoJudicial) {
        this.itemParcelamentoJudicial = itemParcelamentoJudicial;
    }

    public CertidaoDividaAtiva getCertidaoDividaAtiva() {
        return certidaoDividaAtiva;
    }

    public void setCertidaoDividaAtiva(CertidaoDividaAtiva certidaoDividaAtiva) {
        this.certidaoDividaAtiva = certidaoDividaAtiva;
    }

    public ParcelaValorDivida getParcelaValorDivida() {
        return parcelaValorDivida;
    }

    public void setParcelaValorDivida(ParcelaValorDivida parcelaValorDivida) {
        this.parcelaValorDivida = parcelaValorDivida;
    }

    public ResultadoParcela getResultadoParcela() {
        return resultadoParcela;
    }

    public void setResultadoParcela(ResultadoParcela resultadoParcela) {
        this.resultadoParcela = resultadoParcela;
    }
}
