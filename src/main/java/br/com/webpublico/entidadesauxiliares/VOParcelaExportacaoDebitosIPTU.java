package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

import java.io.Serializable;
import java.util.Comparator;

public class VOParcelaExportacaoDebitosIPTU implements Serializable {
    private ResultadoParcela parcela;
    private VODam dam;
    private VOCadastroImobiliario cadastroImobiliario;
    private VOPessoa pessoa;

    public VOParcelaExportacaoDebitosIPTU() {
    }

    public ResultadoParcela getParcela() {
        return parcela;
    }

    public void setParcela(ResultadoParcela parcela) {
        this.parcela = parcela;
    }

    public VODam getDam() {
        return dam;
    }

    public void setDam(VODam dam) {
        this.dam = dam;
    }

    public VOCadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(VOCadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public VOPessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(VOPessoa pessoa) {
        this.pessoa = pessoa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VOParcelaExportacaoDebitosIPTU that = (VOParcelaExportacaoDebitosIPTU) o;
        return Objects.equal(parcela, that.parcela);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parcela);
    }

    public static Comparator<VOParcelaExportacaoDebitosIPTU> comparator() {
        return new Comparator<VOParcelaExportacaoDebitosIPTU>() {
            @Override
            public int compare(VOParcelaExportacaoDebitosIPTU o1, VOParcelaExportacaoDebitosIPTU o2) {
                ResultadoParcela parcela1 = o1.getParcela();
                ResultadoParcela parcela2 = o2.getParcela();
                Ordering<Comparable<?>> naturalOrder = Ordering.natural().nullsLast();
                return ComparisonChain.start()
                    .compare(parcela1.getOrdemApresentacao(), parcela2.getOrdemApresentacao(), naturalOrder)
                    .compare(parcela1.getDivida(), parcela2.getDivida(), naturalOrder)
                    .compare(parcela1.getExercicio(), parcela2.getExercicio(), naturalOrder)
                    .compare(parcela1.getReferencia(), parcela2.getReferencia(), naturalOrder)
                    .compare(parcela1.getSd(), parcela2.getSd(), naturalOrder)
                    .compare(parcela1.getCotaUnica(), parcela2.getCotaUnica(), naturalOrder)
                    .compare(parcela1.getVencimento(), parcela2.getVencimento(), naturalOrder)
                    .compare(parcela1.getParcela(), parcela2.getParcela(), naturalOrder)
                    .compare(parcela1.getSituacao(), parcela2.getSituacao(), naturalOrder)
                    .result();
            }
        };
    }
}
