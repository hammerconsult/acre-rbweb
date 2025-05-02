package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.rh.esocial.TipoApuracaoFolha;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Etiqueta("Registro E-Social S-1299")
@Entity
@Audited
public class RegistroEventoEsocialS1299 extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Mês")
    private Mes mes;

    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Ano")
    private Exercicio exercicio;

    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Folha de Pagamento")
    private TipoFolhaDePagamento tipoFolhaDePagamento;

    @ManyToOne
    @Tabelavel
    private Entidade entidade;

    @ManyToOne
    private PessoaFisica responsavel;

    private Boolean remuneracaoTrabalhador;

    private Boolean rendimentoTrabalho;

    private Boolean aquisicaoProdutoRural;

    private Boolean comercializacaoProducao;

    private Boolean trabalhadorAvulsoNaoPortuario;

    private Boolean desoneracaoFolha;

    @Transient
    private TipoApuracaoFolha tipoApuracaoFolha;

    public RegistroEventoEsocialS1299() {
        remuneracaoTrabalhador = Boolean.FALSE;
        rendimentoTrabalho = Boolean.FALSE;
        aquisicaoProdutoRural = Boolean.FALSE;
        comercializacaoProducao = Boolean.FALSE;
        trabalhadorAvulsoNaoPortuario = Boolean.FALSE;
        desoneracaoFolha = Boolean.FALSE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public PessoaFisica getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(PessoaFisica responsavel) {
        this.responsavel = responsavel;
    }

    public Boolean getRemuneracaoTrabalhador() {
        return remuneracaoTrabalhador != null ? remuneracaoTrabalhador : false;
    }

    public void setRemuneracaoTrabalhador(Boolean remuneracaoTrabalhador) {
        this.remuneracaoTrabalhador = remuneracaoTrabalhador;
    }

    public Boolean getRendimentoTrabalho() {
        return rendimentoTrabalho != null ? rendimentoTrabalho : false;
    }

    public void setRendimentoTrabalho(Boolean rendimentoTrabalho) {
        this.rendimentoTrabalho = rendimentoTrabalho;
    }

    public Boolean getAquisicaoProdutoRural() {
        return aquisicaoProdutoRural != null ? aquisicaoProdutoRural : false;
    }

    public void setAquisicaoProdutoRural(Boolean aquisicaoProdutoRural) {
        this.aquisicaoProdutoRural = aquisicaoProdutoRural;
    }

    public Boolean getComercializacaoProducao() {
        return comercializacaoProducao != null ? comercializacaoProducao : false;
    }

    public void setComercializacaoProducao(Boolean comercializacaoProducao) {
        this.comercializacaoProducao = comercializacaoProducao;
    }

    public Boolean getTrabalhadorAvulsoNaoPortuario() {
        return trabalhadorAvulsoNaoPortuario != null ? trabalhadorAvulsoNaoPortuario : false;
    }

    public void setTrabalhadorAvulsoNaoPortuario(Boolean trabalhadorAvulsoNaoPortuario) {
        this.trabalhadorAvulsoNaoPortuario = trabalhadorAvulsoNaoPortuario;
    }

    public Boolean getDesoneracaoFolha() {
        return desoneracaoFolha != null ? desoneracaoFolha : false;
    }

    public void setDesoneracaoFolha(Boolean desoneracaoFolha) {
        this.desoneracaoFolha = desoneracaoFolha;
    }

    public TipoApuracaoFolha getTipoApuracaoFolha() {
        return tipoApuracaoFolha;
    }

    public void setTipoApuracaoFolha(TipoApuracaoFolha tipoApuracaoFolha) {
        this.tipoApuracaoFolha = tipoApuracaoFolha;
    }
}
