package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Audited
public class ParametroSaudDocumento extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private ParametroSaud parametroSaud;
    @Etiqueta("Descrição")
    @Obrigatorio
    private String descricao;
    @Etiqueta("Extensões Permitidas")
    @Obrigatorio
    private String extensoesPermitidas;
    private Boolean ativo;

    public ParametroSaudDocumento() {
        ativo = Boolean.TRUE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParametroSaud getParametroSaud() {
        return parametroSaud;
    }

    public void setParametroSaud(ParametroSaud parametroSaud) {
        this.parametroSaud = parametroSaud;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getExtensoesPermitidas() {
        return extensoesPermitidas;
    }

    public void setExtensoesPermitidas(String extensoesPermitidas) {
        this.extensoesPermitidas = extensoesPermitidas;
    }

    public Boolean getAtivo() {
        return ativo != null ? ativo : Boolean.FALSE;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
