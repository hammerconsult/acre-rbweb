package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoContratoCalamidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Etiqueta("Calamidade Pública/Contrato")
public class CalamidadePublicaContrato extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CalamidadePublica calamidadePublica;
    @ManyToOne
    @Etiqueta("Contrato")
    @Obrigatorio
    private Contrato contrato;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private SituacaoContratoCalamidade situacao;

    public CalamidadePublicaContrato() {
        super();
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

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public SituacaoContratoCalamidade getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoContratoCalamidade situacao) {
        this.situacao = situacao;
    }
}
