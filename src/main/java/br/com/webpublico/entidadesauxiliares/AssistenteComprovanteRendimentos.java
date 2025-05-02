package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.dirf.DirfInfoComplementares;
import br.com.webpublico.entidadesauxiliares.rh.ComprovanteRendimentosIsentos;
import br.com.webpublico.entidadesauxiliares.rh.ComprovanteRendimentosPagosConferencia;
import br.com.webpublico.entidadesauxiliares.rh.ComprovanteRendimentosTributaveis;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.webreportdto.dto.rh.*;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class AssistenteComprovanteRendimentos implements Serializable {

    private Exercicio anoCalendario;
    private List<VinculoFP> vinculos;

    public AssistenteComprovanteRendimentos() {
        vinculos = Lists.newLinkedList();
    }

    public Exercicio getAnoCalendario() {
        return anoCalendario;
    }

    public void setAnoCalendario(Exercicio anoCalendario) {
        this.anoCalendario = anoCalendario;
    }

    public List<VinculoFP> getVinculos() {
        return vinculos;
    }

    public void setVinculos(List<VinculoFP> vinculos) {
        this.vinculos = vinculos;
    }

    public static List<DirfInfoComplementaresDTO> complementaresListDTO(List<DirfInfoComplementares> complementares) {
        List<DirfInfoComplementaresDTO> toReturn = Lists.newArrayList();
        for (DirfInfoComplementares dirf : complementares) {
            toReturn.add(complementarestoDTO(dirf));
        }
        return toReturn;
    }

    private static DirfInfoComplementaresDTO complementarestoDTO(DirfInfoComplementares dirf) {
        DirfInfoComplementaresDTO dirfDTO = new DirfInfoComplementaresDTO();
        dirfDTO.setValor(dirf.getValor());
        dirfDTO.setCpfBeneficiarioPensao(dirf.getCpfBeneficiarioPensao());
        dirfDTO.setNascimentoBeneficiarioPensao(DataUtil.getDataFormatada(dirf.getNascimentoBeneficiarioPensao()));
        dirfDTO.setNomeBeneficiarioPensao(dirf.getNomeBeneficiarioPensao());
        return dirfDTO;
    }

    public static List<ComprovanteRendimentosIRRFFonteDTO> comprovanteRendimentosListDTO(List<ComprovanteRendimentosIRRFFonte> rendimentosIRRFFontes) {
        List<ComprovanteRendimentosIRRFFonteDTO> toReturn = Lists.newArrayList();
        for (ComprovanteRendimentosIRRFFonte rendimento : rendimentosIRRFFontes) {
            toReturn.add(comprovanteRendimentosToDTO(rendimento));
        }
        return toReturn;
    }

    private static ComprovanteRendimentosIRRFFonteDTO comprovanteRendimentosToDTO(ComprovanteRendimentosIRRFFonte comprovante) {
        ComprovanteRendimentosIRRFFonteDTO comprovanteDTO = new ComprovanteRendimentosIRRFFonteDTO();
        comprovanteDTO.setAnoCalendario(comprovante.getAnoCalendario());
        comprovanteDTO.setAnoExercicio(comprovante.getAnoExercicio());
        comprovanteDTO.setCnpjFontePagadora(comprovante.getCnpjFontePagadora());
        comprovanteDTO.setContribuicaoPreviFundoAposentadoria(comprovante.getContribuicaoPreviFundoAposentadoria());
        comprovanteDTO.setContribuicaoPrevidenciaria(comprovante.getContribuicaoPrevidenciaria());
        comprovanteDTO.setCpfBeneficiaria(comprovante.getCpfBeneficiaria());
        comprovanteDTO.setData(comprovante.getData());
        comprovanteDTO.setDecimoTerceiro(comprovante.getDecimoTerceiro());
        comprovanteDTO.setDiariasEAjudaCusto(comprovante.getDiariasEAjudaCusto());
        comprovanteDTO.setFontePagadora(comprovante.getFontePagadora());
        comprovanteDTO.setImpostoRetido(comprovante.getImpostoRetido());
        comprovanteDTO.setIndenizacaoPorRescisaoContrato(comprovante.getIndenizacaoPorRescisaoContrato());
        comprovanteDTO.setIrRetidoNaFonte13Salario(comprovante.getIrRetidoNaFonte13Salario());
        comprovanteDTO.setLucroEDividendo(comprovante.getLucroEDividendo());
        comprovanteDTO.setNaturezaRendimento(comprovante.getNaturezaRendimento());
        comprovanteDTO.setNome(comprovante.getNome());
        comprovanteDTO.setNomeResponsavel(comprovante.getNomeResponsavel());
        comprovanteDTO.setOutrosIsentos(comprovante.getOutrosIsentos());
        comprovanteDTO.setOutrosSujeitosTributacao(comprovante.getOutrosSujeitosTributacao());
        comprovanteDTO.setParcelIsentaProvetosAposentadoria(comprovante.getParcelIsentaProvetosAposentadoria());
        comprovanteDTO.setPensaoAlimenticia(comprovante.getPensaoAlimenticia());
        comprovanteDTO.setPensoesProventosApoMolestiaGrave(comprovante.getPensoesProventosApoMolestiaGrave());
        comprovanteDTO.setTotalRendimentos(comprovante.getTotalRendimentos());
        comprovanteDTO.setValoresPagosAoTitular(comprovante.getValoresPagosAoTitular());
        comprovanteDTO.getInformacoesComplementares().addAll(complementaresListDTO(comprovante.getInformacoesComplementares()));
        comprovanteDTO.setQuantidadeDeMeses(comprovante.getQuantidadeDeMeses());
        comprovanteDTO.setTotalRendimentosTributaveis(comprovante.getTotalRendimentosTributaveis());
        comprovanteDTO.setExclusaoDespesasAcaoJudicial(comprovante.getExclusaoDespesasAcaoJudicial());
        comprovanteDTO.setDeducaoContribuicaoPrev(comprovante.getDeducaoContribuicaoPrev());
        comprovanteDTO.setDeducaoPensaoAlimenticia(comprovante.getDeducaoPensaoAlimenticia());
        comprovanteDTO.setImpostoSobreARendaRetido(comprovante.getImpostoSobreARendaRetido());
        comprovanteDTO.setRendimentosIsentosDePensao(comprovante.getRendimentosIsentosDePensao());
        comprovanteDTO.setPensao(comprovante.isPensao());
        return comprovanteDTO;
    }

    public static List<ComprovanteRendimentosPagosConferenciaDTO> comprovanteRendimentosPagosListDTO(List<ComprovanteRendimentosPagosConferencia> rendimentosPagos) {
        List<ComprovanteRendimentosPagosConferenciaDTO> toReturn = Lists.newArrayList();
        for (ComprovanteRendimentosPagosConferencia pagos : rendimentosPagos) {
            toReturn.add(comprovanteRendimentosPagosToDTO(pagos));
        }
        return toReturn;
    }

    private static ComprovanteRendimentosPagosConferenciaDTO comprovanteRendimentosPagosToDTO(ComprovanteRendimentosPagosConferencia rendimentosPagos) {
        ComprovanteRendimentosPagosConferenciaDTO rendimentosPagosDTO = new ComprovanteRendimentosPagosConferenciaDTO();
        rendimentosPagosDTO.setFontePagadora(rendimentosPagos.getFontePagadora());
        rendimentosPagosDTO.setFonteCpf(rendimentosPagos.getFonteCpf());
        rendimentosPagosDTO.setBeneficiarioCpf(rendimentosPagos.getBeneficiarioCpf());
        rendimentosPagosDTO.setBeneficiarioNome(rendimentosPagos.getBeneficiarioNome());
        rendimentosPagosDTO.setAnoExercicio(rendimentosPagos.getAnoExercicio());
        rendimentosPagosDTO.setAnoCalendario(rendimentosPagos.getAnoCalendario());
        rendimentosPagosDTO.setResponsavelInformacao(rendimentosPagos.getResponsavelInformacao());
        rendimentosPagosDTO.setNaturezaRendimento(rendimentosPagos.getNaturezaRendimento());
        rendimentosPagosDTO.getRendimentosTributaveis().addAll(comprovanteRendimentosTributaveisListDTO(rendimentosPagos.getRendimentosTributaveis()));
        rendimentosPagosDTO.getRendimentosIsentos().addAll(comprovanteRendimentosIsentosListDTO(rendimentosPagos.getRendimentosIsentos()));
        return rendimentosPagosDTO;
    }

    public static List<ComprovanteRendimentosTributaveisDTO> comprovanteRendimentosTributaveisListDTO(List<ComprovanteRendimentosTributaveis> rendimentosPagos) {
        List<ComprovanteRendimentosTributaveisDTO> toReturn = Lists.newArrayList();
        for (ComprovanteRendimentosTributaveis rendimentosTributaveis : rendimentosPagos) {
            toReturn.add(comprovanteRendimentosTributaveisToDTO(rendimentosTributaveis));
        }
        return toReturn;
    }

    private static ComprovanteRendimentosTributaveisDTO comprovanteRendimentosTributaveisToDTO(ComprovanteRendimentosTributaveis rendimentosTributaveis) {
        ComprovanteRendimentosTributaveisDTO rendimentosTributaveisDTO = new ComprovanteRendimentosTributaveisDTO();
        rendimentosTributaveisDTO.setMes(rendimentosTributaveis.getMes());
        rendimentosTributaveisDTO.setValor(rendimentosTributaveis.getValor());
        return rendimentosTributaveisDTO;
    }

    public static List<ComprovanteRendimentosIsentosDTO> comprovanteRendimentosIsentosListDTO(List<ComprovanteRendimentosIsentos> rendimentosPagos) {
        List<ComprovanteRendimentosIsentosDTO> toReturn = Lists.newArrayList();
        for (ComprovanteRendimentosIsentos rendimentosTributaveis : rendimentosPagos) {
            toReturn.add(rendimentosIsentosToDTO(rendimentosTributaveis));
        }
        return toReturn;
    }

    private static ComprovanteRendimentosIsentosDTO rendimentosIsentosToDTO(ComprovanteRendimentosIsentos rendimentosTributaveis) {
        ComprovanteRendimentosIsentosDTO rendimentosIsentosDTO = new ComprovanteRendimentosIsentosDTO();
        rendimentosIsentosDTO.setMes(rendimentosTributaveis.getMes());
        rendimentosIsentosDTO.setValor(rendimentosTributaveis.getValor());
        return rendimentosIsentosDTO;
    }

}
