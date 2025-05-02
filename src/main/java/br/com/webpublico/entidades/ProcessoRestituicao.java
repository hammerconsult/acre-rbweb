package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCredorRestituicao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by andregustavo on 18/05/2015.
 */
@Entity
@Audited
@Etiqueta("Processo de Restituição")
public class ProcessoRestituicao extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data")
    private Date data;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Protocolo")
    private Processo protocolo;
    @ManyToOne
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;
    @Etiqueta("Motivo")
    private String motivo;
    private Boolean atualizacaoMonetaria;
    private BigDecimal valorRestituicao;
    private BigDecimal valorRestituicaoAtualizado;
    @ManyToOne
    @Etiqueta("Restituinte")
    private Pessoa restituinte;
    @OneToMany(mappedBy = "processoRestituicao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PessoaRestituicao> pessoas;
    @OneToMany(mappedBy = "processoRestituicao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestituicaoParcela> parcelas;
    @OneToMany(mappedBy = "processoRestituicao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestituicaoPagamento> pagamentos;

    public ProcessoRestituicao() {
        atualizacaoMonetaria = false;
        pessoas = new ArrayList<>();
        parcelas = new ArrayList<>();
        pagamentos = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Processo getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(Processo protocolo) {
        this.protocolo = protocolo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public List<PessoaRestituicao> getPessoas() {
        return pessoas;
    }

    public void setPessoas(List<PessoaRestituicao> pessoas) {
        this.pessoas = pessoas;
    }

    public List<RestituicaoParcela> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<RestituicaoParcela> parcelas) {
        this.parcelas = parcelas;
    }

    public List<RestituicaoPagamento> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<RestituicaoPagamento> pagamentos) {
        this.pagamentos = pagamentos;
    }

    public Boolean getAtualizacaoMonetaria() {
        return atualizacaoMonetaria;
    }

    public void setAtualizacaoMonetaria(Boolean atualizacaoMonetaria) {
        this.atualizacaoMonetaria = atualizacaoMonetaria;
    }

    public Pessoa getRestituinte() {
        return restituinte;
    }

    public void setRestituinte(Pessoa restituinte) {
        this.restituinte = restituinte;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getValorRestituicao() {
        return valorRestituicao;
    }

    public void setValorRestituicao(BigDecimal valorRestituicao) {
        this.valorRestituicao = valorRestituicao;
    }

    public BigDecimal getValorRestituicaoAtualizado() {
        return valorRestituicaoAtualizado;
    }

    public void setValorRestituicaoAtualizado(BigDecimal valorRestituicaoAtualizado) {
        this.valorRestituicaoAtualizado = valorRestituicaoAtualizado;
    }
}
