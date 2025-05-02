/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.enums.FuncaoResponsavelUnidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Administrativo")
@Etiqueta("Responsável por Unidade")
public class ResponsavelUnidade extends SuperEntidade implements PossuidorArquivo, Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Unidade Organizacional")
    @Pesquisavel
    @Obrigatorio
    private UnidadeOrganizacional unidadeOrganizacional;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Inicio de Vigência")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private Date inicioVigencia;
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Fim de Vigência")
    @Pesquisavel
    private Date fimVigencia;
    @OneToOne
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Pessoa")
    @Pesquisavel
    private Pessoa pessoa;
    @OneToOne
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Função")
    @Pesquisavel
    private FuncaoResponsavelUnidade funcao;
    @Tabelavel
    @Etiqueta("Curriculo")
    @Pesquisavel
    private String curriculo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "responsavel", orphanRemoval = true)
    private List<AgendaResponsavelUnidade> agenda;
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public ResponsavelUnidade() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
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

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public FuncaoResponsavelUnidade getFuncao() {
        return funcao;
    }

    public void setFuncao(FuncaoResponsavelUnidade funcao) {
        this.funcao = funcao;
    }

    public String getCurriculo() {
        return curriculo;
    }

    public void setCurriculo(String curriculo) {
        this.curriculo = curriculo;
    }

    public List<AgendaResponsavelUnidade> getAgenda() {
        return agenda;
    }

    public void setAgenda(List<AgendaResponsavelUnidade> agenda) {
        this.agenda = agenda;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public void validarAntesDeConfirmar() throws Exception {
        if (!Util.validaCampos(this)) {
            throw new Exception("Existem campos obrigatórios que não foram informados.");
        }

        if (this.fimVigencia.before(this.inicioVigencia)) {
            throw new Exception("A DATA INICIAL da vigência deve ser ANTERIOR a DATA FINAL DE VIGÊNCIA.");
        }
    }

    @Override
    public String toString() {
        return pessoa + " - " + funcao.getDescricao();
    }
}
