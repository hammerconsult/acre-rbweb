package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.nfse.enums.TipoCupomCampanhaNfse;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.beust.jcommander.internal.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "NFSE")
@Etiqueta("Campanha Nota Premiada")
public class CampanhaNfse extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String descricao;
    @Obrigatorio
    @Etiqueta("Início do Sorteio")
    @Temporal(TemporalType.DATE)
    private Date inicio;
    @Obrigatorio
    @Etiqueta("Fim do Sorteio")
    @Temporal(TemporalType.DATE)
    private Date fim;
    @Obrigatorio
    @Etiqueta("Tipo de Cupom")
    @Enumerated(EnumType.STRING)
    private TipoCupomCampanhaNfse tipoCupom;
    @Etiqueta("Valor por Cupom")
    @Tabelavel
    private BigDecimal valorPorCupom;
    @OneToMany(mappedBy = "campanha", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BloqueioCpfCampanhaNfse> cpfsBloqueados;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public CampanhaNfse() {
        super();
        tipoCupom = TipoCupomCampanhaNfse.POR_NOTA;
        cpfsBloqueados = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public TipoCupomCampanhaNfse getTipoCupom() {
        return tipoCupom;
    }

    public void setTipoCupom(TipoCupomCampanhaNfse tipoCupom) {
        this.tipoCupom = tipoCupom;
    }

    public BigDecimal getValorPorCupom() {
        return valorPorCupom;
    }

    public void setValorPorCupom(BigDecimal valorPorCupom) {
        this.valorPorCupom = valorPorCupom;
    }

    public List<BloqueioCpfCampanhaNfse> getCpfsBloqueados() {
        return cpfsBloqueados;
    }

    public void setCpfsBloqueados(List<BloqueioCpfCampanhaNfse> cpfsBloqueados) {
        this.cpfsBloqueados = cpfsBloqueados;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public boolean hasParticipanteBloqueado(String cpfCnpj) {
        if (cpfsBloqueados != null && !cpfsBloqueados.isEmpty()) {
            for (BloqueioCpfCampanhaNfse cpfBloqueado : cpfsBloqueados) {
                if (cpfBloqueado.getPessoaFisica().getCpf().equals(StringUtil.retornaApenasNumeros(cpfCnpj)))
                    return true;
            }
        }
        return false;
    }

    @Override
    public void realizarValidacoes() {
        super.realizarValidacoes();
        if (tipoCupom != null && tipoCupom.isPorValor()) {
            if (valorPorCupom == null || valorPorCupom.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidacaoException("O campo 'Bilhete a cada (R$)' deve ser informado com valor superior a 0(zero).");
            }
        }
    }

    @Override
    public String toString() {
        return descricao;
    }
}
