package br.com.webpublico.dte.entidades;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.beust.jcommander.internal.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Etiqueta("Grupo de Contribuinte - DTE")
@Entity
@Audited
public class GrupoContribuinteDte extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Descrição")
    @Pesquisavel
    @Tabelavel
    private String descricao;

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GrupoContribuinteCmcDte> cadastros;

    public GrupoContribuinteDte() {
        super();
        cadastros = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<GrupoContribuinteCmcDte> getCadastros() {
        return cadastros;
    }

    public void setCadastros(List<GrupoContribuinteCmcDte> cadastros) {
        this.cadastros = cadastros;
    }

    public boolean hasCadastro(CadastroEconomico cadastroEconomico) {
        if (cadastros != null && cadastros.isEmpty()) {
            for (GrupoContribuinteCmcDte cadastro : cadastros) {
                if (cadastro.getCadastroEconomico().equals(cadastroEconomico))
                    return true;
            }
        }
        return false;
    }
}
