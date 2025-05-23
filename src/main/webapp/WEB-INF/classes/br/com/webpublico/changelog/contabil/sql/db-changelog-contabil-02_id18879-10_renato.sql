update ArquivoRemessaBanco ar set contratoobn_id = (select co.id from contratoobn co where co.numeroContrato = ar.numeroContrato)
