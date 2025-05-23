update execucaocontrato ex set ex.datalancamento = (select c.datalancamento from contrato c where ex.contrato_id = c.contratado_id)
