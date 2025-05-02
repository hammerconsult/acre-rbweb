package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.rh.esocial.InconsistenciaRetornoQualificacaoCadastral;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by mateus on 18/07/17.
 */
@Entity
@Audited
@Table(name = "RETORNOQUALIFICACAOCAD")
public class RetornoQualificacaoCadastral extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private QualificacaoCadastralPessoa qualificacaoCadPessoa;
    @Enumerated(EnumType.STRING)
    private InconsistenciaRetornoQualificacaoCadastral inconsistenciaRetorno;
    //Campo utilizado para a descrição do nome correto caso o retorno seja COD_CPF_NOME
    private String observacao;

    public RetornoQualificacaoCadastral() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InconsistenciaRetornoQualificacaoCadastral getInconsistenciaRetorno() {
        return inconsistenciaRetorno;
    }

    public void setInconsistenciaRetorno(InconsistenciaRetornoQualificacaoCadastral codigoRetorno) {
        this.inconsistenciaRetorno = codigoRetorno;
    }

    public QualificacaoCadastralPessoa getQualificacaoCadPessoa() {
        return qualificacaoCadPessoa;
    }

    public void setQualificacaoCadPessoa(QualificacaoCadastralPessoa qualificacaoCadastralPessoa) {
        this.qualificacaoCadPessoa = qualificacaoCadastralPessoa;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getDescricaoInconsistencia() {
        return inconsistenciaRetorno.getDescricaoProcessado();
    }
}
