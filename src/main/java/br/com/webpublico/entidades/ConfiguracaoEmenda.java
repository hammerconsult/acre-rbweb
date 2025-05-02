package br.com.webpublico.entidades;


import br.com.webpublico.enums.ModalidadeEmenda;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Audited
public class ConfiguracaoEmenda extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Quantidade Máxima de Emendas")
    private Integer quantidadeMaximaEmenda;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Valor Máximo por Vereador")
    private BigDecimal valorMaximoPorVereador;
    @Etiqueta("Código do Programa a Remover")
    @Obrigatorio
    private String codigoPrograma;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "configuracaoEmenda")
    private List<ConfiguracaoEmendaConta> contasDeDespesa;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "configuracaoEmenda")
    private List<ConfiguracaoEmendaFonte> fontesDeRecurso;

    public ConfiguracaoEmenda() {
        super();
        contasDeDespesa = Lists.newArrayList();
        fontesDeRecurso = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Integer getQuantidadeMaximaEmenda() {
        return quantidadeMaximaEmenda;
    }

    public void setQuantidadeMaximaEmenda(Integer quantidadeMaximaEmenda) {
        this.quantidadeMaximaEmenda = quantidadeMaximaEmenda;
    }

    public BigDecimal getValorMaximoPorVereador() {
        return valorMaximoPorVereador;
    }

    public void setValorMaximoPorVereador(BigDecimal valorMaximoPorVereador) {
        this.valorMaximoPorVereador = valorMaximoPorVereador;
    }

    public String getCodigoPrograma() {
        return codigoPrograma;
    }

    public void setCodigoPrograma(String codigoPrograma) {
        this.codigoPrograma = codigoPrograma;
    }

    public List<ConfiguracaoEmendaConta> getContasDeDespesaIndiretas() {
        List<ConfiguracaoEmendaConta> retorno = Lists.newArrayList();
        for (ConfiguracaoEmendaConta configuracaoEmendaConta : contasDeDespesa) {
            if (configuracaoEmendaConta.getModalidadeEmenda().isIndireta()) {
                retorno.add(configuracaoEmendaConta);
            }
        }
        return retorno;
    }

    public List<ConfiguracaoEmendaConta> getContasDeDespesaDiretas() {
        List<ConfiguracaoEmendaConta> retorno = Lists.newArrayList();
        for (ConfiguracaoEmendaConta configuracaoEmendaConta : contasDeDespesa) {
            if (!configuracaoEmendaConta.getModalidadeEmenda().isIndireta()) {
                retorno.add(configuracaoEmendaConta);
            }
        }
        return retorno;
    }

    public List<ConfiguracaoEmendaConta> getContasDeDespesa() {
        return contasDeDespesa;
    }

    public void setContasDeDespesa(List<ConfiguracaoEmendaConta> contasDeDespesa) {
        this.contasDeDespesa = contasDeDespesa;
    }

    public List<ConfiguracaoEmendaFonte> getFontesDeRecurso() {
        return fontesDeRecurso;
    }

    public void setFontesDeRecurso(List<ConfiguracaoEmendaFonte> fontesDeRecurso) {
        this.fontesDeRecurso = fontesDeRecurso;
    }
}
