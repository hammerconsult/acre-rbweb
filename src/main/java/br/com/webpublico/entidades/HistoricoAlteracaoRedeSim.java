package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Fabio on 09/10/2018.
 */
@Entity
@Audited
@Table(name = "HISTORICOREDESIM")
@Etiqueta("Histórico de Alteração REDESIM")
public class HistoricoAlteracaoRedeSim extends SuperEntidade implements Comparable<HistoricoAlteracaoRedeSim> {

    public static String CADASTRO_ECONOMICO = "Cadastro Econômico - Sincronização Manual";
    public static String PESSOA_JURIDICA = "Pessoa Jurídica - Sincronização Manual";
    public static String SINCRONIZADOR_REDESIM = "Sincronizador REDESIM - Sincronização Manual";
    public static String AGENDAMENTO_TAREFA = "Agendamento de Tarefa - Sincronização Automática";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Pessoa Jurídica")
    private PessoaJuridica pessoaJuridica;

    @ManyToOne
    @Etiqueta("Usuário Sistema")
    private UsuarioSistema usuarioSistema;

    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data da Alteração")
    private Date dataAlteracao;

    @Etiqueta("Conteúdo")
    private String conteudo;

    @Etiqueta("Descrição")
    private String descricao;

    public HistoricoAlteracaoRedeSim() {
    }

    public HistoricoAlteracaoRedeSim(PessoaJuridica pessoaJuridica, UsuarioSistema usuarioSistema, String descricaoHistorico, String conteudoJason) {
        this.pessoaJuridica = pessoaJuridica;
        this.usuarioSistema = usuarioSistema;
        this.dataAlteracao = new Date();
        this.conteudo = conteudoJason;
        this.descricao = descricaoHistorico;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    @Override
    public int compareTo(HistoricoAlteracaoRedeSim o) {
        if (o.getDataAlteracao() != null && getDataAlteracao() != null) {
            return o.getDataAlteracao().compareTo(getDataAlteracao());
        }
        return 0;
    }
}
