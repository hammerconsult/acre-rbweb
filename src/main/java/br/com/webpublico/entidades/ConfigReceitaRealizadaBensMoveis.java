package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "CONFIGRECREALIZADABENSMOV")
@Entity
@Audited
@Etiqueta("Configuração de Receita Realizada / Grupo Patrimonial")
public class ConfigReceitaRealizadaBensMoveis extends SuperEntidade implements Serializable, ValidadorVigencia {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Conta de Receita")
    private Conta contaReceita;
    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Grupo Patrimonial")
    private GrupoBem grupoBem;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Grupo")
    @Enumerated(EnumType.STRING)
    private TipoGrupo tipoGrupo;
    @Tabelavel
    @Etiqueta("Início de Vigência")
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Obrigatorio
    private Date inicioVigencia;
    @Tabelavel
    @Etiqueta("Fim de Vigência")
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date fimVigencia;

    public ConfigReceitaRealizadaBensMoveis() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conta getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(Conta contaReceita) {
        this.contaReceita = contaReceita;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public TipoGrupo getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupo tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    @Override
    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return fimVigencia;
    }

    @Override
    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    @Override
    public String toString() {
        return contaReceita + " - " + grupoBem;
    }
}
