package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.Servico;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@GrupoDiagrama(nome = "CadastroEconomico")
@Entity

@Audited
@Etiqueta("Código de Tributação")
public class CodigoTributacao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;

    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Serviço")
    private Servico servico;

    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Código")
    private String codigo;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descricão")
    @Obrigatorio
    private String descricao;


    public CodigoTributacao() {
        super();
    }

    public CodigoTributacao(String codigo, String descricao) {
        this();
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }

    public String getToStringAutoComplete() {
        if (codigo == null) {
            return "";
        }
        if (descricao == null) {
            return codigo + " - ";
        }
        if (descricao.length() > 90) {
            StringBuilder sb = new StringBuilder(codigo).append(" - ").append(descricao.substring(0, 90)).append("...");
            return sb.toString();
        }
        return codigo + " - " + descricao;
    }
}
