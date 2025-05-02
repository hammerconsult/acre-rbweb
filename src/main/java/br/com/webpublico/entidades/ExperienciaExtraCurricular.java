package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoExperienciaExtraCurricular;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by mga on 01/06/2017.
 */
@GrupoDiagrama(nome = "Recursos Humanos")
@Audited
@Entity
@Etiqueta("Experiência Extracurricular")
public class ExperienciaExtraCurricular extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Servidor")
    private VinculoFP vinculoFP;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Cadastro")
    private TipoExperienciaExtraCurricular tipoExperiencia;

    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;

    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Fim de Vigência")
    private Date fimVigencia;

    @Pesquisavel
    @Etiqueta("Carga Horária")
    private Integer cargaHoraria;

    @Pesquisavel
    @Etiqueta("Instituição")
    private String instituicao;

    @Pesquisavel
    @Etiqueta("Curso")
    private String curso;

    @Pesquisavel
    @ManyToOne
    @Etiqueta("Atribuição da Comissão")
    private AtribuicaoComissaoExtraCurricular atribuicaoComissao;

    @Pesquisavel
    @ManyToOne
    @Etiqueta("Tipo Comissão")
    private TipoComissaoExtraCurricular tipoComissao;

    public ExperienciaExtraCurricular() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public TipoExperienciaExtraCurricular getTipoExperiencia() {
        return tipoExperiencia;
    }

    public void setTipoExperiencia(TipoExperienciaExtraCurricular tipoExperiencia) {
        this.tipoExperiencia = tipoExperiencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public Integer getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(Integer cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public String getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public AtribuicaoComissaoExtraCurricular getAtribuicaoComissao() {
        return atribuicaoComissao;
    }

    public void setAtribuicaoComissao(AtribuicaoComissaoExtraCurricular atribuicaoComissao) {
        this.atribuicaoComissao = atribuicaoComissao;
    }

    public TipoComissaoExtraCurricular getTipoComissao() {
        return tipoComissao;
    }

    public void setTipoComissao(TipoComissaoExtraCurricular tipoComissao) {
        this.tipoComissao = tipoComissao;
    }

    public boolean isTipoCadastroComissao() {
        return this.tipoExperiencia != null && TipoExperienciaExtraCurricular.COMISSAO.equals(this.tipoExperiencia);
    }

    public boolean isTipoCadastroCurso() {
        return this.tipoExperiencia != null && TipoExperienciaExtraCurricular.CURSO.equals(this.tipoExperiencia);
    }

    @Override
    public String toString() {
        try {
            return this.vinculoFP.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
