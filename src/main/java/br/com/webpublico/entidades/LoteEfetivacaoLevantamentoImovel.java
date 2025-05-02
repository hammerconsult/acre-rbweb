package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Desenvolvimento on 10/11/2015.
 */
@Entity
@Audited
@Etiqueta("Efetivação de Levantamento Imóvel")
@Table(name = "LOTEEFETLEVIMOVEL")
public class LoteEfetivacaoLevantamentoImovel extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código da Efetivação")
    private Long codigo;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Efetivação")
    @Temporal(TemporalType.DATE)
    private Date dataEfetivacao;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Efetivador")
    @ManyToOne
    private UsuarioSistema usuarioSistema;

    @Etiqueta("Unidade Administrativa")
    @ManyToOne
    private UnidadeOrganizacional unidadeAdministrativa;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Unidade Orçamentária")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrcamentaria;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @OneToMany(mappedBy = "loteEfetivacaoImovel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EfetivacaoLevantamentoImovel> itensEfetivacaoImovel;

    public LoteEfetivacaoLevantamentoImovel() {
        super();
        this.itensEfetivacaoImovel = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnidadeOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(UnidadeOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(UnidadeOrganizacional unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public List<EfetivacaoLevantamentoImovel> getItensEfetivacaoImovel() {
        return itensEfetivacaoImovel;
    }

    public void setItensEfetivacaoImovel(List<EfetivacaoLevantamentoImovel> itensEfetivacaoImovel) {
        this.itensEfetivacaoImovel = itensEfetivacaoImovel;
    }

    public Date getDataEfetivacao() {
        return dataEfetivacao;
    }

    public void setDataEfetivacao(Date dataEfetivacao) {
        this.dataEfetivacao = dataEfetivacao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public String totalLevantamento() {
        BigDecimal total = BigDecimal.ZERO;
        for (EfetivacaoLevantamentoImovel item : itensEfetivacaoImovel) {
            total = total.add(item.getValorDoLancamento());
        }
        return Util.formataValor(total);
    }
}
