package br.com.webpublico.entidades.tributario;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class LicenciamentoAmbientalLei extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Descrição Reduzida")
    private String descricaoReduzida;
    @Obrigatorio
    @Etiqueta("Descrição Completa")
    private String descricaoCompleta;
    @Obrigatorio
    @Etiqueta("Arquivo")
    @OneToOne(cascade ={CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;
    private Boolean ativo;

    public LicenciamentoAmbientalLei() {
        super();
        this.ativo = Boolean.TRUE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricaoReduzida() {
        return descricaoReduzida;
    }

    public void setDescricaoReduzida(String descricaoReduzida) {
        this.descricaoReduzida = descricaoReduzida;
    }

    public String getDescricaoCompleta() {
        return descricaoCompleta;
    }

    public void setDescricaoCompleta(String descricaoCompleta) {
        this.descricaoCompleta = descricaoCompleta;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return descricaoReduzida;
    }
}
