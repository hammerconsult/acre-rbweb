package br.com.webpublico.entidades.rh.configuracao;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SuperEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class DirfCodigo extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Exercicio exercicio;
    @ManyToOne
    private ConfiguracaoRH configuracaoRH;
    private String codigo;
    private Boolean permiteEmitirInfRendimentos;

    public DirfCodigo() {
        permiteEmitirInfRendimentos = true;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public ConfiguracaoRH getConfiguracaoRH() {
        return configuracaoRH;
    }

    public void setConfiguracaoRH(ConfiguracaoRH configuracaoRH) {
        this.configuracaoRH = configuracaoRH;
    }

    public Boolean getPermiteEmitirInfRendimentos() {
        return permiteEmitirInfRendimentos == null ? Boolean.FALSE : permiteEmitirInfRendimentos;
    }

    public void setPermiteEmitirInfRendimentos(Boolean permiteEmitirInfRendimentos) {
        this.permiteEmitirInfRendimentos = permiteEmitirInfRendimentos;
    }
}
