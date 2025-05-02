package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoProcessoDebito;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

@Stateless
public class ProcessoDebitoLoteFacade {

    private static final Logger logger = LoggerFactory.getLogger(ProcessoDebitoLoteFacade.class);

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private ProcessoDebitoFacade processoDebitoFacade;

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    @Asynchronous
    @TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
    public Future lancar(AssistenteBarraProgresso assistenteBarraProgresso,
                         Exercicio exercicio,
                         UsuarioSistema usuarioSistema,
                         TipoProcessoDebito tipoProcessoDebito,
                         String protocolo,
                         AtoLegal atoLegal,
                         String motivo,
                         TipoCadastroTributario tipoCadastro,
                         InputStream inputStream,
                         Divida divida) {
        assistenteBarraProgresso.setDescricaoProcesso("Verificando cadastros para registro de processo de débito.");
        List<String> inscricoes = lerCadastros(inputStream);
        assistenteBarraProgresso.setTotal(inscricoes.size());
        for (String inscricao : inscricoes) {
            CadastroImobiliario cadastroImobiliario = cadastroImobiliarioFacade.recuperaPorInscricao(inscricao);
            processoDebitoFacade.lancarProcessoDebito(exercicio, usuarioSistema, tipoProcessoDebito,
                protocolo, atoLegal, motivo, tipoCadastro, cadastroImobiliario, divida);
            assistenteBarraProgresso.conta();
        }
        return new AsyncResult<>(null);
    }

    private List<String> lerCadastros(InputStream inputStream) {
        Set<String> cadastros = Sets.newHashSet();
        try {
            XSSFWorkbook wb = new XSSFWorkbook(inputStream);
            int index = 0;
            while (index < wb.getNumberOfSheets()) {
                XSSFSheet sheet = wb.getSheetAt(index);
                Iterator<Row> itr = sheet.iterator();
                while (itr.hasNext()) {
                    Row row = itr.next();
                    if (row.getCell(0) == null)
                        break;
                    cadastros.add(String.format("%.0f", row.getCell(0).getNumericCellValue()));
                }
                index++;
            }
        } catch (IOException e) {
            logger.debug("Erro ao ler cadastros do arquivo .xlsx para geração de processo de débito.", e);
            logger.error("Erro ao ler cadastros do arquivo .xlsx para geração de processo de débito.");
        }
        return Lists.newArrayList(cadastros);
    }
}
