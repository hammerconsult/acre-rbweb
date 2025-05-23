update execucaocontrato set datalancamento = (select c.datalancamento from contrato c where c.id = execucaocontrato.contrato_id)
where datalancamento is null;

update execucaocontrato set unidadeorcamentaria_id = (
          select sol.unidadeorganizacional_id from execucaocontrato ex
          inner join execucaocontratoempenho exemp on exemp.execucaocontrato_id = ex.id
          inner join solicitacaoempenho sol on sol.id = exemp.solicitacaoempenho_id
          where ex.id = execucaocontrato.id
          and rownum = 1);
