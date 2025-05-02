/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.AssistenteDetentorArquivoComposicao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Heinz
 */

@GrupoDiagrama(nome = "CadastroEconomico")
@Entity
@Audited
public class ParametrosFuncionarios implements Serializable, PossuidorArquivo {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ParametrosITBI parametrosbce;
    @ManyToOne
    @NotAudited
    private FuncaoParametrosITBI funcaoParametrosITBI;
    @Etiqueta("Vigência Inicial")
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date vigenciaInicial;
    @Etiqueta("Vigência Final")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date vigenciaFinal;
    @Etiqueta("Funcionário")
    @Obrigatorio
    @ManyToOne
    private PessoaFisica pessoa;
    @Invisivel
    @Transient
    private Long criadoEm;
    @OneToOne(cascade = CascadeType.ALL)
    @Obrigatorio
    @Etiqueta("Assinatura Digital")
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public ParametrosFuncionarios() {
        this.criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public FuncaoParametrosITBI getFuncaoParametrosITBI() {
        return funcaoParametrosITBI;
    }

    public void setFuncaoParametrosITBI(FuncaoParametrosITBI funcaoParametrosITBI) {
        this.funcaoParametrosITBI = funcaoParametrosITBI;
    }

    public PessoaFisica getPessoa() {
        return pessoa;
    }

    public void setPessoa(PessoaFisica pessoa) {
        this.pessoa = pessoa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParametrosITBI getParametros() {
        return parametrosbce;
    }

    public void setParametros(ParametrosITBI parametros) {
        this.parametrosbce = parametros;
    }

    public Date getVigenciaInicial() {
        return vigenciaInicial;
    }

    public void setVigenciaInicial(Date vigenciaInicial) {
        this.vigenciaInicial = vigenciaInicial;
    }

    public Date getVigenciaFinal() {
        return vigenciaFinal;
    }

    public void setVigenciaFinal(Date vigenciaFinal) {
        this.vigenciaFinal = vigenciaFinal;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    @Override
    public String toString() {
        return pessoa.toString();
    }
}
