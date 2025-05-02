package br.com.webpublico.entidades;

import br.com.webpublico.enums.rh.esocial.ProvimentoEstatutarioEsocial;
import br.com.webpublico.enums.rh.esocial.TipoPrazoContrato;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Etiqueta("Modalidade de Contrato")
public class ModalidadeContratoFP implements Serializable, Comparable<Object> {

    public static final Long CONTRATO_TEMPORARIO = 4l;
    public static final Long CONCURSADOS = 1l;
    public static final Long PRESTADOR_SERVICO = 8l;
    public static final Long CONSELHEIRO_TUTELAR = 6l;
    public static final Long CONTRATO_TRABALHO_TEMPO_INDETERMINADO = 8l;
    public static final Long CARGO_COMISSAO = 2l;
    public static final Long AGENTE_POLITICO = 3l;
    public static final Long CARGO_ELETIVO = 7l;
    public static final Long FUNCAO_COORDENACAO = 9l;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Código")
    @Tabelavel
    private Long codigo;
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Descrição")
    @Tabelavel
    private String descricao;
    @OneToMany(mappedBy = "modalidadeContratoFP", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegraDeducaoDDF> regrasDeducoesDDFs;
    @OneToMany(mappedBy = "modalidadeContratoFP", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegraModalidadeTipoAfast> regrasModalidadesTiposAfasts;
    private Boolean inativo;
    @Etiqueta("Código Vínculo Sicap")
    @Pesquisavel
    @Tabelavel
    private Integer codigoVinculoSicap;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Prazo do Contrato")
    @Tabelavel
    private TipoPrazoContrato tipoPrazoContrato;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Provimento (ESOCIAL)")
    private ProvimentoEstatutarioEsocial provimentoEstatutarioEsocial;

    private String codigoSig;

    public ModalidadeContratoFP(Long id, Long codigo, String descricao) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public ModalidadeContratoFP(Long id) {
        this.id = id;
    }

    public ModalidadeContratoFP() {
    }

    public ModalidadeContratoFP(String descricao, EventoFP vantagemVencimentoBase) {
        this.descricao = descricao;
    }

    public Integer getCodigoVinculoSicap() {
        return codigoVinculoSicap;
    }

    public void setCodigoVinculoSicap(Integer codigoVinculoSicap) {
        this.codigoVinculoSicap = codigoVinculoSicap;
    }

    public TipoPrazoContrato getTipoPrazoContrato() {
        return tipoPrazoContrato;
    }

    public void setTipoPrazoContrato(TipoPrazoContrato tipoPrazoContrato) {
        this.tipoPrazoContrato = tipoPrazoContrato;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }


    public List<RegraDeducaoDDF> getRegrasDeducoesDDFs() {
        return regrasDeducoesDDFs;
    }

    public void setRegrasDeducoesDDFs(List<RegraDeducaoDDF> regrasDedudocoesDDFs) {
        regrasDeducoesDDFs = regrasDedudocoesDDFs;
    }

    public List<RegraModalidadeTipoAfast> getRegrasModalidadesTiposAfasts() {
        return regrasModalidadesTiposAfasts;
    }

    public void setRegrasModalidadesTiposAfasts(List<RegraModalidadeTipoAfast> regrasModalidadesTiposAfasts) {
        this.regrasModalidadesTiposAfasts = regrasModalidadesTiposAfasts;
    }

    public Boolean getInativo() {
        return inativo;
    }

    public void setInativo(Boolean inativo) {
        this.inativo = inativo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ModalidadeContratoFP other = (ModalidadeContratoFP) obj;
        if (id != other.id && (id == null || !id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public String getCodigoSig() {
        return codigoSig;
    }

    public void setCodigoSig(String codigoSig) {
        this.codigoSig = codigoSig;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return descricao;
    }

    @Override
    public int compareTo(Object o) {
        return codigo.compareTo(((ModalidadeBeneficioFP) o).getCodigo());
    }

    public ProvimentoEstatutarioEsocial getProvimentoEstatutarioEsocial() {
        return provimentoEstatutarioEsocial;
    }

    public void setProvimentoEstatutarioEsocial(ProvimentoEstatutarioEsocial provimentoEstatutarioEsocial) {
        this.provimentoEstatutarioEsocial = provimentoEstatutarioEsocial;
    }

    public String getDescricaoModalideParaEConsig() {
        Integer codigo = getCodigo().intValue();
        switch (codigo) {
            case 1:
                return "CARREIRA";
            case 2:
                return "COMISSÃO";
            case 3:
                return "COMISSÃO";
            case 4:
                return "CT ATIVO";
            case 6:
                return "COMISSÃO";
            case 7:
                return "ELETIVO";
            default:
                return "NÃO DEFINIDO";
        }

    }
}
