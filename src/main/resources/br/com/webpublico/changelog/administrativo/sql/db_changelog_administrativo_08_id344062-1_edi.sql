insert into fornecedorcontrato (ID, CONTRATO_ID, FORNECEDOR_ID, RESPONSAVELFORNECEDOR_ID, ORDEM)
SELECT HIBERNATE_SEQUENCE.nextval,
       c.id,
       c.CONTRATADO_ID,
       c.RESPONSAVELEMPRESA_ID,
       0
from contrato c;
