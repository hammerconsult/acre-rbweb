package br.com.webpublico.entidades.rh.portal.atualizacaocadastral;

import br.com.webpublico.entidades.ConselhoClasseOrdem;
import br.com.webpublico.entidades.UF;
import br.com.webpublico.pessoa.dto.ConselhoClasseOrdemDTO;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by peixe on 29/08/17.
 */
@Entity
public class ConselhoClasseOrdemPortal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Long id;
    @ManyToOne
    private PessoaFisicaPortal pessoaFisicaPortal;
    private String numeroDocumento;
    private Date dataEmissao;
    @ManyToOne
    private UF uf;
    @ManyToOne
    private ConselhoClasseOrdem conselhoClasseOrdem;
    private Date dateRegistro;

    public ConselhoClasseOrdemPortal() {
    }

    public PessoaFisicaPortal getPessoaFisicaPortal() {
        return pessoaFisicaPortal;
    }

    public void setPessoaFisicaPortal(PessoaFisicaPortal pessoaFisicaPortal) {
        this.pessoaFisicaPortal = pessoaFisicaPortal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public UF getUf() {
        return uf;
    }

    public void setUf(UF uf) {
        this.uf = uf;
    }

    public ConselhoClasseOrdem getConselhoClasseOrdem() {
        return conselhoClasseOrdem;
    }

    public void setConselhoClasseOrdem(ConselhoClasseOrdem conselhoClasseOrdem) {
        this.conselhoClasseOrdem = conselhoClasseOrdem;
    }

    public Date getDateRegistro() {
        return dateRegistro;
    }

    public void setDateRegistro(Date dateRegistro) {
        this.dateRegistro = dateRegistro;
    }

    public static List<ConselhoClasseOrdemPortal> dtoToConselhos(List<ConselhoClasseOrdemDTO> dtos, PessoaFisicaPortal pessoa) {
        List<ConselhoClasseOrdemPortal> conselhos = Lists.newLinkedList();
        for (ConselhoClasseOrdemDTO dto : dtos) {
            ConselhoClasseOrdemPortal f = dtoToConselhoClasseOrdemPortal(dto, pessoa);
            if (f != null) {
                conselhos.add(f);
            }
        }
        return conselhos;
    }

    private static ConselhoClasseOrdemPortal dtoToConselhoClasseOrdemPortal(ConselhoClasseOrdemDTO dto, PessoaFisicaPortal pessoa) {
        ConselhoClasseOrdemPortal c = new ConselhoClasseOrdemPortal();
        c.setConselhoClasseOrdem(ConselhoClasseOrdem.dtoToConselhoClasseOrdem(dto.getConselhoClasse()));
        c.setDataEmissao(dto.getDataEmissaoConselho());
        c.setDateRegistro(new Date());
        c.setNumeroDocumento(dto.getNumeroDocumento());
        c.setPessoaFisicaPortal(pessoa);
        c.setUf(UF.dtoToUF(dto.getUfConselho()));
        return c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConselhoClasseOrdemPortal that = (ConselhoClasseOrdemPortal) o;
        return Objects.equals(numeroDocumento != null ? numeroDocumento.trim() : null, that.numeroDocumento != null ? that.numeroDocumento.trim() : null) &&
            Objects.equals(DataUtil.getDataFormatada(dataEmissao), DataUtil.getDataFormatada(that.dataEmissao)) &&
            Objects.equals(uf, that.uf) &&
            Objects.equals(conselhoClasseOrdem, that.conselhoClasseOrdem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroDocumento, dataEmissao, uf, conselhoClasseOrdem);
    }
}
