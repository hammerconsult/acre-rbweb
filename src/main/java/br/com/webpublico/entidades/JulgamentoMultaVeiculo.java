package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 31/10/14
 * Time: 14:21
 * To change this template use File | Settings | File Templates.
 */
@Etiqueta("Julgamento da Multa do Veículo")
@Entity
@Audited
public class JulgamentoMultaVeiculo extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private MultaVeiculo multaVeiculo;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Data")
    private Date dataJulgamento;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Resultado")
    @Enumerated(EnumType.STRING)
    private ResultadoJulgamento resultadoJulgamento;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Número do Processo")
    private String numeroProcesso;
    private Boolean houveRessarcimento;
    @Etiqueta("Número do Dam")
    private String numeroDam;
    @Etiqueta("Valor")
    private BigDecimal valor;

    public JulgamentoMultaVeiculo() {
        super();
        houveRessarcimento = Boolean.FALSE;
    }

    public enum ResultadoJulgamento {
        DEFERIDO("Deferido"), INDEFERIDO("Indeferido");

        ResultadoJulgamento(String descricao) {
            this.descricao = descricao;
        }

        private String descricao;

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MultaVeiculo getMultaVeiculo() {
        return multaVeiculo;
    }

    public void setMultaVeiculo(MultaVeiculo multaVeiculo) {
        this.multaVeiculo = multaVeiculo;
    }

    public Date getDataJulgamento() {
        return dataJulgamento;
    }

    public void setDataJulgamento(Date dataJulgamento) {
        this.dataJulgamento = dataJulgamento;
    }

    public ResultadoJulgamento getResultadoJulgamento() {
        return resultadoJulgamento;
    }

    public void setResultadoJulgamento(ResultadoJulgamento resultadoJulgamento) {
        this.resultadoJulgamento = resultadoJulgamento;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public Boolean getHouveRessarcimento() {
        return houveRessarcimento;
    }

    public void setHouveRessarcimento(Boolean houveRessarcimento) {
        this.houveRessarcimento = houveRessarcimento;
    }

    public String getNumeroDam() {
        return numeroDam;
    }

    public void setNumeroDam(String numeroDam) {
        this.numeroDam = numeroDam;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
