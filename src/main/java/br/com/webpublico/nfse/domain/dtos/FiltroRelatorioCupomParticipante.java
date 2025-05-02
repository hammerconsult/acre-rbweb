package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.nfse.domain.SorteioNfse;
import br.com.webpublico.util.DataUtil;

import java.util.Date;

public class FiltroRelatorioCupomParticipante {

    private Date dataSorteio;
    private Date dataInicial;
    private Date dataFinal;
    private SorteioNfse sorteioNfse;
    private Pessoa pessoa;

    public FiltroRelatorioCupomParticipante() {
    }

    public String montarDescricaoFiltros() {
        StringBuilder retorno = new StringBuilder();
        if (dataSorteio != null) {
            retorno.append(" Data do Sorteio: ").append(DataUtil.getDataFormatada(dataSorteio)).append("; ");
        }
        if (dataInicial != null) {
            retorno.append(" Data Inicial: ").append(DataUtil.getDataFormatada(dataInicial)).append("; ");
        }
        if (dataFinal != null) {
            retorno.append(" Data Final: ").append(DataUtil.getDataFormatada(dataFinal)).append("; ");
        }
        if (sorteioNfse != null) {
            retorno.append(" Campanha: ").append(sorteioNfse.getDescricao()).append("; ");
        }
        if (pessoa != null) {
            retorno.append(" Pessoa: ").append(pessoa.getCpf_Cnpj()).append("; ");
        }
        return retorno.toString();
    }

    public String montarClausulaWhere() {
        StringBuilder sql = new StringBuilder();
        String clausula = " where ";
        if (dataSorteio != null) {
            sql.append(clausula).append(" premio.dataSorteio = to_date('").append(DataUtil.getDataFormatada(dataSorteio)).append("', 'dd/mm/yyyy')");
            clausula = " and ";
        }
        if (dataInicial != null) {
            sql.append(clausula).append(" campanha.inicio >= to_date('").append(DataUtil.getDataFormatada(dataInicial)).append("', 'dd/mm/yyyy')");
            clausula = " and ";
        }
        if (dataFinal != null) {
            sql.append(clausula).append(" campanha.fim <= to_date('").append(DataUtil.getDataFormatada(dataFinal)).append("', 'dd/mm/yyyy')");
            clausula = " and ";
        }
        if (sorteioNfse != null) {
            sql.append(clausula).append(" campanha.id = ").append(sorteioNfse.getId());
            clausula = " and ";
        }
        if (pessoa != null) {
            sql.append(clausula).append(" bilhete.pessoa_id = ").append(pessoa.getId());
        }
        return sql.toString();
    }

    public Date getDataSorteio() {
        return dataSorteio;
    }

    public void setDataSorteio(Date dataSorteio) {
        this.dataSorteio = dataSorteio;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public SorteioNfse getCampanhaNfse() {
        return sorteioNfse;
    }

    public void setCampanhaNfse(SorteioNfse sorteioNfse) {
        this.sorteioNfse = sorteioNfse;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }
}
