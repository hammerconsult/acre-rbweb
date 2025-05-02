/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoMovimentoMaterial;
import br.com.webpublico.enums.TipoSaidaMaterial;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.negocios.IntegradorMateriaisContabilFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Felipe Marinzeck
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Saída de Materiais")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class SaidaMaterial extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    private Long numero;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data da Saída")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private Date dataSaida;

    @OneToOne
    @Tabelavel
    @Etiqueta("Usuário")
    @Obrigatorio
    private UsuarioSistema usuario;

    @Tabelavel
    @Etiqueta("Retroativo")
    @Pesquisavel
    private Boolean retroativo;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Processado")
    private Boolean processado;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Retirado Por")
    private String retiradoPor;

    @ManyToOne
    @Etiqueta("Tipo de Baixa")
    @Obrigatorio
    private TipoBaixaBens tipoBaixaBens;

    @Etiqueta("Itens")
    @OneToMany(mappedBy = "saidaMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemSaidaMaterial> listaDeItemSaidaMaterial;

    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Histórico")
    private String historico;

    private String pin;

    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoMovimentoMaterial situacao;

    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data da Conclusão")
    private Date dataConclusao;

    @Version
    private Long versao;

    public SaidaMaterial() {
        super();
        this.retroativo = false;
        this.processado = false;
        this.listaDeItemSaidaMaterial = new ArrayList<>();
        this.dataSaida = new Date();
    }

    protected SaidaMaterial(Long id, Long numero, UsuarioSistema usuario, Date dataSaida, Boolean retroativo, Boolean processado, String retiradoPor) {
        super();
        this.id = id;
        this.numero = numero;
        this.usuario = usuario;
        this.dataSaida = dataSaida;
        this.retroativo = retroativo;
        this.processado = processado;
        this.listaDeItemSaidaMaterial = new ArrayList<>();
        this.retiradoPor = retiradoPor;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(Date dataSaida) {
        this.dataSaida = dataSaida;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public List<ItemSaidaMaterial> getListaDeItemSaidaMaterial() {
        return listaDeItemSaidaMaterial;
    }

    public void setListaDeItemSaidaMaterial(List<ItemSaidaMaterial> listaDeItemSaidaMaterial) {
        this.listaDeItemSaidaMaterial = listaDeItemSaidaMaterial;
    }

    public Boolean isRetroativo() {
        return retroativo;
    }

    public void setRetroativo(Boolean retroativo) {
        this.retroativo = retroativo;
    }

    public boolean isProcessado() {
        return processado;
    }

    public void setProcessado(boolean processado) {
        this.processado = processado;
    }

    public boolean isSaidaRequisicao() {
        return TipoSaidaMaterial.CONSUMO.equals(getTipoDestaSaida()) || TipoSaidaMaterial.TRANSFERENCIA.equals(getTipoDestaSaida());
    }

    public boolean isSaidaConsumo() {
        return TipoSaidaMaterial.CONSUMO.equals(getTipoDestaSaida());
    }

    public boolean isSaidaTransferencia() {
        return TipoSaidaMaterial.TRANSFERENCIA.equals(getTipoDestaSaida());
    }

    public boolean isSaidaDevolucao() {
        return TipoSaidaMaterial.DEVOLUCAO.equals(getTipoDestaSaida());
    }

    public boolean isSaidaProducao() {
        return TipoSaidaMaterial.PRODUCAO.equals(getTipoDestaSaida());
    }

    public boolean isSaidaInventario() {
        return TipoSaidaMaterial.INVENTARIO.equals(getTipoDestaSaida());
    }

    public boolean isSaidaDesincorporacao() {
        return TipoSaidaMaterial.DESINCORPORACAO.equals(getTipoDestaSaida());
    }

    public boolean isSaidaDireta() {
        return TipoSaidaMaterial.DIRETA.equals(getTipoDestaSaida());
    }

    public String getRetiradoPor() {
        return retiradoPor;
    }

    public void setRetiradoPor(String retiradoPor) {
        this.retiradoPor = retiradoPor;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public TipoBaixaBens getTipoBaixaBens() {
        return tipoBaixaBens;
    }

    public void setTipoBaixaBens(TipoBaixaBens tipoBaixaBens) {
        this.tipoBaixaBens = tipoBaixaBens;
    }

    public Boolean getRetroativo() {
        return retroativo;
    }

    public Boolean getProcessado() {
        return processado;
    }

    public void setProcessado(Boolean processado) {
        this.processado = processado;
    }

    public SituacaoMovimentoMaterial getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoMovimentoMaterial situacao) {
        this.situacao = situacao;
    }

    public Date getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(Date dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    @Override
    public String toString() {
        return "Saída nº: " + numero;
    }

    public String toStringAutoComplete() {
        try {
            return this.numero + " - " + DataUtil.getDataFormatada(this.dataSaida) + " - " + this.getUsuario() + " - " + getTipoDestaSaida();
        } catch (Exception e) {
            return this.numero + " - " + DataUtil.getDataFormatada(this.dataSaida);
        }
    }

    public abstract TipoSaidaMaterial getTipoDestaSaida();

    public Map<LocalEstoqueOrcamentario, Map<Material, BigDecimal>> getMapaLocalDeEstoqueMaterialQuantidadeConsolidada() {
        Map<LocalEstoqueOrcamentario, Map<Material, BigDecimal>> mapaLocalEstoque = new HashMap<>();

        for (ItemSaidaMaterial ism : listaDeItemSaidaMaterial) {
            Map<Material, BigDecimal> mapaMaterial = mapaLocalEstoque.get(ism.getLocalEstoqueOrcamentario());
            if (mapaMaterial == null) {
                mapaMaterial = new HashMap<>();
            }

            BigDecimal quantidade = mapaMaterial.get(ism.getMaterial());
            mapaMaterial.put(ism.getMaterial(), quantidade != null ? quantidade.add(ism.getQuantidade()) : ism.getQuantidade());

            mapaLocalEstoque.put(ism.getLocalEstoqueOrcamentario(), mapaMaterial);
        }

        return mapaLocalEstoque;
    }

    public Map<LocalEstoqueOrcamentario, Map<LoteMaterial, BigDecimal>> getMapaLocalDeEstoqueLoteMaterialQuantidadeConsolidada() {
        Map<LocalEstoqueOrcamentario, Map<LoteMaterial, BigDecimal>> mapaLocalEstoque = new HashMap<>();

        for (ItemSaidaMaterial ism : listaDeItemSaidaMaterial) {
            if (ism.requerLote()) {
                Map<LoteMaterial, BigDecimal> mapaLote = mapaLocalEstoque.get(ism.getLocalEstoqueOrcamentario());

                if (mapaLote == null) {
                    mapaLote = new HashMap<>();
                }
                BigDecimal quantidade = mapaLote.get(ism.getLoteMaterial());
                mapaLote.put(ism.getLoteMaterial(), quantidade != null ? quantidade.add(ism.getQuantidade()) : ism.getQuantidade());

                mapaLocalEstoque.put(ism.getLocalEstoqueOrcamentario(), mapaLote);
            }
        }
        return mapaLocalEstoque;
    }

    public String getHistoricoRazao(IntegradorMateriaisContabilFacade.AgrupadorItensSaida itemAgrupado, String descricaoSaida) {
        try {
            return descricaoSaida + " nº " + getNumero() + UtilBeanContabil.SEPARADOR_HISTORICO
                + getNumeroItensAgrupados(itemAgrupado) + UtilBeanContabil.SEPARADOR_HISTORICO
                + getHistorico();
        } catch (Exception ex) {
            return descricaoSaida;
        }
    }

    private String getNumeroItensAgrupados(IntegradorMateriaisContabilFacade.AgrupadorItensSaida itemAgrupado) {
        String historico = "Item: ";
        for (Object o : itemAgrupado.getItensSaida()) {
            ItemSaidaMaterial item = (ItemSaidaMaterial) o;
            historico += "nº " + item.getNumeroItem() + " - " + item.getMaterial().getCodigo() + ", ";
        }
        historico = historico.substring(0, historico.length() - 2);
        return historico;
    }
}
