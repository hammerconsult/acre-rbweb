package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Publicação do Contrato")
public class PublicacaoContrato extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Contrato")
    @Tabelavel
    @Pesquisavel
    private Contrato contrato;

    @Etiqueta("Data de Publicação")
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private Date dataPublicacao;

    @Etiqueta("Veículo de Publicação")
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    private VeiculoDePublicacao veiculoDePublicacao;

    @Etiqueta("Número da Publicação")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private Integer numero;

    @Etiqueta("Página da Publicação")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private Integer pagina;

    @Etiqueta("Observação")
    private String observacao;

    public PublicacaoContrato() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public VeiculoDePublicacao getVeiculoDePublicacao() {
        return veiculoDePublicacao;
    }

    public void setVeiculoDePublicacao(VeiculoDePublicacao veiculoDePublicacao) {
        this.veiculoDePublicacao = veiculoDePublicacao;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getPagina() {
        return pagina;
    }

    public void setPagina(Integer pagina) {
        this.pagina = pagina;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
