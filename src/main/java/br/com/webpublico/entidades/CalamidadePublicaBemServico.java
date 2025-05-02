package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Etiqueta("Calamidade Pública - Bens e Serviços Recebidos")
@Table(name = "CALAMIDADEPUBLICABEMSERV")
public class CalamidadePublicaBemServico extends SuperEntidade implements Serializable {

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
    private UnidadeOrganizacional unidadeOrganizacional;
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @Etiqueta("Entidade Transferidor")
    private String pessoa;

    public CalamidadePublicaBemServico() {
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

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
        if (hierarquiaOrganizacional != null) {
            unidadeOrganizacional = hierarquiaOrganizacional.getSubordinada();
        }
    }

    public String getPessoa() {
        return pessoa;
    }

    public void setPessoa(String pessoa) {
        this.pessoa = pessoa;
    }
}
