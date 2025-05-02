package br.com.webpublico.entidades;

import com.beust.jcommander.internal.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class AlteracaoHorFuncionamento extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAlteracao;
    private boolean calcularAlvara;
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    @OneToMany(mappedBy = "alteracaoHorFuncionamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorariosFuncAlteracao> horariosAlterados;

    public AlteracaoHorFuncionamento() {
        this.calcularAlvara = Boolean.FALSE;
        this.horariosAlterados = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public boolean isCalcularAlvara() {
        return calcularAlvara;
    }

    public void setCalcularAlvara(boolean calcularAlvara) {
        this.calcularAlvara = calcularAlvara;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public List<HorariosFuncAlteracao> getHorariosAlterados() {
        return horariosAlterados;
    }

    public void setHorariosAlterados(List<HorariosFuncAlteracao> horariosAlterados) {
        this.horariosAlterados = horariosAlterados;
    }
}
