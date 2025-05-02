/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributario")
@Etiqueta("Processo de Cancelamento de Parcelamento")
public class CancelamentoParcelamento extends Calculo implements Serializable {

    public static String NOME_USUARIO_PROCESSO_AUTOMATICO = "Realizado via processo automático";
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;
    private String motivo;
    @OneToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Processo de Parcelamento")
    private ProcessoParcelamento processoParcelamento;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data do Cancelamento")
    private Date dataCancelamento;
    @ManyToOne
    private ParcelaValorDivida parcelaValorDivida;
    @Etiqueta("Responsável pelo Cancelamento")
    @ManyToOne
    private UsuarioSistema usuario;
    @Tabelavel
    @Transient
    @Etiqueta("Responsável pelo Cancelamento")
    private String nomeUsuarioResponsavel;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "cancelamentoParcelamento")
    private List<ItemCancelamentoParcelamento> itens;
    @ManyToOne
    private ProcessoCalculoCancelamentoParcelamento processoCalculo;
    private String sequenciaParcela;
    private Long quantidadeParcela;
    private Boolean dividaAtiva;
    private Boolean dividaAtivaAjuizada;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date vencimento;
    @Etiqueta("Número Protocolo")
    @Tabelavel
    @Pesquisavel
    private String numeroProtocolo;
    @Etiqueta("Exercício Protocolo")
    @Tabelavel
    @Pesquisavel
    private String anoProtocolo;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "cancelamentoParcelamento")
    private List<ParcelasCancelamentoParcelamento> parcelas;
    @Etiqueta("Arquivos")
    @OneToMany(mappedBy = "cancelamentoParcelamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CancelamentoParcelamentoArquivo> arquivos;

    public CancelamentoParcelamento() {
        super.setTipoCalculo(TipoCalculo.CANCELAMENTO_PARCELAMENTO);
        this.itens = Lists.newArrayList();
        this.parcelas = Lists.newArrayList();
        arquivos = Lists.newArrayList();
    }

    public CancelamentoParcelamento(Long id, Exercicio exercicio, Long codigo, ProcessoParcelamento processoParcelamento, Date dataCancelamento, UsuarioSistema usuarioSistema, String numeroProtocolo, String anoProtocolo) {
        setId(id);
        this.exercicio = exercicio;
        this.codigo = codigo;
        this.processoParcelamento = processoParcelamento;
        this.dataCancelamento = dataCancelamento;
        if (usuarioSistema != null) {
            this.nomeUsuarioResponsavel = usuarioSistema.toString();
        } else {
            nomeUsuarioResponsavel = NOME_USUARIO_PROCESSO_AUTOMATICO;
        }
        this.numeroProtocolo = numeroProtocolo;
        this.anoProtocolo = anoProtocolo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Date getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(Date dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public ProcessoParcelamento getProcessoParcelamento() {
        return processoParcelamento;
    }

    public void setProcessoParcelamento(ProcessoParcelamento processoParcelamento) {
        this.processoParcelamento = processoParcelamento;
    }

    public ParcelaValorDivida getParcelaValorDivida() {
        return parcelaValorDivida;
    }

    public void setParcelaValorDivida(ParcelaValorDivida parcelaValorDivida) {
        this.parcelaValorDivida = parcelaValorDivida;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public List<ItemCancelamentoParcelamento> getItens() {
        return itens;
    }

    public void setItens(List<ItemCancelamentoParcelamento> itens) {
        this.itens = itens;
    }

    @Override
    public ProcessoCalculoCancelamentoParcelamento getProcessoCalculo() {
        return processoCalculo;
    }

    public void setProcessoCalculo(ProcessoCalculoCancelamentoParcelamento processoCalculo) {
        super.setProcessoCalculo(processoCalculo);
        this.processoCalculo = processoCalculo;
    }

    public String getSequenciaParcela() {
        return sequenciaParcela;
    }

    public void setSequenciaParcela(String sequenciaParcela) {
        this.sequenciaParcela = sequenciaParcela;
    }

    public Long getQuantidadeParcela() {
        return quantidadeParcela;
    }

    public void setQuantidadeParcela(Long quantidadeParcela) {
        this.quantidadeParcela = quantidadeParcela;
    }

    public Boolean getDividaAtiva() {
        return dividaAtiva;
    }

    public void setDividaAtiva(Boolean dividaAtiva) {
        this.dividaAtiva = dividaAtiva;
    }

    public Boolean getDividaAtivaAjuizada() {
        return dividaAtivaAjuizada;
    }

    public void setDividaAtivaAjuizada(Boolean dividaAtivaAjuizada) {
        this.dividaAtivaAjuizada = dividaAtivaAjuizada;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(String anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public List<ParcelasCancelamentoParcelamento> getParcelas() {
        if (parcelas == null) parcelas = Lists.newArrayList();
        return parcelas;
    }

    public void setParcelas(List<ParcelasCancelamentoParcelamento> parcelas) {
        this.parcelas = parcelas;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public List<CancelamentoParcelamentoArquivo> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<CancelamentoParcelamentoArquivo> arquivos) {
        this.arquivos = arquivos;
    }

    public String getNomeUsuarioResponsavel() {
        return nomeUsuarioResponsavel;
    }

    public void setNomeUsuarioResponsavel(String nomeUsuarioResponsavel) {
        this.nomeUsuarioResponsavel = nomeUsuarioResponsavel;
    }

    public List<ParcelasCancelamentoParcelamento> getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento tipoParcela) {
        List<ParcelasCancelamentoParcelamento> retorno = Lists.newArrayList();
        for (ParcelasCancelamentoParcelamento parcela : parcelas) {
            if (parcela.getTipoParcelaCancelamento().equals(tipoParcela)) {
                retorno.add(parcela);
            }
        }
        return retorno;
    }

    public ParcelasCancelamentoParcelamento getNovaParcelaAbertoComValorResidual() {
        List<Long> idsParcelasAbatidas = Lists.newArrayList();
        for (ParcelasCancelamentoParcelamento parcela : getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_ABATIDAS)) {
            idsParcelasAbatidas.add(parcela.getParcela().getId());
        }

        for (ParcelasCancelamentoParcelamento parcela : getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_EM_ABERTO)) {
            if (idsParcelasAbatidas.contains(parcela.getParcela().getId())) {
                return parcela;
            }
        }
        return null;
    }
}
