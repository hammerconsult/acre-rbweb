package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Length;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Publicação Alteração Contratual")
@Table(name = "PUBLICACAOALTERACAOCONT")
public class PublicacaoAlteracaoContratual extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Alteração Contratual")
    private AlteracaoContratual alteracaoContratual;

    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Publicação")
    private Date dataPublicacao;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Veículo de Publicação")
    private VeiculoDePublicacao veiculoDePublicacao;

    @Obrigatorio
    @Etiqueta("Número da Publicação")
    private Integer numero;

    @Obrigatorio
    @Etiqueta("Página da Publicação")
    private Integer pagina;

    @Length(maximo = 3000)
    @Etiqueta("Observação")
    private String observacao;

    public PublicacaoAlteracaoContratual() {
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

    public AlteracaoContratual getAlteracaoContratual() {
        return alteracaoContratual;
    }

    public void setAlteracaoContratual(AlteracaoContratual alteracaoContratual) {
        this.alteracaoContratual = alteracaoContratual;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
