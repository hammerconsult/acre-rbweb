package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.entidades.Servico;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.webreportdto.dto.comum.OperacaoRelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.ParametrosRelatoriosDTO;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.beust.jcommander.internal.Lists;
import org.apache.logging.log4j.util.Strings;

import java.util.Date;
import java.util.List;

public class FiltroRelatorioNotasDivergentesPgdas {

    private String inscricaoInicial;
    private String inscricaoFinal;
    private String cnpjInicial;
    private String cnpjFinal;
    private Servico servico;
    private Date dataInicial;
    private Date dataFinal;

    public String getInscricaoInicial() {
        return inscricaoInicial;
    }

    public void setInscricaoInicial(String inscricaoInicial) {
        this.inscricaoInicial = inscricaoInicial;
    }

    public String getInscricaoFinal() {
        return inscricaoFinal;
    }

    public void setInscricaoFinal(String inscricaoFinal) {
        this.inscricaoFinal = inscricaoFinal;
    }

    public String getCnpjInicial() {
        return cnpjInicial;
    }

    public void setCnpjInicial(String cnpjInicial) {
        this.cnpjInicial = cnpjInicial;
    }

    public String getCnpjFinal() {
        return cnpjFinal;
    }

    public void setCnpjFinal(String cnpjFinal) {
        this.cnpjFinal = cnpjFinal;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
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

    public String montarDescricaoFiltros() {
        StringBuilder retorno = new StringBuilder();
        if (dataInicial != null) {
            retorno.append(" Data Inicial: ").append(DataUtil.getDataFormatada(dataInicial)).append("; ");
        }
        if (dataFinal != null) {
            retorno.append(" Data Final: ").append(DataUtil.getDataFormatada(dataFinal)).append("; ");
        }
        if (inscricaoInicial != null) {
            retorno.append(" Inscrição Municipal Inicial: ").append(inscricaoInicial).append("; ");
        }
        if (inscricaoFinal != null) {
            retorno.append(" Inscrição Municipal Final: ").append(inscricaoFinal).append("; ");
        }
        if (Strings.isNotEmpty(cnpjInicial)) {
            retorno.append(" CNPJ Inicial: ").append(cnpjInicial).append("; ");
        }
        if (Strings.isNotEmpty(cnpjFinal)) {
            retorno.append(" CNPJ Final: ").append(cnpjFinal).append("; ");
        }
        if (servico != null) {
            retorno.append(" Serviço: ").append(servico).append("; ");
        }
        return retorno.toString();
    }

    public void montarParametros(RelatorioDTO dto) {
        dto.adicionarParametro("MUNICIPIO", "Municipio de Rio Branco");
        dto.adicionarParametro("TITULO", "Secretaria Municipal de Finanças");
        dto.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
        dto.adicionarParametro("FILTROS", montarDescricaoFiltros());

        List<ParametrosRelatoriosDTO> parametros = Lists.newArrayList();
        if (getDataInicial() != null) {
            parametros.add(new ParametrosRelatoriosDTO(" dec.competencia ", ":datainicial",
                null, OperacaoRelatorioDTO.MAIOR_IGUAL, DataUtil.getDataFormatada(getDataInicial()), null, 1, true));
        }
        if (getDataInicial() != null) {
            parametros.add(new ParametrosRelatoriosDTO(" dec.competencia ", ":datafinal",
                null, OperacaoRelatorioDTO.MENOR_IGUAL, DataUtil.getDataFormatada(getDataFinal()), null, 1, true));
        }
        if (Strings.isNotEmpty(getInscricaoInicial())) {
            parametros.add(new ParametrosRelatoriosDTO(" cad.inscricaocadastral ", ":inscricaoinicial",
                null, OperacaoRelatorioDTO.MAIOR_IGUAL, getInscricaoInicial(), null, 1, false));
        }
        if (!Strings.isNotEmpty(getInscricaoFinal())) {
            parametros.add(new ParametrosRelatoriosDTO(" cad.inscricaocadastral ", ":inscricaofinal",
                null, OperacaoRelatorioDTO.MENOR_IGUAL, getInscricaoInicial(), null, 1, false));
        }
        if (getServico() != null) {
            parametros.add(new ParametrosRelatoriosDTO(" serv.id ", ":idservico",
                null, OperacaoRelatorioDTO.IGUAL, getServico().getId(), null, 1, false));
        }
        dto.adicionarParametro("parametros", parametros);
    }

    public void validar() {
        ValidacaoException ve = new ValidacaoException();
        if (getDataInicial() == null || getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Período deve ser informado.");
        }
        ve.lancarException();
    }
}
