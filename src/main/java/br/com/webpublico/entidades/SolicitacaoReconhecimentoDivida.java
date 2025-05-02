package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoSolicitacaoReconhecimentoDivida;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@GrupoDiagrama(nome = "Contabil")
@Audited
@Etiqueta("Solicitação de Empenho/Reconhecimento de Dívida do Exercício")
@Table(name = "SOLRECONHECIMENTODIVIDA")
public class SolicitacaoReconhecimentoDivida extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    private String numero;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Cadastro")
    private Date dataCadastro;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Reconhecimento da Dívida do Exercício")
    private ReconhecimentoDivida reconhecimentoDivida;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Classe da Pessoa")
    private ClasseCredor classeCredor;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoSolicitacaoReconhecimentoDivida situacao;
    @ManyToOne
    @Etiqueta("Criado por")
    private UsuarioSistema usuarioSistema;

    public SolicitacaoReconhecimentoDivida() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public ReconhecimentoDivida getReconhecimentoDivida() {
        return reconhecimentoDivida;
    }

    public void setReconhecimentoDivida(ReconhecimentoDivida reconhecimentoDivida) {
        this.reconhecimentoDivida = reconhecimentoDivida;
    }

    public SituacaoSolicitacaoReconhecimentoDivida getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoSolicitacaoReconhecimentoDivida situacao) {
        this.situacao = situacao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public boolean isConcluido() {
        return SituacaoSolicitacaoReconhecimentoDivida.CONCLUIDO.equals(situacao);
    }
}
