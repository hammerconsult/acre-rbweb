/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.tributario.consultadebitos.enums.TipoTributoDTO;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author munif
 */
@GrupoDiagrama(nome = "Tributario")
@Entity

@Audited
public class Tributo extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Código")
    @Pesquisavel
    private Long codigo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    @Obrigatorio
    private String descricao;
    @ManyToOne
    @Etiqueta("Entidade")
    @Obrigatorio
    private Entidade entidade;
    @Invisivel
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tributo", orphanRemoval = true)
    private List<ContaTributoReceita> contaTributoReceitas;
    private Boolean geraCreditoPagamento;
    @ManyToOne
    private Tributo tributoMulta;
    @ManyToOne
    private Tributo tributoJuros;
    @ManyToOne
    private Tributo tributoCorrecaoMonetaria;
    @ManyToOne
    private Tributo tributoHonorarios;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Tributo")
    private TipoTributo tipoTributo;
    @Transient
    private List<ContaTributoReceita> contasParaRemover;

    public Tributo() {
        super();
        this.dataRegistro = new Date();
        contaTributoReceitas = new ArrayList<ContaTributoReceita>();
    }

    public Tributo(Long id) {
        super();
        this.id = id;
    }

    public Tributo(Long id, Long codigo, String descricao, TipoTributo tipoTributo) {
        super();
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.tipoTributo = tipoTributo;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }


    public List<ContaTributoReceita> getContasParaRemover() {
        return contasParaRemover;
    }

    public void setContasParaRemover(List<ContaTributoReceita> contasParaRemover) {
        this.contasParaRemover = contasParaRemover;
    }

    public List<ContaTributoReceita> getContaTributoReceitas() {
        return contaTributoReceitas;
    }

    public void setContaTributoReceitas(List<ContaTributoReceita> contaTributoReceitas) {
        this.contaTributoReceitas = contaTributoReceitas;
    }

    public Boolean getGeraCreditoPagamento() {
        return geraCreditoPagamento;
    }

    public void setGeraCreditoPagamento(Boolean geraCreditoPagamento) {
        this.geraCreditoPagamento = geraCreditoPagamento;
    }

    public Tributo getTributoMulta() {
        return tributoMulta;
    }

    public void setTributoMulta(Tributo tributoMulta) {
        this.tributoMulta = tributoMulta;
    }

    public Tributo getTributoJuros() {
        return tributoJuros;
    }

    public void setTributoJuros(Tributo tributoJuros) {
        this.tributoJuros = tributoJuros;
    }

    public TipoTributo getTipoTributo() {
        return tipoTributo;
    }

    public void setTipoTributo(TipoTributo tipoTributo) {
        this.tipoTributo = tipoTributo;
    }

    public Tributo getTributoCorrecaoMonetaria() {
        return tributoCorrecaoMonetaria;
    }

    public void setTributoCorrecaoMonetaria(Tributo tributoCorrecaoMonetaria) {
        this.tributoCorrecaoMonetaria = tributoCorrecaoMonetaria;
    }

    public Tributo getTributoHonorarios() {
        return tributoHonorarios;
    }

    public void setTributoHonorarios(Tributo tributoHonorarios) {
        this.tributoHonorarios = tributoHonorarios;
    }

    @Override
    public String toString() {
        return this.codigo + " - " + this.descricao;
    }

    public Tributo getTributoPorTipo(TipoTributo tipoTributo) {
        switch (tipoTributo) {
            case JUROS:
                return getTributoJuros();
            case MULTA:
                return getTributoMulta();
            case CORRECAO:
                return getTributoCorrecaoMonetaria();
            case HONORARIOS:
                return getTributoHonorarios();
        }
        return null;
    }

    public static void validarTributoParaAdicaoEmLista(Tributo tributo, List<Tributo> tributos) {
        ValidacaoException ve = new ValidacaoException();
        if (tributo == null || tributo.getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um tributo para adicionar");
        } else if (tributos.contains(tributo)) {
            ve.adicionarMensagemDeCampoObrigatorio("Esse tributo já foi selecionado.");
        }
        ve.lancarException();
    }

    public enum TipoTributo implements EnumComDescricao {

        TAXA("Taxa", TipoTributoDTO.TAXA),
        IMPOSTO("Imposto", TipoTributoDTO.IMPOSTO),
        MULTA("Multa do Original", TipoTributoDTO.MULTA),
        JUROS("Juros do Original", TipoTributoDTO.JUROS),
        CORRECAO("Correção Monetária do Original", TipoTributoDTO.CORRECAO),
        HONORARIOS("Honorários do Original", TipoTributoDTO.HONORARIOS);

        private final String descricao;
        private final TipoTributoDTO toDto;

        TipoTributo(String descricao, TipoTributoDTO toDto) {
            this.descricao = descricao;
            this.toDto = toDto;
        }

        public TipoTributoDTO getToDto() {
            return toDto;
        }

        public static List<TipoTributo> getOriginais() {
            return Lists.newArrayList(TAXA, IMPOSTO);
        }

        public static List<TipoTributo> getAcrescimos() {
            return Lists.newArrayList(JUROS, MULTA, CORRECAO, HONORARIOS);
        }

        public static List<TipoTributo> getTipoImpostoTaxa() {
            return Lists.newArrayList(IMPOSTO, TAXA);
        }

        public String getDescricao() {
            return descricao;
        }

        public boolean isImpostoTaxa() {
            return isImposto() || isTaxa();
        }

        public boolean isImposto() {
            return this.equals(IMPOSTO);
        }

        public boolean isTaxa() {
            return this.equals(TAXA);
        }

        public boolean isAcrescimos() {
            return this.equals(MULTA) || this.equals(JUROS) || this.equals(CORRECAO) || this.equals(HONORARIOS);
        }

        public boolean isAcrescimosSemHonorarios() {
            return this.equals(MULTA) || this.equals(JUROS) || this.equals(CORRECAO);
        }

        public boolean isHonorarios() {
            return this.equals(HONORARIOS);
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public String toStringAutoComplete() {
        if (descricao != null) {
            return codigo + " - " + descricao + " - " + tipoTributo;
        }
        return "";
    }

    @Override
    public Tributo clone() throws CloneNotSupportedException {
        Tributo tributo = (Tributo) super.clone();
        tributo.setId(null);
        tributo.setContaTributoReceitas(Lists.newArrayList());
        for (ContaTributoReceita contaTributoReceita : this.getContaTributoReceitas()) {
            ContaTributoReceita contaTributoReceitaClone = contaTributoReceita.clone();
            contaTributoReceitaClone.setTributo(tributo);
            tributo.getContaTributoReceitas().add(contaTributoReceitaClone);
        }
        return tributo;
    }
}
