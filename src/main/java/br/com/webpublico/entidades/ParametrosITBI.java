/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoBaseCalculo;
import br.com.webpublico.enums.TipoITBI;
import br.com.webpublico.enums.VencimentoLaudoDeAvaliacao;
import br.com.webpublico.enums.VerificarDebitosDoImovel;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Heinz
 */

@GrupoDiagrama(nome = "CadastroEconomico")
@Audited
@Etiqueta("Parâmetros do ITBI")
@Entity
public class ParametrosITBI extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo do ITBI")
    @Obrigatorio
    private TipoITBI tipoITBI;
    @Etiqueta("Código")
    private Long codigo;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Dívida")
    @ManyToOne
    @Obrigatorio
    private Divida divida;
    @Etiqueta("Inicia por Exercício")
    private boolean iniciaPorExercicio;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Exercício")
    @Obrigatorio
    private Exercicio exercicio;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo Base de Cálculo")
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private TipoBaseCalculo tipoBaseCalculo;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Verificar Débitos do Imóvel")
    private VerificarDebitosDoImovel verificarDebitosImovel;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Vencimento do Laudo de Avaliação")
    private VencimentoLaudoDeAvaliacao vencLaudoDeAvaliacao;
    @Obrigatorio
    @Etiqueta("Vencimento do Laudo de Avaliação em Dias")
    private Integer vencLaudoAvaliacaoEmDias;
    @Etiqueta("Percentual de Reajuste")
    private BigDecimal percentualReajuste;
    @Obrigatorio
    @Etiqueta("Vencimento da Primeira Parcela (em Dias a partir da data de lançamento)")
    private Integer diaFixoVencimento;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parametrosbce", orphanRemoval = true)
    private List<ParametrosFuncionarios> listaFuncionarios;
    @OneToMany(mappedBy = "parametrosITBI", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FaixaValorParcelamento> listaDeFaixaValorParcelamento;
    @ManyToOne
    private Tributo tributoITBI;
    private int diasVencimentoSegundaViaItbi;
    @OneToMany(mappedBy = "parametrosITBI", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParametrosITBIDocumento> documentos;

    public ParametrosITBI() {
        super();
        this.listaFuncionarios = Lists.newArrayList();
        this.listaDeFaixaValorParcelamento = Lists.newArrayList();
        this.documentos = Lists.newArrayList();
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Integer getDiaFixoVencimento() {
        return diaFixoVencimento;
    }

    public void setDiaFixoVencimento(Integer diaFixoVencimento) {
        this.diaFixoVencimento = diaFixoVencimento;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public boolean isIniciaPorExercicio() {
        return iniciaPorExercicio;
    }

    public void setIniciaPorExercicio(boolean iniciaPorExercicio) {
        this.iniciaPorExercicio = iniciaPorExercicio;
    }

    public List<ParametrosFuncionarios> getListaFuncionarios() {
        return listaFuncionarios;
    }

    public void setListaFuncionarios(List<ParametrosFuncionarios> listaFuncionarios) {
        this.listaFuncionarios = listaFuncionarios;
    }

    public BigDecimal getPercentualReajuste() {
        return percentualReajuste;
    }

    public void setPercentualReajuste(BigDecimal percentualReajuste) {
        if (percentualReajuste != null && !percentualReajuste.equals(BigDecimal.ZERO)) {
            this.percentualReajuste = percentualReajuste;
        } else {
            this.percentualReajuste = null;
        }
    }

    public TipoBaseCalculo getTipoBaseCalculo() {
        return tipoBaseCalculo;
    }

    public void setTipoBaseCalculo(TipoBaseCalculo tipoBaseCalculo) {
        this.tipoBaseCalculo = tipoBaseCalculo;
    }

    public VerificarDebitosDoImovel getVerificarDebitosImovel() {
        return verificarDebitosImovel;
    }

    public void setVerificarDebitosImovel(VerificarDebitosDoImovel verificarDebitosDoImovel) {
        this.verificarDebitosImovel = verificarDebitosDoImovel;
    }

    public VencimentoLaudoDeAvaliacao getVencLaudoDeAvaliacao() {
        return vencLaudoDeAvaliacao;
    }

    public void setVencLaudoDeAvaliacao(VencimentoLaudoDeAvaliacao vencimentoDoLaudoDeAvaliação) {
        this.vencLaudoDeAvaliacao = vencimentoDoLaudoDeAvaliação;
    }

    public Integer getVencLaudoAvaliacaoEmDias() {
        return vencLaudoAvaliacaoEmDias;
    }

    public void setVencLaudoAvaliacaoEmDias(Integer vencimentoDoLaudoDeAvaliacaoEmDias) {
        this.vencLaudoAvaliacaoEmDias = vencimentoDoLaudoDeAvaliacaoEmDias;
    }

    public List<FaixaValorParcelamento> getListaDeFaixaValorParcelamento() {
        return listaDeFaixaValorParcelamento;
    }

    public void setListaDeFaixaValorParcelamento(List<FaixaValorParcelamento> faixaValorParcelamento) {
        this.listaDeFaixaValorParcelamento = faixaValorParcelamento;
    }

    public TipoITBI getTipoITBI() {
        return tipoITBI;
    }

    public void setTipoITBI(TipoITBI tipoITBI) {
        this.tipoITBI = tipoITBI;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDiasVencimentoSegundaViaItbi() {
        return diasVencimentoSegundaViaItbi;
    }

    public void setDiasVencimentoSegundaViaItbi(int diasVencimentoSegundaViaItbi) {
        this.diasVencimentoSegundaViaItbi = diasVencimentoSegundaViaItbi;
    }

    public Tributo getTributoITBI() {
        return tributoITBI;
    }

    public void setTributoITBI(Tributo tributoITBI) {
        this.tributoITBI = tributoITBI;
    }

    public List<ParametrosITBIDocumento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<ParametrosITBIDocumento> documentos) {
        this.documentos = documentos;
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
        return "br.com.webpublico.entidades.Parametros[ id=" + id + " ]";
    }

    public ParametrosFuncionarios getPrimeiroFuncionarioVigente(Date dataOperacao) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            dataOperacao = sdf.parse(sdf.format(dataOperacao));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (ParametrosFuncionarios pf : getListaFuncionarios()) {
            if (pf.getVigenciaInicial().compareTo(dataOperacao) <= 0) {
                return pf;
            }
        }

        return null;
    }

    public ParametrosFuncionarios getFuncionario(Pessoa pessoa) {
        for (ParametrosFuncionarios pf : listaFuncionarios) {
            if (pf.getPessoa().equals(pessoa)) {
                return pf;
            }
        }

        return null;
    }

    public boolean faixaEstaContidaEmOutraFaixa(FaixaValorParcelamento faixa) {
        for (FaixaValorParcelamento fvp : listaDeFaixaValorParcelamento) {
            if (fvp.faixaEstaContida(faixa)) {
                return true;
            }
        }

        return false;
    }

    public boolean faixaContemOutraFaixa(FaixaValorParcelamento faixa) {
        for (FaixaValorParcelamento fvp : listaDeFaixaValorParcelamento) {
            if (faixa.faixaEstaContida(fvp)) {
                return true;
            }
        }

        return false;
    }

    public FaixaValorParcelamento getFaixaValorParcelamento(BigDecimal valor) {
        for (FaixaValorParcelamento fvp : listaDeFaixaValorParcelamento) {
            if (fvp.valorEstaContido(valor)) {
                return fvp;
            }
        }
        return null;
    }

    public List<ParametrosITBIDocumento> getDocumentosPorNatureza(ParametrosITBIDocumento.NaturezaDocumento naturezaDocumento) {
        return documentos
            .stream()
            .filter(d -> d.getAtivo() && d.getNaturezaDocumento().equals(naturezaDocumento))
            .collect(Collectors.toList());
    }
}
