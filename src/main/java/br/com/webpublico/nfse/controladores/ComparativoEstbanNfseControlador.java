package br.com.webpublico.nfse.controladores;


import br.com.webpublico.DateUtils;
import br.com.webpublico.StringUtils;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.negocios.CadastroEconomicoFacade;
import br.com.webpublico.negocios.ConfiguracaoTributarioFacade;
import br.com.webpublico.nfse.domain.dtos.ComparativoEstbanNfseDTO;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import br.com.webpublico.nfse.facades.DeclaracaoMensalServicoFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(
        id = "comparativo-estban-nfse",
        pattern = "/nfse/comparativo-estban-nfse/",
        viewId = "/faces/tributario/nfse/comparativo-estban-nfse/edita.xhtml")
})
public class ComparativoEstbanNfseControlador {

    private static Logger log = LoggerFactory.getLogger(ComparativoEstbanNfseControlador.class);

    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private DeclaracaoMensalServicoFacade declaracaoMensalServicoFacade;
    private List<ComparativoEstbanNfseDTO> registros;
    private Map<Integer, Exercicio> mapExercicios;
    private Map<String, CadastroEconomico> mapCadastros;


    public List<ComparativoEstbanNfseDTO> getRegistros() {
        return registros;
    }

    public void setRegistros(List<ComparativoEstbanNfseDTO> registros) {
        this.registros = registros;
    }

    @URLAction(mappingId = "comparativo-estban-nfse", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        this.mapExercicios = Maps.newHashMap();
        this.mapCadastros = Maps.newHashMap();
    }

    public void lerArquivoEstban(FileUploadEvent event) {
        try {
            this.registros = new ArrayList<>();
            ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();

            BufferedReader csvReader = new BufferedReader(new InputStreamReader(event.getFile().getInputstream()));
            Integer row = 0;
            String rowValue = null;
            Map<String, Integer> mapIndexColunas = Maps.newHashMap();
            while ((rowValue = csvReader.readLine()) != null) {
                row = lerLinhaArquivo(configuracaoTributario, row, rowValue, mapIndexColunas);
                if (row == null) continue;
            }
        } catch (Exception e) {
            log.error("Erro ao ler arquivo do ESTBAN. Erro {}", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao ler o arquivo do ESTBAN. Erro: " + e.getMessage());
        }
    }

    private Integer lerLinhaArquivo(ConfiguracaoTributario configuracaoTributario,
                                    Integer row,
                                    String rowValue,
                                    Map<String, Integer> mapIndexColunas) {
        row++;
        //discartando cabeçalho
        if (row < 3) {
            return row;
        } else if (row == 3) {
            String[] data = rowValue.split(";");
            for (int i = 0; i < data.length; i++) {
                mapIndexColunas.put(data[i].trim(), i);
            }
        } else {
            String[] data = rowValue.split(";");

            String uf = getValueColumn(data, mapIndexColunas.get("UF"));
            String nomeCidade = getValueColumn(data, mapIndexColunas.get("MUNICIPIO"));


            if (uf.equalsIgnoreCase(configuracaoTributario.getCidade().getUf().getUf()) &&
                nomeCidade.equals(configuracaoTributario.getCidade().getNome())) {
                Date competencia = getValueColumnDate(data, mapIndexColunas.get("#DATA_BASE"));
                String cnpjAgencia = StringUtils.getApenasNumeros(getValueColumn(data, mapIndexColunas.get("AGENCIA")));
                BigDecimal valorEstban = getValueColumnDecimal(data, mapIndexColunas.get("VERBETE_711_CONTAS_CREDORAS"));

                ComparativoEstbanNfseDTO dto = new ComparativoEstbanNfseDTO();
                dto.setCompetencia(competencia);
                dto.setCadastroEconomico(cadastroEconomicoFacade.buscarCadastroEconomicoPorCnpj(cnpjAgencia));
                dto.setValorEstban(valorEstban);
                dto.setValorNfse(BigDecimal.ZERO);
                if (dto.getCadastroEconomico() != null) {
                    dto.setValorNfse(declaracaoMensalServicoFacade.buscarValorDeclarado(dto.getCadastroEconomico(),
                        TipoMovimentoMensal.INSTITUICAO_FINANCEIRA, DateUtils.getAno(dto.getCompetencia()),
                        DateUtils.getMes(dto.getCompetencia())));
                }
                dto.setDiferenca(dto.getValorEstban().subtract(dto.getValorNfse()));
                registros.add(dto);
            }
        }
        return row;
    }

    private String getValueColumn(String data[], Integer index) {
        if (data.length < index) {
            return "";
        } else {
            return data[index];
        }
    }

    private Date getValueColumnDate(String data[], Integer index) {
        String value = getValueColumn(data, index);
        if (!value.isEmpty()) {
            return DateUtils.getData(value, "yyyyMM");
        }
        return null;
    }

    private BigDecimal getValueColumnDecimal(String data[], Integer index) {
        String value = getValueColumn(data, index);
        if (value.isEmpty() || value.equals("0")) {
            return BigDecimal.ZERO;
        } else {
            value = value.substring(0, value.length() - 2) + "." +
                value.substring(value.length() - 2);
        }
        return new BigDecimal(value);
    }

    public boolean hasDiferenca(ComparativoEstbanNfseDTO dto) {
        return dto.getDiferenca().compareTo(BigDecimal.ZERO) != 0;
    }
}
