/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Contribuinte com Débitos de Outorga")
public class ContribuinteDebitoOutorga extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private CadastroEconomico cadastroEconomico;
    @Pesquisavel
    private BigDecimal percentual;
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 5)
    @Etiqueta("Data de Cadastro")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date cadastradoEm;
    @Etiqueta("Data de Atualização")
    @Tabelavel(ordemApresentacao = 7)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date atualizadoEm;
    @OneToOne
    @Tabelavel(ordemApresentacao = 6)
    @Etiqueta("Usuário de Cadastro")
    private UsuarioSistema usuarioCadastrou;
    @OneToOne
    @Etiqueta("Usuário de Atualização")
    @Tabelavel(ordemApresentacao = 8)
    private UsuarioSistema usuarioAlterou;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "contribuinteDebitoOutorga")
    private List<OutorgaIPO> listaIpo;
    @Transient
    @Tabelavel(ordemApresentacao = 3)
    @Etiqueta("Nome/Razão Social")
    private Pessoa pessoaParaLista;
    @Transient
    @Tabelavel(ordemApresentacao = 4)
    @Etiqueta("CNPJ")
    private String cnpj;
    @Transient
    @Tabelavel(ordemApresentacao = 2)
    @Pesquisavel
    @Etiqueta("C.M.C.")
    private String inscricaoCadastral;
    @ManyToOne
    @Tabelavel(ordemApresentacao = 1)
    @Pesquisavel
    @Etiqueta("Exercício de Referência")
    private Exercicio exercicio;
    /*
     * Os atributos usuarioQueCadastrou e usuarioQueAlterou devem ser preenchidos com o login do usuario em questão.
     * Os atributos usuarioCadastrou e usuarioAlterou não serão atribuídos na migração, pois até o momento os usuários não foram migrados.
     * Quando os usuários forem migrados, os atributos usuarioQueCadastrou e usuarioQueAlterou poderão ser utilizados para recuperar
     * seus respectivos objetos de Usuario Sistema.
     */
    private String usuarioQueCadastrou;
    private String usuarioQueAlterou;

    public ContribuinteDebitoOutorga() {
        this.listaIpo = new ArrayList<>();
        this.criadoEm = System.nanoTime();
    }

    public ContribuinteDebitoOutorga(Long id, CadastroEconomico cmc, Date cadastradoEm, UsuarioSistema usuarioCadastrou, Date atualizadoEm, UsuarioSistema usuarioAlterou) {
        this.setCadastroEconomico(cmc);
        this.id = id;
        this.inscricaoCadastral = cmc.getInscricaoCadastral();
        this.pessoaParaLista = cmc.getPessoa();
        this.cnpj = cmc.getPessoa().getCpf_Cnpj();
        this.cadastradoEm = cadastradoEm;
        this.usuarioCadastrou = usuarioCadastrou;
        this.atualizadoEm = atualizadoEm;
        this.usuarioAlterou = usuarioAlterou;
    }

    public Pessoa getPessoaParaLista() {
        return pessoaParaLista;
    }

    public void setPessoaParaLista(Pessoa pessoaParaLista) {
        this.pessoaParaLista = pessoaParaLista;
    }

    public Date getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(Date atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public List<OutorgaIPO> getListaIpo() {
        return listaIpo;
    }

    public void setListaIpo(List<OutorgaIPO> listaIpo) {
        this.listaIpo = listaIpo;
    }

    public Date getCadastradoEm() {
        return cadastradoEm;
    }

    public void setCadastradoEm(Date cadastradoEm) {
        this.cadastradoEm = cadastradoEm;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public UsuarioSistema getUsuarioAlterou() {
        return usuarioAlterou;
    }

    public void setUsuarioAlterou(UsuarioSistema usuarioAlterou) {
        this.usuarioAlterou = usuarioAlterou;
    }

    public UsuarioSistema getUsuarioCadastrou() {
        return usuarioCadastrou;
    }

    public void setUsuarioCadastrou(UsuarioSistema usuarioCadastrou) {
        this.usuarioCadastrou = usuarioCadastrou;
    }

    public String getUsuarioQueAlterou() {
        return usuarioQueAlterou;
    }

    public void setUsuarioQueAlterou(String usuarioQueAlterou) {
        this.usuarioQueAlterou = usuarioQueAlterou;
    }

    public String getUsuarioQueCadastrou() {
        return usuarioQueCadastrou;
    }

    public void setUsuarioQueCadastrou(String usuarioQueCadastrou) {
        this.usuarioQueCadastrou = usuarioQueCadastrou;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
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
    public String toString() {
        return "br.com.webpublico.ContribuinteDebitoOutorga [" + id + "]";
    }

}
