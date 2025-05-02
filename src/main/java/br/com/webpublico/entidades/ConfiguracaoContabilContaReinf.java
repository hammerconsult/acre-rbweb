package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.reinf.eventos.TipoArquivoReinf;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;


@Entity
@GrupoDiagrama(nome = "Contábil")
@Audited
@Table(name = "CONFIGCONTCONTAREINF")
public class ConfiguracaoContabilContaReinf extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta de Extra")
    private Conta contaExtra;
    @Obrigatorio
    @Etiqueta("Configuração Contábil")
    @ManyToOne
    private ConfiguracaoContabil configuracaoContabil;
    @Enumerated(EnumType.STRING)
    private TipoArquivoReinf tipoArquivoReinf;
    private BigDecimal retencaoMaxima;

    public ConfiguracaoContabilContaReinf() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conta getContaExtra() {
        return contaExtra;
    }

    public void setContaExtra(Conta contaExtra) {
        this.contaExtra = contaExtra;
    }

    public ConfiguracaoContabil getConfiguracaoContabil() {
        return configuracaoContabil;
    }

    public void setConfiguracaoContabil(ConfiguracaoContabil configuracaoContabil) {
        this.configuracaoContabil = configuracaoContabil;
    }

    public TipoArquivoReinf getTipoArquivoReinf() {
        return tipoArquivoReinf;
    }

    public void setTipoArquivoReinf(TipoArquivoReinf tipoArquivoReinf) {
        this.tipoArquivoReinf = tipoArquivoReinf;
    }

    public BigDecimal getRetencaoMaxima() {
        return retencaoMaxima;
    }

    public void setRetencaoMaxima(BigDecimal retencaoMaxima) {
        this.retencaoMaxima = retencaoMaxima;
    }
}
