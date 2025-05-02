package br.com.webpublico.entidades;

import br.com.webpublico.enums.administrativo.frotas.TipoCotaAbastecimento;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 30/09/14
 * Time: 08:40
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Cotas para Abastecimentos")
public class CotaAbastecimento extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número da Cota")
    @Transient
    private String numeroCotaFormatado;

    private Integer anoCota;
    private Integer numeroCota;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo de Cota")
    @Enumerated(EnumType.STRING)
    private TipoCotaAbastecimento tipoCotaAbastecimento;

    @Etiqueta("Unidade Gestora")
    @Tabelavel
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacionalGestora;

    @Pesquisavel
    @Etiqueta("Unidade Gestora")
    @ManyToOne
    private UnidadeOrganizacional unidadeGestora;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Contrato")
    @ManyToOne
    private Contrato contrato;

    @Tabelavel
    @Etiqueta("Unidade Cotista")
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacionalCotista;

    @Pesquisavel
    @Etiqueta("Unidade Cotista")
    @ManyToOne
    private UnidadeOrganizacional unidadeCotista;

    @Tabelavel
    @Etiqueta("Unidade Filha")
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacionalFilha;

    @Pesquisavel
    @Etiqueta("Unidade Filha")
    @ManyToOne
    private UnidadeOrganizacional unidadeFilha;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "cotaAbastecimento")
    private List<ItemCotaAbastecimento> itensCotaAbastecimento;

    @OneToOne
    private CotaAbastecimento cotaAbastecimento;


    public CotaAbastecimento() {
        this.setItensCotaAbastecimento(new ArrayList());
    }

    public CotaAbastecimento(CotaAbastecimento cotaAbastecimento,
                             HierarquiaOrganizacional hierarquiaOrganizacionalGestora,
                             HierarquiaOrganizacional hierarquiaOrganizacionalCotista,
                             HierarquiaOrganizacional hierarquiaOrganizacionalFilha) {
        this.setId(cotaAbastecimento.getId());
        this.setAnoCota(cotaAbastecimento.getAnoCota());
        this.setNumeroCota(cotaAbastecimento.getNumeroCota());
        this.setDescricao(cotaAbastecimento.getDescricao());
        this.setContrato(cotaAbastecimento.getContrato());
        this.setHierarquiaOrganizacionalGestora(hierarquiaOrganizacionalGestora);
        this.setHierarquiaOrganizacionalCotista(hierarquiaOrganizacionalCotista);
        this.setNumeroCota(cotaAbastecimento.getNumeroCota());
        this.setNumeroCotaFormatado(cotaAbastecimento.getNumeroCotaFormatado());
        this.setHierarquiaOrganizacionalFilha(hierarquiaOrganizacionalFilha);
        this.setTipoCotaAbastecimento(cotaAbastecimento.getTipoCotaAbastecimento());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAnoCota() {
        return anoCota;
    }

    public void setAnoCota(Integer anoCota) {
        this.anoCota = anoCota;
    }

    public Integer getNumeroCota() {
        return numeroCota;
    }

    public void setNumeroCota(Integer numeroCota) {
        this.numeroCota = numeroCota;
    }

    public UnidadeOrganizacional getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeOrganizacional unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public UnidadeOrganizacional getUnidadeCotista() {
        return unidadeCotista;
    }

    public void setUnidadeCotista(UnidadeOrganizacional unidadeCotista) {
        this.unidadeCotista = unidadeCotista;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalGestora() {
        return hierarquiaOrganizacionalGestora;
    }

    public void setHierarquiaOrganizacionalGestora(HierarquiaOrganizacional hierarquiaOrganizacionalGestora) {
        if (hierarquiaOrganizacionalGestora != null) {
            this.unidadeGestora = hierarquiaOrganizacionalGestora.getSubordinada();
        }
        this.hierarquiaOrganizacionalGestora = hierarquiaOrganizacionalGestora;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalCotista() {
        return hierarquiaOrganizacionalCotista;
    }

    public void setHierarquiaOrganizacionalCotista(HierarquiaOrganizacional hierarquiaOrganizacionalCotista) {
        if (hierarquiaOrganizacionalCotista != null) {
            this.unidadeCotista = hierarquiaOrganizacionalCotista.getSubordinada();
        }
        this.hierarquiaOrganizacionalCotista = hierarquiaOrganizacionalCotista;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalFilha() {
        return hierarquiaOrganizacionalFilha;
    }

    public void setHierarquiaOrganizacionalFilha(HierarquiaOrganizacional hierarquiaOrganizacionalFilha) {
        if (hierarquiaOrganizacionalFilha != null) {
            this.unidadeFilha = hierarquiaOrganizacionalFilha.getSubordinada();
        }
        this.hierarquiaOrganizacionalFilha = hierarquiaOrganizacionalFilha;
    }

    public UnidadeOrganizacional getUnidadeFilha() {
        return unidadeFilha;
    }

    public void setUnidadeFilha(UnidadeOrganizacional unidadeFilha) {
        this.unidadeFilha = unidadeFilha;
    }

    public List<ItemCotaAbastecimento> getItensCotaAbastecimento() {
        return itensCotaAbastecimento;
    }

    public void setItensCotaAbastecimento(List<ItemCotaAbastecimento> itensCotaAbastecimento) {
        this.itensCotaAbastecimento = itensCotaAbastecimento;
    }

    public String getNumeroCotaFormatado() {
        if (id == null) {
            numeroCotaFormatado = "Gerado automaticamente ao salvar.";
        } else {
            numeroCotaFormatado = numeroCota.toString() + "/" + anoCota;
        }
        return numeroCotaFormatado;
    }

    public void setNumeroCotaFormatado(String numeroCotaFormatado) {
        this.numeroCotaFormatado = numeroCotaFormatado;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoCotaAbastecimento getTipoCotaAbastecimento() {
        return tipoCotaAbastecimento;
    }

    public void setTipoCotaAbastecimento(TipoCotaAbastecimento tipoCotaAbastecimento) {
        this.tipoCotaAbastecimento = tipoCotaAbastecimento;
    }

    public CotaAbastecimento getCotaAbastecimento() {
        return cotaAbastecimento;
    }

    public void setCotaAbastecimento(CotaAbastecimento cotaAbastecimento) {
        this.cotaAbastecimento = cotaAbastecimento;
    }

    public boolean isNormal() {
        return TipoCotaAbastecimento.NORMAL.equals(tipoCotaAbastecimento);
    }

    public boolean isDistribuicao() {
        return TipoCotaAbastecimento.DISTRIBUICAO.equals(tipoCotaAbastecimento);
    }

    @Override
    public String toString() {
        return numeroCota.toString() + "/" + anoCota + " - " + descricao;
    }
}
