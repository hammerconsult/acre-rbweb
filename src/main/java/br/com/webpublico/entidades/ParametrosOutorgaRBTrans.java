/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoMesVencimento;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author cheles
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Parâmetros de Outorga do RBTRANS")
@Table(name = "PARAMOUTORGARBTRANS")
public class ParametrosOutorgaRBTrans extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer diaVencimentoPrimeiraParcela;
    private Integer diaVencimentoSegundaParcela;
    @ManyToOne
    @Etiqueta("Exercício")
    @Tabelavel
    @Pesquisavel
    private Exercicio exercicio;
    @ManyToOne
    @Etiqueta("Dívida")
    @Tabelavel
    @Pesquisavel
    private Divida divida;
    @ManyToOne
    @Etiqueta("Tributo")
    @Tabelavel
    @Pesquisavel
    private Tributo tributo;
    @OneToOne
    @Pesquisavel
    @Etiqueta("Usuário de Cadastro")
    private UsuarioSistema usuarioCadastrou;
    @Pesquisavel
    @Etiqueta("Data de Cadastro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cadastradoEm;
    @OneToOne
    @Pesquisavel
    @Etiqueta("Usuário de Cadastro")
    private UsuarioSistema usuarioAlterou;
    @Pesquisavel
    @Etiqueta("Data de Cadastro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date atualizadoEm;
    @Etiqueta("Tipo mês de referência.")
    @Enumerated(EnumType.STRING)
    private TipoMesVencimento tipoMesVencimento;
    @ManyToOne
    private TipoDoctoOficial tipoDoctoOficial;

    @OneToMany(mappedBy = "parametroOutorga", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParametroOutorgaSubvencao> parametroOutorgaSubvencao;

    public ParametrosOutorgaRBTrans() {
        parametroOutorgaSubvencao = Lists.newArrayList();
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioSistema getUsuarioCadastrou() {
        return usuarioCadastrou;
    }

    public void setUsuarioCadastrou(UsuarioSistema usuarioCadastrou) {
        this.usuarioCadastrou = usuarioCadastrou;
    }

    public Date getCadastradoEm() {
        return cadastradoEm;
    }

    public void setCadastradoEm(Date cadastradoEm) {
        this.cadastradoEm = cadastradoEm;
    }

    public UsuarioSistema getUsuarioAlterou() {
        return usuarioAlterou;
    }

    public void setUsuarioAlterou(UsuarioSistema usuarioAlterou) {
        this.usuarioAlterou = usuarioAlterou;
    }

    public Date getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(Date atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Integer getDiaVencimentoPrimeiraParcela() {
        return diaVencimentoPrimeiraParcela;
    }

    public void setDiaVencimentoPrimeiraParcela(Integer diaVencimentoPrimeiraParcela) {
        this.diaVencimentoPrimeiraParcela = diaVencimentoPrimeiraParcela;
    }


    public List<ParametroOutorgaSubvencao> getParametroOutorgaSubvencao() {
        return parametroOutorgaSubvencao;
    }

    public void setParametroOutorgaSubvencao(List<ParametroOutorgaSubvencao> parametroOutorgaSubvencao) {
        this.parametroOutorgaSubvencao = parametroOutorgaSubvencao;
    }

    public Integer getDiaVencimentoSegundaParcela() {
        return diaVencimentoSegundaParcela;
    }

    public void setDiaVencimentoSegundaParcela(Integer diaVencimentoSegundaParcela) {
        this.diaVencimentoSegundaParcela = diaVencimentoSegundaParcela;
    }

    public TipoMesVencimento getTipoMesVencimento() {
        return tipoMesVencimento;
    }

    public void setTipoMesVencimento(TipoMesVencimento tipoMesVencimento) {
        this.tipoMesVencimento = tipoMesVencimento;
    }

    public TipoDoctoOficial getTipoDoctoOficial() {
        return tipoDoctoOficial;
    }

    public void setTipoDoctoOficial(TipoDoctoOficial tipoDoctoOficial) {
        this.tipoDoctoOficial = tipoDoctoOficial;
    }
}
