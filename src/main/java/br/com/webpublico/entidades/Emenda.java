/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.EsferaOrcamentaria;
import br.com.webpublico.enums.ModalidadeEmenda;
import br.com.webpublico.enums.StatusEmenda;
import br.com.webpublico.enums.TipoEmenda;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 15/06/15
 * Time: 17:39
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "PPA")
@Entity
@Etiqueta("Emenda")
@Audited
public class Emenda extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private String numero;
    @ManyToOne
    @Etiqueta("Exercício")
    @Pesquisavel
    @Tabelavel
    private Exercicio exercicio;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Data da Emenda")
    private Date dataEmenda;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Vereador")
    private Vereador vereador;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Modalidade da Emenda")
    @Enumerated(EnumType.STRING)
    private ModalidadeEmenda modalidadeEmenda;
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Esfera Orçamentária")
    private EsferaOrcamentaria esferaOrcamentaria;
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Objeto Resumido")
    @Length(maximo = 250)
    private String objetoResumido;
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Descrição Detalhada")
    @Length(maximo = 3000)
    private String descricaoDetalhada;
    @Etiqueta("Status da Câmara")
    @Enumerated(EnumType.STRING)
    private StatusEmenda statusCamara;
    @Etiqueta("Status da Prefeitura")
    @Enumerated(EnumType.STRING)
    private StatusEmenda statusPrefeitura;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "emenda", orphanRemoval = true)
    private List<BeneficiarioEmenda> beneficiarioEmendas;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "emenda", orphanRemoval = true)
    private List<SuplementacaoEmenda> suplementacaoEmendas;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "emenda", orphanRemoval = true)
    private List<AnulacaoEmenda> anulacaoEmendas;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "emenda", orphanRemoval = true)
    private List<HistoricoEmenda> historicos;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "emenda", orphanRemoval = true)
    private List<HistoricoAlteracaoEmenda> historicosAlteracoes;
    @Enumerated(EnumType.STRING)
    private TipoEmenda tipoEmenda;

    public Emenda() {
        super();
        beneficiarioEmendas = Lists.newArrayList();
        suplementacaoEmendas = Lists.newArrayList();
        anulacaoEmendas = Lists.newArrayList();
        historicos = Lists.newArrayList();
        historicosAlteracoes = Lists.newArrayList();
        statusCamara = StatusEmenda.ABERTO;
        statusPrefeitura = StatusEmenda.ABERTO;
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

    public Vereador getVereador() {
        return vereador;
    }

    public void setVereador(Vereador vereador) {
        this.vereador = vereador;
    }

    public ModalidadeEmenda getModalidadeEmenda() {
        return modalidadeEmenda;
    }

    public void setModalidadeEmenda(ModalidadeEmenda modalidadeEmenda) {
        this.modalidadeEmenda = modalidadeEmenda;
    }

    public EsferaOrcamentaria getEsferaOrcamentaria() {
        return esferaOrcamentaria;
    }

    public void setEsferaOrcamentaria(EsferaOrcamentaria esferaOrcamentaria) {
        this.esferaOrcamentaria = esferaOrcamentaria;
    }

    public String getDescricaoDetalhada() {
        return descricaoDetalhada;
    }

    public void setDescricaoDetalhada(String justificativa) {
        this.descricaoDetalhada = justificativa;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public List<BeneficiarioEmenda> getBeneficiarioEmendas() {
        return beneficiarioEmendas;
    }

    public void setBeneficiarioEmendas(List<BeneficiarioEmenda> beneficiarioEmendas) {
        this.beneficiarioEmendas = beneficiarioEmendas;
    }

    public List<SuplementacaoEmenda> getSuplementacaoEmendas() {
        return suplementacaoEmendas;
    }

    public void setSuplementacaoEmendas(List<SuplementacaoEmenda> suplementacaoEmendas) {
        this.suplementacaoEmendas = suplementacaoEmendas;
    }

    public BigDecimal getTotalEmenda() {
        BigDecimal total = BigDecimal.ZERO;
        for (SuplementacaoEmenda suplementacaoEmenda : suplementacaoEmendas) {
            total = total.add(suplementacaoEmenda.getValor());
        }
        return total;
    }

    public List<AnulacaoEmenda> getAnulacaoEmendas() {
        return anulacaoEmendas;
    }

    public void setAnulacaoEmendas(List<AnulacaoEmenda> anulacaoEmendas) {
        this.anulacaoEmendas = anulacaoEmendas;
    }

    public Date getDataEmenda() {
        return dataEmenda;
    }

    public void setDataEmenda(Date dataEmenda) {
        this.dataEmenda = dataEmenda;
    }

    public StatusEmenda getStatusCamara() {
        return statusCamara;
    }

    public void setStatusCamara(StatusEmenda statusCamara) {
        this.statusCamara = statusCamara;
    }

    public StatusEmenda getStatusPrefeitura() {
        return statusPrefeitura;
    }

    public void setStatusPrefeitura(StatusEmenda statusPrefeitura) {
        this.statusPrefeitura = statusPrefeitura;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getObjetoResumido() {
        return objetoResumido;
    }

    public void setObjetoResumido(String objetoResumido) {
        this.objetoResumido = objetoResumido;
    }

    public List<HistoricoEmenda> getHistoricosOrdenados() {
        Collections.sort(historicos, new Comparator<HistoricoEmenda>() {
            @Override
            public int compare(HistoricoEmenda o1, HistoricoEmenda o2) {
                return o1.getDataAlteracao().compareTo(o2.getDataAlteracao());
            }
        });
        return historicos;
    }

    public List<HistoricoEmenda> getHistoricos() {
        return historicos;
    }

    public void setHistoricos(List<HistoricoEmenda> historicos) {
        this.historicos = historicos;
    }

    public List<HistoricoAlteracaoEmenda> getHistoricosAlteracoes() {
        return historicosAlteracoes;
    }

    public void setHistoricosAlteracoes(List<HistoricoAlteracaoEmenda> historicosAlteracoes) {
        this.historicosAlteracoes = historicosAlteracoes;
    }

    public List<HistoricoAlteracaoEmenda> getHistoricosAlteracoesOrdenados() {
        Collections.sort(historicosAlteracoes, new Comparator<HistoricoAlteracaoEmenda>() {
            @Override
            public int compare(HistoricoAlteracaoEmenda o1, HistoricoAlteracaoEmenda o2) {
                return o1.getDataAlteracao().compareTo(o2.getDataAlteracao());
            }
        });
        return historicosAlteracoes;
    }

    @Override
    public String toString() {
        String retorno = numero != null ? numero + " - " : "";
        if (dataEmenda != null) {
            retorno += DataUtil.getDataFormatada(dataEmenda) + " - ";
        }
        if (vereador != null) {
            retorno += vereador.getPessoa().getNome();
        }
        return retorno;
    }

    public String toStringAutoComplete() {
        int tamanho = 70;
        String retorno = numero != null ? numero + " - " : "";
        if (dataEmenda != null) {
            retorno += DataUtil.getDataFormatada(dataEmenda) + " - ";
        }
        if (vereador != null) {
            retorno += vereador.getPessoa().getNome() + " - ";
        }
        if (descricaoDetalhada != null) {
            String desc = descricaoDetalhada.replace("\n", " ").replace("\r", " ");
            retorno += (desc.length() > tamanho ? desc.substring(0, tamanho) + "..." : desc);
        }
        //todo no pré ainda não esta recuperando no autocomplete, adicionado sout para conseguir identificar qual o problema, sera removido posteriormente
        System.out.println("toStringAutoComplete EMENDA: " + retorno);
        return retorno;
    }

    public TipoEmenda getTipoEmenda() {
        return tipoEmenda;
    }

    public void setTipoEmenda(TipoEmenda tipoEmenda) {
        this.tipoEmenda = tipoEmenda;
    }
}
