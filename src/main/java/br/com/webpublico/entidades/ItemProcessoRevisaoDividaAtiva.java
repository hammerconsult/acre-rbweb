package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoParcelaRevisaoDA;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@GrupoDiagrama(nome = "Tributario")
@Audited
@Table(name="ITEMPROCESSOREVDIVIDAATIVA")
public class ItemProcessoRevisaoDividaAtiva implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoRevisaoDividaAtiva processoRevisao;
    @ManyToOne
    private ParcelaValorDivida parcela;
    @Enumerated(EnumType.STRING)
    private TipoParcelaRevisaoDA tipoParcelaRevisaoDA;
    @Transient
    private Long criadoEm = System.nanoTime();

    @Transient
    List<ResultadoParcela> parcelas;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return this.criadoEm;
    }

    public ParcelaValorDivida getParcela() {
        return parcela;
    }

    public void setParcela(ParcelaValorDivida parcela) {
        this.parcela = parcela;
    }

    public ProcessoRevisaoDividaAtiva getProcessoRevisao() {
        return processoRevisao;
    }

    public void setProcessoRevisao(ProcessoRevisaoDividaAtiva processoRevisao) {
        this.processoRevisao = processoRevisao;
    }

    public TipoParcelaRevisaoDA getTipoParcelaRevisaoDA() {
        return tipoParcelaRevisaoDA;
    }

    public void setTipoParcelaRevisaoDA(TipoParcelaRevisaoDA tipoParcelaRevisaoDA) {
        this.tipoParcelaRevisaoDA = tipoParcelaRevisaoDA;
    }

    public List<ResultadoParcela> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<ResultadoParcela> parcelas) {
        this.parcelas = parcelas;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }
}
