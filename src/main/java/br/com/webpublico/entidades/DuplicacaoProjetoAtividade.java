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
@Etiqueta("Duplicação de Projeto/Atividade")
public class DuplicacaoProjetoAtividade extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Data")
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    private Date data;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;
    @ManyToOne(cascade = CascadeType.MERGE)
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Projeto/Atividade")
    private AcaoPPA projetoAtividade;
    @Obrigatorio
    @Etiqueta("Nova Descrição")
    private String novaDescricao;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Nova Unidade Responsável")
    private UnidadeOrganizacional unidadeOrganizacional;

    public DuplicacaoProjetoAtividade() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public AcaoPPA getProjetoAtividade() {
        return projetoAtividade;
    }

    public void setProjetoAtividade(AcaoPPA projetoAtividade) {
        this.projetoAtividade = projetoAtividade;
    }

    public String getNovaDescricao() {
        return novaDescricao;
    }

    public void setNovaDescricao(String novaDescricao) {
        this.novaDescricao = novaDescricao;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }
}
