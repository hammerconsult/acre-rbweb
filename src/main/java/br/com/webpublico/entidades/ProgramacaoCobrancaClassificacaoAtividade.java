package br.com.webpublico.entidades;

import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author octavio
 */
@Audited
@Entity
@Table(name = "PROGCOBRANCACLASSATIVIDADE")
public class ProgramacaoCobrancaClassificacaoAtividade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Programação de Cobrança")
    private ProgramacaoCobranca programacaoCobranca;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Classificação da Atividade")
    private ClassificacaoAtividade classificacaoAtividade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProgramacaoCobranca getProgramacaoCobranca() {
        return programacaoCobranca;
    }

    public void setProgramacaoCobranca(ProgramacaoCobranca programacaoCobranca) {
        this.programacaoCobranca = programacaoCobranca;
    }

    public ClassificacaoAtividade getClassificacaoAtividade() {
        return classificacaoAtividade;
    }

    public void setClassificacaoAtividade(ClassificacaoAtividade classificacaoAtividade) {
        this.classificacaoAtividade = classificacaoAtividade;
    }
}
