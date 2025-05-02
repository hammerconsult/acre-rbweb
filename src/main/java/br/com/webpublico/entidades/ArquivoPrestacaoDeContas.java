package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Audited
@Entity
@Etiqueta("Arquivo Prestação de Contas")
public class ArquivoPrestacaoDeContas extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data Geração")
    private Date dataGeracao;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Mês")
    private Mes mes;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;
    @Obrigatorio
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Etiqueta("Arquivo")
    private Arquivo arquivo;

    public ArquivoPrestacaoDeContas() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(Date dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }
}
