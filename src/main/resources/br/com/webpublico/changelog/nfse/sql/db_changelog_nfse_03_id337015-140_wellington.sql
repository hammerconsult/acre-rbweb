update itemdeclaracaoservico
set conta_id = null
where conta_id is not null;
update pgcc
set tarifabancaria_id = null,
    produtoservico_id = null;
delete
from pgcctarifabancaria;
delete
from pgccprodutoservico;
delete
from arquivodesifregistro0100;
delete
from arquivodesifregistro0200;
delete
from arquivodesifregistro0300;
delete
from arquivodesifregistro0400;
delete
from arquivodesifregistro0410;
delete
from arquivodesifregistro0430;
delete
from arquivodesifregistro0440;
delete
from arquivodesifregistro1000;
update declaracaomensalservico
set arquivodesif_id = null
where arquivodesif_id is not null;
delete
from arquivodesif;
delete
from pgcc;
