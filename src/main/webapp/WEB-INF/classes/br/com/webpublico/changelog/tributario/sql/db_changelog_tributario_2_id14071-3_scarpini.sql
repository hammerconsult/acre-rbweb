UPDATE CALCULORBTRANS TOUPDATE SET TOUPDATE.NUMEROLANCAMENTO =
(select TO_NUMBER(trim(REGEXP_REPLACE(substr(calculo.referencia, instr(calculo.referencia, 'Processo:', 1,1)+9 ,5), '[^0-9]')))
from calculorbtrans rb
inner join calculo on calculo.id =rb.id
WHERE rb.id = TOUPDATE.ID
)
