package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Etiqueta("Calamidade Pública - Bens Doados")
@Table(name = "CALAMIDADEPUBLICABEMDOADO")
public class CalamidadePublicaBemDoado extends SuperEntidade implements Serializable, PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CalamidadePublica calamidadePublica;
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @ManyToOne
    @Etiqueta("Unidade de Medida")
    private UnidadeMedida unidadeMedida;
    @Etiqueta("Quantidade")
    private Integer quantidade;
    @ManyToOne
    @Etiqueta("Órgão/Entidade pela entrega")
    private UnidadeOrganizacional unidadeOrganizacional;
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @Etiqueta("Donatário")
    private String donatario;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public CalamidadePublicaBemDoado() {
        super();
        quantidade = 0;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CalamidadePublica getCalamidadePublica() {
        return calamidadePublica;
    }

    public void setCalamidadePublica(CalamidadePublica calamidadePublica) {
        this.calamidadePublica = calamidadePublica;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public String getDonatario() {
        return donatario;
    }

    public void setDonatario(String donatario) {
        this.donatario = donatario;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
        if (hierarquiaOrganizacional != null) {
            unidadeOrganizacional = hierarquiaOrganizacional.getSubordinada();
        }
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }
}
