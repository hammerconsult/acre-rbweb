package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoContaCorrenteTCE;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by Edi on 19/04/2016.
 */
@Entity
@Audited
@Etiqueta("Extensão da Fonte de Recurso")
public class ExtensaoFonteRecurso extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Código")
    private Long codigo;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Tipo de Conta Corrente")
    private TipoContaCorrenteTCE tipoContaCorrenteTCE;

    public ExtensaoFonteRecurso() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoContaCorrenteTCE getTipoContaCorrenteTCE() {
        return tipoContaCorrenteTCE;
    }

    public void setTipoContaCorrenteTCE(TipoContaCorrenteTCE tipoContaCorrenteTCE) {
        this.tipoContaCorrenteTCE = tipoContaCorrenteTCE;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }

    public Integer getCodigoEs() {
        return getCodigo().toString().startsWith("4") ? 2 :
            getCodigo().toString().startsWith("1") ||
                getCodigo().toString().startsWith("2") ||
                getCodigo().toString().startsWith("3") ? 1 : 0;
    }
}
