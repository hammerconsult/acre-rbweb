package br.com.webpublico.entidades.rh.parametro.cargocomissaofuncaogratificada;

import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by William on 27/03/2019.
 */
@Entity
@Audited
@Etiqueta("Controle de Cargo em Comissão e Função Gratificada")
public class ParametroControleCargoFG extends SuperEntidade{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data Inicial")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private Date dataInicial;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data Final")
    @Pesquisavel
    @Tabelavel
    private Date dataFinal;

    @ManyToOne
    @Etiqueta("Entidade")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private Entidade entidade;

    @Etiqueta("Quantidade Cargo em Comissão –  CC")
    @Pesquisavel
    @Tabelavel
    private Integer quantidadeCargoComissao;

    @Etiqueta("Quantidade de Função Gratificada  - FG")
    @Pesquisavel
    @Tabelavel
    private Integer quantidadeFuncaoGratificada;

    @Etiqueta("Quantidade de Função Gratificada de Coordenação - FGC")
    @Pesquisavel
    @Tabelavel
    private Integer quantidadeFgCoordenacao;

    @Etiqueta("Limite mensal de Cargo em Comissão –  CC")
    @Pesquisavel
    @Tabelavel
    private BigDecimal valorCargoComissao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }


    public Integer getQuantidadeCargoComissao() {
        return quantidadeCargoComissao;
    }

    public void setQuantidadeCargoComissao(Integer quantidadeCargoComissao) {
        this.quantidadeCargoComissao = quantidadeCargoComissao;
    }

    public Integer getQuantidadeFuncaoGratificada() {
        return quantidadeFuncaoGratificada;
    }

    public void setQuantidadeFuncaoGratificada(Integer quantidadeFuncaoGratificada) {
        this.quantidadeFuncaoGratificada = quantidadeFuncaoGratificada;
    }

    public BigDecimal getValorCargoComissao() {
        return valorCargoComissao;
    }

    public void setValorCargoComissao(BigDecimal valorCargoComissao) {
        this.valorCargoComissao = valorCargoComissao;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Integer getQuantidadeFgCoordenacao() {
        return quantidadeFgCoordenacao;
    }

    public void setQuantidadeFgCoordenacao(Integer quantidadeFgCoordenacao) {
        this.quantidadeFgCoordenacao = quantidadeFgCoordenacao;
    }
}
