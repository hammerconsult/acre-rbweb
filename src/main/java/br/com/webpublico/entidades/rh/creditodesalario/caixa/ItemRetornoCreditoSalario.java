package br.com.webpublico.entidades.rh.creditodesalario.caixa;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.enums.TipoRegistroCnab240;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "RH")
@Etiqueta("Itens do Retorno do Crédito de Salario")
public class ItemRetornoCreditoSalario extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String linhaArquivo;

    @Enumerated(EnumType.STRING)
    private TipoRegistroCnab240 tipoRegistro;

    @ManyToOne
    private VinculoFP vinculoFP;

    @OneToMany(mappedBy = "itemRetornoCreditoSalario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RetornoCaixaOcorrencias> retornoCaixaOcorrencias;

    @ManyToOne
    private RetornoCreditoSalario retornoCreditoSalario;

    @Transient
    private Integer indice;

    private String identificadorFicha;

    private BigDecimal valorLiquido;

    private Integer loteServico;

    public ItemRetornoCreditoSalario() {
        retornoCaixaOcorrencias = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLinhaArquivo() {
        return linhaArquivo;
    }

    public void setLinhaArquivo(String linhaArquivo) {
        this.linhaArquivo = linhaArquivo;
    }

    public TipoRegistroCnab240 getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(TipoRegistroCnab240 tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }


    public List<RetornoCaixaOcorrencias> getRetornoCaixaOcorrencias() {
        return retornoCaixaOcorrencias;
    }

    public void setRetornoCaixaOcorrencias(List<RetornoCaixaOcorrencias> retornoCaixaOcorrencias) {
        this.retornoCaixaOcorrencias = retornoCaixaOcorrencias;
    }

    public RetornoCreditoSalario getRetornoCreditoSalario() {
        return retornoCreditoSalario;
    }

    public void setRetornoCreditoSalario(RetornoCreditoSalario arquivoRetornoCreditoSalario) {
        this.retornoCreditoSalario = arquivoRetornoCreditoSalario;
    }

    public Integer getIndice() {
        return indice;
    }

    public void setIndice(Integer indice) {
        this.indice = indice;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public String getIdentificadorFicha() {
        return identificadorFicha;
    }

    public void setIdentificadorFicha(String identificadorFicha) {
        this.identificadorFicha = identificadorFicha;
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public Integer getLoteServico() {
        return loteServico;
    }

    public void setLoteServico(Integer loteServico) {
        this.loteServico = loteServico;
    }

    public TipoRegistroCnab240 getTipoRegistroCnab240(String line) {
        String tipoRegistroArquivo = line.substring(7, 8);
        if ("3".equals(tipoRegistroArquivo)) {
            String tipoSegmento = line.substring(13, 14);
            tipoRegistroArquivo += tipoSegmento;
        }
        return TipoRegistroCnab240.getTipoPorCodigo(tipoRegistroArquivo);
    }

    public String getCPFCnb240(String line, TipoRegistroCnab240 tipoRegistroCnab240) {
        if ("3B".equals(tipoRegistroCnab240.getCodigo())) {
            return line.substring(21, 32);
        }
        return null;
    }

    public String getIdentificadorFichaFinanceira(String line, TipoRegistroCnab240 tipoRegistroCnab240) {
        if ("3A".equals(tipoRegistroCnab240.getCodigo())) {
            return line.substring(73, 79);
        }
        return null;
    }

    public String getIdentificadorLote(String line) {
        return line.substring(191, 211);
    }

    public String getSequencial(String line, TipoRegistroCnab240 tipoRegistroCnab240) {
        if ("0".equals(tipoRegistroCnab240.getCodigo())) {
            return line.substring(19, 32);
        }
        return null;
    }

    public String getNumeroConvenio(String line, TipoRegistroCnab240 tipoRegistroCnab240) {
        if ("0".equals(tipoRegistroCnab240.getCodigo())) {
            return line.substring(32, 38);
        }
        return null;
    }

    public String getMatricula(String line, TipoRegistroCnab240 tipoRegistroCnab240) {
        if ("3A".equals(tipoRegistroCnab240.getCodigo())) {
            return line.substring(73, 79);
        }
        return null;
    }

    public String getNomeVinculo() {
        if ("3A".equals(this.tipoRegistro.getCodigo())) {
            return this.linhaArquivo.substring(43, 73);
        }
        return null;
    }

    public String getOcorrencias(String line) {
        return line.substring(230, 240);
    }

    public String getValor(String line) {
        return line.substring(119, 134);
    }

    public String getLoteServico(String line) {
        return line.substring(3, 7);
    }
}
