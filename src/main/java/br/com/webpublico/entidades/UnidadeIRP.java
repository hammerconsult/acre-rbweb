package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Unidade Intenção Registro de Preço")
public class UnidadeIRP extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Intenção de Registro Preço")
    private IntencaoRegistroPreco intencaoRegistroPreco;

    @ManyToOne
    @Etiqueta("Unidade Gerenciadora")
    private UnidadeOrganizacional unidadeGerenciadora;

    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Fim de Vigência")
    private Date fimVigencia;

    public UnidadeIRP() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IntencaoRegistroPreco getIntencaoRegistroPreco() {
        return intencaoRegistroPreco;
    }

    public void setIntencaoRegistroPreco(IntencaoRegistroPreco intencaoRegistroPreco) {
        this.intencaoRegistroPreco = intencaoRegistroPreco;
    }

    public UnidadeOrganizacional getUnidadeGerenciadora() {
        return unidadeGerenciadora;
    }

    public void setUnidadeGerenciadora(UnidadeOrganizacional unidadeGerenciadora) {
        this.unidadeGerenciadora = unidadeGerenciadora;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }
}
